package com.example.hoard;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    public Task<Void> deleteItem(Item item) {
        return itemsCollection.document(item.getSerialNumber())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firestore", "Error deleting document", e);
                    }
                });
    }
    public Task<Void> bulkDeleteItems(ArrayList<Item> items) {
        List<Task<Void>> deleteTasks = new ArrayList<>();

        for (Item item : items) {
            // Call the existing deleteItem method for each item
            deleteTasks.add(deleteItem(item));
        }

        // Wait for all delete tasks to complete
        Task<Void> allTasks = Tasks.whenAll(deleteTasks)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "All items successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error in deleting some items", e));

        return allTasks;
    }





    public Task<Void> editItem(Item item) {
        return itemsCollection.document(item.getSerialNumber())
                .set(item) // Overwrites the document with the new data
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("Firestore", "Error updating document", e);
                    }
                });
    }


    // Example method to retrieve all items from Firestore
    public Task<QuerySnapshot> getAllItems() {
        return itemsCollection.get();
    }
}