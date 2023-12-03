package com.example.hoard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/**
 * Activity for scanning barcodes and fetching product information.
 * <p>
 * This activity allows the user to scan barcodes using the device's camera.
 * After scanning, it fetches product information from the Barcode Lookup API
 * and displays it in the UI. It also provides options to close the activity or
 * return the scanned barcode data to a calling activity.
 */
public class BarcodeScannerActivity extends AppCompatActivity {

    private static final String API_KEY = "pfhjpg9m2en2k2e8sv2va0x55wxmeg";
    private TextView barcodeNumberText;
    private Button scanButton;
    private TextView AutogenDescription;
    private Button getDescriptionBtn;

    private Button CloseBtn;
    private Button AddDescriptionBtn;
    private ProgressBar progressBar;
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String barcode = result.getContents();
                    barcodeNumberText.setText(barcode);
                    fetchProductInfo(barcode);
                }
            }
    );
    /**
     * Initializes the activity, setting up the UI and binding actions to buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains
     *                           the most recent data provided by onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        progressBar = findViewById(R.id.progressBar);
        barcodeNumberText = findViewById(R.id.scannedBarcode);
        scanButton = findViewById(R.id.BarcodeScan);

        scanButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 123);
            } else {
                startScanning();
            }
        });

        getDescriptionBtn = findViewById(R.id.GetDescriptionBtn);

        getDescriptionBtn.setOnClickListener(view -> {
            String barcodeValue = barcodeNumberText.getText().toString();
            if (!barcodeValue.isEmpty()) {
                fetchProductInfo(barcodeValue);
            } else {
                Toast.makeText(BarcodeScannerActivity.this, "No barcode scanned", Toast.LENGTH_SHORT).show();
            }
        });

        CloseBtn = findViewById(R.id.closeButton);
        AddDescriptionBtn = findViewById(R.id.AddDescriptionBtn);

        // Set up the Close button
        CloseBtn.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        // Set up the Add Description button
        AddDescriptionBtn.setOnClickListener(view -> {
            String description = barcodeNumberText.getText().toString(); // Assuming you want to return the barcode number
            Intent returnIntent = new Intent();
            returnIntent.putExtra("barcodeDescription", description);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }
    /**
     * Starts the barcode scanning process using ZXing's ScanOptions.
     *
     * This method sets up the scanning options and launches the barcode scanner.
     */
    private void startScanning() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a barcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }
    /**
     * Fetches product information based on the scanned barcode value.
     * <p>
     * Makes an asynchronous network request to the Barcode Lookup API using OkHttp.
     * On success, updates the UI with product information. On failure, displays a toast message.
     *
     * @param barcodeValue The barcode value for which product information is to be fetched.
     */
    private void fetchProductInfo(String barcodeValue) {
        progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.barcodelookup.com/v3/products?barcode=" + barcodeValue + "&formatted=y&key=" + API_KEY)
                .build();

        // Asynchronous network request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                runOnUiThread(() -> Toast.makeText(BarcodeScannerActivity.this, "Failed to auto-gen description", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray products = jsonObject.getJSONArray("products");

                    if (products.length() > 0) {
                        JSONObject firstProduct = products.getJSONObject(0);
                        String description = firstProduct.optString("description", "No description available.");

                        runOnUiThread(() -> {
                            AutogenDescription.setText(description); // Update the TextView with the description
                        });


                    } else {
                        Log.d(BarcodeScannerActivity.class.getSimpleName(), "No products found");
                        runOnUiThread(() -> {
                            AutogenDescription.setText("No description available."); // Update UI for no products found
                        });
                    }
                } catch (JSONException e) {
                    Log.e(BarcodeScannerActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
                }
            }

        });
    }

}
