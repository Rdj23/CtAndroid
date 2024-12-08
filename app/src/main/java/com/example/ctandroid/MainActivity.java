package com.example.ctandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getFirebase Instance

        firebaseAuth = FirebaseAuth.getInstance();


        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance((this));


        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        assert clevertapDefaultInstance != null;

        EditText nameInput = findViewById(R.id.name);
        EditText emailInput = findViewById(R.id.email);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        Button skipButton = findViewById(R.id.skipButton);

        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String name = nameInput.getText().toString().trim();

//            if (email.isEmpty() || name.isEmpty()) {
//                Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Welcome, " + email, Toast.LENGTH_SHORT).show();
//            }
//            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("userName", name); // Store the name
//            editor.apply(); // Commit changes


            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put("Name", name);    // String
            profileUpdate.put("Identity", name);
            profileUpdate.put("Email", email);

            //coms
            profileUpdate.put("MSG-email", true);        // Disable email notifications
            profileUpdate.put("MSG-push", true);          // Enable push notifications
            profileUpdate.put("MSG-sms", true);          // Disable SMS notifications
            profileUpdate.put("MSG-whatsapp", true);

            clevertapDefaultInstance.onUserLogin(profileUpdate);


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
            startActivity(new Intent(this, RegisterActivity.class)); // Navigate to RegisterActivity.
        });

        skipButton.setOnClickListener(v -> {
            // Optional: Redirect to another activity or handle skipping functionality.
            Toast.makeText(this, "Skipped login!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeScreenActivity.class));
        });
    }
}
