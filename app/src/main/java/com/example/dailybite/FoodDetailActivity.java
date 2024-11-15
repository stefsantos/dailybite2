package com.example.dailybite;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodNameTextView, caloriesTextView, proteinsTextView, carbsTextView, fatsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodNameTextView = findViewById(R.id.foodNameTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        proteinsTextView = findViewById(R.id.proteinsTextView);
        carbsTextView = findViewById(R.id.carbsTextView);
        fatsTextView = findViewById(R.id.fatsTextView);

        // Get data from intent and display
        String foodName = getIntent().getStringExtra("foodName");
        int calories = getIntent().getIntExtra("calories", 0);
        int proteins = getIntent().getIntExtra("proteins", 0);
        int carbs = getIntent().getIntExtra("carbs", 0);
        int fats = getIntent().getIntExtra("fats", 0);

        foodNameTextView.setText(foodName);
        caloriesTextView.setText("Calories: " + calories + " kcal");
        proteinsTextView.setText("Proteins: " + proteins + " g");
        carbsTextView.setText("Carbs: " + carbs + " g");
        fatsTextView.setText("Fats: " + fats + " g");
    }
}