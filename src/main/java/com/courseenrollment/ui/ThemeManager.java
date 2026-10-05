package com.courseenrollment.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Manages FlatLaf theming, color palettes, fonts, and dark/light mode switching.
 */
public class ThemeManager {

    private static boolean isDark = true;

    // Brand Palette
    public static final Color PRIMARY = new Color(99, 102, 241);        // Indigo-500
    public static final Color PRIMARY_HOVER = new Color(79, 70, 229);  // Indigo-600
    public static final Color PRIMARY_LIGHT = new Color(224, 231, 255); // Indigo-100

    public static final Color ACCENT_CYAN = new Color(14, 165, 233);    // Programming Badge Cyan
    public static final Color ACCENT_ROSE = new Color(244, 63, 94);     // Design Badge Rose
    public static final Color ACCENT_EMERALD = new Color(16, 185, 129); // Success Green
    public static final Color ACCENT_AMBER = new Color(245, 158, 11);   // Warning Amber
    public static final Color ACCENT_DANGER = new Color(239, 68, 68);   // Red Danger

    // Dark Mode Surfaces
    public static final Color DARK_BG = new Color(15, 23, 42);          // Slate 900
    public static final Color DARK_CARD = new Color(30, 41, 59);        // Slate 800
    public static final Color DARK_CARD_HOVER = new Color(51, 65, 85);  // Slate 700
    public static final Color DARK_SIDEBAR = new Color(15, 23, 42);     // Slate 900
    public static final Color DARK_BORDER = new Color(51, 65, 85);      // Slate 700
    public static final Color DARK_TEXT_MUTED = new Color(148, 163, 184);// Slate 400

    // Light Mode Surfaces
    public static final Color LIGHT_BG = new Color(248, 250, 252);      // Slate 50
    public static final Color LIGHT_CARD = new Color(255, 255, 255);    // White
    public static final Color LIGHT_CARD_HOVER = new Color(241, 245, 249);
    public static final Color LIGHT_SIDEBAR = new Color(255, 255, 255);
    public static final Color LIGHT_BORDER = new Color(226, 232, 240);  // Slate 200
    public static final Color LIGHT_TEXT_MUTED = new Color(100, 116, 139);

    public static void initializeTheme() {
        try {
            // Configure FlatLaf global styles before setting L&F
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabHeight", 38);
            UIManager.put("TabbedPane.selectedBackground", PRIMARY);

            // Default font
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
            UIManager.put("defaultFont", defaultFont);

            if (isDark) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
        } catch (Exception e) {
            System.err.println("Could not initialize FlatLaf theme: " + e.getMessage());
        }
    }

    public static void toggleTheme(JFrame rootFrame) {
        isDark = !isDark;
        try {
            if (isDark) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            FlatLaf.updateUI();
            if (rootFrame != null) {
                SwingUtilities.updateComponentTreeUI(rootFrame);
                rootFrame.repaint();
            }
        } catch (Exception e) {
            System.err.println("Failed to toggle theme: " + e.getMessage());
        }
    }

    public static boolean isDarkMode() {
        return isDark;
    }

    public static Color getBackgroundColor() {
        return isDark ? DARK_BG : LIGHT_BG;
    }

    public static Color getCardColor() {
        return isDark ? DARK_CARD : LIGHT_CARD;
    }

    public static Color getCardHoverColor() {
        return isDark ? DARK_CARD_HOVER : LIGHT_CARD_HOVER;
    }

    public static Color getBorderColor() {
        return isDark ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Color getTextColor() {
        return isDark ? Color.WHITE : new Color(30, 41, 59);
    }

    public static Color getMutedTextColor() {
        return isDark ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED;
    }

    public static Color getSidebarColor() {
        return isDark ? DARK_SIDEBAR : LIGHT_SIDEBAR;
    }
}
