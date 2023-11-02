package com.example.hoard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        deleteItemByField(itemsCollection, "serialNumber", item.getSerialNumber());
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
    public Task<QuerySnapshot> filter(FilterCriteria filterCriteria) {
        Query query = constructDynamicQuery(filterCriteria);
        return query.get();
    }

    public Query constructDynamicQuery(FilterCriteria filterCriteria) {
        Query query = itemsCollection; // Start with the base query

        if (filterCriteria != null) {
            if(filterCriteria.getMakes() != null) {
                for(String make: filterCriteria.getMakes()) {
                    query = query.whereEqualTo("make", make.toLowerCase());
                }
            }

        }
        return query;
    }
}