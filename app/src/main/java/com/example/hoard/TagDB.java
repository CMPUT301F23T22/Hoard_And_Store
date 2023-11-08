package com.example.hoard;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TagDB {
    private FirebaseFirestore db;
    private CollectionReference tagsCollection;

    public TagDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        tagsCollection = db.collection("tags");
    }
    public void addTag(Tag tag, OnCompleteListener<Void> onCompleteListener) {
        // Create a new document with a generated ID in the "tag" collection
        DocumentReference newItemRef = tagsCollection.document();
        // Set the data for the new document based on the item object
        newItemRef.set(tag).addOnCompleteListener(onCompleteListener);
    }

    // Example method to retrieve all items from Firestore
    public Task<QuerySnapshot> getAllTags() {
        return tagsCollection.get();
    }
}
