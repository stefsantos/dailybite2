package com.example.dailybite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileTitle, profileEmail, goalValue, calorieIntake;
    private ImageButton backButton, toProfileButton;
    private ImageView userImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient mGoogleSignInClient; // Add this for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth, Firestore, SharedPreferences, and Google Sign-In
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize the views from the layout
        backButton = findViewById(R.id.backbutton2);
        userImage = findViewById(R.id.user_image);
        profileTitle = findViewById(R.id.textView3); // Username TextView
        profileEmail = findViewById(R.id.textView4); // Email TextView
        goalValue = findViewById(R.id.textView5);
        calorieIntake = findViewById(R.id.CalorieCount2);
        toProfileButton = findViewById(R.id.toProfile);

        // Load the user's username, email, and calorie intake
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
            // Sign out the user from Firebase
            mAuth.signOut();

            // Sign out the user from Google as well
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                // Update SharedPreferences to reflect the logout state
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();

                // Navigate to the login screen (MainActivity)
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
                startActivity(intent);
                finish(); // Close the ProfileActivity
            });
        });
    }

    // Load user's profile information and calorie intake from Firestore
    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String email = documentSnapshot.getString("email");

                // Retrieve calorie intake from Firestore
                Double calories = documentSnapshot.getDouble("intake.calories");

                if (username != null) {
                    profileTitle.setText(username);
                }
                if (email != null) {
                    profileEmail.setText(email);
                }
                if (calories != null) {
                    // Display the calories in the TextView with "cal" suffix
                    calorieIntake.setText(String.format(Locale.getDefault(), "%.0f cal", calories));
                }
            } else {
                Log.d("ProfileActivity", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("ProfileActivity", "Error fetching document", e));
    }
}
