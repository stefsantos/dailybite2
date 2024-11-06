package com.example.dailybite;

public class NutritionixRequest {
    private String query;

    public NutritionixRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
