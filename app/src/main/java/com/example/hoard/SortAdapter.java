package com.example.hoard;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.SortViewHolder> {
    private List<String> sortOptions;
    private Map<String, String> sortOptionsState;
    private Map<String, String> sortOptionsEnabled;

    private Map<String, String> userToDatabaseMapping = new HashMap<>();
    private Map<String, String> databaseToUserMapping = new HashMap<>();
    private FilterCriteria filterCriteria;

    public SortAdapter() {
        this.sortOptions = Arrays.asList("Date", "Description", "Make", "Estimated Value");
        // this is to just inialize all the values
        this.sortOptionsState = new HashMap<>();

        //this keeps track of what the user wants for sorting
        this.sortOptionsEnabled = new HashMap<>();

        // Initialize the state of all options to "ascending"
        // Initialize the mappings
        userToDatabaseMapping.put("Date", "date");
        userToDatabaseMapping.put("Description", "comment");
        userToDatabaseMapping.put("Make", "make");
        userToDatabaseMapping.put("Estimated Value", "estimatedValue");

        // Create the reverse mapping for converting from database to user-friendly names
        for (Map.Entry<String, String> entry : userToDatabaseMapping.entrySet()) {
            databaseToUserMapping.put(entry.getValue(), entry.getKey());
        }

    }

    public static class SortViewHolder extends RecyclerView.ViewHolder {
        public TextView sortTextView;
        public CheckBox sortCheckBox;
        public Button sortType;

        public SortViewHolder(View itemView) {
            super(itemView);
            sortTextView = itemView.findViewById(R.id.sortTextView);
            sortCheckBox = itemView.findViewById(R.id.sort_check_box);
            sortType = itemView.findViewById(R.id.sort_type);
        }
    }

    @Override
    public SortViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sort_content, parent, false);
        return new SortViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SortViewHolder holder, int position) {
        String option = sortOptions.get(position);
        holder.sortTextView.setText(option);

        filterCriteria = FilterCriteria.getInstance();
        if (filterCriteria.getSortOptions() != null) {
            sortOptionsEnabled = filterCriteria.getSortOptions();

            // Check if the current option is in the sortOptionsEnabled
            if (sortOptionsEnabled.containsKey(userToDatabaseMapping.get(option))) {
                holder.sortCheckBox.setChecked(true);
                String sortTypeText = sortOptionsEnabled.get(userToDatabaseMapping.get(option));
                holder.sortType.setText(sortTypeText);
            }

        }

        holder.sortType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle between "ascending" and "descending" when the button is clicked
                String currentState = holder.sortType.getText().toString();
                String newState = currentState.equals("Ascending") ? "Descending" : "Ascending";
                holder.sortType.setText(newState);

                //if its already checked and is in sortOptionEnabled update
                // Use the database name to update the sortOptionsEnabled map
                String databaseName = userToDatabaseMapping.get(option);
                if (databaseName != null) {
                    if (sortOptionsEnabled.containsKey(databaseName)) {
                        sortOptionsEnabled.put(databaseName, newState);
                    }
                }
            }
        });
        holder.sortCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.sortCheckBox.isChecked()) {
                    //get the value of the button
                    String currentState = holder.sortType.getText().toString();
                    String databaseName = userToDatabaseMapping.get(option);
                    if (databaseName != null) {
                        sortOptionsEnabled.put(databaseName, currentState);
                    }
                } else {
                    // it's unchecked, so remove it from enabled
                    String databaseName = userToDatabaseMapping.get(option);
                    if (databaseName != null) {
                        sortOptionsEnabled.remove(databaseName);
                    }
                }
            }
        });
    }

    public Map<String, String> getSortOptionsEnabled() {
        // we only need a way to return the enabled options
        return sortOptionsEnabled;
    }

    @Override
    public int getItemCount() {
        return sortOptions.size();
    }


}
