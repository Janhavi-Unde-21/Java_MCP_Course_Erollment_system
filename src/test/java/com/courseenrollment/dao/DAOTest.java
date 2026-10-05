package com.courseenrollment.dao;

import com.courseenrollment.db.MongoConnection;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.DesignCourse;
import com.courseenrollment.model.Enrollment;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.ProgrammingCourse;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration and unit tests for UserDAO, CourseDAO, and EnrollmentDAO.
 */
public class DAOTest {

    private static UserDAO userDAO;
    private static CourseDAO courseDAO;
    private static EnrollmentDAO enrollmentDAO;

    private String testSuffix;
    private String testStudentEmail;
    private String testInstructorEmail;

    @BeforeAll
    public static void setUpClass() {
        userDAO = new UserDAO();
        courseDAO = new CourseDAO();
        enrollmentDAO = new EnrollmentDAO();
        assertTrue(MongoConnection.getInstance().ping(), "MongoDB must be reachable on localhost:27017");
    }

    @BeforeEach
    public void setUp() {
        testSuffix = UUID.randomUUID().toString().substring(0, 8);
        testStudentEmail = "student_" + testSuffix + "@test.edu";
        testInstructorEmail = "instructor_" + testSuffix + "@test.edu";
    }

    @AfterEach
    public void tearDown() {
        // Cleanup test data
        enrollmentDAO.deleteEnrollmentsByUser(testStudentEmail);
        for (Course c : courseDAO.getCoursesByInstructor(testInstructorEmail)) {
            enrollmentDAO.deleteEnrollmentsByCourse(c.getId());
        }
        courseDAO.deleteCoursesByInstructor(testInstructorEmail);
        userDAO.deleteUser(testStudentEmail);
        userDAO.deleteUser(testInstructorEmail);
    }

    @Test
    public void testUserRegistrationAndBCryptAuthentication() {
        // 1. Register student
        Student student = new Student("Test Student " + testSuffix, testStudentEmail, null);
        boolean registered = userDAO.registerUser(student, "securePassword123");
        assertTrue(registered, "Student should be registered successfully");
        assertNotNull(student.getId(), "User ID should be generated");

        // 2. Prevent duplicate registration
        Student duplicate = new Student("Duplicate", testStudentEmail, null);
        boolean dupResult = userDAO.registerUser(duplicate, "securePassword123");
        assertFalse(dupResult, "Duplicate email registration must fail");

        // 3. Authenticate with correct password
        User authUser = userDAO.authenticate(testStudentEmail, "securePassword123");
        assertNotNull(authUser, "Authentication should succeed with valid password");
        assertEquals("Student", authUser.getRole(), "Role should be Student");
        assertTrue(authUser instanceof Student, "User should be an instance of Student");

        // 4. Authenticate with wrong password
        User failedAuth = userDAO.authenticate(testStudentEmail, "wrongPass");
        assertNull(failedAuth, "Authentication must fail with incorrect password");
    }

    @Test
    public void testPolymorphicCourseCreationAndSearch() {
        // 1. Register instructor
        Instructor instructor = new Instructor("Prof. " + testSuffix, testInstructorEmail, null);
        userDAO.registerUser(instructor, "instructorPass");

        // 2. Create Programming Course
        ProgrammingCourse progCourse = new ProgrammingCourse(
                "Java Unit Testing " + testSuffix,
                "Hands on JUnit 5 testing",
                testInstructorEmail,
                instructor.getName(),
                15,
                "Java"
        );
        boolean pSaved = courseDAO.createCourse(progCourse);
        assertTrue(pSaved, "Programming course should be saved");
        assertNotNull(progCourse.getId());
        assertEquals("Programming", progCourse.getCourseType());

        // 3. Create Design Course
        DesignCourse designCourse = new DesignCourse(
                "Figma Prototyping " + testSuffix,
                "UI design masterclass",
                testInstructorEmail,
                instructor.getName(),
                20,
                "Figma"
        );
        boolean dSaved = courseDAO.createCourse(designCourse);
        assertTrue(dSaved, "Design course should be saved");
        assertNotNull(designCourse.getId());
        assertEquals("Design", designCourse.getCourseType());

        // 4. Search courses by query
        List<Course> searchResults = courseDAO.searchCourses("Unit Testing " + testSuffix, "All");
        assertFalse(searchResults.isEmpty(), "Should find course matching query");
        assertEquals(progCourse.getTitle(), searchResults.get(0).getTitle());
        assertTrue(searchResults.get(0) instanceof ProgrammingCourse);

        // 5. Filter courses by type
        List<Course> designResults = courseDAO.searchCourses(testSuffix, "Design");
        assertEquals(1, designResults.size());
        assertEquals("Design", designResults.get(0).getCourseType());
        assertTrue(designResults.get(0) instanceof DesignCourse);
    }

    @Test
    public void testEnrollmentAndLookupAggregationJoin() {
        // 1. Register student & instructor
        Student student = new Student("Student " + testSuffix, testStudentEmail, null);
        userDAO.registerUser(student, "pass123");

        Instructor instructor = new Instructor("Instructor " + testSuffix, testInstructorEmail, null);
        userDAO.registerUser(instructor, "pass123");

        // 2. Create Course
        ProgrammingCourse course = new ProgrammingCourse(
                "Advanced Algorithms " + testSuffix,
                "Graph algorithms & dynamic programming",
                testInstructorEmail,
                instructor.getName(),
                30,
                "Algorithms"
        );
        courseDAO.createCourse(course);

        // 3. Enroll Student
        boolean enrolled = enrollmentDAO.enrollStudent(testStudentEmail, course.getId());
        assertTrue(enrolled, "Student should be successfully enrolled");

        // 4. Check duplicate enrollment prevention
        boolean duplicateEnroll = enrollmentDAO.enrollStudent(testStudentEmail, course.getId());
        assertFalse(duplicateEnroll, "Duplicate enrollment should be rejected");

        // 5. Query enrolled courses with $lookup join
        List<Enrollment> enrollments = enrollmentDAO.getEnrolledCoursesForStudent(testStudentEmail);
        assertEquals(1, enrollments.size(), "Should have exactly 1 enrollment");

        Enrollment enr = enrollments.get(0);
        assertNotNull(enr.getCourse(), "Joined Course object must not be null ($lookup join success)");
        assertEquals(course.getTitle(), enr.getCourse().getTitle(), "Joined course title should match");
        assertEquals("Programming", enr.getCourse().getCourseType());

        // 6. Test instructor view of enrollments
        List<Enrollment> instructorRoster = enrollmentDAO.getEnrollmentsForInstructor(testInstructorEmail);
        assertFalse(instructorRoster.isEmpty(), "Instructor should see student enrollment in roster");
        assertEquals(student.getName(), instructorRoster.get(0).getStudentName());

        // 7. Drop enrollment
        boolean dropped = enrollmentDAO.dropEnrollment(testStudentEmail, course.getId());
        assertTrue(dropped, "Enrollment drop should succeed");
        assertFalse(enrollmentDAO.isEnrolled(testStudentEmail, course.getId()));
    }
}
