package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemEditDeleteActivity extends Activity {


    private TextView dateOfAcquisitionTextView, makeTextView, modelTextView, serialNumberTextView, estimatedValueTextView, commentTextView, briefDescriptionTextView;
    private Item selectedItem;
    private ItemDBController itemDBController;
    // Other UI elements like ImageView for photos can be defined here

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
        //TODO: Load photos components
        //TODO: Load tags in components

        Button editButton = findViewById(R.id.editButton);
        Button closeButton = findViewById(R.id.closeButton);

        // Retrieve the selected item ID from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_ITEM_ID")) {
            String itemId = intent.getStringExtra("SELECTED_ITEM_ID");
            // Use the ItemDBController to fetch the Item by its ID
            selectedItem = itemDBController.getItemById(itemId);
            if (selectedItem != null) {
                displaySelectedItem();
            } else {
                // Item not found with the given ID
                Toast.makeText(this, "Item not found", Toast.LENGTH_LONG).show();
                finish(); // Close the activity
            }
        } else {
            // No item ID was passed in the intent
            Toast.makeText(this, "No item ID provided", Toast.LENGTH_LONG).show();
            finish(); // Close the activity
        }

        // Display the item's properties
        displaySelectedItem();

        // Set up the button listeners
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClose();
            }
        });
    }

    // Method to display the selected item's info
    private void displaySelectedItem() {
        // Assuming selectedItem has getters for each property
        // Format the date
        Date date = selectedItem.getDateOfAcquisition();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(date);
        dateOfAcquisitionTextView.setText(formattedDate);
        briefDescriptionTextView.setText(selectedItem.getBriefDescription());
        makeTextView.setText(selectedItem.getMake());
        modelTextView.setText(selectedItem.getModel());
        serialNumberTextView.setText(selectedItem.getSerialNumber());
        estimatedValueTextView.setText(String.valueOf(selectedItem.getEstimatedValue()));
        commentTextView.setText(selectedItem.getComment());
        //TODO: Load photos into ImageView or a gallery view
        //TODO: Load tags
    }

    // Method to edit the selected item's information
    private void editItem() {
        // TODO figure out how to call the add item activity
    }

    // Method to delete the selected item
    private void btnClose() {

        finish();
    }
}
