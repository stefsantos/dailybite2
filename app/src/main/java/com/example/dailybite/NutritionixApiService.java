package com.example.dailybite;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NutritionixApiService {

    @Headers({
            "x-app-id: 458392d0",
            "x-app-key: e36ac9dd137240cad20c398e4b47bc80",
            "Content-Type: application/json"
    })
    @POST("v2/natural/nutrients")
    Call<NutritionixResponse> searchFood(@Body NutritionixRequest request);
}
