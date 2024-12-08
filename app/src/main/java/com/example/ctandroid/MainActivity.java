package com.example.ctandroid;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;

import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.interfaces.NotificationHandler;
import com.google.firebase.auth.FirebaseAuth;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CTInboxListener {
    FirebaseAuth firebaseAuth;
    CleverTapAPI clevertapDefaultInstance;
    Button btnInbox;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get CleverTap instance
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this);


        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        CleverTapAPI.setNotificationHandler((NotificationHandler)new PushTemplateNotificationHandler());
        clevertapDefaultInstance.setCTNotificationInboxListener(this);
        //Initialize the inbox and wait for callbacks on overridden methods
        clevertapDefaultInstance.initializeInbox();
        CleverTapAPI.createNotificationChannel(getApplicationContext(), "Pushmsg", "testing", "YourChannelDescription", NotificationManager.IMPORTANCE_MAX, true);

        CleverTapAPI.setNotificationHandler((NotificationHandler) new PushTemplateNotificationHandler());
        assert clevertapDefaultInstance != null;

        firebaseAuth = FirebaseAuth.getInstance();

        EditText nameInput = findViewById(R.id.name);
        EditText emailInput = findViewById(R.id.email);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        Button skipButton = findViewById(R.id.skipButton);
        Button inApp = findViewById(R.id.inAppNotification);
        btnInbox = findViewById(R.id.appInbox);




        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String name = nameInput.getText().toString().trim();

            HashMap<String, Object> profileUpdate = new HashMap<>();
            profileUpdate.put("Name", name);    // String
            profileUpdate.put("Identity", name);
            profileUpdate.put("Email", email);

            // Notification preferences
            profileUpdate.put("MSG-email", true);        // Enable email notifications
            profileUpdate.put("MSG-push", true);         // Enable push notifications
            profileUpdate.put("MSG-sms", true);          // Enable SMS notifications
            profileUpdate.put("MSG-whatsapp", true);

            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.onUserLogin(profileUpdate);  // Send user data to CleverTap
            }

            // Sign up or login
            firebaseAuth.signInWithEmailAndPassword(email, name)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeScreenActivity.class));
                        } else {
                            Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class)); // Navigate to RegisterActivity
        });

        skipButton.setOnClickListener(v -> {
            // Optional: Redirect to another activity or handle skipping functionality.
            Toast.makeText(this, "Skipped login!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeScreenActivity.class));
        });

        inApp.setOnClickListener(v -> {
            Toast.makeText(this, "In-App!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InApp.class));
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (clevertapDefaultInstance != null && !clevertapDefaultInstance.isPushPermissionGranted()) {
            JSONObject jsonObject = CTLocalInApp.builder()
                    .setInAppType(CTLocalInApp.InAppType.ALERT)
                    .setTitleText("Get Notified")
                    .setMessageText("Enable Notification permission")
                    .followDeviceOrientation(true)
                    .setPositiveBtnText("Allow")
                    .setNegativeBtnText("Cancel")
                    .build();
            clevertapDefaultInstance.promptPushPrimer(jsonObject);
        }
    }


    @Override
    public void inboxDidInitialize() {

        btnInbox.setOnClickListener(v -> {
            ArrayList<String> tabs = new ArrayList<>();
            tabs.add("Promotions");
            tabs.add("Offers");//We support upto 2 tabs only. Additional tabs will be ignored

            CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
            styleConfig.setFirstTabTitle("First Tab");
            styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
            styleConfig.setTabBackgroundColor("#FF0000");
            styleConfig.setSelectedTabIndicatorColor("#0000FF");
            styleConfig.setSelectedTabColor("#0000FF");
            styleConfig.setUnselectedTabColor("#FFFFFF");
            styleConfig.setBackButtonColor("#FF0000");
            styleConfig.setNavBarTitleColor("#FF0000");
            styleConfig.setNavBarTitle("MY INBOX");
            styleConfig.setNavBarColor("#FFFFFF");
            styleConfig.setInboxBackgroundColor("#ADD8E6");
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
            }
            //ct.showAppInbox();//Opens Activity with default style configs
        });
    }

    @Override
    public void inboxMessagesDidUpdate() {

    }
}





