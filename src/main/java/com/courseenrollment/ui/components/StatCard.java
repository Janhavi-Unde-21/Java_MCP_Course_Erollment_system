package com.courseenrollment.ui.components;

import com.courseenrollment.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * Metric card component displaying key statistics with visual accents and icons.
 */
public class StatCard extends RoundedPanel {

    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel iconLabel;
    private final JLabel subtitleLabel;
    private final Color accentColor;

    public StatCard(String title, String value, String iconText, String subtitle, Color accentColor) {
        super(16);
        this.accentColor = accentColor != null ? accentColor : ThemeManager.PRIMARY;
        setHoverable(true);

        setLayout(new BorderLayout(12, 10));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Header Panel with Title & Icon
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(ThemeManager.getMutedTextColor());
        topPanel.add(titleLabel, BorderLayout.WEST);

        iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        topPanel.add(iconLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel with Large Metric Value
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(ThemeManager.getTextColor());
        centerPanel.add(valueLabel, BorderLayout.CENTER);

        if (subtitle != null && !subtitle.isEmpty()) {
            subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            subtitleLabel.setForeground(accentColor);
            centerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        } else {
            subtitleLabel = null;
        }

        add(centerPanel, BorderLayout.CENTER);

        updateColors();
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void updateColors() {
        setCustomBackground(ThemeManager.getCardColor());
        setCustomBorderColor(ThemeManager.getBorderColor());
        titleLabel.setForeground(ThemeManager.getMutedTextColor());
        valueLabel.setForeground(ThemeManager.getTextColor());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Paint top accent line
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(accentColor);
        g2.fillRoundRect(20, 8, 32, 4, 4, 4);
        g2.dispose();
    }
}
