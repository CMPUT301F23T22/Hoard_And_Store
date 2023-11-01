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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Sort extends Fragment {
    private ArrayList<String> dataList;
    private RecyclerView recyclerView;
    private CityAdapter cityAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button showDatePicker;
    private BottomNavigationView bottomNav;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);
        bottomNav = view.findViewById(R.id.bottomNavigationView);
        recyclerView = view.findViewById(R.id.sorting);
        recyclerView.setHasFixedSize(true);


        String[] cities = {"Edmonton", "Vancouver", "Moscow", "Sydney", "Edmonton", "Vancouver", "Moscow", "Sydney"};
        dataList = new ArrayList<>(Arrays.asList(cities));

        cityAdapter = new CityAdapter(dataList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cityAdapter);

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
