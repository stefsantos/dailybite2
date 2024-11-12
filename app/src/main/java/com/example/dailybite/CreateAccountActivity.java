package com.example.dailybite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput;
    private Button loginButton;
    private ImageView backButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        backButton = findViewById(R.id.back_button);

        // Handle back button press
        backButton.setOnClickListener(v -> finish());

        // Handle login button press (for account creation)
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(CreateAccountActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                createAccount(username, email, password);
            }
        });
    }

    private void createAccount(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Show a success toast
                            Toast.makeText(CreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            // Save additional user details in Firestore
                            saveUserDetails(user.getUid(), username, email);
                        }
                    } else {
                        // Display error message if account creation fails
                        Toast.makeText(CreateAccountActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String username, String email) {
        // Retrieve data from shared preferences for weight, height, age, etc.
        SharedPreferences weightPrefs = getSharedPreferences("WeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences heightPrefs = getSharedPreferences("HeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences agePrefs = getSharedPreferences("AgePrefs", Context.MODE_PRIVATE);
        SharedPreferences genderPrefs = getSharedPreferences("GenderPrefs", Context.MODE_PRIVATE);
        SharedPreferences activityPrefs = getSharedPreferences("ActivityLevelPrefs", Context.MODE_PRIVATE);

        // Retrieve saved values from shared preferences
        String weight = weightPrefs.getString("Weight", "");
        String weightUnit = weightPrefs.getBoolean("Unit", true) ? "kg" : "lbs";
        int heightMeters = heightPrefs.getInt("HeightMeters", 0);
        int heightCentimeters = heightPrefs.getInt("HeightCentimeters", 0);
        boolean isMetric = heightPrefs.getBoolean("UnitSystem", true);
        String height = isMetric ? heightMeters + "m " + heightCentimeters + "cm" : heightMeters + "ft " + heightCentimeters + "in";
        String age = agePrefs.getString("Age", "");
        String gender = genderPrefs.getString("SelectedGender", "");
        String activityLevel = activityPrefs.getString("SelectedActivityLevel", "");

        // Create a user object to store in Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);

        // Organize additional data under a "user_info" nested map
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("weight", weight);
        userInfo.put("weight_unit", weightUnit);
        userInfo.put("height", height);
        userInfo.put("age", age);
        userInfo.put("gender", gender);
        userInfo.put("activity_level", activityLevel);

        // Add userInfo as a nested field within the user map
        user.put("user_info", userInfo);

        // Add user document to Firestore in the "users" collection with the UID as the document ID
        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    // Show welcome message and navigate to Homepage on success
                    Toast.makeText(CreateAccountActivity.this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateAccountActivity.this, Homepage.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Display an error if saving details fails
                    Toast.makeText(CreateAccountActivity.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
