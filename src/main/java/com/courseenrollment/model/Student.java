package com.courseenrollment.model;

import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Concrete User subclass representing a Student.
 */
public class Student extends User {

    public Student() {
        super();
    }

    public Student(String name, String email, String passwordHash) {
        super(name, email, passwordHash);
    }

    public Student(ObjectId id, String name, String email, String passwordHash, Date createdAt) {
        super(id, name, email, passwordHash, createdAt);
    }

    @Override
    public String getRole() {
        return "Student";
    }
}
