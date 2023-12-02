package com.example.hoard;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.BarcodeScanner;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BarcodeScannerActivity extends Activity {

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final String API_KEY = "YOUR_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to your layout
        // setContentView(R.layout.activity_barcode_scanner);

        // Request camera permission and start CameraActivity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCameraActivity();
        }
    }

    private void startCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            byte[] byteArray = data.getByteArrayExtra("capturedImage");
            Bitmap capturedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            processImage(InputImage.fromBitmap(capturedImage, 0));
        }
    }

    // Method to process the image and detect barcodes
    private void processImage(InputImage image) {
        BarcodeScanner scanner = BarcodeScanning.getClient();

        scanner.process(image)
                .addOnSuccessListener(this::onBarcodesDetected)
                .addOnFailureListener(e -> Log.e(BarcodeScannerActivity.class.getSimpleName(), "Barcode scanning failed", e));
    }

    private void onBarcodesDetected(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String rawValue = barcode.getRawValue();
            Log.d(BarcodeScannerActivity.class.getSimpleName(), "Barcode found: " + rawValue);
            fetchProductInfo(rawValue);
        }
    }

    // Method to make an API call to the Barcode Lookup API
    private void fetchProductInfo(String barcodeValue) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.barcodelookup.com/v2/products?barcode=" + barcodeValue + "&formatted=y&key=" + API_KEY)
                .build();

        // Asynchronous network request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                // Handle the API response
                String responseData = response.body().string();
                Log.d(BarcodeScannerActivity.class.getSimpleName(), "Response from API: " + responseData);
                // TODO: Parse and use the response data as needed
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // startCamera(); // Uncomment this when you implement startCamera method
            } else {
                // Handle the case where the user denies the permission
            }
        }
    }

    // ... Include methods to handle camera initialization, permission requests, etc.
}
