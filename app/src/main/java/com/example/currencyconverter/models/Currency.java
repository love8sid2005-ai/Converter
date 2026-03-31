package com.example.currencyconverter.models;

public class Currency {
    private String code;
    private String name;
    private double rate;
    private String country;
    private String change;

    // Конструктор с 5 параметрами
    public Currency(String code, String name, double rate, String country, String change) {
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.country = country;
        this.change = change;
    }

    // Старый конструктор для совместимости (если нужно)
    public Currency(String code, String name, double rate, String country) {
        this(code, name, rate, country, "0.0%");
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public String getCountry() {
        return country;
    }

    public String getChange() {
        return change;
    }

    public String getDisplayRate() {
        return String.format("%.4f", rate);
    }
}