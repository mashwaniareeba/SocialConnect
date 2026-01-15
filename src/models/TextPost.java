package models;

public class TextPost extends Post {
    private static final long serialVersionUID = 1L;
    private String textContent;

    public TextPost(String id, String authorId, String authorUsername, String authorFullName, String textContent) {
        super(id, authorId, authorUsername, authorFullName);
        this.textContent = textContent;
    }

    @Override
    public String getPostType() {
        return "Text Post";
    }

    @Override
    public String getContent() {
        return textContent;
    }

    @Override
    public String getDisplayContent() {
        return textContent;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + textContent;
    }
}
