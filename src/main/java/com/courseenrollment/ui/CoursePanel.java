package com.courseenrollment.ui;

import com.courseenrollment.dao.CourseDAO;
import com.courseenrollment.dao.EnrollmentDAO;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.DesignCourse;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.ProgrammingCourse;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;
import com.courseenrollment.ui.components.ToastNotification;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Course catalog panel allowing students to browse/search/filter/enroll in courses,
 * and instructors to publish new courses and manage/delete existing ones.
 */
public class CoursePanel extends JPanel {

    private final MainFrame mainFrame;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private User currentUser;

    private JTextField searchField;
    private JComboBox<String> typeFilterCombo;
    private JPanel cardsGridPanel;
    private JLabel countLabel;
    private CustomButton addCourseButton;

    public CoursePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
    }

    private void initComponents() {
        // TOP: Header + Search & Filter Toolbar
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Header Row (Title + Add Course Button)
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel pageTitle = new JLabel("Explore Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(ThemeManager.getTextColor());
        titleBlock.add(pageTitle);

        countLabel = new JLabel("Discover and enroll in top-tier technical and design courses");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(ThemeManager.getMutedTextColor());
        titleBlock.add(countLabel);

        headerRow.add(titleBlock, BorderLayout.WEST);

        addCourseButton = new CustomButton("+ Add New Course", CustomButton.Variant.PRIMARY);
        addCourseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        headerRow.add(addCourseButton, BorderLayout.EAST);

        topContainer.add(headerRow);
        topContainer.add(Box.createVerticalStrut(16));

        // Filter & Search Bar
        RoundedPanel filterBar = new RoundedPanel(14, ThemeManager.getCardColor());
        filterBar.setLayout(new BorderLayout(12, 0));
        filterBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        // Search Input
        JPanel searchBox = new JPanel(new BorderLayout(8, 0));
        searchBox.setOpaque(false);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchBox.add(searchIcon, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Search by title, instructor, or tech stack...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { loadCourses(); }
            @Override
            public void removeUpdate(DocumentEvent e) { loadCourses(); }
            @Override
            public void changedUpdate(DocumentEvent e) { loadCourses(); }
        });
        searchBox.add(searchField, BorderLayout.CENTER);

        filterBar.add(searchBox, BorderLayout.CENTER);

        // Category Filter Combo
        JPanel filterBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterBox.setOpaque(false);

        JLabel filterLabel = new JLabel("Category:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(ThemeManager.getMutedTextColor());
        filterBox.add(filterLabel);

        typeFilterCombo = new JComboBox<>(new String[]{"All Categories", "Programming", "Design"});
        typeFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeFilterCombo.addActionListener(e -> loadCourses());
        filterBox.add(typeFilterCombo);

        filterBar.add(filterBox, BorderLayout.EAST);

        topContainer.add(filterBar);
        add(topContainer, BorderLayout.NORTH);

        // CENTER: Scrollable Course Cards Grid
        cardsGridPanel = new JPanel();
        cardsGridPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 18, 18));
        cardsGridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsGridPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setUser(User user) {
        this.currentUser = user;
        // Only instructors see "+ Add New Course"
        addCourseButton.setVisible(user instanceof Instructor);
        loadCourses();
    }

    public void loadCourses() {
        String query = searchField.getText().trim();
        String selectedType = (String) typeFilterCombo.getSelectedItem();
        String typeFilter = "All Categories".equals(selectedType) ? "All" : selectedType;

        new SwingWorker<List<Course>, Void>() {
            private Set<ObjectId> enrolledIds;

            @Override
            protected List<Course> doInBackground() {
                if (currentUser instanceof Student) {
                    enrolledIds = enrollmentDAO.getEnrolledCourseIds(currentUser.getEmail());
                }
                return courseDAO.searchCourses(query, typeFilter);
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    cardsGridPanel.removeAll();

                    countLabel.setText("Showing " + courses.size() + " course" + (courses.size() == 1 ? "" : "s"));

                    if (courses.isEmpty()) {
                        cardsGridPanel.add(createEmptyState());
                    } else {
                        for (Course course : courses) {
                            boolean isEnrolled = enrolledIds != null && enrolledIds.contains(course.getId());
                            CourseCard card = new CourseCard(
                                    course,
                                    currentUser,
                                    isEnrolled,
                                    CoursePanel.this::handleEnroll,
                                    CoursePanel.this::handleDeleteCourse
                            );
                            cardsGridPanel.add(card);
                        }
                    }

                    cardsGridPanel.revalidate();
                    cardsGridPanel.repaint();
                } catch (Exception ex) {
                    System.err.println("Error loading courses: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private JPanel createEmptyState() {
        RoundedPanel emptyPanel = new RoundedPanel(16, ThemeManager.getCardColor());
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        emptyPanel.setPreferredSize(new Dimension(500, 220));

        JLabel emptyIcon = new JLabel("🔍");
        emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptyIcon);

        emptyPanel.add(Box.createVerticalStrut(10));

        JLabel emptyTitle = new JLabel("No Courses Found");
        emptyTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        emptyTitle.setForeground(ThemeManager.getTextColor());
        emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptyTitle);

        emptyPanel.add(Box.createVerticalStrut(6));

        JLabel emptySub = new JLabel("Try adjusting your search keyword or category filter.");
        emptySub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emptySub.setForeground(ThemeManager.getMutedTextColor());
        emptySub.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(emptySub);

        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.setOpaque(false);
        container.add(emptyPanel);
        return container;
    }

    private void handleEnroll(Course course) {
        if (currentUser == null || !(currentUser instanceof Student)) {
            ToastNotification.show(mainFrame, "Only students can enroll in courses.", ToastNotification.Type.WARNING);
            return;
        }

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return enrollmentDAO.enrollStudent(currentUser.getEmail(), course.getId());
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        ToastNotification.show(mainFrame, "Successfully enrolled in: " + course.getTitle(), ToastNotification.Type.SUCCESS);
                        loadCourses(); // Refresh UI
                    } else {
                        ToastNotification.show(mainFrame, "You are already enrolled in this course.", ToastNotification.Type.INFO);
                    }
                } catch (Exception ex) {
                    ToastNotification.show(mainFrame, "Enrollment failed: " + ex.getMessage(), ToastNotification.Type.ERROR);
                }
            }
        }.execute();
    }

    private void handleDeleteCourse(Course course) {
        int confirm = JOptionPane.showConfirmDialog(
                mainFrame,
                "Are you sure you want to delete \"" + course.getTitle() + "\"?\nThis will also remove all student enrollments for this course.",
                "Confirm Course Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    // Cascade: delete enrollments first, then delete course
                    enrollmentDAO.deleteEnrollmentsByCourse(course.getId());
                    return courseDAO.deleteCourse(course.getId(), currentUser.getEmail());
                }

                @Override
                protected void done() {
                    try {
                        boolean deleted = get();
                        if (deleted) {
                            ToastNotification.show(mainFrame, "Course deleted successfully.", ToastNotification.Type.SUCCESS);
                            loadCourses();
                        } else {
                            ToastNotification.show(mainFrame, "Could not delete course.", ToastNotification.Type.ERROR);
                        }
                    } catch (Exception ex) {
                        ToastNotification.show(mainFrame, "Deletion failed: " + ex.getMessage(), ToastNotification.Type.ERROR);
                    }
                }
            }.execute();
        }
    }

    private void showAddCourseDialog() {
        JDialog dialog = new JDialog(mainFrame, "Create New Course", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Course Title
        JLabel titleLbl = new JLabel("Course Title *");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(titleLbl);
        formPanel.add(Box.createVerticalStrut(4));

        JTextField titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(titleField);

        formPanel.add(Box.createVerticalStrut(12));

        // Course Type
        JLabel typeLbl = new JLabel("Course Type *");
        typeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(typeLbl);
        formPanel.add(Box.createVerticalStrut(4));

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Programming", "Design"});
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(typeCombo);

        formPanel.add(Box.createVerticalStrut(12));

        // Specialization Tag / Tool / Language
        JLabel tagLbl = new JLabel("Primary Language / Design Tool *");
        tagLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(tagLbl);
        formPanel.add(Box.createVerticalStrut(4));

        JTextField tagField = new JTextField();
        tagField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tagField.putClientProperty("JTextField.placeholderText", "e.g., Java, Python, Figma, React, Illustrator");
        formPanel.add(tagField);

        formPanel.add(Box.createVerticalStrut(12));

        // Duration (Hours)
        JLabel durationLbl = new JLabel("Duration (Hours)");
        durationLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(durationLbl);
        formPanel.add(Box.createVerticalStrut(4));

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 300, 1));
        durationSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formPanel.add(durationSpinner);

        formPanel.add(Box.createVerticalStrut(12));

        // Description
        JLabel descLbl = new JLabel("Course Description *");
        descLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(descLbl);
        formPanel.add(Box.createVerticalStrut(4));

        JTextArea descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        CustomButton cancelBtn = new CustomButton("Cancel", CustomButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());

        CustomButton createBtn = new CustomButton("Publish Course", CustomButton.Variant.PRIMARY);
        createBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String tag = tagField.getText().trim();
            int duration = (Integer) durationSpinner.getValue();
            String desc = descArea.getText().trim();

            if (title.isEmpty() || tag.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course newCourse;
            if ("Design".equalsIgnoreCase(type)) {
                newCourse = new DesignCourse(title, desc, currentUser.getEmail(), currentUser.getName(), duration, tag);
            } else {
                newCourse = new ProgrammingCourse(title, desc, currentUser.getEmail(), currentUser.getName(), duration, tag);
            }

            boolean saved = courseDAO.createCourse(newCourse);
            if (saved) {
                dialog.dispose();
                ToastNotification.show(mainFrame, "Course published successfully!", ToastNotification.Type.SUCCESS);
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to create course in MongoDB.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBar.add(cancelBtn);
        btnBar.add(createBtn);
        dialog.add(btnBar, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
