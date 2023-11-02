package com.example.hoard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.SortViewHolder> {
    private ArrayList<String> dataList;

    public SortAdapter(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    public static class SortViewHolder extends RecyclerView.ViewHolder {
        public TextView sortTextView;

        public SortViewHolder(View itemView) {
            super(itemView);
            sortTextView = itemView.findViewById(R.id.sortTextView);
        }
    }

    @Override
    public SortViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sort_content, parent, false);
        return new SortViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SortViewHolder holder, int position) {
        String cityName = dataList.get(position);
        holder.sortTextView.setText(cityName);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}