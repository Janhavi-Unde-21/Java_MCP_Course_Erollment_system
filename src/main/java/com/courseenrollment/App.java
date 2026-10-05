package com.courseenrollment;

import com.courseenrollment.db.DatabaseSeeder;
import com.courseenrollment.db.MongoConnection;
import com.courseenrollment.ui.MainFrame;
import com.courseenrollment.ui.ThemeManager;

import javax.swing.*;

/**
 * Main application entry point for the Online Course Enrollment System.
 * Initializes FlatLaf theme, establishes MongoDB connection, seeds default data, and starts the UI.
 */
public class App {

    public static void main(String[] args) {
        // 1. Initialize FlatLaf modern UI theme
        ThemeManager.initializeTheme();

        // 2. Connect to MongoDB and seed default data if needed
        try {
            boolean connected = MongoConnection.getInstance().ping();
            if (connected) {
                System.out.println("[App] Successfully connected to MongoDB on localhost:27017 (database: online_course_db)");
                DatabaseSeeder.seedIfEmpty();
            } else {
                System.err.println("[App] Warning: Could not connect to MongoDB. Please ensure 'mongod' is running on localhost:27017.");
            }
        } catch (Exception e) {
            System.err.println("[App] Error connecting to MongoDB: " + e.getMessage());
        }

        // 3. Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.startApp();
        });
    }
}
