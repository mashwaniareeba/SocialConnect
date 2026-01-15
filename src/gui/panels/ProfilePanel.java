package gui.panels;

import gui.Theme;
import gui.components.*;
import models.*;
import system.SocialNetworkSystem;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * ProfilePanel - Displays user profile with stats and posts
 */
public class ProfilePanel extends JPanel {
    private SocialNetworkSystem system;
    private User displayedUser;
    private JPanel contentPanel;
    private JScrollPane scrollPane;

    public ProfilePanel() {
        system = SocialNetworkSystem.getInstance();
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.BACKGROUND);

        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Theme.BACKGROUND);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadCurrentUserProfile() {
        displayedUser = system.getCurrentUser();
        refreshProfile();
    }

    public void loadUserProfile(User user) {
        this.displayedUser = user;
        refreshProfile();
    }
    
    public User getDisplayedUser() {
        return displayedUser;
    }

    public void refreshProfile() {
        contentPanel.removeAll();

        // If no displayed user is set, load current user's profile
        if (displayedUser == null) {
            displayedUser = system.getCurrentUser();
        }
        
        if (displayedUser == null) {
            showNoUserState();
            return;
        }

        contentPanel.add(Box.createVerticalStrut(20));

        // Profile header card
        JPanel headerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerWrapper.setOpaque(false);
        headerWrapper.add(createProfileHeader());
        contentPanel.add(headerWrapper);

        contentPanel.add(Box.createVerticalStrut(20));

        // User's posts section
        JPanel postsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        postsWrapper.setOpaque(false);
        postsWrapper.add(createPostsSection());
        contentPanel.add(postsWrapper);

        contentPanel.add(Box.createVerticalStrut(50));

        contentPanel.revalidate();
        contentPanel.repaint();
        
        SwingUtilities.invokeLater(() -> scrollToTop());
    }
    
    public void scrollToTop() {
        if (scrollPane != null) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(0);
            });
        }
    }

    private void showNoUserState() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));

        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel("No user selected");
        textLabel.setFont(Theme.FONT_HEADING);
        textLabel.setForeground(Theme.TEXT_ON_DARK);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(textLabel);

        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * @return
     */
    private JPanel createProfileHeader() {
        RoundedPanel card = new RoundedPanel(16, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BorderLayout(20, 0));
        // Don't set fixed height - let it size naturally

        // Cover photo simulation
        JPanel coverPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, Theme.PRIMARY_BLUE,
                    getWidth(), 0, Theme.SECONDARY_BLUE
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        coverPanel.setPreferredSize(new Dimension(600, 80));

        // Main info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Top section: Avatar and name
        JPanel topSection = new JPanel(new BorderLayout(15, 0));
        topSection.setOpaque(false);

        // Large avatar
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        avatarPanel.setOpaque(false);
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(Theme.LIGHT_BLUE);
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        avatarPanel.add(avatarLabel);

        // Name and username
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        // Full name with verified badge
        String fullNameText = displayedUser.getFullName();
        String verifiedBadgeHtml = "";
        if (displayedUser instanceof RegularUser && ((RegularUser) displayedUser).isVerified()) {
            String verifiedColor = String.format("#%06X", Theme.VERIFIED_BLUE.getRGB() & 0xFFFFFF);
            verifiedBadgeHtml = " <span style='color:" + verifiedColor + "; font-size:20px; font-weight:bold;'>✓</span>";
        }
        if (displayedUser instanceof Admin) {
            verifiedBadgeHtml += " <span style='font-size:18px;'>🛡</span>";
        }
        JLabel fullNameLabel = new JLabel("<html>" + fullNameText + verifiedBadgeHtml + "</html>");
        fullNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        fullNameLabel.setForeground(Theme.TEXT_PRIMARY);

        String usernameText = "@" + displayedUser.getUsername();
        if (displayedUser instanceof Admin) {
            usernameText = "<html>" + usernameText + " <span style='font-size:12px;'>🛡</span></html>";
        }
        JLabel usernameLabel = new JLabel(usernameText);
        usernameLabel.setFont(Theme.FONT_BODY);
        usernameLabel.setForeground(Theme.TEXT_SECONDARY);

        namePanel.add(fullNameLabel);
        namePanel.add(Box.createVerticalStrut(3));
        namePanel.add(usernameLabel);

        topSection.add(avatarPanel, BorderLayout.WEST);
        topSection.add(namePanel, BorderLayout.CENTER);

        infoPanel.add(topSection);
        infoPanel.add(Box.createVerticalStrut(15));

        // Bio section
        JLabel bioLabel = new JLabel(displayedUser.getBio().isEmpty() ? "No bio yet" : displayedUser.getBio());
        bioLabel.setFont(Theme.FONT_BODY);
        bioLabel.setForeground(Theme.TEXT_PRIMARY);
        bioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(bioLabel);
        infoPanel.add(Box.createVerticalStrut(15));

        // Stats row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setPreferredSize(new Dimension(580, 60));
        statsRow.add(createStatLabel(String.valueOf(displayedUser.getPostCount()), "Posts"));
        statsRow.add(createStatLabel(String.valueOf(displayedUser.getFollowerCount()), "Followers"));
        statsRow.add(createStatLabel(String.valueOf(displayedUser.getFollowingCount()), "Following"));
        infoPanel.add(statsRow);
        infoPanel.add(Box.createVerticalStrut(15));

        // Actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionsPanel.setOpaque(false);
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.setPreferredSize(new Dimension(580, 80));
        actionsPanel.setMaximumSize(new Dimension(580, 100));

        User currentUser = system.getCurrentUser();
        
        // Check if viewing own profile
        if (currentUser != null && displayedUser != null) {
            boolean isOwnProfile = currentUser.getId().equals(displayedUser.getId());
            
            if (isOwnProfile) {
                // Viewing own profile - show Edit button
                JButton editButton = new JButton("Edit Profile");
                editButton.setFont(Theme.FONT_BUTTON);
                editButton.setPreferredSize(new Dimension(140, 40));
                editButton.setBackground(Theme.PRIMARY_BLUE);
                editButton.setForeground(Color.WHITE);
                editButton.setFocusPainted(false);
                editButton.setBorderPainted(false);
                editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                editButton.addActionListener(e -> showEditProfileDialog());
                actionsPanel.add(editButton);
                
                // Only show followers/following buttons for regular users, not admins
                if (displayedUser instanceof RegularUser) {
                    // Add followers button
                    ModernButton followersButton = new ModernButton("Followers", ModernButton.STYLE_GHOST);
                    followersButton.setFont(Theme.FONT_BUTTON);
                    followersButton.setPreferredSize(new Dimension(120, 40));
                    followersButton.setMinimumSize(new Dimension(120, 40));
                    followersButton.addActionListener(e -> showFollowersList(displayedUser));
                    actionsPanel.add(followersButton);
                    
                    // Add following button
                    ModernButton followingButton = new ModernButton("Following", ModernButton.STYLE_GHOST);
                    followingButton.setFont(Theme.FONT_BUTTON);
                    followingButton.setPreferredSize(new Dimension(120, 40));
                    followingButton.setMinimumSize(new Dimension(120, 40));
                    followingButton.addActionListener(e -> showFollowingList(displayedUser));
                    actionsPanel.add(followingButton);
                }
                
                // Add verification request button for regular users viewing own profile
                if (displayedUser instanceof RegularUser) {
                    RegularUser regularUser = (RegularUser) displayedUser;
                    boolean hasPending = system.hasPendingVerificationRequest(displayedUser.getId());
                    boolean isVerified = regularUser.isVerified();
                    
                    ModernButton verifyRequestButton = new ModernButton("Request Verification", ModernButton.STYLE_GHOST);
                    verifyRequestButton.setFont(Theme.FONT_BUTTON);
                    verifyRequestButton.setPreferredSize(new Dimension(180, 40));
                    verifyRequestButton.setVisible(true);
                    verifyRequestButton.setEnabled(true);
                    
                    if (isVerified) {
                        verifyRequestButton.setText("Verified");
                        verifyRequestButton.setEnabled(false);
                    } else if (hasPending) {
                        verifyRequestButton.setText("⏳ Pending");
                        verifyRequestButton.setEnabled(false);
                    } else {
                        verifyRequestButton.addActionListener(e -> {
                            // Show file chooser to select verification content file
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setDialogTitle("Select Verification Content File");
                            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            
                            int result = fileChooser.showOpenDialog(this);
                            if (result == JFileChooser.APPROVE_OPTION) {
                                File selectedFile = fileChooser.getSelectedFile();
                                String filePath = selectedFile.getAbsolutePath();
                                
                                if (system.requestVerification(filePath)) {
                                    JOptionPane.showMessageDialog(this,
                                        "Verification request submitted! An admin will review it.",
                                        "Request Submitted",
                                        JOptionPane.INFORMATION_MESSAGE);
                                    refreshProfile();
                                } else {
                                    JOptionPane.showMessageDialog(this,
                                        "Failed to submit verification request. Please try again.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
                    }
                    actionsPanel.add(verifyRequestButton);
                }
                
                // Admin viewing own profile - show verification management
                if (currentUser instanceof Admin && displayedUser instanceof Admin) {
                    ModernButton verifyRequestsButton = new ModernButton("Manage Verification Requests", ModernButton.STYLE_PRIMARY);
                    verifyRequestsButton.setFont(Theme.FONT_BUTTON);
                    verifyRequestsButton.setPreferredSize(new Dimension(250, 40));
                    verifyRequestsButton.addActionListener(e -> showVerificationRequestsDialog());
                    actionsPanel.add(verifyRequestsButton);
                }
                
                // Force panel to be visible and repaint
                actionsPanel.setVisible(true);
                actionsPanel.revalidate();
                actionsPanel.repaint();
            } else {
                // Viewing another user's profile - show Follow/Message buttons
            boolean isFollowing = system.isFollowingUser(displayedUser.getId());
                
                // Follow/Unfollow button
            ModernButton followButton = new ModernButton(
                isFollowing ? "Unfollow" : "Follow",
                isFollowing ? ModernButton.STYLE_SECONDARY : ModernButton.STYLE_PRIMARY
            );
                followButton.setFont(Theme.FONT_BUTTON);
            followButton.addActionListener(e -> {
                system.followUser(displayedUser.getId());
                refreshProfile();
            });
            actionsPanel.add(followButton);

                // Message button
            ModernButton messageButton = new ModernButton("Message", ModernButton.STYLE_GHOST);
                messageButton.setFont(Theme.FONT_BUTTON);
            messageButton.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "Messaging coming soon!", "Message", 
                                                 JOptionPane.INFORMATION_MESSAGE);
            });
            actionsPanel.add(messageButton);
                
                // Admin actions when viewing other users
                if (currentUser instanceof Admin && displayedUser instanceof RegularUser) {
                    RegularUser regularUser = (RegularUser) displayedUser;
                    
                    // Verify/Unverify button
                    boolean isVerified = regularUser.isVerified();
                    ModernButton verifyButton = new ModernButton(
                        isVerified ? "Unverify" : "Verify",
                        isVerified ? ModernButton.STYLE_SECONDARY : ModernButton.STYLE_PRIMARY
                    );
                    verifyButton.setFont(Theme.FONT_BUTTON);
                    verifyButton.setBackground(isVerified ? Theme.ACCENT_RED : Theme.VERIFIED_BLUE);
                    verifyButton.setForeground(Color.WHITE);
                    verifyButton.addActionListener(e -> {
                        if (isVerified) {
                            int confirm = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to remove verification from " + displayedUser.getFullName() + "?",
                                "Unverify User", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (confirm == JOptionPane.YES_OPTION) {
                                if (system.unverifyUser(displayedUser.getId())) {
                                    JOptionPane.showMessageDialog(this,
                                        "User verification removed successfully.",
                                        "Unverified", JOptionPane.INFORMATION_MESSAGE);
                                    refreshProfile();
                                }
                            }
                        } else {
                            if (system.verifyUser(displayedUser.getId())) {
                                JOptionPane.showMessageDialog(this,
                                    "User verified successfully!",
                                    "Verified", JOptionPane.INFORMATION_MESSAGE);
                                refreshProfile();
                            }
                        }
                    });
                    actionsPanel.add(verifyButton);
                    
                    // Ban/Unban button
                    boolean isBanned = system.isUserBannedByAdmin(displayedUser.getId());
                    ModernButton banButton = new ModernButton(
                        isBanned ? "Unban User" : "Ban User",
                        isBanned ? ModernButton.STYLE_SECONDARY : ModernButton.STYLE_PRIMARY
                    );
                    banButton.setFont(Theme.FONT_BUTTON);
                    banButton.setBackground(isBanned ? Theme.VERIFIED_BLUE : Theme.ACCENT_RED);
                    banButton.setForeground(Color.WHITE);
                    banButton.addActionListener(e -> {
                        if (isBanned) {
                            int confirm = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to unban " + displayedUser.getFullName() + "?",
                                "Unban User", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (confirm == JOptionPane.YES_OPTION) {
                                if (system.unbanUser(displayedUser.getId())) {
                                    JOptionPane.showMessageDialog(this,
                                        "User unbanned successfully.",
                                        "Unbanned", JOptionPane.INFORMATION_MESSAGE);
                                    refreshProfile();
                                }
                            }
                        } else {
                            String reason = JOptionPane.showInputDialog(this,
                                "Enter reason for banning " + displayedUser.getFullName() + ":\n" +
                                "(e.g., Inappropriate content, Spam, Harassment)",
                                "Ban User",
                                JOptionPane.QUESTION_MESSAGE);
                            if (reason != null && !reason.trim().isEmpty()) {
                                int confirm = JOptionPane.showConfirmDialog(this,
                                    "Are you sure you want to ban " + displayedUser.getFullName() + "?\n" +
                                    "Reason: " + reason.trim(),
                                    "Ban User", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                if (confirm == JOptionPane.YES_OPTION) {
                                    if (system.banUser(displayedUser.getId())) {
                                        JOptionPane.showMessageDialog(this,
                                            "User banned successfully.\nReason: " + reason.trim(),
                                            "Banned", JOptionPane.INFORMATION_MESSAGE);
                                        refreshProfile();
                                    }
                                }
                            }
                        }
                    });
                    actionsPanel.add(banButton);
                }
                
                // View Followers/Following buttons (only for regular users, not admins)
                if (displayedUser instanceof RegularUser) {
                    ModernButton viewFollowersButton = new ModernButton("Followers", ModernButton.STYLE_GHOST);
                    viewFollowersButton.setFont(Theme.FONT_BUTTON);
                    viewFollowersButton.addActionListener(e -> showFollowersList(displayedUser));
                    actionsPanel.add(viewFollowersButton);
                    
                    ModernButton viewFollowingButton = new ModernButton("Following", ModernButton.STYLE_GHOST);
                    viewFollowingButton.setFont(Theme.FONT_BUTTON);
                    viewFollowingButton.addActionListener(e -> showFollowingList(displayedUser));
                    actionsPanel.add(viewFollowingButton);
                }
            }
        } else {
            // Not logged in or no user displayed
            JLabel noActionsLabel = new JLabel("Login to interact");
            noActionsLabel.setFont(Theme.FONT_SMALL);
            noActionsLabel.setForeground(Theme.TEXT_MUTED);
            actionsPanel.add(noActionsLabel);
        }

        infoPanel.add(actionsPanel);

        // Assemble
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(coverPanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        card.add(mainPanel);

        return card;
    }

    private JPanel createStatLabel(String value, String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 50));
        panel.setMinimumSize(new Dimension(100, 50));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(Theme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(Theme.FONT_SMALL);
        textLabel.setForeground(Theme.TEXT_MUTED);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(textLabel);

        return panel;
    }

    private JPanel createPostsSection() {
        RoundedPanel card = new RoundedPanel(16, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 420));

        // Section header
        JLabel headerLabel = new JLabel("<html><span style='font-family:Segoe UI Emoji;font-size:18px;'>✎</span> Posts</html>");
        headerLabel.setFont(Theme.FONT_HEADING);
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerLabel);
        card.add(Box.createVerticalStrut(15));

        // User's posts
        List<Post> userPosts = system.getPostsByUser(displayedUser.getId());

        if (userPosts.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));

            JLabel emptyIcon = new JLabel("📭");
            emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyText = new JLabel("No posts yet");
            emptyText.setFont(Theme.FONT_BODY);
            emptyText.setForeground(Theme.TEXT_MUTED);
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(Box.createVerticalStrut(30));
            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(10));
            emptyPanel.add(emptyText);

            card.add(emptyPanel);
        } else {
            JPanel postsContainer = new JPanel();
            postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
            postsContainer.setOpaque(false);

            for (Post post : userPosts) {
                JPanel postItem = createPostItem(post);
                postsContainer.add(postItem);
                postsContainer.add(Box.createVerticalStrut(10));
            }

            JScrollPane postsScroll = new JScrollPane(postsContainer);
            postsScroll.setBorder(null);
            postsScroll.setOpaque(false);
            postsScroll.getViewport().setOpaque(false);
            postsScroll.setPreferredSize(new Dimension(550, 300));
            postsScroll.getVerticalScrollBar().setUnitIncrement(16);

            card.add(postsScroll);
        }

        return card;
    }

    private JPanel createPostItem(Post post) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // Handle ImagePost - show thumbnail
        if (post instanceof ImagePost) {
            ImagePost imagePost = (ImagePost) post;
            JLabel imageLabel = createThumbnail(imagePost.getImagePath());
            if (imageLabel != null) {
                imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                textPanel.add(imageLabel);
                textPanel.add(Box.createVerticalStrut(5));
            }
            if (imagePost.hasCaption()) {
                String caption = imagePost.getCaption();
                if (caption.length() > 80) {
                    caption = caption.substring(0, 80) + "...";
                }
                JLabel captionLabel = new JLabel(caption);
                captionLabel.setFont(Theme.FONT_BODY);
                captionLabel.setForeground(Theme.TEXT_PRIMARY);
                textPanel.add(captionLabel);
                textPanel.add(Box.createVerticalStrut(3));
            }
        } else {
            // Text post
        String content = post.getDisplayContent();
        if (content.length() > 100) {
            content = content.substring(0, 100) + "...";
        }
        JLabel contentLabel = new JLabel("<html>" + content + "</html>");
        contentLabel.setFont(Theme.FONT_BODY);
        contentLabel.setForeground(Theme.TEXT_PRIMARY);
            textPanel.add(contentLabel);
            textPanel.add(Box.createVerticalStrut(3));
        }

        JLabel metaLabel = new JLabel(post.getRelativeTime() + " • " + 
                                      post.getLikeCount() + " likes • " + 
                                      post.getCommentCount() + " comments");
        metaLabel.setFont(Theme.FONT_SMALL);
        metaLabel.setForeground(Theme.TEXT_MUTED);
        textPanel.add(metaLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createThumbnail(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                JLabel errorLabel = new JLabel("📷 " + imageFile.getName());
                errorLabel.setFont(Theme.FONT_SMALL);
                errorLabel.setForeground(Theme.TEXT_MUTED);
                return errorLabel;
            }
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                return new JLabel("📷 Unable to load image");
            }
            // Create thumbnail (max 200px width)
            int maxWidth = 200;
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int displayWidth = Math.min(originalWidth, maxWidth);
            int displayHeight = (int) ((double) displayWidth / originalWidth * originalHeight);
            
            Image scaledImage = originalImage.getScaledInstance(displayWidth, displayHeight, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaledImage));
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("📷 Error loading image");
            errorLabel.setFont(Theme.FONT_SMALL);
            errorLabel.setForeground(Theme.TEXT_MUTED);
            return errorLabel;
        }
    }

    private void showEditProfileDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField fullNameField = new JTextField(displayedUser.getFullName(), 20);
        JTextArea bioField = new JTextArea(displayedUser.getBio(), 3, 20);
        bioField.setLineWrap(true);
        bioField.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Bio:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(bioField), gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Profile",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            displayedUser.setFullName(fullNameField.getText().trim());
            displayedUser.setBio(bioField.getText().trim());
            system.saveData(); // Save changes to disk
            refreshProfile();
        }
    }
    
    private void showFollowersList(User user) {
        List<String> followerIds = user.getFollowers();
        
        if (followerIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                user.getFullName() + " has no followers yet.", 
                "Followers", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder listText = new StringBuilder();
        listText.append(user.getFullName()).append("'s Followers:\n\n");
        
        for (String followerId : followerIds) {
            User follower = system.getUserById(followerId);
            if (follower != null) {
                listText.append("• ").append(follower.getFullName())
                        .append(" (@").append(follower.getUsername()).append(")\n");
            }
        }
        
        JTextArea textArea = new JTextArea(listText.toString());
        textArea.setEditable(false);
        textArea.setFont(Theme.FONT_BODY);
        textArea.setRows(10);
        textArea.setColumns(30);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Followers", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showFollowingList(User user) {
        List<String> followingIds = user.getFollowing();
        
        if (followingIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                user.getFullName() + " is not following anyone yet.", 
                "Following", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder listText = new StringBuilder();
        listText.append(user.getFullName()).append(" is following:\n\n");
        
        for (String followingId : followingIds) {
            User followedUser = system.getUserById(followingId);
            if (followedUser != null) {
                listText.append("• ").append(followedUser.getFullName())
                        .append(" (@").append(followedUser.getUsername()).append(")\n");
            }
        }
        
        JTextArea textArea = new JTextArea(listText.toString());
        textArea.setEditable(false);
        textArea.setFont(Theme.FONT_BODY);
        textArea.setRows(10);
        textArea.setColumns(30);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Following", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showVerificationRequestsDialog() {
        List<VerificationRequest> requests = system.getPendingVerificationRequests();
        
        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No pending verification requests.",
                "Verification Requests",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create dialog with list of requests
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Pending Verification Requests (" + requests.size() + ")");
        titleLabel.setFont(Theme.FONT_HEADING);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        
        for (VerificationRequest request : requests) {
            User user = system.getUserById(request.getUserId());
            if (user != null) {
                JPanel requestCard = createVerificationRequestCard(user);
                panel.add(requestCard);
                panel.add(Box.createVerticalStrut(10));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Verification Requests", JOptionPane.PLAIN_MESSAGE);
    }
    
    private JPanel createVerificationRequestCard(User user) {
        RoundedPanel card = new RoundedPanel(12, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel(user.getFullName() + " (@" + user.getUsername() + ")");
        nameLabel.setFont(Theme.FONT_SUBHEADING);
        
        JLabel bioLabel = new JLabel(user.getBio().isEmpty() ? "No bio" : user.getBio());
        bioLabel.setFont(Theme.FONT_SMALL);
        bioLabel.setForeground(Theme.TEXT_MUTED);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(nameLabel);
        infoPanel.add(bioLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        
        ModernButton verifyButton = new ModernButton("Verify", ModernButton.STYLE_PRIMARY);
        verifyButton.setPreferredSize(new Dimension(80, 30));
        verifyButton.addActionListener(e -> {
            if (system.verifyUser(user.getId())) {
                JOptionPane.showMessageDialog(this, "User verified!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showVerificationRequestsDialog();
            }
        });
        
        ModernButton rejectButton = new ModernButton("Reject", ModernButton.STYLE_SECONDARY);
        rejectButton.setPreferredSize(new Dimension(80, 30));
        rejectButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Reject verification request from " + user.getFullName() + "?",
                "Reject Request", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (system.rejectVerificationRequest(user.getId())) {
                    JOptionPane.showMessageDialog(this, "Request rejected.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
                    showVerificationRequestsDialog();
                }
            }
        });
        
        buttonPanel.add(verifyButton);
        buttonPanel.add(rejectButton);
        
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);
        
        return card;
    }
}