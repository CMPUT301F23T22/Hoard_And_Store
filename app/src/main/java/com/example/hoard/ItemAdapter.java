package com.example.hoard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
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

    // Firebase storage for image handling
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    boolean isSwitchView = true;

    private final RecyclerView recyclerView;
    private List<Item> filteredItems;
    private Context context;
    private double currentSum = 0;
    private SumCallBack sumCallBack;
    private boolean isSelectionMode = false;
    private ItemAdapterListener itemAdapterListener;
    private final SparseBooleanArray selectedItems = new SparseBooleanArray();

    private static final int LIST_ITEM = 0;
    private static final int GRID_ITEM = 1;
    private FilterCriteria filterCriteria;

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

    /**
     * Updates the total estimated value of all items in the adapter.
     * Iterates through the itemList, summing up the estimated values of each item.
     * The calculated sum is then passed to the registered itemAdapterListener.
     * Finally, it calls notifyAndRecalculate to update the UI.
     */
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

    /**
     * gets the filtered items in the item adapter
     * @param itemList list of items
     * @param recyclerView RecyclerView
     * @return list of filtered items
     */
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

    /**
     * Resets the background colors of all item views in the RecyclerView to their default state.
     * Iterates through the items in the adapter and sets the background color of each corresponding view to white.
     * This method is typically used to clear the selection visual state when exiting selection mode.
     */
    private void resetBackgroundColors() {
        // reset the background colours to the unselected colour
        for (int i = 0; i < itemList.size(); i++) {
            View itemView = recyclerView.getLayoutManager().findViewByPosition(i);
            if (itemView != null) {
                itemView.setBackgroundColor(Color.WHITE);
            }
        }
    }
    /**
     * Creates a new ViewHolder instance for the RecyclerView.
     * Inflates the layout for the item view, and returns a new ViewHolder instance with the inflated view.
     *
     * @param parent   The parent ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == LIST_ITEM){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_content, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType (int position) {
        if (filterCriteria == null){
            filterCriteria = filterCriteria.getInstance();
        }

        if (filterCriteria.getListType()){
            return LIST_ITEM;
        }else{
            return GRID_ITEM;
        }
    }

    public boolean toggleItemViewType () {
        filterCriteria.setListType(!filterCriteria.getListType());
        return filterCriteria.getListType();
    }

    public Boolean getListType(){
        if (filterCriteria == null){
            filterCriteria.getInstance();
        }
        return filterCriteria.getListType();
    }

    /**
     * Binds the data at the specified position in the itemList to the ViewHolder.
     * Sets the text of the TextViews in the ViewHolder to the corresponding data from the Item object.
     * Sets an OnClickListener on the ViewHolder's itemView to handle click events.
     * Sets an OnLongClickListener on the ViewHolder's itemView to handle long click events.
     *
     * @param holder   The ViewHolder instance to bind the data to.
     * @param position The position of the item in the itemList.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item currentItem = filteredItems.get(position);
        holder.bind(selectedItems.get(position, false));
        filterCriteria = FilterCriteria.getInstance();
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
        double estimatedValue = currentItem.getEstimatedValue();

        // Format the double value to two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedValue = decimalFormat.format(estimatedValue);

        // Set the formatted value to the TextView or wherever you want to display it
        holder.estimatedValue.setText(formattedValue);

        loadImage(currentItem,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();

                if(!isSelectionMode) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        Item itemAtPosition = itemList.get(adapterPosition);
                        context = view.getContext();
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra("SELECTED_ITEM", itemAtPosition);
                        context.startActivity(intent);
                    }
                } else {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        if (isSelectionMode) {
                            toggleItemSelection(adapterPosition);
                            notifyItemChanged(adapterPosition);
                        }
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * Called when a view has been clicked and held.
             *
             * @param view The view that was clicked and held.
             * @return true if the callback consumed the long click, false otherwise.
             */
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

    /**
     * Loads an image into the provided ViewHolder's ImageView.
     *
     * @param currentItem The current Item whose image is to be loaded.
     * @param holder      The ViewHolder where the image will be set.
     */
    private void loadImage(Item currentItem, ViewHolder holder) {
        List<String> imageUrls = currentItem.getImageUrls();
        String imagePath = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;

        if (imagePath == null || imagePath.isEmpty()) {
            holder.itemImage.setImageResource(R.drawable.defultimage); // Default image
            return;
        }

        StorageReference imageRef = storageRef.child(imagePath);

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri.toString())
                    .into(holder.itemImage);
        }).addOnFailureListener(exception -> {
            Toast.makeText(holder.itemView.getContext(), "Image could not load", Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Toggles the selection state of an item at a given position in the RecyclerView.
     * If the item is currently selected, it gets deselected, and vice versa.
     * After toggling the selection, this method also triggers a callback to notify that the selection mode may have changed.
     *
     * @param position The position of the item in the RecyclerView whose selection state is to be toggled.
     */
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

    /**
     * get the filter
     *
     * @return Filter: filter
     */
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

    /**
     * filter by date range
     *
     * @param startDate Date: start date
     */
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
        public ShapeableImageView itemImage;

        /**
         * ViewHolder constructor
         *
         * @param itemView View
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            briefDescription = itemView.findViewById(R.id.descriptionList);
            dateOfAcquisition = itemView.findViewById(R.id.dateOfAcquisitionList);
            estimatedValue = itemView.findViewById(R.id.estimatedValueList);
            itemImage = itemView.findViewById(R.id.imageView);

        }
        /**
         * Binds the data at the specified position in the itemList to the ViewHolder.
         * Sets the background color of the ViewHolder's itemView to the specified color.
         *
         * @param isSelected boolean indicating whether the item is selected or not.
         */
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

    /**
     * get the size of the item adapter
     *
     * @return int: size of the item adapter
     */
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
    /**
     * get the sum of all items in adapter
     *
     * @return double: sum of all items
     */
    public double getSum() {
        double sum = 0.0;
        for (Item item : itemList) {
            sum = sum + item.getEstimatedValue();
        }
        return sum;
    }

    /**
     * Notify data set changes, and recalculate sum
     */
    public void notifyAndRecalculate() {
        notifyDataSetChanged();
        // Calculate the sum after the change
        double sum = currentSum;
        // You can use this sum for any purpose, like displaying it in your UI.
    }

    /**
     * Interface for listener to handle adapter related events.
     */
    public interface ItemAdapterListener {
        void onEstimatedValueChanged(double sum);
    }
}