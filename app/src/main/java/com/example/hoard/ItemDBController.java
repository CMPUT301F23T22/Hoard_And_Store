package com.example.hoard;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public void loadItems(final DataLoadCallback callback) {
        List<Item> items = new ArrayList<>();
        itemDB.getAllItems().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

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

                        Item item = new Item(dateOfAcquisition, briefDescription, make, model, serialNumber, estimatedValue, comment);
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

    public void addItem(Item item){
        itemDB.addItem(item);
    }

    public void deleteItem(Item item) {itemDB.deleteItem(item);}

    public void editItem(Item item){
        itemDB.editItem(item);
    }
}