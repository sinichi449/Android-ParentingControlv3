package com.sinichi.parentingcontrolv3.model;

public class QuotesModel {
    public String id;
    public String quotes;
    public String author;
    public boolean valid;

    public QuotesModel() {

    }

    public QuotesModel(String quotes, String author, boolean isValid) {
        this.quotes = quotes;
        this.author = author;
        this.valid = isValid;
    }

    public String getQuotes() {
        return quotes;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
