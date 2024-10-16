package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
        saveButton.setOnClickListener(v -> {
            // Save the current meal data logic
        });
    }

    // Sample data for the food items
    private List<foodItem> getSampleFoodItems() {
        List<foodItem> foodItems = new ArrayList<>();
        foodItems.add(new foodItem("Fried eggs", "100 g", "300 Cal"));
        foodItems.add(new foodItem("Apple", "200 g", "116 Cal"));
        return foodItems;
    }
}
