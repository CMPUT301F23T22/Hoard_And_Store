package com.example.hoard;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This class is used to connect to the Firestore database and perform CRUD operations on the
 * "tags" collection.
 */
public class TagDB {
    private final FirebaseFirestore db;
    private final CollectionReference tagsCollection;
    private FirebaseAuth mAuth;
    /**
     * Constructor for the TagDB class.
     *
     * @param dbConnector The ItemDBConnector object used to connect to the Firestore database.
     */
    public TagDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        ItemDBController itemdbController = ItemDBController.getInstance();

        String userDocumentId = itemdbController.getUserDocID();
        CollectionReference userCollection = itemdbController.getUserCollection();

        userCollection.document(userDocumentId).collection("items");
        tagsCollection =userCollection.document(userDocumentId).collection("tags");
    }

    /**
     * Adds a new tag to the "tags" collection in Firestore.
     *
     * @param tag                 The tag to be added to the database.
     * @param onCompleteListener The listener to be called when the operation is complete.
     */
    public void addTag(Tag tag, OnCompleteListener<Void> onCompleteListener) {
        // Create a new document with a generated ID in the "tag" collection
        DocumentReference newItemRef = tagsCollection.document();
        // Set the data for the new document based on the item object
        newItemRef.set(tag).addOnCompleteListener(onCompleteListener);
    }

    /**
     * Gets all tags from the "tags" collection in Firestore.
     *
     * @return A Task containing a QuerySnapshot of all tags in the database.
     */
    public Task<QuerySnapshot> getAllTags() {
        return tagsCollection.get();
    }
}
