package com.ventricles.incovid;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MigrantApiService {
    String API_ROUTE = "migrantLabour/index.php";
    @Headers({
            "Content-type: application/json"
    })
    @GET(API_ROUTE)
    Call<ResponseBody> sendPosts(
            @Query("name") String name,
            @Query("phone") String phone,
            @Query("isMsf") int isMsf,
            @Query("kisMsf") int kisMsf,
            @Query("lat") double lat,
            @Query("lon") double lon
    );
}
