package com.example.dailybite;

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
    private OnFoodItemDeletedListener deleteListener;

    public foodAdapter(Context context, List<foodItem> foodList, OnFoodItemDeletedListener deleteListener) {
        this.context = context;
        this.foodList = foodList;
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

        // Set click listener to open FoodDetailActivity with food nutrient details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("foodName", foodItem.getName());
            intent.putExtra("calories", foodItem.getCalories());
            intent.putExtra("proteins", foodItem.getProteins());
            intent.putExtra("carbs", foodItem.getCarbs());
            intent.putExtra("fats", foodItem.getFats());
            context.startActivity(intent);
        });

        // Optional delete button logic, if needed
        holder.deleteButton.setOnClickListener(v -> {
            foodList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodList.size());

            // Notify listener of the deletion
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
        TextView foodNameTextView;
        ImageButton deleteButton; // Assuming you want a delete button for each item

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton); // Ensure deleteButton exists in food_item.xml
        }
    }

    public interface OnFoodItemDeletedListener {
        void onFoodItemDeleted();
    }
}
