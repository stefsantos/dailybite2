package com.example.dailybite;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CalorieIntakeActivity extends AppCompatActivity {

    private TextView caloriesText, proteinsText, fatsText, carbsText, waterText;
    private Button saveButton;
    private ImageButton backButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Map<String, Object> updatedIntakeData; // Store updated intake values for Firebase

    private static final String TAG = "CalorieIntakeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_intake);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        caloriesText = findViewById(R.id.caloriesText);
        proteinsText = findViewById(R.id.proteinsText);
        fatsText = findViewById(R.id.fatsText);
        carbsText = findViewById(R.id.carbsText);
        waterText = findViewById(R.id.waterText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.back_button_calorie_intake);

        updatedIntakeData = new HashMap<>(); // Initialize the map

        // Fetch intake data from Firestore and display it
        fetchIntakeData();

        // Set click listeners for editable fields
        caloriesText.setOnClickListener(v -> showEditDialog("Edit Calories", caloriesText, "cal"));
        proteinsText.setOnClickListener(v -> showEditDialog("Edit Proteins", proteinsText, "g"));
        fatsText.setOnClickListener(v -> showEditDialog("Edit Fats", fatsText, "g"));
        carbsText.setOnClickListener(v -> showEditDialog("Edit Carbs", carbsText, "g"));
        waterText.setOnClickListener(v -> showEditDialog("Edit Water", waterText, "ml"));

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Handle save button click to update Firestore
        saveButton.setOnClickListener(v -> saveChangesToFirestore());
    }

    // Fetch intake data from Firestore and display it
    private void fetchIntakeData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> intake = (Map<String, Object>) documentSnapshot.get("intake");
                        if (intake != null) {
                            // Display data
                            caloriesText.setText(intake.get("calories") + " cal");
                            proteinsText.setText(intake.get("proteins") + " g");
                            fatsText.setText(intake.get("fats") + " g");
                            carbsText.setText(intake.get("carbs") + " g");
                            waterText.setText(intake.get("water") + " ml");

                            // Populate the map with the intake data
                            updatedIntakeData.put("calories", intake.get("calories"));
                            updatedIntakeData.put("proteins", intake.get("proteins"));
                            updatedIntakeData.put("fats", intake.get("fats"));
                            updatedIntakeData.put("carbs", intake.get("carbs"));
                            updatedIntakeData.put("water", intake.get("water"));
                        }
                    } else {
                        Toast.makeText(CalorieIntakeActivity.this, "No intake data found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CalorieIntakeActivity.this, "Failed to load intake data.", Toast.LENGTH_SHORT).show();
                });
    }

    // Show dialog for input fields (calories, proteins, fats, etc.) and update locally
    private void showEditDialog(String title, TextView textViewToUpdate, String unit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CalorieIntakeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_value, null);
        builder.setView(dialogView);

        EditText valueInput = dialogView.findViewById(R.id.editValueInput);
        valueInput.setText(textViewToUpdate.getText().toString().replace(unit, "").trim());

        builder.setTitle(title)
                .setPositiveButton("Done", (dialog, id) -> {
                    String newValue = valueInput.getText().toString().trim();
                    if (newValue.isEmpty()) {
                        Toast.makeText(CalorieIntakeActivity.this, "Please enter a valid value.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update the text view with the new value and unit
                    String formattedValue = newValue + " " + unit;
                    textViewToUpdate.setText(formattedValue);

                    // Update the map with the new value (as a number, not a string with units)
                    updatedIntakeData.put(title.toLowerCase().replace("edit ", ""), Integer.parseInt(newValue));

                    Toast.makeText(CalorieIntakeActivity.this, title + " updated to: " + formattedValue, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Save all changes to Firestore
    private void saveChangesToFirestore() {
        String userId = mAuth.getCurrentUser().getUid();

        // Update the "intake" field in Firestore with the updated intake data
        db.collection("users").document(userId)
                .update("intake", updatedIntakeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CalorieIntakeActivity.this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CalorieIntakeActivity.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
                });
    }
}
