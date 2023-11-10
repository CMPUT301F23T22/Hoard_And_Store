package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {
    private TextView dateOfAcquisitionTextView, makeTextView, modelTextView, serialNumberTextView, estimatedValueTextView, commentTextView, briefDescriptionTextView;
    private Item selectedItem;
    ChipGroup chipGroup;
    private final ActivityResultLauncher<Intent> editActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleEditResult);

    /**
     * This method is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        // Initialize UI components from item_details.xml
        dateOfAcquisitionTextView = findViewById(R.id.dateOfAcquisitionTextView);
        briefDescriptionTextView = findViewById(R.id.briefDescriptionTextView);
        makeTextView = findViewById(R.id.makeTextView);
        modelTextView = findViewById(R.id.modelTextView);
        serialNumberTextView = findViewById(R.id.serialNumberTextView);
        estimatedValueTextView = findViewById(R.id.estimatedValueTextView);
        commentTextView = findViewById(R.id.commentTextView);
        chipGroup = findViewById(R.id.chipGroup);

        // Check if any of the views are null
        if (dateOfAcquisitionTextView == null || briefDescriptionTextView == null || makeTextView == null ||
                modelTextView == null || serialNumberTextView == null || estimatedValueTextView == null || commentTextView == null) {
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Button editButton = findViewById(R.id.editButton);
        Button closeButton = findViewById(R.id.closeButton);
        // Check if buttons are null
        if (editButton == null || closeButton == null) {
            Toast.makeText(this, "Error initializing buttons", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Retrieve the selected item from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_ITEM")) {
            selectedItem = (Item) intent.getSerializableExtra("SELECTED_ITEM");
            if (selectedItem != null) {
                displaySelectedItem();
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No item provided", Toast.LENGTH_LONG).show();
            finish();
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListScreen.class);
                finish();
                startActivity(intent);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, AddEditItem.class);
                intent.putExtra("ITEM_TO_EDIT", selectedItem);
                editActivityResultLauncher.launch(intent);
            }
        });
    }

    /**
     * Handles the result from the edit activity. If the result is OK, the selected item is updated
     * with the new details returned from the edit activity.
     *
     * @param result The result data from the activity.
     */
    private void handleEditResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Item returnedItem = (Item) result.getData().getSerializableExtra("updatedItem");
            if (returnedItem != null) {
                selectedItem = returnedItem;
                displaySelectedItem();
            }
        }
    }

    /**
     * Displays the selected item's details in the activity's views. It formats the date
     * and updates all text views with the item's information. It also creates chips for each tag
     * associated with the item and adds them to the chip group.
     */
    private void displaySelectedItem() {
        // Format the date
        Date date = selectedItem.getDateOfAcquisition();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(date);
        formattedDate = getResources().getString(R.string.date_of_purchase_placeholder, formattedDate);
        dateOfAcquisitionTextView.setText(formattedDate);
        String briefDescription = getResources().getString(R.string.description_placeholder, selectedItem.getBriefDescription());
        briefDescriptionTextView.setText(briefDescription);
        String make = getResources().getString(R.string.make_placeholder, selectedItem.getMake());
        makeTextView.setText(make);
        String model = getResources().getString(R.string.model_placeholder, selectedItem.getModel());
        modelTextView.setText(model);
        String serialNumber = getResources().getString(R.string.serial_number_placeholder, selectedItem.getSerialNumber());
        serialNumberTextView.setText(serialNumber);
        String estimatedValue = getResources().getString(R.string.estimated_value_placeholder, selectedItem.getEstimatedValue());
        estimatedValueTextView.setText(estimatedValue);
        String comment = getResources().getString(R.string.comment_placeholder, selectedItem.getComment());
        commentTextView.setText(comment);
        ArrayList<Tag> tags = selectedItem.getTags();
        // create a chips from tags
        chipGroup.removeAllViews();

        // iterate over the tags and add to chip group
        for (Tag tag : tags) {
            Chip chip = new Chip(DetailsActivity.this);
            chip.setText(tag.getTagName());
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(tag.getTagColor())));
            // Add the chip to the ChipGroup
            chipGroup.addView(chip);
        }
    }

}
