package com.example.hoard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

import java.util.ArrayList;
import java.util.Arrays;
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
    public Task<Void> addItem(Item item, OnCompleteListener<Void> onCompleteListener) {
        // Create a new document with a generated ID in the "items" collection
        DocumentReference newItemRef = itemsCollection.document();
        // Set the data for the new document based on the item object
        newItemRef.set(item).addOnCompleteListener(onCompleteListener);
        return newItemRef.set(item);
    }

    public Task<Void> editItem(String itemId, Item item) {
        // Create a new TaskCompletionSource<Void> which will allow us to return Task<Void>
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

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
                                        taskCompletionSource.setResult(null); // Set result to null to indicate success
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error editing document", e);
                                        taskCompletionSource.setException(e);
                                    });
                        }
                    } else {
                        Log.e("Firestore", "Query failed: ", task.getException());
                        taskCompletionSource.setException(task.getException());
                    }
                });

        return taskCompletionSource.getTask();
    }


    public Task<Void> deleteItemByField(CollectionReference collectionReference, String fieldName, Object fieldValue) {
        // Create a TaskCompletionSource that you can use to manually set the result of the Task
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        collectionReference.whereEqualTo(fieldName, fieldValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> deletionTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            collectionReference.document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                                        tcs.setResult(null); // Successfully deleted
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error deleting document", e);
                                        tcs.setException(e);
                                    });
                        }
                        // When all delete tasks are successful, set the TaskCompletionSource result to null
                        Tasks.whenAll(deletionTasks)
                                .addOnSuccessListener(aVoid -> taskCompletionSource.setResult(null))
                                .addOnFailureListener(e -> taskCompletionSource.setException(e));
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });

        // Return the Task from the TaskCompletionSource
        return taskCompletionSource.getTask();
    }

    public Task<Void> deleteItem(Item item) {
        // Assuming deleteItemByField returns a Task<Void>
        return deleteItemByField(itemsCollection, "serialNumber", item.getSerialNumber());
    }

    public Task<Void> bulkDeleteItems(List<Item> items) {
        List<Task<Void>> deleteTasks = new ArrayList<>();

        for (Item item : items) {
            // Add the Task<Void> returned by deleteItem to the list
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
    public Task<QuerySnapshot> filter(FilterCriteria filterCriteria) {
        Query query = constructDynamicQuery(filterCriteria);
        return query.get();
    }

    public Query constructDynamicQuery(FilterCriteria filterCriteria) {
        Query query = itemsCollection; // Start with the base query

        if (filterCriteria != null) {
            if (filterCriteria.getMakes() != null && !filterCriteria.getMakes().isEmpty()) {
                // Create a list of makes to filter on
                List<String> makes = filterCriteria.getMakes();

                // Use the "whereIn" method to create an "OR" condition
                query = query.whereIn("make", makes);
            }

            if (filterCriteria.getStartDate() != null && filterCriteria.getEndDate() != null) {
                Timestamp startTimestamp = new Timestamp(filterCriteria.getStartDate());
                Timestamp endTimestamp = new Timestamp(filterCriteria.getEndDate());

                query = query.whereGreaterThanOrEqualTo("dateOfAcquisition", startTimestamp)
                        .whereLessThanOrEqualTo("dateOfAcquisition", endTimestamp);

                Log.d("Firestore", "startTimestamp: " + startTimestamp.toString());
                Log.d("Firestore", "endTimestamp: " + endTimestamp.toString());
            }


            if (filterCriteria.getDescriptionKeyWords() != null && !filterCriteria.getDescriptionKeyWords().isEmpty()) {
                List<String> descriptionKeyWords = filterCriteria.getDescriptionKeyWords();
                query = query.whereArrayContainsAny("briefDescriptionList", descriptionKeyWords);
                Log.d("Firestore", "descriptionKeyWords: " + descriptionKeyWords.toString());
            }
        }
        return query;
    }
}