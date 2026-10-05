package com.courseenrollment.model;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Concrete Course subclass representing a Programming & Software Development Course.
 */
public class ProgrammingCourse extends Course {
    private String primaryLanguage; // e.g., Java, Python, C++, JavaScript

    public ProgrammingCourse() {
        super();
        this.primaryLanguage = "General";
    }

    public ProgrammingCourse(String title, String description, String createdBy, String instructorName, int durationHours, String primaryLanguage) {
        super(title, description, createdBy, instructorName, durationHours);
        this.primaryLanguage = (primaryLanguage != null && !primaryLanguage.trim().isEmpty()) ? primaryLanguage : "Java";
    }

    public ProgrammingCourse(ObjectId id, String title, String description, String createdBy, String instructorName, int durationHours, String primaryLanguage, Date createdAt) {
        super(id, title, description, createdBy, instructorName, durationHours, createdAt);
        this.primaryLanguage = (primaryLanguage != null && !primaryLanguage.trim().isEmpty()) ? primaryLanguage : "Java";
    }

    @Override
    public String getCourseType() {
        return "Programming";
    }

    @Override
    public String getSpecializationTag() {
        return primaryLanguage != null ? primaryLanguage : "Programming";
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }
}
