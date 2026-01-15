package gui.panels;

import gui.Theme;
import gui.components.*;
import models.*;
import system.SocialNetworkSystem;

import javax.swing.*;
import java.awt.*;

/**
 * CreatePostPanel - Create new posts (text or image)
 * Features tabbed interface for post type selection
 */
public class CreatePostPanel extends JPanel {
    private SocialNetworkSystem system;
    private JTabbedPane tabbedPane;
    private JTextArea textPostArea;
    private ModernTextField imagePathField;
    private JTextArea captionArea;
    private Runnable onPostCreated;

    public CreatePostPanel() {
        system = SocialNetworkSystem.getInstance();
        setLayout(new BorderLayout());
        setOpaque(false); // Transparent to show gradient background
        initComponents();
    }
    
    public void refreshUserInfo() {
        // Refresh user name display when user logs in for both tabs
        if (tabbedPane != null) {
            JPanel textPostPanel = createTextPostTab();
            tabbedPane.setComponentAt(0, textPostPanel);
            
            JPanel imagePostPanel = createImagePostTab();
            tabbedPane.setComponentAt(1, imagePostPanel);
        }
    }

    private void initComponents() {
        JPanel mainWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mainWrapper.setOpaque(false); // Transparent to show gradient background
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        RoundedPanel card = new RoundedPanel(16, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(570, 470));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("<html><span style='font-family:Segoe UI Emoji;font-size:20px;'>✎</span> Create Post</html>");
        titleLabel.setFont(Theme.FONT_HEADING);
        titleLabel.setForeground(Theme.TEXT_PRIMARY); // Dark text for light card

        JLabel subtitleLabel = new JLabel("Share your thoughts with the world");
        subtitleLabel.setFont(Theme.FONT_SMALL);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY); // Medium gray for light card

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Tabbed pane for post types
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_BODY);
        tabbedPane.setBackground(Theme.CARD_BACKGROUND); // Light card background

        // Text Post Tab
        JPanel textPostPanel = createTextPostTab();
        tabbedPane.addTab("<html><span style='font-family:Segoe UI Emoji;'>✎</span> Text Post</html>", textPostPanel);

        // Image Post Tab
        JPanel imagePostPanel = createImagePostTab();
        tabbedPane.addTab("Image Post", imagePostPanel);

        // Assemble card
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        card.add(contentPanel);
        mainWrapper.add(card);
        add(mainWrapper, BorderLayout.CENTER);
    }

    private JPanel createTextPostTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(true);
        panel.setBackground(new Color(0xAB, 0xE7, 0xB2)); // #ABE7B2 - Medium green
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        User currentUser = system.getCurrentUser();
        String userName = currentUser != null ? currentUser.getFullName() : "Guest";
        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(Theme.FONT_SUBHEADING);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        userPanel.add(avatarLabel);
        userPanel.add(nameLabel);

        // Text area
        textPostArea = new JTextArea();
        textPostArea.setFont(Theme.FONT_BODY);
        textPostArea.setLineWrap(true);
        textPostArea.setWrapStyleWord(true);
        textPostArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        textPostArea.setRows(6);

        // Placeholder behavior
        textPostArea.setText("What's on your mind?");
        textPostArea.setForeground(Theme.TEXT_MUTED);
        textPostArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textPostArea.getText().equals("What's on your mind?")) {
                    textPostArea.setText("");
                    textPostArea.setForeground(Theme.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textPostArea.getText().isEmpty()) {
                    textPostArea.setText("What's on your mind?");
                    textPostArea.setForeground(Theme.TEXT_MUTED);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(textPostArea);
        scrollPane.setBorder(null);

        // Post button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton postButton = new ModernButton(" Post", ModernButton.STYLE_PRIMARY);
        postButton.setPreferredSize(new Dimension(120, 40));
        postButton.addActionListener(e -> createTextPost());

        buttonPanel.add(postButton);

        panel.add(userPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createImagePostTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(true);
        panel.setBackground(new Color(0xAB, 0xE7, 0xB2)); // #ABE7B2 - Medium green
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);

        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        User currentUser = system.getCurrentUser();
        String userName = currentUser != null ? currentUser.getFullName() : "Guest";
        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(Theme.FONT_SUBHEADING);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        userPanel.add(avatarLabel);
        userPanel.add(nameLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Image path
        JLabel imageLabel = new JLabel("Image Path:");
        imageLabel.setFont(Theme.FONT_BODY);
        imageLabel.setForeground(Theme.TEXT_PRIMARY);
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pathPanel = new JPanel(new BorderLayout(10, 0));
        pathPanel.setOpaque(false);
        pathPanel.setMaximumSize(new Dimension(500, 44));
        pathPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        imagePathField = new ModernTextField("Enter image path or URL...");
        
        ModernButton browseButton = new ModernButton("Browse", ModernButton.STYLE_GHOST);
        browseButton.setPreferredSize(new Dimension(80, 40));
        browseButton.addActionListener(e -> browseImage());

        pathPanel.add(imagePathField, BorderLayout.CENTER);
        pathPanel.add(browseButton, BorderLayout.EAST);

        // Caption
        JLabel captionLabel = new JLabel("Caption:");
        captionLabel.setFont(Theme.FONT_BODY);
        captionLabel.setForeground(Theme.TEXT_PRIMARY);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        captionArea = new JTextArea();
        captionArea.setFont(Theme.FONT_BODY);
        captionArea.setLineWrap(true);
        captionArea.setWrapStyleWord(true);
        captionArea.setRows(4);
        captionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JScrollPane captionScroll = new JScrollPane(captionArea);
        captionScroll.setBorder(null);
        captionScroll.setMaximumSize(new Dimension(500, 120));
        captionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(imageLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(pathPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(captionLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(captionScroll);

        // Post button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton postButton = new ModernButton(" Post Image", ModernButton.STYLE_PRIMARY);
        postButton.setPreferredSize(new Dimension(120, 40));
        postButton.addActionListener(e -> createImagePost());

        buttonPanel.add(postButton);

        panel.add(userPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void createTextPost() {
        String content = textPostArea.getText().trim();
        
        if (content.isEmpty() || content.equals("What's on your mind?")) {
            JOptionPane.showMessageDialog(this,
                "Please write something to post!",
                "Empty Post",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        TextPost post = system.createTextPost(content);
        
        if (post != null) {
            JOptionPane.showMessageDialog(this,
                "Post created successfully! 🎉",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            clearTextPostForm();
            if (onPostCreated != null) {
                onPostCreated.run();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to create post. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createImagePost() {
        String imagePath = imagePathField.getText().trim();
        String caption = captionArea.getText().trim();

        if (imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter an image path!",
                "Missing Image",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        ImagePost post = system.createImagePost(imagePath, caption);

        if (post != null) {
            JOptionPane.showMessageDialog(this,
                "Image post created successfully! 🎉",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            clearImagePostForm();
            if (onPostCreated != null) {
                onPostCreated.run();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to create post. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif", "bmp"
        ));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            imagePathField.setActualText(path);
        }
    }

    private void clearTextPostForm() {
        textPostArea.setText("What's on your mind?");
        textPostArea.setForeground(Theme.TEXT_MUTED);
    }

    private void clearImagePostForm() {
        imagePathField.clearField();
        captionArea.setText("");
    }

    public void setOnPostCreated(Runnable callback) {
        this.onPostCreated = callback;
    }

   
}




