package com.example.hoard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * CameraActivity is responsible for handling camera operations using CameraX library.
 * It captures a photo, saves it, and returns its URI.
 */
public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private static final String PHOTO_EXTENSION = ".jpg";
    private ImageCapture imageCapture;
    private File outputDirectory;

    private static final int IMAGE_CAPTURE_FAILURE_RESULT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initialize output directory for saving captured images
        outputDirectory = getOutputDirectory(CameraActivity.this);

        // Initialize camera preview view
        PreviewView previewView = findViewById(R.id.previewView);
        startCamera(previewView);

        // Setup capture button to take a photo
        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> takePhoto());

        // Close button to finish the activity
        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());
    }

    /**
     * Starts the camera with CameraX library.
     * @param previewView The view to render camera preview.
     */
    private void startCamera(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Prepare the preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Prepare the image capture use case
                imageCapture = new ImageCapture.Builder().build();

                // Choose the camera by default (usually the back camera)
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage(), e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Captures a photo, saves it in the device storage, and returns its URI.
     */
    private void takePhoto() {
        if (imageCapture != null) {
            // Create a timestamped file for the photo
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + PHOTO_EXTENSION;
            File photoFile = new File(outputDirectory, imageFileName);

            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    // Create URI from the saved file
                    Uri savedUri = Uri.fromFile(photoFile);
                    Log.d(TAG, "Photo capture succeeded: " + savedUri);

                    // Use the URI in the method to handle the captured image
                    returnCapturedImageUri(savedUri);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                    returnErrorResult(exception.getMessage());
                }
            });
        }
    }

    /**
     * Returns the captured image URI to the calling activity.
     * @param imageUri The URI of the captured image.
     */
    private void returnCapturedImageUri(Uri imageUri) {
        Intent returnIntent = new Intent();
        // Put the URI string into the return intent
        returnIntent.putExtra("imageUri", imageUri != null ? imageUri.toString() : null);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Gets the output directory for saving images.
     * @param context The application context.
     * @return The file representing the output directory.
     */
    private File getOutputDirectory(Context context) {
        File mediaStorageDir;

        mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Hoard_And_Store");

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e(TAG, "Failed to create directory");
        }

        return mediaStorageDir;
    }

    /**
     * Handles the return of an error result to the calling activity.
     * This method creates an intent, puts the error message into it,
     * sets the result code for indicating a failure, and finishes the activity.
     *
     * @param errorMessage The error message to be sent back to the calling activity.
     */
    private void returnErrorResult(String errorMessage) {
        // Creating an intent to hold the result data
        Intent returnIntent = new Intent();

        // Putting the error message into the intent
        returnIntent.putExtra("error_message", errorMessage);

        // Setting the result with a custom failure code and the intent
        setResult(IMAGE_CAPTURE_FAILURE_RESULT_CODE, returnIntent);

        // Finishing the activity, returning to the caller
        finish();
    }
}