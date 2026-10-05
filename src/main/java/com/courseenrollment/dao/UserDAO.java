package com.courseenrollment.dao;

import com.courseenrollment.db.MongoConnection;
import com.courseenrollment.model.Instructor;
import com.courseenrollment.model.Student;
import com.courseenrollment.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for User entities (Students and Instructors) in MongoDB.
 */
public class UserDAO {

    private final MongoCollection<Document> collection;

    public UserDAO() {
        this.collection = MongoConnection.getInstance().getCollection("users");
    }

    /**
     * Registers a new user with BCrypt password hashing.
     * @param user User instance (Student or Instructor)
     * @param plainPassword Raw password to hash
     * @return true if registration succeeded, false if email already exists
     */
    public boolean registerUser(User user, String plainPassword) {
        if (user == null || user.getEmail() == null || plainPassword == null || plainPassword.isEmpty()) {
            return false;
        }

        String normalizedEmail = user.getEmail().trim().toLowerCase();

        // Check if user already exists
        if (findByEmail(normalizedEmail) != null) {
            return false;
        }

        // Hash the password securely using BCrypt with salt rounds = 12
        String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        user.setPasswordHash(passwordHash);
        user.setEmail(normalizedEmail);
        user.setCreatedAt(new Date());

        Document doc = new Document("name", user.getName().trim())
                .append("email", normalizedEmail)
                .append("passwordHash", passwordHash)
                .append("role", user.getRole())
                .append("createdAt", user.getCreatedAt());

        collection.insertOne(doc);
        ObjectId insertedId = doc.getObjectId("_id");
        user.setId(insertedId);
        return true;
    }

    /**
     * Authenticates a user by email and plain-text password using BCrypt.
     * @param email User's email
     * @param plainPassword Raw password entered by user
     * @return Authenticated User object (Student or Instructor), or null if failed
     */
    public User authenticate(String email, String plainPassword) {
        if (email == null || plainPassword == null || email.trim().isEmpty() || plainPassword.isEmpty()) {
            return null;
        }

        String normalizedEmail = email.trim().toLowerCase();
        Document doc = collection.find(Filters.eq("email", normalizedEmail)).first();

        if (doc == null) {
            return null;
        }

        String storedHash = doc.getString("passwordHash");
        if (storedHash == null || !BCrypt.checkpw(plainPassword, storedHash)) {
            return null;
        }

        return documentToUser(doc);
    }

    /**
     * Retrieves a user by their email address.
     * @param email User email
     * @return Polymorphic User instance (Student or Instructor) or null
     */
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        Document doc = collection.find(Filters.eq("email", email.trim().toLowerCase())).first();
        if (doc == null) {
            return null;
        }

        return documentToUser(doc);
    }

    /**
     * Deletes a user by email address.
     * @param email User email
     * @return true if a user document was deleted
     */
    public boolean deleteUser(String email) {
        if (email == null) return false;
        long deletedCount = collection.deleteOne(Filters.eq("email", email.trim().toLowerCase())).getDeletedCount();
        return deletedCount > 0;
    }

    /**
     * Retrieves all instructors in the system.
     */
    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        for (Document doc : collection.find(Filters.eq("role", "Instructor"))) {
            User user = documentToUser(doc);
            if (user instanceof Instructor instructor) {
                instructors.add(instructor);
            }
        }
        return instructors;
    }

    /**
     * Counts total users in the system.
     */
    public long countUsers() {
        return collection.countDocuments();
    }

    /**
     * Counts students in the system.
     */
    public long countStudents() {
        return collection.countDocuments(Filters.eq("role", "Student"));
    }

    /**
     * Counts instructors in the system.
     */
    public long countInstructors() {
        return collection.countDocuments(Filters.eq("role", "Instructor"));
    }

    /**
     * Helper method to map a MongoDB Document to a polymorphic User subclass.
     */
    public static User documentToUser(Document doc) {
        if (doc == null) return null;

        ObjectId id = doc.getObjectId("_id");
        String name = doc.getString("name");
        String email = doc.getString("email");
        String passwordHash = doc.getString("passwordHash");
        String role = doc.getString("role");
        Date createdAt = doc.getDate("createdAt");

        if ("Instructor".equalsIgnoreCase(role)) {
            return new Instructor(id, name, email, passwordHash, createdAt);
        } else {
            return new Student(id, name, email, passwordHash, createdAt);
        }
    }
}
