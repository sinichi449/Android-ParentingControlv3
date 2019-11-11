package com.sinichi.parentingcontrolv3.retrofit.MuslimSalat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServiceMuslimSalat {

    @GET("{lokasi}/daily.json")
    Call<JadwalSholatMuslimSalat> getJadwalMuslimSalat(@Path("lokasi") String lokasi);
}
