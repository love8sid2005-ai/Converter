package com.example.currencyconverter.models;

public class Security {
    private String symbol;
    private String name;
    private double price;
    private double change;
    private double volume;
    private String type; // "stock", "bond", "etf"
    private String sector;
    private String description;
    private int iconResId;
    private int availableQuantity; // Доступное количество для покупки
    private int ownedQuantity; // Количество в портфеле пользователя

    public Security(String symbol, String name, double price, double change,
                    double volume, String type, String sector, String description, int iconResId) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = change;
        this.volume = volume;
        this.type = type;
        this.sector = sector;
        this.description = description;
        this.iconResId = iconResId;
        this.availableQuantity = (int)(Math.random() * 90000) + 10000; // 10,000 - 100,000
        this.ownedQuantity = 0;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getFormattedPrice() { return String.format("$%.2f", price); }
    public double getChange() { return change; }
    public String getFormattedChange() { return String.format("%+.2f%%", change); }
    public double getVolume() { return volume; }
    public String getFormattedVolume() {
        if (volume >= 1000000) {
            return String.format("%.1fM", volume / 1000000);
        } else if (volume >= 1000) {
            return String.format("%.1fK", volume / 1000);
        } else {
            return String.valueOf((int)volume);
        }
    }
    public String getType() { return type; }
    public String getSector() { return sector; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
    public boolean isRising() { return change >= 0; }
    public int getAvailableQuantity() { return availableQuantity; }
    public int getOwnedQuantity() { return ownedQuantity; } // ЭТОТ МЕТОД ДОЛЖЕН БЫТЬ
    public void setOwnedQuantity(int ownedQuantity) { this.ownedQuantity = ownedQuantity; }

    public String getFormattedAvailableQuantity() {
        if (availableQuantity >= 1000) {
            return String.format("%.1fK", availableQuantity / 1000.0);
        } else {
            return String.valueOf(availableQuantity);
        }
    }

    public String getFormattedOwnedQuantity() {
        if (ownedQuantity >= 1000) {
            return String.format("%.1fK", ownedQuantity / 1000.0);
        } else {
            return String.valueOf(ownedQuantity);
        }
    }
}