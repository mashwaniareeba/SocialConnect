package models;

import java.io.Serializable;

/**
 * VerificationRequest - Represents a verification request with uploaded content
 */
public class VerificationRequest implements Serializable {
    private static final long serialVersionUID = 2L;
    
    private String userId;
    private String username;
    private String fullName;
    private String contentFilePath; // Path to uploaded content file (image/document)
    private long timestamp;
    private boolean resolved; // Whether admin has handled it
    
    public VerificationRequest(String userId, String username, String fullName,
                              String contentFilePath) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.contentFilePath = contentFilePath;
        this.timestamp = System.currentTimeMillis();
        this.resolved = false;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getContentFilePath() { return contentFilePath; }
    public long getTimestamp() { return timestamp; }
    public boolean isResolved() { return resolved; }
    
    // Setters
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    
    public String getRelativeTime() {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / 60000;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d ago";
        if (hours > 0) return hours + "h ago";
        if (minutes > 0) return minutes + "m ago";
        return "Just now";
    }
}

