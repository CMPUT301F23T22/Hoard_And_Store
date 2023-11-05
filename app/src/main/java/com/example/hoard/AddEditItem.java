package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddEditItem extends AppCompatActivity implements CustomDatePicker.DatePickListener, TagAddEditFragment.TagAddListener {

    private EditText descriptionInput, makeInput, modelInput, serialNumberInput, valueInput, commentInput, dateInput;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private TextInputLayout dateInputLayout;
    private TextView addEditHeader;
    private Date selectedDateObject;
    private Item currentItem; // Item to edit
    private boolean isEdit; // To identify whether it's an edit operation
    private ItemDBController dbController;

    private ArrayList<Tag> NewTagsList;
    private ArrayAdapter<Tag> tagAdapter; // Adapter for ListView or use a RecyclerView.Adapter for RecyclerView
    private ChipGroup chipGroupTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add_edit);

        dbController = ItemDBController.getInstance();

        // Initialize views
        descriptionInput = findViewById(R.id.descriptionInput);
        makeInput = findViewById(R.id.makeInput);
        modelInput = findViewById(R.id.modelInput);
        serialNumberInput = findViewById(R.id.serialNumberInput);
        valueInput = findViewById(R.id.valueInput);
        commentInput = findViewById(R.id.commentInput);
        dateInput = findViewById(R.id.dateInput);
        dateInputLayout = findViewById(R.id.dateInputLayout);
        addEditHeader = findViewById(R.id.addEditHeader);

        // Check if editing
        Intent intent = getIntent();
        if (intent.hasExtra("ITEM_TO_EDIT")) {
            isEdit = true;
            currentItem = (Item) intent.getSerializableExtra("ITEM_TO_EDIT");
            populateFields(currentItem);
        } else {
            isEdit = false;
        }

        // Listener for the date picker
        dateInput.setOnClickListener(v -> showDatePicker());
        dateInputLayout.setEndIconOnClickListener(v -> showDatePicker());

        // Save button listener
        Button saveButton = findViewById(R.id.submitButton);
        saveButton.setOnClickListener(v -> {
            try {
                saveItem();
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing date", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button listener
        Button cancelButton = findViewById(R.id.closeButton);
        cancelButton.setOnClickListener(v -> finish());



        Button addTagBtn = findViewById(R.id.AddTagButton);
        addTagBtn.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment tagEditFragment = new TagAddEditFragment();

            // fills the whole activity
            transaction.replace(android.R.id.content, tagEditFragment, "ADD_TAG");
            transaction.addToBackStack(null); // if you want to add the transaction to the back stack
            transaction.commit();
        });

        chipGroupTags = findViewById(R.id.chip_group_tags);

    }

    private void showDatePicker() {
        // Create an instance of the CustomDatePicker
        CustomDatePicker customDatePicker = new CustomDatePicker(this, this);
        customDatePicker.showDatePicker();

    }

    private void populateFields(Item item) {
        addEditHeader.setText("EDIT");
        descriptionInput.setText(item.getBriefDescription());
        makeInput.setText(item.getMake());
        modelInput.setText(item.getModel());
        serialNumberInput.setText(item.getSerialNumber());
        valueInput.setText(String.valueOf(item.getEstimatedValue()));
        commentInput.setText(item.getComment());
        dateInput.setText(sdf.format(item.getDateOfAcquisition()));
    }

    private void saveItem() throws ParseException {
        if (isEdit) {
            updateItem();
        } else {
            createNewItem();
        }
    }

    private void createNewItem() throws ParseException {
        Item newItem = getItemFromFields();
        dbController.addItem(newItem, task -> {
            if (task.isSuccessful()) {
                // Log success
                Log.i("AddItem", "Item added successfully");


                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();

                resultIntent.putExtra("newItem", newItem);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                // This block handles the failure case
                Exception e = task.getException();
                // Log failure
                Log.e("AddItem", "Failed to add item", e);

                Toast.makeText(this, "Failed to add item" + (e != null ? ": " + e.getMessage() : ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateItem() throws ParseException {
        Item updatedItem = getItemFromFields();

        dbController.editItem(updatedItem.getItemID(), updatedItem, task -> {
            if (task.isSuccessful()) {
                // Log success
                Log.i("EditItem", "Item updated successfully");

                Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();

                resultIntent.putExtra("updatedItem", updatedItem);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                // This block handles the failure case
                Exception e = task.getException();
                // Log failure
                Log.e("EditItem", "Failed to update item", e);

                Toast.makeText(this, "Failed to update item" + (e != null ? ": " + e.getMessage() : ""), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Item getItemFromFields() throws ParseException {
        String description = descriptionInput.getText().toString();
        String make = makeInput.getText().toString();
        String model = modelInput.getText().toString();
        String serialNumber = serialNumberInput.getText().toString();
        double value = Double.parseDouble(valueInput.getText().toString());
        String comment = commentInput.getText().toString();
        Date acquisitionDate = sdf.parse(dateInput.getText().toString());

        if (currentItem != null) {
           // If this is an edit, update the existing item's fields
            currentItem.setBriefDescription(description);
            currentItem.setMake(make);
            currentItem.setModel(model);
            currentItem.setSerialNumber(serialNumber);
            currentItem.setEstimatedValue(value);
            currentItem.setComment(comment);
            currentItem.setDateOfAcquisition(acquisitionDate);
            for(Tag tag : NewTagsList) {
                currentItem.addTag(tag);
            }

            return currentItem;
        }

        // otherwise return the old item #TODO check if tags was added
        return new Item(
                acquisitionDate,
                description,
                make,
                model,
                serialNumber,
                value,
                comment);

    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        selectedDateObject = new GregorianCalendar(year, month, day).getTime();
        dateInput.setText(sdf.format(selectedDateObject));
    }

    @Override
    public void onTagAdded(Tag newTag) {
        // Update the UI

        if(currentItem != null) {
            currentItem.addTag(newTag);
        }else {

            NewTagsList.add(newTag);
        }

        tagAdapter.notifyDataSetChanged();

        // Create a new Chip for the newTag
        Chip chip = new Chip(this);
        chip.setText(newTag.getTagName()); // Assuming Tag has a getName method.
        chip.setChipIconResource(R.drawable.tag); // Optional icon.
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(view -> {
            // Handle the close icon click listener
            chipGroupTags.removeView(chip);
            // remove the tag from the data source.

            if(currentItem != null) {
                currentItem.removeTag(newTag);
            }else {

                NewTagsList.remove(newTag);
            }
        });

        // Add the Chip to the ChipGroup
        chipGroupTags.addView(chip);

    }
}
