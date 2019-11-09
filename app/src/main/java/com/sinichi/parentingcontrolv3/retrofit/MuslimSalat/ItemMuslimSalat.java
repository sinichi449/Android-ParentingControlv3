package com.sinichi.parentingcontrolv3.retrofit.MuslimSalat;

import com.google.gson.annotations.SerializedName;

public class ItemMuslimSalat {
    @SerializedName("fajr")
    private String subuh;

    @SerializedName("shurooq")
    private String syurooq;

    @SerializedName("dhuhr")
    private String dhuhr;

    @SerializedName("asr")
    private String ashar;

    @SerializedName("maghrib")
    private String maghrib;

    @SerializedName("isha")
    private String isya;

    public ItemMuslimSalat() {

    }

    public String getSubuh() {
        return subuh;
    }

    public void setSubuh(String subuh) {
        this.subuh = subuh;
    }

    public String getSyurooq() {
        return syurooq;
    }

    public void setSyurooq(String syurooq) {
        this.syurooq = syurooq;
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
