package com.sinichi.parentingcontrolv3.retrofit.TimeSiswadi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServiceSiswandi {

    @GET("pray")
    Call<DataSiswandi> getData(@Query("address") String lokasi);
}
