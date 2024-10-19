package com.example.dailybite;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton backButton;
    private TextView emptyTextView;
    private RecyclerView foodRecyclerView;
    private SearchAdapter foodAdapter;
    private List<foodItem> foodItems;
    private Spinner filterSpinner;

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
        filterSpinner = findViewById(R.id.filterSpinner);

        // Set up RecyclerView
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItems = getAllFoodItems();  // Load all food items
        foodAdapter = new SearchAdapter(this, foodItems, true);
        foodRecyclerView.setAdapter(foodAdapter);

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Set up search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterFoodList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set up filter spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                sortFoodList(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Show empty message if no food items
        updateEmptyView();
    }

    // Filter food items based on search input
    private void filterFoodList(String query) {
        List<foodItem> filteredList = new ArrayList<>();
        for (foodItem item : foodItems) {
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

        foodAdapter.updateFoodList(filteredList);
    }

    // Sort food items based on selected filter
    private void sortFoodList(String filter) {
        switch (filter) {
            case "Calories":
                Collections.sort(foodItems, Comparator.comparingInt(foodItem::getCalories));
                break;
            case "Proteins":
                Collections.sort(foodItems, Comparator.comparingInt(foodItem::getProteins));
                break;
            case "Carbs":
                Collections.sort(foodItems, Comparator.comparingInt(foodItem::getCarbs));
                break;
            case "Fats":
                Collections.sort(foodItems, Comparator.comparingInt(foodItem::getFats));
                break;
        }

        // Notify the adapter of the changes
        foodAdapter.updateFoodList(foodItems);
    }

    // Mock food items, in real case, fetch from a database or API
    private List<foodItem> getAllFoodItems() {
        List<foodItem> foodItems = new ArrayList<>();
        foodItems.add(new foodItem("Apple", 116, 1, 30, 0));
        foodItems.add(new foodItem("Banana", 90, 1, 22, 0));
        foodItems.add(new foodItem("Fried eggs", 300, 6, 1, 20));
        // Add more items here
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
