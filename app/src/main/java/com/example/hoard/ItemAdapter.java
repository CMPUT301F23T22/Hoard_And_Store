package com.example.hoard;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
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
