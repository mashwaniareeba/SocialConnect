package gui.panels;

import gui.Theme;
import system.SocialNetworkSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainAppPanel - Main application layout with sidebar navigation
 * Contains Feed, Profile, Search, and Create Post panels
 */
public class MainAppPanel extends JPanel {
    private SocialNetworkSystem system;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private FeedPanel feedPanel;
    private ProfilePanel profilePanel;
    private SearchPanel searchPanel;
    private CreatePostPanel createPostPanel;
    private Runnable onLogout;

    // Sidebar buttons for state management
    private JButton activeSidebarButton;
    private JButton feedButton;
    private JButton profileButton;
    private JButton searchButton;
    private JButton createPostButton;

    public MainAppPanel() {
        system = SocialNetworkSystem.getInstance();
        setLayout(new BorderLayout());
        setOpaque(true);
        initComponents();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(
            0, 0, Theme.GRADIENT_START,
            0, getHeight(), Theme.GRADIENT_END
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    private void initComponents() {
        // Create sidebar
        sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Create content area with CardLayout (with gradient background)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, Theme.GRADIENT_START,
                    0, getHeight(), Theme.GRADIENT_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        contentPanel.setOpaque(true);

        // Create all panels
        profilePanel = new ProfilePanel();
        feedPanel = new FeedPanel();
        searchPanel = new SearchPanel(profilePanel);
        createPostPanel = new CreatePostPanel();

        // Set callback for post creation
        createPostPanel.setOnPostCreated(() -> {
            feedPanel.refresh();
            feedPanel.scrollToTop();
            showPanel("feed");
        });

        // Add panels to content
        contentPanel.add(feedPanel, "feed");
        contentPanel.add(profilePanel, "profile");
        contentPanel.add(searchPanel, "search");
        contentPanel.add(createPostPanel, "create");

        add(contentPanel, BorderLayout.CENTER);

        // Show feed by default
        showPanel("feed");
        setActiveSidebarButton(feedButton);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, Theme.GRADIENT_START,
                    0, getHeight(), Theme.GRADIENT_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        sidebar.setOpaque(true);
        sidebar.setLayout(new BorderLayout());

        // Logo section - Enhanced to stand out
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JLabel logoIcon = new JLabel("🌐");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        logoIcon.setForeground(Theme.SIDEBAR_TEXT);
        logoIcon.setVerticalAlignment(SwingConstants.CENTER);
        logoIcon.setHorizontalAlignment(SwingConstants.CENTER);

        logoPanel.add(logoIcon);

        // Navigation menu
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        feedButton = createNavButton("📄", "Feed");
        feedButton.addActionListener(e -> {
            showPanel("feed");
            setActiveSidebarButton(feedButton);
        });

        profileButton = createNavButton("👤", "Profile");
        profileButton.addActionListener(e -> {
            profilePanel.loadCurrentUserProfile();
            showPanel("profile");
            setActiveSidebarButton(profileButton);
        });

        searchButton = createNavButton("🔍", "Search");
        searchButton.addActionListener(e -> {
            showPanel("search");
            setActiveSidebarButton(searchButton);
        });

        createPostButton = createNavButton("✎", "Create Post");
        createPostButton.addActionListener(e -> {
            createPostPanel.refreshUserInfo(); // Refresh user name display
            showPanel("create");
            setActiveSidebarButton(createPostButton);
        });

        navPanel.add(feedButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(profileButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(searchButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(createPostButton);

        // Bottom section with user info and logout
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 25, 15));

        // User info
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userInfoPanel.setOpaque(false);
        userInfoPanel.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH - 30, 50));

        JPanel userTextPanel = new JPanel();
        userTextPanel.setLayout(new BoxLayout(userTextPanel, BoxLayout.Y_AXIS));
        userTextPanel.setOpaque(false);

        String userName = system.getCurrentUser() != null ? 
                         system.getCurrentUser().getFullName() : "Guest";
        String userHandle = system.getCurrentUser() != null ? 
                           "@" + system.getCurrentUser().getUsername() : "";

        JLabel userNameLabel = new JLabel(userName);
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userNameLabel.setForeground(Theme.SIDEBAR_TEXT);

        JLabel userHandleLabel = new JLabel(userHandle);
        userHandleLabel.setFont(Theme.FONT_SMALL);
        userHandleLabel.setForeground(Theme.SIDEBAR_TEXT);

        userTextPanel.add(userNameLabel);
        userTextPanel.add(userHandleLabel);

        userInfoPanel.add(userTextPanel);

        // Divider
        JSeparator divider = new JSeparator();
        divider.setForeground(Theme.SIDEBAR_HOVER);
        divider.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH - 30, 1));

