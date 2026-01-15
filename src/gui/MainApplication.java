package gui;

import gui.panels.*;
import system.SocialNetworkSystem;

import javax.swing.*;
import java.awt.*;

/**
 * MainApplication - Main frame and application entry point
 * Manages navigation between Login, Register, and Main App screens
 */
public class MainApplication extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegistrationPanel registrationPanel;
    private MainAppPanel mainAppPanel;
    private SocialNetworkSystem system;

    public MainApplication() {
        system = SocialNetworkSystem.getInstance();
        initFrame();
        initComponents();
        setupCallbacks();
    }

    private void initFrame() {
        setTitle("SocialConnect - Connect with the World");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null); // Center on screen
        
        // Add window listener to save data on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                system.forceSave();
                System.exit(0);
            }
        });
        
        // Add shutdown hook as backup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            system.forceSave();
        }));
        
      
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout) {
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
        mainPanel.setOpaque(true);

        // Create all panels
        loginPanel = new LoginPanel();
        registrationPanel = new RegistrationPanel();
        mainAppPanel = new MainAppPanel();

        // Add panels to card layout
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registrationPanel, "register");
        mainPanel.add(mainAppPanel, "main");

        add(mainPanel);

        // Show login panel by default
        showLogin();
    }

    private void setupCallbacks() {
        // Login panel callbacks
        loginPanel.setOnLoginSuccess(() -> {
            mainAppPanel.refreshAll();
            showMainApp();
        });

        loginPanel.setOnSwitchToRegister(this::showRegister);

        // Registration panel callbacks
        registrationPanel.setOnRegistrationSuccess(this::showLogin);
        registrationPanel.setOnSwitchToLogin(this::showLogin);

        // Main app callbacks
        mainAppPanel.setOnLogout(() -> {
            loginPanel.clearFields();
            showLogin();
        });
    }

    public void showLogin() {
        cardLayout.show(mainPanel, "login");
        setTitle("SocialConnect - Login");
    }

    public void showRegister() {
        cardLayout.show(mainPanel, "register");
        setTitle("SocialConnect - Create Account");
    }

    public void showMainApp() {
        cardLayout.show(mainPanel, "main");
        String userName = system.getCurrentUser() != null ? 
                         system.getCurrentUser().getFullName() : "User";
        setTitle("SocialConnect - Welcome, " + userName);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Custom UI defaults for better appearance
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("ScrollBar.track", Theme.BACKGROUND);
            UIManager.put("ScrollBar.thumb", Theme.BORDER_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}




