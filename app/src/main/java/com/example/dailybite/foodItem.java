package com.example.dailybite;

public class foodItem {
    private String name;
    private int calories;
    private int proteins;
    private int carbs;
    private int fats;

    public foodItem(String name, int calories, int proteins, int carbs, int fats) {
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.carbs = carbs;
        this.fats = fats;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public int getProteins() {
        return proteins;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFats() {
        return fats;
    }
}
