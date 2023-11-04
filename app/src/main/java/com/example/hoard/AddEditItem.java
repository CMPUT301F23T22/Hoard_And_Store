package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class AddEditItem extends AppCompatActivity implements CustomDatePicker.DatePickListener, TagEditFragment.TagAddListener {

    private EditText descriptionInput;
    private EditText makeInput;
    private EditText modelInput;
    private EditText serialNumberInput;
    private EditText valueInput;
    private EditText commentInput;

    private EditText dateInput;

    private TextInputLayout dateInputLayout;

    private TextView addEditHeader;

    private Date selectedDateObject;

    private Item currentItem; // Item to edit

    private boolean wasEdited;

    CustomDatePicker customDatePicker;

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
        if(intent.hasExtra("item_to_edit")) {
            currentItem = (Item) intent.getSerializableExtra("item_to_edit");
        }

        if(currentItem != null) {
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
            Fragment tagEditFragment = new TagEditFragment();

            // If you're replacing an existing fragment that fills the whole activity
            transaction.replace(android.R.id.content, tagEditFragment, "ADD_TAG");

            // Alternatively, if you have a container where the fragment should be placed
            // transaction.replace(R.id.fragment_container, tagEditFragment, "ADD_TAG");

            transaction.addToBackStack(null); // if you want to add the transaction to the back stack
            transaction.commit();
        });


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
        // Handle the new tag here.
        // #TODO
    }
}
