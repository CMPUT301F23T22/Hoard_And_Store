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
    private FilterCriteria filterCriteria;

    public SortAdapter() {
        this.sortOptions = Arrays.asList("Date", "Description", "Make", "Estimated Value");
        // this is to just inialize all the values
        this.sortOptionsState = new HashMap<>();

        //this keeps track of what the user wants for sorting
        this.sortOptionsEnabled = new HashMap<>();

        // Initialize the state of all options to "ascending"
        for (String option : sortOptions) {
            sortOptionsState.put(option, "ascending");
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
            sortOptionsState = filterCriteria.getSortOptions();
            sortOptionsEnabled = filterCriteria.getSortOptions();

            // Check if the current option is in the sortOptionsState
            if (sortOptionsState.containsKey(option)) {
                String currentState = sortOptionsState.get(option);
                holder.sortType.setText(currentState);

                // Check if the current option is in the sortOptionsEnabled
                if (sortOptionsEnabled.containsKey(option)) {
                    holder.sortCheckBox.setChecked(true);
                }
            }
        }

        holder.sortType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle between "ascending" and "descending" when the button is clicked
                String currentState = sortOptionsState != null ? sortOptionsState.get(option) : null;
                if (currentState != null) {
                    String newState = currentState.equals("ascending") ? "descending" : "ascending";
                    sortOptionsState.put(option, newState);
                    holder.sortType.setText(newState);
                } else {
                    // Handle the case when currentState is null
                    String newState =
                    sortOptionsState.put(option, newState);
                    holder.sortType.setText(newState);
                }
            }
        });
        holder.sortCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.sortCheckBox.isChecked()) {
                    // if it's checked, we add to the options enabled map
                    String currentState = sortOptionsState.get(option);
                    sortOptionsEnabled.put(option, currentState);
                } else {
                    // it's unchecked, so remove it from enabled
                    sortOptionsEnabled.remove(option);
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

    // our database stores the fields in camelCase however we display them to end user differently
    public static String toCamelCase(String input, String delimiter) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split(delimiter);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                result.append(word.toLowerCase()); // Convert the first word to lowercase
            } else {
                result.append(word.substring(0, 1).toUpperCase()); // Capitalize the first letter
                result.append(word.substring(1).toLowerCase()); // Convert the rest to lowercase
            }
        }

        return result.toString();
    }
}
