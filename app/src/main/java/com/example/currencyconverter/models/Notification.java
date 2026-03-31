package com.example.currencyconverter.models;

public class Notification {
    private String title;
    private String message;
    private long timestamp;
    private String type; // "important", "market", "transaction"
    private boolean isRead;
    private String impact; // "positive", "negative", "neutral", "premium"

    public Notification(String title, String message, long timestamp, String type, boolean isRead, String impact) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.isRead = isRead;
        this.impact = impact;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getImpact() { return impact; }
}