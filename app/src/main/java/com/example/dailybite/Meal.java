package com.example.dailybite;

import java.sql.Timestamp;

public class Meal {
    private String name;
    private String time;
    private float calories;
    private float proteins;
    private float fats;
    private float carbs;

    // Constructor to initialize proteins, fats, and carbs
    public Meal(String name, String time, float calories, float proteins, float fats, float carbs) {
        this.name = name;
        this.time = time;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
    }

    public Meal() {
        // This  is required for Firestore deserialization
    }

    public void setTime(String currentSelectedDate){currentSelectedDate = time;}
    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public float getCalories() {
        return calories;
    }

    public float getProteins() {
        return proteins;
    }

    public float getFats() {
        return fats;
    }

    public float getCarbs() {
        return carbs;
    }

    // Update meal method, if you want to update protein, fat, and carb values as well
    public void updateMeal(String name, String time,  float calories, float proteins, float fats, float carbs) {
        this.name = name;
        this.time = time;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
    }
}
