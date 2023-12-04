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
 * Activity class for scanning barcodes and retrieving product information.
 * This class uses the device's camera to scan barcodes and then fetches product information
 * from an external API. It also provides the functionality to return the scanned data to the calling activity.
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
     * Initializes the activity, setting up the user interface and binding actions to buttons.
     * It configures the barcode scanning and network request functionalities.
     *
     * @param savedInstanceState Contains the data most recently provided in onSaveInstanceState(Bundle) if the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        progressBar = findViewById(R.id.progressBar);
        barcodeNumberText = findViewById(R.id.scannedBarcode);
        scanButton = findViewById(R.id.BarcodeScan);
        AutogenDescription = findViewById(R.id.autodescriptiontext);

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
                // Show the ProgressBar on the UI thread
                runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
                fetchProductInfo(barcodeValue);
            } else {
                Toast.makeText(BarcodeScannerActivity.this, "No barcode scanned", Toast.LENGTH_SHORT).show();
            }
        });

        CloseBtn = findViewById(R.id.DiscriptionCloseButton);
        AddDescriptionBtn = findViewById(R.id.AddDescriptionBtn);

        // Set up the Close button
        CloseBtn.setOnClickListener(view -> {
            finish(); // Close the activity
        });

        // Set up the Add Description button
        AddDescriptionBtn.setOnClickListener(view -> {
            String description = AutogenDescription.getText().toString(); // Assuming you want to return the barcode number
            // Return the description to the calling activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra("productDescription", description);
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
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.barcodelookup.com/v3/products?barcode=" + barcodeValue + "&formatted=y&key=" + API_KEY)
                .build();

        // Asynchronous network request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Hide the ProgressBar and show Toast on the UI thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(BarcodeScannerActivity.this, "Failed to auto-gen description", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(BarcodeScannerActivity.class.getSimpleName(), "Unexpected code " + response);
                    // Hide the ProgressBar on the UI thread
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    return;
                }
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray products = jsonObject.getJSONArray("products");

                    if (products.length() > 0) {
                        JSONObject firstProduct = products.getJSONObject(0);
                        String description = firstProduct.optString("description", "No description available.");

                        // Update the TextView with the description on the UI thread
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            AutogenDescription.setText(description);
                        });
                    } else {
                        Log.d(BarcodeScannerActivity.class.getSimpleName(), "No products found");
                        // Update UI for no products found on the UI thread
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            AutogenDescription.setText("No description available.");
                        });
                    }
                } catch (JSONException e) {
                    Log.e(BarcodeScannerActivity.class.getSimpleName(), "Json parsing error: " + e.getMessage());
                    // Hide the ProgressBar on the UI thread
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                }
            }
        });
    }
}
