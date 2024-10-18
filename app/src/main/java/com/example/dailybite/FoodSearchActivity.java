package com.example.dailybite;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton backButton;
    private TextView emptyTextView;
    private RecyclerView foodRecyclerView;
    private foodAdapter foodAdapter;  // Use the correct class name (FoodAdapter) here
    private List<foodItem> foodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText);
        backButton = findViewById(R.id.back_button);
        emptyTextView = findViewById(R.id.emptyTextView);
        foodRecyclerView = findViewById(R.id.foodRecyclerView);

        // Set up RecyclerView
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItems = new ArrayList<>();
        foodAdapter = new foodAdapter(this, foodItems, true);  // Correct instance of FoodAdapter
        foodRecyclerView.setAdapter(foodAdapter);

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Set up search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list based on search input
                filterFoodList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Show empty message if no food items
        updateEmptyView();
    }

    // Filter food items based on search input
    private void filterFoodList(String query) {
        List<foodItem> filteredList = new ArrayList<>();
        for (foodItem item : getAllFoodItems()) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            foodRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            foodRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }

        foodAdapter.updateFoodList(filteredList);  // Update list in the adapter
    }

    // Mock food items, you can load from API or database in real implementation
    private List<foodItem> getAllFoodItems() {
        List<foodItem> foodItems = new ArrayList<>();
        foodItems.add(new foodItem("Apple", "200 g", "116 Cal"));
        foodItems.add(new foodItem("Banana", "150 g", "90 Cal"));
        foodItems.add(new foodItem("Fried eggs", "100 g", "300 Cal"));
        // Add more items
        return foodItems;
    }

    private void updateEmptyView() {
        if (foodItems.isEmpty()) {
            foodRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            foodRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }
}
