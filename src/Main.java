import gui.MainApplication;
import javax.swing.SwingUtilities;

/**
 * Main - Application entry point
 * Launches the SocialConnect application
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗"); 
        System.out.println("║     🌐 SocialConnect v1.0               ║ ");
        System.out.println("║     Mini Social Network System           ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Starting application...");
        System.out.println();
        System.out.println("Demo Credentials:");
        System.out.println("  Username: ali");
        System.out.println("  Password: password123");
        System.out.println();
        System.out.println("  Or create a new account!");
        System.out.println();

        // Launch the GUI application
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}

