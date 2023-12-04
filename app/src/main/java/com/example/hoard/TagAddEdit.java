package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.List;

/**
 * Activity for adding or editing a tag.
 */
public class TagAddEdit extends AppCompatActivity {

    private EditText editTextTagName;

    private EditText editTextTagColor;

    private TextInputLayout tagColorInputLayout;
    private View colorIndicator;
    private String tagColor = "#FFFFFF"; // Default color
    private String tagName;

    private ChipGroup chipGroup;

    private TagDBController dbController;
    /**
     * Initializes the activity and sets up the UI for adding or editing a tag.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_create);

        dbController = TagDBController.getInstance();
        // Find the ChipGroup in layout
        chipGroup = findViewById(R.id.chipGroup);

        dbController.loadTags(new DataLoadCallBackTag() {
            @Override
            public void onDataLoaded(List<Tag> tags) {
                // Iterate through the items, creating a chip for each one
                for (Tag tag : tags) {
                    Chip chip = new Chip(TagAddEdit.this);
                    chip.setText(tag.getTagName());
                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(tag.getTagColor())));
                    // Add the chip to the ChipGroup
                    chipGroup.addView(chip);
                }
            }
        });

        editTextTagName = findViewById(R.id.tagNameInput);
        editTextTagColor = findViewById(R.id.tagColorInput);
        colorIndicator = findViewById(R.id.colorIndicator);

        // Color picker implementation from github repo
        // https://github.com/skydoves/ColorPickerView
        tagColorInputLayout = findViewById(R.id.tagColorInputLayout);
        tagColorInputLayout.setEndIconOnClickListener(v -> showColorPickerDialog());

        // Cancel button listener
        Button cancelButton = findViewById(R.id.closeButton);
        cancelButton.setOnClickListener(v -> finish());

        Button createButton = findViewById(R.id.submitButton);
        createButton.setOnClickListener(v -> createAndSendTag());
    }

    /**
     * Shows the color picker dialog.
     */
    private void showColorPickerDialog() {
        new ColorPickerDialog.Builder(this)
                .setPositiveButton(getString(R.string.submit_button_text),
                        (ColorEnvelopeListener) (envelope, fromUser) -> setLayoutColor(envelope))
                .setNegativeButton(getString(R.string.cancel_text),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show();
    }

    /**
     * Sets the color of the layout to the selected color.
     *
     * @param envelope The color envelope containing the selected color.
     */
    private void setLayoutColor(ColorEnvelope envelope) {
        tagColor = "#" + envelope.getHexCode();
        editTextTagColor.setText(tagColor);
        colorIndicator.setBackgroundColor(Color.parseColor(tagColor));

    }

    /**
     * Creates a new tag and sends it back to the calling activity.
     */
    private void createAndSendTag() {
        tagName = editTextTagName.getText().toString().trim();
        // Assume tagColor holds the selected color hex code. It needs to be initialized in your activity.
        tagColor = editTextTagColor.getText().toString().trim();

        boolean isValid = true;

        if (tagName.isEmpty()) {
            // Set error on tag name input
            editTextTagName.setError("Tag name cannot be empty");
            isValid = false;
        } else {
            editTextTagName.setError(null); // Clear the error
        }

        if (tagColor.isEmpty()) {
            // Set error on tag color input
            editTextTagColor.setError("You must select a color");
            isValid = false;
        } else {
            editTextTagColor.setError(null); // Clear the error
        }

        if (isValid) {
            // Create and use the newTag object as needed
            Tag newTag = new Tag(tagName, tagColor);
            dbController.addTag(newTag, task -> {
                if (task.isSuccessful()) {
                    // Log success
                    Log.i("AddTag", "Tag added successfully");
                    Toast.makeText(this, "Tag added successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newTag", newTag);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    // This block handles the failure case
                    Exception e = task.getException();
                    // Log failure
                    Log.e("AddItem", "Failed to add Tag", e);

                    Toast.makeText(this, "Failed to add item" + (e != null ? ": " + e.getMessage() : ""), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}