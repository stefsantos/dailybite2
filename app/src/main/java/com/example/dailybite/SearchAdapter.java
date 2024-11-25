package com.example.dailybite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.FoodViewHolder> {

    private final Context context;
    private List<NutritionixResponse.FoodItem> foodList;
    private final boolean showCalories;
    private OnItemClickListener listener;

    // Constructor
    public SearchAdapter(Context context, List<NutritionixResponse.FoodItem> foodList, boolean showCalories) {
        this.context = context;
        this.foodList = foodList;
        this.showCalories = showCalories;
    }

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(NutritionixResponse.FoodItem foodItem);
    }

    // Setter for the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        NutritionixResponse.FoodItem foodItem = foodList.get(position);
        holder.foodNameTextView.setText(foodItem.getFoodName());

        // Show or hide calories based on the `showCalories` flag
        if (showCalories) {
            holder.foodCaloriesTextView.setVisibility(View.VISIBLE);
            holder.foodCaloriesTextView.setText(foodItem.getCalories() + " Calories");
        } else {
            holder.foodCaloriesTextView.setVisibility(View.GONE);
        }

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(foodItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Update the food list dynamically
    public void updateFoodList(List<NutritionixResponse.FoodItem> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }

    // ViewHolder class for holding item views
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView foodCaloriesTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodCaloriesTextView = itemView.findViewById(R.id.foodCaloriesTextView);
        }
    }
}
