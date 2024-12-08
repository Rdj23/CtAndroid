package com.example.ctandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.interfaces.NotificationHandler;

public class InApp extends AppCompatActivity {

    // Declare buttons
    Button halfIntButton, headerButton, alertButton;
    Button coverButton, imageIntButton;
    Button npsButton, userRatingsButton;
    Button customHtmlButton;

    private  CleverTapAPI cleverTapInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_in_app);


        // Initialize buttons using findViewById
        halfIntButton = findViewById(R.id.half_int_button);
        headerButton = findViewById(R.id.header_button);
        alertButton = findViewById(R.id.alert_button);

        coverButton = findViewById(R.id.cover_button);
        imageIntButton = findViewById(R.id.image_int_button);

        npsButton = findViewById(R.id.nps_button);
        userRatingsButton = findViewById(R.id.user_ratings_button);

        customHtmlButton = findViewById(R.id.custom_html_button);


        cleverTapInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setNotificationHandler((NotificationHandler)new PushTemplateNotificationHandler());

        if (cleverTapInstance != null){
            halfIntButton.setOnClickListener(v -> {
                cleverTapInstance.pushEvent("Half Interstitial");
                Toast.makeText(this, "Half Int!", Toast.LENGTH_SHORT).show();
            });


            alertButton.setOnClickListener(v -> {
                cleverTapInstance.pushEvent("Alert");
                Toast.makeText(this, "Alert!", Toast.LENGTH_SHORT).show();

            });


            userRatingsButton.setOnClickListener(v -> {
                cleverTapInstance.pushEvent("User Rating");
                Toast.makeText(this,"User Ratings" ,Toast.LENGTH_SHORT).show();
            });

        }




    }
}