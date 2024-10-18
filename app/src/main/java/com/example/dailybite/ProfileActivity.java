package com.example.dailybite;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileTitle, profileEmail, goalValue, calorieIntake, weightValue;
    private ImageButton backButton, toProfileButton;
    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure this matches your XML file name

        // Initialize the views from the layout
        backButton = findViewById(R.id.backbutton2); // Back button
        userImage = findViewById(R.id.user_image); // User image
        profileTitle = findViewById(R.id.textView3); // User name (Itami)
        profileEmail = findViewById(R.id.textView4); // User email (itamiomw@gmail.com)
        goalValue = findViewById(R.id.textView5); // "Me" button text
        calorieIntake = findViewById(R.id.CalorieCount2); // Calorie count
        weightValue = findViewById(R.id.weightInKG); // Weight value
        toProfileButton = findViewById(R.id.toProfile); // Next button on "Me"

        // Initialize the Back Button functionality
        backButton.setOnClickListener(v -> finish());

        // Set default or sample values
        setupDefaultValues();

        // Set up any additional button actions or logic as needed
        findViewById(R.id.ProfileButtonContainer).setOnClickListener(v -> {
            // Action when "Me" is clicked
            Toast.makeText(this, "Navigating to Personal Page...", Toast.LENGTH_SHORT).show();
            // You can add intent to navigate to another activity here
        });

        findViewById(R.id.CalorieDisplayReport).setOnClickListener(v -> {
            // Action when "Calorie Report" is clicked
            Toast.makeText(this, "Navigating to Calorie Report...", Toast.LENGTH_SHORT).show();
            // You can add intent to navigate to another activity here
        });

        findViewById(R.id.WeightReport).setOnClickListener(v -> {
            // Action when "Weight Report" is clicked
            Toast.makeText(this, "Navigating to Weight Report...", Toast.LENGTH_SHORT).show();
            // You can add intent to navigate to another activity here
        });

        findViewById(R.id.Logout).setOnClickListener(v -> {
            // Action when "Logout" is clicked
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            // Add logout logic here
        });
    }

    // Method to setup default values for the profile fields
    private void setupDefaultValues() {
        // You can fetch these values from SharedPreferences, a database, or API
        profileTitle.setText("Itami");
        profileEmail.setText("itamiomw@gmail.com");
        goalValue.setText("Me"); // Refers to the "Me" button
        calorieIntake.setText("3400 cal");
        weightValue.setText("88 kg");
    }
}
