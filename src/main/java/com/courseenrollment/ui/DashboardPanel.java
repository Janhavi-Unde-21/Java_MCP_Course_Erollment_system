package com.courseenrollment.ui;

import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application dashboard featuring a modern sidebar and role-aware navigation
 * between Overview, Courses, Enrollments, and Profile panels.
 */
public class DashboardPanel extends JPanel {

    public static final String TAB_OVERVIEW = "OVERVIEW";
    public static final String TAB_COURSES = "COURSES";
    public static final String TAB_MY_ENROLLMENTS = "MY_ENROLLMENTS";
    public static final String TAB_INSTRUCTOR_ENROLLMENTS = "INSTRUCTOR_ENROLLMENTS";
    public static final String TAB_PROFILE = "PROFILE";

    private final MainFrame mainFrame;
    private User currentUser;

    private JPanel contentContainer;
    private CardLayout contentCardLayout;

    private OverviewPanel overviewPanel;
    private CoursePanel coursePanel;
    private MyEnrollmentsPanel myEnrollmentsPanel;
    private InstructorEnrollmentsPanel instructorEnrollmentsPanel;
    private ProfilePanel profilePanel;

    private JLabel sidebarUserName;
    private JLabel sidebarUserRole;
    private CustomButton navEnrollmentsBtn;
    private final Map<String, CustomButton> navButtons = new HashMap<>();

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getBackgroundColor());

        initComponents();
    }

    private void initComponents() {
        // 1. LEFT SIDEBAR
        JPanel sidebar = new JPanel(new BorderLayout(0, 16));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(ThemeManager.getSidebarColor());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.getBorderColor()));

        // Sidebar Top: Brand & User Profile
        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setOpaque(false);
        sidebarTop.setBorder(new EmptyBorder(24, 18, 16, 18));

        // Brand Logo
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandIcon = new JLabel("🎓");
        brandIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        brandRow.add(brandIcon);

        JLabel brandTitle = new JLabel("CoursePortal");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(ThemeManager.getTextColor());
        brandRow.add(brandTitle);

        sidebarTop.add(brandRow);
        sidebarTop.add(Box.createVerticalStrut(20));

        // User Profile Mini Card
        RoundedPanel userMiniCard = new RoundedPanel(12, ThemeManager.getCardColor());
        userMiniCard.setLayout(new BorderLayout(10, 0));
        userMiniCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        userMiniCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userAvatar = new JLabel("👤");
        userAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        userMiniCard.add(userAvatar, BorderLayout.WEST);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        sidebarUserName = new JLabel("User Name");
        sidebarUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sidebarUserName.setForeground(ThemeManager.getTextColor());
        userInfo.add(sidebarUserName);

        sidebarUserRole = new JLabel("Student");
        sidebarUserRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sidebarUserRole.setForeground(ThemeManager.PRIMARY);
        userInfo.add(sidebarUserRole);

        userMiniCard.add(userInfo, BorderLayout.CENTER);
        sidebarTop.add(userMiniCard);

        sidebar.add(sidebarTop, BorderLayout.NORTH);

        // Sidebar Center: Navigation Buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(0, 14, 0, 14));

        CustomButton overviewBtn = createNavButton("🏠  Dashboard Overview", TAB_OVERVIEW);
        CustomButton coursesBtn = createNavButton("📚  Browse Courses", TAB_COURSES);
        navEnrollmentsBtn = createNavButton("🎓  My Enrollments", TAB_MY_ENROLLMENTS);
        CustomButton profileBtn = createNavButton("👤  Profile & Account", TAB_PROFILE);

        navPanel.add(overviewBtn);
        navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(coursesBtn);
        navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(navEnrollmentsBtn);
        navPanel.add(Box.createVerticalStrut(6));
        navPanel.add(profileBtn);

        sidebar.add(navPanel, BorderLayout.CENTER);

        // Sidebar Bottom: Theme Toggle & Logout
        JPanel sidebarBottom = new JPanel();
        sidebarBottom.setLayout(new BoxLayout(sidebarBottom, BoxLayout.Y_AXIS));
        sidebarBottom.setOpaque(false);
        sidebarBottom.setBorder(new EmptyBorder(16, 14, 20, 14));

        CustomButton themeToggleBtn = new CustomButton("🌓  Toggle Theme", CustomButton.Variant.SECONDARY);
        themeToggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        themeToggleBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        themeToggleBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        themeToggleBtn.addActionListener(e -> {
            ThemeManager.toggleTheme(mainFrame);
            updateAllColors();
        });
        sidebarBottom.add(themeToggleBtn);

        sidebarBottom.add(Box.createVerticalStrut(8));

        CustomButton logoutBtn = new CustomButton("🚪  Sign Out", CustomButton.Variant.DANGER);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to sign out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.logout();
            }
        });
        sidebarBottom.add(logoutBtn);

        sidebar.add(sidebarBottom, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // 2. RIGHT CONTENT AREA (CardLayout)
        contentCardLayout = new CardLayout();
        contentContainer = new JPanel(contentCardLayout);
        contentContainer.setOpaque(false);

        overviewPanel = new OverviewPanel(mainFrame, this);
        coursePanel = new CoursePanel(mainFrame);
        myEnrollmentsPanel = new MyEnrollmentsPanel(mainFrame);
        instructorEnrollmentsPanel = new InstructorEnrollmentsPanel(mainFrame);
        profilePanel = new ProfilePanel(mainFrame);

        contentContainer.add(overviewPanel, TAB_OVERVIEW);
        contentContainer.add(coursePanel, TAB_COURSES);
        contentContainer.add(myEnrollmentsPanel, TAB_MY_ENROLLMENTS);
        contentContainer.add(instructorEnrollmentsPanel, TAB_INSTRUCTOR_ENROLLMENTS);
        contentContainer.add(profilePanel, TAB_PROFILE);

        add(contentContainer, BorderLayout.CENTER);

        navigateTo(TAB_OVERVIEW);
    }

    private CustomButton createNavButton(String text, String targetTab) {
        CustomButton btn = new CustomButton(text, CustomButton.Variant.SECONDARY);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> navigateTo(targetTab));
        navButtons.put(targetTab, btn);
        return btn;
    }

    public void navigateTo(String tabName) {
        contentCardLayout.show(contentContainer, tabName);

        // Update active nav button state
        for (Map.Entry<String, CustomButton> entry : navButtons.entrySet()) {
            if (entry.getKey().equals(tabName)) {
                entry.getValue().setVariant(CustomButton.Variant.PRIMARY);
            } else {
                entry.getValue().setVariant(CustomButton.Variant.SECONDARY);
            }
        }

        // Trigger dynamic refresh on panel open
        if (TAB_OVERVIEW.equals(tabName)) {
            overviewPanel.loadDashboardMetrics();
        } else if (TAB_COURSES.equals(tabName)) {
            coursePanel.loadCourses();
        } else if (TAB_MY_ENROLLMENTS.equals(tabName)) {
            myEnrollmentsPanel.loadEnrollments();
        } else if (TAB_INSTRUCTOR_ENROLLMENTS.equals(tabName)) {
            instructorEnrollmentsPanel.loadInstructorEnrollments();
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            sidebarUserName.setText(user.getName());
            sidebarUserRole.setText(user.getRole() + " Account");

            boolean isInstructor = user instanceof Instructor;
            if (isInstructor) {
                navEnrollmentsBtn.setText("📋  Student Roster");
                navButtons.put(TAB_INSTRUCTOR_ENROLLMENTS, navEnrollmentsBtn);
            } else {
                navEnrollmentsBtn.setText("🎓  My Enrollments");
                navButtons.put(TAB_MY_ENROLLMENTS, navEnrollmentsBtn);
            }

            // Propagate user to all panels
            overviewPanel.setUser(user);
            coursePanel.setUser(user);
            myEnrollmentsPanel.setUser(user);
            instructorEnrollmentsPanel.setUser(user);
            profilePanel.setUser(user);

            navigateTo(TAB_OVERVIEW);
        }
    }

    public void updateAllColors() {
        setBackground(ThemeManager.getBackgroundColor());
        revalidate();
        repaint();
    }
}
