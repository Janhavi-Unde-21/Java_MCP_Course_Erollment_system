package com.courseenrollment.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A JPanel with customizable corner radius, background color, and border rendering.
 */
public class RoundedPanel extends JPanel {

    private int cornerRadius = 16;
    private Color customBackground = null;
    private Color customBorderColor = null;
    private int borderWidth = 1;
    private boolean isHoverable = false;
    private boolean isHovered = false;

    public RoundedPanel() {
        setOpaque(false);
    }

    public RoundedPanel(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color background) {
        this.cornerRadius = radius;
        this.customBackground = background;
        setOpaque(false);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setCustomBackground(Color bg) {
        this.customBackground = bg;
        repaint();
    }

    public void setCustomBorderColor(Color border) {
        this.customBorderColor = border;
        repaint();
    }

    public void setBorderWidth(int width) {
        this.borderWidth = width;
        repaint();
    }

    public void setHoverable(boolean hoverable) {
        this.isHoverable = hoverable;
        if (hoverable) {
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background color resolution
        Color bg = customBackground != null ? customBackground : getBackground();
        if (isHoverable && isHovered) {
            // Brighten slightly on hover
            bg = new Color(
                    Math.min(255, bg.getRed() + 15),
                    Math.min(255, bg.getGreen() + 15),
                    Math.min(255, bg.getBlue() + 20)
            );
        }

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerRadius, cornerRadius));

        // Border painting
        if (customBorderColor != null && borderWidth > 0) {
            g2.setColor(customBorderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(new RoundRectangle2D.Double(
                    borderWidth / 2.0,
                    borderWidth / 2.0,
                    width - borderWidth,
                    height - borderWidth,
                    cornerRadius,
                    cornerRadius
            ));
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
