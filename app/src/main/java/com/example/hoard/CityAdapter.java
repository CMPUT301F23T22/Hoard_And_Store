package com.example.hoard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private ArrayList<String> dataList;

    public CityAdapter(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView cityNameTextView;

        public CityViewHolder(View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.cityNameTextView);
        }
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delafter, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        String cityName = dataList.get(position);
        holder.cityNameTextView.setText(cityName);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}