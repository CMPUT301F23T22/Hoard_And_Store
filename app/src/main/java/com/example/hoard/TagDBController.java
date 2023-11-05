package com.example.hoard;

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
}
