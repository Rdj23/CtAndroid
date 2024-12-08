package com.example.ctandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private CleverTapAPI cleverTapInstance;
    private Uri imageUri;
    private ImageView profileImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize CleverTap
        cleverTapInstance = CleverTapAPI.getDefaultInstance(this);

        // Bind UI elements
        EditText nameInput = findViewById(R.id.name);
        EditText subscriptionTypeInput = findViewById(R.id.subscriptionType);
        profileImage = findViewById(R.id.profileImage);
        Button uploadButton = findViewById(R.id.uploadBtn);
        Button saveButton = findViewById(R.id.saveButton);

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        profileImage.setImageURI(imageUri); // Show the chosen image in the ImageView
                    }
                }
        );

        // Open image picker when upload button is clicked
        uploadButton.setOnClickListener(view -> openImagePicker());

        // Save profile details when save button is clicked
        saveButton.setOnClickListener(view -> {
            String name = nameInput.getText().toString().trim();
            String subscriptionType = subscriptionTypeInput.getText().toString().trim();

            if (name.isEmpty() || subscriptionType.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> profileUpdate = new HashMap<>();
            profileUpdate.put("Name", name);
            profileUpdate.put("Subscription Type", subscriptionType);

            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    String base64Image = convertBitmapToBase64(bitmap);
                    profileUpdate.put("Photo", base64Image);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to process the image.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
            }

            cleverTapInstance.pushProfile(profileUpdate);
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }
}
