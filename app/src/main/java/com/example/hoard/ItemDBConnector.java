package com.example.hoard;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
/**
 * A connector class for Firebase Firestore, providing a configured instance of FirebaseFirestore.
 *
 */
public class ItemDBConnector {
    private final FirebaseFirestore db;


    public ItemDBConnector() {
        // connect to the FireStore DB
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // Enable local data persistence
                .build();
        db.setFirestoreSettings(settings);
    }

    public FirebaseFirestore getDatabase() {
        return db;
    }
}
