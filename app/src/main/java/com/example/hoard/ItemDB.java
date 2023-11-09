package com.example.hoard;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        collectionReference.whereEqualTo(fieldName, fieldValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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
                    } else {
                        Log.e("Firestore", "Query failed: ", task.getException());
                        tcs.setException(task.getException());
                    }
                });

        return tcs.getTask();
    }

    public Task<Void> deleteItem(Item item) {
        return deleteItemByField(itemsCollection, "itemID", item.getItemID());
    }


    public Task<List<DocumentSnapshot>> getAllItems() {
        return itemsCollection.get().continueWith(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                return querySnapshot.getDocuments();
            } else {
                throw task.getException();
            }
        });
    }

    public Task<List<DocumentSnapshot>> filter(FilterCriteria filterCriteria) {
        Query query = constructDynamicQuery(filterCriteria);

        return query.get().continueWith(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> sortedResults = sortResults(querySnapshot, filterCriteria);
                return sortedResults;
            } else {
                throw task.getException();
            }
        });
    }



//    public Task<QuerySnapshot> filter(FilterCriteria filterCriteria) {
//        // Construct the query with filters
//        Query query = constructDynamicQuery(filterCriteria);
//
//        // Execute the query and return the Task<QuerySnapshot>
//        return query.get().continueWith(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                List<DocumentSnapshot> sortedResults = sortResults(querySnapshot, filterCriteria);
//
//                return querySnapshot; // Return the original QuerySnapshot
//            } else {
//                // Handle query execution errors
//                throw task.getException();
//            }
//        });
//    }


    public Query constructDynamicQuery(FilterCriteria filterCriteria) {
        Query query = itemsCollection; // Start with the base query

        if (filterCriteria != null) {
            if (filterCriteria.getMakes() != null && !filterCriteria.getMakes().isEmpty()) {
                // Create a list of makes to filter on
                List<String> makes = filterCriteria.getMakes();

                // Use the "whereIn" method to create an "OR" condition
                query = query.whereIn("make", makes);
            }
        }

        return query; // Filter applied, now sort on the client-side
    }

    public List<DocumentSnapshot> sortResults(QuerySnapshot querySnapshot, FilterCriteria filterCriteria) {
        List<DocumentSnapshot> filteredAndSortedResults = querySnapshot.getDocuments();

        Map<String, String> sortOptions = filterCriteria.getSortOptions();
        if (sortOptions != null) {
            for (Map.Entry<String, String> entry : sortOptions.entrySet()) {
                String sortField = entry.getKey();
                String sortOrder = entry.getValue();

                // Sort the results based on the provided field and direction
                filteredAndSortedResults.sort((doc1, doc2) -> {
                    Object value1 = doc1.get(sortField);
                    Object value2 = doc2.get(sortField);

                    // Use the CustomComparator for comparison
                    return new SortComparator().compare(value1, value2);
                });

                if (sortOrder.equalsIgnoreCase("descending")) {
                    Collections.reverse(filteredAndSortedResults);
                }
            }
        }

        return filteredAndSortedResults;
    }

}