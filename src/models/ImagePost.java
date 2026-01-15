package models;

public class ImagePost extends Post {
    private static final long serialVersionUID = 1L;
    private String imagePath;
    private String caption;

    public ImagePost(String id, String authorId, String authorUsername, String authorFullName, 
                     String imagePath, String caption) {
        super(id, authorId, authorUsername, authorFullName);
        this.imagePath = imagePath;
        this.caption = caption;
    }

    @Override
    public String getPostType() {
        return "Image Post";
    }

    @Override
    public String getContent() {
        return imagePath;
    }

    @Override
    public String getDisplayContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ Image: ").append(getImageFileName()).append("]");
        if (caption != null && !caption.isEmpty()) {
            sb.append("\n").append(caption);
        }
        return sb.toString();
    }

    public String getImagePath() { return imagePath; }

    public String getCaption() { return caption; }

    public String getImageFileName() {
        if (imagePath == null || imagePath.isEmpty()) {
            return "No image";
        }
        int lastSeparator = Math.max(imagePath.lastIndexOf('/'), imagePath.lastIndexOf('\\'));
        if (lastSeparator >= 0 && lastSeparator < imagePath.length() - 1) {
            return imagePath.substring(lastSeparator + 1);
        }
        return imagePath;
    }

    public boolean hasCaption() {
        return caption != null && !caption.trim().isEmpty();
    }

    @Override
    public String toString() {
        return super.toString() + "\n[Image: " + getImageFileName() + "]" + 
               (hasCaption() ? "\n" + caption : "");
    }
}
