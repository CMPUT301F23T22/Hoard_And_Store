package com.example.hoard;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private Context context;

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
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
        holder.dateOfAcquisition.setText((CharSequence) item.getDateOfAcquisition());
        holder.make.setText(item.getMake());
        holder.model.setText(item.getModel());
        holder.serialNumber.setText(item.getSerialNumber());
        holder.estimatedValue.setText((int) item.getEstimatedValue());
        holder.comment.setText(item.getComment());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView briefDescription;
        public TextView dateOfAcquisition;
        public TextView make;
        public TextView model;
        public TextView serialNumber;
        public TextView estimatedValue;
        public TextView comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            briefDescription = itemView.findViewById(R.id.description_list);
            dateOfAcquisition = itemView.findViewById(R.id.date_list);
            make = itemView.findViewById(R.id.make_list);
            model = itemView.findViewById(R.id.model_list);
            serialNumber = itemView.findViewById(R.id.SN_list);
            estimatedValue = itemView.findViewById(R.id.Value_list);
            comment = itemView.findViewById(R.id.comment_list);
        }
    }
}
