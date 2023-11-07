package com.example.hoard;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ItemDBController {
    private static ItemDBController instance;
    private ItemDB itemDB;

    private ItemDBController() {
        itemDB = new ItemDB(new ItemDBConnector());
    }

    // chatgpt: to make a singleton we only ever want one instance here
    // prompts: Need to only have one instance of a class how can i do this in java
    // Replied with pesudo code on how to do this
    public static ItemDBController getInstance() {
        if (instance == null) {
            synchronized (ItemDBController.class) {
                if (instance == null) {
                    instance = new ItemDBController();
                }
            }
        }
        return instance;
    }

    public void loadItems(final DataLoadCallback callback, final FilterCriteria filterCriteria) {
        if (filterCriteria != null) {
            itemDB.filter(filterCriteria)
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Item> filteredItems = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    double estimatedValue = (double) data.get("estimatedValue");
                                    Timestamp timestamp = (Timestamp) data.get("dateOfAcquisition");
                                    Date dateOfAcquisition = timestamp.toDate();
                                    String comment = (String) data.get("comment");
                                    String serialNumber = (String) data.get("serialNumber");
                                    String model = (String) data.get("model");
                                    String make = (String) data.get("make");
                                    String briefDescription = (String) data.get("briefDescription");
                                    String itemID = (String) data.get("itemID");
                                    ArrayList<Tag> tags = new ArrayList<Tag>();
                                    // convert tags from hash map to tag objects
                                    if (document.contains("tags")) { //TODO check for null array in tag list
                                        List<Map<String, Object>> tagsList = (List<Map<String, Object>>) document.getData().get("tags");
                                        for (Map<String, Object> tagMap : tagsList) {
                                            String tagName = (String) tagMap.get("tagName");
                                            String tagColor = (String) tagMap.get("tagColor");
                                            String tagID = (String) tagMap.get("tagID");
                                            Tag tag = new Tag(tagName,tagColor,tagID);
                                            tags.add(tag);
                                        }
                                    }

                                    Item item = new Item(dateOfAcquisition, briefDescription, make, model, serialNumber, estimatedValue, comment,itemID,tags);
                                    filteredItems.add(item);

                                }
                                callback.onDataLoaded(filteredItems);
                            } else {
                                // Handle the error when fetching data
                                // You can show an error message or take appropriate action
                            }
                        }
                    });
        } else {
            itemDB.getAllItems().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //                        String iteml = document.getId();
                            Map<String, Object> data = document.getData();
                            double estimatedValue = (double) data.get("estimatedValue");
                            Timestamp timestamp = (Timestamp) data.get("dateOfAcquisition");
                            Date dateOfAcquisition = timestamp.toDate();
                            String comment = (String) data.get("comment");
                            String serialNumber = (String) data.get("serialNumber");
                            String model = (String) data.get("model");
                            String make = (String) data.get("make");
                            String briefDescription = (String) data.get("briefDescription");
                            String itemID = (String) data.get("itemID");
                            ArrayList<Tag> tags = new ArrayList<Tag>();
                            // convert tags from hash map to tag objects
                            if (document.contains("tags")) { //TODO check for null array in tag list
                                List<Map<String, Object>> tagsList = (List<Map<String, Object>>) document.getData().get("tags");
                                for (Map<String, Object> tagMap : tagsList) {
                                    String tagName = (String) tagMap.get("tagName");
                                    String tagColor = (String) tagMap.get("tagColor");
                                    String tagID = (String) tagMap.get("tagID");
                                    Tag tag = new Tag(tagName,tagColor,tagID);
                                    tags.add(tag);
                                }
                            }
                            Item item = new Item(dateOfAcquisition, briefDescription, make, model, serialNumber, estimatedValue, comment,itemID,tags);
                            items.add(item);
                        }
                        callback.onDataLoaded(items);
                    } else {
                        // Handle the error when fetching data
                        // You can show an error message or take appropriate action
                    }
                }

        });
    }
    public void getTotalValue(final Consumer<Double> callback) {
        itemDB.getAllItems().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    double totalValue = 0.0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        if (data.containsKey("estimatedValue")) {
                            Object estimatedValueObj = data.get("estimatedValue");
                            if (estimatedValueObj instanceof Number) {
                                totalValue += ((Number) estimatedValueObj).doubleValue();
                            }
                        }
                    }
                    callback.accept(totalValue);
                } else {
                    // TODO: Handle the error when fetching data
                    callback.accept(0.0);
                }
            }
        });
    }
    public void addItem(Item item,OnCompleteListener<Void> onCompleteListener){
        itemDB.addItem(item,onCompleteListener);
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