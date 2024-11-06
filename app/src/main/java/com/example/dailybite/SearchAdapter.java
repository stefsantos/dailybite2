package com.example.dailybite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.FoodViewHolder> {

    private Context context;
    private List<NutritionixResponse.FoodItem> foodItems;

    // Constructor that takes a context and a list of FoodItems
    public SearchAdapter(Context context, List<NutritionixResponse.FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    // Method to update the food list and refresh the RecyclerView
    public void updateFoodList(List<NutritionixResponse.FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
        notifyDataSetChanged(); // Notify the RecyclerView to refresh the list
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each food item
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // Ensure foodItems list is not empty before accessing it
        if (foodItems != null && position < foodItems.size()) {
            NutritionixResponse.FoodItem foodItem = foodItems.get(position);

            // Bind data to views with proper formatting
            holder.foodNameTextView.setText(foodItem.getFoodName());
            holder.foodCaloriesTextView.setText(String.format("%.0f cal / 100 g", foodItem.getCalories()));
            holder.foodProteinsTextView.setText(String.format("Proteins: %.1f g", foodItem.getProtein()));
            holder.foodCarbsTextView.setText(String.format("Carbs: %.1f g", foodItem.getCarbs()));
            holder.foodFatsTextView.setText(String.format("Fats: %.1f g", foodItem.getFats()));
        }
    }

    @Override
    public int getItemCount() {
        return (foodItems != null) ? foodItems.size() : 0;
    }

    // ViewHolder class to hold the views for each food item
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView foodCaloriesTextView;
        TextView foodProteinsTextView;
        TextView foodCarbsTextView;
        TextView foodFatsTextView;
        ImageButton deleteButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views from the layout with matching IDs
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodCaloriesTextView = itemView.findViewById(R.id.foodCaloriesTextView);
            foodProteinsTextView = itemView.findViewById(R.id.foodProteinsTextView);
            foodCarbsTextView = itemView.findViewById(R.id.foodCarbsTextView);
            foodFatsTextView = itemView.findViewById(R.id.foodFatsTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton); // Optional delete button, can be used for item removal
        }
    }
}
