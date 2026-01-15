package gui.panels;

import gui.Theme;
import gui.components.*;
import models.User;
import system.SocialNetworkSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginPanel - Beautiful login screen with modern design
 */
public class LoginPanel extends JPanel {
    private ModernTextField usernameField;
    private ModernPasswordField passwordField;
    private ModernButton loginButton;
    private ModernButton registerButton;
    private JLabel errorLabel;
    private Runnable onLoginSuccess;
    private Runnable onSwitchToRegister;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        initComponents();
        // Paint gradient background
        setOpaque(true);
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
        // Main container card
        RoundedPanel card = new RoundedPanel(20, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 480));
        card.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 30));

        // Logo section - Enhanced to stand out
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0)); // Top padding to make logo visible

        // Logo icon on top
        JPanel logoIconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoIconPanel.setOpaque(false);
        logoIconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoIconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel logoIcon = new JLabel("🌐");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        logoIcon.setVerticalAlignment(SwingConstants.CENTER);
        logoIcon.setHorizontalAlignment(SwingConstants.CENTER);

        logoIconPanel.add(logoIcon);

        // Tagline below logo
        JPanel taglinePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        taglinePanel.setOpaque(false);
        taglinePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglinePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel tagline = new JLabel("SocialConnect");
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 28));
        tagline.setForeground(Color.BLACK);
        tagline.setVerticalAlignment(SwingConstants.CENTER);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        taglinePanel.add(tagline);

        // Description below tagline
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel description = new JLabel("Connect with friends and the world");
        description.setFont(Theme.FONT_BODY);
        description.setForeground(Theme.TEXT_SECONDARY);
        bottomRow.add(description);

        logoPanel.add(logoIconPanel);
        logoPanel.add(Box.createVerticalStrut(8));
        logoPanel.add(taglinePanel);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(bottomRow);

        // Form section
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        usernameField = new ModernTextField("Username");
        usernameField.setMaximumSize(new Dimension(320, 44));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password field
        passwordField = new ModernPasswordField("Password");
        passwordField.setMaximumSize(new Dimension(320, 44));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.ACCENT_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        loginButton = new ModernButton("Log In", ModernButton.STYLE_PRIMARY);
        loginButton.setMaximumSize(new Dimension(320, 44));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());
        loginButton.setEnabled(true);

        // Divider
        JPanel dividerPanel = createDivider();
        dividerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Register button
        registerButton = new ModernButton("Create New Account", ModernButton.STYLE_SUCCESS);
        registerButton.setMaximumSize(new Dimension(320, 44));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> {
            if (onSwitchToRegister != null) {
                onSwitchToRegister.run();
            }
        });

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(dividerPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerButton);

        // Add all to card
        card.add(Box.createVerticalStrut(30));
        card.add(logoPanel);
        card.add(Box.createVerticalStrut(30));
        card.add(formPanel);
        card.add(Box.createVerticalStrut(20));

        card.add(Box.createVerticalStrut(20));

        // Add card to main panel
        add(card);

        // Enter key listener
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });

        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    }

    private JPanel createDivider() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(320, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JSeparator leftLine = new JSeparator();
        leftLine.setForeground(Theme.BORDER_COLOR);

        JLabel orLabel = new JLabel("  OR  ");
        orLabel.setFont(Theme.FONT_SMALL);
        orLabel.setForeground(Theme.TEXT_MUTED);

        JSeparator rightLine = new JSeparator();
        rightLine.setForeground(Theme.BORDER_COLOR);

        gbc.gridx = 0;
        panel.add(leftLine, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        panel.add(orLabel, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1;
        panel.add(rightLine, gbc);

        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getActualPassword();

        if (username.isEmpty() || password == null || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        SocialNetworkSystem system = SocialNetworkSystem.getInstance();
        User user = system.login(username, password);

        if (user != null) {
            errorLabel.setText(" ");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            // Check if login failed due to ban (safely)
            boolean isBanned = false;
            try {
                isBanned = system.isLoginFailedDueToBan(username);
            } catch (Exception e) {
                // Ignore ban check errors
            }
            
            if (isBanned) {
                showError("Your account has been banned. Please contact an administrator.");
            } else {
                showError("Invalid username or password");
            }
            shakeCard();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void shakeCard() {
        // Simple shake animation effect
        Timer timer = new Timer(50, null);
        final int[] count = {0};
        final int[] direction = {1};
        Point originalLocation = getLocation();

        timer.addActionListener(e -> {
            if (count[0] < 6) {
                setLocation(originalLocation.x + (5 * direction[0]), originalLocation.y);
                direction[0] *= -1;
                count[0]++;
            } else {
                setLocation(originalLocation);
                timer.stop();
            }
        });
        timer.start();
    }

    public void clearFields() {
        usernameField.clearField();
        passwordField.clearField();
        errorLabel.setText(" ");
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    public void setOnSwitchToRegister(Runnable callback) {
        this.onSwitchToRegister = callback;
    }
}




