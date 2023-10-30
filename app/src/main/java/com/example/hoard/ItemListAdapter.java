package com.example.hoard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private List<Item> itemList;
    private boolean isSelectionMode = false;

    public ItemListAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO relate to UI
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item, position);
    }

    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged(); // Refresh all items to update their appearance based on the selection state
    }

    public List<Item> getSelectedItems() {
        List<Item> selectedItems = new ArrayList<>();
        for (Item item : itemList) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public int getItemCount() {
        return itemList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        // TODO initialize views of item layout

        ViewHolder(View itemView) {
            super(itemView);
            // TODO get views from item view
        }

        void bind(Item item, int position) {

            // TODO set views


            // TODO set colours from colors.xml
            itemView.setBackgroundColor(item.isSelected() ? 0xFFFF0000 : 0xFFFFFFFF); // Red if selected, white otherwise

            itemView.setOnClickListener(v -> {
                if (isSelectionMode) {
                    item.setSelected(!item.isSelected());
                    notifyItemChanged(position);
                } else {
                    // TODO Handle normal click event
                }
            });
        }
    }
}

