package gui.panels;

import gui.Theme;
import gui.components.*;
import models.*;
import system.SocialNetworkSystem;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * SearchPanel - Search for users with modern design
 */
public class SearchPanel extends JPanel {
    private SocialNetworkSystem system;
    private ModernTextField searchField;
    private JPanel resultsPanel;
    private ProfilePanel profilePanel;
    private JScrollPane scrollPane;
    private JPanel adminButtonPanel;
    private JPanel adminSection;

    public SearchPanel(ProfilePanel profilePanel) {
        this.profilePanel = profilePanel;
        system = SocialNetworkSystem.getInstance();
        setLayout(new BorderLayout());
        setOpaque(false); // Transparent to show gradient background
        initComponents();
    }

    private void initComponents() {
        // Search header
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(Theme.CARD_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.DIVIDER_COLOR),
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));

        JLabel titleLabel = new JLabel("<html><span style='font-family:Segoe UI Emoji;font-size:20px;'>🔍</span> Search Users</html>");
        titleLabel.setFont(Theme.FONT_HEADING);
        titleLabel.setForeground(Theme.TEXT_PRIMARY); // Dark text for light card

        searchField = new ModernTextField("Search by name or username...");
        searchField.setPreferredSize(new Dimension(400, 44));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        ModernButton searchButton = new ModernButton("Search", ModernButton.STYLE_PRIMARY);
        searchButton.setPreferredSize(new Dimension(100, 44));
        searchButton.addActionListener(e -> performSearch());

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField);
        searchRow.add(searchButton);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        headerPanel.add(searchRow, BorderLayout.SOUTH);

        // Results panel
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false); // Transparent to show gradient background

        scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false); // Transparent
        scrollPane.getViewport().setOpaque(false); // Transparent
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Admin section for verification requests
        adminSection = new JPanel();
        adminSection.setLayout(new BorderLayout());
        adminSection.setOpaque(false); // Transparent to show gradient background
        
        adminButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        adminButtonPanel.setOpaque(false);
        adminButtonPanel.setBorder(BorderFactory.createEmptyBorder(15, 35, 15, 35));
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(headerPanel, BorderLayout.NORTH);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        
        adminSection.add(adminButtonPanel, BorderLayout.NORTH);
        adminSection.add(mainContent, BorderLayout.CENTER);
        
        add(adminSection, BorderLayout.CENTER);
        
        // Refresh admin section
        refreshAdminSection();

        // Show all users initially
        showAllUsers();
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        
        resultsPanel.removeAll();
        resultsPanel.add(Box.createVerticalStrut(20));

        List<User> results;
        if (query.isEmpty()) {
            results = system.getAllUsers();
        } else {
            results = system.searchUsers(query);
        }

        if (results.isEmpty()) {
            showNoResults();
        } else {
            JPanel headerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            headerWrapper.setOpaque(false);
            
            JLabel resultCount = new JLabel(results.size() + " user(s) found");
            resultCount.setFont(Theme.FONT_BODY);
            resultCount.setForeground(Theme.TEXT_PRIMARY); // Dark text for light background
            headerWrapper.add(resultCount);
            resultsPanel.add(headerWrapper);
            resultsPanel.add(Box.createVerticalStrut(15));

            for (User user : results) {
                JPanel userCard = createUserCard(user);
                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                wrapper.setOpaque(false);
                wrapper.add(userCard);
                resultsPanel.add(wrapper);
                resultsPanel.add(Box.createVerticalStrut(15));
            }
        }

        resultsPanel.add(Box.createVerticalStrut(50));
        resultsPanel.revalidate();
        resultsPanel.repaint();
        
        // Scroll to top after search
        SwingUtilities.invokeLater(() -> scrollToTop());
    }
    
    public void scrollToTop() {
        if (scrollPane != null) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMinimum());
            });
        }
    }

    private void showAllUsers() {
        performSearch(); // With empty query shows all
    }

    private void showNoResults() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        JLabel iconLabel = new JLabel("🔍");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel("No users found");
        textLabel.setFont(Theme.FONT_HEADING);
        textLabel.setForeground(Theme.TEXT_PRIMARY); // Dark text for light background
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTextLabel = new JLabel("Try a different search term");
        subTextLabel.setFont(Theme.FONT_BODY);
        subTextLabel.setForeground(Theme.TEXT_SECONDARY); // Medium gray for light background
        subTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(textLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subTextLabel);

        resultsPanel.add(panel);
    }

    private JPanel createUserCard(User user) {
        RoundedPanel card = new RoundedPanel(16, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BorderLayout(15, 0));
        card.setPreferredSize(new Dimension(520, 110));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Avatar with profile photo
        JLabel avatarLabel = createAvatar(user, 50);
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Name with badge
        String displayName = user.getFullName();
        String badgeHtml = "";
        if (user instanceof RegularUser && ((RegularUser) user).isVerified()) {
            String verifiedColor = String.format("#%06X", Theme.VERIFIED_BLUE.getRGB() & 0xFFFFFF);
            badgeHtml = " <span style='color:" + verifiedColor + "; font-size:18px; font-weight:bold;'>✓</span>";
        }
        if (user instanceof Admin) {
            badgeHtml += " <span style='font-size:16px;'>🛡</span>";
        }
        JLabel nameLabel = new JLabel("<html>" + displayName + badgeHtml + "</html>");
        nameLabel.setFont(Theme.FONT_SUBHEADING);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel usernameLabel = new JLabel("@" + user.getUsername());
        usernameLabel.setFont(Theme.FONT_BODY);
        usernameLabel.setForeground(Theme.TEXT_SECONDARY);

        String bio = user.getBio();
        if (bio.length() > 50) {
            bio = bio.substring(0, 50) + "...";
        }
        JLabel bioLabel = new JLabel(bio.isEmpty() ? "No bio" : bio);
        bioLabel.setFont(Theme.FONT_SMALL);
        bioLabel.setForeground(Theme.TEXT_MUTED);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(usernameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(bioLabel);

        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        statsPanel.add(createMiniStat(String.valueOf(user.getPostCount()), "posts"));
        statsPanel.add(createMiniStat(String.valueOf(user.getFollowerCount()), "followers"));

        // Action button
        User currentUser = system.getCurrentUser();
        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            boolean isFollowing = system.isFollowingUser(user.getId());
            boolean hasSentRequest = system.hasSentFollowRequest(user.getId());
            boolean isTargetPrivate = (user instanceof RegularUser) && 
                                     ((RegularUser) user).isPrivateAccount();
            
            // Determine button text and style
            String buttonText;
            int buttonStyle;
            
            if (isFollowing) {
                buttonText = "Following";
                buttonStyle = ModernButton.STYLE_SECONDARY;
            } else if (hasSentRequest && isTargetPrivate) {
                buttonText = "Requested";
                buttonStyle = ModernButton.STYLE_SECONDARY;
            } else {
                buttonText = "Follow";
                buttonStyle = ModernButton.STYLE_PRIMARY;
            }
            
            ModernButton followBtn = new ModernButton(buttonText, buttonStyle);
            followBtn.setPreferredSize(new Dimension(90, 32));
            followBtn.addActionListener(e -> {
                system.followUser(user.getId());
                performSearch(); // Refresh to update button text
            });
            statsPanel.add(followBtn);
        }

        card.add(avatarLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(statsPanel, BorderLayout.EAST);

        // Click to view profile
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (profilePanel != null) {
                    profilePanel.loadUserProfile(user);
                    // Find parent and switch to profile panel
                    Container parent = getParent();
                    while (parent != null && !(parent instanceof MainAppPanel)) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof MainAppPanel) {
                        ((MainAppPanel) parent).showPanel("profile");
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackgroundColor(new Color(0xFF, 0xF5, 0xF0)); // Light peach/orange tint on hover (contrasts with light card)
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackgroundColor(Theme.CARD_BACKGROUND);
            }
        });

        return card;
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
                    BufferedImage img = ImageIO.read(imageFile);
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
    
    private JPanel createMiniStat(String value, String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(Theme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        textLabel.setForeground(Theme.TEXT_MUTED);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(valueLabel);
        panel.add(textLabel);

        return panel;
    }

    public void refresh() {
        refreshAdminSection();
        performSearch();
        scrollToTop();
    }
    
    private void refreshAdminSection() {
        adminButtonPanel.removeAll();
        adminButtonPanel.revalidate();
        adminButtonPanel.repaint();
    }
}




