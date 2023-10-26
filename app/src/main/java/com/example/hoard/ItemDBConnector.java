package com.example.hoard;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class ItemDBConnector {
    private FirebaseFirestore db;

    public ItemDBConnector() {
        db = FirebaseFirestore.getInstance();

        // Optionally configure Firestore settings
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // Enable local data persistence
                .build();
        db.setFirestoreSettings(settings);
    }

    public FirebaseFirestore getDatabase() {
        return db;
    }
}
