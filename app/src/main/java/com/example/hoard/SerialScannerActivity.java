package com.example.hoard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

/**
 * This activity allows users to scan serial numbers by using OCR text recognition
 */
public class SerialScannerActivity extends AppCompatActivity {

    // Request code for camera permission
    private static final int REQUEST_CAMERA_PERMISSION = 123;

    // UI elements
    private TextView scannedSerialNumber;
    private Button scanButton;
    private Button closeButton;
    private Button AddSerialBtn;

    /**
     * Activity result launcher for serial camera activity intent
     */
    private final ActivityResultLauncher<Intent> serialLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        processImage(InputImage.fromBitmap(imageBitmap, 0));
                    }
                }
            }
    );
    /**
     * Creates the serial scanner activity which allows users to scan serial numbers by using OCR text recognition
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_scanner);

        // UI elements
        scannedSerialNumber = findViewById(R.id.scannedSerial);
        scanButton = findViewById(R.id.SerialScan);
        closeButton = findViewById(R.id.CloseButton);
        AddSerialBtn = findViewById(R.id.AddSerialNumber);

        /**
         * Sets up the scan button to start the camera activity
         */
        scanButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                startCamera();
            }
        });

        /**
         * Sets up the close button to close the activity
         */
        closeButton.setOnClickListener(view -> finish());

        /**
         * Sets up the add serial button to return the serial number to the calling activity
         */
        AddSerialBtn.setOnClickListener(view -> {
            String serialNumber = scannedSerialNumber.getText().toString(); // Assuming you want to return the barcode number
            // Return the description to the calling activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra("serialNumber", serialNumber);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    /**
     * Starts the camera activity
     */
    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        serialLauncher.launch(cameraIntent);
    }

    /**
     * Processes the image taken by the camera
     * @param image The image taken by the camera
     */
    private void processImage(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(this::displayResult)
                .addOnFailureListener(
                        e -> Toast.makeText(SerialScannerActivity.this, "Error processing image", Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays the result of the OCR text recognition
     * @param text The text recognized by the OCR text recognition
     */
    private void displayResult(Text text) {
        StringBuilder resultText = new StringBuilder();
        for (Text.TextBlock block : text.getTextBlocks()) {
            resultText.append(block.getText()).append("\n");
        }
        scannedSerialNumber.setText(resultText.toString());
    }

    /**
     * Handles the result of the camera permission request
     * @param requestCode The request code
     * @param permissions The permissions requested
     * @param grantResults The results of the permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
