package com.example.hoard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for a RecyclerView that displays sorting options.
 * Users can choose the sorting criteria for the list of items, such as by date, description, etc.
 */
public class SortAdapter extends RecyclerView.Adapter<SortAdapter.SortViewHolder> {
    private final List<String> sortOptions;
    private final Map<String, String> sortOptionsState;
    private Map<String, String> sortOptionsEnabled;

    private final Map<String, String> userToDatabaseMapping = new HashMap<>();
    private final Map<String, String> databaseToUserMapping = new HashMap<>();
    private FilterCriteria filterCriteria;
    private int selectedPos = RecyclerView.NO_POSITION;
    private boolean isSelected = false;

    private String clickedText;

    /**
     * Constructor initializes sorting options and mappings.
     */
    public SortAdapter() {
        this.sortOptions = Arrays.asList("Date", "Description", "Make", "Estimated Value", "Tags");
        // this is to just inialize all the values
        this.sortOptionsState = new HashMap<>();

        //this keeps track of what the user wants for sorting
        this.sortOptionsEnabled = new HashMap<>();

        // Initialize the mappings
        userToDatabaseMapping.put("Date", "dateOfAcquisition");
        userToDatabaseMapping.put("Description", "briefDescription");
        userToDatabaseMapping.put("Make", "make");
        userToDatabaseMapping.put("Estimated Value", "estimatedValue");
        userToDatabaseMapping.put("Tags", "tags");

        // Create the reverse mapping for converting from database to user-friendly names
        for (Map.Entry<String, String> entry : userToDatabaseMapping.entrySet()) {
            databaseToUserMapping.put(entry.getValue(), entry.getKey());
        }

    }
    public int getPositionForItem(String str) {
        if (sortOptions != null) {
            for (int i = 0; i < sortOptions.size(); i++) {
                if (str.equals(sortOptions.get(i))) {
                    return i;
                }
            }
        }
        return RecyclerView.NO_POSITION; // Item not found
    }

    /**
     * ViewHolder for the SortAdapter.
     */
    public static class SortViewHolder extends RecyclerView.ViewHolder {
        public TextView sortTextView;
        public CheckBox sortCheckBox;
        public Button sortType;

        public SortViewHolder(View itemView) {
            super(itemView);
            sortTextView = itemView.findViewById(R.id.sortTextView);
            //sortCheckBox = itemView.findViewById(R.id.sort_check_box);
//            sortType = itemView.findViewById(R.id.sort_type);
        }
    }
    public void deselectAllItems() {
        if (selectedPos != RecyclerView.NO_POSITION) {
            int previouslySelectedPos = selectedPos;
            selectedPos = RecyclerView.NO_POSITION;
            notifyItemChanged(previouslySelectedPos);
        }
    }

    @Override
    public SortViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sort_content, parent, false);
        return new SortViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SortViewHolder holder, @SuppressLint("RecyclerView") int position) {
            String option = sortOptions.get(position);
            holder.sortTextView.setText(option);
            holder.itemView.setSelected(selectedPos == position);

            filterCriteria = FilterCriteria.getInstance();
            if (filterCriteria.getSortOptions() != null) {
                sortOptionsEnabled = filterCriteria.getSortOptions();

                boolean isSelected = sortOptionsEnabled.containsKey(userToDatabaseMapping.get(option));
                holder.itemView.setSelected(isSelected);

                // Update selectedPos based on the current item's selection state
                if (isSelected) {
                    selectedPos = position;
                }
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedText = holder.sortTextView.getText().toString();
                int clickedPos = holder.getAdapterPosition();

                // Deselect the previous item
                if (selectedPos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPos);
                }

                // Clicking a different item, update selectedPos
                String databaseName = userToDatabaseMapping.get(option);
                if (databaseName != null) {
                    // Remove the item from sortOptionsEnabled if it exists
                    if (!sortOptionsEnabled.containsKey(databaseName)) {
                        filterCriteria.setSortBy(null);
                        filterCriteria.setSortOption(null);
                    } else {
                        // If not, add the item
                        sortOptionsEnabled.put(databaseName, null);
                    }
                }
                selectedPos = clickedPos;

                // Notify dataset changed after handling the click
                notifyItemChanged(clickedPos);
            }
        });




//        holder.sortType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Toggle between "ascending" and "descending" when the button is clicked
//                String currentState = holder.sortType.getText().toString();
//                String newState = currentState.equals("Ascending") ? "Descending" : "Ascending";
//                holder.sortType.setText(newState);
//
//                //if its already checked and is in sortOptionEnabled update
//                // Use the database name to update the sortOptionsEnabled map
//                String databaseName = userToDatabaseMapping.get(option);
//                if (databaseName != null) {
//                    if (sortOptionsEnabled.containsKey(databaseName)) {
//                        sortOptionsEnabled.put(databaseName, newState);
//                    }
//                }
//            }
//        });
//        holder.sortCheckBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.sortCheckBox.isChecked()) {
//                    //get the value of the button
//                    String currentState = holder.sortType.getText().toString();
//                    String databaseName = userToDatabaseMapping.get(option);
//                    if (databaseName != null) {
//                        sortOptionsEnabled.put(databaseName, currentState);
//                    }
//                } else {
//                    // it's unchecked, so remove it from enabled
//                    String databaseName = userToDatabaseMapping.get(option);
//                    if (databaseName != null) {
//                        sortOptionsEnabled.remove(databaseName);
//                    }
//                }
//            }
//        });
    }

    public Map<String, String> getSortOptionsEnabled() {
        // we only need a way to return the enabled options
        return sortOptionsEnabled;
    }

    public String getSortBy(){
        if(filterCriteria.getSortBy()!= null){
            return filterCriteria.getSortBy();
        }
        return userToDatabaseMapping.get(clickedText);
    }

    @Override
    public int getItemCount() {
        return sortOptions.size();
    }


}
