package com.example.hoard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for adding images to an item. This class handles capturing new images using the camera,
 * selecting images from the device's storage, and returning the selected images.
 */
public class AddImage extends AppCompatActivity{

    private static final int IMAGE_CAPTURE_FAILURE_RESULT_CODE = 2;
    private ViewPager2 viewPager;
    private TextView addEditHeader;
    private ImageCarouselAdapter adapter;
    private final List<Uri> images = new ArrayList<>();

    private final List<Uri> newImages = new ArrayList<>();

    private boolean isEdit;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private Item currentItem;

    private List<String> imageUrls;

    // Launchers for handling different results
    private final ActivityResultLauncher<Intent> captureImageResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    this::handleCaptureResult);
    private final ActivityResultLauncher<String> selectImageResultLauncher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
                    this::handleImageSelection);
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    this::handlePermissionResult);
    /**
     * Initializes the activity, sets up the ViewPager for image carousel, and configures button listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_handling);

        // Initialize the ViewPager2 and the ImageCarouselAdapter
        viewPager = findViewById(R.id.viewPagerImageCarousel);
        adapter = new ImageCarouselAdapter(images, this);
        viewPager.setAdapter(adapter);

        // Initialize UI elements and set up event listeners
        Button btnCapture = findViewById(R.id.btnCapture);
        Button btnSelect = findViewById(R.id.btnSelect);
        Button btnConfirm = findViewById(R.id.submitButton);
        Button btnCancel = findViewById(R.id.closeButton);
        Button btnDelete = findViewById(R.id.btnDelete);
        addEditHeader = findViewById(R.id.addEditHeader);

        Intent intent = getIntent();
        if (intent.hasExtra("CURRENT_ITEM")) {
            currentItem = (Item) intent.getSerializableExtra("CURRENT_ITEM");
            isEdit = true;
            imageUrls = currentItem.getImageUrls();
            loadImages(imageUrls);
            addEditHeader.setText("Edit Photo");
        } else {
            imageUrls = new ArrayList<>();
        }

        // Set up listeners for buttons
        btnCancel.setOnClickListener(v -> finish());
        btnCapture.setOnClickListener(view -> {
            if (checkPermission(Manifest.permission.CAMERA)) {
                captureImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnSelect.setOnClickListener(view -> {
            if (checkPermission(Manifest.permission.READ_MEDIA_IMAGES)) {
                selectImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        });

        btnDelete.setOnClickListener(view -> {
            int currentPosition = viewPager.getCurrentItem();
            if (!images.isEmpty() && currentPosition < images.size()) {
                Uri imageUriToDelete = images.get(currentPosition);
                if (isEdit) { // Check if it's an edit operation
                    // Delete the image from Firestore
                    String imageLocationToDelete = imageUrls.get(currentPosition);
                    deleteImageFromDatabase(imageLocationToDelete);
                }
                // Handle the delete operation as before if it's not an edit operation
                deleteImageLocally(currentPosition);

            } else {
                Toast.makeText(this, "No images to delete", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(view -> confirmSelection());
    }

    private void deleteImageFromDatabase(String imageLocationToDelete) {
        ItemDBController.getInstance().deleteImage(imageLocationToDelete, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddImage.this, "Image successfully deleted", Toast.LENGTH_SHORT).show();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddImage.this, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteImageLocally(int position) {
        if(position < images.size() && position < imageUrls.size()) {
            images.remove(position);
            imageUrls.remove(position);
            adapter.notifyDataSetChanged();
        }
    }

    private void loadImages(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Initialize placeholders for each image URL
            for (int i = 0; i < imageUrls.size(); i++) {
                images.add(Uri.EMPTY); // Placeholder for each image
            }
            for (int i = 0; i < imageUrls.size(); i++) {
                String currentImagePath = imageUrls.get(i);
                if (currentImagePath != null && !currentImagePath.isEmpty()) {
                    final int index = i; // Final index for use inside the listener
                    StorageReference imageRef = storageRef.child(currentImagePath);
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        images.set(index, downloadUri); // Replace placeholder with actual URI
                        adapter.notifyDataSetChanged();
                    }).addOnFailureListener(exception ->
                            Toast.makeText(AddImage.this, "Failed to load image: " + exception.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        }
    }


    /**
     * Checks if a specific permission is granted.
     *
     * @param permission The permission to check.
     * @return true if the permission is granted, false otherwise.
     */
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Initiates the process to capture an image.
     */
    private void captureImage() {
        // Create an intent to open the camera activity
        Intent takePictureIntent = new Intent(this, CameraActivity.class);
        captureImageResultLauncher.launch(takePictureIntent);
    }

    /**
     * Initiates the process to select an image from the device storage.
     */
    private void selectImage() {
        selectImageResultLauncher.launch("image/*");
    }

    /**
     * Confirms the image selection and returns the selected images to the calling activity.
     */
    private void confirmSelection() {
        if (!images.isEmpty()) {
            Intent resultIntent = new Intent();
            if (isEdit){
                ArrayList<Uri> selectedUrisEdit = new ArrayList<>(newImages);
                resultIntent.putParcelableArrayListExtra("itemImagesData", selectedUrisEdit);
                resultIntent.putStringArrayListExtra("previousUrls", new ArrayList<>(imageUrls));
            } else {
                ArrayList<Uri> selectedUris = new ArrayList<>(images);
                resultIntent.putParcelableArrayListExtra("itemImagesData", selectedUris);
            }
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    /**
     * Handles the result from the image capture activity.
     * Adds the captured image URI to the list of images and updates the adapter.
     *
     * @param result The result data from the image capture activity.
     */
    private void handleCaptureResult(ActivityResult result) {

        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            // Get the URI string of the captured image
            String imageUriString = result.getData().getStringExtra("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                // Add the URI of the captured image to your list and notify the adapter
                images.add(imageUri);
                adapter.notifyDataSetChanged();
                if (isEdit){
                    newImages.add(imageUri);
                }
            } else {
                Toast.makeText(this, "Error retrieving image", Toast.LENGTH_LONG).show();
            }
        } else if (result.getResultCode() == IMAGE_CAPTURE_FAILURE_RESULT_CODE) {
            String errorMessage = result.getData() != null ? result.getData().getStringExtra("error_message") : "Unknown error";
            Toast.makeText(this, "Error capturing image: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles the result from selecting images from the device storage.
     * Adds all selected image URIs to the list and updates the adapter.
     *
     * @param uris The URIs of the selected images.
     */
    private void handleImageSelection(List<Uri> uris) {
        for (Uri uri : uris) {
            try {
                // Take persistable URI permission to access the URI across different components
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                // Add URI to your list
                images.add(uri);
                if (isEdit) {
                    newImages.add(uri);
                }
            } catch (SecurityException e) {
                Log.e("ImagePicker", "Error taking persistable URI permission", e);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Handles the result of the permission request, displaying a toast message based on the result.
     *
     * @param isGranted Indicates whether the permission was granted.
     */
    private void handlePermissionResult(boolean isGranted) {
        if (isGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

}