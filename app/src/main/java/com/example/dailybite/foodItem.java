package com.example.dailybite;

public class foodItem {

    private String name;
    private String servingSize;
    private String calories;

    public foodItem(String name, String servingSize, String calories) {
        this.name = name;
        this.servingSize = servingSize;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public String getServingSize() {
        return servingSize;
    }

    public String getCalories() {
        return calories;
    }
}
