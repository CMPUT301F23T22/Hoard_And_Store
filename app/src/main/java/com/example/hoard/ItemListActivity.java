package com.example.hoard;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * This is to overwrite the array adapter to get our own custom list view
 * Pretty much what we all did for assignment 1 probably
 */
public class ItemListActivity extends ArrayAdapter<Item> {
    public ItemListActivity(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

    }
}
