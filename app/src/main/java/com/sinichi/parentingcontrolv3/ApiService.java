package com.sinichi.parentingcontrolv3;

import com.sinichi.parentingcontrolv3.model.Item;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("Malang/daily.json")
    Call<Item> getJadwalSholat();
}
