package com.example.dailybite;
import android.app.Activity;
import android.util.TypedValue;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;



public class HomeFragment extends Fragment implements MealAdapter.OnMealClickListener{
    private ActivityResultLauncher<Intent> mealInputLauncher;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;
    private View waterBackg; // View for water background
    private TextView waterPercent; // TextView for showing the number of glasses
    private int waterHeight; // Store the current height of water
    private static final int MAX_MARGIN_TOP_DP = 468; // Initial margin at 0 glasses
    private static final int MIN_MARGIN_TOP_DP = 345; // Minimum margin at 8 glasses
    private static final int GLASS_HEIGHT_DP = (1+MAX_MARGIN_TOP_DP-MIN_MARGIN_TOP_DP)/8; // Each glass increases height by 0dp
    private static final double DAILY_GOAL_LITERS = 2.8; // daily goal in liters
    private static final double GLASS_VOLUME_LITERS = 0.35; // 350 mL per glass in liters
    private int glassesOfWater;
    private TextView litersWaterTextView;
    private TextView lastTimeTextView;
    private int editPosition = -1; // -1 means no meal is currently being edited

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

        // Initialize views and set up click listeners
        recyclerView = view.findViewById(R.id.recyclerView_meals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        waterBackg = view.findViewById(R.id.water_backg);
        waterPercent = view.findViewById(R.id.water_drank); // TextView for glasses
        ImageView plusIcon = view.findViewById(R.id.plus_icon);
        ImageView minusIcon = view.findViewById(R.id.minus_icon);

        mealList = new ArrayList<>();
        mealList.add(new Meal("Breakfast", "10:45 AM", "531 Cal"));
        mealList.add(new Meal("Lunch", "03:45 PM", "1024 Cal"));

        // Pass 'this' as the listener to the adapter
        mealAdapter = new MealAdapter(mealList, this);  // 'this' refers to HomeFragment, which implements the listener
        recyclerView.setAdapter(mealAdapter);

        // Set initial values for water consumption
        glassesOfWater = 0;
        litersWaterTextView = view.findViewById(R.id.liters_water);
        updateWaterDisplay();
        lastTimeTextView = view.findViewById(R.id.last_time_1);

        // Set onClickListeners for buttons to add or remove water
        plusIcon.setOnClickListener(v -> addGlass());
        minusIcon.setOnClickListener(v -> minusGlass());



        mealInputLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String mealName = result.getData().getStringExtra("MEAL_NAME");
                        String mealCalories = result.getData().getStringExtra("MEAL_CALORIES");

                        // Get the current time as a string
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        String currentTime = sdf.format(new Date());

                        // Check if we are editing an existing meal
                        if (editPosition != -1) {
                            // Update the existing meal at editPosition
                            Meal existingMeal = mealList.get(editPosition);
                            existingMeal.updateMeal(mealName, currentTime, mealCalories);

                            mealAdapter.notifyItemChanged(editPosition);
                            editPosition = -1;
                        } else {
                            // Create a new Meal with the current time and received data
                            Meal newMeal = new Meal(mealName, currentTime, mealCalories);
                            mealList.add(newMeal);
                            mealAdapter.notifyItemInserted(mealList.size() - 1);
                        }
                    }
                }
        );



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
        mealInputLauncher.launch(intent); // Use the launcher to start the activity
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
        double litersConsumed = glassesOfWater * GLASS_VOLUME_LITERS;
        String displayText = String.format("%.2f / %.1f Liters", litersConsumed, DAILY_GOAL_LITERS);
        litersWaterTextView.setText(displayText);
    }

    // Increase by one glass height
    private void addGlass(){
        if (glassesOfWater < 8) {
            glassesOfWater++;
            waterHeight += GLASS_HEIGHT_DP;
            updateWaterDisplay();
            updateLastTime();
        }
    }
    // Decrease by one glass height
    private void minusGlass(){
            if (glassesOfWater > 0) {
                glassesOfWater--;
                waterHeight -= waterHeight;
                updateWaterDisplay();
                updateLastTime();
            }
    }

    private void updateLastTime() {
        // Get the current time
        String currentTime = getCurrentTime();
        lastTimeTextView.setText(currentTime);
    }

    // Get current time formatted as a string
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return "Last added: " + sdf.format(new Date());
    }

    private void navigateToUserProfile() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent); // Replace UserProfileActivity with your desired activity
    }

    @Override
    public void onMealClick(Meal meal) {
        // Set editPosition to the position of the clicked meal to avoid duplicates
        editPosition = mealList.indexOf(meal);

        Intent intent = new Intent(getActivity(), meal_input.class);
        intent.putExtra("MEAL_NAME", meal.getName());
        intent.putExtra("MEAL_CALORIES", meal.getCalories());
        startActivity(intent);
    }


}


