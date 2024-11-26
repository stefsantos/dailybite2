package com.example.dailybite;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class foodAdapter extends RecyclerView.Adapter<foodAdapter.FoodViewHolder> {

    private Context context;
    private List<foodItem> foodList;
    private boolean showCalories; // Flag to determine whether to show calories
    private OnFoodItemDeletedListener deleteListener;

    public foodAdapter(Context context, List<foodItem> foodList, boolean showCalories, OnFoodItemDeletedListener deleteListener) {
        this.context = context;
        this.foodList = foodList;
        this.showCalories = showCalories; // Initialize the flag
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        foodItem foodItem = foodList.get(position);
        holder.foodNameTextView.setText(foodItem.getName());

        // Show or hide calories based on the `showCalories` flag
        if (showCalories) {
            holder.foodCaloriesTextView.setVisibility(View.VISIBLE);
            holder.foodCaloriesTextView.setText(String.format("%.1f Cal", foodItem.getCalories()));
            holder.foodFatsTextView.setText(String.format("Fats: %.2f g", foodItem.getFats()));
            holder.foodCarbsTextView.setText(String.format("Carbs: %.2f g", foodItem.getCarbs()));
            holder.foodProteinsTextView.setText(String.format("Proteins: %.2f g", foodItem.getProteins()));
        } else {
            holder.foodCaloriesTextView.setVisibility(View.GONE);
            holder.foodProteinsTextViewSpacer.setVisibility(View.GONE);
            holder.foodCarbsTextViewSpacer.setVisibility(View.GONE);
            holder.foodCaloriesTextView.setVisibility(View.GONE);
            holder.foodFatsTextView.setVisibility(View.GONE);
            holder.foodCarbsTextView.setVisibility(View.GONE);
            holder.foodProteinsTextView.setVisibility(View.GONE);
        }

        // Open FoodDetailActivity on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("foodName", foodItem.getName());
            intent.putExtra("calories", foodItem.getCalories());
            intent.putExtra("proteins", foodItem.getProteins());
            intent.putExtra("carbs", foodItem.getCarbs());
            intent.putExtra("fats", foodItem.getFats());
            context.startActivity(intent);
        });

        // Delete button logic
        holder.deleteButton.setOnClickListener(v -> {
            foodList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodList.size());

            // Notify the listener
            if (deleteListener != null) {
                deleteListener.onFoodItemDeleted();
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public List<foodItem> getFoodList() {
        return foodList;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodCarbsTextViewSpacer,foodProteinsTextViewSpacer, foodCarbsTextView, foodProteinsTextView, foodFatsTextView ;
        TextView foodNameTextView;
        TextView foodCaloriesTextView; // Added TextView for calories
        ImageButton deleteButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodCaloriesTextView = itemView.findViewById(R.id.foodCaloriesTextView); // Calories TextView
            deleteButton = itemView.findViewById(R.id.deleteButton);
            foodProteinsTextViewSpacer = itemView.findViewById(R.id.foodFatsTextViewSpacer);
            foodCarbsTextViewSpacer = itemView.findViewById(R.id.foodCarbsTextViewSpacer);
            foodProteinsTextViewSpacer = itemView.findViewById(R.id.foodFatsTextViewSpacer);
            foodCarbsTextView = itemView.findViewById(R.id.foodCarbsTextView);
            foodProteinsTextView = itemView.findViewById(R.id.foodProteinsTextView);
            foodFatsTextView = itemView.findViewById(R.id.foodFatsTextView);
        }
    }

    public interface OnFoodItemDeletedListener {
        void onFoodItemDeleted();
    }
}
