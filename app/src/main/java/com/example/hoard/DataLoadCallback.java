package com.example.hoard;

import java.util.List;
// got from chatgpt couldnt figure out how to deal with the asyncronous nature of firebase
// prompt how to notify UI when query is finished
// mentioned several ways and this was one of them
public interface DataLoadCallback {
    void onDataLoaded(List<Item> items);
}