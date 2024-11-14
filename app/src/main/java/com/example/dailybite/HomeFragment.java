package com.example.dailybite;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements MealAdapter.OnMealClickListener, MealAdapter.OnMealLongClickListener {
    private TextView tvProtein, tvFats, tvCarbs, tvCalories;
    private ProgressBar progressBarProteins, progressBarFats, progressBarCarbs, progressBarCalories;
    private ActivityResultLauncher<Intent> mealInputLauncher;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;
    private View waterBackg;
    private TextView waterPercent;
    private int waterHeight;
    private static final int MAX_MARGIN_TOP_DP = 468;
    private static final int MIN_MARGIN_TOP_DP = 345;
    private static final int GLASS_HEIGHT_DP = (1 + MAX_MARGIN_TOP_DP - MIN_MARGIN_TOP_DP) / 8;
    private static final double DAILY_GOAL_LITERS = 2.8;
    private static final double GLASS_VOLUME_LITERS = 0.35;
    private int glassesOfWater;
    private TextView litersWaterTextView;
    private TextView lastTimeTextView;
    private TextView username;
    private int editPosition = -1;
    private int TARGET_PROTEINS = 150;
    private int TARGET_FATS = 50;
    private int TARGET_CARBS = 190;
    private int TARGET_CALORIES = 2000;
    private int currentProteins = 0;
    private int currentFats = 0;
    private int currentCarbs = 0;
    private int currentCalories = 0;
    private boolean mealsInitialized = false;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize username TextView
        username = view.findViewById(R.id.username);

        // Fetch and display username
        loadUsername();

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
        waterPercent = view.findViewById(R.id.water_drank);
        ImageView plusIcon = view.findViewById(R.id.plus_icon);
        ImageView minusIcon = view.findViewById(R.id.minus_icon);

        mealList = new ArrayList<>();
        mealList.add(new Meal("Breakfast", "10:45 AM", 431, 20, 10, 50));
        mealList.add(new Meal("Lunch", "03:45 PM", 900, 20, 20, 90));

        mealAdapter = new MealAdapter(mealList, this, this);
        recyclerView.setAdapter(mealAdapter);

        glassesOfWater = 0;
        litersWaterTextView = view.findViewById(R.id.liters_water);
        updateWaterDisplay();
        lastTimeTextView = view.findViewById(R.id.last_time_1);

        plusIcon.setOnClickListener(v -> addGlass());
        minusIcon.setOnClickListener(v -> minusGlass());

        tvProtein = view.findViewById(R.id.tv_protein);
        tvFats = view.findViewById(R.id.tv_fats);
        tvCarbs = view.findViewById(R.id.tv_carbs);
        tvCalories = view.findViewById(R.id.tv_calories);

        progressBarProteins = view.findViewById(R.id.progressBarDeterminate_proteins);
        progressBarFats = view.findViewById(R.id.progressBarDeterminate_fats);
        progressBarCarbs = view.findViewById(R.id.progressBarDeterminate_Carbs);
        progressBarCalories = view.findViewById(R.id.progressBarDeterminate_calories);

        mealInputLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String mealName = result.getData().getStringExtra("MEAL_NAME");
                        int mealCalories = Integer.parseInt(result.getData().getStringExtra("MEAL_CALORIES"));
                        int mealProteins = Integer.parseInt(result.getData().getStringExtra("MEAL_PROTEINS"));
                        int mealFats = Integer.parseInt(result.getData().getStringExtra("MEAL_FATS"));
                        int mealCarbs = Integer.parseInt(result.getData().getStringExtra("MEAL_CARBS"));
                        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                        if (editPosition != -1) {
                            deleteMeal(mealList.get(editPosition));
                            Meal existingMeal = mealList.get(editPosition);
                            existingMeal.updateMeal(mealName, currentTime, mealCalories, mealProteins, mealFats, mealCarbs);
                            mealAdapter.notifyItemChanged(editPosition);
                            addMeal(existingMeal);
                            editPosition = -1;
                        } else {
                            Meal newMeal = new Meal(mealName, currentTime, mealCalories, mealProteins, mealFats, mealCarbs);
                            addMeal(newMeal);
                            mealList.add(newMeal);
                            mealAdapter.notifyItemInserted(mealList.size() - 1);
                        }
                    }
                }
        );

        // Fetch intake targets from Firestore
        fetchIntakeTargets();

        if (!mealsInitialized) {
            initializeMeals();
            mealsInitialized = true;
        }

        updateNutrientViews();
        return view;
    }

    private void fetchIntakeTargets() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> intake = (Map<String, Object>) documentSnapshot.get("intake");
                        if (intake != null) {
                            TARGET_PROTEINS = ((Number) intake.get("proteins")).intValue();
                            TARGET_FATS = ((Number) intake.get("fats")).intValue();
                            TARGET_CARBS = ((Number) intake.get("carbs")).intValue();
                            TARGET_CALORIES = ((Number) intake.get("calories")).intValue();
                        }
                        updateNutrientViews();
                    } else {
                        Log.d("HomeFragment", "No intake data found for this user.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("HomeFragment", "Error fetching intake data", e);
                    Toast.makeText(getContext(), "Failed to load intake targets", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateNutrientViews() {
        tvProtein.setText(currentProteins + " / " + TARGET_PROTEINS);
        progressBarProteins.setProgress((int) ((float) currentProteins / TARGET_PROTEINS * 100));

        tvFats.setText(currentFats + " / " + TARGET_FATS);
        progressBarFats.setProgress((int) ((float) currentFats / TARGET_FATS * 100));

        tvCarbs.setText(currentCarbs + " / " + TARGET_CARBS);
        progressBarCarbs.setProgress((int) ((float) currentCarbs / TARGET_CARBS * 100));

        tvCalories.setText(currentCalories + " / " + TARGET_CALORIES);
        progressBarCalories.setProgress((int) ((float) currentCalories / TARGET_CALORIES * 100));
    }

    private void loadUsername() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String savedUsername = documentSnapshot.getString("username");
                if (savedUsername != null) {
                    username.setText(savedUsername);
                }
            } else {
                Log.d("HomeFragment", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("HomeFragment", "Error fetching document", e));
    }

    private void openCalendar() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {},
                year, month, day
        );

        datePickerDialog.show();
    }

    private void navigateToMealInputWithoutDate() {
        Intent intent = new Intent(getActivity(), meal_input.class);
        mealInputLauncher.launch(intent);
    }

    private void updateWaterDisplay() {
        float waterHeightDp = glassesOfWater * GLASS_HEIGHT_DP;
        float marginTopDp = MAX_MARGIN_TOP_DP - (glassesOfWater * (MAX_MARGIN_TOP_DP - MIN_MARGIN_TOP_DP) / 8);

        int waterHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, waterHeightDp, getResources().getDisplayMetrics());
        int marginTopPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginTopDp, getResources().getDisplayMetrics());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) waterBackg.getLayoutParams();
        params.height = waterHeightPx;
        params.topMargin = marginTopPx;
        waterBackg.setLayoutParams(params);
        waterPercent.setText(glassesOfWater + " Cups");

        double litersConsumed = glassesOfWater * GLASS_VOLUME_LITERS;
        litersWaterTextView.setText(String.format("%.2f / %.1f Liters", litersConsumed, DAILY_GOAL_LITERS));
    }

    private void addGlass() {
        if (glassesOfWater < 8) {
            glassesOfWater++;
            waterHeight += GLASS_HEIGHT_DP;
            updateWaterDisplay();
            updateLastTime();
        }
    }

    private void minusGlass() {
        if (glassesOfWater > 0) {
            glassesOfWater--;
            waterHeight -= waterHeight;
            updateWaterDisplay();
            updateLastTime();
        }
    }

    private void updateLastTime() {
        String currentTime = getCurrentTime();
        lastTimeTextView.setText(currentTime);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return "Last added: " + sdf.format(new Date());
    }

    private void navigateToUserProfile() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMealClick(Meal meal) {
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
                    Meal mealToDelete = mealList.get(position);
                    mealList.remove(position);
                    deleteMeal(mealToDelete);
                    mealAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
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

    private void addMeal(Meal meal) {
        currentProteins += meal.getProteins();
        currentFats += meal.getFats();
        currentCarbs += meal.getCarbs();
        currentCalories += meal.getCalories();
        updateNutrientViews();
    }

    private void editMeal(Meal oldMeal, Meal newMeal) {
        currentProteins -= oldMeal.getProteins();
        currentFats -= oldMeal.getFats();
        currentCarbs -= oldMeal.getCarbs();
        currentCalories -= oldMeal.getCalories();

        currentProteins += newMeal.getProteins();
        currentFats += newMeal.getFats();
        currentCarbs += newMeal.getCarbs();
        currentCalories += newMeal.getCalories();

        updateNutrientViews();
    }
}
