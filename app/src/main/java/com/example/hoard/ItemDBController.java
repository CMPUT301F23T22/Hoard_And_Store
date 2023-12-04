package com.example.hoard;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import android.net.Uri;
import android.util.Log;

/**
 * Controller for managing item database operations.
 *
 * This class serves as a mediator between the application logic and the Firestore database,
 * handling the creation, retrieval, update, and deletion of items. It utilizes the ItemDB class
 * for direct database interactions and provides a singleton instance for global access.
 *
 */
public class ItemDBController {
    private static ItemDBController instance;
    private final ItemDB itemDB;
    private final boolean isItemDBInitialized = false;

    private final User loggedInUser = UserManager.getInstance().getLoggedInUser();

    public interface OnInitializationCompleteListener {
        void onInitializationComplete();
    }

    private ItemDBController() {itemDB = new ItemDB(new ItemDBConnector()); }

    /**
     * Singleton for ItemDBController to have an ensured instance
     * across classes
     */
    public static ItemDBController getInstance() {
        // chatgpt: to make a singleton we only ever want one instance here
        // prompts: Need to only have one instance of a class how can i do this in java
        // Replied with pesudo code on how to do this
        if (instance == null) {
            synchronized (ItemDBController.class) {
                if (instance == null) {
                    instance = new ItemDBController();
                }
            }
        }
        return instance;
    }

    /**
     * Will call the login in method in itemDB which creates and inialzies the user firebase collection
     * will an items subcollection
     */
    public void login(final OnAccountActionComplete callback) {
        itemDB.initializeItemCollection(new ItemDB.OnItemCollectionInitialized() {
            @Override
            public void onItemCollectionInitialized() {
                // Notify the callback that login is complete
                callback.OnAccountActionComplete();
            }
        });
    }

    public Task<String> getUsername(){
        return itemDB.getUsername();
    }

    public interface OnAccountActionComplete {
        void OnAccountActionComplete();
    }


    /**
     * TagCounts Callback the returns a list of items for a set of selected tags
     */
    public interface TagCountsCallback {
        void onTagCountsReady(List<Item> itemWithSelectedTags);
        void onError(Exception e);
    }

    /**
     * deletes the currently signed in users account
     *
     * @return
     */
    public Task<Void> deleteAccount(){
        return itemDB.deleteAccount();
    }

    /**
     * sign outs the currently signed in users account
     */
    public void signOut(){
        itemDB.signOut();
    }

