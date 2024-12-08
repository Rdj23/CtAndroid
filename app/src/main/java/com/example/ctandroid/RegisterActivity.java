package com.example.ctandroid;

import android.annotation.SuppressLint;
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

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private  CleverTapAPI clevertapDefaultInstance;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get instance
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this);

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        assert clevertapDefaultInstance != null;





        firebaseAuth = FirebaseAuth.getInstance();

        EditText nameInput = findViewById(R.id.name);
        EditText emailInput = findViewById(R.id.email);
        Button registerButton = findViewById(R.id.loginButton); // Note: This will act as the register button.
        Button skipButton = findViewById(R.id.skipButton);

        registerButton.setText("Register"); // Update button text for clarity.

        registerButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = nameInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and name", Toast.LENGTH_SHORT).show();
                return;
            }


            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put("Name", password);    // String
            profileUpdate.put("Identity", password);
            profileUpdate.put("Email", email);

            //coms
            profileUpdate.put("MSG-email", true);        // Disable email notifications
            profileUpdate.put("MSG-push", true);          // Enable push notifications
            profileUpdate.put("MSG-sms", true);          // Disable SMS notifications
            profileUpdate.put("MSG-whatsapp", true);

            clevertapDefaultInstance.onUserLogin(profileUpdate);

//            SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("userName", password); // Store the name
//            editor.apply(); // Commit changes



            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeScreenActivity.class)); // Redirect to login.

                        } else {
                            Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        });

        skipButton.setOnClickListener(view -> {
            // Optional: Redirect to another activity or handle skipping functionality.
            Toast.makeText(this, "Skipped registration!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeScreenActivity.class));
        });
    }
}
