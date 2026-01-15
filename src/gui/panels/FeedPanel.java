package gui.panels;

import gui.Theme;
import gui.components.*;
import models.*;
import system.SocialNetworkSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * FeedPanel - Displays posts in a scrollable feed
 * Features like, comment, and delete interactions with animations
 */
public class FeedPanel extends JPanel {
    private JPanel feedContainer;
    private JScrollPane scrollPane;
    private SocialNetworkSystem system;

    public FeedPanel() {
        system = SocialNetworkSystem.getInstance();
        setLayout(new BorderLayout());
        setOpaque(false); // Transparent to show gradient background
        initComponents();
        loadPosts();
    }

    private void initComponents() {
        // Feed container
        feedContainer = new JPanel();
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
        feedContainer.setOpaque(false); // Transparent to show gradient background

        // Scroll pane
        scrollPane = new JScrollPane(feedContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false); // Transparent
        scrollPane.getViewport().setOpaque(false); // Transparent

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadPosts() {
        feedContainer.removeAll();
        feedContainer.add(Box.createVerticalStrut(10));

        List<Post> posts = system.getFeedPosts();

        if (posts.isEmpty()) {
            JPanel emptyPanel = createEmptyState();
            feedContainer.add(emptyPanel);
        } else {
            for (Post post : posts) {
                JPanel postCard = createPostCard(post);
                feedContainer.add(postCard);
                feedContainer.add(Box.createVerticalStrut(20)); // More elegant spacing
            }
        }

        feedContainer.add(Box.createVerticalStrut(50));
        feedContainer.revalidate();
        feedContainer.repaint();
        
        // Don't scroll to top - preserve current scroll position
    }
    
    public void scrollToTop() {
        if (scrollPane != null) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMinimum());
            });
        }
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        JLabel iconLabel = new JLabel("📭");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel("No posts yet");
        textLabel.setFont(Theme.FONT_HEADING);
        textLabel.setForeground(Theme.TEXT_ON_DARK);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTextLabel = new JLabel("Be the first to post something!");
        subTextLabel.setFont(Theme.FONT_BODY);
        subTextLabel.setForeground(Theme.TEXT_ON_DARK);
        subTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(textLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subTextLabel);

        return panel;
    }

    private JPanel createPostCard(Post post) {
        RoundedPanel card = new RoundedPanel(16, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Header (author info)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        JPanel authorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        authorPanel.setOpaque(false);

        // Avatar placeholder
        User author = system.getUserById(post.getAuthorId());
        JLabel avatarLabel = createAvatar(author, 32);
        
        JPanel authorInfo = new JPanel();
        authorInfo.setLayout(new BoxLayout(authorInfo, BoxLayout.Y_AXIS));
        authorInfo.setOpaque(false);

        // Get author to check if verified
        String authorName = post.getAuthorFullName();
        String verifiedBadgeHtml = "";
        if (author instanceof RegularUser && ((RegularUser) author).isVerified()) {
            String verifiedColor = String.format("#%06X", Theme.VERIFIED_BLUE.getRGB() & 0xFFFFFF);
            verifiedBadgeHtml = " <span style='color:" + verifiedColor + "; font-size:16px; font-weight:bold;'>✓</span>";
        }
        if (author instanceof Admin) {
            verifiedBadgeHtml += " <span style='font-size:14px;'>🛡</span>";
        }
        JLabel nameLabel = new JLabel("<html>" + authorName + verifiedBadgeHtml + "</html>");
        nameLabel.setFont(Theme.FONT_SUBHEADING);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel timeLabel = new JLabel(post.getRelativeTime() + " • " + post.getPostType());
        timeLabel.setFont(Theme.FONT_SMALL);
        timeLabel.setForeground(Theme.TEXT_MUTED);

        authorInfo.add(nameLabel);
        authorInfo.add(timeLabel);

        authorPanel.add(avatarLabel);
        authorPanel.add(authorInfo);

        headerPanel.add(authorPanel, BorderLayout.WEST);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // Handle different post types
        if (post instanceof ImagePost) {
            ImagePost imagePost = (ImagePost) post;
            // Display image
            JLabel imageLabel = createImageLabel(imagePost.getImagePath());
            if (imageLabel != null) {
                contentPanel.add(imageLabel);
                contentPanel.add(Box.createVerticalStrut(10));
            }
            // Display caption if exists
            if (imagePost.hasCaption()) {
                JTextArea captionText = new JTextArea(imagePost.getCaption());
                captionText.setFont(Theme.FONT_BODY);
                captionText.setForeground(Theme.TEXT_PRIMARY);
                captionText.setLineWrap(true);
                captionText.setWrapStyleWord(true);
                captionText.setEditable(false);
                captionText.setOpaque(false);
                captionText.setBorder(null);
                captionText.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(captionText);
            }
        } else {
            // Text post
            JTextArea contentText = new JTextArea(post.getDisplayContent());
            contentText.setFont(Theme.FONT_BODY);
            contentText.setForeground(Theme.TEXT_PRIMARY);
            contentText.setLineWrap(true);
            contentText.setWrapStyleWord(true);
            contentText.setEditable(false);
            contentText.setOpaque(false);
            contentText.setBorder(null);
            contentPanel.add(contentText);
        }

        // Stats bar
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.DIVIDER_COLOR),
            BorderFactory.createEmptyBorder(12, 0, 12, 0)
        ));

        int likeCount = post.getLikeCount();
        int commentCount = post.getCommentCount();

        if (likeCount > 0 || commentCount > 0) {
            StringBuilder stats = new StringBuilder();
            if (likeCount > 0) stats.append("♥ ").append(likeCount).append(" likes");
            if (commentCount > 0) {
                if (stats.length() > 0) stats.append("  •  ");
                stats.append(commentCount).append(" comments");
            }
            
            JLabel statsLabel = new JLabel(stats.toString());
            statsLabel.setFont(Theme.FONT_SMALL);
            statsLabel.setForeground(Theme.TEXT_MUTED);
            statsPanel.add(statsLabel);
        }

        // Actions bar
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0)); // No bottom padding

        // Like button
        boolean isLiked = system.isPostLikedByCurrentUser(post.getId());
        JButton likeButton = createActionButton(
            isLiked ? "♥ Unlike" : "♡ Like",
            isLiked ? Theme.LIKE_RED : Theme.TEXT_SECONDARY
        );
        likeButton.addActionListener(e -> {
            // Save scroll position before refresh
            int scrollPosition = scrollPane.getVerticalScrollBar().getValue();
            system.likePost(post.getId());
            loadPosts(); // Refresh to show updated state
            // Restore scroll position after refresh
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(scrollPosition);
            });
        });
        actionsPanel.add(likeButton);

        // Comment button (after like button)
        JButton commentButton = createActionButton("💬 Comment", Theme.TEXT_SECONDARY);
        commentButton.addActionListener(e -> showCommentDialog(post));
        actionsPanel.add(commentButton);

        // Delete button (only show for post owner or admin)
        User currentUser = system.getCurrentUser();
        if (currentUser != null) {
            boolean canDelete = post.getAuthorId().equals(currentUser.getId()) || 
                               currentUser.canDeleteAnyPost();
            if (canDelete) {
                JButton deleteButton = createActionButton("<html><span style='font-family:Segoe UI Emoji;'>🗑</span> Delete</html>", Theme.ACCENT_RED);
                deleteButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this post?",
                        "Delete Post",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (system.deletePost(post.getId())) {
                            JOptionPane.showMessageDialog(
                                this,
                                "Post deleted successfully!",
                                "Deleted",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            loadPosts(); // Refresh feed
                        } else {
                            JOptionPane.showMessageDialog(
                                this,
                                "Failed to delete post.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                });
                actionsPanel.add(deleteButton);
            }
        }

        // Comments section (show recent comments) - positioned on the right side
        JPanel commentsSection = new JPanel(new BorderLayout());
        commentsSection.setOpaque(false);
        commentsSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No padding
        
        JPanel commentsContainer = new JPanel();
        commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        commentsContainer.setOpaque(false);
        commentsContainer.setAlignmentX(Component.RIGHT_ALIGNMENT);

        List<Comment> comments = post.getComments();
        if (!comments.isEmpty()) {
            // Initial collapsed view - show only 2 comments
            JPanel collapsedView = new JPanel();
            collapsedView.setLayout(new BoxLayout(collapsedView, BoxLayout.Y_AXIS));
            collapsedView.setOpaque(false);
            
            int displayCount = Math.min(2, comments.size());
            for (int i = 0; i < displayCount; i++) {
                Comment comment = comments.get(i);
                JPanel commentPanel = createCommentPanel(comment);
                collapsedView.add(commentPanel);
                if (i < displayCount - 1) {
                    collapsedView.add(Box.createVerticalStrut(8));
                }
            }
            
            commentsContainer.add(collapsedView);
            
            if (comments.size() > 2) {
                // "View all" / "Show less" toggle link
                JLabel toggleLabel = new JLabel("View all " + comments.size() + " comments");
                toggleLabel.setFont(Theme.FONT_SMALL);
                toggleLabel.setForeground(Theme.TEXT_LINK);
                toggleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                toggleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                // Expanded view - all comments in scrollpane
                JPanel expandedCommentsPanel = new JPanel();
                expandedCommentsPanel.setLayout(new BoxLayout(expandedCommentsPanel, BoxLayout.Y_AXIS));
                expandedCommentsPanel.setOpaque(false);
                
                for (int i = 0; i < comments.size(); i++) {
                    Comment comment = comments.get(i);
                    JPanel commentPanel = createCommentPanel(comment);
                    expandedCommentsPanel.add(commentPanel);
                    if (i < comments.size() - 1) {
                        expandedCommentsPanel.add(Box.createVerticalStrut(8));
                    }
                }
                
                JScrollPane commentsScrollPane = new JScrollPane(expandedCommentsPanel);
                commentsScrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
                commentsScrollPane.setOpaque(false);
                commentsScrollPane.getViewport().setOpaque(false);
                commentsScrollPane.setPreferredSize(new Dimension(550, 200));
                commentsScrollPane.setMaximumSize(new Dimension(550, 200));
                commentsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
                commentsScrollPane.setVisible(false); // Hidden initially
                
                toggleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    private boolean expanded = false;
                    
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        expanded = !expanded;
                        if (expanded) {
                            // Show all comments
                            collapsedView.setVisible(false);
                            commentsScrollPane.setVisible(true);
                            toggleLabel.setText("Show less");
                        } else {
                            // Show only 2 comments
                            collapsedView.setVisible(true);
                            commentsScrollPane.setVisible(false);
                            toggleLabel.setText("View all " + comments.size() + " comments");
                        }
                        commentsContainer.revalidate();
                        commentsContainer.repaint();
                    }
                });
                
                commentsContainer.add(Box.createVerticalStrut(8));
                commentsContainer.add(toggleLabel);
                commentsContainer.add(Box.createVerticalStrut(8));
                commentsContainer.add(commentsScrollPane);
            }
        }
        commentsSection.add(commentsContainer, BorderLayout.WEST);

        // Assemble card
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.add(headerPanel);
        mainContent.add(contentPanel);
        mainContent.add(statsPanel);
        mainContent.add(actionsPanel);
        // Add minimal spacing between buttons and comments
        mainContent.add(Box.createVerticalStrut(5));
        mainContent.add(commentsSection);

        card.add(mainContent, BorderLayout.CENTER);

        // Return card directly without wrapper to extend across entire background
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private JButton createActionButton(String text, Color textColor) {
        // Use HTML to render icons properly
        String htmlText = "<html>" + text + "</html>";
        JButton button = new JButton(htmlText);
        button.setFont(Theme.FONT_BODY);
        button.setForeground(textColor);
        button.setBackground(Theme.CARD_BACKGROUND);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45)); // Bigger buttons
        button.setMinimumSize(new Dimension(140, 45));
        button.setMaximumSize(new Dimension(140, 45));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.LIGHT_BLUE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Theme.CARD_BACKGROUND);
            }
        });

        return button;
    }

    private JPanel createCommentPanel(Comment comment) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align the comment panel
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No padding for extreme left

        // Get commenter info
        User commenter = system.getUserById(comment.getAuthorId());
        JLabel avatarLabel = createAvatar(commenter, 20);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align text panel
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No padding

        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topLine.setOpaque(false);

        JLabel authorLabel = new JLabel(comment.getAuthorFullName());
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        authorLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel timeLabel = new JLabel("• " + comment.getRelativeTime());
        timeLabel.setFont(Theme.FONT_SMALL);
        timeLabel.setForeground(Theme.TEXT_MUTED);

        topLine.add(authorLabel);
        topLine.add(timeLabel);
        
        // Add Report link (only if not own comment AND it's on current user's post)
        User currentUser = system.getCurrentUser();
        Post parentPost = system.getPostById(comment.getPostId());
        boolean isOwnPost = currentUser != null && parentPost != null && 
                           parentPost.getAuthorId().equals(currentUser.getId());
        boolean isOwnComment = currentUser != null && comment.getAuthorId().equals(currentUser.getId());
        
        if (currentUser != null && isOwnPost && !isOwnComment) {
            JLabel reportLabel = new JLabel("• Report");
            reportLabel.setFont(Theme.FONT_SMALL);
            reportLabel.setForeground(new Color(0xDC, 0x14, 0x3C)); // Red
            reportLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            reportLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showReportCommentDialog(comment);
                }
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    reportLabel.setForeground(Color.RED);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    reportLabel.setForeground(new Color(0xDC, 0x14, 0x3C));
                }
            });
            topLine.add(reportLabel);
        }

        JLabel contentLabel = new JLabel(comment.getContent());
        contentLabel.setFont(Theme.FONT_SMALL);
        contentLabel.setForeground(Theme.TEXT_PRIMARY);
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align content

        textPanel.add(topLine);
        textPanel.add(contentLabel);

        panel.add(avatarLabel);
        panel.add(textPanel);

        return panel;
    }
    
    private void showReportCommentDialog(Comment comment) {
        String[] options = {
            "Spam or misleading",
            "Harassment or hate speech",
            "Violence or dangerous content",
            "Sexual content",
            "Other (specify)"
        };
        
        String selectedReason = (String) JOptionPane.showInputDialog(
            this,
            "Why are you reporting this comment?",
            "Report Comment",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (selectedReason != null) {
            String finalReason = selectedReason;
            
            // If "Other", ask for details
            if (selectedReason.equals("Other (specify)")) {
                String customReason = JOptionPane.showInputDialog(
                    this,
                    "Please specify the reason:",
                    "Report Comment",
                    JOptionPane.PLAIN_MESSAGE
                );
                if (customReason != null && !customReason.trim().isEmpty()) {
                    finalReason = customReason.trim();
                } else {
                    return; // Cancelled
                }
            }
            
            // Submit report
            if (system.reportComment(comment.getId(), comment.getPostId(), finalReason)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Thank you for reporting. An admin will review this comment.",
                    "Report Submitted",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to submit report. You may have already reported this comment.",
                    "Report Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showCommentDialog(Post post) {
        String comment = JOptionPane.showInputDialog(
            this,
            "Write a comment:",
            "Add Comment",
            JOptionPane.PLAIN_MESSAGE
        );

        if (comment != null && !comment.trim().isEmpty()) {
            // Save scroll position before refresh
            int scrollPosition = scrollPane.getVerticalScrollBar().getValue();
            system.addComment(post.getId(), comment.trim());
            loadPosts();
            // Restore scroll position after refresh
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(scrollPosition);
            });
        }
    }

    private JLabel createAvatar(User user, int size) {
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(size, size));
        avatarLabel.setOpaque(false);
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Try to load profile photo
        if (user != null && user.getProfilePhotoPath() != null && !user.getProfilePhotoPath().isEmpty()) {
            try {
                File imageFile = new File(user.getProfilePhotoPath());
                if (imageFile.exists()) {
                    BufferedImage img = javax.imageio.ImageIO.read(imageFile);
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                        avatarLabel.setIcon(new ImageIcon(scaledImg));
                        return avatarLabel;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading avatar: " + e.getMessage());
            }
        }
        
        // Fallback to emoji
        avatarLabel.setText("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size * 2 / 3));
        return avatarLabel;
    }
    
    private JLabel createImageLabel(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }
        
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                // Try to show a placeholder or error message
                JLabel errorLabel = new JLabel("📷 Image not found: " + imageFile.getName());
                errorLabel.setFont(Theme.FONT_SMALL);
                errorLabel.setForeground(Theme.TEXT_MUTED);
                return errorLabel;
            }
            
            // Load and resize image
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                JLabel errorLabel = new JLabel("📷 Unable to load image");
                errorLabel.setFont(Theme.FONT_SMALL);
                errorLabel.setForeground(Theme.TEXT_MUTED);
                return errorLabel;
            }
            
            // Calculate dimensions (max width 600px, maintain aspect ratio)
            int maxWidth = 600;
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            
            int displayWidth = Math.min(originalWidth, maxWidth);
            int calculatedHeight = (int) ((double) displayWidth / originalWidth * originalHeight);
            // Display at half the calculated height
            int displayHeight = calculatedHeight / 2;
            
            // Scale image
            Image scaledImage = originalImage.getScaledInstance(
                displayWidth, displayHeight, Image.SCALE_SMOOTH);
            
            // Create label with image
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            return imageLabel;
        } catch (Exception e) {
            // If image loading fails, show error message
            JLabel errorLabel = new JLabel("📷 Error loading image: " + new File(imagePath).getName());
            errorLabel.setFont(Theme.FONT_SMALL);
            errorLabel.setForeground(Theme.TEXT_MUTED);
            return errorLabel;
        }
    }

    public void refresh() {
        loadPosts();
        scrollToTop();
    }
}




