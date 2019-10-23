package com.sinichi.parentingcontrolv3.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DataModel {

    private String id;
    private String tanggal;
    private String hari;
    private String bulan;
    private String tahun;
    private String jumlahSholat;
    private boolean membantuOrangTua;
    private boolean sekolah;

    public DataModel() {
    }

    public DataModel(String tanggal, String hari, String bulan, String tahun, String jumlahSholat, boolean membantuOrangTua, boolean sekolah) {
        this.tanggal = tanggal;
        this.hari = hari;
        this.bulan = bulan;
        this.tahun = tahun;
        this.jumlahSholat = jumlahSholat;
        this.sekolah = sekolah;
        this.membantuOrangTua = membantuOrangTua;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getJumlahSholat() {
        return jumlahSholat;
    }

    public void setJumlahSholat(String jumlahSholat) {
        this.jumlahSholat = jumlahSholat;
    }

    public boolean isMembantuOrangTua() {
        return membantuOrangTua;
    }

    public void setMembantuOrangTua(boolean membantuOrangTua) {
        this.membantuOrangTua = membantuOrangTua;
    }

    public boolean isSekolah() {
        return sekolah;
    }

    public void setSekolah(boolean sekolah) {
        this.sekolah = sekolah;
    }
}
