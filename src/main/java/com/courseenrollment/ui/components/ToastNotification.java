package com.courseenrollment.ui.components;

import com.courseenrollment.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern floating glassmorphic toast notification component.
 */
public class ToastNotification extends JPanel {

    public enum Type {
        SUCCESS,
        ERROR,
        INFO,
        WARNING
    }

    private final String message;
    private final Type type;
    private float opacity = 0f;
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Timer displayTimer;

    public ToastNotification(String message, Type type, JFrame parentFrame) {
        this.message = message;
        this.type = type;
        setOpaque(false);
        setLayout(new BorderLayout(12, 0));
        setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        String iconStr;
        Color accentColor;

        switch (type) {
            case SUCCESS -> {
                iconStr = "✓";
                accentColor = ThemeManager.ACCENT_EMERALD;
            }
            case ERROR -> {
                iconStr = "✕";
                accentColor = ThemeManager.ACCENT_DANGER;
            }
            case WARNING -> {
                iconStr = "⚠";
                accentColor = ThemeManager.ACCENT_AMBER;
            }
            default -> {
                iconStr = "ℹ";
                accentColor = ThemeManager.PRIMARY;
            }
        }

        JLabel iconLabel = new JLabel(iconStr);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        iconLabel.setForeground(accentColor);
        add(iconLabel, BorderLayout.WEST);

        JLabel textLabel = new JLabel(message);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        textLabel.setForeground(Color.WHITE);
        add(textLabel, BorderLayout.CENTER);

        // Timers for smooth fade in/out
        fadeOutTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity = Math.max(0.0f, opacity - 0.1f);
                repaint();
                if (opacity <= 0.0f) {
                    fadeOutTimer.stop();
                    Container parent = getParent();
                    if (parent != null) {
                        parent.remove(ToastNotification.this);
                        parent.repaint();
                    }
                }
            }
        });

        displayTimer = new Timer(2600, e -> {
            displayTimer.stop();
            fadeOutTimer.start();
        });

        fadeInTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity = Math.min(1.0f, opacity + 0.1f);
                repaint();
                if (opacity >= 1.0f) {
                    fadeInTimer.stop();
                    displayTimer.start();
                }
            }
        });
    }

    public static void show(JFrame frame, String message, Type type) {
        if (frame == null) return;

        SwingUtilities.invokeLater(() -> {
            JLayeredPane layeredPane = frame.getLayeredPane();
            ToastNotification toast = new ToastNotification(message, type, frame);

            Dimension prefSize = toast.getPreferredSize();
            int width = Math.min(prefSize.width + 40, 500);
            int height = Math.max(prefSize.height, 46);

            int x = (frame.getWidth() - width) / 2;
            int y = frame.getHeight() - height - 50;

            toast.setBounds(x, y, width, height);
            layeredPane.add(toast, JLayeredPane.POPUP_LAYER);
            layeredPane.repaint();

            toast.fadeInTimer.start();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(new Color(15, 23, 42, 235)); // Deep slate translucent
        g2.fill(new RoundRectangle2D.Double(0, 0, width, height, 14, 14));

        // Border
        Color borderColor;
        switch (type) {
            case SUCCESS -> borderColor = new Color(16, 185, 129, 180);
            case ERROR -> borderColor = new Color(239, 68, 68, 180);
            case WARNING -> borderColor = new Color(245, 158, 11, 180);
            default -> borderColor = new Color(99, 102, 241, 180);
        }

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Double(1, 1, width - 2, height - 2, 14, 14));

        g2.dispose();
        super.paintComponent(g);
    }
}
