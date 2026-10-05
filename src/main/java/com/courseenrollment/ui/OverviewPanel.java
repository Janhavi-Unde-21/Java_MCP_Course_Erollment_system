package com.courseenrollment.ui;

import com.courseenrollment.dao.CourseDAO;
import com.courseenrollment.dao.EnrollmentDAO;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;
import com.courseenrollment.ui.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Overview dashboard homepage displaying key metric cards, quick action tiles,
 * and featured courses.
 */
public class OverviewPanel extends JPanel {

    private final MainFrame mainFrame;
    private final DashboardPanel dashboardPanel;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private User currentUser;

    private JLabel greetingTitle;
    private JLabel greetingSubtitle;
    private JPanel statsGridPanel;
    private StatCard statCard1;
    private StatCard statCard2;
    private StatCard statCard3;
    private StatCard statCard4;

    public OverviewPanel(MainFrame mainFrame, DashboardPanel dashboardPanel) {
        this.mainFrame = mainFrame;
        this.dashboardPanel = dashboardPanel;
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();

        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
    }

    private void initComponents() {
        JPanel contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setOpaque(false);

        // 1. Welcome Banner Card
        RoundedPanel banner = new RoundedPanel(20, ThemeManager.PRIMARY);
        banner.setLayout(new BorderLayout(16, 0));
        banner.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        greetingTitle = new JLabel("Welcome back!");
        greetingTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        greetingTitle.setForeground(Color.WHITE);
        bannerText.add(greetingTitle);

        bannerText.add(Box.createVerticalStrut(4));

        greetingSubtitle = new JLabel("Explore cutting-edge courses, track your learning, and level up your skills.");
        greetingSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        greetingSubtitle.setForeground(new Color(224, 231, 255));
        bannerText.add(greetingSubtitle);

        banner.add(bannerText, BorderLayout.CENTER);

        JLabel bannerIcon = new JLabel("🚀");
        bannerIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        banner.add(bannerIcon, BorderLayout.EAST);

        contentContainer.add(banner);
        contentContainer.add(Box.createVerticalStrut(20));

        // 2. Metrics / Stat Cards Grid
        statsGridPanel = new JPanel(new GridLayout(1, 4, 16, 16));
        statsGridPanel.setOpaque(false);
        statsGridPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        statsGridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statCard1 = new StatCard("Total Courses", "0", "📚", "Platform Catalog", ThemeManager.PRIMARY);
        statCard2 = new StatCard("My Enrollments", "0", "🎓", "Active Path", ThemeManager.ACCENT_EMERALD);
        statCard3 = new StatCard("Programming", "0", "💻", "Tech & Coding", ThemeManager.ACCENT_CYAN);
        statCard4 = new StatCard("Design", "0", "🎨", "UI/UX & Arts", ThemeManager.ACCENT_ROSE);

        statsGridPanel.add(statCard1);
        statsGridPanel.add(statCard2);
        statsGridPanel.add(statCard3);
        statsGridPanel.add(statCard4);

        contentContainer.add(statsGridPanel);
        contentContainer.add(Box.createVerticalStrut(24));

        // 3. Quick Actions Section
        JLabel actionsTitle = new JLabel("Quick Actions");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionsTitle.setForeground(ThemeManager.getTextColor());
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentContainer.add(actionsTitle);

        contentContainer.add(Box.createVerticalStrut(10));

        JPanel actionsGrid = new JPanel(new GridLayout(1, 3, 16, 16));
        actionsGrid.setOpaque(false);
        actionsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        actionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action 1: Browse Catalog
        RoundedPanel action1 = createActionTile("📚 Browse Courses", "Search and enroll in new courses", () -> {
            dashboardPanel.navigateTo(DashboardPanel.TAB_COURSES);
        });

        // Action 2: Enrollments / Manage
        RoundedPanel action2 = createActionTile("🎓 My Curriculum", "View active enrollments & progress", () -> {
            if (currentUser instanceof Student) {
                dashboardPanel.navigateTo(DashboardPanel.TAB_MY_ENROLLMENTS);
            } else {
                dashboardPanel.navigateTo(DashboardPanel.TAB_INSTRUCTOR_ENROLLMENTS);
            }
        });

        // Action 3: Profile Settings
        RoundedPanel action3 = createActionTile("👤 Profile & Account", "Manage settings and account data", () -> {
            dashboardPanel.navigateTo(DashboardPanel.TAB_PROFILE);
        });

        actionsGrid.add(action1);
        actionsGrid.add(action2);
        actionsGrid.add(action3);

        contentContainer.add(actionsGrid);

        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private RoundedPanel createActionTile(String title, String desc, Runnable action) {
        RoundedPanel tile = new RoundedPanel(16, ThemeManager.getCardColor());
        tile.setHoverable(true);
        tile.setLayout(new BorderLayout(0, 8));
        tile.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(ThemeManager.getTextColor());
        tile.add(titleLbl, BorderLayout.NORTH);

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(ThemeManager.getMutedTextColor());
        tile.add(descLbl, BorderLayout.CENTER);

        CustomButton goBtn = new CustomButton("Open →", CustomButton.Variant.OUTLINE);
        goBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        goBtn.setMargin(new Insets(4, 8, 4, 8));
        goBtn.addActionListener(e -> action.run());
        tile.add(goBtn, BorderLayout.SOUTH);

        return tile;
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            greetingTitle.setText("Welcome back, " + user.getName() + "!");
            if (user instanceof Student) {
                greetingSubtitle.setText("Student Dashboard • Discover courses, track progress, and level up your skills.");
            } else {
                greetingSubtitle.setText("Instructor Dashboard • Publish curriculum, manage offerings, and monitor student enrollments.");
            }
            loadDashboardMetrics();
        }
    }

    public void loadDashboardMetrics() {
        if (currentUser == null) return;

        new SwingWorker<long[], Void>() {
            @Override
            protected long[] doInBackground() {
                long totalCourses = courseDAO.countCourses();
                long progCourses = courseDAO.countCoursesByType("Programming");
                long designCourses = courseDAO.countCoursesByType("Design");
                long userMetric;

                if (currentUser instanceof Student) {
                    userMetric = enrollmentDAO.countEnrollmentsByUser(currentUser.getEmail());
                } else {
                    userMetric = enrollmentDAO.countEnrollmentsForInstructor(currentUser.getEmail());
                }

                return new long[]{totalCourses, userMetric, progCourses, designCourses};
            }

            @Override
            protected void done() {
                try {
                    long[] metrics = get();
                    statCard1.setValue(String.valueOf(metrics[0]));

                    if (currentUser instanceof Student) {
                        statCard2.setValue(String.valueOf(metrics[1]));
                    } else {
                        statCard2.setValue(String.valueOf(metrics[1]));
                    }

                    statCard3.setValue(String.valueOf(metrics[2]));
                    statCard4.setValue(String.valueOf(metrics[3]));

                    statCard1.updateColors();
                    statCard2.updateColors();
                    statCard3.updateColors();
                    statCard4.updateColors();
                } catch (Exception ignored) {}
            }
        }.execute();
    }
}
