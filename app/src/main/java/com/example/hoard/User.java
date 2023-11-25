package com.example.hoard;

import java.util.UUID;

public class User {
    private String username;
    private UUID userId;
    private String documentId;

    // Required empty constructor for Firestore
    public User() {
    }

    public User(String username) {
        this.username = username;
        userId = UUID.randomUUID();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId.toString();
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setDocId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocId() {
        return documentId;
    }
}
