package com.example.hoard;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to connect to the Firestore database and perform CRUD operations on the
 * "tags" collection.
 */
public class TagDBController {
    private static TagDBController instance;
    private final TagDB tagDB;

    /**
     * Constructor for the TagDBController class.
     */
    private TagDBController() {
        tagDB = new TagDB(new ItemDBConnector());
    }

    // chatgpt: to make a singleton we only ever want one instance here
    // prompts: Need to only have one instance of a class how can i do this in java
    // Replied with pesudo code on how to do this

    /**
     * Gets the instance of the TagDBController class.
     *
     * @return
     */
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

    /**
     * Gets all tags from the "tags" collection in Firestore.
     *
     * @param callback The callback to be called when the operation is complete.
     */
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
                        Tag tag = new Tag(tagName, tagColor, tagID);
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

    /**
     * Adds a new tag to the "tags" collection in Firestore.
     *
     * @param tag                 The tag to be added to the database.
     * @param onCompleteListener The listener to be called when the operation is complete.
     */
    public void addTag(Tag tag, OnCompleteListener<Void> onCompleteListener) {
        if (tag != null)
            tagDB.addTag(tag, onCompleteListener);
    }

    public Task<Void> deleteTag(Tag deleteTag) {
        if (deleteTag != null) {
            // Find the document ID based on the tag UID
            return tagDB.findTagDocIdByUid(deleteTag.getTagID()).continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
                @Override
                public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Retrieve the document ID from the query result
                            String tagIdToDelete = querySnapshot.getDocuments().get(0).getId();

                            // Delete the tag using the document ID
                            return tagDB.deleteTag(tagIdToDelete);
                        } else {
                            // Tag not found based on the provided UID
                            return Tasks.forException(new Exception("Tag not found"));
                        }
                    } else {
                        // Query failed
                        Exception e = task.getException();
                        // Handle the failure
                        return Tasks.forException(e);
                    }
                }
            });
        } else {
            // Handle the case where deleteTag is null
            return Tasks.forException(new Exception("Tag is null"));
        }
    }

}
