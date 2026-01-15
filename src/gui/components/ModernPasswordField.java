package gui.components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ModernPasswordField - Styled password field with placeholder and focus effects
 */
public class ModernPasswordField extends JPasswordField {
    private String placeholder;
    private Color placeholderColor;
    private Color focusBorderColor;
    private Color normalBorderColor;
    private boolean showingPlaceholder;

    public ModernPasswordField() {
        this("Password");
    }

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        this.placeholderColor = new Color(0x99, 0x99, 0x99);
        this.focusBorderColor = new Color(0x3B, 0x59, 0x98);
        this.normalBorderColor = new Color(0xDD, 0xDF, 0xE2);
        this.showingPlaceholder = true;

        setupField();
    }

    private void setupField() {
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(200, 40));
        setBorder(createBorder(normalBorderColor));
        setForeground(placeholderColor);
        setEchoChar((char) 0); // Show placeholder text
        setText(placeholder);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(createBorder(focusBorderColor));
                if (showingPlaceholder) {
                    setText("");
                    setForeground(Color.BLACK);
                    setEchoChar('●');
                    showingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(createBorder(normalBorderColor));
                // Only show placeholder if password is actually empty
                // Use a small delay to avoid clearing password when clicking login button
                Timer timer = new Timer(100, evt -> {
                    if (getPassword().length == 0 && !showingPlaceholder) {
                        setForeground(placeholderColor);
                        setEchoChar((char) 0);
                        setText(placeholder);
                        showingPlaceholder = true;
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    private Border createBorder(Color color) {
        return BorderFactory.createCompoundBorder(
            new LineBorder(color, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    public String getActualPassword() {
        // Always get the actual password from the field, regardless of placeholder state
        char[] passwordChars = getPassword();
        if (passwordChars == null || passwordChars.length == 0) {
            return "";
        }
        String password = new String(passwordChars);
        // Don't return placeholder text as password
        if (password.equals(placeholder)) {
            return "";
        }
        return password;
    }

    public void clearField() {
        setText("");
        showingPlaceholder = true;
        setForeground(placeholderColor);
        setEchoChar((char) 0);
        setText(placeholder);
    }
}




