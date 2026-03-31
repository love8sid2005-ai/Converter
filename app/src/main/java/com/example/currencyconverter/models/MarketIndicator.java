package com.example.currencyconverter.models;

public class MarketIndicator {
    private String name;
    private String value;
    private String change;
    private boolean isPositive;
    private int iconRes;

    public MarketIndicator(String name, String value, String change, boolean isPositive, int iconRes) {
        this.name = name;
        this.value = value;
        this.change = change;
        this.isPositive = isPositive;
        this.iconRes = iconRes;
    }

    public String getName() { return name; }
    public String getValue() { return value; }
    public String getChange() { return change; }
    public boolean isPositive() { return isPositive; }
    public int getIconRes() { return iconRes; }
}