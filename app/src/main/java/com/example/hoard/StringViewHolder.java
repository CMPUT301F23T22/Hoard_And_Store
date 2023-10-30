package com.example.hoard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class StringViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public StringViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.cityNameTextView); // Replace with your TextView ID
    }
}