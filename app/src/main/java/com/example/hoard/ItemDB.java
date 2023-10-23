package com.example.hoard;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ItemDB {
    private FirebaseFirestore db;
    private CollectionReference itemsCollection;

    public ItemDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        itemsCollection = db.collection("items");
    }

    // Methods to interact with Firestore for items
    // Example method to add an item to Firestore
    public Task<Void> addItem(Item item) {
        DocumentReference newItemRef = itemsCollection.document();
        return newItemRef.set(item); // item should be a Map or a custom class
    }

    // Example method to retrieve all items from Firestore
    public Task<QuerySnapshot> getAllItems() {
        return itemsCollection.get();
    }
}