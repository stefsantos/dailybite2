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

    private List<foodItem> foodItems;
    private Context context;
    private boolean showOptionsButton;

    // Constructor
    public foodAdapter(Context context, List<foodItem> foodItems, boolean showOptionsButton) {
        this.context = context;
        this.foodItems = foodItems;
        this.showOptionsButton = showOptionsButton;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        foodItem item = foodItems.get(position);
        holder.nameText.setText(item.getName());
        holder.servingSizeText.setText(item.getServingSize());

        // Show or hide the options button based on the flag
        if (showOptionsButton) {
            holder.optionsButton.setVisibility(View.VISIBLE);
        } else {
            holder.optionsButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    // Update the food list in the adapter
    public void updateFoodList(List<foodItem> newFoodItems) {
        this.foodItems.clear();
        this.foodItems.addAll(newFoodItems);
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, servingSizeText;
        ImageButton optionsButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.food_name);
            servingSizeText = itemView.findViewById(R.id.food_serving_size);
            optionsButton = itemView.findViewById(R.id.food_options_button);
        }
    }
}
