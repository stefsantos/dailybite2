package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileTitle, profileEmail, goalValue, calorieIntake;
    private ImageButton backButton, toProfileButton;
    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the views from the layout
        backButton = findViewById(R.id.backbutton2); // Back button
        userImage = findViewById(R.id.user_image); // User image
        profileTitle = findViewById(R.id.textView3); // User name (Itami)
        profileEmail = findViewById(R.id.textView4); // User email (itamiomw@gmail.com)
        goalValue = findViewById(R.id.textView5); // "Me" button text
        calorieIntake = findViewById(R.id.CalorieCount2); // Calorie count
        toProfileButton = findViewById(R.id.toProfile); // "Me" Button inside the profile card


        backButton.setOnClickListener(v -> finish());

        // Set default or sample values
        setupDefaultValues();

        findViewById(R.id.ProfileButtonContainer).setOnClickListener(v -> {
            Toast.makeText(this, "Navigating to Me...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, MeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.CalorieDisplayReport).setOnClickListener(v -> {
            Toast.makeText(this, "Navigating to Calorie Report...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, CalorieIntakeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.Logout).setOnClickListener(v -> {
            // Action when "Logout" is clicked
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Method to setup default values for the profile fields
    private void setupDefaultValues() {
        // You can fetch these values from SharedPreferences, a database, or API
        profileTitle.setText("Itami");
        profileEmail.setText("itamiomw@gmail.com");
        goalValue.setText("Me"); // Refers to the "Me" button
        calorieIntake.setText("3400 cal");
    }
}
