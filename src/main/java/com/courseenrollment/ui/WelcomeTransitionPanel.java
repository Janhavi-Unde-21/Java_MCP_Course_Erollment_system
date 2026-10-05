package com.courseenrollment.ui;

import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

/**
 * Modern welcome transition screen (~1 second) displayed upon successful login
 * greeting the user by name before navigating to the Dashboard.
 */
public class WelcomeTransitionPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JLabel greetingLabel;
    private final JLabel roleBadgeLabel;
    private final JLabel subTextLabel;
    private Timer transitionTimer;

    public WelcomeTransitionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42));

        RoundedPanel card = new RoundedPanel(24, new Color(30, 41, 59));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        card.setPreferredSize(new Dimension(460, 320));

        // Animated Avatar Icon
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 68;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                g2.setColor(new Color(16, 185, 129, 60)); // Emerald glow
                g2.fill(new Ellipse2D.Double(x - 6, y - 6, size + 12, size + 12));

                g2.setColor(ThemeManager.ACCENT_EMERALD);
                g2.fill(new Ellipse2D.Double(x, y, size, size));

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "👋";
                int strX = (getWidth() - fm.stringWidth(icon)) / 2;
                int strY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(icon, strX, strY);

                g2.dispose();
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(90, 80));
        avatarPanel.setMaximumSize(new Dimension(90, 80));
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(avatarPanel);

        card.add(Box.createVerticalStrut(16));

        // Greeting Text
        greetingLabel = new JLabel("Hello, User!");
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(greetingLabel);

        card.add(Box.createVerticalStrut(8));

        // Role Badge
        roleBadgeLabel = new JLabel(" Student Account ");
        roleBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleBadgeLabel.setForeground(ThemeManager.PRIMARY);
        roleBadgeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(roleBadgeLabel);

        card.add(Box.createVerticalStrut(24));

        // Subtext / Loading indicator
        subTextLabel = new JLabel("Loading your personalized workspace...");
        subTextLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subTextLabel.setForeground(new Color(148, 163, 184));
        subTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subTextLabel);

        add(card);
    }

    public void startTransition(User user) {
        if (user != null) {
            greetingLabel.setText("Hello, " + user.getName() + "!");
            String roleText = " " + user.getRole() + " Portal ";
            roleBadgeLabel.setText(roleText);
            roleBadgeLabel.setForeground("Instructor".equalsIgnoreCase(user.getRole()) ? ThemeManager.ACCENT_CYAN : ThemeManager.ACCENT_EMERALD);
        }

        if (transitionTimer != null && transitionTimer.isRunning()) {
            transitionTimer.stop();
        }

        transitionTimer = new Timer(1100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionTimer.stop();
                mainFrame.showDashboard();
            }
        });
        transitionTimer.setRepeats(false);
        transitionTimer.start();
    }
}
