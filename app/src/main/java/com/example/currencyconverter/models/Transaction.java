package com.example.currencyconverter.models;

public class Transaction {
    private int id;
    private int userId;
    private String title;
    private String description;
    private double amountOut;
    private double amountIn;
    private String currencyOut;
    private String currencyIn;
    private String type; // "exchange", "deposit", "withdrawal", "payment"
    private String status; // "completed", "pending", "failed"
    private long timestamp;
    private int iconResId;

    // Конструктор для новой транзакции
    public Transaction(int userId, String title, String description,
                       double amountOut, double amountIn,
                       String currencyOut, String currencyIn,
                       String type, String status, int iconResId) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.amountOut = amountOut;
        this.amountIn = amountIn;
        this.currencyOut = currencyOut;
        this.currencyIn = currencyIn;
        this.type = type;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
        this.iconResId = iconResId;
    }

    // Конструктор для загрузки из БД
    public Transaction(int id, int userId, String title, String description,
                       double amountOut, double amountIn,
                       String currencyOut, String currencyIn,
                       String type, String status, long timestamp, int iconResId) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.amountOut = amountOut;
        this.amountIn = amountIn;
        this.currencyOut = currencyOut;
        this.currencyIn = currencyIn;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
        this.iconResId = iconResId;
    }

    // Геттеры
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getAmountOut() { return amountOut; }
    public double getAmountIn() { return amountIn; }
    public String getCurrencyOut() { return currencyOut; }
    public String getCurrencyIn() { return currencyIn; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public int getIconResId() { return iconResId; }

    // Форматированный вывод для адаптера
    public String getFormattedAmountOut() {
        if (amountOut > 0) {
            return String.format("-%.2f %s", amountOut, currencyOut);
        }
        return "";
    }

    public String getFormattedAmountIn() {
        if (amountIn > 0) {
            return String.format("+%.2f %s", amountIn, currencyIn);
        }
        return "";
    }
}