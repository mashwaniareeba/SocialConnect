package gui.components;

import gui.Theme;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ModernTextField - Styled text field with placeholder and focus effects
 */
public class ModernTextField extends JTextField {
    private String placeholder;
    private Color placeholderColor;
    private Color focusBorderColor;
    private Color normalBorderColor;
    private boolean showingPlaceholder;

    public ModernTextField() {
        this("");
    }

    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        this.placeholderColor = Theme.TEXT_MUTED; // Muted text for placeholder
        this.focusBorderColor = Theme.GRADIENT_END; // Magenta border on focus
        this.normalBorderColor = Theme.BORDER_COLOR; // Light gray border
        this.showingPlaceholder = true;

        setupField();
    }

    private void setupField() {
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(200, 40));
        setBackground(Theme.CARD_BACKGROUND); // White/light background
        setForeground(Theme.TEXT_PRIMARY); // Dark text
        setBorder(createBorder(normalBorderColor));
        setForeground(placeholderColor);
        setText(placeholder);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(createBorder(focusBorderColor));
                if (showingPlaceholder) {
                    setText("");
                    setForeground(Theme.TEXT_PRIMARY); // Light text
                    showingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(createBorder(normalBorderColor));
                if (getText().isEmpty()) {
                    setForeground(placeholderColor);
                    setText(placeholder);
                    showingPlaceholder = true;
                }
            }
        });
    }

    private Border createBorder(Color color) {
        return BorderFactory.createCompoundBorder(
            new LineBorder(color, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    @Override
    public String getText() {
        String text = super.getText();
        if (showingPlaceholder) {
            return "";
        }
        return text;
    }

    public void clearField() {
        setText("");
        showingPlaceholder = true;
        setForeground(placeholderColor);
        super.setText(placeholder);
    }

    public void setActualText(String text) {
        showingPlaceholder = false;
        setForeground(Theme.TEXT_PRIMARY); // Light text
        super.setText(text);
    }
}
