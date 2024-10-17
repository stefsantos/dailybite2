package com.example.dailybite;

public class foodItem {
    private String name;
    private int calories;

    public foodItem(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }
}
