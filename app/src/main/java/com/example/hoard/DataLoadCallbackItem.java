package com.example.hoard;

import java.util.List;

// got from chatgpt couldnt figure out how to deal with the asyncronous nature of firebase
// prompt how to notify UI when query is finished
// mentioned several ways and this was one of them
/**
 * An interface defining a callback for when Item data is loaded.
 * This interface is used to notify when a list of Item objects has been loaded.
 *
 */
public interface DataLoadCallbackItem {
    void onDataLoaded(List<Item> items);
}