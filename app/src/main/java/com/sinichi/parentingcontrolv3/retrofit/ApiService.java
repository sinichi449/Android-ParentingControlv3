package com.sinichi.parentingcontrolv3.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("pray")
    Call<Data> getData(@Query("address") String lokasi);
}
