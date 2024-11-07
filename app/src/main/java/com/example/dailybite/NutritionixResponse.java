package com.example.dailybite;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NutritionixResponse {

    @SerializedName("common")
    private List<FoodItem> common;

    @SerializedName("branded")
    private List<BrandedFoodItem> branded;

    public List<FoodItem> getCommonFoods() {
        return common;
    }

    public List<BrandedFoodItem> getBrandedFoods() {
        return branded;
    }

    public static class FoodItem {
        @SerializedName("food_name")
        private String foodName;

        @SerializedName("serving_qty")
        private int servingQty;

        @SerializedName("serving_unit")
        private String servingUnit;

        @SerializedName("photo")
        private Photo photo;

        public String getFoodName() {
            return foodName;
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
