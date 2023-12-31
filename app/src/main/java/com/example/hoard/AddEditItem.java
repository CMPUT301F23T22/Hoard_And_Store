package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Activity class for adding or editing items in the inventory.
 * It provides UI to input item details like description, make, model, serial number, value, etc.
 * This class also handles barcode scanning, image adding, and date picking functionalities.
 */
public class AddEditItem extends AppCompatActivity implements CustomDatePicker.DatePickListener {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    ChipGroup chipGroupTags;
    private final ActivityResultLauncher<Intent> addTagResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleAddTagResult);
    private final ActivityResultLauncher<Intent> serialScannerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleSerialScannerResult
    );
    private EditText descriptionInput, makeInput, modelInput, serialNumberInput, valueInput, commentInput, dateInput;

    // ActivityResultLauncher for the barcode scanner
    private final ActivityResultLauncher<Intent> barcodeScannerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleBarcodeScannerResult
    );

    private ArrayList<Uri> imagesData;
    // ActivityResultLauncher for adding images
    private final ActivityResultLauncher<Intent> addImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleAddImageResult
    );
    private final List<String> imageURLs = new ArrayList<>();

    private List<String> previousUrlsList = new ArrayList<>();

    private TextView addEditHeader;
    private Button addPhotoBtn;
    private Item currentItem; // Item to edit
    private boolean isEdit; // To identify whether it's an edit operation
    private ItemDBController itemDBController;
    private ArrayList<Tag> selectedTagList;

    /**
     * Initializes the activity, sets up UI components and listeners.
     * It also checks if the activity is opened for editing an existing item or adding a new item.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add_edit);

        selectedTagList = new ArrayList<Tag>();

        imagesData = new ArrayList<Uri>();

        itemDBController = ItemDBController.getInstance();
        TagDBController tagDBController = TagDBController.getInstance();

        // Initialize views
        descriptionInput = findViewById(R.id.descriptionInput);
        makeInput = findViewById(R.id.makeInput);
        modelInput = findViewById(R.id.modelInput);
        serialNumberInput = findViewById(R.id.serialNumberInput);
        valueInput = findViewById(R.id.valueInput);
        commentInput = findViewById(R.id.commentInput);
        dateInput = findViewById(R.id.dateInput);
        TextInputLayout dateInputLayout = findViewById(R.id.dateInputLayout);
        addEditHeader = findViewById(R.id.addEditHeader);
        chipGroupTags = findViewById(R.id.tagChipGroup);
        addPhotoBtn = findViewById(R.id.AddImageButton);

        // Check if editing
        Intent intent = getIntent();
        if (intent.hasExtra("ITEM_TO_EDIT")) {
            isEdit = true;
            currentItem = (Item) intent.getSerializableExtra("ITEM_TO_EDIT");
            populateFields();
        } else {
            isEdit = false;
        }

        tagDBController.loadTags(new DataLoadCallBackTag() {
            @Override
            public void onDataLoaded(List<Tag> tags) {
                // Iterate through the items, creating a chip for each one
                for (Tag tag : tags) {
                    Chip chip = new Chip(AddEditItem.this);
                    chip.setText(tag.getTagName());
                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(tag.getTagColor())));
                    chip.setCheckedIconVisible(true);
                    chip.setCheckable(true);
                    chip.setTag(tag);
                    // Add the chip to the ChipGroup
                    chipGroupTags.addView(chip);
                }
                if (isEdit) {
                    // select the tags from before
                    selectTags();
                }

            }
        });

        // Listener for the date picker
        dateInput.setOnClickListener(v -> showDatePicker());
        dateInputLayout.setEndIconOnClickListener(v -> showDatePicker());

        // Listener for the barcode scanner
        TextInputLayout descriptionInputLayout = findViewById(R.id.descriptionInputLayout);
        descriptionInputLayout.setEndIconOnClickListener(v -> launchBarcodeScanner());

        // Listener for the serial scanner
        TextInputLayout serialNumberInputLayout = findViewById(R.id.serialNumberInputLayout);
        serialNumberInputLayout.setEndIconOnClickListener(v -> launchSerialScanner());


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
            // Create an intent that will start the TagAddEditActivity.
            Intent tagIntent = new Intent(this, TagAddEdit.class);
            // Start the new activity.
            addTagResultLauncher.launch(tagIntent);
        });

        Button addImageBtn = findViewById(R.id.AddImageButton);
        addImageBtn.setOnClickListener(view -> {
            // Create an intent that will start the TagAddEditActivity.
            Intent addImageIntent = new Intent(this, AddImage.class);
            // Check if it's an edit operation
            if (isEdit) {
                // Assume currentItem is the item you're editing, and it's a Parcelable or Serializable object
                addImageIntent.putExtra("CURRENT_ITEM", currentItem);
            }
            // Start the new activity.
            addImageResultLauncher.launch(addImageIntent);
        });

    }
    /**
     * Launches the barcode scanner activity.
     */
    private void launchBarcodeScanner() {
        Intent intent = new Intent(this, BarcodeScannerActivity.class);
        barcodeScannerResultLauncher.launch(intent);
    }

    /**
     * Launches the serial number scanner activity.
     */
    private void launchSerialScanner() {
        Intent intent = new Intent(this, SerialScannerActivity.class);
        serialScannerResultLauncher.launch(intent);
    }

    /**
     * Shows the date picker dialog.
     */
    private void showDatePicker() {
        // Create an instance of the CustomDatePicker
        CustomDatePicker customDatePicker = new CustomDatePicker(this, this, false);
        customDatePicker.showDatePicker();

    }

    /**
     * Populates the fields with the details of the item to edit.
     */
    private void populateFields() {
        addEditHeader.setText("EDIT");
        descriptionInput.setText(currentItem.getBriefDescription());
        makeInput.setText(currentItem.getMake());
        modelInput.setText(currentItem.getModel());
        serialNumberInput.setText(currentItem.getSerialNumber());
        valueInput.setText(String.valueOf(currentItem.getEstimatedValue()));
        commentInput.setText(currentItem.getComment());
        dateInput.setText(sdf.format(currentItem.getDateOfAcquisition()));
        addPhotoBtn.setText("Edit Photo");
    }

    /**
     * Selects the tags that were previously selected for the item being edited.
     */
    private void selectTags() {
        ArrayList<Tag> tags = currentItem.getTags();
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            // Assuming each chip's tag has been set with a Tag object
            Tag chipTag = (Tag) chip.getTag();
            if (chipTag != null) {
                // Loop through list of tags to check if the chip's tag ID is present
                for (Tag tag : tags) {
                    if (tag.getTagID().equals(chipTag.getTagID())) {
                        chip.setChecked(true);
                    }
                }
            }
        }
    }

    /**
     * Saves the item to the database.
     */
    private void saveItem() throws ParseException {
        TextInputLayout dateInputLayout = findViewById(R.id.dateInputLayout);
        TextInputEditText dateInput = findViewById(R.id.dateInput);
        TextInputLayout descriptionInputLayout = findViewById(R.id.descriptionInputLayout);
        TextInputEditText descriptionInput = findViewById(R.id.descriptionInput);
        TextInputLayout makeInputLayout = findViewById(R.id.makeInputLayout);
        TextInputEditText makeInput = findViewById(R.id.makeInput);
        TextInputLayout modelInputLayout = findViewById(R.id.modelInputLayout);
        TextInputEditText modelInput = findViewById(R.id.modelInput);
        TextInputLayout serialNumberInputLayout = findViewById(R.id.serialNumberInputLayout);
        TextInputEditText serialNumberInput = findViewById(R.id.serialNumberInput);
        TextInputLayout valueInputLayout = findViewById(R.id.valueInputLayout);
        TextInputEditText valueInput = findViewById(R.id.valueInput);
        TextInputLayout commentInputLayout = findViewById(R.id.commentInputLayout);
        TextInputEditText commentInput = findViewById(R.id.commentInput);

        // Validate fields before saving
        if (isFieldEmpty(dateInput) || !isValidDate(dateInput.getText().toString())) {
            dateInputLayout.setError("Invalid date format. Use dd/mm/yyyy");
            dateInputLayout.setErrorEnabled(true);
            dateInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_color));
            return;
        } else {
            dateInputLayout.setError(null);
            dateInputLayout.setErrorEnabled(false);
        }

        if (isFieldEmpty(descriptionInput)) {
            descriptionInputLayout.setError("Description cannot be empty");
            descriptionInputLayout.setErrorEnabled(true);
            descriptionInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_color));
            return;
        } else {
            descriptionInput.setError(null);
            descriptionInputLayout.setErrorEnabled(false);
        }

        if (isFieldEmpty(makeInput)) {
            makeInputLayout.setError("Make cannot be empty");
            makeInputLayout.setErrorEnabled(true);
            makeInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_color));
            return;
        } else {
            makeInputLayout.setError(null);
            makeInputLayout.setErrorEnabled(false);
        }

        if (isFieldEmpty(modelInput)) {
            modelInputLayout.setError("Model cannot be empty");
            modelInputLayout.setErrorEnabled(true);
            modelInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_color));
            return;
        } else {
            modelInputLayout.setError(null);
            modelInputLayout.setErrorEnabled(false);
        }

        if (isFieldEmpty(serialNumberInput)) {
            serialNumberInputLayout.setError("Serial number cannot be empty");
            serialNumberInputLayout.setErrorEnabled(true);
            serialNumberInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_color));
            return;
        } else {
            serialNumberInputLayout.setError(null);
            serialNumberInputLayout.setErrorEnabled(false);
        }

        if (isFieldEmpty(valueInput) || !isValidValue(valueInput.getText().toString())) {
            valueInputLayout.setError("Invalid value");
            valueInputLayout.setErrorEnabled(true);
            valueInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_color));
            return;
        } else {
            valueInputLayout.setError(null);
            valueInputLayout.setErrorEnabled(false);
        }

        if (isFieldEmpty(commentInput)) {
            commentInputLayout.setError("Comment cannot be empty");
            commentInputLayout.setErrorEnabled(true);
            commentInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.error_color));
            return;
        } else {
            commentInputLayout.setError(null);
            commentInputLayout.setErrorEnabled(false);
        }

        if (isEdit) {
            updateItem();
        } else {
            createNewItem();
        }
    }


    /**
     * Checks if the date is in the correct format.
     *
     * @param date The date to check.
     * @return True if the date is in the correct format, false otherwise.
     */
    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Checks if the value is a valid number.
     *
     * @param value The value to check.
     * @return True if the value is a valid number, false otherwise.
     */
    private boolean isValidValue(String value) {
        try {
            double parsedValue = Double.parseDouble(value);
            return parsedValue >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the field is empty.
     *
     * @param editText The field to check.
     * @return True if the field is empty, false otherwise.
     */
    private boolean isFieldEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }


    /**
     * Creates a new item and adds it to the database.
     */
    private void createNewItem() throws ParseException {
        Item newItem = getItemFromFields();
        itemDBController.addItem(newItem, task -> {
            if (task.isSuccessful()) {
                // Log success
                Log.i("AddItem", "Item added successfully");
                // Call the method to upload images and update the item
                itemDBController.uploadImagesWithItemURl(newItem.getImageUrls(), imagesData, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("UploadImages", "Images uploaded and item updated successfully");
                            Toast.makeText(AddEditItem.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("newItem", newItem);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Log.e("UploadImages", "Error uploading images: " + task.getException().getMessage());
                        }
                    }
                });
            } else {
                // This block handles the failure case
                Exception e = task.getException();
                // Log failure
                Log.e("AddItem", "Failed to add item", e);

                Toast.makeText(this, "Failed to add item" + (e != null ? ": " + e.getMessage() : ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the item in the database.
     */
    private void updateItem() throws ParseException {
        Item updatedItem = getItemFromFields();

        itemDBController.editItem(updatedItem.getItemID(), updatedItem, task -> {
            if (task.isSuccessful()) {
                // Log success
                Log.i("EditItem", "Item updated successfully");
                itemDBController.uploadImagesWithItemURl(imageURLs, imagesData, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("UploadImages", "Images uploaded and item updated successfully");
                            Toast.makeText(AddEditItem.this, "Item Updated", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("updatedItem", updatedItem);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Log.e("UploadImages", "Error uploading images: " + task.getException().getMessage());
                        }
                    }
                });
            } else {
                // This block handles the failure case
                Exception e = task.getException();
                // Log failure
                Log.e("EditItem", "Failed to update item", e);

                Toast.makeText(this, "Failed to update item" + (e != null ? ": " + e.getMessage() : ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gets the item details from the fields.
     *
     * @return The item object.
     */
    private Item getItemFromFields() throws ParseException {
        String description = descriptionInput.getText().toString();
        String make = makeInput.getText().toString();
        String model = modelInput.getText().toString();
        String serialNumber = serialNumberInput.getText().toString();
        double value = Double.parseDouble(valueInput.getText().toString());
        String comment = commentInput.getText().toString();
        Date acquisitionDate = sdf.parse(dateInput.getText().toString());

        // get selected tag
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            if (chip.isChecked()) {
                Tag selectedtag = (Tag) chip.getTag();
                selectedTagList.add(selectedtag);
            }
        }
        if (currentItem != null) {
            // If this is an edit, update the existing item's fields
            currentItem.setBriefDescription(description);
            currentItem.setMake(make);
            currentItem.setModel(model);
            currentItem.setSerialNumber(serialNumber);
            currentItem.setEstimatedValue(value);
            currentItem.setComment(comment);
            currentItem.setDateOfAcquisition(acquisitionDate);
            currentItem.setTags(selectedTagList);

            for (int i = 0; i < imagesData.size(); i++) {
                String filePath = "images/" + currentItem.getItemID() + "/" + UUID.randomUUID().toString() + ".jpg";
                imageURLs.add(filePath);
            }
            if (previousUrlsList.isEmpty()){
                previousUrlsList = currentItem.getImageUrls();
            }
            previousUrlsList.addAll(imageURLs);
            currentItem.setImageUrls(previousUrlsList);
            return currentItem;
        }

        Item item = new Item(
                acquisitionDate,
                description,
                make,
                model,
                serialNumber,
                value,
                comment,
                selectedTagList,
                imageURLs);
        for (int i = 0; i < imagesData.size(); i++) {
            String filePath = "images/" + item.getItemID() + "/" + UUID.randomUUID().toString() + ".jpg";
            imageURLs.add(filePath);
        }
        item.setImageUrls(imageURLs);
        return item;
    }

    /**
     * Handles the result from the date picker. If the result is OK, the selected date is set in the
     * date input field.
     *
     * @param year  The year that was picked.
     * @param month The month that was picked.
     * @param day   The day that was picked.
     */
    @Override
    public void onSingleDatePicked(int year, int month, int day) {
        Date selectedDateObject = new GregorianCalendar(year, month, day).getTime();
        dateInput.setText(sdf.format(selectedDateObject));
    }

    /**
     * Handles the result from the add tag activity. If the result is OK, the selected tag is added
     * to the chip group.
     *
     * @param result The result data from the activity.
     */
    private void handleAddTagResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Tag returnedTag = (Tag) result.getData().getSerializableExtra("newTag");
            if (returnedTag != null) {
                Chip chip = new Chip(AddEditItem.this);
                chip.setText(returnedTag.getTagName());
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(returnedTag.getTagColor())));
                chip.setCheckedIconVisible(true);
                chip.setCheckable(true);
                chip.setTag(returnedTag);
                // make it so that new created tag is selected
                chip.setChecked(true);
                // Add the chip to the ChipGroup
                chipGroupTags.addView(chip);
            }
        }
    }

    /**
     * Handles the result from the image adding activity. If the result is OK, it retrieves the selected image URIs
     * and adds them to the imagesData list.
     *
     * @param result The result data from the image adding activity, containing the URIs of the selected images.
     */
    private void handleAddImageResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            if (isEdit) {
                ArrayList<String> previousUrls = result.getData().getStringArrayListExtra("previousUrls");
                if (previousUrls != null) {
                    previousUrlsList = new ArrayList<>(previousUrls);
                }
            }

            // Retrieve the ArrayList of Uri objects
            ArrayList<Uri> selectedUris = result.getData().getParcelableArrayListExtra("itemImagesData");
            if (selectedUris != null) {
                // Add all the Uri objects to your imagesData list
                imagesData.addAll(selectedUris);
            }
        }
    }

    /**
     * Handles the result from the serial number scanner activity. If the result is OK, it sets the scanned serial number
     * to the serialNumberInput field.
     *
     * @param result The result data from the serial scanner activity, containing the scanned serial number.
     */
    private void handleSerialScannerResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            String serialNumber = result.getData().getStringExtra("serialNumber");
            serialNumberInput.setText(serialNumber);
        }
    }

    /**
     * Handles the result from the barcode scanner activity. If the result is OK, it sets the scanned product's
     * description to the descriptionInput field.
     *
     * @param result The result data from the barcode scanner activity, containing the product description.
     */
    private void handleBarcodeScannerResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            String description = result.getData().getStringExtra("productDescription");
            descriptionInput.setText(description); // Set the scanned product description
        }
    }

}
