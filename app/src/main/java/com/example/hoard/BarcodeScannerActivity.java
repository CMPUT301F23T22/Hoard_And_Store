package com.example.hoard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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

public class BarcodeScannerActivity extends AppCompatActivity {

    private static final String API_KEY = "pfhjpg9m2en2k2e8sv2va0x55wxmeg";
    private TextView barcodeNumberText;
    private Button scanButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        barcodeNumberText = findViewById(R.id.serialNumberInput);
        scanButton = findViewById(R.id.BarcodeScan);

        scanButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 123);
            } else {
                startScanning();
            }
        });
    }

    private void startScanning() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a barcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
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
