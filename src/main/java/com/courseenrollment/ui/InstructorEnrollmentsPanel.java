package com.courseenrollment.ui;

import com.courseenrollment.dao.EnrollmentDAO;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.Enrollment;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel for instructors to view all student enrollments across their published courses.
 */
public class InstructorEnrollmentsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final EnrollmentDAO enrollmentDAO;
    private User currentUser;

    private JTable enrollmentTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

    public InstructorEnrollmentsPanel(MainFrame mainFrame) {
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

        JLabel pageTitle = new JLabel("Student Course Enrollments");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(ThemeManager.getTextColor());
        titleBlock.add(pageTitle);

        countLabel = new JLabel("All active student registrations across your published curriculum");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(ThemeManager.getMutedTextColor());
        titleBlock.add(countLabel);

        headerPanel.add(titleBlock, BorderLayout.WEST);

        CustomButton refreshBtn = new CustomButton("🔄 Refresh Roster", CustomButton.Variant.SECONDARY);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> loadInstructorEnrollments());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // CENTER: Modern Table Container
        RoundedPanel tableCard = new RoundedPanel(16, ThemeManager.getCardColor());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] columns = {"#", "Student Name", "Student Email", "Course Title", "Category", "Enrolled Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        enrollmentTable.setRowHeight(36);
        enrollmentTable.setShowVerticalLines(false);
        enrollmentTable.setIntercellSpacing(new Dimension(0, 4));
        enrollmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        enrollmentTable.getTableHeader().setReorderingAllowed(false);

        // Column alignment renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        enrollmentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        enrollmentTable.getColumnModel().getColumn(0).setMaxWidth(50);
        enrollmentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        enrollmentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ThemeManager.getCardColor());

        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadInstructorEnrollments();
    }

    public void loadInstructorEnrollments() {
        if (currentUser == null) return;

        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() {
                return enrollmentDAO.getEnrollmentsForInstructor(currentUser.getEmail());
            }

            @Override
            protected void done() {
                try {
                    List<Enrollment> enrollments = get();
                    tableModel.setRowCount(0);

                    countLabel.setText("Found " + enrollments.size() + " total student registration" + (enrollments.size() == 1 ? "" : "s"));

                    int index = 1;
                    for (Enrollment enr : enrollments) {
                        Course c = enr.getCourse();
                        String courseTitle = c != null ? c.getTitle() : "Unknown Course";
                        String courseType = c != null ? c.getCourseType() : "-";
                        String studentName = enr.getStudentName() != null ? enr.getStudentName() : enr.getUserEmail();
                        String dateStr = enr.getEnrolledAt() != null ? dateFormat.format(enr.getEnrolledAt()) : "-";

                        tableModel.addRow(new Object[]{
                                index++,
                                studentName,
                                enr.getUserEmail(),
                                courseTitle,
                                courseType,
                                dateStr
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading instructor enrollments: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
