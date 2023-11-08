package com.example.hoard;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TagDBController {
    private static TagDBController instance;
    private TagDB tagDB;

    private TagDBController() {
        tagDB = new TagDB(new ItemDBConnector());
    }
    // chatgpt: to make a singleton we only ever want one instance here
    // prompts: Need to only have one instance of a class how can i do this in java
    // Replied with pesudo code on how to do this
    public static TagDBController getInstance() {
        if (instance == null) {
            synchronized (ItemDBController.class) {
                if (instance == null) {
                    instance = new TagDBController();
                }
            }
        }
        return instance;
    }
    public void loadTags(final DataLoadCallBackTag callback) {
        List<Tag> Tags = new ArrayList<>();
        tagDB.getAllTags().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // String item = document.getId();
                        Map<String, Object> data = document.getData();
                        String tagName = (String) data.get("tagName");
                        String tagColor = (String) data.get("tagColor");
                        String tagID = (String) data.get("tagID");
                        Tag tag = new Tag(tagName,tagColor,tagID);
                        Tags.add(tag);
                    }

                    callback.onDataLoaded(Tags);
                } else {
                    // Handle the error when fetching data
                    // You can show an error message or take appropriate action
                }
            }

        });
    }
    public void addTag(Tag tag, OnCompleteListener<Void> onCompleteListener){
        tagDB.addTag(tag,onCompleteListener);
    }

}
