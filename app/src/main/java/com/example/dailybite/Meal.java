package com.example.dailybite;

public class Meal {
    private String name;
    private String time;
    private String calories;

    public Meal(String name, String time, String calories) {
        this.name = name;
        this.time = time;
        this.calories = calories;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getCalories() {
        return calories;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    // Method to update meal properties
    public void updateMeal(String name, String time, String calories) {
        this.name = name;
        this.time = time;
        this.calories = calories;
    }
}
