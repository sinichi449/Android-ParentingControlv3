package com.sinichi.parentingcontrolv3.retrofit;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("data")
    private JadwalSholat jadwalSholat;

    private Data() {

    }

    public JadwalSholat getJadwalSholat() {
        return jadwalSholat;
    }

    public void setJadwalSholat(JadwalSholat jadwalSholat) {
        this.jadwalSholat = jadwalSholat;
    }
}
