package com.example.hoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView tvR, tvPython, tvCPP, tvJava;
    PieChart pieChart;
    private ItemDBController itemDBController;
    private TagDBController tagDBController;
    private ChipGroup chipGroupTags;
    private Button  applyButton;
    private Map<Tag, Integer> selectedTagMap;
    private ArrayList<Tag> selectedTagList;
    private BottomNavigationView bottomNav;
    private Menu bottomMenu;
    private MenuItem sortMenuItem;
    private MenuItem homeMenuItem;
    private MenuItem profileMenuItem;
    private MenuItem chartMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_breakdown);

        applyButton = findViewById(R.id.apply_tags);
        selectedTagList = new ArrayList<Tag>();

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
                    Intent homeIntent = new Intent(getApplicationContext(), ListScreen.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                } else if (id == R.id.nav_sort) {
                    Intent sortIntent = new Intent(getApplicationContext(), SortActivity.class);
                    sortIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(sortIntent);
                    return true;
                } else if (id == R.id.nav_chart) {
//                    Intent chartIntent = new Intent(getApplicationContext(), ItemBreakdown.class);
//                    chartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(chartIntent);

                } else if (id == R.id.nav_profile){
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
                selectedTagList.clear();
                for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroupTags.getChildAt(i);
                    if (chip.isChecked()) {
                        Tag selectedtag = (Tag) chip.getTag();
                        selectedTagList.add(selectedtag);
//                        selectedTagMap.put(selectedtag, 0);
                    }
                }
                if(selectedTagList.size() > 5){
                    showSnackbar("Only 5 tags can be selected");
                } else {
                    setData();
                }



            }
        });
    }

    private void setData() {
        itemDBController.getTagCounts(selectedTagList, new ItemDBController.TagCountsCallback() {
            @Override
            public void onTagCountsReady(Map<Tag, Integer> tagCounts) {
                if(pieChart != null){
                    pieChart.clearChart();
                }


                for (Tag tag : tagCounts.keySet()) {
                    pieChart.addPieSlice(
                            new PieModel(
                                    tag.getTagName().toString(),
                                    Integer.parseInt(tagCounts.get(tag).toString()),
                                    Color.parseColor(tag.getTagColor()))
                    );
                }

                setUpUI(tagCounts);


            }

            @Override
            public void onError(Exception e) {

            }
        });



    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

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