package com.example.hoard;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An activity class dedicated to sorting functionalities within the application.
 *
 */
public class SortActivity extends AppCompatActivity implements CustomDatePicker.DatePickListener{
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
    private Button resetDateRange;
    private Button resetDescriptionKeyWords;
    private List<String> appliedMakes;
    private FilterCriteria filterCriteria;
    private EditText startDateEditText;
    private  EditText endDateEditText;
    private Toolbar topBar;
    private Menu topBarMenu;
    private  EditText BriefDescriptionKeywordEditText;
    private RadioGroup sortOrder;
    private String sortOrderPicked;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private RadioButton radioButtonAscending;
    private RadioButton radioButtonDescending;
    private ChipGroup chipGroupTags;
    private ArrayList<Tag> selectedTagList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        BriefDescriptionKeywordEditText = findViewById(R.id.BriefDescriptionKeyword);
        selectedTagList = new ArrayList<Tag>();
        // Initialize data
        appliedMakes = new ArrayList<>();
        makes = new ArrayList<>();
        ArrayList<String> dataList = new ArrayList<>();
        filterCriteria = FilterCriteria.getInstance();

        // Find views
        recyclerView = findViewById(R.id.sorting);
        bottomNav = findViewById(R.id.bottomNavigationView);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomMenu = bottomNav.getMenu();
        sort = bottomMenu.findItem(R.id.nav_sort);
        sort.setChecked(true);
        topBar = findViewById(R.id.topAppBar);
        topBar.setTitle("Filter/Sort");
        topBarMenu = topBar.getMenu();
        sortOrder = findViewById(R.id.sort_order_radio_button);

        radioButtonAscending = findViewById(R.id.sort_ascedning);
        radioButtonDescending = findViewById(R.id.sort_descending);
        dbController = ItemDBController.getInstance();
        TagDBController tagDBController = TagDBController.getInstance();





        // Assuming you have references to the menu items
        MenuItem applyMenuItem = topBarMenu.findItem(R.id.action_apply);
        MenuItem resetMenuItem = topBarMenu.findItem(R.id.action_reset);
        MenuItem closeMenu = topBarMenu.findItem(R.id.close_bulk_select); // should rename

        // Enable or disable the menu items
        applyMenuItem.setEnabled(true);
        resetMenuItem.setEnabled(true);
        closeMenu.setEnabled(true);

        applyMenuItem.setVisible(true);
        resetMenuItem.setVisible(true);
        closeMenu.setVisible(true);


        // Initialize adapters and layout manager
        itemMakes = new ArrayAdapter<>(this, android.R.layout.select_dialog_item);
        search = findViewById(R.id.filter_make_search);
        search.setThreshold(1);

        sortAdapter = new SortAdapter();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(sortAdapter);

        fab = findViewById(R.id.addItemButton);
//        applyButton = findViewById(R.id.apply_filter_sort_button);
        addMoreFilters = findViewById(R.id.add_more_make_filter);
        resetFilters = findViewById(R.id.reset_make_filter);
        resetDateRange = findViewById(R.id.reset_date_range);
        resetDescriptionKeyWords = findViewById(R.id.reset_description_keywords);
        startDateEditText = findViewById(R.id.start_date_edit_text);
        endDateEditText = findViewById(R.id.end_date_edit_text);
        chipGroupTags = findViewById(R.id.tagChipGroup);
        //addMoreFilters.setVisibility(View.INVISIBLE);

