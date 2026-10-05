package com.courseenrollment.ui;

import com.courseenrollment.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window managing all screen transitions via CardLayout.
 */
public class MainFrame extends JFrame {

    public static final String SCREEN_SPLASH = "SPLASH";
    public static final String SCREEN_AUTH = "AUTH";
    public static final String SCREEN_WELCOME = "WELCOME";
    public static final String SCREEN_DASHBOARD = "DASHBOARD";

    private final CardLayout cardLayout;
    private final JPanel rootContainer;

    private final SplashPanel splashPanel;
    private final LoginSignupPanel loginSignupPanel;
    private final WelcomeTransitionPanel welcomePanel;
    private final DashboardPanel dashboardPanel;

    private User authenticatedUser;

    public MainFrame() {
        setTitle("Online Course Enrollment System — Modern Java & MongoDB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        rootContainer = new JPanel(cardLayout);

        splashPanel = new SplashPanel(this);
        loginSignupPanel = new LoginSignupPanel(this);
        welcomePanel = new WelcomeTransitionPanel(this);
        dashboardPanel = new DashboardPanel(this);

        rootContainer.add(splashPanel, SCREEN_SPLASH);
        rootContainer.add(loginSignupPanel, SCREEN_AUTH);
        rootContainer.add(welcomePanel, SCREEN_WELCOME);
        rootContainer.add(dashboardPanel, SCREEN_DASHBOARD);

        setContentPane(rootContainer);
    }

    public void startApp() {
        setVisible(true);
        showScreen(SCREEN_SPLASH);
        splashPanel.startAnimation();
    }

    public void showScreen(String screenName) {
        cardLayout.show(rootContainer, screenName);
    }

    public void setAuthenticatedUser(User user) {
        this.authenticatedUser = user;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void showWelcomeTransition(User user) {
        setAuthenticatedUser(user);
        welcomePanel.startTransition(user);
        showScreen(SCREEN_WELCOME);
    }

    public void showDashboard() {
        if (authenticatedUser != null) {
            dashboardPanel.setUser(authenticatedUser);
            showScreen(SCREEN_DASHBOARD);
        } else {
            showScreen(SCREEN_AUTH);
        }
    }

    public void logout() {
        this.authenticatedUser = null;
        loginSignupPanel.resetFields();
        showScreen(SCREEN_AUTH);
    }

    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }
}
