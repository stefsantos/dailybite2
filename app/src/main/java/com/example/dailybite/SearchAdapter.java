package com.example.dailybite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.FoodViewHolder> {

    private Context context;
    private List<NutritionixResponse.FoodItem> foodList;

    public SearchAdapter(Context context, List<NutritionixResponse.FoodItem> foodList) {
        this.context = context;
        this.foodList = foodList;
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

        // Set click listener to open FoodDetailActivity with food nutrient details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("foodName", foodItem.getFoodName());
            intent.putExtra("calories", foodItem.getCalories());
            intent.putExtra("proteins", foodItem.getProteins());
            intent.putExtra("carbs", foodItem.getCarbs());
            intent.putExtra("fats", foodItem.getFats());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateFoodList(List<NutritionixResponse.FoodItem> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
        }
    }
}
