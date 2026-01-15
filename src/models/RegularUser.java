package models;

import java.util.ArrayList;
import java.util.List;

/**
 * RegularUser class 
 */
public class RegularUser extends User {
    private static final long serialVersionUID = 1L;
    private boolean isVerified;
    private boolean isPrivateAccount;
    private List<String> pendingFollowRequests; // Incoming follow requests (user IDs who want to follow this user)

    public RegularUser(String id, String username, String password, String fullName, String email, int age) {
        super(id, username, password, fullName, email, age);
        this.isVerified = false;
        this.isPrivateAccount = false; // Default to public account
        this.pendingFollowRequests = new ArrayList<>();
    }


    @Override
    public String getUserType() {
        return "Regular User";
    }

    @Override
    public boolean canDeleteAnyPost() {
        return false; 
    }

    // Additional methods specific to RegularUser
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    
    public boolean isPrivateAccount() { return isPrivateAccount; }
    public void setPrivateAccount(boolean isPrivate) { this.isPrivateAccount = isPrivate; }

    // Follow request management
    public List<String> getPendingFollowRequests() { 
        initializePendingRequests();
        return new ArrayList<>(pendingFollowRequests); 
    }
    
    public void addFollowRequest(String userId) {
        initializePendingRequests();
        if (!pendingFollowRequests.contains(userId) && !isFollowing(userId)) {
            pendingFollowRequests.add(userId);
        }
    }
    
    public void removeFollowRequest(String userId) {
        initializePendingRequests();
        pendingFollowRequests.remove(userId);
    }
    
    public boolean hasPendingRequestFrom(String userId) {
        initializePendingRequests();
        return pendingFollowRequests.contains(userId);
    }
    
    public boolean hasSentRequestTo(String userId) {
        // Check if this user has sent a request to userId
        // We'll need to check the other user's pending requests
        return false; // This will be checked via SocialNetworkSystem
    }
    
    public int getPendingRequestCount() {
        initializePendingRequests();
        return pendingFollowRequests.size();
    }
    
    // Initialize pendingFollowRequests if it's null (for backwards compatibility)
    private void initializePendingRequests() {
        if (pendingFollowRequests == null) {
            pendingFollowRequests = new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        String verifiedBadge = isVerified ? " ✓" : "";
        return super.toString() + verifiedBadge;
    }
}