        tagDBController.loadTags(new DataLoadCallBackTag() {
            @Override
            public void onDataLoaded(List<Tag> tags) {
                // Iterate through the items, creating a chip for each one
                for (Tag tag : tags) {
                    Chip chip = new Chip(SortActivity.this);
                    chip.setText(tag.getTagName());
                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(tag.getTagColor())));
                    chip.setCheckedIconVisible(true);
                    chip.setCheckable(true);
                    chip.setTag(tag);
                    // Add the chip to the ChipGroup
                    chipGroupTags.addView(chip);
                }
            }
        });

        setFiltersCount(addMoreFilters, filterCriteria.getMakes());
        setFilterOption();

        // Load items and populate the AutoCompleteTextView
        dbController.loadItems(new DataLoadCallbackItem() {
            @Override
            public void onDataLoaded(List<Item> items) {
                itemAdapter = new ItemAdapter(items, recyclerView);
                loadAdapter(itemAdapter);
                search.setAdapter(itemMakes);
            }
        }, null);


        sortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.sort_ascedning){
                    if(filterCriteria.getSortOption() != null & filterCriteria.getSortOption() == "Ascending"){
                        radioButtonAscending.setChecked(true);
                    } else {
                        sortOrderPicked = "Ascending";
                    }
                } else if (i == R.id.sort_descending) {
                    if(filterCriteria.getSortOption() != null & filterCriteria.getSortOption() == "Descending") {
                        radioButtonDescending.setChecked(true);
                    } else {
                        sortOrderPicked = "Descending";
                    }

                }
            }
        });

        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_apply) {
                    EditText startDateEditText = findViewById(R.id.start_date_edit_text);
                    EditText endDateEditText = findViewById(R.id.end_date_edit_text);
                    String startDateString = startDateEditText.getText().toString();
                    String endDateString = endDateEditText.getText().toString();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    if (!startDateString.isEmpty()) {
                        try {
                            Date startDate = dateFormatter.parse(startDateString);
                            filterCriteria.setStartDate(startDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        filterCriteria.setStartDate(null);
                    }
                    if (!endDateString.isEmpty()) {
                        try {
                            Date endDate = dateFormatter.parse(endDateString);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(endDate);
                            calendar.set(Calendar.HOUR_OF_DAY, 23);
                            calendar.set(Calendar.MINUTE, 59);
                            calendar.set(Calendar.SECOND, 59);
                            calendar.set(Calendar.MILLISECOND, 999);
                            endDate = calendar.getTime();
                            filterCriteria.setEndDate(endDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        filterCriteria.setEndDate(null);
                    }

                    EditText BriefDescriptionKeywordEditText = findViewById(R.id.BriefDescriptionKeyword);
                    String BriefDescriptionKeywordString = BriefDescriptionKeywordEditText.getText().toString();
                    if (!BriefDescriptionKeywordString.isEmpty()) {
                        List<String> briefDescriptionKeywords = Arrays.asList(BriefDescriptionKeywordString.split("\\s+"));
                        filterCriteria.setDescriptionKeyWords(briefDescriptionKeywords);
                    } else {
                        filterCriteria.setDescriptionKeyWords(null);
                    }

                    for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                        Chip chip = (Chip) chipGroupTags.getChildAt(i);
                        if (chip.isChecked()) {
                            Tag selectedtag = (Tag) chip.getTag();
                            selectedTagList.add(selectedtag);

                        }
                    }
                    filterCriteria.setTags(selectedTagList);

                    String enteredMake = search.getText().toString();
                    Intent returnIntent = new Intent(getApplicationContext(), ListScreen.class);
                    returnIntent.putExtra("filterCriteria", filterCriteria);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    if (!enteredMake.isEmpty()) {
                        appliedMakes.add(enteredMake);
                        filterCriteria.setMakes(appliedMakes);

                    }

                    String sortBy = sortAdapter.getSortBy();
                    Map<String, String> sortOptionsEnables = new HashMap<>();

                    if(sortOrderPicked == null){
                        sortOrderPicked = "Ascending";
                    }
                    sortOptionsEnables.put(sortBy, sortOrderPicked);
                    filterCriteria.setSortBy(sortBy);
                    filterCriteria.setSortOption(sortOrderPicked);
                    filterCriteria.setSortOptions(sortOptionsEnables);

                    Intent listIntent = new Intent(getApplicationContext(), ListScreen.class);
                    startActivity(listIntent);

                } else if (id == R.id.action_reset) {
                    filterCriteria.removeAllOptions();
                    Intent listIntent = new Intent(getApplicationContext(), ListScreen.class);
                    startActivity(listIntent);

                } else if (id ==R.id.close_bulk_select) {
                    Intent listIntent = new Intent(getApplicationContext(), ListScreen.class);
                    startActivity(listIntent);
                }
                return false;
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

        resetDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterCriteria.clearEndDate();
                filterCriteria.clearStartDate();

                startDateEditText.setText("");
                endDateEditText.setText("");
            }
        });

        resetDescriptionKeyWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterCriteria.clearDescriptionKeyWords();
                BriefDescriptionKeywordEditText.setText("");

            }
        });

        // Handle bottom navigation item selection
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent homeIntent = new Intent(getApplicationContext(), ListScreen.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                } else if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    sortIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(sortIntent);
                    return true;
                } else if (id == R.id.nav_chart) {
                    Intent chartIntent = new Intent(getApplicationContext(), ItemBreakdown.class);
                    chartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(chartIntent);

                } else if (id == R.id.nav_profile){
                    Intent profileIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(profileIntent);
                }

                return true;
            }
        });

        // Set up date range picker
