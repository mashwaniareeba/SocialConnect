package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Abstract User class - Base class for all user types
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private int age;
    private String bio;
    private String profilePhotoPath;
    private List<String> followers;
    private List<String> following;
    private List<String> postIds;

    public User(String id, String username, String password, String fullName, String email, int age) {
        this.id = id;
        this.username = username;
        this.password = hashPassword(password); 
        this.fullName = fullName;
        this.email = email;
        this.age = age;
        this.bio = "";
        this.profilePhotoPath = null;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.postIds = new ArrayList<>();
    }

    // Abstract method 
    public abstract String getUserType();
    public abstract boolean canDeleteAnyPost();

    // Getters and Setters 
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; } 
    public void setPassword(String password) { this.password = hashPassword(password); }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePhotoPath() { return profilePhotoPath; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }

    public List<String> getFollowers() { return new ArrayList<>(followers); }
    public int getFollowerCount() { return followers.size(); }

    public List<String> getFollowing() { return new ArrayList<>(following); }
    public int getFollowingCount() { return following.size(); }

    public List<String> getPostIds() { return new ArrayList<>(postIds); }
    public int getPostCount() { return postIds.size(); }

    // Social actions
    public void addFollower(String userId) {
        if (!followers.contains(userId)) {
            followers.add(userId);
        }
    }

    public void removeFollower(String userId) {
        followers.remove(userId);
    }

    public void follow(String userId) {
        if (!following.contains(userId)) {
            following.add(userId);
        }
    }

    public void unfollow(String userId) {
        following.remove(userId);
    }

    public boolean isFollowing(String userId) {
        return following.contains(userId);
    }

    public void addPost(String postId) {
        postIds.add(postId);
    }

    public void removePost(String postId) {
        postIds.remove(postId);
    }

    public boolean validatePassword(String inputPassword) {
        // Compare hashed input with stored hash
        String hashedInput = hashPassword(inputPassword);
        return this.password != null && this.password.equals(hashedInput);
    }
    
    /**
     * Hash a password using SHA-256 algorithm
     */
    private String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to plain text if hashing fails 
            System.err.println("Error hashing password: " + e.getMessage());
            return password;
        }
    }

    @Override
    public String toString() {
        return String.format("%s (@%s)", fullName, username);
    }
}




