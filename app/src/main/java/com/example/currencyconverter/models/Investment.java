package com.example.currencyconverter.models;

public class Investment {
    private String symbol;
    private String name;
    private double price;
    private double quantity;  // изменено с int на double для поддержки дробных количеств
    private double totalValue;
    private double profit;
    private String type; // "stock", "bond", "crypto"
    private long purchaseDate;

    // Конструктор для целых количеств (акции, облигации)
    public Investment(String symbol, String name, double price, int quantity, String type) {
        this(symbol, name, price, (double) quantity, type);
    }

    // Основной конструктор с double для количества
    public Investment(String symbol, String name, double price, double quantity, String type) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalValue = price * quantity;
        this.profit = 0;
        this.type = type;
        this.purchaseDate = System.currentTimeMillis();
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getQuantity() { return quantity; }
    public double getTotalValue() { return totalValue; }
    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }
    public String getType() { return type; }
    public long getPurchaseDate() { return purchaseDate; }

    public String getFormattedTotalValue() {
        return String.format("$%.2f", totalValue);
    }

    public String getFormattedProfit() {
        return String.format("%+.2f", profit);
    }

    public String getFormattedQuantity() {
        if (quantity >= 1000) {
            return String.format("%.1fK", quantity / 1000.0);
        } else if (quantity >= 1) {
            return String.format("%.0f", quantity);
        } else {
            return String.format("%.4f", quantity);
        }
    }
}