package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class GenderActivity extends AppCompatActivity {

    private TextView maleText, femaleText;
    private ImageButton nextButton;
    private TextView backText;
    private String selectedGender = null;  // Variable to store selected gender

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);  // Set your layout

        // Initialize the UI components
        maleText = findViewById(R.id.male_button);
        femaleText = findViewById(R.id.female_button);
        nextButton = findViewById(R.id.next_button);
        backText = findViewById(R.id.back_text);

        // Set onClickListener for Male option
        maleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Male";
                selectGenderOption(maleText);  // Highlight male option
                Toast.makeText(GenderActivity.this, "Selected: Male", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for Female option
        femaleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Female";
                selectGenderOption(femaleText);  // Highlight female option
                Toast.makeText(GenderActivity.this, "Selected: Female", Toast.LENGTH_SHORT).show();
            }
        });

        // Click listener for Back text
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the previous activity (if any)
                onBackPressed();  // Simply go back to the previous activity
            }
        });

        // Click listener for Next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGender != null) {
                    // If a gender is selected, proceed to the next activity
                    Intent intent = new Intent(GenderActivity.this, ActivenessActivity.class);
                    intent.putExtra("selectedGender", selectedGender);  // Pass the selected gender to the next activity
                    startActivity(intent);
                } else {
                    // Optionally, show a message if no gender is selected
                    showMessage("Please select a gender before proceeding.");
                }
            }
        });
    }

    private void selectGenderOption(TextView selectedOption) {
        // Reset the backgrounds for both options
        maleText.setBackgroundResource(android.R.color.transparent);
        femaleText.setBackgroundResource(android.R.color.transparent);

        // Apply the outline to the selected option
        selectedOption.setBackgroundResource(R.drawable.outline);
    }

    // Method to show a message (e.g., toast) when no gender is selected
    private void showMessage(String message) {
        Toast.makeText(GenderActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
