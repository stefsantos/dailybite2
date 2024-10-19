package com.example.dailybite;
import android.util.TypedValue;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;
    private View waterBackg; // View for water background
    private TextView waterPercent; // TextView for showing the number of glasses
    private int waterHeight; // Store the current height of water
    private int glassesOfWater; // Number of glasses
    private static final int MAX_MARGIN_TOP_DP = 468; // Initial margin at 0 glasses
    private static final int MIN_MARGIN_TOP_DP = 345; // Minimum margin at 8 glasses
    private static final int GLASS_HEIGHT_DP = (1+MAX_MARGIN_TOP_DP-MIN_MARGIN_TOP_DP)/8; // Each glass increases height by 0dp
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView username = view.findViewById(R.id.username);
        username.setOnClickListener(v -> navigateToUserProfile());

        // Initialize the calendar icon for date selection
        ImageView calendarIcon = view.findViewById(R.id.calendar_icon);
        calendarIcon.setOnClickListener(v -> openCalendar());

        // Initialize the plus icon for navigating to meal input
        ImageView plusIconMeal = view.findViewById(R.id.plus_icon_meal);
        plusIconMeal.setOnClickListener(v -> navigateToMealInputWithoutDate());

        //initialize recyclerview-meals
        recyclerView = view.findViewById(R.id.recyclerView_meals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        waterBackg = view.findViewById(R.id.water_backg);
        waterPercent = view.findViewById(R.id.water_drank); // TextView for glasses
        ImageView plusIcon = view.findViewById(R.id.plus_icon);
        ImageView minusIcon = view.findViewById(R.id.minus_icon);

        mealList = new ArrayList<>();
        mealList.add(new Meal("Breakfast", "10:45 AM", "531 Cal"));
        mealList.add(new Meal("Lunch", "03:45 PM", "1024 Cal"));

        mealAdapter = new MealAdapter(mealList);
        recyclerView.setAdapter(mealAdapter);

        // Set initial values
        glassesOfWater = 0;
        waterHeight = 0;
        updateWaterDisplay();

        // Plus button to add water
        plusIcon.setOnClickListener(v -> {
            if (glassesOfWater < 8) {
                glassesOfWater++;
                waterHeight += GLASS_HEIGHT_DP; // Increase by one glass height
                updateWaterDisplay();
            }
        });

        // Minus button to remove water
        minusIcon.setOnClickListener(v -> {
            if (glassesOfWater > 0) {
                glassesOfWater--;
                waterHeight -= waterHeight; // Decrease by one glass height
                updateWaterDisplay();
            }
        });
        return view;


    }

    // Open a calendar to select a date (only opens the calendar, no navigation)
    private void openCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // No navigation here, just opens the calendar
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    // Navigate to meal input activity without passing a date
    private void navigateToMealInputWithoutDate() {
        Intent intent = new Intent(getActivity(), meal_input.class);
        startActivity(intent); // No date passed, direct navigation
    }

    private void updateWaterDisplay() {
        float waterHeightDp = glassesOfWater * GLASS_HEIGHT_DP;
        float marginTopDp = MAX_MARGIN_TOP_DP - (glassesOfWater * (MAX_MARGIN_TOP_DP - MIN_MARGIN_TOP_DP) / 8);

        int waterHeightPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                waterHeightDp,
                getResources().getDisplayMetrics()
        );

        int marginTopPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginTopDp,
                getResources().getDisplayMetrics()
        );

        // Update the layout parameters of the water view
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) waterBackg.getLayoutParams();
        params.height = waterHeightPx;
        params.topMargin = marginTopPx;
        waterBackg.setLayoutParams(params);
        waterPercent.setText(glassesOfWater + " Cups");
    }

    private void navigateToUserProfile() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent); // Replace UserProfileActivity with your desired activity
    }

}


