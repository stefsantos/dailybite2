package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class WeightgoalActivity extends AppCompatActivity {

    // Declare variables for the TextViews and Button
    private TextView loseWeightText;
    private TextView keepWeightText;
    private TextView gainWeightText;
    private ImageButton proceedButton;
    private TextView backTextView;
    private TextView selectedOption = null;  // Variable to store selected weight goal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weightgoal);

        // Initialize TextViews and Button
        loseWeightText = findViewById(R.id.lose_weight);
        keepWeightText = findViewById(R.id.keep_weight);
        gainWeightText = findViewById(R.id.gain_weight);
        proceedButton = findViewById(R.id.proceed_button);
        backTextView = findViewById(R.id.textView);  // Back text view

        // Set onClickListeners for the weight options
        loseWeightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeightOption(loseWeightText);
                Toast.makeText(WeightgoalActivity.this, "Selected: Lose Weight", Toast.LENGTH_SHORT).show();
            }
        });

        keepWeightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeightOption(keepWeightText);
                Toast.makeText(WeightgoalActivity.this, "Selected: Keep Weight", Toast.LENGTH_SHORT).show();
            }
        });

        gainWeightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeightOption(gainWeightText);
                Toast.makeText(WeightgoalActivity.this, "Selected: Gain Weight", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for the back button
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish this activity and go back to the previous screen
                finish();
            }
        });

        // Set onClickListener for the proceed button
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedOption == null) {
                    // If no option has been selected, show an error message
                    Toast.makeText(WeightgoalActivity.this, "Please select a weight goal before proceeding", Toast.LENGTH_SHORT).show();
                } else {
                    // If a weight goal is selected, proceed to the next activity
                    Intent intent = new Intent(WeightgoalActivity.this, GenderActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void selectWeightOption(TextView selectedOptionView) {
        // Reset the backgrounds for all options
        loseWeightText.setBackgroundResource(android.R.color.transparent);
        keepWeightText.setBackgroundResource(android.R.color.transparent);
        gainWeightText.setBackgroundResource(android.R.color.transparent);

        // Apply the outline to the selected option
        selectedOptionView.setBackgroundResource(R.drawable.outline);

        // Set the selected option to track which one was chosen
        selectedOption = selectedOptionView;
    }
}
