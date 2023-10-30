package com.example.hoard;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ItemEditDeleteActivity extends Activity {


    private TextView textViewDateOfPurchase, textViewMake, textViewModel, textViewSerialNumber, textViewEstimatedValue, textViewComments,TextViewDescription;
    private Item selectedItem;
    private ItemDBController itemDBController;
    // Other UI elements like ImageView for photos can be defined here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        // Initialize UI components from item_details.xml
        textViewDateOfPurchase = findViewById(R.id.textViewDateOfPurchase);
        TextViewDescription = findViewById(R.id.TextViewDescription);
        textViewMake = findViewById(R.id.textViewMake);
        textViewModel = findViewById(R.id.textViewModel);
        textViewSerialNumber = findViewById(R.id.textViewSerialNumber);
        textViewEstimatedValue = findViewById(R.id.textViewEstimatedValue);
        textViewComments = findViewById(R.id.textViewComments);
        //TODO: Load photos components
        //TODO: Load tags in components


        Button btnEdit = findViewById(R.id.buttonEdit);
        Button btnCancel = findViewById(R.id.buttonCancel);

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
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel();
            }
        });
    }

    // Method to display the selected item's info
    private void displaySelectedItem() {
        // Assuming selectedItem has getters for each property
        textViewDateOfPurchase.setText(selectedItem.getDateOfPurchase());
        TextViewDescription.setText(selectedItem.getDescription());
        textViewMake.setText(selectedItem.getMake());
        textViewModel.setText(selectedItem.getModel());
        textViewSerialNumber.setText(selectedItem.getSerialNumber());
        textViewEstimatedValue.setText(String.valueOf(selectedItem.getEstimatedValue()));
        textViewComments.setText(selectedItem.getComment());
        //TODO: Load photos into ImageView or a gallery view
        //TODO: Load tags in scoller
    }

    // Method to edit the selected item's information
    private void editItem() {
        // Save the edited fields back to the item
        selectedItem.setDescription(editTextDescription.getText().toString());
        // ... save other fields

        // Update the item in the database
        itemDBController.updateItem(selectedItem);
    }

    // Method to delete the selected item
    private void btnCancel() {

        finish();
    }
}
