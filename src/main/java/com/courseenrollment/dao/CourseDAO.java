package com.courseenrollment.dao;

import com.courseenrollment.db.MongoConnection;
import com.courseenrollment.model.Course;
import com.courseenrollment.model.DesignCourse;
import com.courseenrollment.model.ProgrammingCourse;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Data Access Object for Course entities in MongoDB.
 * Maps documents into polymorphic ProgrammingCourse or DesignCourse objects.
 */
public class CourseDAO {

    private final MongoCollection<Document> collection;

    public CourseDAO() {
        this.collection = MongoConnection.getInstance().getCollection("courses");
    }

    /**
     * Creates and inserts a new course document into MongoDB.
     * @param course Course entity (ProgrammingCourse or DesignCourse)
     * @return true if successful
     */
    public boolean createCourse(Course course) {
        if (course == null || course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            return false;
        }

        course.setCreatedAt(new Date());

        Document doc = new Document("title", course.getTitle().trim())
                .append("description", course.getDescription() != null ? course.getDescription().trim() : "")
                .append("type", course.getCourseType())
                .append("createdBy", course.getCreatedBy() != null ? course.getCreatedBy().trim().toLowerCase() : "")
                .append("instructorName", course.getInstructorName() != null ? course.getInstructorName().trim() : "Instructor")
                .append("durationHours", course.getDurationHours())
                .append("tag", course.getSpecializationTag())
                .append("createdAt", course.getCreatedAt());

        collection.insertOne(doc);
        ObjectId insertedId = doc.getObjectId("_id");
        course.setId(insertedId);
        return true;
    }

    /**
     * Retrieves all courses ordered by creation date descending.
     */
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        for (Document doc : collection.find().sort(Sorts.descending("createdAt"))) {
            Course c = documentToCourse(doc);
            if (c != null) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Retrieves courses filtered by type ("Programming" or "Design").
     */
    public List<Course> getCoursesByType(String type) {
        if (type == null || type.equalsIgnoreCase("All")) {
            return getAllCourses();
        }

        List<Course> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("type", type)).sort(Sorts.descending("createdAt"))) {
            Course c = documentToCourse(doc);
            if (c != null) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Searches courses with case-insensitive matching and optional type filtering.
     * @param query Search keyword
     * @param typeFilter Course type or "All"
     */
    public List<Course> searchCourses(String query, String typeFilter) {
        List<Bson> filters = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            Pattern regex = Pattern.compile(Pattern.quote(query.trim()), Pattern.CASE_INSENSITIVE);
            filters.add(Filters.or(
                    Filters.regex("title", regex),
                    Filters.regex("description", regex),
                    Filters.regex("instructorName", regex),
                    Filters.regex("tag", regex)
            ));
        }

        if (typeFilter != null && !typeFilter.equalsIgnoreCase("All") && !typeFilter.trim().isEmpty()) {
            filters.add(Filters.eq("type", typeFilter.trim()));
        }

        Bson combinedFilter = filters.isEmpty() ? new Document() :
                (filters.size() == 1 ? filters.get(0) : Filters.and(filters));

        List<Course> results = new ArrayList<>();
        for (Document doc : collection.find(combinedFilter).sort(Sorts.descending("createdAt"))) {
            Course c = documentToCourse(doc);
            if (c != null) {
                results.add(c);
            }
        }
        return results;
    }

    /**
     * Finds a single course by its MongoDB ObjectId.
     */
    public Course getCourseById(ObjectId id) {
        if (id == null) return null;
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return documentToCourse(doc);
    }

    /**
     * Retrieves all courses created by a specific instructor.
     */
    public List<Course> getCoursesByInstructor(String instructorEmail) {
        if (instructorEmail == null) return new ArrayList<>();

        List<Course> list = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("createdBy", instructorEmail.trim().toLowerCase()))
                                      .sort(Sorts.descending("createdAt"))) {
            Course c = documentToCourse(doc);
            if (c != null) {
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Deletes a course by its ID, ensuring only the owner instructor can delete it.
     */
    public boolean deleteCourse(ObjectId id, String instructorEmail) {
        if (id == null) return false;

        Bson filter;
        if (instructorEmail != null && !instructorEmail.isEmpty()) {
            filter = Filters.and(
                    Filters.eq("_id", id),
                    Filters.eq("createdBy", instructorEmail.trim().toLowerCase())
            );
        } else {
            filter = Filters.eq("_id", id);
        }

        long count = collection.deleteOne(filter).getDeletedCount();
        return count > 0;
    }

    /**
     * Deletes all courses created by an instructor (used during cascading account deletion).
     */
    public long deleteCoursesByInstructor(String instructorEmail) {
        if (instructorEmail == null) return 0;
        return collection.deleteMany(Filters.eq("createdBy", instructorEmail.trim().toLowerCase())).getDeletedCount();
    }

    /**
     * Counts total courses.
     */
    public long countCourses() {
        return collection.countDocuments();
    }

    /**
     * Counts courses by type.
     */
    public long countCoursesByType(String type) {
        return collection.countDocuments(Filters.eq("type", type));
    }

    /**
     * Counts courses created by a specific instructor.
     */
    public long countCoursesByInstructor(String instructorEmail) {
        if (instructorEmail == null) return 0;
        return collection.countDocuments(Filters.eq("createdBy", instructorEmail.trim().toLowerCase()));
    }

    /**
     * Maps a MongoDB document to a polymorphic Course subclass (ProgrammingCourse or DesignCourse).
     */
    public static Course documentToCourse(Document doc) {
        if (doc == null) return null;

        ObjectId id = doc.getObjectId("_id");
        String title = doc.getString("title");
        String description = doc.getString("description");
        String type = doc.getString("type");
        String createdBy = doc.getString("createdBy");
        String instructorName = doc.getString("instructorName");
        Integer durationHours = doc.getInteger("durationHours", 20);
        String tag = doc.getString("tag");
        Date createdAt = doc.getDate("createdAt");

        if ("Design".equalsIgnoreCase(type)) {
            return new DesignCourse(id, title, description, createdBy, instructorName,
                    durationHours != null ? durationHours : 20, tag != null ? tag : "UI/UX", createdAt);
        } else {
            return new ProgrammingCourse(id, title, description, createdBy, instructorName,
                    durationHours != null ? durationHours : 20, tag != null ? tag : "Java", createdAt);
        }
    }
}
