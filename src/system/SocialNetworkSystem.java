package system;

import models.*;
import java.util.*;

/**
 * SocialNetworkSystem 
 */
public class SocialNetworkSystem {
    private static SocialNetworkSystem instance;
    
    private ArrayList<User> users;
    private ArrayList<Post> posts;
    private User currentUser;
    private int userIdCounter;
    private int postIdCounter;
    private int commentIdCounter;
    private int reportIdCounter;
    private ArrayList<VerificationRequest> verificationRequests; 
    private ArrayList<CommentReport> commentReports; 

    private SocialNetworkSystem() {
        users = new ArrayList<>();
        posts = new ArrayList<>();
        currentUser = null;
        userIdCounter = 1;
        postIdCounter = 1;
        commentIdCounter = 1;
        reportIdCounter = 1;
        verificationRequests = new ArrayList<>();
        commentReports = new ArrayList<>();
        
        //  load saved data
        if (!loadSavedData()) {
           
            initializeSampleData();
        }
    }

    public static SocialNetworkSystem getInstance() {
        if (instance == null) {
            instance = new SocialNetworkSystem();
        }
        return instance;
    }

    // ==================== USER MANAGEMENT ====================

    public User registerUser(String username, String password, String fullName, 
                            String email, int age, boolean isAdmin) {
        // Check if username already exists
        if (getUserByUsername(username) != null) {
            return null;
        }
        
        // Check if email already exists (case-insensitive)
        if (email != null && getUserByEmail(email.trim()) != null) {
            return null;
        }

        String id = "user_" + (userIdCounter++);
        User newUser;
        
        if (isAdmin) {
            newUser = new Admin(id, username, password, fullName, email, age);
        } else {
            newUser = new RegularUser(id, username, password, fullName, email, age);
        }
        
        users.add(newUser);
        saveData(); // Auto-save after registration
        return newUser;
    }
    
    /**
     * Check if email is already registered (for validation before registration)
     */
    public boolean isEmailTaken(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return getUserByEmail(email.trim()) != null;
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = getUserByUsername(username);
        if (user != null && user.validatePassword(password)) {
            // Check if user is banned
            if (isUserBanned(user.getId())) {
                return null; // User is banned, cannot login
            }
            currentUser = user;
            return user;
        }
        return null;
    }
    
