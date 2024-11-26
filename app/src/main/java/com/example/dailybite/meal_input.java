package com.example.dailybite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class meal_input extends AppCompatActivity implements foodAdapter.OnFoodItemDeletedListener {

    private TextView caloriesText, proteinsText, fatsText, carbsText, meal_title;
    private RecyclerView foodRecyclerView;
    private foodAdapter foodAdapter;
    private Button saveButton;
    private ImageButton addButton, closeButton;
    private String mealName;
    private ActivityResultLauncher<Intent> foodSearchLauncher;
    private float foodCal,foodPro,foodFat,foodCar;
    private String foodName;
    private List<foodItem> foodItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_input);

        // Initialize views
        caloriesText = findViewById(R.id.caloriesText);
        proteinsText = findViewById(R.id.proteinsText);
        fatsText = findViewById(R.id.fatsText);
        carbsText = findViewById(R.id.carbsText);
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.add_button);
        closeButton = findViewById(R.id.close_button);
        meal_title = findViewById(R.id.meal_title);

        // Get meal name from the intent
        mealName = getIntent().getStringExtra("MEAL_NAME");
        if (mealName == null || mealName.trim().isEmpty()) {
            mealName = "New Meal";
        }
        meal_title.setText(mealName);
        if (foodItems == null) {
            foodItems = new ArrayList<>();
        }
        // Set up RecyclerView
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodAdapter = new foodAdapter(this, foodItems, true, this); // Show calories for saved meals
        foodRecyclerView.setAdapter(foodAdapter);

        foodSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // Handle the result from FoodSearchActivity
                        Intent data = result.getData();
                        String foodName = data.getStringExtra("foodName");
                        float foodCal = data.getFloatExtra("calories", -1);
                        float foodPro = data.getFloatExtra("proteins", -1);
                        float foodCar = data.getFloatExtra("carbs", -1);
                        float foodFat = data.getFloatExtra("fats", -1);
                        foodItems.add(new foodItem(foodName, foodCal, foodPro, foodFat, foodCar));
                        calculateTotalNutrients();
                        foodAdapter.notifyDataSetChanged();
                    }
                }
        );
        // Add button click opens FoodSearchActivity
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodSearchActivity.class);
            foodSearchLauncher.launch(intent);
        });

        // Close button to finish activity
        closeButton.setOnClickListener(v -> finish());

        // Save button logic
        saveButton.setOnClickListener(v -> saveMeal());

        // Calculate total nutrients
        calculateTotalNutrients();
    }

    private void saveMeal() {
        String calories = caloriesText.getText().toString();
        String proteins = proteinsText.getText().toString();
        String fats = fatsText.getText().toString();
        String carbs = carbsText.getText().toString();
        String newMealName = meal_title.getText().toString();

        // Create an Intent to hold the meal data
        Intent resultIntent = new Intent();
        resultIntent.putExtra("MEAL_NAME", newMealName);
        resultIntent.putExtra("MEAL_CALORIES", calories);
        resultIntent.putExtra("MEAL_PROTEINS", proteins);
        resultIntent.putExtra("MEAL_FATS", fats);
        resultIntent.putExtra("MEAL_CARBS", carbs);

        // Set the result and finish the activity
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, "Meal saved: " + newMealName + " Calories: " + calories +
                " Proteins: " + proteins + " Fats: " + fats + " Carbs: " + carbs, Toast.LENGTH_SHORT).show();

        finish();
    }

    // Calculate total nutrients
    private void calculateTotalNutrients() {
        int totalCalories = 0;
        float totalProteins = 0;
        float totalFats = 0;
        float totalCarbs = 0;

        for (foodItem item : foodAdapter.getFoodList()) {
            totalCalories += item.getCalories();
            totalProteins += item.getProteins();
            totalFats += item.getFats();
            totalCarbs += item.getCarbs();
        }

        caloriesText.setText(String.valueOf(totalCalories));
        proteinsText.setText(String.valueOf(totalProteins));
        fatsText.setText(String.valueOf(totalFats));
        carbsText.setText(String.valueOf(totalCarbs));
    }

    @Override
    public void onFoodItemDeleted() {
        calculateTotalNutrients();
    }
}
