package com.example.hoard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class Sort extends Fragment {
    private ArrayList<String> dataList;
    ArrayAdapter<String> cityAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);

        // Find the ListView in your layout
        ListView listView = view.findViewById(R.id.sorting);

        String[] cities = {"Edmonton", "Vancouver", "Moscow", "Sydney"};
        dataList = new ArrayList<>(Arrays.asList(cities));

        // Create an ArrayAdapter to populate the ListView with the data from dataList
        cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, dataList);

        // Set the adapter for the ListView
        listView.setAdapter(cityAdapter);

        return view;
    }
}