    /**
     * Check if login failed due to ban (call this after login returns null)
     */
    public boolean isLoginFailedDueToBan(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        try {
            User user = getUserByUsername(username);
            return user != null && isUserBanned(user.getId());
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isUserBanned(String userId) {
        if (userId == null || users == null) {
            return false;
        }
        // Check if any admin has banned this user
        for (User admin : users) {
            if (admin instanceof Admin) {
                Admin adminUser = (Admin) admin;
                if (adminUser != null && adminUser.isUserBanned(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        if (username == null || users == null) {
            return null;
        }
        for (User user : users) {
            if (user != null && user.getUsername() != null && user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
    
    public User getUserByEmail(String email) {
        if (email == null || users == null) {
            return null;
        }
        String trimmedEmail = email.trim();
        for (User user : users) {
            if (user != null && user.getEmail() != null) {
                String userEmail = user.getEmail().trim();
                if (userEmail.equalsIgnoreCase(trimmedEmail)) {
                    return user;
                }
            }
        }
        return null;
    }
    
    public int getUserCount() {
        return users != null ? users.size() : 0;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public List<User> searchUsers(String query) {
        String lowerQuery = query.toLowerCase();
        ArrayList<User> results = new ArrayList<>();
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(lowerQuery) ||
                user.getFullName().toLowerCase().contains(lowerQuery)) {
                results.add(user);
            }
        }
        return results;
    }

    // ==================== POST MANAGEMENT ====================

    public TextPost createTextPost(String content) {
        if (currentUser == null || content == null || content.trim().isEmpty()) {
            return null;
        }

        String id = "post_" + (postIdCounter++);
        TextPost post = new TextPost(id, currentUser.getId(), currentUser.getUsername(),
                                     currentUser.getFullName(), content);
        posts.add(post);
        currentUser.addPost(id);
        saveData(); // Auto-save after creating post
        return post;
    }

    public ImagePost createImagePost(String imagePath, String caption) {
        if (currentUser == null || imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        String id = "post_" + (postIdCounter++);
        ImagePost post = new ImagePost(id, currentUser.getId(), currentUser.getUsername(),
                                       currentUser.getFullName(), imagePath, caption);
        posts.add(post);
        currentUser.addPost(id);
        saveData(); // Auto-save after creating post
        return post;
    }

    public Post getPostById(String id) {
        for (Post post : posts) {
            if (post.getId().equals(id)) {
                return post;
            }
        }
        return null;
    }

    public List<Post> getAllPosts() {
        ArrayList<Post> allPosts = new ArrayList<>(posts);
        // Sort by timestamp, newest first
        allPosts.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
        return allPosts;
    }

    public List<Post> getPostsByUser(String userId) {
        ArrayList<Post> userPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getAuthorId().equals(userId)) {
                userPosts.add(post);
            }
        }
        userPosts.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
        return userPosts;
    }

    public List<Post> getFeedPosts() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        // Admins see all posts, regular users see only posts from users they follow 
        ArrayList<Post> feedPosts = new ArrayList<>();
        
        if (currentUser instanceof Admin) {
          
            feedPosts.addAll(posts);
        } else {
            // Also respect private account settings
            for (Post post : posts) {
                // Always show own posts
                if (post.getAuthorId() != null && post.getAuthorId().equals(currentUser.getId())) {
                    feedPosts.add(post);
                    continue;
                }
                
                User author = getUserById(post.getAuthorId());
                
                // Check if author exists (null check)
                if (author == null) {
                    continue; // Skip posts with invalid authors
                }
                
                // Check if author is a regular user with private account
                if (author instanceof RegularUser) {
                    RegularUser authorRegular = (RegularUser) author;
                    
                    if (authorRegular.isPrivateAccount()) {
                        // Private account - only show if following
                        if (currentUser.isFollowing(post.getAuthorId())) {
                            feedPosts.add(post);
                        }
                    } else {
                        // Public account - show if following
                        if (currentUser.isFollowing(post.getAuthorId())) {
                            feedPosts.add(post);
                        }
                    }
                } else {
                    // Admin posts - show if following
                    if (currentUser.isFollowing(post.getAuthorId())) {
                        feedPosts.add(post);
                    }
                }
            }
        }
        
        // Sort by timestamp, newest first
        feedPosts.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
        return feedPosts;
    }

    public boolean deletePost(String postId) {
        Post post = getPostById(postId);
        if (post == null) return false;

        // Check permissions
        if (currentUser == null) return false;
        
        boolean canDelete = post.getAuthorId().equals(currentUser.getId()) ||
                           currentUser.canDeleteAnyPost();
        
        if (canDelete) {
            User author = getUserById(post.getAuthorId());
            if (author != null) {
                author.removePost(postId);
            }
            posts.remove(post);
            saveData(); // Auto-save after deleting post
            return true;
        }
        return false;
    }

    // ==================== SOCIAL INTERACTIONS ====================

    public boolean likePost(String postId) {
        if (currentUser == null) return false;
        
        Post post = getPostById(postId);
        if (post != null) {
            if (post.isLikedBy(currentUser.getId())) {
                post.removeLike(currentUser.getId());
            } else {
                post.addLike(currentUser.getId());
            }
            saveData(); // Auto-save after like/unlike
            return true;
        }
        return false;
    }

    public boolean isPostLikedByCurrentUser(String postId) {
        if (currentUser == null) return false;
        Post post = getPostById(postId);
        return post != null && post.isLikedBy(currentUser.getId());
    }

    public Comment addComment(String postId, String content) {
        if (currentUser == null || content == null || content.trim().isEmpty()) {
            return null;
        }

        Post post = getPostById(postId);
        if (post == null) return null;

        String commentId = "comment_" + (commentIdCounter++);
        Comment comment = new Comment(commentId, postId, currentUser.getId(),
                                     currentUser.getUsername(), currentUser.getFullName(), content);
        post.addComment(comment);
        saveData(); // Auto-save after adding comment
        return comment;
    }

    public boolean followUser(String userId) {
        if (currentUser == null || currentUser.getId().equals(userId)) {
            return false;
        }
        
        // Prevent admins from following anyone
        if (currentUser instanceof Admin) {
            return false;
        }

        User targetUser = getUserById(userId);
        if (targetUser == null || !(targetUser instanceof RegularUser)) {
            return false;
        }
        
        RegularUser targetRegular = (RegularUser) targetUser;
        
        // If already following, unfollow
        if (currentUser.isFollowing(userId)) {
            currentUser.unfollow(userId);
            targetUser.removeFollower(currentUser.getId());
            // Also remove any pending requests when unfollowing
            targetRegular.removeFollowRequest(currentUser.getId());
            saveData();
            return true;
        }
        
        // Check if there's a pending request
        boolean hasPendingRequest = targetRegular.hasPendingRequestFrom(currentUser.getId());
        
        // If target account is private
        if (targetRegular.isPrivateAccount()) {
            if (hasPendingRequest) {
                // Cancel the request
                targetRegular.removeFollowRequest(currentUser.getId());
            } else {
                // Send follow request
                targetRegular.addFollowRequest(currentUser.getId());
            }
            saveData();
            return true;
        } else {
            // Public account - follow directly
            // First, remove any stale pending requests
            if (hasPendingRequest) {
                targetRegular.removeFollowRequest(currentUser.getId());
            }
            currentUser.follow(userId);
            targetUser.addFollower(currentUser.getId());
            saveData();
            return true;
        }
    }

    public boolean isFollowingUser(String userId) {
        return currentUser != null && currentUser.isFollowing(userId);
    }
    
    /**
     * Approve a follow request (for private accounts)
     */
    public boolean approveFollowRequest(String requesterId) {
        if (currentUser == null || !(currentUser instanceof RegularUser)) {
            return false;
        }
        
        RegularUser currentRegular = (RegularUser) currentUser;
        User requester = getUserById(requesterId);
        
        if (requester == null) {
            return false;
        }
        
        // Check if there's actually a pending request
        if (!currentRegular.hasPendingRequestFrom(requesterId)) {
            return false;
        }
        
        // Remove the request
        currentRegular.removeFollowRequest(requesterId);
        
        // Add to followers/following
        requester.follow(currentUser.getId());
        currentUser.addFollower(requesterId);
        
        saveData();
        return true;
    }
    
    /**
     * Reject a follow request
     */
    public boolean rejectFollowRequest(String requesterId) {
        if (currentUser == null || !(currentUser instanceof RegularUser)) {
            return false;
        }
        
        RegularUser currentRegular = (RegularUser) currentUser;
        
        if (currentRegular.hasPendingRequestFrom(requesterId)) {
            currentRegular.removeFollowRequest(requesterId);
            saveData();
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if current user has sent a follow request to a user
     */
    public boolean hasSentFollowRequest(String userId) {
        if (currentUser == null) {
            return false;
        }
        
        User targetUser = getUserById(userId);
        if (targetUser instanceof RegularUser) {
            RegularUser targetRegular = (RegularUser) targetUser;
            return targetRegular.hasPendingRequestFrom(currentUser.getId());
        }
        
        return false;
    }
    
    /**
     * Get pending follow requests for current user
     */
    public List<String> getPendingFollowRequests() {
        if (currentUser instanceof RegularUser) {
            RegularUser currentRegular = (RegularUser) currentUser;
            return currentRegular.getPendingFollowRequests();
        }
        return new ArrayList<>();
    }
    
    /**
     * Get count of pending follow requests
     */
    public int getPendingFollowRequestCount() {
        if (currentUser instanceof RegularUser) {
            RegularUser currentRegular = (RegularUser) currentUser;
            return currentRegular.getPendingRequestCount();
        }
        return 0;
    }

    // ==================== VERIFICATION MANAGEMENT ====================

    
    /**
     * Request verification 
     */
    public boolean requestVerification(String contentFilePath) {
        if (currentUser == null || !(currentUser instanceof RegularUser)) {
            return false; // Only regular users can request verification
        }
        
        // Check if already has pending request
        for (VerificationRequest req : verificationRequests) {
            if (req.getUserId().equals(currentUser.getId()) && !req.isResolved()) {
                return false; // Already has pending request
            }
        }
        
        VerificationRequest request = new VerificationRequest(
            currentUser.getId(),
            currentUser.getUsername(),
            currentUser.getFullName(),
            contentFilePath
        );
        
        verificationRequests.add(request);
        saveData();
        return true;
    }
    
    /**
     * Check if user has pending verification request
     */
    public boolean hasPendingVerificationRequest(String userId) {
        for (VerificationRequest req : verificationRequests) {
            if (req.getUserId().equals(userId) && !req.isResolved()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all pending verification requests (admin only)
     */
    public List<VerificationRequest> getPendingVerificationRequests() {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return new ArrayList<>();
        }
        
        List<VerificationRequest> pending = new ArrayList<>();
        for (VerificationRequest req : verificationRequests) {
            if (!req.isResolved()) {
                pending.add(req);
            }
        }
        
        // Sort by timestamp, newest first
        pending.sort((r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
        
        return pending;
    }
    
    /**
     * Verify a user (admin only)
     */
    public boolean verifyUser(String userId) {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return false;
        }
        User user = getUserById(userId);
        if (user instanceof RegularUser) {
            RegularUser regularUser = (RegularUser) user;
            regularUser.setVerified(true);
            
            // Mark all requests for this user as resolved
            for (VerificationRequest req : verificationRequests) {
                if (req.getUserId().equals(userId)) {
                    req.setResolved(true);
                }
            }
            
            saveData();
            return true;
        }
        return false;
    }
    
    /**
     * Reject a verification request (admin only)
     */
    public boolean rejectVerificationRequest(String userId) {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return false;
        }
        
        // Mark pending requests as resolved
        boolean found = false;
        for (VerificationRequest req : verificationRequests) {
            if (req.getUserId().equals(userId) && !req.isResolved()) {
                req.setResolved(true);
                found = true;
            }
        }
        
        if (found) {
            saveData();
            return true;
        }
        return false;
    }
    
    /**
     * Unverify a user (admin only)
     */
    public boolean unverifyUser(String userId) {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return false;
        }
        User user = getUserById(userId);
        if (user instanceof RegularUser) {
            RegularUser regularUser = (RegularUser) user;
            regularUser.setVerified(false);
            saveData();
            return true;
        }
        return false;
    }
    
    // ==================== BAN MANAGEMENT ====================
    
    /**
     * Ban a user (admin only)
     */
    public boolean banUser(String userId) {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return false; // Only admins can ban
        }
        User targetUser = getUserById(userId);
        if (targetUser == null || targetUser instanceof Admin) {
            return false; // Cannot ban admins or non-existent users
        }
        
        Admin admin = (Admin) currentUser;
        admin.banUser(userId);
        saveData();
        return true;
    }
    
    /**
     * Unban a user (admin only)
     */
    public boolean unbanUser(String userId) {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return false; // Only admins can unban
        }
        
        Admin admin = (Admin) currentUser;
        admin.unbanUser(userId);
        saveData();
        return true;
    }
    
    /**
     * Check if a user is banned
     */
    public boolean isUserBannedByAdmin(String userId) {
        return isUserBanned(userId);
    }

    // ==================== SAMPLE DATA ====================

    private void initializeSampleData() {
        // Create sample users
        RegularUser ali = (RegularUser) registerUser("ali", "password123", 
            "Ali", "ali@email.com", 22, false);
        ali.setBio("Software developer | Coffee lover | Travel enthusiast");
        
        RegularUser arfa = (RegularUser) registerUser("arfa", "password123", 
            "Arfa", "arfa@email.com", 21, false);
        arfa.setBio("Designer & Artist 🎨 | Nature photographer 📸");
        arfa.setVerified(true);
        
        RegularUser hashir = (RegularUser) registerUser("hashir", "password123", 
            "Hashir", "hashir@email.com", 23, false);
        hashir.setBio("Tech geek 💻 | Gamer 🎮 | Music producer 🎵");
        
        Admin admin = (Admin) registerUser("admin", "admin123", 
            "System Admin", "admin@socialconnect.com", 30, true);
        admin.setBio("Platform Administrator");

        // Reset current user
        currentUser = null;
    }

    // ==================== DATA PERSISTENCE ====================
    
    /**
     * Load saved data from files
     */
    private boolean loadSavedData() {
        DataPersistence.LoadResult result = DataPersistence.loadData();
        if (result.success && !result.users.isEmpty()) {
            this.users = result.users;
            this.posts = result.posts;
            this.userIdCounter = result.userIdCounter;
            this.postIdCounter = result.postIdCounter;
            this.commentIdCounter = result.commentIdCounter;
            this.reportIdCounter = result.reportIdCounter;
            // Handle backward compatibility for verification requests
            if (result.verificationRequests != null) {
                this.verificationRequests = result.verificationRequests;
            } else {
                this.verificationRequests = new ArrayList<>();
            }
            this.commentReports = result.commentReports != null ? result.commentReports : new ArrayList<>();
            System.out.println("✓ Loaded saved data: " + users.size() + " users, " + posts.size() + " posts");
            
            // Migrate old plain text passwords to hashed passwords
            migratePasswordsIfNeeded();
            
            return true;
        }
        return false;
    }
    

    private void migratePasswordsIfNeeded() {
        boolean needsSave = false;
        for (User user : users) {
            String storedPassword = user.getPassword();
            // SHA-256 hash is always 64 hex characters
            // If password is not 64 chars, it's likely plain text and needs to be hashed
            if (storedPassword != null && storedPassword.length() != 64) {
                // This is likely a plain text password, hash it
                System.out.println("Migrating password for user: " + user.getUsername());
                user.setPassword(storedPassword); // This will hash it via setPassword()
                needsSave = true;
            }
        }
        if (needsSave) {
            System.out.println("Password migration complete. Saving updated data...");
            saveData();
        }
    }
    
    // ==================== PROFILE PHOTO MANAGEMENT ====================
    
    /**
     * Update user's profile photo
     */
    public boolean updateProfilePhoto(String photoPath) {
        if (currentUser == null) {
            return false;
        }
        currentUser.setProfilePhotoPath(photoPath);
        saveData();
        return true;
    }
    
    // ==================== COMMENT REPORTING ====================
    
    /**
     * Report a comment as inappropriate
     */
    public boolean reportComment(String commentId, String postId, String reason) {
        if (currentUser == null || reason == null || reason.trim().isEmpty()) {
            return false;
        }
        
        // Find the comment
        Post post = getPostById(postId);
        if (post == null) {
            return false;
        }
        
        Comment comment = null;
        for (Comment c : post.getComments()) {
            if (c.getId().equals(commentId)) {
                comment = c;
                break;
            }
        }
        
        if (comment == null) {
            return false;
        }
        
        // Can't report your own comment
        if (comment.getAuthorId().equals(currentUser.getId())) {
            return false;
        }
        
        // Check if already reported by this user
        for (CommentReport report : commentReports) {
            if (report.getCommentId().equals(commentId) && 
                report.getReporterId().equals(currentUser.getId()) &&
                !report.isResolved()) {
                return false; // Already reported
            }
        }
        
        // Create report
        String reportId = "report_" + (reportIdCounter++);
        CommentReport report = new CommentReport(
            reportId,
            commentId,
            postId,
            currentUser.getId(),
            currentUser.getUsername(),
            comment.getAuthorId(),
            comment.getAuthorUsername(),
            comment.getContent(),
            reason.trim()
        );
        
        commentReports.add(report);
        saveData();
        return true;
    }
    
    /**
     * Get all unresolved comment reports (admin only)
     */
    public List<CommentReport> getUnresolvedReports() {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return new ArrayList<>();
        }
        
        List<CommentReport> unresolvedReports = new ArrayList<>();
        for (CommentReport report : commentReports) {
            if (!report.isResolved()) {
                unresolvedReports.add(report);
            }
        }
        
        // Sort by timestamp, newest first
        unresolvedReports.sort((r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
        
        return unresolvedReports;
    }
    
    /**
     * Get count of unresolved reports (admin only)
     */
    public int getUnresolvedReportCount() {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return 0;
        }
        
        int count = 0;
        for (CommentReport report : commentReports) {
            if (!report.isResolved()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Mark a report as resolved (admin only)
     */
    public boolean resolveReport(String reportId) {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return false;
        }
        
        for (CommentReport report : commentReports) {
            if (report.getReportId().equals(reportId)) {
                report.setResolved(true);
                saveData();
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Delete a reported comment (admin only)
     */
    public boolean deleteReportedComment(String commentId, String postId) {
        if (currentUser == null || !(currentUser instanceof Admin)) {
            return false;
        }
        
        Post post = getPostById(postId);
        if (post == null) {
            return false;
        }
        
        boolean removed = post.removeComment(commentId);
        if (removed) {
            // Mark all reports for this comment as resolved
            for (CommentReport report : commentReports) {
                if (report.getCommentId().equals(commentId)) {
                    report.setResolved(true);
                }
            }
            saveData();
        }
        
        return removed;
    }
    
    /**
     * Save current data to files
     */
    public void saveData() {
        DataPersistence.saveData(users, posts, userIdCounter, postIdCounter, commentIdCounter, 
                                reportIdCounter, verificationRequests, commentReports);
    }
    
    /**
     * Manually trigger data save (useful for shutdown hooks)
     */
    public void forceSave() {
        saveData();
        System.out.println("✓ Data saved successfully");
    }
}
