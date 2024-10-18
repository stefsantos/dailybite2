package com.example.dailybite;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MeActivity extends AppCompatActivity {

    private TextView goalText, ageText, heightText, weightText, genderText, lifestyleText;
    private Button saveButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        // Initialize views
        goalText = findViewById(R.id.goalText);
        ageText = findViewById(R.id.ageText);
        heightText = findViewById(R.id.heightText);
        weightText = findViewById(R.id.weightText);
        genderText = findViewById(R.id.genderText);
        lifestyleText = findViewById(R.id.lifestyleText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.back_button_me);

        // Set default values
        goalText.setText("Gain weight");
        ageText.setText("17 years");
        heightText.setText("184 cm");
        weightText.setText("88 kg");
        genderText.setText("Male");
        lifestyleText.setText("Active");

        // Set click listeners for editable fields
        goalText.setOnClickListener(v -> showSelectionDialog("Edit Goal", goalText, new String[]{"Lose weight", "Keep weight", "Gain weight"}));
        ageText.setOnClickListener(v -> showEditDialog("Edit Age", ageText));
        heightText.setOnClickListener(v -> showEditDialog("Edit Height", heightText));
        weightText.setOnClickListener(v -> showEditDialog("Edit Weight", weightText));
        lifestyleText.setOnClickListener(v -> showSelectionDialog("Edit Lifestyle", lifestyleText, new String[]{"Sedentary", "Low Active", "Active", "Very Active"}));

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            Toast.makeText(MeActivity.this, "Information saved!", Toast.LENGTH_SHORT).show();
        });
    }

    // Show dialog for input fields (age, height, weight)
    private void showEditDialog(String title, TextView textViewToUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_value, null);  // The layout for input dialog
        builder.setView(dialogView);

        EditText valueInput = dialogView.findViewById(R.id.editValueInput);
        valueInput.setText(textViewToUpdate.getText().toString());

        builder.setTitle(title)
                .setPositiveButton("Done", (dialog, id) -> {
                    String newValue = valueInput.getText().toString();
                    textViewToUpdate.setText(newValue);
                    Toast.makeText(MeActivity.this, title + " updated to: " + newValue, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Show dialog for selection fields (goal, lifestyle)
    private void showSelectionDialog(String title, TextView textViewToUpdate, String[] options) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_value, null);  // The layout for selection dialog
        builder.setView(dialogView);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        RadioButton option1 = dialogView.findViewById(R.id.radio_option_1);
        RadioButton option2 = dialogView.findViewById(R.id.radio_option_2);
        RadioButton option3 = dialogView.findViewById(R.id.radio_option_3);

        // Set the radio button text dynamically
        option1.setText(options[0]);
        option2.setText(options[1]);
        option3.setText(options[2]);

        builder.setTitle(title);

        AlertDialog alertDialog = builder.create();

        Button doneButton = dialogView.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedOption = dialogView.findViewById(selectedId);
                textViewToUpdate.setText(selectedOption.getText().toString());
                Toast.makeText(MeActivity.this, title + " updated to: " + selectedOption.getText().toString(), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
