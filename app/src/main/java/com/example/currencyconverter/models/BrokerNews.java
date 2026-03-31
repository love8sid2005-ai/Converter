package com.example.currencyconverter.models;

public class BrokerNews {
    private String title;
    private String message;
    private String time;
    private String impact; // "positive", "negative", "neutral"
    private String assetType; // "stock", "bond", "currency", "crypto"
    private int iconResId;

    public BrokerNews(String title, String message, String time, String impact, String assetType, int iconResId) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.impact = impact;
        this.assetType = assetType;
        this.iconResId = iconResId;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getImpact() { return impact; }
    public String getAssetType() { return assetType; }
    public int getIconResId() { return iconResId; }

    // Возвращаем строковое значение цвета, которое будем использовать в адаптере
    public String getImpactColor() {
        return impact;
    }
}