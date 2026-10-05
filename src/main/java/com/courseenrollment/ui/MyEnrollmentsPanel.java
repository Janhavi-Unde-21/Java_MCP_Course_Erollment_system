package com.courseenrollment.ui;

import com.courseenrollment.dao.EnrollmentDAO;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.DesignCourse;
import com.courseenrollment.model.Enrollment;
import com.courseenrollment.model.ProgrammingCourse;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;
import com.courseenrollment.ui.components.ToastNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel displaying all courses enrolled by the active student,
 * retrieved via MongoDB $lookup aggregation joins.
 */
public class MyEnrollmentsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final EnrollmentDAO enrollmentDAO;
    private User currentUser;

    private JPanel cardsPanel;
    private JLabel countLabel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

    public MyEnrollmentsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.enrollmentDAO = new EnrollmentDAO();

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
    }

    private void initComponents() {
        // TOP: Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel pageTitle = new JLabel("My Enrolled Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(ThemeManager.getTextColor());
        titleBlock.add(pageTitle);

        countLabel = new JLabel("Your active learning paths & curriculum");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(ThemeManager.getMutedTextColor());
        titleBlock.add(countLabel);

        headerPanel.add(titleBlock, BorderLayout.WEST);

        CustomButton refreshBtn = new CustomButton("🔄 Refresh", CustomButton.Variant.SECONDARY);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> loadEnrollments());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // CENTER: Scrollable list of enrolled courses
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 18, 18));
        cardsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadEnrollments();
    }

    public void loadEnrollments() {
        if (currentUser == null) return;

        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() {
                return enrollmentDAO.getEnrolledCoursesForStudent(currentUser.getEmail());
            }

            @Override
            protected void done() {
                try {
                    List<Enrollment> enrollments = get();
                    cardsPanel.removeAll();

                    countLabel.setText("You are enrolled in " + enrollments.size() + " active course" + (enrollments.size() == 1 ? "" : "s"));

                    if (enrollments.isEmpty()) {
                        cardsPanel.add(createEmptyState());
                    } else {
                        for (Enrollment enrollment : enrollments) {
                            cardsPanel.add(createEnrollmentCard(enrollment));
                        }
                    }

                    cardsPanel.revalidate();
                    cardsPanel.repaint();
                } catch (Exception ex) {
                    System.err.println("Error loading enrollments: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private JPanel createEnrollmentCard(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        if (course == null) return new JPanel();

        RoundedPanel card = new RoundedPanel(16, ThemeManager.getCardColor());
        card.setHoverable(true);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        card.setPreferredSize(new Dimension(340, 250));

        // Top Badges
        JPanel topBadgePanel = new JPanel(new BorderLayout(8, 0));
        topBadgePanel.setOpaque(false);

        boolean isProgramming = course instanceof ProgrammingCourse;
        String typeName = course.getCourseType();
        String typeIcon = isProgramming ? "💻 " : "🎨 ";
        Color badgeColor = isProgramming ? ThemeManager.ACCENT_CYAN : ThemeManager.ACCENT_ROSE;

        JLabel typeBadge = new JLabel(typeIcon + typeName);
        typeBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        typeBadge.setForeground(badgeColor);
        topBadgePanel.add(typeBadge, BorderLayout.WEST);

        JLabel tagBadge = new JLabel("• " + course.getSpecializationTag());
        tagBadge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tagBadge.setForeground(ThemeManager.getMutedTextColor());
        topBadgePanel.add(tagBadge, BorderLayout.EAST);

        card.add(topBadgePanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("<html><body style='width: 250px'><b>" + escapeHtml(course.getTitle()) + "</b></body></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(ThemeManager.getTextColor());
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createVerticalStrut(6));

        JLabel instructorLabel = new JLabel("👤 Instructor: " + course.getInstructorName());
        instructorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        instructorLabel.setForeground(ThemeManager.getMutedTextColor());
        instructorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(instructorLabel);

        centerPanel.add(Box.createVerticalStrut(4));

        String dateStr = enrollment.getEnrolledAt() != null ? dateFormat.format(enrollment.getEnrolledAt()) : "Recent";
        JLabel dateLabel = new JLabel("📅 Enrolled: " + dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(ThemeManager.getMutedTextColor());
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(dateLabel);

        centerPanel.add(Box.createVerticalStrut(8));

        // Progress bar simulation / status badge
        JPanel progressBox = new JPanel(new BorderLayout(8, 0));
        progressBox.setOpaque(false);
        progressBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar pBar = new JProgressBar(0, 100);
        pBar.setValue(45);
        pBar.setPreferredSize(new Dimension(180, 6));
        pBar.setForeground(ThemeManager.ACCENT_EMERALD);
        pBar.setBackground(new Color(51, 65, 85));
        progressBox.add(pBar, BorderLayout.CENTER);

        JLabel statusTxt = new JLabel(" In Progress ");
        statusTxt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusTxt.setForeground(ThemeManager.ACCENT_EMERALD);
        progressBox.add(statusTxt, BorderLayout.EAST);

        centerPanel.add(progressBox);

        card.add(centerPanel, BorderLayout.CENTER);

        // Bottom: Drop Enrollment Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        CustomButton dropBtn = new CustomButton("Drop Course", CustomButton.Variant.OUTLINE);
        dropBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dropBtn.addActionListener(e -> handleDropCourse(course));
        bottomPanel.add(dropBtn, BorderLayout.CENTER);

        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createEmptyState() {
        RoundedPanel emptyPanel = new RoundedPanel(16, ThemeManager.getCardColor());
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        emptyPanel.setPreferredSize(new Dimension(480, 220));

        JLabel emptyIcon = new JLabel("📚");
        emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptyIcon);

        emptyPanel.add(Box.createVerticalStrut(10));

        JLabel emptyTitle = new JLabel("No Active Enrollments");
        emptyTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        emptyTitle.setForeground(ThemeManager.getTextColor());
        emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptyTitle);

        emptyPanel.add(Box.createVerticalStrut(6));

        JLabel emptySub = new JLabel("You haven't enrolled in any courses yet.");
        emptySub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emptySub.setForeground(ThemeManager.getMutedTextColor());
        emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptySub);

        emptyPanel.add(Box.createVerticalStrut(14));

        CustomButton exploreBtn = new CustomButton("Explore Course Catalog", CustomButton.Variant.PRIMARY);
        exploreBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exploreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exploreBtn.addActionListener(e -> {
            if (mainFrame.getDashboardPanel() != null) {
                mainFrame.getDashboardPanel().navigateTo(DashboardPanel.TAB_COURSES);
            }
        });
        emptyPanel.add(exploreBtn);

        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.setOpaque(false);
        container.add(emptyPanel);
        return container;
    }

    private void handleDropCourse(Course course) {
        int confirm = JOptionPane.showConfirmDialog(
                mainFrame,
                "Are you sure you want to drop \"" + course.getTitle() + "\"?",
                "Confirm Drop Course",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return enrollmentDAO.dropEnrollment(currentUser.getEmail(), course.getId());
                }

                @Override
                protected void done() {
                    try {
                        boolean dropped = get();
                        if (dropped) {
                            ToastNotification.show(mainFrame, "Dropped course: " + course.getTitle(), ToastNotification.Type.INFO);
                            loadEnrollments();
                        } else {
                            ToastNotification.show(mainFrame, "Could not drop course.", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception ex) {
                        ToastNotification.show(mainFrame, "Error: " + ex.getMessage(), ToastNotification.Type.ERROR);
                    }
                }
            }.execute();
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
