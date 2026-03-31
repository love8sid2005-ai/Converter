package com.example.currencyconverter.models;

public class WalletCurrency {
    private String code;
    private String name;
    private double amount;
    private double rate; // Курс к USD
    private int iconResId;

    public WalletCurrency(String code, String name, double amount, double rate, int iconResId) {
        this.code = code;
        this.name = name;
        this.amount = amount;
        this.rate = rate;
        this.iconResId = iconResId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }

    public int getIconResId() {
        return iconResId;
    }

    public double getAmountInUSD() {
        return amount / rate;
    }

    public String getFormattedAmount() {
        return String.format("%.2f", amount);
    }

    public String getFormattedAmountInUSD() {
        return String.format("$%.2f", getAmountInUSD());
    }
}