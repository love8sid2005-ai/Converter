package com.example.currencyconverter.models;

public class Card {
    private String type;
    private String number;
    private int iconResId;
    private boolean isActive;

    public Card(String type, String number, int iconResId, boolean isActive) {
        this.type = type;
        this.number = number;
        this.iconResId = iconResId;
        this.isActive = isActive;
    }

    public String getType() { return type; }
    public String getNumber() { return number; }
    public int getIconResId() { return iconResId; }
    public boolean isActive() { return isActive; }
}