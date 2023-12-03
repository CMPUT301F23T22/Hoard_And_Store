package com.example.hoard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.util.Base64;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity allows users to add images. It provides options to capture an image using the camera
 * or to select images from the device's gallery.
 */
public class AddImage extends AppCompatActivity {

    private static final int IMAGE_CAPTURE_FAILURE_RESULT_CODE = 2; // A distinct result code for failure
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    this::handlePermissionResult);
    private ViewPager2 viewPager;
    private ImageCarouselAdapter adapter;
    private List<Bitmap> images = new ArrayList<>();
    // Launchers for various activity results
    private final ActivityResultLauncher<Intent> captureImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleCaptureResult);
    private final ActivityResultLauncher<String> selectImageResultLauncher =
            registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
                    this::handleImageSelection);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_handling);

        // Setup the ViewPager for image carousel
        viewPager = findViewById(R.id.viewPagerImageCarousel);
        adapter = new ImageCarouselAdapter(images); // Assuming ImageCarouselAdapter is defined elsewhere
        viewPager.setAdapter(adapter);

        // Initialize buttons
        Button btnCapture = findViewById(R.id.btnCapture);
        Button btnSelect = findViewById(R.id.btnSelect);
        Button btnConfirm = findViewById(R.id.submitButton);

        // Cancel button to close the activity
        Button btnCancel = findViewById(R.id.closeButton);
        btnCancel.setOnClickListener(v -> finish());

        // Capture button listener
        btnCapture.setOnClickListener(view -> {
            if (checkPermission(Manifest.permission.CAMERA)) {
                captureImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Select button listener
        btnSelect.setOnClickListener(view -> {
            if (checkPermission(Manifest.permission.READ_MEDIA_IMAGES)) {
                selectImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        });

        // Confirm button listener
        btnConfirm.setOnClickListener(view -> confirmSelection());
    }

    /**
     * Checks if the specified permission is granted.
     *
     * @param permission The permission to check.
     * @return true if permission is granted, false otherwise.
     */
    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Starts an activity to capture an image.
     */
    private void captureImage() {
        Intent takePictureIntent = new Intent(this, CameraActivity.class);
        captureImageResultLauncher.launch(takePictureIntent);
    }

    /**
     * Starts an activity to select an image.
     */
    private void selectImage() {
        selectImageResultLauncher.launch("image/*");
    }

    /**
     * Handles the action after confirming the selection of images.
     */
    private void confirmSelection() {
        if (!images.isEmpty()) {
            Intent resultIntent = new Intent();
            Bitmap selectedImage = images.get(0);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

            resultIntent.putExtra("selectedImageData", base64Image);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    /**
     * Handles the result of capturing an image.
     *
     * @param result The result of the image capture activity.
     */
    private void handleCaptureResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            byte[] byteArray = result.getData().getByteArrayExtra("capturedImage");

            String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedImageData", base64Image);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else if (result.getResultCode() == IMAGE_CAPTURE_FAILURE_RESULT_CODE) {
            String errorMessage = result.getData() != null ? result.getData().getStringExtra("error_message") : "Unknown error";
            Toast.makeText(this, "Error capturing image: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles the result of selecting images.
     *
     * @param uris List of URIs of the selected images.
     */
    private void handleImageSelection(List<Uri> uris) {
        for (Uri uri : uris) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    images.add(bitmap);
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Handles the result of a permission request.
     *
     * @param isGranted True if the permission was granted, false otherwise.
     */
    private void handlePermissionResult(boolean isGranted) {
        if (isGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}
