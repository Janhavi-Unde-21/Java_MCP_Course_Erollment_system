package com.courseenrollment.model;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Concrete Course subclass representing a Design, UI/UX, or Graphics Course.
 */
public class DesignCourse extends Course {
    private String primaryTool; // e.g., Figma, Adobe Photoshop, Blender, Illustrator

    public DesignCourse() {
        super();
        this.primaryTool = "General Design";
    }

    public DesignCourse(String title, String description, String createdBy, String instructorName, int durationHours, String primaryTool) {
        super(title, description, createdBy, instructorName, durationHours);
        this.primaryTool = (primaryTool != null && !primaryTool.trim().isEmpty()) ? primaryTool : "UI/UX";
    }

    public DesignCourse(ObjectId id, String title, String description, String createdBy, String instructorName, int durationHours, String primaryTool, Date createdAt) {
        super(id, title, description, createdBy, instructorName, durationHours, createdAt);
        this.primaryTool = (primaryTool != null && !primaryTool.trim().isEmpty()) ? primaryTool : "UI/UX";
    }

    @Override
    public String getCourseType() {
        return "Design";
    }

    @Override
    public String getSpecializationTag() {
        return primaryTool != null ? primaryTool : "Design";
    }

    public String getPrimaryTool() {
        return primaryTool;
    }

    public void setPrimaryTool(String primaryTool) {
        this.primaryTool = primaryTool;
    }
}
