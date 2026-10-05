package com.courseenrollment.model;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Model representing an Enrollment record in the "enrollments" collection.
 * Holds references to userEmail and courseId, as well as joined course/student details.
 */
public class Enrollment {
    private ObjectId id;
    private String userEmail;
    private ObjectId courseId;
    private Date enrolledAt;

    // Joined fields (populated via MongoDB $lookup aggregations)
    private Course course;
    private String studentName;

    public Enrollment() {
        this.enrolledAt = new Date();
    }

    public Enrollment(String userEmail, ObjectId courseId) {
        this.userEmail = userEmail;
        this.courseId = courseId;
        this.enrolledAt = new Date();
    }

    public Enrollment(ObjectId id, String userEmail, ObjectId courseId, Date enrolledAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt != null ? enrolledAt : new Date();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ObjectId getCourseId() {
        return courseId;
    }

    public void setCourseId(ObjectId courseId) {
        this.courseId = courseId;
    }

    public Date getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Date enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", courseId=" + courseId +
                ", enrolledAt=" + enrolledAt +
                ", course=" + (course != null ? course.getTitle() : "null") +
                '}';
    }
}
