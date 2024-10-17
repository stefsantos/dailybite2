package com.example.dailybite;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class meal_input extends AppCompatActivity implements foodAdapter.OnItemDeleteListener { // Ensure the class name matches

    private RecyclerView recyclerView;
    private foodAdapter foodAdapter;
    private List<foodItem> foodItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_input); // Make sure the layout name is correct

        recyclerView = findViewById(R.id.food_recycler_view);
        foodItemList = new ArrayList<>();

        foodItemList.add(new foodItem("Fried Eggs", 300));
        foodItemList.add(new foodItem("Ham", 145));
        foodItemList.add(new foodItem("Bread", 53));

        foodAdapter = new foodAdapter(this, foodItemList, this); // 'this' should refer to the current activity
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemDelete(foodItem foodItem, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Food Item")
                .setMessage("Are you sure you want to delete " + foodItem.getName() + "?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    foodItemList.remove(position);
                    foodAdapter.notifyItemRemoved(position);
                    Toast.makeText(this, foodItem.getName() + " deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
