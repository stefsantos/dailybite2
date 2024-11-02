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

public class foodAdapter extends RecyclerView.Adapter<foodAdapter.FoodViewHolder> {

    private Context context;
    private List<foodItem> foodList;
    private OnFoodItemDeletedListener deleteListener;

    public foodAdapter(Context context, List<foodItem> foodList, OnFoodItemDeletedListener deleteListener) {
        this.context = context;
        this.foodList = foodList;
        this.deleteListener = deleteListener;
    }


    public interface OnFoodItemDeletedListener {
        void onFoodItemDeleted();
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
        holder.foodCaloriesTextView.setText(String.format("%d Cal", foodItem.getCalories()));
        holder.foodProteinsTextView.setText(String.format("Proteins: %d g", foodItem.getProteins()));
        holder.foodCarbsTextView.setText(String.format("Carbs: %d g", foodItem.getCarbs()));
        holder.foodFatsTextView.setText(String.format("Fats: %d g", foodItem.getFats()));

        holder.deleteButton.setOnClickListener(v -> {
            foodList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, foodList.size());

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

    public void updateFoodList(List<foodItem> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView, foodCaloriesTextView, foodProteinsTextView, foodCarbsTextView, foodFatsTextView;
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
