package com.example.hoard;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import android.util.Log;

import androidx.annotation.NonNull;

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
    private boolean isItemDBInitialized = false;

    private User loggedInUser = UserManager.getInstance().getLoggedInUser();

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
     * @param user
     * @param OnLoginCompleteListener
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

    public interface OnAccountActionComplete {
        void OnAccountActionComplete();
    }

    public void deleteAccount(){
        itemDB.deleteAccount();
    }
    public void signOut(){
        itemDB.deleteAccount();
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
        return new Item(dateOfAcquisition, briefDescription, make, model, serialNumber, estimatedValue, comment, itemID, (ArrayList<Tag>) tags);

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


    public void addUser(FirebaseUser user, String email, String userName) {
        itemDB.addUser(user, email, userName);
    }


    public Task<Void> bulkDeleteItems(List<Item> items) {
        return itemDB.bulkDeleteItems(items);
    }


    public void deleteItem(Item item, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        itemDB.deleteItem(item)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void editItem(String itemID, Item item, OnCompleteListener<Void> onCompleteListener) {
        itemDB.editItem(itemID, item)
                .addOnCompleteListener(onCompleteListener);
    }



}
