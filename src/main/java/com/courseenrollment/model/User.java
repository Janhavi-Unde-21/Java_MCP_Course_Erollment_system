package com.courseenrollment.model;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Abstract base class representing a generic user in the system.
 * Subclasses {@link Student} and {@link Instructor} provide concrete roles.
 */
public abstract class User {
    protected ObjectId id;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected Date createdAt;

    public User() {
        this.createdAt = new Date();
    }

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = new Date();
    }

    public User(ObjectId id, String name, String email, String passwordHash, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt != null ? createdAt : new Date();
    }

    /**
     * Polymorphic method to get the role of the user.
     * @return "Student" or "Instructor"
     */
    public abstract String getRole();

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + getRole() + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
