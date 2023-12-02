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
import android.widget.Toast;

import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.BarcodeScanner;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BarcodeScannerActivity extends Activity {

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final String API_KEY = "pfhjpg9m2en2k2e8sv2va0x55wxmeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request camera permission and start CameraActivity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCameraActivity();
        }
    }

    private void startCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("fromBarcodeScanner", true); // Indicating that this activity is started from BarcodeScannerActivity
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
        if (!barcodes.isEmpty()) {
            Barcode barcode = barcodes.get(0); // Get the first detected barcode
            String rawValue = barcode.getRawValue();
            Log.d(BarcodeScannerActivity.class.getSimpleName(), "Barcode found: " + rawValue);
            fetchProductInfo(rawValue);

            // Show a Toast message
            runOnUiThread(() -> Toast.makeText(BarcodeScannerActivity.this, "Barcode detected successfully!", Toast.LENGTH_SHORT).show());
        } else {
            Log.d(BarcodeScannerActivity.class.getSimpleName(), "No barcode found");
            //TODO: Handle the case when no barcode is detected
        }
    }


    // Method to make an API call to the Barcode Lookup API
    private void fetchProductInfo(String barcodeValue) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.barcodelookup.com/v3/products?barcode=" + barcodeValue + "&formatted=y&key=" + API_KEY)
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

                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray products = jsonObject.getJSONArray("products");

                    if (products.length() > 0) {
                        JSONObject firstProduct = products.getJSONObject(0);
                        String description = firstProduct.optString("description", "No description available.");

                        Log.d(BarcodeScannerActivity.class.getSimpleName(), "Description: " + description);
                        // Return the description to the calling activity
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("productDescription", description);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    } else {
                        Log.d(BarcodeScannerActivity.class.getSimpleName(), "No products found");
                        // Handle the case when no products are returned
                    }
                } catch (JSONException e) {
                    Log.e(BarcodeScannerActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
                }
            }

        });
    }

}
