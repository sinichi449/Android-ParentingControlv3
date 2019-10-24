package com.sinichi.parentingcontrolv3.interfaces;

import com.sinichi.parentingcontrolv3.network.Items;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("{periode}/daily.json")
    Call<Items> getJadwalSholat(@Path("periode") String periode);
}
