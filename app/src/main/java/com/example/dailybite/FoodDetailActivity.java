package com.example.dailybite;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodNameTextView, caloriesTextView, proteinsTextView, carbsTextView, fatsTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Initialize views
        foodNameTextView = findViewById(R.id.foodNameTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        proteinsTextView = findViewById(R.id.proteinsTextView);
        carbsTextView = findViewById(R.id.carbsTextView);
        fatsTextView = findViewById(R.id.fatsTextView);
        backButton = findViewById(R.id.back_button);

        // Get data from intent
        String foodName = getIntent().getStringExtra("foodName");
        float calories = getIntent().getFloatExtra("calories", -1);
        float proteins = getIntent().getFloatExtra("proteins", -1);
        float carbs = getIntent().getFloatExtra("carbs", -1);
        float fats = getIntent().getFloatExtra("fats", -1);

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Validate and display data
        if (foodName != null && !foodName.isEmpty()) {
            foodNameTextView.setText(foodName);
        } else {
            foodNameTextView.setText("Food Name: Not available");
        }

        caloriesTextView.setText(calories >= 0 ? "Calories: " + calories + " kcal" : "Calories: Not available");
        proteinsTextView.setText(proteins >= 0 ? "Proteins: " + proteins + " g" : "Proteins: Not available");
        carbsTextView.setText(carbs >= 0 ? "Carbs: " + carbs + " g" : "Carbs: Not available");
        fatsTextView.setText(fats >= 0 ? "Fats: " + fats + " g" : "Fats: Not available");
    }
}
