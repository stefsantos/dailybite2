package com.example.dailybite;

public class Meal {
    private String name;
    private String time;
    private int calories;
    private int proteins;
    private int fats;
    private int carbs;

    // Constructor to initialize proteins, fats, and carbs
    public Meal(String name, String time, int calories, int proteins, int fats, int carbs) {
        this.name = name;
        this.time = time;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getCalories() {
        return calories;
    }

    public int getProteins() {
        return proteins;
    }

    public int getFats() {
        return fats;
    }

    public int getCarbs() {
        return carbs;
    }

    // Update meal method, if you want to update protein, fat, and carb values as well
    public void updateMeal(String name, String time, int calories, int proteins, int fats, int carbs) {
        this.name = name;
        this.time = time;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
    }
}
