package com.example.dailybite;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton backButton;
    private TextView emptyTextView;
    private RecyclerView foodRecyclerView;
    private SearchAdapter foodAdapter;
    private List<NutritionixResponse.FoodItem> foodItems;
    private Spinner filterSpinner;

    private NutritionixApiService apiService;

    // Handler for managing search delays
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

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

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://trackapi.nutritionix.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(NutritionixApiService.class);

        // Set up RecyclerView
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItems = new ArrayList<>();  // Initialize empty food items list
        foodAdapter = new SearchAdapter(this, foodItems);
        foodRecyclerView.setAdapter(foodAdapter);

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Set up search listener with throttling
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Cancel any previous search tasks
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                // Start a new search with a delay to throttle API calls
                searchRunnable = () -> {
                    String query = charSequence.toString().trim();
                    if (query.length() >= 3) { // Trigger search only if query has 3 or more characters
                        searchFood(query);
                    } else {
                        foodItems.clear();
                        foodAdapter.updateFoodList(foodItems);
                        updateEmptyView();
                    }
                };
                handler.postDelayed(searchRunnable, 500); // 500ms delay
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set up filter spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Show empty message if no food items
        updateEmptyView();
    }

    private void searchFood(String query) {
        // Prepare the request
        NutritionixRequest request = new NutritionixRequest(query);

        // Call the API
        apiService.searchFood(request).enqueue(new Callback<NutritionixResponse>() {
            @Override
            public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    foodItems = response.body().getFoods();
                    if (foodItems != null && !foodItems.isEmpty()) {
                        foodAdapter.updateFoodList(foodItems);
                        updateEmptyView();
                    } else {
                        Log.d("FoodSearchActivity", "No food items found in response.");
                        emptyTextView.setText("No results found.");
                        updateEmptyView();
                    }
                } else {
                    Log.d("FoodSearchActivity", "API response was unsuccessful: " + response.errorBody());
                    emptyTextView.setText("Failed to load data.");
                    updateEmptyView();
                }
            }

            @Override
            public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                Log.d("FoodSearchActivity", "API call failed", t);
                emptyTextView.setText("Failed to load data.");
                updateEmptyView();
            }
        });
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
