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

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getCalories() {
        return calories;
    }
}

