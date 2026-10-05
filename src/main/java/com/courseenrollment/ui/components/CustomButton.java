package com.courseenrollment.ui.components;

import com.courseenrollment.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern custom-styled button with rounded corners, smooth hover effects,
 * and semantic button variants (PRIMARY, SECONDARY, DANGER, SUCCESS, OUTLINE).
 */
public class CustomButton extends JButton {

    public enum Variant {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS,
        OUTLINE
    }

    private Variant variant = Variant.PRIMARY;
    private int cornerRadius = 12;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public CustomButton(String text) {
        this(text, Variant.PRIMARY);
    }

    public CustomButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        initButton();
    }

    public CustomButton(String text, Icon icon, Variant variant) {
        super(text, icon);
        this.variant = variant;
        initButton();
    }

    private void initButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setMargin(new Insets(10, 18, 10, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        if (!isEnabled()) {
            bg = ThemeManager.isDarkMode() ? new Color(51, 65, 85) : new Color(226, 232, 240);
            fg = ThemeManager.isDarkMode() ? new Color(100, 116, 139) : new Color(148, 163, 184);
        } else {
            switch (variant) {
                case PRIMARY -> {
                    bg = isPressed ? ThemeManager.PRIMARY_HOVER.darker() : (isHovered ? ThemeManager.PRIMARY_HOVER : ThemeManager.PRIMARY);
                    fg = Color.WHITE;
                }
                case SECONDARY -> {
                    bg = ThemeManager.isDarkMode()
                            ? (isHovered ? new Color(71, 85, 105) : new Color(51, 65, 85))
                            : (isHovered ? new Color(226, 232, 240) : new Color(241, 245, 249));
                    fg = ThemeManager.getTextColor();
                }
                case DANGER -> {
                    bg = isPressed ? new Color(185, 28, 28) : (isHovered ? new Color(220, 38, 38) : ThemeManager.ACCENT_DANGER);
                    fg = Color.WHITE;
                }
                case SUCCESS -> {
                    bg = isPressed ? new Color(4, 120, 87) : (isHovered ? new Color(5, 150, 105) : ThemeManager.ACCENT_EMERALD);
                    fg = Color.WHITE;
                }
                case OUTLINE -> {
                    bg = isHovered ? (ThemeManager.isDarkMode() ? new Color(51, 65, 85) : new Color(241, 245, 249)) : new Color(0, 0, 0, 0);
                    fg = ThemeManager.PRIMARY;
                    border = ThemeManager.PRIMARY;
                }
                default -> {
                    bg = ThemeManager.PRIMARY;
                    fg = Color.WHITE;
                }
            }
        }

        // Draw background
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerRadius, cornerRadius));

        // Draw outline border if specified
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));
        }

        setForeground(fg);
        g2.dispose();
        super.paintComponent(g);
    }
}
