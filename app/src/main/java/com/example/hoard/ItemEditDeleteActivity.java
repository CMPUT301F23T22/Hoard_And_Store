package com.example.hoard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemEditDeleteActivity extends AppCompatActivity {

    private TextView dateOfAcquisitionTextView, makeTextView, modelTextView, serialNumberTextView, estimatedValueTextView, commentTextView, briefDescriptionTextView;
    private Item selectedItem;

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
                finish();
            }
        });
    }

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
    }

    private void editItem() {
        // TODO: implement the edit item functionality
    }
}
