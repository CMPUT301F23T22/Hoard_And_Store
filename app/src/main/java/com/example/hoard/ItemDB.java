package com.example.hoard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class ItemDB {
    private FirebaseFirestore db;
    private CollectionReference itemsCollection;

    public ItemDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        itemsCollection = db.collection("items");
    }

    // Methods to interact with Firestore for items
    // Example method to add an item to Firestore
    public void addItem(Item item, OnCompleteListener<Void> onCompleteListener) {
        // Create a new document with a generated ID in the "items" collection
        DocumentReference newItemRef = itemsCollection.document();
        // Set the data for the new document based on the item object
        newItemRef.set(item).addOnCompleteListener(onCompleteListener);
    }

    public void editItem(String itemId, Item item, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        itemsCollection.whereEqualTo("itemID", itemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            itemsCollection.document(documentId)
                                    .set(item, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Item successfully edited: " + itemId);
                                        // Call the onCompleteListener if provided
                                        if (onCompleteListener != null) {
                                            onCompleteListener.onComplete(task); // Pass the original task as it's now successful
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error editing document", e);
                                        if (onCompleteListener != null) {
                                            onCompleteListener.onComplete(task); // The task will carry the exception
                                        }
                                    });
                        }
                    } else {
                        Log.e("Firestore", "Query failed: ", task.getException());
                        if (onCompleteListener != null) {
                            onCompleteListener.onComplete(task); // The task will carry the exception
                        }
                    }
                });
    }

    public void deleteItemByField(CollectionReference collectionReference, String fieldName, Object fieldValue) {
        // Create a query to find the item based on a field and its value
        collectionReference.whereEqualTo(fieldName, fieldValue)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the document ID of the item
                                String documentId = document.getId();

                                // Delete the item by using the document ID
                                collectionReference.document(documentId)
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
                        } else {
                            Log.e("Firestore", "Query failed: " + task.getException());
                        }
                    }
                });
    }
    public void deleteItem(Item item) {
        deleteItemByField(itemsCollection, "serialNumber", item.getItemID());
    }

    // Example method to retrieve all items from Firestore
    public Task<QuerySnapshot> getAllItems() {
        return itemsCollection.get();
    }

}