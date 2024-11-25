package com.example.dailybite;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.POST;

public interface NutritionixApiService {

    @Headers({
            "x-app-id: bb416853", // Replace with your actual app ID
            "x-app-key: 369bb1d33928cae7e772ea26cda85d7e", // Replace with your actual app key
            "Content-Type: application/json"
    })
    @GET("v2/search/instant")
    Call<NutritionixResponse> searchInstant(@Query("query") String query);

    @Headers({
            "x-app-id: bb416853", // Replace with your actual app ID
            "x-app-key: 369bb1d33928cae7e772ea26cda85d7e", // Replace with your actual app key
            "Content-Type: application/json"
    })
    @POST("v2/natural/nutrients")
    Call<NutritionixResponse> getNutrients(@Body NutritionixRequest request);
}
