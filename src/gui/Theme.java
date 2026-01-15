package gui;

import java.awt.*;


public class Theme {
    // Primary Colors - Orange to Magenta Gradient Theme
    public static final Color PRIMARY_BLUE = new Color(0xFF, 0x6B, 0x35); // #FF6B35 - Vibrant orange (buttons)
    public static final Color SECONDARY_BLUE = new Color(0xFF, 0x8C, 0x69); // #FF8C69 - Lighter orange
    public static final Color LIGHT_BLUE = new Color(0xFF, 0x8C, 0x69); // #FF8C69 - Lighter orange
    public static final Color GRADIENT_START = new Color(0xFF, 0x6B, 0x35); // #FF6B35 - Orange (gradient start)
    public static final Color GRADIENT_END = new Color(0xE9, 0x1E, 0x63); // #E91E63 - Magenta/pink (gradient end)
    
    // Accent Colors 
    public static final Color ACCENT_RED = new Color(0xDC, 0x14, 0x3C); // Bright red for errors/logout
    public static final Color LIKE_RED = new Color(0xFF, 0x6B, 0x7A); // Soft rose
    public static final Color SUCCESS_GREEN = new Color(0x4C, 0xAF, 0x50); // Green for success
    public static final Color WARNING_ORANGE = new Color(0xFF, 0x98, 0x00); // Orange for warnings
    public static final Color INFO_CYAN = new Color(0x00, 0xBC, 0xD4); // Cyan for info
    public static final Color VERIFIED_BLUE = new Color(0x1E, 0x88, 0xE5); // Blue for verified badge
    
    // Neutral Colors - Orange to Magenta Gradient Background Theme
    // Background uses gradient (orange to magenta), cards and buttons use contrasting colors
    public static final Color BACKGROUND = GRADIENT_START; // #FF6B35 - Orange (gradient start, will be painted as gradient)
    public static final Color CARD_BACKGROUND = new Color(0xFA, 0xFA, 0xFA); // #FAFAFA - White/light gray for cards (contrasts with gradient)
    public static final Color BORDER_COLOR = new Color(0xE0, 0xE0, 0xE0); // #E0E0E0 - Light gray border
    public static final Color DIVIDER_COLOR = new Color(0xE0, 0xE0, 0xE0); // #E0E0E0 - Light gray divider
    
    // Text Colors - Optimized for gradient background and light cards
    public static final Color TEXT_PRIMARY = new Color(0x1A, 0x1A, 0x1A); // #1A1A1A - Dark text for light cards
    public static final Color TEXT_SECONDARY = new Color(0x66, 0x66, 0x66); // #666666 - Medium gray for light cards
    public static final Color TEXT_MUTED = new Color(0x99, 0x99, 0x99); // #999999 - Muted gray
    public static final Color TEXT_LINK = new Color(0xE9, 0x1E, 0x63); // #E91E63 - Magenta for links
    public static final Color TEXT_ON_DARK = new Color(0xFA, 0xFA, 0xFA); // #FAFAFA - Light text for gradient background
    
    // Sidebar Colors - Gradient Background Theme
    public static final Color SIDEBAR_BACKGROUND = GRADIENT_START; // Gradient background
    public static final Color SIDEBAR_BUTTON = new Color(0xF0, 0x87, 0x87); // #F08787 - Light pink/coral (contrasts with gradient)
    public static final Color SIDEBAR_HOVER = new Color(0xF5, 0x9A, 0x9A); // Lighter pink on hover
    public static final Color SIDEBAR_ACTIVE = new Color(0xFA, 0xAD, 0xAD); // Even lighter pink when active
    public static final Color SIDEBAR_TEXT = new Color(0x1A, 0x1A, 0x1A); // Dark text for sidebar (on light pink buttons)
    public static final Color SIDEBAR_ICON = new Color(0xFA, 0xFA, 0xFA); // Light icons for sidebar
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 22);
    
    
    // Dimensions
    public static final int CORNER_RADIUS = 16; // More rounded for elegance
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 44;
    public static final int SIDEBAR_WIDTH = 250;
    public static final int CARD_PADDING = 20; // More padding for elegance
    
    // Shadow Colors 
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 12); // Very soft shadow
    public static final Color SHADOW_COLOR_MEDIUM = new Color(0, 0, 0, 20); // Medium shadow
    public static final Color SHADOW_COLOR_STRONG = new Color(0, 0, 0, 35); // Stronger shadow for depth
    
    private Theme() {} // Prevent instantiation
    
    /**
     * Creates a standard panel with the app background color (gradient)
     */
    public static javax.swing.JPanel createBackgroundPanel() {
        javax.swing.JPanel panel = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, GRADIENT_START,
                    0, getHeight(), GRADIENT_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        panel.setBackground(BACKGROUND);
        return panel;
    }  
    
    /**
     * Creates a styled label with the given style
     */
    public static javax.swing.JLabel createLabel(String text, Font font, Color color) {
        javax.swing.JLabel label = new javax.swing.JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    /**
     * Applies hover effect to a component
     */
    public static void applyHoverEffect(java.awt.Component component, 
                                        Color normalColor, Color hoverColor) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                component.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                component.setBackground(normalColor);
            }
        });
    }
}

