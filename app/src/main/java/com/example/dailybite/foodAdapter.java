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

    private List<foodItem> foodList;
    private Context context;
    private OnItemDeleteListener onItemDeleteListener;

    public foodAdapter(Context context, List<foodItem> foodList, OnItemDeleteListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.onItemDeleteListener = listener;
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
        holder.foodName.setText(foodItem.getName());
        holder.calories.setText(String.valueOf(foodItem.getCalories()) + " cal");

        holder.deleteButton.setOnClickListener(v -> {
            if (onItemDeleteListener != null) {
                onItemDeleteListener.onItemDelete(foodItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView calories;
        ImageButton deleteButton;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            calories = itemView.findViewById(R.id.calories);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnItemDeleteListener {
        void onItemDelete(foodItem foodItem, int position);
    }
}
