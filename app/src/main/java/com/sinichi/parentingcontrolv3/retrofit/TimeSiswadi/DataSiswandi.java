package com.sinichi.parentingcontrolv3.retrofit.TimeSiswadi;

import com.google.gson.annotations.SerializedName;

public class DataSiswandi {
    @SerializedName("data")
    private JadwalSholatSiswandi jadwalSholatSiswandi;

    private DataSiswandi() {

    }

    public JadwalSholatSiswandi getJadwalSholatSiswandi() {
        return jadwalSholatSiswandi;
    }

    public void setJadwalSholatSiswandi(JadwalSholatSiswandi jadwalSholatSiswandi) {
        this.jadwalSholatSiswandi = jadwalSholatSiswandi;
    }
}