        // Logout button
        JButton logoutButton = createNavButton("🚶", "Logout");
        logoutButton.setForeground(Theme.ACCENT_RED);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                system.logout();
                if (onLogout != null) {
                    onLogout.run();
                }
            }
        });

        bottomPanel.add(userInfoPanel);
        bottomPanel.add(Box.createVerticalStrut(15));
        bottomPanel.add(divider);
        bottomPanel.add(Box.createVerticalStrut(15));
        bottomPanel.add(logoutButton);

        // Assemble sidebar
        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String icon, String text) {
        // Use HTML with emoji font for icons
        JButton button = new JButton("<html><span style='font-family:Segoe UI Emoji;font-size:18px;'>" + icon + "</span>  <span style='font-size:15px;'>" + text + "</span></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setForeground(Theme.SIDEBAR_TEXT);
        button.setBackground(Theme.SIDEBAR_BUTTON);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH - 30, 50));
        button.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH - 30, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add border with padding for pop-up effect
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.SIDEBAR_HOVER, 1, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeSidebarButton) {
                    button.setBackground(Theme.SIDEBAR_HOVER);
                    // Enhanced border on hover for pop-up effect
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.SIDEBAR_ACTIVE, 2, true),
                        BorderFactory.createEmptyBorder(11, 17, 11, 17)
                    ));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeSidebarButton) {
                    button.setBackground(Theme.SIDEBAR_BUTTON);
                    // Reset border
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.SIDEBAR_HOVER, 1, true),
                        BorderFactory.createEmptyBorder(12, 18, 12, 18)
                    ));
                }
            }
        });

        return button;
    }

    private void setActiveSidebarButton(JButton button) {
        // Reset previous active button
        if (activeSidebarButton != null) {
            activeSidebarButton.setBackground(Theme.SIDEBAR_BUTTON);
            activeSidebarButton.setForeground(Theme.SIDEBAR_TEXT);
            activeSidebarButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.SIDEBAR_HOVER, 1, true),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));
        }

        // Set new active button
        activeSidebarButton = button;
        if (button != null) {
            button.setBackground(Theme.SIDEBAR_ACTIVE);
            button.setForeground(Theme.SIDEBAR_TEXT);
            // Enhanced border for active state - pop-up effect
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.SIDEBAR_TEXT, 2, true),
                BorderFactory.createEmptyBorder(11, 17, 11, 17)
            ));
        }
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        
        // Refresh panel content when shown and scroll to top
        switch (panelName) {
            case "feed":
                feedPanel.refresh();
                feedPanel.scrollToTop();
                break;
            case "profile":
                // Only load current user profile if no specific user is being displayed
                // Otherwise, keep the displayed user's profile
                if (profilePanel.getDisplayedUser() == null) {
                    profilePanel.loadCurrentUserProfile();
                } else {
                    profilePanel.refreshProfile();
                }
                profilePanel.scrollToTop();
                break;
            case "search":
                searchPanel.refresh();
                searchPanel.scrollToTop();
                break;
            case "create":
                createPostPanel.refreshUserInfo(); // Refresh user name when showing create post panel
                break;
        }
    }

    public void setOnLogout(Runnable callback) {
        this.onLogout = callback;
    }

    public void refreshAll() {
        feedPanel.refresh();
        profilePanel.loadCurrentUserProfile();
        searchPanel.refresh();
        
        // Update user info in sidebar
        if (system.getCurrentUser() != null) {
            // Rebuild sidebar to update user info
            remove(sidebarPanel);
            sidebarPanel = createSidebar();
            add(sidebarPanel, BorderLayout.WEST);
            revalidate();
            repaint();
        }
    }
}




