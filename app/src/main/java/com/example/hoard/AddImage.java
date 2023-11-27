package com.example.hoard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddImage extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageCarouselAdapter adapter;
    private List<Bitmap> images = new ArrayList<>();

    // ActivityResultLauncher for capturing an image
    private final ActivityResultLauncher<Intent> captureImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    images.add(imageBitmap);
                    adapter.notifyDataSetChanged();
                }
            });

    // ActivityResultLauncher for selecting an image
    private final ActivityResultLauncher<Intent> selectImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        // Multiple images selected
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                                images.add(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else if (result.getData().getData() != null) {
                        // Single image selected
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            images.add(bitmap);
                            adapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    // ActivityResultLauncher for requesting permissions
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_handling);

        viewPager = findViewById(R.id.viewPagerImageCarousel);
        adapter = new ImageCarouselAdapter(images);
        viewPager.setAdapter(adapter);

        Button btnCapture = findViewById(R.id.btnCapture);
        Button btnSelect = findViewById(R.id.btnSelect);
        Button btnConfirm = findViewById(R.id.submitButton);

        // Cancel button listener
        Button btnCancel = findViewById(R.id.closeButton);
        btnCancel.setOnClickListener(v -> finish());

        btnCapture.setOnClickListener(view -> {
            if (checkPermission(Manifest.permission.CAMERA)) {
                captureImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnSelect.setOnClickListener(view -> {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                selectImage();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        btnConfirm.setOnClickListener(view -> confirmSelection());
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageResultLauncher.launch(takePictureIntent);
    }

    private void selectImage() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageResultLauncher.launch(pickPictureIntent);
    }

    private void confirmSelection() {
        // Handle confirmation, e.g., pass images back to calling activity or upload
    }
}
