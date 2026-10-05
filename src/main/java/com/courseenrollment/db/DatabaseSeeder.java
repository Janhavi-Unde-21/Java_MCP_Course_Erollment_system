package com.courseenrollment.db;

import com.courseenrollment.dao.CourseDAO;
import com.courseenrollment.dao.EnrollmentDAO;
import com.courseenrollment.dao.UserDAO;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.DesignCourse;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.ProgrammingCourse;
import com.courseenrollment.model.Student;

import java.util.List;

/**
 * Utility to seed sample instructors, students, courses, and initial enrollments
 * if the MongoDB database is empty.
 */
public class DatabaseSeeder {

    public static void seedIfEmpty() {
        try {
            UserDAO userDAO = new UserDAO();
            CourseDAO courseDAO = new CourseDAO();
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

            if (userDAO.countUsers() == 0) {
                System.out.println("[DatabaseSeeder] Seeding initial demo users and courses...");

                // 1. Seed Instructors
                Instructor alan = new Instructor("Dr. Alan Turing", "alan.turing@college.edu", null);
                userDAO.registerUser(alan, "password123");

                Instructor ada = new Instructor("Prof. Ada Lovelace", "ada.lovelace@college.edu", null);
                userDAO.registerUser(ada, "password123");

                // 2. Seed Students
                Student grace = new Student("Grace Hopper", "grace.hopper@student.edu", null);
                userDAO.registerUser(grace, "password123");

                Student jane = new Student("Jane Doe", "jane.doe@student.edu", null);
                userDAO.registerUser(jane, "password123");

                // 3. Seed Courses
                ProgrammingCourse c1 = new ProgrammingCourse(
                        "Full-Stack Java 21 & Spring Boot",
                        "Master enterprise Java, Spring Boot 3, REST APIs, Microservices, and MongoDB integration with hands-on projects.",
                        "alan.turing@college.edu",
                        "Dr. Alan Turing",
                        45,
                        "Java / Spring"
                );
                courseDAO.createCourse(c1);

                ProgrammingCourse c2 = new ProgrammingCourse(
                        "Data Structures & Algorithms in Python",
                        "In-depth guide to trees, graphs, dynamic programming, and algorithmic problem-solving for tech interviews.",
                        "alan.turing@college.edu",
                        "Dr. Alan Turing",
                        36,
                        "Python"
                );
                courseDAO.createCourse(c2);

                ProgrammingCourse c3 = new ProgrammingCourse(
                        "Modern Web Dev with React & TypeScript",
                        "Build blazing fast reactive user interfaces with React 19, TypeScript, state management, and modern Tailwind CSS.",
                        "ada.lovelace@college.edu",
                        "Prof. Ada Lovelace",
                        32,
                        "React / TS"
                );
                courseDAO.createCourse(c3);

                DesignCourse c4 = new DesignCourse(
                        "UI/UX Design Systems & Micro-Interactions",
                        "Design sleek modern interfaces, responsive design systems, interactive prototypes, and animations in Figma.",
                        "ada.lovelace@college.edu",
                        "Prof. Ada Lovelace",
                        28,
                        "Figma / UI"
                );
                courseDAO.createCourse(c4);

                DesignCourse c5 = new DesignCourse(
                        "Brand Identity, Typography & Visual Arts",
                        "Explore visual hierarchy, color harmonies, vector illustration, typography rules, and modern brand design.",
                        "ada.lovelace@college.edu",
                        "Prof. Ada Lovelace",
                        24,
                        "Illustrator"
                );
                courseDAO.createCourse(c5);

                ProgrammingCourse c6 = new ProgrammingCourse(
                        "Cloud-Native Systems with Docker & Kubernetes",
                        "Containerize services, deploy resilient microservice clusters, automate CI/CD pipelines, and monitor apps.",
                        "alan.turing@college.edu",
                        "Dr. Alan Turing",
                        40,
                        "Docker / K8s"
                );
                courseDAO.createCourse(c6);

                // 4. Seed initial enrollments for demo student Grace Hopper
                List<Course> allCourses = courseDAO.getAllCourses();
                if (!allCourses.isEmpty()) {
                    enrollmentDAO.enrollStudent("grace.hopper@student.edu", allCourses.get(0).getId());
                    if (allCourses.size() > 1) {
                        enrollmentDAO.enrollStudent("grace.hopper@student.edu", allCourses.get(1).getId());
                    }
                    if (allCourses.size() > 3) {
                        enrollmentDAO.enrollStudent("grace.hopper@student.edu", allCourses.get(3).getId());
                    }
                }

                System.out.println("[DatabaseSeeder] Successfully seeded demo database with accounts and courses!");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseSeeder] Warning: Could not seed database: " + e.getMessage());
        }
    }
}