//        Button showDatePicker = findViewById(R.id.showDateRangePicker);
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
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
                if (filterCriteria.getMakes() == null) {
                    addMoreFilters.setText("Add");
                } else {
                    addMoreFilters.setText(String.format("Add (%d)", filterCriteria.getMakes().size()));
                    if (filterCriteria.getMakes().size() == 0) {
                        addMoreFilters.setText("Add");
                    }

                }

                String filterText = s.toString();
                itemMakes.getFilter().filter(filterText);

                //grab the first option if not completed?
                addMoreFilters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enteredMake = search.getText().toString();
                        if (!enteredMake.isEmpty()) {
                            List<String> multipleMakes = Collections.singletonList(enteredMake);
                            filterCriteria.setMakes(multipleMakes);
                            search.setText("");
                            setFiltersCount(addMoreFilters, filterCriteria.getMakes());

                        }
                    }
                });
            }
        });
    }

    private void setFilterOption() {
        try {
            if(filterCriteria.getSortOption() != null & filterCriteria.getSortOption() == "Ascending"){
                radioButtonAscending.setChecked(true);
            } else if(filterCriteria.getSortOption() != null & filterCriteria.getSortOption() == "Descending"){
                radioButtonDescending.setChecked(true);
            } else {
                sortOrderPicked = "Ascending";
            }
            if (filterCriteria.getStartDate() != null) {
                startDateEditText.setText(sdf.format(filterCriteria.getStartDate()));
            }

            if (filterCriteria.getEndDate() != null) {
                endDateEditText.setText(sdf.format(filterCriteria.getEndDate()));
            }

            List<String> descriptionKeywordsList = filterCriteria.getDescriptionKeyWords();
            if (descriptionKeywordsList != null && !descriptionKeywordsList.isEmpty()) {
                // Join the list elements with spaces
                String joinedKeywords = String.join(" ", descriptionKeywordsList);
                BriefDescriptionKeywordEditText.setText(joinedKeywords);
            }

            if(filterCriteria.getTagList() != null){

            }
        } catch (NullPointerException e) {
            // Handle the exception as needed, e.g., log an error, show a message to the user, etc.
            e.printStackTrace(); // or log.error("Error setting filter options", e);
        }

    }

    /**
     * Loads the given adapter into the AutoCompleteTextView.
     *
     * @param itemAdapter the adapter to load
     */
    private void loadAdapter(ItemAdapter itemAdapter) {
        itemMakes.clear();
        int itemCount = itemAdapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            Item item = itemAdapter.getItem(i);
            itemMakes.add(item.getMake());
        }
        itemMakes.notifyDataSetChanged();
    }

    /**
     * Creates and configures a new instance of MaterialDatePicker for date range selection.
     *
     * @return Configured instance of MaterialDatePicker.
     */
    private MaterialDatePicker<Pair<Long, Long>> createMaterialDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Date Range");
        return builder.build();
    }


    /**
     * Sets the text of the given button to the number of filters applied.
     *
     * @param bttn       the button to set the text of
     * @param filterList the list of filters
     * @param <E>        the type of the list
     */
    public <E> void setFiltersCount(Button bttn, List<E> filterList) {
        if (filterList == null) {
            bttn.setText("Add");
        } else {
            bttn.setText(String.format("Add (%d)", filterList.size()));
            if (filterList.size() == 0) {
                addMoreFilters.setText("Add");
            }

        }
    }

    private void showDatePicker() {
        // Create an instance of the CustomDatePicker
        CustomDatePicker customDatePicker = new CustomDatePicker((Context) this, (CustomDatePicker.DatePickListener) this, true);
        customDatePicker.showDatePicker();

    }

    @Override
    public void onDateRangePicked(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        Date selectedStartDateObject = new GregorianCalendar(startYear, startMonth, startDay+1).getTime();
        Date selectedEndDateObject = new GregorianCalendar(endYear, endMonth, endDay+1).getTime();
        startDateEditText.setText(sdf.format(selectedStartDateObject));
        endDateEditText.setText(sdf.format(selectedEndDateObject));

    }
}