    /**
     * loads all items from db with filtering and sorting applied
     *
     * @param callback       callback to detect when the query finished
     * @param filterCriteria get filter and sort information
     * @return task
     */
    public void loadItems(final DataLoadCallbackItem callback, final FilterCriteria filterCriteria) {

        if (filterCriteria != null) {
            itemDB.getItems(filterCriteria).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> items = task.getResult();
                    List<Item> itemList = new ArrayList<>();
                    for (DocumentSnapshot document : items) {
                        Item item = documentDataToItem(document);
                        itemList.add(item);
                    }
                    callback.onDataLoaded(itemList);
                } else {
                    // Log an error if the task is unsuccessful
                    Log.e("ItemDB", "Error loading items", task.getException());
                }
            });
        } else {
            itemDB.getAllItems().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> items = task.getResult();
                    List<Item> itemList = new ArrayList<>();
                    for (DocumentSnapshot document : items) {
                        Item item = documentDataToItem(document);
                        itemList.add(item);
                    }
                    callback.onDataLoaded(itemList);
                }
            });
        }
    }

    /**
     * converts a firestore document to an item
     *
     * @param document firestore document
     * @return item
     */
    private Item documentDataToItem(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        double estimatedValue = (double) data.get("estimatedValue");
        Timestamp timestamp = (Timestamp) data.get("dateOfAcquisition");
        Date dateOfAcquisition = timestamp.toDate();
        String model = (String) data.get("model");
        String comment = (String) data.get("comment");
        String serialNumber = (String) data.get("serialNumber");
        String make = (String) data.get("make");
        String briefDescription = (String) data.get("briefDescription");
        String itemID = (String) data.get("itemID");
        List<Tag> tags = new ArrayList<>();
        if (document.contains("tags")) {
            List<Map<String, Object>> tagsList = (List<Map<String, Object>>) data.get("tags");
            if (tagsList != null) {
                for (Map<String, Object> tagMap : tagsList) {
                    String tagName = (String) tagMap.get("tagName");
                    String tagColor = (String) tagMap.get("tagColor");
                    String tagID = (String) tagMap.get("tagID");
                    Tag tag = new Tag(tagName, tagColor, tagID);
                    tags.add(tag);
                }
            }
        }

        List<String> imageUrls = new ArrayList<>();
        Object urlsObject = data.get("imageUrls");
        if (urlsObject instanceof List<?>) {
            List<?> urlsList = (List<?>) urlsObject;
            for (Object obj : urlsList) {
                if (obj instanceof String) {
                    imageUrls.add((String) obj);
                }
            }
        }

        return new Item(dateOfAcquisition, briefDescription, make, model, serialNumber, estimatedValue, comment, itemID, (ArrayList<Tag>) tags,imageUrls);
    }


    public void getTotalValue(final Consumer<Double> callback) {
        // We may need to rethink how we are getting estimated value this is very messy and touches alot of code everywhere
//        itemDB.getAllItems().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    double totalValue = 0.0;
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Map<String, Object> data = document.getData();
//                        if (data.containsKey("estimatedValue")) {
//                            Object estimatedValueObj = data.get("estimatedValue");
//                            if (estimatedValueObj instanceof Number) {
//                                totalValue += ((Number) estimatedValueObj).doubleValue();
//                            }
//                        }
//                    }
//                    callback.accept(totalValue);
//                } else {
//                    // TODO: Handle the error when fetching data
//                    callback.accept(0.0);
//                }
//            }
//        });
    }



    public void addItem(Item item, OnCompleteListener<DocumentReference> onCompleteListener) {
        itemDB.addItem(item, onCompleteListener);
    }

    /**
     * adds a new user to the firebase database and authentication
     *
     * @param user a firebase user
     * @param email entered email
     * @param userName entered user name
     *
     */
    public void addUser(FirebaseUser user, String email, String userName) {
        itemDB.addUser(user, email, userName);
    }

    /**
     * deletes the list of items
     *
     * @param items a list of items
     *
     */
    public Task<Void> bulkDeleteItems(List<Item> items) {
        return itemDB.bulkDeleteItems(items);
    }



    /**
     * deletes a single item
     *
     * @param item an item
     * @param onSuccessListener listener to handle on success
     * @param onFailureListener listener to handle on failure
     *
     *
     */
    public void deleteItem(Item item, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        itemDB.deleteItem(item)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    /**
     * edits a single item
     *
     * @param item an item
     * @param itemID UID of an item
     * @param onCompleteListener to handle what happens on complete
     *
     */
    public void editItem(String itemID, Item item, OnCompleteListener<Void> onCompleteListener) {
        itemDB.editItem(itemID, item)
                .addOnCompleteListener(onCompleteListener);
    }

    /**
     * will update the signed in users username
     *
     * @param username username of user
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<Void> updateUserName(String username){

        return itemDB.updateUserName(username);
    }


    /**
     * will update the signed in users password
     *
     * @param currentPassword current password
     * @param newPassword new password
     * @param email current email
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<Void> updateUserPassword (String currentPassword, String newPassword, String email){
        return itemDB.updateUserPassword(currentPassword, newPassword, email);
    }


    /**
     * will update the signed in users email
     * @param email current email
     * @param newEmail new email
     * @param password current password
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<Void> updateUserEmail (String email, String newEmail, String password){
        return itemDB.updateUserEmail(email, newEmail, password);
    }


    /**
     * will reauth the user
     *
     * @return Task to be continued and handle on success/oncomplete
     */
    public Task<QuerySnapshot> reauth(){
        return itemDB.setSubcollection();
    }


    /**
     * calls back with the list of items contains selected tags
     * @see TagCountsCallback
     */
    public void getTagCounts(ArrayList<Tag> selectedTagList, final TagCountsCallback callback) {
        List<Item> itemWithSelectedTags = new ArrayList<>();
        itemDB.getItemTags().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    Map<Tag, Integer> tagCounts = new HashMap<>();
                    List<Tag> itemTags = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        itemWithSelectedTags.add(documentDataToItem(document));

                    }
                    // Call the callback when the operation is complete
                    callback.onTagCountsReady(itemWithSelectedTags);
                } else {
                    // Call the callback with an error if there's an issue
                    callback.onError(task.getException());
                }
            }
        });
    }

    public String getUserDocID(){
        return itemDB.getUserDocumentId();
    }

    public CollectionReference getUserCollection(){
        return itemDB.getUserCollection();
    }
    /**
     * Uploads images for an item to firebase storage.
     *
     * @param itemId             The ID of the item.
     * @param imageUris          The URIs of the images to be uploaded.
     * @param onCompleteListener Listener to be called when the operation is complete.
     */
    public void uploadImagesAndUpdateItem(String itemId, List<String> imageUrls, List<Uri> imageUris, OnCompleteListener<Void> onCompleteListener) {
        List<UploadTask> uploadTasks = itemDB.uploadItemImages(imageUris, imageUrls);
        Tasks.whenAllSuccess(uploadTasks).addOnSuccessListener(tasks -> {
            List<Task<Uri>> downloadUrlTasks = new ArrayList<>();
            for (Object task : tasks) {
                UploadTask.TaskSnapshot snapshot = (UploadTask.TaskSnapshot) task;
                StorageReference ref = snapshot.getStorage();
                downloadUrlTasks.add(ref.getDownloadUrl());
            }
            // Wait for all download URL tasks to complete
            Tasks.whenAllSuccess(downloadUrlTasks).addOnSuccessListener(downloadUrls -> {
                // Then, call the onCompleteListener
                onCompleteListener.onComplete(Tasks.forResult(null)); // Indicate success
            }).addOnFailureListener(e -> {
                // Handle failure in getting download URLs
                onCompleteListener.onComplete(Tasks.forException(e));
            });
        }).addOnFailureListener(e -> {
            // Handle failure in uploading images
            onCompleteListener.onComplete(Tasks.forException(e));
        });
    }
}
