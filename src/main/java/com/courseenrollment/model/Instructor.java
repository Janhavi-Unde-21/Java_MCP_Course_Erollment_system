package com.courseenrollment.model;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Concrete User subclass representing an Instructor.
 */
public class Instructor extends User {

    public Instructor() {
        super();
    }

    public Instructor(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }

    public Instructor(ObjectId id, String name, String email, String passwordHash, Date createdAt) {
        super(id, name, email, passwordHash, createdAt);
    }

    @Override
    public String getRole() {
        return "Instructor";
    }
}
