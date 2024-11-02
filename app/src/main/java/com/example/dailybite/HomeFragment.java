package com.example.dailybite;
import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;



public class HomeFragment extends Fragment implements MealAdapter.OnMealClickListener, MealAdapter.OnMealLongClickListener{
    private TextView tvProtein, tvFats, tvCarbs, tvCalories;
    private ProgressBar progressBarProteins, progressBarFats, progressBarCarbs, progressBarCalories;
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
    private final int TARGET_PROTEINS = 150;
    private final int TARGET_FATS = 50;
    private final int TARGET_CARBS = 190;
    private final int TARGET_CALORIES = 2000;
    private int currentProteins = 0;
    private int currentFats = 0;
    private int currentCarbs = 0;
    private int currentCalories = 0;
    private boolean mealsInitialized = false;
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
        mealList.add(new Meal("Breakfast", "10:45 AM", 431,20,10,50));
        mealList.add(new Meal("Lunch", "03:45 PM", 900,20,20,90));

        // Pass 'this' as the listener to the adapter
        mealAdapter = new MealAdapter(mealList, this, this);  // 'this' refers to HomeFragment, which implements the listener
        recyclerView.setAdapter(mealAdapter);

        // Set initial values for water consumption
        glassesOfWater = 0;
        litersWaterTextView = view.findViewById(R.id.liters_water);
        updateWaterDisplay();
        lastTimeTextView = view.findViewById(R.id.last_time_1);

        // Set onClickListeners for buttons to add or remove water
        plusIcon.setOnClickListener(v -> addGlass());
        minusIcon.setOnClickListener(v -> minusGlass());

        // Initialize TextViews
        tvProtein = view.findViewById(R.id.tv_protein);
        tvFats = view.findViewById(R.id.tv_fats);
        tvCarbs = view.findViewById(R.id.tv_carbs);
        tvCalories = view.findViewById(R.id.tv_calories);

        // Initialize ProgressBars
        progressBarProteins = view.findViewById(R.id.progressBarDeterminate_proteins);
        progressBarFats = view.findViewById(R.id.progressBarDeterminate_fats);
        progressBarCarbs = view.findViewById(R.id.progressBarDeterminate_Carbs);
        progressBarCalories = view.findViewById(R.id.progressBarDeterminate_calories);


        mealInputLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String mealName = result.getData().getStringExtra("MEAL_NAME");
                        String mealCaloriesString = result.getData().getStringExtra("MEAL_CALORIES");
                        String mealProteinsString = result.getData().getStringExtra("MEAL_PROTEINS");
                        String mealFatsString = result.getData().getStringExtra("MEAL_FATS");
                        String mealCarbsString = result.getData().getStringExtra("MEAL_CARBS");
                        int mealCalories = Integer.parseInt(mealCaloriesString);
                        int mealProteins = Integer.parseInt(mealProteinsString);
                        int mealFats = Integer.parseInt(mealFatsString);
                        int mealCarbs = Integer.parseInt(mealCarbsString);
                        // Get the current time as a string
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        String currentTime = sdf.format(new Date());

                        // Check if we are editing an existing meal
                        if (editPosition != -1) {
                            // Update the existing meal at editPosition
                            deleteMeal(mealList.get(editPosition));
                            Meal existingMeal = mealList.get(editPosition);
                            existingMeal.updateMeal(mealName, currentTime, mealCalories,mealProteins,mealFats,mealCarbs);

                            mealAdapter.notifyItemChanged(editPosition);
                            addMeal(existingMeal);
                            editPosition = -1;
                        } else {
                            // Create a new Meal with the current time and received data
                            Meal newMeal = new Meal(mealName, currentTime, mealCalories,mealProteins,mealFats,mealCarbs);
                            addMeal(newMeal);
                            mealList.add(newMeal);
                            mealAdapter.notifyItemInserted(mealList.size() - 1);
                        }
                    }
                }
        );
        if (!mealsInitialized) {
            initializeMeals();
            mealsInitialized = true;
        }

        updateNutrientViews();
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
        intent.putExtra("MEAL_CALORIES", String.valueOf(meal.getCalories()));
        intent.putExtra("MEAL_PROTEINS", String.valueOf(meal.getProteins()));
        intent.putExtra("MEAL_FATS", String.valueOf(meal.getFats()));
        intent.putExtra("MEAL_CARBS", String.valueOf(meal.getCarbs()));
        mealInputLauncher.launch(intent);
    }


    @Override
    public void onMealLongClick(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Meal")
                .setMessage("Are you sure you want to delete this meal?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Meal mealToDelete = mealList.get(position); // Retrieve the meal to delete
                    mealList.remove(position); // Remove the meal from the list
                    deleteMeal(mealToDelete); // Pass the meal to the deleteMeal method
                    mealAdapter.notifyItemRemoved(position); // Notify the adapter
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void updateNutrientViews() {
        // Update protein
        tvProtein.setText(currentProteins + " / " + TARGET_PROTEINS);
        progressBarProteins.setProgress((int) ((float) currentProteins / TARGET_PROTEINS * 100));

        // Update fats
        tvFats.setText(currentFats + " / " + TARGET_FATS);
        progressBarFats.setProgress((int) ((float) currentFats / TARGET_FATS * 100));

        // Update carbs
        tvCarbs.setText(currentCarbs + " / " + TARGET_CARBS);
        progressBarCarbs.setProgress((int) ((float) currentCarbs / TARGET_CARBS * 100));

        // Update calories
        tvCalories.setText(currentCalories + " / " + TARGET_CALORIES);
        progressBarCalories.setProgress((int) ((float) currentCalories / TARGET_CALORIES * 100));
    }

    // Add a new meal and update nutrients
    private void addMeal(Meal meal) {
        currentProteins += meal.getProteins();
        currentFats += meal.getFats();
        currentCarbs += meal.getCarbs();
        currentCalories += meal.getCalories();
        updateNutrientViews();
    }

    private void initializeMeals() {
        for (Meal meal : mealList) {
            addMeal(meal);
        }
    }

    private void deleteMeal(Meal meal) {
        currentProteins -= meal.getProteins();
        currentFats -= meal.getFats();
        currentCarbs -= meal.getCarbs();
        currentCalories -= meal.getCalories();

        updateNutrientViews();
    }

    private void editMeal(Meal oldMeal, Meal newMeal) {
        // Subtract old meal values
        currentProteins -= oldMeal.getProteins();
        currentFats -= oldMeal.getFats();
        currentCarbs -= oldMeal.getCarbs();
        currentCalories -= oldMeal.getCalories();

        // Add new meal values
        currentProteins += newMeal.getProteins();
        currentFats += newMeal.getFats();
        currentCarbs += newMeal.getCarbs();
        currentCalories += newMeal.getCalories();

        updateNutrientViews();
    }


}


