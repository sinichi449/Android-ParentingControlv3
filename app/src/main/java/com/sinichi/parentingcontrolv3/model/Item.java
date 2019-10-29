package com.sinichi.parentingcontrolv3.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item {
    @SerializedName("items")
    private List<JadwalSholat> items;

    public List<JadwalSholat> getItems() {
        return items;
    }

    public void setItems(List<JadwalSholat> items) {
        this.items = items;
    }
}