package com.courseenrollment.dao;

import com.courseenrollment.db.MongoConnection;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.Enrollment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * Data Access Object for Enrollment entities in MongoDB.
 * Implements MongoDB Aggregation $lookup pipelines to join enrollments with courses and users.
 */
public class EnrollmentDAO {

    private final MongoCollection<Document> collection;

    public EnrollmentDAO() {
        this.collection = MongoConnection.getInstance().getCollection("enrollments");
    }

    /**
     * Enrolls a student in a course.
     * @param userEmail Student's email
     * @param courseId ObjectId of the course
     * @return true if successfully enrolled, false if already enrolled
     */
    public boolean enrollStudent(String userEmail, ObjectId courseId) {
        if (userEmail == null || courseId == null) {
            return false;
        }

        String normalizedEmail = userEmail.trim().toLowerCase();

        if (isEnrolled(normalizedEmail, courseId)) {
            return false;
        }

        Document doc = new Document("userEmail", normalizedEmail)
                .append("courseId", courseId)
                .append("enrolledAt", new Date());

        collection.insertOne(doc);
        return true;
    }

    /**
     * Checks whether a student is already enrolled in a specific course.
     */
    public boolean isEnrolled(String userEmail, ObjectId courseId) {
        if (userEmail == null || courseId == null) {
            return false;
        }

        Document existing = collection.find(Filters.and(
                Filters.eq("userEmail", userEmail.trim().toLowerCase()),
                Filters.eq("courseId", courseId)
        )).first();

        return existing != null;
    }

    /**
     * Returns a set of all Course ObjectIds the student is currently enrolled in.
     * Used for O(1) button state lookups on Course cards.
     */
    public Set<ObjectId> getEnrolledCourseIds(String userEmail) {
        Set<ObjectId> ids = new HashSet<>();
        if (userEmail == null) return ids;

        for (Document doc : collection.find(Filters.eq("userEmail", userEmail.trim().toLowerCase()))) {
            ObjectId courseId = doc.getObjectId("courseId");
            if (courseId != null) {
                ids.add(courseId);
            }
        }
        return ids;
    }

    /**
     * Retrieves all enrollments for a student, joining the full Course details via a MongoDB $lookup aggregation.
     * Equivalent to: SELECT * FROM enrollments JOIN courses ON enrollments.courseId = courses._id WHERE userEmail = ?
     */
    public List<Enrollment> getEnrolledCoursesForStudent(String userEmail) {
        List<Enrollment> enrollments = new ArrayList<>();
        if (userEmail == null) return enrollments;

        String normalizedEmail = userEmail.trim().toLowerCase();

        // MongoDB Aggregation Pipeline: $match -> $lookup -> $unwind -> $sort
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(Filters.eq("userEmail", normalizedEmail)),
                Aggregates.lookup("courses", "courseId", "_id", "courseDetails"),
                Aggregates.unwind("$courseDetails"),
                Aggregates.sort(Sorts.descending("enrolledAt"))
        );

        for (Document resultDoc : collection.aggregate(pipeline)) {
            ObjectId enrollmentId = resultDoc.getObjectId("_id");
            ObjectId courseId = resultDoc.getObjectId("courseId");
            Date enrolledAt = resultDoc.getDate("enrolledAt");

            Enrollment enrollment = new Enrollment(enrollmentId, normalizedEmail, courseId, enrolledAt);

            Document courseDoc = resultDoc.get("courseDetails", Document.class);
            if (courseDoc != null) {
                Course course = CourseDAO.documentToCourse(courseDoc);
                enrollment.setCourse(course);
            }

            enrollments.add(enrollment);
        }

        return enrollments;
    }

    /**
     * Retrieves all enrollments across courses created by a specific instructor.
     * Uses an aggregation pipeline joining enrollments -> courses and enrollments -> users.
     */
    public List<Enrollment> getEnrollmentsForInstructor(String instructorEmail) {
        List<Enrollment> enrollments = new ArrayList<>();
        if (instructorEmail == null) return enrollments;

        String normalizedInstructor = instructorEmail.trim().toLowerCase();

        // Pipeline: $lookup courses -> $unwind -> $match createdBy -> $lookup users -> $unwind -> $sort
        List<Bson> pipeline = Arrays.asList(
                Aggregates.lookup("courses", "courseId", "_id", "courseDetails"),
                Aggregates.unwind("$courseDetails"),
                Aggregates.match(Filters.eq("courseDetails.createdBy", normalizedInstructor)),
                Aggregates.lookup("users", "userEmail", "email", "userDetails"),
                Aggregates.unwind("$userDetails", new com.mongodb.client.model.UnwindOptions().preserveNullAndEmptyArrays(true)),
                Aggregates.sort(Sorts.descending("enrolledAt"))
        );

        for (Document resultDoc : collection.aggregate(pipeline)) {
            ObjectId enrollmentId = resultDoc.getObjectId("_id");
            String studentEmail = resultDoc.getString("userEmail");
            ObjectId courseId = resultDoc.getObjectId("courseId");
            Date enrolledAt = resultDoc.getDate("enrolledAt");

            Enrollment enrollment = new Enrollment(enrollmentId, studentEmail, courseId, enrolledAt);

            Document courseDoc = resultDoc.get("courseDetails", Document.class);
            if (courseDoc != null) {
                Course course = CourseDAO.documentToCourse(courseDoc);
                enrollment.setCourse(course);
            }

            Document userDoc = resultDoc.get("userDetails", Document.class);
            if (userDoc != null) {
                String studentName = userDoc.getString("name");
                enrollment.setStudentName(studentName != null ? studentName : studentEmail);
            } else {
                enrollment.setStudentName(studentEmail);
            }

            enrollments.add(enrollment);
        }

        return enrollments;
    }

    /**
     * Drops/removes a student's enrollment in a course.
     */
    public boolean dropEnrollment(String userEmail, ObjectId courseId) {
        if (userEmail == null || courseId == null) return false;

        long deleted = collection.deleteOne(Filters.and(
                Filters.eq("userEmail", userEmail.trim().toLowerCase()),
                Filters.eq("courseId", courseId)
        )).getDeletedCount();

        return deleted > 0;
    }

    /**
     * Deletes all enrollments for a student (used on account deletion).
     */
    public long deleteEnrollmentsByUser(String userEmail) {
        if (userEmail == null) return 0;
        return collection.deleteMany(Filters.eq("userEmail", userEmail.trim().toLowerCase())).getDeletedCount();
    }

    /**
     * Deletes all enrollments for a specific course (used when course is deleted).
     */
    public long deleteEnrollmentsByCourse(ObjectId courseId) {
        if (courseId == null) return 0;
        return collection.deleteMany(Filters.eq("courseId", courseId)).getDeletedCount();
    }

    /**
     * Counts enrollments for a specific student.
     */
    public long countEnrollmentsByUser(String userEmail) {
        if (userEmail == null) return 0;
        return collection.countDocuments(Filters.eq("userEmail", userEmail.trim().toLowerCase()));
    }

    /**
     * Counts total enrollments across all courses.
     */
    public long countTotalEnrollments() {
        return collection.countDocuments();
    }

    /**
     * Counts enrollments for a specific instructor across all their courses.
     */
    public long countEnrollmentsForInstructor(String instructorEmail) {
        return getEnrollmentsForInstructor(instructorEmail).size();
    }
}
