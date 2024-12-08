package com.example.ctandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.clevertap.android.sdk.CleverTapAPI;
import java.util.HashMap;

public class HomeScreenActivity extends AppCompatActivity {

    private Button buyNowButton;
    private ImageView profileIcon;
    private HashMap<String, Object> productDetails;
    private CleverTapAPI cleverTapInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize CleverTap
        cleverTapInstance = CleverTapAPI.getDefaultInstance(this);

        // Initialize the productDetails HashMap
        productDetails = new HashMap<>();

        // Find UI elements
        buyNowButton = findViewById(R.id.buy_now_button);
        profileIcon = findViewById(R.id.profile_icon);

        // Profile icon click listener
        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Select buttons for products
        Button selectProduct1 = findViewById(R.id.select_product1);
        Button selectProduct2 = findViewById(R.id.select_product2);

        // Set listeners for each product selection
        selectProduct1.setOnClickListener(view -> {
            updateProductDetails("Nike Shoes", 450);
            triggerProductClickedEvent("Nike Shoes", 450);
        });

        selectProduct2.setOnClickListener(view -> {
            updateProductDetails("Adidas Hoodie", 650);
            triggerProductClickedEvent("Adidas Hoodie", 650);
        });

        // Set listener for Buy Now button
        buyNowButton.setOnClickListener(view -> {
            if (!productDetails.isEmpty()) {
                triggerProductBoughtEvent(productDetails);
                Toast.makeText(HomeScreenActivity.this, "Product Bought", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeScreenActivity.this, "No product selected!", Toast.LENGTH_SHORT).show();
            }
        });

        // Disable Buy Now button by default
        buyNowButton.setEnabled(false);
    }

    private void updateProductDetails(String name, int price) {
        // Update product details
        productDetails.put("name", name);
        productDetails.put("price", price);

        // Enable Buy Now button and update its text
        buyNowButton.setEnabled(true);
        buyNowButton.setText("Buy Now (" + name + ")");
    }

    private void triggerProductClickedEvent(String name, int price) {
        // Create event details for the product clicked
        HashMap<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("name", name);
        eventDetails.put("price", price);

        // Send event to CleverTap
        if (cleverTapInstance != null) {
            cleverTapInstance.pushEvent("Product Clicked", eventDetails);
        }
    }

    private void triggerProductBoughtEvent(HashMap<String, Object> productDetails) {
        if (cleverTapInstance != null) {
            // Log the "Charged" event
            cleverTapInstance.pushEvent("Charged", productDetails);

            // Update user profile with the purchased product details
            HashMap<String, Object> profileUpdate = new HashMap<>();
            profileUpdate.put("PName", productDetails.get("name"));
            profileUpdate.put("Price", productDetails.get("price"));

            // Push the updated profile to CleverTap
            cleverTapInstance.pushProfile(profileUpdate);
        }
    }
}
