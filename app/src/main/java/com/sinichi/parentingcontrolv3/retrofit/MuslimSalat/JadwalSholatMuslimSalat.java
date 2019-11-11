package com.sinichi.parentingcontrolv3.retrofit.MuslimSalat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JadwalSholatMuslimSalat {
    @SerializedName("items")
    private List<ItemMuslimSalat> itemMuslimSalats;

    public JadwalSholatMuslimSalat() {

    }

    public List<ItemMuslimSalat> getItemMuslimSalats() {
        return itemMuslimSalats;
    }

    public void setItemMuslimSalats(List<ItemMuslimSalat> itemMuslimSalats) {
        this.itemMuslimSalats = itemMuslimSalats;
    }
}
