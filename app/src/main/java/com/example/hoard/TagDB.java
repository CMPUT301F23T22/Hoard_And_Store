package com.example.hoard;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TagDB {
    private FirebaseFirestore db;
    private CollectionReference tagsCollection;

    public TagDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        tagsCollection = db.collection("tags");
    }
}
