package com.example.hoard;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private Context context;

    private boolean isSelectionMode = false;

    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }
    public boolean getSelectionMode() {
        return isSelectionMode;
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
        Item currentItem = itemList.get(position);
        holder.bind(currentItem.isSelected());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (isSelectionMode) {
                        Item itemAtPosition = itemList.get(adapterPosition);
                        itemAtPosition.setSelected(!itemAtPosition.isSelected());
                        notifyItemChanged(adapterPosition);
                    }
                }
            }
        });



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
                    Intent intent = new Intent(context, ItemEditDeleteActivity.class);
                    intent.putExtra("SELECTED_ITEM", itemAtPosition);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
            if (isSelected) {
                itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }
        }
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public int getsize(){
        return itemList.size();
    }
}
