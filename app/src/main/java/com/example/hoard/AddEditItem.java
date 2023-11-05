package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddEditItem extends AppCompatActivity implements CustomDatePicker.DatePickListener, TagAddEditFragment.TagAddListener {

    private EditText descriptionInput, makeInput, modelInput, serialNumberInput, valueInput, commentInput, dateInput;

    private TextInputLayout dateInputLayout;

    private TextView addEditHeader;

    private Date selectedDateObject;

    private Item currentItem; // Item to edit

    private ArrayList<Tag> NewTagsList;

    private boolean wasEdited;

    CustomDatePicker customDatePicker;
    private ArrayList<Tag> tagList; // Your list of tags
    private ArrayAdapter<Tag> tagAdapter; // Adapter for ListView or use a RecyclerView.Adapter for RecyclerView

    private ChipGroup chipGroupTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit);

        descriptionInput = findViewById(R.id.descriptionInput);
        makeInput = findViewById(R.id.makeInput);
        modelInput = findViewById(R.id.modelInput);
        serialNumberInput = findViewById(R.id.serialNumberInput);
        valueInput = findViewById(R.id.valueInput);
        commentInput = findViewById(R.id.commentInput);
        dateInput = findViewById(R.id.dateInput);
        dateInputLayout = findViewById(R.id.dateInputLayout);
        addEditHeader = findViewById(R.id.addEditHeader);

        // If you're passing an Item to edit via Intent
        Intent intent = getIntent();
        if (intent.hasExtra("item_to_edit")) {
            currentItem = (Item) intent.getSerializableExtra("item_to_edit");
        }

        if (currentItem != null) {
            addEditHeader.setText("EDIT");
            descriptionInput.setText(currentItem.getBriefDescription());
            makeInput.setText(currentItem.getMake());
            modelInput.setText(currentItem.getModel());
            serialNumberInput.setText(currentItem.getSerialNumber());
            valueInput.setText(String.valueOf(currentItem.getEstimatedValue()));
            commentInput.setText(currentItem.getComment());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateInput.setText(sdf.format(currentItem.getDateOfAcquisition()));

        }

        // Create an instance of the CustomDatePicker
        customDatePicker = new CustomDatePicker(this, this);

        dateInput.setOnClickListener(v -> {
            customDatePicker.showDatePicker();
        });

        dateInputLayout.setEndIconOnClickListener(v -> {
            customDatePicker.showDatePicker();
        });

        Button saveButton = findViewById(R.id.submitButton);
        saveButton.setOnClickListener(v -> saveItem());

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



    private void saveItem() {
        Intent returnIntent = new Intent();
        if(currentItem == null) {
            // Create new
            wasEdited = false;
            Item item = new Item(
                    selectedDateObject,
                    descriptionInput.getText().toString(),
                    makeInput.getText().toString(),
                    modelInput.getText().toString(),
                    serialNumberInput.getText().toString(),
                    Double.parseDouble(valueInput.getText().toString()),
                    commentInput.getText().toString()
            );
            for(Tag tag : NewTagsList) {
                item.addTag(tag);
            }


            returnIntent.putExtra("itemData", item);
        } else {
            // Update existing
            wasEdited = true;
            currentItem.setBriefDescription(descriptionInput.getText().toString());
            currentItem.setMake(makeInput.getText().toString());
            currentItem.setModel(modelInput.getText().toString());
            currentItem.setSerialNumber(serialNumberInput.getText().toString());
            currentItem.setEstimatedValue(Double.parseDouble(valueInput.getText().toString()));
            currentItem.setComment(commentInput.getText().toString());
            currentItem.setDateOfAcquisition(selectedDateObject);
            returnIntent.putExtra("itemData", currentItem);
        }
        returnIntent.putExtra("wasEdited", wasEdited);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onDatePicked(Date newdate) {
        // Handle the picked date here
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateInput.setText(sdf.format(newdate));
        selectedDateObject = newdate;
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

