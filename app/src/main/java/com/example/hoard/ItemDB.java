package com.example.hoard;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A database helper class for managing the 'items' collection in Firebase Firestore.
 *
 */
public class ItemDB {
    private final FirebaseFirestore db;
    private final CollectionReference itemsCollection;

    public ItemDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        itemsCollection = db.collection("items");
    }

    /**
     * add a given item
     *
     * @param item               items to be edited
     * @param onCompleteListener listener to verify
     * @return task
     */
    public Task<Void> addItem(Item item, OnCompleteListener<Void> onCompleteListener) {
        // Create a new document with a generated ID in the "items" collection
        DocumentReference newItemRef = itemsCollection.document();
        // Set the data for the new document based on the item object
        newItemRef.set(item).addOnCompleteListener(onCompleteListener);
        return newItemRef.set(item);
    }

    /**
     * Edit a given item
     *
     * @param item   items to be edited
     * @param itemId itemId to edit
     * @return task
     */
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

    /**
     * This method searches the collection for documents where 'fieldName' equals 'fieldValue'.
     * If matching documents are found, it proceeds to delete them. The method returns a Task<Void>
     * that indicates the completion status of the operation.
     *
     * @param collectionReference The Firestore CollectionReference where the deletion is to be performed.
     * @param fieldName           The name of the field to check for the given value.
     * @param fieldValue          The value to match for the specified field.
     * @return A Task<Void> representing the asynchronous delete operation.
     *         The task is successful when all deletions are successful, and it fails if any deletion fails.
     */
    public Task<Void> deleteItemByField(CollectionReference collectionReference, String fieldName, Object fieldValue) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
    
        collectionReference.whereEqualTo(fieldName, fieldValue)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> deletionTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            Task<Void> deleteTask = collectionReference.document(documentId)
                                    .delete();
                            
                            deleteTask.addOnSuccessListener(aVoid -> 
                                Log.d("Firestore", "DocumentSnapshot successfully deleted!"))
                                    .addOnFailureListener(e -> 
                                Log.w("Firestore", "Error deleting document", e));
    
                            deletionTasks.add(deleteTask);
                        }
    
                        Tasks.whenAll(deletionTasks)
                                .addOnSuccessListener(aVoid -> taskCompletionSource.setResult(null))
                                .addOnFailureListener(e -> taskCompletionSource.setException(e));
                    } else {
                        taskCompletionSource.setException(task.getException());
                    }
                });
    
        return taskCompletionSource.getTask();
    }

    /**
     * delete a single item
     *
     * @param item items to delete
     * @return task
     */
    public Task<Void> deleteItem(Item item) {
        // Assuming deleteItemByField returns a Task<Void>
        return deleteItemByField(itemsCollection, "serialNumber", item.getSerialNumber());
    }

    /**
     * delete multiple items
     *
     * @param items list of items to be deleted
     * @return task
     */
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

    /**
     * Edit a given item
     *
     * @param item items to be edited
     * @return task
     */
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


    /**
     * returns all the items in the database
     *
     * @return Task of list of document snapshots
     */
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

    /**
     * filter and sort the items if required
     *
     * @param filterCriteria the filtercriteria to sort/filter
     * @return Task of list of document snapshots
     */
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

    /**
     * This method builds a query for the 'itemsCollection' using the provided FilterCriteria.
     * It allows for filtering based on multiple criteria like 'makes', date range (start and end dates),
     * and keywords in descriptions. The method dynamically adds conditions to the base query
     * based on the presence of these criteria in the FilterCriteria object.
     *
     * @param filterCriteria The criteria to use for filtering, including makes, date range, and description keywords.
     * @return A Query object that represents the constructed Firestore query based on the provided criteria.
     */
    private Query constructDynamicQuery(FilterCriteria filterCriteria) {
        Query query = itemsCollection; // Start with the base query

        if (filterCriteria != null) {
            if (filterCriteria.getMakes() != null && !filterCriteria.getMakes().isEmpty()) {
                // Create a list of makes to filter on
                List<String> makes = filterCriteria.getMakes();

                // Use the "whereIn" method to create an "OR" condition
                query = query.whereIn("make", makes);
            }

            if (filterCriteria.getTags() != null && !filterCriteria.getTags().isEmpty()) {
                List<Map<String, Object>> tags = filterCriteria.getTags();

                // Query documents where at least one tag in the list is in the "Tags" array
                query = query.whereArrayContainsAny("tags", tags);

                Log.d("Firestore", "tags: " + tags);
            }

            if (filterCriteria.getStartDate() != null && filterCriteria.getEndDate() != null) {
                Timestamp startTimestamp = new Timestamp(filterCriteria.getStartDate());
                Timestamp endTimestamp = new Timestamp(filterCriteria.getEndDate());

                query = query.whereGreaterThanOrEqualTo("dateOfAcquisition", startTimestamp)
                        .whereLessThanOrEqualTo("dateOfAcquisition", endTimestamp);

                Log.d("Firestore", "startTimestamp: " + startTimestamp);
                Log.d("Firestore", "endTimestamp: " + endTimestamp);
            }


            if (filterCriteria.getDescriptionKeyWords() != null && !filterCriteria.getDescriptionKeyWords().isEmpty()) {
                List<String> descriptionKeyWords = filterCriteria.getDescriptionKeyWords();
                query = query.whereEqualTo("briefDescriptionList." + descriptionKeyWords.get(0), true);
//                query = query.whereArrayContainsAny("briefDescriptionList", descriptionKeyWords);
                Log.d("Firestore", "descriptionKeyWords: " + descriptionKeyWords);
            }


        }
        return query;
    }

    /**
     * sorts the items returned from firestore
     *
     * @param querySnapshot  items returned from the db
     * @param filterCriteria the filtercriteria to sort/filter
     * @return list of document snapshots
     */
    public List<DocumentSnapshot> sortResults(QuerySnapshot querySnapshot, FilterCriteria filterCriteria) {
        List<DocumentSnapshot> filteredAndSortedResults = querySnapshot.getDocuments();

        Map<String, String> sortOptions = filterCriteria.getSortOptions();
        if (sortOptions != null) {
            String sortBy = filterCriteria.getSortBy();
            String sortOrder = filterCriteria.getSortOption();

            // Sort the results based on the provided field and direction
            filteredAndSortedResults.sort((doc1, doc2) -> {
                Object value1 = doc1.get(sortBy);
                Object value2 = doc2.get(sortBy);

                // Use the CustomComparator for comparison
                return new SortComparator().compare(value1, value2);
            });

            if (sortOrder.equalsIgnoreCase("descending")) {
                Collections.reverse(filteredAndSortedResults);
            }

//            for (Map.Entry<String, String> entry : sortOptions.entrySet()) {
//                String sortField = entry.getKey();
//                String sortOrder = entry.getValue();
//
//                // Sort the results based on the provided field and direction
//                filteredAndSortedResults.sort((doc1, doc2) -> {
//                    Object value1 = doc1.get(sortField);
//                    Object value2 = doc2.get(sortField);
//
//                    // Use the CustomComparator for comparison
//                    return new SortComparator().compare(value1, value2);
//                });
//
//                if (sortOrder.equalsIgnoreCase("descending")) {
//                    Collections.reverse(filteredAndSortedResults);
//                }
//            }
        }

        return filteredAndSortedResults;
    }

}