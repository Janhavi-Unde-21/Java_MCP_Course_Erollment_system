package com.courseenrollment.model;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Abstract base class representing a Course in the system.
 * Subclasses {@link ProgrammingCourse} and {@link DesignCourse} implement concrete types.
 */
public abstract class Course {
    protected ObjectId id;
    protected String title;
    protected String description;
    protected String createdBy;       // Instructor email
    protected String instructorName;  // Display name of instructor
    protected int durationHours;      // Course duration in hours
    protected Date createdAt;

    public Course() {
        this.createdAt = new Date();
    }

    public Course(String title, String description, String createdBy, String instructorName, int durationHours) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.instructorName = instructorName;
        this.durationHours = durationHours;
        this.createdAt = new Date();
    }

    public Course(ObjectId id, String title, String description, String createdBy, String instructorName, int durationHours, Date createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.instructorName = instructorName;
        this.durationHours = durationHours;
        this.createdAt = createdAt != null ? createdAt : new Date();
    }

    /**
     * Polymorphic method to get the specific course type.
     * @return "Programming" or "Design"
     */
    public abstract String getCourseType();

    /**
     * Returns a specific badge detail or tag (e.g. programming language or design tool).
     */
    public abstract String getSpecializationTag();

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + getCourseType() + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", instructorName='" + instructorName + '\'' +
                '}';
    }
}
