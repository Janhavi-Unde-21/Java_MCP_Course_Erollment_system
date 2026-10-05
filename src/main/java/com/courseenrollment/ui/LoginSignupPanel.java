package com.courseenrollment.ui;

import com.courseenrollment.dao.UserDAO;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import com.courseenrollment.ui.components.CustomButton;
import com.courseenrollment.ui.components.RoundedPanel;
import com.courseenrollment.ui.components.ToastNotification;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Modern Authentication Panel supporting Login, Signup with Student/Instructor role selection,
 * BCrypt password hashing, and instant Demo Account quick-fills.
 */
public class LoginSignupPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Login Form Fields
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JLabel loginErrorLabel;

    // Signup Form Fields
    private JTextField signupNameField;
    private JTextField signupEmailField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
    private JRadioButton studentRoleRadio;
    private JRadioButton instructorRoleRadio;
    private JLabel signupErrorLabel;

    private JTabbedPane authTabs;

    public LoginSignupPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();

        setLayout(new GridBagLayout());
        setBackground(new Color(15, 23, 42));

        initComponents();
    }

    private void initComponents() {
        RoundedPanel card = new RoundedPanel(24, new Color(30, 41, 59));
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        card.setPreferredSize(new Dimension(500, 620));

        // Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel brandIcon = new JLabel("🎓");
        brandIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        brandIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(brandIcon);

        headerPanel.add(Box.createVerticalStrut(6));

        JLabel titleLabel = new JLabel("Course Enrollment Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign in to your account or create a new one");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        card.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for Login and Register
        authTabs = new JTabbedPane();
        authTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        authTabs.addTab("  Sign In  ", createLoginPanel());
        authTabs.addTab("  Sign Up  ", createSignupPanel());

        card.add(authTabs, BorderLayout.CENTER);

        // Bottom Demo Accounts Quick-Fill Bar
        JPanel demoBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        demoBar.setOpaque(false);

        JLabel demoHint = new JLabel("Quick Test:");
        demoHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        demoHint.setForeground(new Color(148, 163, 184));
        demoBar.add(demoHint);

        CustomButton demoStudentBtn = new CustomButton("Demo Student", CustomButton.Variant.OUTLINE);
        demoStudentBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoStudentBtn.setMargin(new Insets(4, 8, 4, 8));
        demoStudentBtn.addActionListener(e -> fillDemoCredentials("grace.hopper@student.edu", "password123"));
        demoBar.add(demoStudentBtn);

        CustomButton demoInstructorBtn = new CustomButton("Demo Instructor", CustomButton.Variant.OUTLINE);
        demoInstructorBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoInstructorBtn.setMargin(new Insets(4, 8, 4, 8));
        demoInstructorBtn.addActionListener(e -> fillDemoCredentials("alan.turing@college.edu", "password123"));
        demoBar.add(demoInstructorBtn);

        card.add(demoBar, BorderLayout.SOUTH);

        add(card);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 12, 12, 12));

        // Email Label & Field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(226, 232, 240));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(4));

        loginEmailField = new JTextField();
        loginEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(loginEmailField);

        panel.add(Box.createVerticalStrut(14));

        // Password Label & Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(226, 232, 240));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passLabel);
        panel.add(Box.createVerticalStrut(4));

        loginPasswordField = new JPasswordField();
        loginPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(loginPasswordField);

        panel.add(Box.createVerticalStrut(8));

        // Show/Hide Password Checkbox
        JCheckBox showPassCheck = new JCheckBox("Show Password");
        showPassCheck.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        showPassCheck.setForeground(new Color(148, 163, 184));
        showPassCheck.setOpaque(false);
        showPassCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPassCheck.addActionListener(e -> {
            loginPasswordField.setEchoChar(showPassCheck.isSelected() ? (char) 0 : '•');
        });
        panel.add(showPassCheck);

        panel.add(Box.createVerticalStrut(6));

        // Error message label
        loginErrorLabel = new JLabel(" ");
        loginErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginErrorLabel.setForeground(ThemeManager.ACCENT_DANGER);
        loginErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(loginErrorLabel);

        panel.add(Box.createVerticalStrut(12));

        // Sign In Button
        CustomButton signInButton = new CustomButton("Sign In to Account", CustomButton.Variant.PRIMARY);
        signInButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signInButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        signInButton.addActionListener(e -> handleLogin());
        panel.add(signInButton);

        // Allow pressing Enter key to submit
        loginPasswordField.addActionListener(e -> handleLogin());
        loginEmailField.addActionListener(e -> handleLogin());

        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Full Name
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(226, 232, 240));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(3));

        signupNameField = new JTextField();
        signupNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        signupNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(signupNameField);

        panel.add(Box.createVerticalStrut(10));

        // Email
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(226, 232, 240));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(3));

        signupEmailField = new JTextField();
        signupEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        signupEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(signupEmailField);

        panel.add(Box.createVerticalStrut(10));

        // Role Selection (Student vs Instructor)
        JLabel roleLabel = new JLabel("Account Role");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(new Color(226, 232, 240));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(roleLabel);
        panel.add(Box.createVerticalStrut(4));

        JPanel roleRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        roleRadioPanel.setOpaque(false);
        roleRadioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        studentRoleRadio = new JRadioButton("Student (Browse & Enroll)", true);
        studentRoleRadio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentRoleRadio.setForeground(Color.WHITE);
        studentRoleRadio.setOpaque(false);

        instructorRoleRadio = new JRadioButton("Instructor (Create Courses)");
        instructorRoleRadio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructorRoleRadio.setForeground(Color.WHITE);
        instructorRoleRadio.setOpaque(false);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(studentRoleRadio);
        roleGroup.add(instructorRoleRadio);

        roleRadioPanel.add(studentRoleRadio);
        roleRadioPanel.add(instructorRoleRadio);
        panel.add(roleRadioPanel);

        panel.add(Box.createVerticalStrut(10));

        // Password
        JLabel passLabel = new JLabel("Password (min 6 characters)");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(226, 232, 240));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passLabel);
        panel.add(Box.createVerticalStrut(3));

        signupPasswordField = new JPasswordField();
        signupPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        signupPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(signupPasswordField);

        panel.add(Box.createVerticalStrut(10));

        // Confirm Password
        JLabel confirmPassLabel = new JLabel("Confirm Password");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confirmPassLabel.setForeground(new Color(226, 232, 240));
        confirmPassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(confirmPassLabel);
        panel.add(Box.createVerticalStrut(3));

        signupConfirmPasswordField = new JPasswordField();
        signupConfirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        signupConfirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupConfirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(signupConfirmPasswordField);

        panel.add(Box.createVerticalStrut(4));

        // Signup error label
        signupErrorLabel = new JLabel(" ");
        signupErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signupErrorLabel.setForeground(ThemeManager.ACCENT_DANGER);
        signupErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(signupErrorLabel);

        panel.add(Box.createVerticalStrut(8));

        // Create Account Button
        CustomButton signUpButton = new CustomButton("Create Account", CustomButton.Variant.SUCCESS);
        signUpButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        signUpButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        signUpButton.addActionListener(e -> handleSignup());
        panel.add(signUpButton);

        return panel;
    }

    private void handleLogin() {
        loginErrorLabel.setText(" ");
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Please enter both email and password.");
            return;
        }

        loginErrorLabel.setText("Verifying credentials...");

        // Perform authentication in background thread to keep UI smooth
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return userDAO.authenticate(email, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        loginErrorLabel.setText(" ");
                        mainFrame.setAuthenticatedUser(user);
                        mainFrame.showWelcomeTransition(user);
                    } else {
                        loginErrorLabel.setText("Invalid email or password. Please try again.");
                    }
                } catch (Exception ex) {
                    loginErrorLabel.setText("Authentication failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void handleSignup() {
        signupErrorLabel.setText(" ");
        String name = signupNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String password = new String(signupPasswordField.getPassword()).trim();
        String confirmPassword = new String(signupConfirmPasswordField.getPassword()).trim();
        boolean isInstructor = instructorRoleRadio.isSelected();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            signupErrorLabel.setText("All fields are required.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            signupErrorLabel.setText("Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            signupErrorLabel.setText("Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            signupErrorLabel.setText("Passwords do not match.");
            return;
        }

        signupErrorLabel.setText("Creating your account...");

        new SwingWorker<Boolean, Void>() {
            private User createdUser;

            @Override
            protected Boolean doInBackground() {
                if (isInstructor) {
                    createdUser = new Instructor(name, email, null);
                } else {
                    createdUser = new Student(name, email, null);
                }
                return userDAO.registerUser(createdUser, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        signupErrorLabel.setText(" ");
                        ToastNotification.show(mainFrame, "Account created successfully! Welcome, " + name, ToastNotification.Type.SUCCESS);
                        mainFrame.setAuthenticatedUser(createdUser);
                        mainFrame.showWelcomeTransition(createdUser);
                    } else {
                        signupErrorLabel.setText("An account with this email already exists.");
                    }
                } catch (Exception ex) {
                    signupErrorLabel.setText("Registration error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    public void fillDemoCredentials(String email, String password) {
        authTabs.setSelectedIndex(0);
        loginEmailField.setText(email);
        loginPasswordField.setText(password);
        loginErrorLabel.setText(" ");
    }

    public void resetFields() {
        loginEmailField.setText("");
        loginPasswordField.setText("");
        loginErrorLabel.setText(" ");
        signupNameField.setText("");
        signupEmailField.setText("");
        signupPasswordField.setText("");
        signupConfirmPasswordField.setText("");
        signupErrorLabel.setText(" ");
        studentRoleRadio.setSelected(true);
    }
}
