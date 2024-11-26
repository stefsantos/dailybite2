package com.example.dailybite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
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

    private RelativeLayout continueWithGoogleButton, continueWithEmailButton;
    private TextView backText, proteinsButton, fatsButton, carbsButton, caloriesButton;

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

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI components
        continueWithGoogleButton = findViewById(R.id.continue_with_google);
        continueWithEmailButton = findViewById(R.id.continue_with_email);
        backText = findViewById(R.id.back_button);
        proteinsButton = findViewById(R.id.proteins_button);
        fatsButton = findViewById(R.id.fats_button);
        carbsButton = findViewById(R.id.carbs_button);
        caloriesButton = findViewById(R.id.calories_button);

        // Retrieve user data from SharedPreferences and display calculated PFC
        calculateAndDisplayPFC();

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

    private void calculateAndDisplayPFC() {
        // Retrieve data from shared preferences for weight, height, age, etc.
        SharedPreferences weightPrefs = getSharedPreferences("WeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences heightPrefs = getSharedPreferences("HeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences agePrefs = getSharedPreferences("AgePrefs", Context.MODE_PRIVATE);
        SharedPreferences genderPrefs = getSharedPreferences("GenderPrefs", Context.MODE_PRIVATE);
        SharedPreferences activityPrefs = getSharedPreferences("ActivityLevelPrefs", Context.MODE_PRIVATE);

        double weight = Double.parseDouble(weightPrefs.getString("Weight", "0"));
        int heightMeters = heightPrefs.getInt("HeightMeters", 0);
        int heightCentimeters = heightPrefs.getInt("HeightCentimeters", 0);
        int heightInCm = heightMeters * 100 + heightCentimeters; // Convert to cm
        int age = Integer.parseInt(agePrefs.getString("Age", "0"));
        String gender = genderPrefs.getString("SelectedGender", "");
        String activityLevel = activityPrefs.getString("SelectedActivityLevel", "sedentary");

        // Calculate TDEE
        double TDEE = calculateTDEE(age, weight, heightInCm, gender, activityLevel);
        Map<String, Double> macros = calculateMacros(TDEE);

        // Save calculated values to SharedPreferences
        SharedPreferences pfcPrefs = getSharedPreferences("PFCValues", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pfcPrefs.edit();
        editor.putFloat("Calories", (float) TDEE);
        editor.putFloat("Proteins", macros.get("Proteins").floatValue());
        editor.putFloat("Fats", macros.get("Fats").floatValue());
        editor.putFloat("Carbs", macros.get("Carbs").floatValue());
        editor.apply();

        // Update the UI with calculated values
        proteinsButton.setText("Proteins: " + String.format("%.0f", macros.get("Proteins")) + "g");
        fatsButton.setText("Fats: " + String.format("%.0f", macros.get("Fats")) + "g");
        carbsButton.setText("Carbs: " + String.format("%.0f", macros.get("Carbs")) + "g");
        caloriesButton.setText("Calories: " + String.format("%.0f", macros.get("Calories")));
    }


    private double calculateTDEE(int age, double weightKg, int heightCm, String gender, String activityLevel) {
        double BMR;
        if (gender.equalsIgnoreCase("male")) {
            BMR = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        } else {
            BMR = (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
        }

        double activityMultiplier;
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                activityMultiplier = 1.2;
                break;
            case "light":
                activityMultiplier = 1.375;
                break;
            case "moderate":
                activityMultiplier = 1.55;
                break;
            case "active":
                activityMultiplier = 1.725;
                break;
            case "very active":
                activityMultiplier = 1.9;
                break;
            default:
                activityMultiplier = 1.2;
                break;
        }

        return BMR * activityMultiplier;
    }

    private Map<String, Double> calculateMacros(double TDEE) {
        Map<String, Double> macros = new HashMap<>();
        double proteinGrams = (TDEE * 0.20) / 4;
        double fatGrams = (TDEE * 0.25) / 9;
        double carbGrams = (TDEE * 0.55) / 4;

        macros.put("Proteins", proteinGrams);
        macros.put("Fats", fatGrams);
        macros.put("Carbs", carbGrams);
        macros.put("Calories", TDEE);

        return macros;
    }

    private void signInWithGoogle() {
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

                            checkIfUserProfileExists(user.getUid(), username, email);
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(PFCActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIfUserProfileExists(String userId, String username, String email) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(PFCActivity.this, "An account with this Google profile already exists.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        mGoogleSignInClient.signOut();
                    } else {
                        saveUserDetails(userId, username, email);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error checking user profile existence", e);
                    Toast.makeText(PFCActivity.this, "Failed to check profile existence. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserDetails(String userId, String username, String email) {
        SharedPreferences weightPrefs = getSharedPreferences("WeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences heightPrefs = getSharedPreferences("HeightPrefs", Context.MODE_PRIVATE);
        SharedPreferences agePrefs = getSharedPreferences("AgePrefs", Context.MODE_PRIVATE);
        SharedPreferences genderPrefs = getSharedPreferences("GenderPrefs", Context.MODE_PRIVATE);
        SharedPreferences activityPrefs = getSharedPreferences("ActivityLevelPrefs", Context.MODE_PRIVATE);

        double weight = Double.parseDouble(weightPrefs.getString("Weight", "0"));
        int heightMeters = heightPrefs.getInt("HeightMeters", 0);
        int heightCentimeters = heightPrefs.getInt("HeightCentimeters", 0);
        int heightInCm = heightMeters * 100 + heightCentimeters;
        int age = Integer.parseInt(agePrefs.getString("Age", "0"));
        String gender = genderPrefs.getString("SelectedGender", "");
        String activityLevel = activityPrefs.getString("SelectedActivityLevel", "sedentary");

        // Calculate TDEE and macros
        double TDEE = calculateTDEE(age, weight, heightInCm, gender, activityLevel);
        Map<String, Double> macros = calculateMacros(TDEE);

        // Prepare data to save
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("weight", weight);
        userInfo.put("weight_unit", weightPrefs.getBoolean("Unit", true) ? "kg" : "lbs");
        userInfo.put("height", heightMeters + "m " + heightCentimeters + "cm");
        userInfo.put("age", age);
        userInfo.put("gender", gender);
        userInfo.put("activity_level", activityLevel);


        user.put("user_info", userInfo);

        Map<String, Object> intake = new HashMap<>();
        intake.put("calories", macros.get("Calories"));
        intake.put("proteins", macros.get("Proteins"));
        intake.put("fats", macros.get("Fats"));
        intake.put("carbs", macros.get("Carbs"));

        user.put("intake", intake);

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
