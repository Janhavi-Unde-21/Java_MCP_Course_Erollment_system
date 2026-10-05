package com.courseenrollment.ui;

import com.courseenrollment.db.MongoConnection;
import com.courseenrollment.ui.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

/**
 * Animated Splash Screen on launch that initializes background connections
 * and smoothly advances to the Login/Signup screen.
 */
public class SplashPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private int progress = 0;
    private Timer animationTimer;

    public SplashPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42)); // Deep Slate

        RoundedPanel container = new RoundedPanel(24, new Color(30, 41, 59));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        container.setPreferredSize(new Dimension(480, 360));

        // Animated / Glowing Logo Icon
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 72;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Outer glow circle
                g2.setColor(new Color(99, 102, 241, 60));
                g2.fill(new Ellipse2D.Double(x - 6, y - 6, size + 12, size + 12));

                // Main circle
                g2.setColor(ThemeManager.PRIMARY);
                g2.fill(new Ellipse2D.Double(x, y, size, size));

                // Icon text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "🎓";
                int strX = (getWidth() - fm.stringWidth(icon)) / 2;
                int strY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(icon, strX, strY);

                g2.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(100, 85));
        logoPanel.setMaximumSize(new Dimension(100, 85));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(logoPanel);

        container.add(Box.createVerticalStrut(16));

        // Title
        JLabel titleLabel = new JLabel("Online Course Enrollment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(titleLabel);

        container.add(Box.createVerticalStrut(6));

        // Subtitle
        JLabel subtitleLabel = new JLabel("College Mini-Project • Java & MongoDB");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(subtitleLabel);

        container.add(Box.createVerticalStrut(30));

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(380, 8));
        progressBar.setMaximumSize(new Dimension(380, 8));
        progressBar.setForeground(ThemeManager.PRIMARY);
        progressBar.setBackground(new Color(51, 65, 85));
        progressBar.setBorderPainted(false);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(progressBar);

        container.add(Box.createVerticalStrut(12));

        // Status Label
        statusLabel = new JLabel("Connecting to local MongoDB...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(148, 163, 184));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(statusLabel);

        add(container);
    }

    public void startAnimation() {
        progress = 0;
        progressBar.setValue(0);

        animationTimer = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 2;
                progressBar.setValue(progress);

                if (progress == 30) {
                    statusLabel.setText("Verifying database collections...");
                } else if (progress == 60) {
                    statusLabel.setText("Applying modern FlatLaf theme...");
                } else if (progress == 85) {
                    statusLabel.setText("Ready!");
                } else if (progress >= 100) {
                    animationTimer.stop();
                    // Advance smoothly to Login/Signup screen
                    mainFrame.showScreen(MainFrame.SCREEN_AUTH);
                }
            }
        });

        // Test DB connection in background thread while progress animates
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    MongoConnection.getInstance().ping();
                } catch (Exception ignored) {}
                return null;
            }
        }.execute();

        animationTimer.start();
    }
}
