package com.example.hoard;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Sort extends Fragment {
    private ArrayList<String> dataList;
    ArrayAdapter<String> cityAdapter;
    private Button showDatePicker;
    private TextView selectedDateText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);

        // Find the ListView in your layout
        ListView listView = view.findViewById(R.id.sorting);

        String[] cities = {"Edmonton", "Vancouver", "Moscow", "Sydney"};
        dataList = new ArrayList<>(Arrays.asList(cities));

        // Create an ArrayAdapter to populate the ListView with the data from dataList
        cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, dataList);

        // Set the adapter for the ListView
        listView.setAdapter(cityAdapter);

        TextView selectedDateText = view.findViewById(R.id.show_selected_date);
        showDatePicker = view.findViewById(R.id.showDateRangePicker);
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();


        /**
         //https://www.geeksforgeeks.org/material-design-date-picker-in-android/

         */

        materialDateBuilder.setTitleText("Select Date Range");

        final MaterialDatePicker<androidx.core.util.Pair<Long, Long>> materialDatePicker = materialDateBuilder.build();

        showDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getChildFragmentManager(), "DATE_PICKER_TAG");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                selectedDateText.setText("Selected Date is : " + materialDatePicker.getHeaderText());
            }

        });

        return view;
    }
}
