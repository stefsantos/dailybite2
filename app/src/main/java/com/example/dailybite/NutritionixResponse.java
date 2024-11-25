package com.example.dailybite;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NutritionixResponse {

    @SerializedName("common")
    private List<FoodItem> common;

    @SerializedName("branded")
    private List<BrandedFoodItem> branded;

    @SerializedName("foods")
    private List<FoodItem> foods; // Used for /v2/natural/nutrients

    // Getters for the "common" and "branded" fields (used in /v2/search/instant)
    public List<FoodItem> getCommonFoods() {
        return common != null ? common : List.of();
    }

    public List<BrandedFoodItem> getBrandedFoods() {
        return branded != null ? branded : List.of();
    }

    // Getter for the "foods" field (used in /v2/natural/nutrients)
    public List<FoodItem> getFoods() {
        return foods != null ? foods : List.of();
    }

    // FoodItem class to handle common foods and natural nutrient responses
    public static class FoodItem {
        @SerializedName("food_name")
        private String foodName;

        @SerializedName("nf_calories")
        private float calories;

        @SerializedName("nf_protein")
        private float proteins;

        @SerializedName("nf_total_carbohydrate")
        private float carbs;

        @SerializedName("nf_total_fat")
        private float fats;

        @SerializedName("serving_qty")
        private int servingQty;

        @SerializedName("serving_unit")
        private String servingUnit;

        @SerializedName("photo")
        private Photo photo;

        // Getters for food details
        public String getFoodName() {
            return foodName;
        }

        public float getCalories() {
            return calories;
        }

        public float getProteins() {
            return proteins;
        }

        public float getCarbs() {
            return carbs;
        }

        public float getFats() {
            return fats;
        }

        public int getServingQty() {
            return servingQty;
        }

        public String getServingUnit() {
            return servingUnit;
        }

        public Photo getPhoto() {
            return photo;
        }

        public static class Photo {
            @SerializedName("thumb")
            private String thumbUrl;

            public String getThumbUrl() {
                return thumbUrl;
            }
        }
    }

    // BrandedFoodItem class to handle branded foods
    public static class BrandedFoodItem {
        @SerializedName("food_name")
        private String foodName;

        @SerializedName("nf_calories")
        private float calories;

        @SerializedName("photo")
        private Photo photo;

        public String getFoodName() {
            return foodName;
        }

        public float getCalories() {
            return calories;
        }

        public Photo getPhoto() {
            return photo;
        }

        public static class Photo {
            @SerializedName("thumb")
            private String thumbUrl;

            public String getThumbUrl() {
                return thumbUrl;
            }
        }
    }
}
