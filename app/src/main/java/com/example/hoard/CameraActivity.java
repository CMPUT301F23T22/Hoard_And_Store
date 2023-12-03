package com.example.hoard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Activity for handling camera operations. This activity starts the camera, shows a preview, and handles image capture.
 */
public class CameraActivity extends AppCompatActivity {

    private static final int IMAGE_CAPTURE_FAILURE_RESULT_CODE = 2; // A distinct result code for capture failure
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initialize camera preview
        PreviewView previewView = findViewById(R.id.previewView);
        startCamera(previewView);

        // Setup button for capturing photo
        Button captureButton = findViewById(R.id.captureButton);
        if (getIntent().getBooleanExtra("fromBarcodeScanner", false)) {
            captureButton.setText("Auto Gen Description");
        }
        captureButton.setOnClickListener(v -> takePhoto());

        // Button to close the activity
        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());
    }

    /**
     * Starts the camera and binds it to the lifecycle of this activity.
     *
     * @param previewView The view to display the camera preview.
     */
    private void startCamera(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraXApp", "Error starting camera: " + e.getMessage(), e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Handles the process of taking a photo.
     */
    private void takePhoto() {
        if (imageCapture == null) return;

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                try {
                    Bitmap capturedImage = imageProxyToBitmap(image);
                    // Handling the successful capture of an image
                    returnCapturedImage(capturedImage);
                } finally {
                    image.close();
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraXApp", "Photo capture failed: " + exception.getMessage(), exception);
                Intent failureIntent = new Intent();
                failureIntent.putExtra("error_message", exception.getMessage());
                setResult(IMAGE_CAPTURE_FAILURE_RESULT_CODE, failureIntent);
                finish();
            }
        });
    }

    /**
     * Converts an ImageProxy to a Bitmap.
     *
     * @param image The ImageProxy to convert.
     * @return A Bitmap representation of the ImageProxy.
     */
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Handles returning the captured image to the calling activity.
     *
     * @param capturedImage The captured image as a Bitmap.
     */
    private void returnCapturedImage(Bitmap capturedImage) {
        // Rotate the image for correct orientation
        capturedImage = rotateImage(capturedImage, 90);

        // Convert Bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Return the image as a result
        Intent returnIntent = new Intent();
        returnIntent.putExtra("capturedImage", byteArray);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Rotates a Bitmap by the specified degrees.
     *
     * @param img    The Bitmap to rotate.
     * @param degree The number of degrees to rotate the image.
     * @return The rotated Bitmap.
     */
    private Bitmap rotateImage(Bitmap img, int degree) {
        // https://stackoverflow.com/questions/63921260/android-camerax-image-rotated
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
