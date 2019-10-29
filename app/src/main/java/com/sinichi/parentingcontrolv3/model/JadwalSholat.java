package com.sinichi.parentingcontrolv3.model;

import com.google.gson.annotations.SerializedName;

public class JadwalSholat {
    @SerializedName("fajr")
    private String subuh;

    @SerializedName("dhuhr")
    private String dhuhr;

    @SerializedName("asr")
    private String ashar;

    @SerializedName("maghrib")
    private String maghrib;

    @SerializedName("isha")
    private String isya;

    public JadwalSholat() {

    }

    public JadwalSholat(String subuh, String dhuhr, String ashar, String maghrib, String isya) {
        this.subuh = subuh;
        this.dhuhr = dhuhr;
        this.ashar = ashar;
        this.maghrib = maghrib;
        this.isya = isya;
    }

    public String getSubuh() {
        return subuh;
    }

    public void setSubuh(String subuh) {
        this.subuh = subuh;
    }

    public String getDhuhr() {
        return dhuhr;
    }

    public void setDhuhr(String dhuhr) {
        this.dhuhr = dhuhr;
    }

    public String getAshar() {
        return ashar;
    }

    public void setAshar(String ashar) {
        this.ashar = ashar;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public void setMaghrib(String maghrib) {
        this.maghrib = maghrib;
    }

    public String getIsya() {
        return isya;
    }

    public void setIsya(String isya) {
        this.isya = isya;
    }
}
