package com.sinichi.parentingcontrolv3.network;

import com.google.gson.annotations.SerializedName;
import com.sinichi.parentingcontrolv3.model.JadwalSholatModel;

import java.util.List;

public class Items {
    @SerializedName("items")
    public List<JadwalSholatModel> items;
    String status_valid;

    public Items(List<JadwalSholatModel> items) {
        this.items = items;
    }

    public String getStatus_valid() {
        return status_valid;
    }

    public List<JadwalSholatModel> getItems() {
        return items;
    }

    public void setItems(List<JadwalSholatModel> items) {
        this.items = items;
    }
}
