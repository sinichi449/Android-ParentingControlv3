package com.sinichi.parentingcontrolv3.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sinichi.parentingcontrolv3.util.Constant.JADWAL_SHOLAT_URL;

public class ApiClient {

    public static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(JADWAL_SHOLAT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
