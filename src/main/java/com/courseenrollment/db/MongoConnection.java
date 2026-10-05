package com.courseenrollment.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class managing the MongoDB connection pool and database instance.
 */
public class MongoConnection {

    private static final String DEFAULT_CONNECTION_URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "online_course_db";

    private static volatile MongoConnection instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoConnection() {
        // Suppress verbose MongoDB logs in console
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

        String connectionUri = System.getProperty("mongodb.uri", DEFAULT_CONNECTION_URI);
        ConnectionString connectionString = new ConnectionString(connectionUri);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(5, TimeUnit.SECONDS)
                               .readTimeout(10, TimeUnit.SECONDS))
                .applyToClusterSettings(builder ->
                        builder.serverSelectionTimeout(5, TimeUnit.SECONDS))
                .build();

        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase(DATABASE_NAME);

        // Ensure necessary indexes
        initializeIndexes();
    }

    public static MongoConnection getInstance() {
        if (instance == null) {
            synchronized (MongoConnection.class) {
                if (instance == null) {
                    instance = new MongoConnection();
                }
            }
        }
        return instance;
    }

    private void initializeIndexes() {
        try {
            // Unique index on email in "users" collection
            MongoCollection<Document> users = database.getCollection("users");
            users.createIndex(Indexes.ascending("email"), new IndexOptions().unique(true));

            // Unique compound index on (userEmail, courseId) in "enrollments" collection
            MongoCollection<Document> enrollments = database.getCollection("enrollments");
            enrollments.createIndex(Indexes.compoundIndex(
                    Indexes.ascending("userEmail"),
                    Indexes.ascending("courseId")
            ), new IndexOptions().unique(true));

        } catch (Exception e) {
            System.err.println("Warning: Could not initialize indexes (MongoDB might still be starting up): " + e.getMessage());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    /**
     * Checks if MongoDB is reachable.
     */
    public boolean ping() {
        try {
            Document pingCommand = new Document("ping", 1);
            database.runCommand(pingCommand);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
