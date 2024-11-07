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
    private List<NutritionixResponse.BrandedFoodItem> brandedFoodItems;
    private boolean isBranded; // Flag to indicate if this adapter is for branded items

    // Constructor for both common and branded foods
    public SearchAdapter(Context context, List<?> items, boolean isBranded) {
        this.context = context;
        this.isBranded = isBranded;

        if (isBranded) {
            this.brandedFoodItems = (List<NutritionixResponse.BrandedFoodItem>) items;
        } else {
            this.foodItems = (List<NutritionixResponse.FoodItem>) items;
        }
    }

    // Method to update the common food list
    public void updateFoodList(List<NutritionixResponse.FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
        this.brandedFoodItems = null; // Clear branded items
        this.isBranded = false;
        notifyDataSetChanged();
    }

    // Method to update the branded food list
    public void updateBrandedFoodList(List<NutritionixResponse.BrandedFoodItem> newBrandedFoodItems) {
        this.brandedFoodItems = newBrandedFoodItems;
        this.foodItems = null; // Clear common items
        this.isBranded = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        if (!isBranded && foodItems != null && position < foodItems.size()) {
            NutritionixResponse.FoodItem foodItem = foodItems.get(position);
            holder.foodNameTextView.setText(foodItem.getFoodName());
            holder.foodCaloriesTextView.setText("N/A"); // No calories for common items
            holder.foodProteinsTextView.setText("N/A");
            holder.foodCarbsTextView.setText("N/A");
            holder.foodFatsTextView.setText("N/A");

        } else if (isBranded && brandedFoodItems != null && position < brandedFoodItems.size()) {
            NutritionixResponse.BrandedFoodItem brandedFoodItem = brandedFoodItems.get(position);
            holder.foodNameTextView.setText(brandedFoodItem.getFoodName());
            holder.foodCaloriesTextView.setText(String.format("%.0f cal", brandedFoodItem.getCalories()));
            // Display other branded-specific fields if available
        }
    }

    @Override
    public int getItemCount() {
        if (isBranded && brandedFoodItems != null) {
            return brandedFoodItems.size();
        } else if (!isBranded && foodItems != null) {
            return foodItems.size();
        }
        return 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView foodCaloriesTextView;
        TextView foodProteinsTextView;
        TextView foodCarbsTextView;
        TextView foodFatsTextView;
        ImageButton deleteButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodCaloriesTextView = itemView.findViewById(R.id.foodCaloriesTextView);
            foodProteinsTextView = itemView.findViewById(R.id.foodProteinsTextView);
            foodCarbsTextView = itemView.findViewById(R.id.foodCarbsTextView);
            foodFatsTextView = itemView.findViewById(R.id.foodFatsTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
