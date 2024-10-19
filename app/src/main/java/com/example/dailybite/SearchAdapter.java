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
    private List<foodItem> foodList;

    public SearchAdapter(Context context, List<foodItem> foodList, boolean someFlag) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for each food item
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // Get the food item at the current position
        foodItem foodItem = foodList.get(position);

        // Bind the data to the views in the ViewHolder
        holder.foodNameTextView.setText(foodItem.getName());
        holder.foodCaloriesTextView.setText(String.format("%d Cal", foodItem.getCalories()));
        holder.foodProteinsTextView.setText(String.format("Proteins: %d g", foodItem.getProteins()));
        holder.foodCarbsTextView.setText(String.format("Carbs: %d g", foodItem.getCarbs()));
        holder.foodFatsTextView.setText(String.format("Fats: %d g", foodItem.getFats()));

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Update the list of food items and notify the adapter
    public void updateFoodList(List<foodItem> newList) {
        this.foodList = newList;
        notifyDataSetChanged(); // Notify the RecyclerView to refresh
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

            // Initialize the views from the layout
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodCaloriesTextView = itemView.findViewById(R.id.foodCaloriesTextView);
            foodProteinsTextView = itemView.findViewById(R.id.foodProteinsTextView);
            foodCarbsTextView = itemView.findViewById(R.id.foodCarbsTextView);
            foodFatsTextView = itemView.findViewById(R.id.foodFatsTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton); // Reference to the delete button
        }
    }
}
