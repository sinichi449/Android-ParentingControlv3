package com.sinichi.parentingcontrolv3.retrofit;

import com.google.gson.annotations.SerializedName;

public class JadwalSholat {
    @SerializedName("Fajr")
    private String subuh;

    @SerializedName("Dhuhr")
    private String dhuhr;

    @SerializedName("Asr")
    private String ashar;

    @SerializedName("Maghrib")
    private String maghrib;

    @SerializedName("Isha")
    private String isya;

    public JadwalSholat() {

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
