package com.example.hoard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A database helper class for managing the 'items' collection in Firebase Firestore.
 *
 */
public class ItemDB {
    private final FirebaseFirestore db;
    private CollectionReference itemsCollection;
    private final CollectionReference userCollection;
    private User loggedInUser = UserManager.getInstance().getLoggedInUser();
    private static final String TAG = "ItemDB";
    private FirebaseAuth mAuth;
    private String documentId;

    public Task<String> getUsername() {
        if (userCollection != null) {
            return userCollection.document(documentId).get().continueWith(new Continuation<DocumentSnapshot, String>() {
                @Override
                public String then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Assuming "username" is a field in your Firestore document
                            String username = document.getString("userName");
                            if (username != null) {
                                // Return the username
                                return username;
                            } else {
                                // Handle the case where "username" is not present in the document
                                return "Username not found in the document";
                            }
                        } else {
                            // Handle the case where the document does not exist
                            return "Document does not exist";
                        }
                    } else {
                        // Handle the exception
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                        return "Error fetching username";
                    }
                }
            });
        } else {
            // Handle the case where itemsCollection is null
            return Tasks.forException(new NullPointerException("itemsCollection is null"));
        }
    }


    public interface InitializationCallback {
        void onInitializationComplete(CollectionReference itemsCollection);
        void onInitializationFailure(Exception e);
    }
    public ItemDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        userCollection = db.collection("user");
        mAuth = FirebaseAuth.getInstance();

//        String uid = mAuth.getUid();
//        setSubcollection(uid);

//        // Assuming UserManager provides the currently logged-in user
//        User loggedInUser = UserManager.getInstance().getLoggedInUser();
//
//        if (loggedInUser != null) {
//            initializeUserCollection(loggedInUser.getUsername());
//        } else {
//
//        }
    }
//    public Task<QuerySnapshot> login(final FirebaseUser user) {
//        // Perform any actions needed for "login"
//        // For example, perform a query and set a subcollection
//
//        // Example: Perform a query to get a document ID based on a field and value
//        String uid = user.getUid();
//        return getDocumentId("uid", uid)
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot querySnapshot) {
//                        // Assuming there's only one document matching the query
//                        if (!querySnapshot.isEmpty()) {
//                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
//                            String documentId = document.getId();
//
//                            // Set the subcollection for the user
//                            setSubcollection(documentId);
//                        } else {
//                            // Handle the case where no matching document is found
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Handle failure if needed
//                    }
//                });
//    }


    public Task<QuerySnapshot> getDocumentId(String field, String value) {
        Query query = userCollection.whereEqualTo(field, value);

        return query.get();
    }

    // Method to set a subcollection for a specific user document ID
    public Task<QuerySnapshot> setSubcollection() {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        return getDocumentId("uid", uid)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // Assuming there's only one document matching the query
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String documentId = document.getId();

                            // Set the subcollection for the user
                            itemsCollection = userCollection.document(documentId).collection("items");
                        } else {
                            // Handle the case where no matching document is found
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure if needed
                    }
                });
        // Perform any additional actions if needed
    }




//    private void initializeUserCollection(String username, ItemDBController.OnInitializationCompleteListener listener) {
//        User loggedInUser = UserManager.getInstance().getLoggedInUser();
//        getDocumentIdByUsername(username)
//                .addOnSuccessListener(documentId -> {
//                    // Use the documentId to initialize itemsCollection
//                    loggedInUser.setDocId(documentId);
//                    itemsCollection = userCollection.document(documentId).collection("items");
//                    Log.d("User document ID: ", documentId);
//
//                    // Notify the listener that initialization is complete
//                    listener.onInitializationComplete();
//                })
//                .addOnFailureListener(e -> {
//                    // Handle failure
//                    Log.e("ItemDB", "Failed to initialize itemsCollection", e);
//                });
//    }

//    private Task<String> getDocumentIdByUsername(String username) {
//        // Query to find the user with the given username
//        Query query = userCollection.whereEqualTo("username", username);
//
//        // Return the result of the query
//        return query.get().continueWith(new Continuation<QuerySnapshot, String>() {
//            @Override
//            public String then(@NonNull Task<QuerySnapshot> task) throws Exception {
//                if (task.isSuccessful()) {
//                    QuerySnapshot querySnapshot = task.getResult();
//                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                        // Get the first document in the result set
//                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
//
//                        // Return the document ID
//                        return document.getId();
//                    } else {
//                        // No matching documents found
//                        return null;
//                    }
//                } else {
//                    // Handle the error
//                    throw task.getException();
//                }
//            }
//        });
//    }
    /**
     * add a given item
     *
     * @param item               items to be edited
     * @param onCompleteListener listener to verify
     * @return task
     */
//    public Task<Void> addItem(Item item, OnCompleteListener<Void> onCompleteListener) {
//        // Create a new document with a generated ID in the "items" collection
//        DocumentReference newItemRef = itemsCollection.document();
//        // Set the data for the new document based on the item object
//        newItemRef.set(item).addOnCompleteListener(onCompleteListener);
//        return newItemRef.set(item);
//    }

    public Task<DocumentReference> addItem(Item item, OnCompleteListener<DocumentReference> onCompleteListener) {

        return itemsCollection.add(item).addOnCompleteListener(onCompleteListener);
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

    public interface OnItemCollectionInitialized {
        void onItemCollectionInitialized();
    }

    public void initializeItemCollection(final OnItemCollectionInitialized callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        Query query = userCollection.whereEqualTo("uid", user.getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Get the first document
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        // Access the document data
                        documentId = document.getId();

                        // Assuming you have a subcollection named "items"
                        itemsCollection = userCollection.document(documentId).collection("items");

                        // Notify the callback that item collection is initialized
                        callback.onItemCollectionInitialized();
                    } else {
                        // No documents found
                        Log.d(TAG, "No documents found with the specified UID.");
                    }
                } else {
                    // Handle errors
                    Log.w(TAG, "Error getting documents: ", task.getException());
                }
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
    public Task<List<DocumentSnapshot>> getItems(FilterCriteria filterCriteria) {
        // find the current logged in user and set itemsCollection approrietly
//        if(itemsCollection == null){
//            inializeItemCollection();
//        }

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
//        Query query = userCollection.document(userId).collection("items");; // Start with the base query
        Query query = itemsCollection;

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

    public Task<String> addUser(FirebaseUser firebaseUser, String email, String userName) {
        // Extract uid from the FirebaseUser
        String uid = firebaseUser.getUid();

        // Create a new object with only the uid
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("email", email);
        userMap.put("userName", userName);

        // Add the user to userCollection and get the generated document reference
        return userCollection.add(userMap)
                .continueWithTask(addUserTask -> {
                    if (addUserTask.isSuccessful()) {
                        // User added successfully, return the generated document ID
                        return Tasks.forResult(addUserTask.getResult().getId());
                    } else {
                        // Handle failure during user addition
                        Exception exception = addUserTask.getException();
                        // You can handle the exception here
                        throw new RuntimeException("Error adding user", exception);
                    }
                });
    }

    public void removeFromUser(String field, String value){

    }

    public void deleteAccount(){
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
    }

    public void signOut(){
        mAuth.signOut();
    }

    public Task createAccount(String email, String password, String userName) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }
}