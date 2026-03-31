package com.example.currencyconverter.models;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String birthDate;
    private String occupation;
    private double balanceUSD;
    private String currency; // Основная валюта
    private boolean isPremium;
    private long createdAt;

    // Конструктор для регистрации
    public User(String fullName, String email, String password, String phone, String birthDate, String occupation) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDate = birthDate;
        this.occupation = occupation;
        this.balanceUSD = 1000.0; // Приветственный бонус
        this.currency = "RUB";
        this.isPremium = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Конструктор для загрузки из БД
    public User(int id, String fullName, String email, String password, String phone,
                String birthDate, String occupation, double balanceUSD,
                String currency, boolean isPremium, long createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDate = birthDate;
        this.occupation = occupation;
        this.balanceUSD = balanceUSD;
        this.currency = currency;
        this.isPremium = isPremium;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public double getBalanceUSD() { return balanceUSD; }
    public void setBalanceUSD(double balanceUSD) { this.balanceUSD = balanceUSD; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}