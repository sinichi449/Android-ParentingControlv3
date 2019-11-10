package com.sinichi.parentingcontrolv3.model;

public class TextChatModel {
    private String id;
    private String name;
    private String text;
    private String time;

    public TextChatModel() {

    }

    public TextChatModel(String name, String text, String time) {
        this.name = name;
        this.text = text;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}