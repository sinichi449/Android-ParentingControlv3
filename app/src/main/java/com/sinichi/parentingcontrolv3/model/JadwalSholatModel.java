package com.sinichi.parentingcontrolv3.model;

import com.google.gson.annotations.SerializedName;

public class JadwalSholatModel {
    @SerializedName("date_for")
    public String tanggal;

    @SerializedName("fajr")
    public String subuh;

    @SerializedName("shurooq")
    public String shurooq;

    @SerializedName("dhuhr")
    public String zuhur;

    @SerializedName("asr")
    public String ashar;

    @SerializedName("maghrib")
    public String maghrib;

    @SerializedName("isha")
    public String isya;

    public String getTanggal() {
        return tanggal;
    }

    public String getSubuh() {
        return subuh;
    }

    public String getShurooq() {
        return shurooq;
    }

    public String getZuhur() {
        return zuhur;
    }

    public String getAshar() {
        return ashar;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public String getIsya() {
        return isya;
    }
}
