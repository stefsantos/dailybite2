package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileTitle, profileEmail, goalValue, calorieIntake;
    private ImageButton backButton, toProfileButton;
    private ImageView userImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize the views from the layout
        backButton = findViewById(R.id.backbutton2);
        userImage = findViewById(R.id.user_image);
        profileTitle = findViewById(R.id.textView3); // Username TextView
        profileEmail = findViewById(R.id.textView4); // Email TextView
        goalValue = findViewById(R.id.textView5);
        calorieIntake = findViewById(R.id.CalorieCount2);
        toProfileButton = findViewById(R.id.toProfile);

        // Load the user's username and email
        loadUserProfile();

        backButton.setOnClickListener(v -> finish());

        findViewById(R.id.ProfileButtonContainer).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.CalorieDisplayReport).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CalorieIntakeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.Logout).setOnClickListener(v -> {
            // Sign out the user
            mAuth.signOut();

            // Navigate to the login screen (MainActivity)
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
            startActivity(intent);
            finish(); // Close the ProfileActivity
        });

    }

    // Load user's profile information from Firestore
    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String email = documentSnapshot.getString("email");

                if (username != null) {
                    profileTitle.setText(username);
                }
                if (email != null) {
                    profileEmail.setText(email);
                }
            } else {
                Log.d("ProfileActivity", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("ProfileActivity", "Error fetching document", e));
    }
}
