package com.example.hoard;

import java.util.List;

/**
 * An interface defining a callback for when Tag data is loaded.
 * This interface is used to notify when a list of Tag objects has been loaded.
 *
 */
public interface DataLoadCallBackTag {
    void onDataLoaded(List<Tag> items);
}
