package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Abstract Post class - Base class for all post types
 */
public abstract class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String authorId;
    private String authorUsername;
    private String authorFullName;
    private long timestamp;
    private ArrayList<String> likedByUserIds;
    private ArrayList<Comment> comments;

    public Post(String id, String authorId, String authorUsername, String authorFullName) {
        this.id = id;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.authorFullName = authorFullName;
        this.timestamp = System.currentTimeMillis();
        this.likedByUserIds = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    // Abstract methods
    public abstract String getPostType();
    public abstract String getContent();
    public abstract String getDisplayContent();

    // Getters
    public String getId() { return id; }
    public String getAuthorId() { return authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public String getAuthorFullName() { return authorFullName; }
    public long getTimestamp() { return timestamp; }

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

    // Like functionality
    public void addLike(String userId) {
        if (!likedByUserIds.contains(userId)) {
            likedByUserIds.add(userId);
        }
    }

    public void removeLike(String userId) {
        likedByUserIds.remove(userId);
    }

    public boolean isLikedBy(String userId) {
        return likedByUserIds.contains(userId);
    }

    public int getLikeCount() {
        return likedByUserIds.size();
    }

    // Comment functionality
    public void addComment(Comment comment) {
        comments.add(comment);
    }
    
    public boolean removeComment(String commentId) {
        return comments.removeIf(c -> c.getId().equals(commentId));
    }

    public ArrayList<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public int getCommentCount() {
        return comments.size();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", getPostType(), authorFullName, getRelativeTime());
    }
}
