package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * ModernButton - Custom styled button with hover and press animations
 */
public class ModernButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    private int cornerRadius;
    private boolean isHovered = false;
    private boolean isPressed = false;

    // Preset button styles
    public static final int STYLE_PRIMARY = 0;
    public static final int STYLE_SECONDARY = 1;
    public static final int STYLE_SUCCESS = 2;
    public static final int STYLE_GHOST = 3;

    public ModernButton(String text) {
        this(text, STYLE_PRIMARY);
    }

    public ModernButton(String text, int style) {
        super(text);
        this.cornerRadius = 10;
        applyStyle(style);
        setupButton();
    }

    private void applyStyle(int style) {
        switch (style) {
            case STYLE_PRIMARY:
                // Dark button that contrasts with gradient background
                normalColor = new Color(0x1A, 0x1A, 0x1A); // #1A1A1A - Dark gray/black
                hoverColor = new Color(0x33, 0x33, 0x33); // #333333 - Lighter dark gray on hover
                pressedColor = new Color(0x0A, 0x0A, 0x0A); // #0A0A0A - Very dark when pressed
                textColor = new Color(0xFA, 0xFA, 0xFA); // Light text for dark button
                break;
            case STYLE_SECONDARY:
                normalColor = new Color(0x66, 0x66, 0x66); // #666666 - Medium gray
                hoverColor = new Color(0x7A, 0x7A, 0x7A); // Lighter gray on hover
                pressedColor = new Color(0x50, 0x50, 0x50); // Darker gray when pressed
                textColor = new Color(0xFA, 0xFA, 0xFA); // Light text
                break;
            case STYLE_SUCCESS:
                normalColor = new Color(0x4C, 0xAF, 0x50); // Green
                hoverColor = new Color(0x66, 0xBB, 0x6A);
                pressedColor = new Color(0x43, 0xA0, 0x47);
                textColor = new Color(0xFA, 0xFA, 0xFA); // Light text
                break;
            case STYLE_GHOST:
                normalColor = new Color(0xFA, 0xFA, 0xFA); // White/light card background
                hoverColor = new Color(0xF0, 0xF0, 0xF0); // Slightly darker on hover
                pressedColor = new Color(0xE0, 0xE0, 0xE0); // Even darker when pressed
                textColor = new Color(0x1A, 0x1A, 0x1A); // Dark text for light background
                break;
            default:
                applyStyle(STYLE_PRIMARY);
        }
    }

    private void setupButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(textColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 40));
        setEnabled(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Ensure click is properly handled
                if (isEnabled()) {
                    fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        // Solid colors for buttons (contrasting with gradient background)
        Color currentColor;
        if (!isEnabled()) {
            currentColor = new Color(0x66, 0x66, 0x66);
        } else if (isPressed) {
            currentColor = pressedColor;
        } else if (isHovered) {
            currentColor = hoverColor;
        } else {
            currentColor = normalColor;
        }
        g2d.setColor(currentColor);
        g2d.fill(shape);

        g2d.setColor(isEnabled() ? textColor : new Color(0x80, 0x80, 0x80));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }
}
