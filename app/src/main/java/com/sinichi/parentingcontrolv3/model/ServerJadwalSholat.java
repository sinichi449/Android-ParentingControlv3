package com.sinichi.parentingcontrolv3.model;

public class ServerJadwalSholat {
    private String id;
    private String server;

    public ServerJadwalSholat() {

    }

    public ServerJadwalSholat(String server) {
        this.server = server;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
