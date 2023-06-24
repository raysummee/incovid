package com.ventricles.incovid;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SymptomsApiService {
    String API_ROUTE = "symptoms/index.php";
    @Headers({
        "Content-type: application/json"
    })
    @GET(API_ROUTE)
    Call<ResponseBody> sendPosts(
            @Query("name") String name,
            @Query("last_place") String last_place,
            @Query("symptoms") String symptoms
    );
}
