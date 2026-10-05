# 🎓 Online Course Enrollment System

A modern, desktop-native **Online Course Enrollment System** designed as a college mini-project. Built using **Java 17+ (Java Swing)** with the **FlatLaf** design system, connected to a local **MongoDB** database via the official MongoDB Java Sync Driver, and secured with **BCrypt** password hashing.

---

## 🌟 Key Features & Highlights

- 🎨 **Modern FlatLaf Look & Feel**: Beautiful dark and light theme switching with custom accent palettes, rounded cards, and smooth transitions (no ugly default Swing components or pop-up frame clutter).
- 🔄 **Single JFrame with `CardLayout`**: Smooth, flicker-free navigation across all application screens:
  1. **Splash Screen**: Animated glowing logo and progress bar auto-advancing to Auth.
  2. **Auth Screen**: Login & Signup with real-time validation, password toggle, role selector, and instant Demo Account quick-fills.
  3. **Welcome Transition**: Personalized "Hello, \<Name\>!" greeting screen.
  4. **Dashboard**: Role-aware sidebar layout with live metrics, catalog browsing, enrollments, and profile management.
- 🗄️ **MongoDB Official Java Driver (No SQL/JDBC)**: Clean, non-blocking NoSQL data access layer for `users`, `courses`, and `enrollments` collections.
- 🧬 **True OOP Hierarchy & Polymorphism**:
  - `User` (abstract) $\rightarrow$ `Student` and `Instructor` (with `getRole()` implementation).
  - `Course` (abstract) $\rightarrow$ `ProgrammingCourse` and `DesignCourse` (with distinct badge colors, specialization tags, and duration metrics).
- 🔗 **MongoDB Aggregation Pipelines (`$lookup`)**: Queries "My Enrollments" and "Instructor Rosters" by performing multi-collection `$lookup` joins equivalent to relational SQL joins.
- 🔒 **Secure BCrypt Hashing**: All passwords are automatically salted and hashed using `jbcrypt` (12 rounds) before persisting to MongoDB.
- 🗑️ **Cascading Account & Course Deletion**: Deleting an account safely cascades to remove all associated student enrollments and instructor courses.
- ⚡ **Auto-Seeding Demo Database**: Automatically detects an empty database on first launch and seeds sample instructors, students, and courses for testing.

---

## 🏗️ Architecture & Package Structure

```
d:\Microproj_EXTC\Java/
├── pom.xml                                  # Maven dependencies & build configuration
├── run.bat                                  # Windows 1-click launcher
├── mvnw.cmd                                 # Maven wrapper launcher
├── README.md                                # Project documentation
└── src/
    ├── main/java/com/courseenrollment/
    │   ├── App.java                         # Main application entry point
    │   ├── db/
    │   │   ├── MongoConnection.java         # Singleton Mongo client & index provider
    │   │   └── DatabaseSeeder.java          # Initial sample data seeder
    │   ├── model/
    │   │   ├── User.java                    # Abstract User base class
    │   │   ├── Student.java                 # Concrete Student (role: "Student")
    │   │   ├── Instructor.java              # Concrete Instructor (role: "Instructor")
    │   │   ├── Course.java                  # Abstract Course base class
    │   │   ├── ProgrammingCourse.java       # Programming Course (type: "Programming")
    │   │   ├── DesignCourse.java            # Design Course (type: "Design")
    │   │   └── Enrollment.java              # Enrollment entity with joined course data
    │   ├── dao/
    │   │   ├── UserDAO.java                 # User registration & BCrypt verification
    │   │   ├── CourseDAO.java               # Course CRUD, regex search, type filters
    │   │   └── EnrollmentDAO.java           # Enrollments + $lookup aggregation pipelines
    │   └── ui/
    │       ├── MainFrame.java               # Single-window CardLayout navigation
    │       ├── ThemeManager.java            # FlatLaf Dark/Light themes & color palette
    │       ├── SplashPanel.java             # Animated intro splash screen
    │       ├── LoginSignupPanel.java        # Tabbed authentication screen
    │       ├── WelcomeTransitionPanel.java  # Personalized greeting transition
    │       ├── DashboardPanel.java          # Role-aware sidebar dashboard
    │       ├── OverviewPanel.java           # Dashboard metrics & quick actions
    │       ├── CourseCard.java              # Reusable course card UI component
    │       ├── CoursePanel.java             # Catalog browser with search & "Add Course"
    │       ├── MyEnrollmentsPanel.java      # Student enrollments & drop course action
    │       ├── InstructorEnrollmentsPanel.java # Instructor student roster table
    │       ├── ProfilePanel.java            # Account info & cascading deletion
    │       ├── WrapLayout.java              # Fluid responsive layout for card grids
    │       └── components/
    │           ├── RoundedPanel.java        # Card panel with configurable rounded corners
    │           ├── CustomButton.java        # FlatLaf semantic button with hover effects
    │           ├── StatCard.java            # Dashboard metric summary card
    │           └── ToastNotification.java   # Floating glassmorphic toast notification
    └── test/java/com/courseenrollment/dao/
        └── DAOTest.java                     # Comprehensive JUnit 5 integration tests
```

