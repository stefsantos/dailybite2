package com.example.dailybite;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

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

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // Setup RecyclerView
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItems = new ArrayList<>();
        foodAdapter = new SearchAdapter(this, foodItems, false);
        foodRecyclerView.setAdapter(foodAdapter);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Add a text change listener to the search bar with a debounce mechanism
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = charSequence.toString().trim();
                    if (query.length() >= 3) {
                        searchFood(query);
                    } else {
                        foodItems.clear();
                        foodAdapter.updateFoodList(foodItems);
                        updateEmptyView();
                    }
                };
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Setup filter spinner (optional)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Handle item click for food details
        foodAdapter.setOnItemClickListener(foodItem -> {
            NutritionixRequest request = new NutritionixRequest(foodItem.getFoodName());
            apiService.getNutrients(request).enqueue(new Callback<NutritionixResponse>() {
                @Override
                public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Retrieve the first food item details
                        NutritionixResponse.FoodItem detailedFood = response.body().getFoods().get(0);

                        // Pass data to FoodDetailActivity
                        Intent intent = new Intent(FoodSearchActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodName", detailedFood.getFoodName());
                        intent.putExtra("calories", detailedFood.getCalories());
                        intent.putExtra("proteins", detailedFood.getProteins());
                        intent.putExtra("carbs", detailedFood.getCarbs());
                        intent.putExtra("fats", detailedFood.getFats());
                        startActivity(intent);
                    } else {
                        Log.d("FoodSearchActivity", "Error fetching food details: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                    Log.e("FoodSearchActivity", "Error fetching food details", t);
                }
            });
        });

        updateEmptyView();
    }

    private void searchFood(String query) {
        Call<NutritionixResponse> call = apiService.searchInstant(query);

        call.enqueue(new Callback<NutritionixResponse>() {
            @Override
            public void onResponse(Call<NutritionixResponse> call, Response<NutritionixResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    foodItems = response.body().getCommonFoods();
                    if (foodItems != null && !foodItems.isEmpty()) {
                        foodAdapter.updateFoodList(foodItems);
                        updateEmptyView();
                    } else {
                        emptyTextView.setText("No results found.");
                        updateEmptyView();
                    }
                } else {
                    Log.d("FoodSearchActivity", "Error: " + response.code() + ", " + response.errorBody());
                    emptyTextView.setText("Failed to load data.");
                    updateEmptyView();
                }
            }

            @Override
            public void onFailure(Call<NutritionixResponse> call, Throwable t) {
                Log.e("FoodSearchActivity", "API call failed", t);
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
