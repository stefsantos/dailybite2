package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView goalValue, ageValue, heightValue, weightValue, genderValue, lifestyleValue;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure this matches your XML file name

        // Initialize the TextViews
        goalValue = findViewById(R.id.goal_value);
        ageValue = findViewById(R.id.age_value);
        heightValue = findViewById(R.id.height_value);
        weightValue = findViewById(R.id.weight_value);
        genderValue = findViewById(R.id.gender_value);
        lifestyleValue = findViewById(R.id.lifestyle_value);

        // Initialize the Save Button
        saveButton = findViewById(R.id.save_button);

        // Set default or passed values (you can load real values from a database or preferences)
        setupDefaultValues();

        // Set a click listener for the Save button
        saveButton.setOnClickListener(v -> {
            // Handle saving logic
            saveUserProfile();
        });

        // Back button functionality
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    // Method to setup default values for the profile fields
    private void setupDefaultValues() {
        // Set some default values or get them from shared preferences or a database
        goalValue.setText("Gain weight");
        ageValue.setText("17 years");
        heightValue.setText("184 cm");
        weightValue.setText("88 kg");
        genderValue.setText("Male");
        lifestyleValue.setText("Active");
    }

    // Method to handle saving the profile
    private void saveUserProfile() {
        // Get the current values
        String goal = goalValue.getText().toString();
        String age = ageValue.getText().toString();
        String height = heightValue.getText().toString();
        String weight = weightValue.getText().toString();
        String gender = genderValue.getText().toString();
        String lifestyle = lifestyleValue.getText().toString();

        // Here you can save the data to shared preferences or a database
        // For now, just show a Toast message
        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();

        // Optionally navigate back or perform another action
        // Example: finish(); // to close the activity and return to the previous one
    }
}
