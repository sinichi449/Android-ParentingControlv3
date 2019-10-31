package com.sinichi.parentingcontrolv3.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DataModel {

    private String id;
    private String tanggal;
    private String hari;
    private String bulan;
    private String tahun;
    private boolean isSholatSubuh;
    private boolean isSholatDhuhr;
    private boolean isSholatAshar;
    private boolean isSholatMaghrib;
    private boolean isSholatIsya;
    private boolean membantuOrangTua;
    private boolean sekolah;

    public DataModel() {
    }

    public DataModel(String tanggal, String hari, String bulan, String tahun, boolean isSholatSubuh, boolean isSholatDhuhr, boolean isSholatAshar, boolean isSholatMaghrib, boolean isSholatIsya, boolean membantuOrangTua, boolean sekolah) {
        this.tanggal = tanggal;
        this.hari = hari;
        this.bulan = bulan;
        this.tahun = tahun;
        this.isSholatSubuh = isSholatSubuh;
        this.isSholatDhuhr = isSholatDhuhr;
        this.isSholatAshar = isSholatAshar;
        this.isSholatMaghrib = isSholatMaghrib;
        this.isSholatIsya = isSholatIsya;
        this.membantuOrangTua = membantuOrangTua;
        this.sekolah = sekolah;
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

    public boolean isSholatSubuh() {
        return isSholatSubuh;
    }

    public void setSholatSubuh(boolean sholatSubuh) {
        isSholatSubuh = sholatSubuh;
    }

    public boolean isSholatDhuhr() {
        return isSholatDhuhr;
    }

    public void setSholatDhuhr(boolean sholatDhuhr) {
        isSholatDhuhr = sholatDhuhr;
    }

    public boolean isSholatAshar() {
        return isSholatAshar;
    }

    public void setSholatAshar(boolean sholatAshar) {
        isSholatAshar = sholatAshar;
    }

    public boolean isSholatMaghrib() {
        return isSholatMaghrib;
    }

    public void setSholatMaghrib(boolean sholatMaghrib) {
        isSholatMaghrib = sholatMaghrib;
    }

    public boolean isSholatIsya() {
        return isSholatIsya;
    }

    public void setSholatIsya(boolean sholatIsya) {
        isSholatIsya = sholatIsya;
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
