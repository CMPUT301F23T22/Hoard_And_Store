package com.example.hoard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
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
import java.util.Date;
import java.util.List;
/**
 * An adapter class for RecyclerView to display Item objects.
 * Extends RecyclerView.Adapter with a custom ViewHolder to bind and present Item data in a list or grid format.
 * Implements Filterable interface to provide filtering functionality.
 * This class is responsible for creating ViewHolders and binding them to their data,
 * defining the layout and behavior of each Item in the RecyclerView.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements Filterable {
    // implements filterable is not for our way of filtering or sorting this is used for the autocomplete view
    // used in activity_sort.xml

    private List<Item> itemList;
    private final RecyclerView recyclerView;
    private List<Item> filteredItems;
    private Context context;
    private double currentSum = 0;
    private SumCallBack sumCallBack;
    private boolean isSelectionMode = false;
    private ItemAdapterListener itemAdapterListener;
    private final SparseBooleanArray selectedItems = new SparseBooleanArray();

    public void setItemAdapterListener(ItemAdapterListener listener) {
        this.itemAdapterListener = listener;
    }

    /**
     * to determine if we are in the multiSelect mode or not
     *
     * @param selectionMode boolean to set the selection mode
     */
    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        if (!isSelectionMode) {
            selectedItems.clear();
            resetBackgroundColors();// Clear the selected items when selection mode is turned off
        }
        notifyDataSetChanged();
    }

    public void updateEstimatedValue() {
        for (Item item : itemList) {
            currentSum = currentSum + item.getEstimatedValue();
        }

        if (itemAdapterListener != null) {
            itemAdapterListener.onEstimatedValueChanged(currentSum);
        }
        notifyAndRecalculate();

    }

    /**
     * gets the current selection mode
     *
     * @return boolean of selectionMode
     */
    public boolean getSelectionMode() {
        return isSelectionMode;
    }

    /**
     * gets the items in the item adapter
     *
     * @return list of items
     */
    public List<Item> getItemList() {
        return itemList;
    }

    public ItemAdapter(List<Item> itemList, RecyclerView recyclerView) {
        this.itemList = itemList;
        this.filteredItems = new ArrayList<>(itemList);
        this.recyclerView = recyclerView;
    }

    /**
     * get the sum of all items in adapter
     */
    public void setSum() {
        currentSum = 0;
        for (Item item : itemList) {
            currentSum = currentSum + item.getEstimatedValue();
        }
        sumCallBack.onSumChanged(currentSum);
    }

    private void resetBackgroundColors() {
        // reset the background colours to the unselected colour
        for (int i = 0; i < itemList.size(); i++) {
            View itemView = recyclerView.getLayoutManager().findViewByPosition(i);
            if (itemView != null) {
                itemView.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item currentItem = filteredItems.get(position);
        holder.bind(selectedItems.get(position, false));

        String description = currentItem.getBriefDescription();
        int maxLength = 15;

        if (description.length() > maxLength) {
            // Find the last space before the maximum length
            int lastSpaceIndex = description.lastIndexOf(' ', maxLength);

            // If a space is found, truncate at that position; otherwise, simply truncate at the maximum length
            int truncateIndex = lastSpaceIndex != -1 ? lastSpaceIndex : maxLength;

            // Truncate the description
            description = description.substring(0, truncateIndex);

            // Add ellipsis (...) to indicate that the description has been truncated
            description = description + "...";
        } else {

            }

        holder.briefDescription.setText(description);
        // Format the date using SimpleDateFormat
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = desiredFormat.format(currentItem.getDateOfAcquisition());
        holder.dateOfAcquisition.setText(formattedDate);
        // Correct the type mismatch issue by converting the double to a string
        holder.estimatedValue.setText(String.valueOf(currentItem.getEstimatedValue()));

        holder.detailsArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Item itemAtPosition = itemList.get(adapterPosition);
                    context = view.getContext();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("SELECTED_ITEM", itemAtPosition);
                    context.startActivity(intent);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (isSelectionMode) {
                        toggleItemSelection(adapterPosition);
                        notifyItemChanged(adapterPosition);
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // we enter the mult select mode
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (!isSelectionMode) {
                        isSelectionMode = true;
                        if (selectionModeCallback != null) {
                            selectionModeCallback.onSelectionModeChanged(true);
                        }
                    }
                    toggleItemSelection(adapterPosition);
                    notifyItemChanged(adapterPosition);
                }
                return true;
            }
        });
    }

    private void toggleItemSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        selectionModeCallback.onSelectionModeChanged(true);
    }

    /**
     * returns the selected of items
     *
     * @return list of items
     */
    public List<Item> getSelectedItems() {
        List<Item> selectedItemsList = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedItemsList.add(itemList.get(selectedItems.keyAt(i)));
        }
        return selectedItemsList;
    }

    /**
     * clear all selected items
     */
    public void clearSelectedItems() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    /**
     * selects an item using its unique id
     *
     * @param itemID items id
     */
    public void selectItem(String itemID) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getItemID().equals(itemID)) {
                selectedItems.put(i, true);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public interface SumCallBack {
        /**
         * create a callback for when the itemSum is changed
         *
         * @param sum double: for the sum
         */
        void onSumChanged(double sum);
    }

    public interface SelectionModeCallback {
        void onSavedInstanceState(Bundle outState);

        /**
         * detect slectionMode changed
         *
         * @param selectionMode indicating the selection mode
         */
        void onSelectionModeChanged(boolean selectionMode);
    }

    private SelectionModeCallback selectionModeCallback;

    /**
     * callback to detect selection mode changed
     *
     * @param callback SelectionModeCallback to determine the slectionMode
     */
    public void setSelectionModeCallback(SelectionModeCallback callback) {

        this.selectionModeCallback = callback;
    }

    /**
     * callback to detect sum changed
     *
     * @param callback SumCallBack to determine the sum
     */
    public void setSumCallback(SumCallBack callback) {
        this.sumCallBack = callback;
    }


    /**
     * get the number of items in adapter
     *
     * @return int: itemList count
     */
    @Override
    public int getItemCount() {
        return filteredItems.size();
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

    public void filterByDateRange(Date startDate, Date endDate) {
        List<Item> tempFilteredList = new ArrayList<>();
        for (Item item : itemList) {
            Date itemDate = item.getDateOfAcquisition();
            if (!itemDate.before(startDate) && !itemDate.after(endDate)) {
                tempFilteredList.add(item);
            }
        }

        filteredItems = tempFilteredList;
        notifyDataSetChanged();
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

        void bind(boolean isSelected) {
            itemView.setBackgroundColor(isSelected ? Color.LTGRAY : Color.TRANSPARENT);
        }
    }

    /**
     * add item to the adpater
     *
     * @param item item to be added
     */
    public void addItem(Item item) {
        itemList.add(item);
        filteredItems.add(item);
        setSum();
        notifyDataSetChanged();
    }

    /**
     * remove an item from the adapter
     *
     * @param position position of item to remove
     */
    public void removeItem(int position) {
        if (position >= 0 && position < itemList.size()) {
            itemList.remove(position);
            filteredItems.remove(position);
            notifyItemRemoved(position);
        }
        setSum();
    }

    public int getsize() {
        return itemList.size();
    }

    /**
     * get the number of items that are selected
     *
     * @return int selection items count
     */
    public int getItemsSelectedCount() {
        return selectedItems.size();
    }

    /**
     * get an item using itemAdapter position
     *
     * @param position int for the item adapter position
     */
    public Item getItem(int position) {
        if (position >= 0 && position < itemList.size()) {
            return itemList.get(position);
        }
        return null; // Return null or handle the out-of-bounds case as needed
    }

    /**
     * set the items in the itemAdapter
     *
     * @param newItems list of new items
     */
    public void setItems(List<Item> newItems) {
        itemList = newItems;
        filteredItems = new ArrayList<>(itemList);
        updateEstimatedValue();
        notifyDataSetChanged();

    }

    public double getSum() {
        double sum = 0.0;
        for (Item item : itemList) {
            sum = sum + item.getEstimatedValue();
        }
        return sum;
    }

    // Notify data set changes, and recalculate sum
    public void notifyAndRecalculate() {
        notifyDataSetChanged();
        // Calculate the sum after the change
        double sum = currentSum;
        // You can use this sum for any purpose, like displaying it in your UI.
    }

    public interface ItemAdapterListener {
        void onEstimatedValueChanged(double sum);
    }
}