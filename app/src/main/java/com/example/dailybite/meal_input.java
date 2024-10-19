package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class meal_input extends AppCompatActivity {

    private TextView caloriesText, proteinsText, fatsText, carbsText;
    private RecyclerView foodRecyclerView;
    private foodAdapter foodAdapter;
    private Button saveButton;
    private ImageButton addButton, closeButton;
    private String mealName;
    private TextView meal_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
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
        mealName = getIntent().getStringExtra("MEAL_NAME");
        meal_title = findViewById(R.id.meal_title);

        if (mealName == null || mealName.trim().isEmpty()) {
            mealName = "New Meal";
            meal_title.setText(mealName);

        } else {
            meal_title.setText(mealName);
        }
        // Set up RecyclerView
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodAdapter = new foodAdapter(this, getSampleFoodItems(), true);  // Correct instance with Context, List, and boolean

        foodRecyclerView.setAdapter(foodAdapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(meal_input.this, FoodSearchActivity.class);
            startActivity(intent);
        });

        // Set up click listeners (for adding new food or closing)
        closeButton.setOnClickListener(v -> finish());

        // Example logic for the save button
        saveButton.setOnClickListener(v -> saveMeal() );
    }

    private void saveMeal() {
        String calories = caloriesText.getText().toString();
        String proteins = proteinsText.getText().toString();
        String fats = fatsText.getText().toString();
        String carbs = carbsText.getText().toString();
        Toast.makeText(this, "Meal saved: " + mealName, Toast.LENGTH_SHORT).show();
    }

    // Sample data for the food items
    private List<foodItem> getSampleFoodItems() {
        List<foodItem> foodItems = new ArrayList<>();
        foodItems.add(new foodItem("Fried eggs", 100, 6, 1, 20));  // 100g serving, 6g proteins, 1g carbs, 20g fats
        foodItems.add(new foodItem("Apple", 116, 1, 30, 0));       // 200g serving, 1g proteins, 30g carbs, 0g fats
        foodItems.add(new foodItem("Banana", 90, 1, 22, 0));       // Another example item
        // Add more food items as needed
        return foodItems;
    }

}