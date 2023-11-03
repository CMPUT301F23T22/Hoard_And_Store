package com.example.hoard;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements Filterable {
    // implements filterable is not for our way of filtering or sorting this is used for the autocomplete view
    // used in activity_sort.xml

    private List<Item> itemList;
    private List<Item> filteredItems;
    private Context context;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.briefDescription.setText(item.getBriefDescription());
        // Format the date using SimpleDateFormat
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = desiredFormat.format(item.getDateOfAcquisition());
        holder.dateOfAcquisition.setText(formattedDate);
        // Correct the type mismatch issue by converting the double to a string
        holder.estimatedValue.setText(String.valueOf(item.getEstimatedValue()));
        // add on click to see details of an Item
        holder.detailsArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Your code for handling the item click
                context = holder.itemView.getContext();
                Intent intent = new Intent(context, ItemEditDeleteActivity.class);
                intent.putExtra("SELECTED_ITEM", item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented, return the original unfiltered data
                    results.values = itemList;
                    results.count = itemList.size();
                } else {
                    // Implement filtering logic here and set the filtered data
                    List<Item> filteredList = new ArrayList<>();
                    // Perform your filtering based on the 'constraint' CharSequence
                    // Add matching items to filteredList
                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems = (List<Item>) results.values;
                notifyDataSetChanged(); // Notify the adapter that the data has changed
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView briefDescription;
        public TextView dateOfAcquisition;
        public TextView estimatedValue;
        public ImageView detailsArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            briefDescription = itemView.findViewById(R.id.descriptionList);
            dateOfAcquisition = itemView.findViewById(R.id.dateOfAcquisitionList);
            estimatedValue = itemView.findViewById(R.id.estimatedValueList);
            detailsArrow = itemView.findViewById(R.id.detailsArrow);
        }
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < itemList.size()) {
            itemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public int getsize(){
        return itemList.size();
    }

    public Item getItem(int position) {
        if (position >= 0 && position < itemList.size()) {
            return itemList.get(position);
        }
        return null; // Return null or handle the out-of-bounds case as needed
    }
}
