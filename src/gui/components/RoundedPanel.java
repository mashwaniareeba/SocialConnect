package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * RoundedPanel - A panel with rounded corners and optional shadow
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private boolean showShadow;
    private int shadowOffset;

    public RoundedPanel() {
        this(15, Color.WHITE, true);
    }

    public RoundedPanel(int cornerRadius) {
        this(cornerRadius, Color.WHITE, true);
    }

    public RoundedPanel(int cornerRadius, Color backgroundColor, boolean showShadow) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
        this.showShadow = showShadow;
        this.shadowOffset = 4;
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        if (showShadow) {
            g2d.setColor(new Color(0, 0, 0, 8));
            g2d.fill(new RoundRectangle2D.Float(shadowOffset + 2, shadowOffset + 2, 
                width - shadowOffset - 3, height - shadowOffset - 3, cornerRadius + 2, cornerRadius + 2));
            
            g2d.setColor(new Color(0, 0, 0, 15));
            g2d.fill(new RoundRectangle2D.Float(shadowOffset, shadowOffset, 
                width - shadowOffset - 1, height - shadowOffset - 1, cornerRadius, cornerRadius));
        }

        g2d.setColor(backgroundColor);
        int panelWidth = showShadow ? width - shadowOffset - 1 : width - 1;
        int panelHeight = showShadow ? height - shadowOffset - 1 : height - 1;
        g2d.fill(new RoundRectangle2D.Float(0, 0, panelWidth, panelHeight, cornerRadius, cornerRadius));

        g2d.setColor(new Color(0xE8, 0xEB, 0xF0));
        g2d.setStroke(new BasicStroke(0.5f));
        g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, panelWidth - 1, panelHeight - 1, cornerRadius, cornerRadius));

        g2d.dispose();
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    @Override
    public Insets getInsets() {
        int padding = 20;
        int bottomRight = showShadow ? padding + shadowOffset : padding;
        return new Insets(padding, padding, bottomRight, bottomRight);
    }
}
