package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Comment class - Represents a comment on a post
 */
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String postId;
    private String authorId;
    private String authorUsername;
    private String authorFullName;
    private String content;
    private long timestamp;
    private ArrayList<String> likedByUserIds;

    public Comment(String id, String postId, String authorId, String authorUsername, 
                   String authorFullName, String content) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.authorFullName = authorFullName;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.likedByUserIds = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public String getAuthorFullName() { return authorFullName; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRelativeTime() {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / 60000;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d";
        if (hours > 0) return hours + "h";
        if (minutes > 0) return minutes + "m";
        return "now";
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

    @Override
    public String toString() {
        return String.format("%s: %s (%s)", authorFullName, content, getRelativeTime());
    }
}
