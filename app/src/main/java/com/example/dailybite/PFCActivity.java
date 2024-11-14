package com.example.dailybite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PFCActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "PFCActivity";

    private Button continueWithGoogleButton, continueWithEmailButton;
    private TextView backText;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pfc);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign-In with forceCodeForRefreshToken to show account chooser
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI components
        continueWithGoogleButton = findViewById(R.id.continue_with_google);
        continueWithEmailButton = findViewById(R.id.continue_with_email);
        backText = findViewById(R.id.back_button);

        // Handle Continue with Google button click
        continueWithGoogleButton.setOnClickListener(v -> signInWithGoogle());

        // Handle Continue with Email button click
        continueWithEmailButton.setOnClickListener(v -> {
            Intent intent = new Intent(PFCActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });

        // Handle the back button
        backText.setOnClickListener(v -> finish());
    }

    private void signInWithGoogle() {
        // Sign out any previously signed-in account to force the account chooser to appear
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(Exception.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (Exception e) {
                Log.w(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            String username = email != null && email.contains("@") ? email.split("@")[0] : user.getDisplayName();

                            // Save user data to Firestore
                            saveUserDetails(user.getUid(), username, email);
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(PFCActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PFCActivity.this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PFCActivity.this, Homepage.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(PFCActivity.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
