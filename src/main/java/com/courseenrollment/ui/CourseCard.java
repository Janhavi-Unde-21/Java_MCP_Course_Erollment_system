package com.courseenrollment.ui;

import com.courseenrollment.model.Course;
import com.courseenrollment.model.DesignCourse;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.ProgrammingCourse;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Modern Card UI component representing a single course with category badges,
 * instructor info, description, and context-sensitive action buttons.
 */
public class CourseCard extends RoundedPanel {

    private final Course course;
    private final User currentUser;
    private final boolean isEnrolled;
    private final Consumer<Course> onEnrollAction;
    private final Consumer<Course> onDeleteAction;

    public CourseCard(Course course, User currentUser, boolean isEnrolled,
                      Consumer<Course> onEnrollAction, Consumer<Course> onDeleteAction) {
        super(16);
        this.course = course;
        this.currentUser = currentUser;
        this.isEnrolled = isEnrolled;
        this.onEnrollAction = onEnrollAction;
        this.onDeleteAction = onDeleteAction;

        setHoverable(true);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        setPreferredSize(new Dimension(340, 260));
        setMaximumSize(new Dimension(380, 270));

        initComponents();
        updateTheme();
    }

    private void initComponents() {
        // TOP: Badges (Course Type & Specialization Tag)
        JPanel topBadgePanel = new JPanel(new BorderLayout(8, 0));
        topBadgePanel.setOpaque(false);

        boolean isProgramming = course instanceof ProgrammingCourse;
        String typeName = course.getCourseType();
        String typeIcon = isProgramming ? "💻 " : "🎨 ";
        Color badgeColor = isProgramming ? ThemeManager.ACCENT_CYAN : ThemeManager.ACCENT_ROSE;

        JLabel typeBadge = new JLabel(typeIcon + typeName);
        typeBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        typeBadge.setForeground(badgeColor);
        typeBadge.setOpaque(false);
        topBadgePanel.add(typeBadge, BorderLayout.WEST);

        if (course.getSpecializationTag() != null && !course.getSpecializationTag().isEmpty()) {
            JLabel tagBadge = new JLabel("• " + course.getSpecializationTag());
            tagBadge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            tagBadge.setForeground(ThemeManager.getMutedTextColor());
            topBadgePanel.add(tagBadge, BorderLayout.EAST);
        }

        add(topBadgePanel, BorderLayout.NORTH);

        // CENTER: Title, Instructor, Description
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("<html><body style='width: 250px'><b>" + escapeHtml(course.getTitle()) + "</b></body></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(ThemeManager.getTextColor());
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createVerticalStrut(6));

        // Instructor & Duration
        String instructorText = "👤 " + (course.getInstructorName() != null ? course.getInstructorName() : "Instructor");
        if (course.getDurationHours() > 0) {
            instructorText += "   ⏱ " + course.getDurationHours() + " hrs";
        }
        JLabel instructorLabel = new JLabel(instructorText);
        instructorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        instructorLabel.setForeground(ThemeManager.getMutedTextColor());
        instructorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(instructorLabel);

        centerPanel.add(Box.createVerticalStrut(8));

        // Description
        String desc = course.getDescription();
        if (desc == null || desc.isEmpty()) {
            desc = "No course description provided.";
        }
        JLabel descLabel = new JLabel("<html><body style='width: 250px; color: #94A3B8; font-size: 11px'>" + escapeHtml(desc) + "</body></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(descLabel);

        add(centerPanel, BorderLayout.CENTER);

        // BOTTOM: Action Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        if (currentUser instanceof Student) {
            if (isEnrolled) {
                CustomButton enrolledBtn = new CustomButton("✓ Enrolled", CustomButton.Variant.SUCCESS);
                enrolledBtn.setEnabled(false);
                enrolledBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                bottomPanel.add(enrolledBtn, BorderLayout.CENTER);
            } else {
                CustomButton enrollBtn = new CustomButton("Enroll Now", CustomButton.Variant.PRIMARY);
                enrollBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                enrollBtn.addActionListener(e -> {
                    if (onEnrollAction != null) {
                        onEnrollAction.accept(course);
                    }
                });
                bottomPanel.add(enrollBtn, BorderLayout.CENTER);
            }
        } else if (currentUser instanceof Instructor) {
            boolean isOwner = currentUser.getEmail() != null &&
                    currentUser.getEmail().equalsIgnoreCase(course.getCreatedBy());

            if (isOwner) {
                CustomButton deleteBtn = new CustomButton("🗑 Delete Course", CustomButton.Variant.DANGER);
                deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                deleteBtn.addActionListener(e -> {
                    if (onDeleteAction != null) {
                        onDeleteAction.accept(course);
                    }
                });
                bottomPanel.add(deleteBtn, BorderLayout.CENTER);
            } else {
                JLabel otherInstructorLabel = new JLabel("Created by peer instructor");
                otherInstructorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                otherInstructorLabel.setForeground(ThemeManager.getMutedTextColor());
                otherInstructorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                bottomPanel.add(otherInstructorLabel, BorderLayout.CENTER);
            }
        }

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateTheme() {
        setCustomBackground(ThemeManager.getCardColor());
        setCustomBorderColor(ThemeManager.getBorderColor());
        repaint();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}
