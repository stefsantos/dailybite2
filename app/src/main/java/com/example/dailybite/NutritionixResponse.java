package com.example.dailybite;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NutritionixResponse {

    @SerializedName("foods")
    private List<FoodItem> foods;

    public List<FoodItem> getFoods() {
        return foods;
    }

    public static class FoodItem {
        @SerializedName("food_name")
        private String foodName;

        @SerializedName("nf_calories")
        private float calories;  // Changed to float to handle decimal values

        @SerializedName("nf_protein")
        private float protein;  // Changed to float

        @SerializedName("nf_total_carbohydrate")
        private float carbs;  // Changed to float

        @SerializedName("nf_total_fat")
        private float fats;  // Changed to float

        // Constructor, if needed
        public FoodItem(String foodName, float calories, float protein, float carbs, float fats) {
            this.foodName = foodName;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fats = fats;
        }

        // Getter methods
        public String getFoodName() {
            return foodName;
        }

        public float getCalories() {
            return calories;
        }

        public float getProtein() {
            return protein;
        }

        public float getCarbs() {
            return carbs;
        }

        public float getFats() {
            return fats;
        }
    }

}
