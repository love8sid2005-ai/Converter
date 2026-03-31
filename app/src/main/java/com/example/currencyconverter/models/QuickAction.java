package com.example.currencyconverter.models;

public class QuickAction {
    private int iconResId;
    private String title;

    public QuickAction(int iconResId, String title) {
        this.iconResId = iconResId;
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}