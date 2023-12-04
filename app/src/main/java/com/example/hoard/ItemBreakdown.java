package com.example.hoard;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBreakdown extends AppCompatActivity {
    PieChart pieChart;
    private ItemDBController itemDBController;
    private TagDBController tagDBController;
    private ChipGroup chipGroupTags;
    private Button  applyButton;
    private Map<Tag, Integer> selectedTagMap;
    private ArrayList<Tag> selectedTagList;
    private BottomNavigationView bottomNav;
    private MenuItem sortMenuItem;
    private MenuItem homeMenuItem;
    private MenuItem profileMenuItem;
    private List<Map.Entry<Tag, Integer>> selectedTagMapList;
    private MenuItem chartMenuItem;
    private ItemBreakDownAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_breakdown);

        applyButton = findViewById(R.id.apply_tags);
        selectedTagList = new ArrayList<Tag>();
        selectedTagMap = new HashMap<>();

        selectedTagMapList = new ArrayList<>(selectedTagMap.entrySet());

        adapter = new ItemBreakDownAdapter(this, selectedTagMapList);

        // Set up the ListView
        ListView listView = findViewById(R.id.tag_breakdown_list); // Replace with your ListView id
        listView.setAdapter(adapter);

        bottomNav = findViewById(R.id.bottomNavigationView);
        Menu bottomMenu = bottomNav.getMenu();

        sortMenuItem = bottomMenu.findItem(R.id.nav_sort);
        homeMenuItem = bottomMenu.findItem(R.id.nav_home);
        chartMenuItem = bottomMenu.findItem(R.id.nav_chart);
        profileMenuItem = bottomMenu.findItem(R.id.nav_profile);

        //we are in the chartMenu
        chartMenuItem.setChecked(true);


        itemDBController = ItemDBController.getInstance();
        tagDBController = TagDBController.getInstance();
        chipGroupTags = findViewById(R.id.tagChipGroup);

        pieChart = findViewById(R.id.piechart);
        pieChart.addPieSlice(
                new PieModel(
                        "Place Holder",
                        Integer.parseInt("1"),
                        R.color.white)
        );

        for (int i = 0; i < 5; i++) {
            View placeholderLayout = findViewById(getResources().getIdentifier("place_holder_colour_" + (i + 1), "id", getPackageName()));
            TextView placeholderText = findViewById(getResources().getIdentifier("place_holder_text_" + (i + 1), "id", getPackageName()));
            placeholderLayout.setVisibility(View.GONE);
            placeholderText.setText("");
        }

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // switch to the expected activity Make sure to clear activity
                    Intent homeIntent = new Intent(getApplicationContext(), ListScreen.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                } else if (id == R.id.nav_sort) {
                    // switch to the expected activity Make sure to clear activity
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    sortIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(sortIntent);
                    return true;
                } else if (id == R.id.nav_chart) {
                    // already in this activity do nothing

                } else if (id == R.id.nav_profile){
                    // switch to the expected activity Make sure to clear activity
                    Intent profileIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(profileIntent);
                }

                return true;
            }
        });


        tagDBController.loadTags(new DataLoadCallBackTag() {
            @Override
            public void onDataLoaded(List<Tag> tags) {
                // Iterate through the items, creating a chip for each one
                for (Tag tag : tags) {
                    Chip chip = new Chip(ItemBreakdown.this);
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

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear any previous  tags that were selected
                selectedTagList.clear();
                // grab the selected tags
                for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroupTags.getChildAt(i);
                    if (chip.isChecked()) {
                        Tag selectedtag = (Tag) chip.getTag();
                        selectedTagList.add(selectedtag);
                        selectedTagMap.put(selectedtag, 0);
                    }
                }
                // we only support 5
                if(selectedTagList.size() > 5){
                    showSnackbar("Only 5 tags can be selected");
                } else {
                    setData();
                }



            }
        });
    }


    /**
     * Uses the selectedTags to generate a pie-chart displaying the # of items
     * using a particular tag
     *
     */
    private void setData() {
        itemDBController.getTagCounts(selectedTagList, new ItemDBController.TagCountsCallback() {
            @Override
            public void onTagCountsReady(List<Item> itemWithSelectedTags) {
                // check if pie chart is not null
                selectedTagMap.clear();
                if(pieChart != null){
                    pieChart.clearChart();
                    pieChart.clearAnimation();
                }

                // add the counts for each tag (# of occurences)
                for(Item selectedItem : itemWithSelectedTags){
                    for(Tag selectedTags: selectedItem.getTags()){
                        if(selectedTagList.contains(selectedTags)){
                            if (!selectedTagMap.containsKey(selectedTags)){
                                selectedTagMap.put(selectedTags, 1);
                            } else {
                                selectedTagMap.put(selectedTags, selectedTagMap.get(selectedTags) + 1);
                            }
                        }
                    }
                }

                // create the pie slices for each tag Selected.
                for (Map.Entry<Tag, Integer> entry : selectedTagMap.entrySet()) {
                    pieChart.addPieSlice(
                            new PieModel(
                                    entry.getKey().getTagName(),
                                    Integer.parseInt(entry.getValue().toString()),
                                    Color.parseColor(entry.getKey().getTagColor()))
                    );
                }

                // update the adapter to the new data
                selectedTagMapList.clear();
                selectedTagMapList.addAll(selectedTagMap.entrySet());
                adapter.notifyDataSetChanged();

                setUpUI(selectedTagMap);


            }

            @Override
            public void onError(Exception e) {

            }
        });



    }


    /**
     * Shows a snack bar with a certain message
     *
     * @param  message message to display
     */
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * updates our placeholder textView and sets/unsets them to be visible to the user
     *
     * @param  tagCounts a map indicating the Tag and the number of occurences in a users item collection
     */
    private void setUpUI(Map<Tag, Integer> tagCounts){
        int maxElementsToShow = 5;

        // Loop through your LinearLayout elements and set visibility based on the map size
        int i = 0;
        for (Map.Entry<Tag, Integer> entry : tagCounts.entrySet()) {
            if (i >= maxElementsToShow) {
                break; // Break the loop if you have shown the maximum number of elements
            }

            View placeholderLayout = findViewById(getResources().getIdentifier("place_holder_colour_" + (i + 1), "id", getPackageName()));
            TextView placeholderText = findViewById(getResources().getIdentifier("place_holder_text_" + (i + 1), "id", getPackageName()));

            // If there is an element in the map, set visibility to visible
            placeholderLayout.setVisibility(View.VISIBLE);

            // Set the text using the tag's name
            placeholderText.setText(entry.getKey().getTagName());
            placeholderLayout.setBackgroundColor(Color.parseColor(entry.getKey().getTagColor()));

            i++;
        }

        // Hide any remaining placeholders if the map size is less than maxElementsToShow
        for (; i < maxElementsToShow; i++) {
            View placeholderLayout = findViewById(getResources().getIdentifier("place_holder_colour_" + (i + 1), "id", getPackageName()));
            TextView placeholderText = findViewById(getResources().getIdentifier("place_holder_text_" + (i + 1), "id", getPackageName()));
            placeholderLayout.setVisibility(View.GONE);
            placeholderText.setText("");
        }
    }

}