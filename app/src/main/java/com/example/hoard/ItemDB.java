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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
    private String userDocumentId;

    public CollectionReference getUserCollection() {
        return userCollection;
    }

    public String getUserDocumentId() {
        return userDocumentId;
    }

    public void setUserDocumentId(String userDocumentId) {
        this.userDocumentId = userDocumentId;
    }

    public Task<String> getUsername() {
        if (userCollection != null) {
            return userCollection.document(userDocumentId).get().continueWith(new Continuation<DocumentSnapshot, String>() {
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



    public ItemDB(ItemDBConnector dbConnector) {
        db = dbConnector.getDatabase();
        userCollection = db.collection("user");
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * returns document snapshot containg doc id for a certain field and value
     *
     * @param field field in firestore
     * @param value value at specified field
     * @return Task<QuerySnapshot> containing the docID
     */
    public Task<QuerySnapshot> getDocumentId(String field, String value) {
        Query query = userCollection.whereEqualTo(field, value);

        return query.get();
    }

    /**
     * sets the users subCollection
     *
     * @return Task<QuerySnapshot> containing the docID
     */
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
                            userDocumentId = document.getId();

                            // Set the subcollection for the user
                            itemsCollection = userCollection.document(userDocumentId).collection("items");
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

    public interface OnItemCollectionInitialized {
        void onItemCollectionInitialized();
    }

    /**
     * inializtxes the users item subcollection
     * @see OnItemCollectionInitialized
     */
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
                        userDocumentId = document.getId();

                        // Assuming you have a subcollection named "items"
                        itemsCollection = userCollection.document(userDocumentId).collection("items");

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
                return Collections.emptyList();
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
        if (itemsCollection == null) {
            setSubcollection();
        }

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

    /**
     * deletes the signed in users account
     *
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<Void> deleteAccount() {
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        FirebaseUser user = mAuth.getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userCollection.document(userDocumentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            tcs.setResult(null);  // Resolve the Task successfully
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                            tcs.setException(e);  // Set an exception for the Task
                                        }
                                    });

                            Log.d(TAG, "User account deleted.");
                        } else {
                            tcs.setException(task.getException());  // Set an exception for the Task
                        }
                    }
                });

        return tcs.getTask();
    }



    /**
     * Signs out the current user
     */
    public void signOut(){
        mAuth.signOut();
    }

//    public Task createAccount(String email, String password, String userName) {
//        return mAuth.createUserWithEmailAndPassword(email, password);
//    }
//    public Task<Void> OLDupdateUserPassword(String password){
//        FirebaseUser user = mAuth.getCurrentUser();
//        return user.updatePassword(password);
//
//    }

//    private Task<Void> updateEmailInDatabase(String newEmail) {
//        DocumentReference userDocRef = userCollection.document(userDocumentId);
//        Task<Void> updateUsernameTask = userDocRef.update("email", newEmail);
//
//        return userCollection.document(userDocumentId).update("email", newEmail);
//    }

    /**
     * updates the signed in users password
     * @param currentPassword current password
     * @param newPassword new password
     * @param email current email
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<Void> updateUserPassword(String currentPassword, String newPassword, String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Handle the case where the user is not authenticated
            return Tasks.forException(new Exception("User not authenticated"));
        }

        // Reauthenticate the user with their current email and password
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

        return user.reauthenticate(credential)
                .continueWithTask(reauthTask -> {
                    if (reauthTask.isSuccessful()) {
                        // Reauthentication successful, update the password
                        return user.updatePassword(newPassword);
                    } else {
                        // Reauthentication failed, handle the failure
                        return Tasks.forException(reauthTask.getException());
                    }
                });
    }


    /**
     * updates the signed in users email
     * @param email current email
     * @param newEmail new email
     * @param password current password
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<Void> updateUserEmail(String email, String newEmail, String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Handle the case where the user is not authenticated
            // You might want to return a failed task or handle it differently
            return Tasks.forException(new Exception("User not authenticated"));
        }

        // Reauthenticate the user with their current email and password
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        return user.reauthenticate(credential)
                .continueWithTask(reauthTask -> {
                    if (reauthTask.isSuccessful()) {
                        return user.verifyBeforeUpdateEmail(newEmail);
                    } else {
                        // Reauthentication failed, handle the failure
                        // You might want to return a failed task or handle it differently
                        return Tasks.forException(reauthTask.getException());
                    }
                });
    }


    /**
     * updates the signed in users user name
     * @param username current username
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<Void> updateUserName(String username) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // Handle the case where the user is not authenticated
            // You might want to return a failed task or handle it differently
            return Tasks.forException(new Exception("User not authenticated"));
        }

        // Update the user's display name in Firebase Auth
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        Task<Void> updateProfileTask = user.updateProfile(profileUpdates);

        // Update the username in the Firestore database
        DocumentReference userDocRef = userCollection.document(userDocumentId);
        Task<Void> updateUsernameTask = userDocRef.update("userName", username);

        // Combine the tasks using Tasks.whenAll
        return Tasks.whenAll(updateProfileTask, updateUsernameTask);
    }

    public Task<QuerySnapshot> getItemTags() {
        return itemsCollection.get();
    }

}