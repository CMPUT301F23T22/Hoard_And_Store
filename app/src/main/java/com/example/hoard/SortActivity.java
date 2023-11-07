package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortActivity extends AppCompatActivity {
    private ArrayList<String> dataList;
    private RecyclerView recyclerView;
    private SortAdapter sortAdapter;
    private LinearLayoutManager layoutManager;
    private Button showDatePicker;
    private BottomNavigationView bottomNav;
    private BottomAppBar bottomAppBar;
    private Menu bottomMenu;
    private MenuItem sort;
    private EditText makeTextField;
    private AutoCompleteTextView search;
    private ItemDBController dbController;
    private ItemAdapter itemAdapter;
    private ArrayAdapter<String> itemMakes;
    private List<String> makes;
    private FloatingActionButton fab;
    private Button applyButton;
    private Button addMoreFilters;
    private Button resetFilters;
    private List<String> appliedMakes;
    private FilterCriteria filterCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        // Initialize data
        appliedMakes = new ArrayList<>();
        makes = new ArrayList<>();
        dataList = new ArrayList<>();
        FilterCriteria filterCriteria = FilterCriteria.getInstance();

        // Find views
        recyclerView = findViewById(R.id.sorting);
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        sort.setChecked(true);

        // Initialize adapters and layout manager
        itemMakes = new ArrayAdapter<>(this, android.R.layout.select_dialog_item);
        search = findViewById(R.id.filter_make_search);
        search.setThreshold(1);
        String[] sortOptions = {"Date", "Make", "Estimated Value", "Description", "Edmonton", "Tags" };
        dataList = new ArrayList<>(Arrays.asList(sortOptions));
        sortAdapter = new SortAdapter(dataList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(sortAdapter);
        dbController = ItemDBController.getInstance();
        fab = findViewById(R.id.addItemButton);
        applyButton = findViewById(R.id.apply_filter_sort_button);
        addMoreFilters = findViewById(R.id.add_more_make_filter);
        resetFilters = findViewById(R.id.reset_make_filter);
        //addMoreFilters.setVisibility(View.INVISIBLE);

        setFiltersCount(addMoreFilters, filterCriteria.getMakes());

        // Load items and populate the AutoCompleteTextView
        dbController.loadItems(new DataLoadCallback() {
            @Override
            public void onDataLoaded(List<Item> items) {
                itemAdapter = new ItemAdapter(items);
                loadAdapter(itemAdapter);
                search.setAdapter(itemMakes);
            }
        }, null);

        // Set up the "Apply" button click listener
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredMake = search.getText().toString();
                if (!enteredMake.isEmpty()) {
                    appliedMakes.add(enteredMake);
                }
                //filterCriteria.setMakes(appliedMakes);

                Intent listIntent = new Intent(getApplicationContext(), ListScreen.class);
                startActivity(listIntent);
            }
        });

        // Set up layout adjustments based on the keyboard visibility
        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getHeight();
                int keypadHeight = screenHeight - r.bottom;
                int threshold = screenHeight / 6;

                if (screenHeight < 1400) {
                    bottomNav.setVisibility(View.GONE);
                    bottomAppBar.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                    bottomAppBar.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });

        resetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterCriteria.clearMakes();
                setFiltersCount(addMoreFilters, filterCriteria.getMakes());
            }
        });

        // Handle bottom navigation item selection
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    startActivity(sortIntent);

                } else if (id == R.id.nav_home) {
                    // Navigate to the home screen
                    finish();
                }
                return true;
            }
        });

        // Set up date range picker
        Button showDatePicker = findViewById(R.id.showDateRangePicker);
        showDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the MaterialDatePicker to select a date range
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = createMaterialDatePicker();

                // Show the date picker
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER_TAG");

            }
        });

        // Handle AutoCompleteTextView text changes
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                addMoreFilters.setVisibility(View.VISIBLE);
                if(filterCriteria.getMakes() == null ){
                    addMoreFilters.setText("Add");
                } else {
                    addMoreFilters.setText(String.format("Add (%d)", filterCriteria.getMakes().size()));
                    if(filterCriteria.getMakes().size() == 0){ addMoreFilters.setText("Add");}

                }

                String filterText = s.toString();
                itemMakes.getFilter().filter(filterText);

                //grab the first option if not completed?
                addMoreFilters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enteredMake = search.getText().toString();
                        if (!enteredMake.isEmpty()) {
                            List<String> multipleMakes = Arrays.asList(enteredMake);
                            filterCriteria.setMakes(multipleMakes);
                            search.setText("");
                            setFiltersCount(addMoreFilters, filterCriteria.getMakes());

                        }
                    }
                });
            }
        });
    }

    private void loadAdapter(ItemAdapter itemAdapter) {
        itemMakes.clear();
        int itemCount = itemAdapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            Item item = itemAdapter.getItem(i);
            itemMakes.add(item.getMake());
        }
        itemMakes.notifyDataSetChanged();
    }

    private MaterialDatePicker<Pair<Long, Long>> createMaterialDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");

        return builder.build();
    }

    public <E> void setFiltersCount(Button bttn, List<E> filterList){
        if(filterList == null ){
            bttn.setText("Add");
        } else {
            bttn.setText(String.format("Add (%d)", filterList.size()));
            if(filterList.size() == 0){ addMoreFilters.setText("Add");}

        }
    }
}


