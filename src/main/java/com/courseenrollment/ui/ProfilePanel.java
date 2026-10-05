package com.courseenrollment.ui;

import com.courseenrollment.dao.CourseDAO;
import com.courseenrollment.dao.EnrollmentDAO;
import com.courseenrollment.dao.UserDAO;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;
import com.courseenrollment.ui.components.ToastNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Account settings and profile management panel with cascading account deletion.
 */
public class ProfilePanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private User currentUser;

    private JLabel nameValue;
    private JLabel emailValue;
    private JLabel roleValue;
    private JLabel dateValue;
    private JLabel statsSummaryValue;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    public ProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();
        this.courseDAO = new CourseDAO();
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

        JLabel pageTitle = new JLabel("User Profile & Account");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(ThemeManager.getTextColor());
        titleBlock.add(pageTitle);

        JLabel subTitle = new JLabel("Manage your account settings and credentials");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitle.setForeground(ThemeManager.getMutedTextColor());
        titleBlock.add(subTitle);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // CENTER: Profile Information & Danger Zone
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // 1. Profile Details Card
        RoundedPanel profileCard = new RoundedPanel(16, ThemeManager.getCardColor());
        profileCard.setLayout(new BorderLayout(20, 0));
        profileCard.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        profileCard.setMaximumSize(new Dimension(800, 200));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar Icon
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 54));
        profileCard.add(avatarLabel, BorderLayout.WEST);

        // User Fields Grid
        JPanel fieldsGrid = new JPanel(new GridLayout(4, 2, 16, 10));
        fieldsGrid.setOpaque(false);

        fieldsGrid.add(createFieldLabel("Full Name:"));
        nameValue = createFieldValue("Loading...");
        fieldsGrid.add(nameValue);

        fieldsGrid.add(createFieldLabel("Email Address:"));
        emailValue = createFieldValue("Loading...");
        fieldsGrid.add(emailValue);

        fieldsGrid.add(createFieldLabel("Role:"));
        roleValue = createFieldValue("Loading...");
        fieldsGrid.add(roleValue);

        fieldsGrid.add(createFieldLabel("Member Since:"));
        dateValue = createFieldValue("Loading...");
        fieldsGrid.add(dateValue);

        profileCard.add(fieldsGrid, BorderLayout.CENTER);
        contentPanel.add(profileCard);

        contentPanel.add(Box.createVerticalStrut(20));

        // 2. Summary Activity Card
        RoundedPanel activityCard = new RoundedPanel(16, ThemeManager.getCardColor());
        activityCard.setLayout(new BorderLayout(0, 10));
        activityCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        activityCard.setMaximumSize(new Dimension(800, 110));
        activityCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel activityTitle = new JLabel("Activity & Stats");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activityTitle.setForeground(ThemeManager.getTextColor());
        activityCard.add(activityTitle, BorderLayout.NORTH);

        statsSummaryValue = new JLabel("Loading your activity metrics...");
        statsSummaryValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsSummaryValue.setForeground(ThemeManager.getMutedTextColor());
        activityCard.add(statsSummaryValue, BorderLayout.CENTER);

        contentPanel.add(activityCard);

        contentPanel.add(Box.createVerticalStrut(20));

        // 3. Danger Zone Card (Delete My Account)
        RoundedPanel dangerCard = new RoundedPanel(16, ThemeManager.getCardColor());
        dangerCard.setCustomBorderColor(new Color(239, 68, 68, 120));
        dangerCard.setBorderWidth(1);
        dangerCard.setLayout(new BorderLayout(16, 12));
        dangerCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        dangerCard.setMaximumSize(new Dimension(800, 130));
        dangerCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dangerTextPanel = new JPanel();
        dangerTextPanel.setLayout(new BoxLayout(dangerTextPanel, BoxLayout.Y_AXIS));
        dangerTextPanel.setOpaque(false);

        JLabel dangerTitle = new JLabel("Danger Zone — Delete Account");
        dangerTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dangerTitle.setForeground(ThemeManager.ACCENT_DANGER);
        dangerTextPanel.add(dangerTitle);

        dangerTextPanel.add(Box.createVerticalStrut(4));

        JLabel dangerDesc = new JLabel("<html>Permanently delete your user profile and all associated data. " +
                "All enrollments (and created courses if instructor) will be cascade removed. This action cannot be undone.</html>");
        dangerDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dangerDesc.setForeground(ThemeManager.getMutedTextColor());
        dangerTextPanel.add(dangerDesc);

        dangerCard.add(dangerTextPanel, BorderLayout.CENTER);

        CustomButton deleteAccBtn = new CustomButton("Delete My Account", CustomButton.Variant.DANGER);
        deleteAccBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteAccBtn.addActionListener(e -> handleDeleteAccount());
        dangerCard.add(deleteAccBtn, BorderLayout.EAST);

        contentPanel.add(dangerCard);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.getMutedTextColor());
        return lbl;
    }

    private JLabel createFieldValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(ThemeManager.getTextColor());
        return lbl;
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            nameValue.setText(user.getName());
            emailValue.setText(user.getEmail());
            roleValue.setText(user.getRole());
            dateValue.setText(user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : "N/A");

            loadUserStats();
        }
    }

    private void loadUserStats() {
        if (currentUser == null) return;

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                if (currentUser instanceof Student) {
                    long enrollCount = enrollmentDAO.countEnrollmentsByUser(currentUser.getEmail());
                    return "You are currently enrolled in " + enrollCount + " courses.";
                } else if (currentUser instanceof Instructor) {
                    long courseCount = courseDAO.countCoursesByInstructor(currentUser.getEmail());
                    long studentCount = enrollmentDAO.countEnrollmentsForInstructor(currentUser.getEmail());
                    return "You have published " + courseCount + " courses with " + studentCount + " total student enrollments.";
                }
                return "";
            }

            @Override
            protected void done() {
                try {
                    statsSummaryValue.setText(get());
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void handleDeleteAccount() {
        if (currentUser == null) return;

        String message = "Are you ABSOLUTELY sure you want to delete your account (" + currentUser.getEmail() + ")?\n\n"
                + "• All your enrollments will be deleted.\n";
        if (currentUser instanceof Instructor) {
            message += "• All courses created by you and student enrollments in those courses will be deleted.\n";
        }
        message += "\nThis action is permanent and cannot be undone.";

        int confirm = JOptionPane.showConfirmDialog(
                mainFrame,
                message,
                "Warning: Permanent Account Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    String email = currentUser.getEmail();

                    // 1. Cascading cleanup for Student
                    enrollmentDAO.deleteEnrollmentsByUser(email);

                    // 2. Cascading cleanup for Instructor
                    if (currentUser instanceof Instructor) {
                        for (var c : courseDAO.getCoursesByInstructor(email)) {
                            enrollmentDAO.deleteEnrollmentsByCourse(c.getId());
                        }
                        courseDAO.deleteCoursesByInstructor(email);
                    }

                    // 3. Delete user document
                    return userDAO.deleteUser(email);
                }

                @Override
                protected void done() {
                    try {
                        boolean deleted = get();
                        if (deleted) {
                            ToastNotification.show(mainFrame, "Your account has been deleted.", ToastNotification.Type.INFO);
                            mainFrame.logout();
                        } else {
                            ToastNotification.show(mainFrame, "Could not delete account.", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception ex) {
                        ToastNotification.show(mainFrame, "Error deleting account: " + ex.getMessage(), ToastNotification.Type.ERROR);
                    }
                }
            }.execute();
        }
    }
}
