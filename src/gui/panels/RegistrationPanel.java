package gui.panels;

import gui.Theme;
import gui.components.*;
import models.User;
import system.SocialNetworkSystem;

import javax.swing.*;
import java.awt.*;

/**
 * RegistrationPanel - User registration with form validation
 */
public class RegistrationPanel extends JPanel {
    private ModernTextField usernameField;
    private ModernTextField fullNameField;
    private ModernTextField emailField;
    private ModernTextField ageField;
    private ModernPasswordField passwordField;
    private ModernPasswordField confirmPasswordField;
    private ModernButton registerButton;
    private ModernButton backToLoginButton;
    private JLabel errorLabel;
    private Runnable onRegistrationSuccess;
    private Runnable onSwitchToLogin;

    public RegistrationPanel() {
        setLayout(new GridBagLayout());
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
        // Main container card
        RoundedPanel card = new RoundedPanel(20, Theme.CARD_BACKGROUND, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(450, 620));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Join SocialConnect today");
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        // Form section
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Full Name field
        fullNameField = new ModernTextField("Full Name");
        fullNameField.setMaximumSize(new Dimension(350, 44));
        fullNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        usernameField = new ModernTextField("Username");
        usernameField.setMaximumSize(new Dimension(350, 44));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email field
        emailField = new ModernTextField("Email");
        emailField.setMaximumSize(new Dimension(350, 44));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Age field
        ageField = new ModernTextField("Age");
        ageField.setMaximumSize(new Dimension(350, 44));
        ageField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password field
        passwordField = new ModernPasswordField("Password");
        passwordField.setMaximumSize(new Dimension(350, 44));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Confirm Password field
        confirmPasswordField = new ModernPasswordField("Confirm Password");
        confirmPasswordField.setMaximumSize(new Dimension(350, 44));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.ACCENT_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Register button
        registerButton = new ModernButton("Sign Up", ModernButton.STYLE_SUCCESS);
        registerButton.setMaximumSize(new Dimension(350, 44));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegistration());

        // Back to login button
        backToLoginButton = new ModernButton("Back to Login", ModernButton.STYLE_GHOST);
        backToLoginButton.setMaximumSize(new Dimension(350, 44));
        backToLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToLoginButton.addActionListener(e -> {
            if (onSwitchToLogin != null) {
                onSwitchToLogin.run();
            }
        });

        // Add fields to form
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(fullNameField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(ageField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(backToLoginButton);

        // Terms text
        JLabel termsLabel = new JLabel("<html><center>By signing up, you agree to our<br>Terms of Service and Privacy Policy</center></html>");
        termsLabel.setFont(Theme.FONT_SMALL);
        termsLabel.setForeground(Theme.TEXT_MUTED);
        termsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add all to card
        card.add(Box.createVerticalStrut(25));
        card.add(headerPanel);
        card.add(Box.createVerticalStrut(25));
        card.add(formPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(termsLabel);
        card.add(Box.createVerticalStrut(20));

        add(card);
    }

    private void handleRegistration() {
        // Get values
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String ageStr = ageField.getText().trim();
        String password = passwordField.getActualPassword();
        String confirmPassword = confirmPasswordField.getActualPassword();

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            ageStr.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (username.contains(" ")) {
            showError("Username cannot contain spaces");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }

        // Email validation: alphanumeric before @, must be @gmail.com
        if (!validateEmail(email)) {
            showError("Invalid email address");
            return;
        }

        // Check if email already exists
        SocialNetworkSystem system = SocialNetworkSystem.getInstance();
        if (system.isEmailTaken(email)) {
            showError("Email already exists. Please use a different email.");
            return;
        }
        
        // Check if username already exists (before registration attempt)
        if (system.getUserByUsername(username) != null) {
            showError("Username already exists. Please choose a different username.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 13 || age > 120) {
                showError("Age must be between 13 and 120");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid age");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Register user (should succeed since we've already validated username and email)
        User user = system.registerUser(username, password, fullName, email, age, false);

        if (user != null) {
            clearFields();
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\nYou can now log in.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            if (onRegistrationSuccess != null) {
                onRegistrationSuccess.run();
            }
        } else {
            showError("Registration failed. Username or email may already be taken.");
        }
    }

    private boolean validateEmail(String email) {
        // Email must be in format: alphanumeric@gmail.com
        if (!email.endsWith("@gmail.com")) {
            return false;
        }
        
        // Get the part before @gmail.com
        String localPart = email.substring(0, email.indexOf("@"));
        
        // Check if local part is not empty and is alphanumeric
        if (localPart.isEmpty()) {
            return false;
        }
        
        // Check if local part contains only alphanumeric characters
        for (char c : localPart.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }

    public void clearFields() {
        fullNameField.clearField();
        usernameField.clearField();
        emailField.clearField();
        ageField.clearField();
        passwordField.clearField();
        confirmPasswordField.clearField();
        errorLabel.setText(" ");
    }

    public void setOnRegistrationSuccess(Runnable callback) {
        this.onRegistrationSuccess = callback;
    }

    public void setOnSwitchToLogin(Runnable callback) {
        this.onSwitchToLogin = callback;
    }
}