---

## 📊 MongoDB Data Model

Database Name: `online_course_db` (running on `mongodb://localhost:27017`)

### 1. `users` Collection
```json
{
  "_id": ObjectId("..."),
  "name": "Dr. Alan Turing",
  "email": "alan.turing@college.edu",
  "passwordHash": "$2a$12$e8F...",
  "role": "Instructor",
  "createdAt": ISODate("2026-09-18T10:00:00Z")
}
```

### 2. `courses` Collection
```json
{
  "_id": ObjectId("..."),
  "title": "Full-Stack Java 21 & Spring Boot",
  "description": "Master enterprise Java, Spring Boot 3, REST APIs, and MongoDB.",
  "type": "Programming",
  "createdBy": "alan.turing@college.edu",
  "instructorName": "Dr. Alan Turing",
  "durationHours": 45,
  "tag": "Java / Spring",
  "createdAt": ISODate("2026-09-18T10:00:00Z")
}
```

### 3. `enrollments` Collection
```json
{
  "_id": ObjectId("..."),
  "userEmail": "grace.hopper@student.edu",
  "courseId": ObjectId("..."),
  "enrolledAt": ISODate("2026-09-18T10:30:00Z")
}
```

---

## 🔑 Pre-Seeded Demo Accounts

You can use the **Quick Test** buttons on the login screen or enter these credentials:

| Role | Name | Email | Password |
| :--- | :--- | :--- | :--- |
| **Instructor** | Dr. Alan Turing | `alan.turing@college.edu` | `password123` |
| **Instructor** | Prof. Ada Lovelace | `ada.lovelace@college.edu` | `password123` |
| **Student** | Grace Hopper | `grace.hopper@student.edu` | `password123` |
| **Student** | Jane Doe | `jane.doe@student.edu` | `password123` |

---

## 🚀 How to Run

### Prerequisites
1. **Java 17 or higher** installed.
2. **MongoDB** running locally on default port `27017`.

### Option 1: One-Click Batch Script (Windows)
Double-click `run.bat` or execute in terminal:
```cmd
run.bat
```

### Option 2: Run Standalone JAR
```cmd
java -jar target/online-course-enrollment-1.0.0.jar
```

### Option 3: Compile and Run via Maven
```cmd
mvn clean compile exec:java
```
*(Or use `.\mvnw.cmd clean compile exec:java`)*

### Run Automated Unit & Integration Tests
```cmd
mvn test
```

---

## 🛠️ Testing Verification Summary

The project includes an automated test suite ([`DAOTest.java`](src/test/java/com/courseenrollment/dao/DAOTest.java)) that verifies:
- ✅ Secure BCrypt registration, duplicate prevention, and authentication.
- ✅ Polymorphic course instantiation (`ProgrammingCourse` / `DesignCourse`) and regex search.
- ✅ MongoDB `$lookup` aggregation joins populating course details for enrollments.
- ✅ Cascading account and course deletions.
