package com.example.hoard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;


public class SortActivity extends AppCompatActivity {
    private ArrayList<String> dataList;
    private RecyclerView recyclerView;
    private SortAdapter sortAdapter;
    private LinearLayoutManager layoutManager;
    private Button showDatePicker;
    private BottomNavigationView bottomNav;
    private Menu bottomMenu;
    private MenuItem sort;
    private MenuItem home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        recyclerView = findViewById(R.id.sorting);
        recyclerView.setHasFixedSize(true);

        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        sort.setChecked(true);

        String[] cities = {"Edmonton", "Vancouver", "Moscow", "Sydney", "Edmonton", "Vancouver", "Moscow", "Sydney"};
        dataList = new ArrayList<>(Arrays.asList(cities));

        sortAdapter = new SortAdapter(dataList);

        layoutManager = new LinearLayoutManager(this); // Use 'this' as context
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(sortAdapter);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    startActivity(sortIntent);

                } else if (id == R.id.nav_home) {
                    // Replace the fragment container with the SortFragment
//                    Intent sortIntent = new Intent(getApplicationContext(), ListScreen.class);
//                    startActivity(sortIntent);

                }
                return true;
            }
        });

        showDatePicker = findViewById(R.id.showDateRangePicker);
        showDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the MaterialDatePicker to select a date range
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = createMaterialDatePicker();

                // Show the date picker
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
            }
        });
    }

    private MaterialDatePicker<androidx.core.util.Pair<Long, Long>> createMaterialDatePicker() {
        MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");

        return builder.build();
    }
}


