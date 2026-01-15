package models;

import java.io.Serializable;

/**
 * CommentReport - Represents a report/complaint about an inappropriate comment
 */
public class CommentReport implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String reportId;
    private String commentId;
    private String postId;
    private String reporterId; // User who reported
    private String reporterUsername;
    private String commentAuthorId; // User who made the comment
    private String commentAuthorUsername;
    private String commentContent;
    private String reason; // Reason for reporting
    private long timestamp;
    private boolean resolved; // Whether admin has handled it
    
    public CommentReport(String reportId, String commentId, String postId, 
                        String reporterId, String reporterUsername,
                        String commentAuthorId, String commentAuthorUsername,
                        String commentContent, String reason) {
        this.reportId = reportId;
        this.commentId = commentId;
        this.postId = postId;
        this.reporterId = reporterId;
        this.reporterUsername = reporterUsername;
        this.commentAuthorId = commentAuthorId;
        this.commentAuthorUsername = commentAuthorUsername;
        this.commentContent = commentContent;
        this.reason = reason;
        this.timestamp = System.currentTimeMillis();
        this.resolved = false;
    }
    
    // Getters
    public String getReportId() { return reportId; }
    public String getCommentId() { return commentId; }
    public String getPostId() { return postId; }
    public String getReporterId() { return reporterId; }
    public String getReporterUsername() { return reporterUsername; }
    public String getCommentAuthorId() { return commentAuthorId; }
    public String getCommentAuthorUsername() { return commentAuthorUsername; }
    public String getCommentContent() { return commentContent; }
    public String getReason() { return reason; }
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

