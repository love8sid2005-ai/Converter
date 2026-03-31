package com.example.currencyconverter.models;

public class MarketCurrency {
    private String code;
    private String name;
    private double rate;
    private double changePercent;
    private boolean isRising; // true - растет, false - падает
    private int chartColor;
    private int[] chartData; // Последние 7 значений для мини-графика

    public MarketCurrency(String code, String name, double rate, double changePercent, boolean isRising) {
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.changePercent = changePercent;
        this.isRising = isRising;
        // Цвет будет устанавливаться в адаптере
        this.chartColor = isRising ? 0xFF00D4AA : 0xFFFF4D6D; // Значения по умолчанию (зеленый/красный)
        generateChartData();
    }

    private void generateChartData() {
        // Генерируем реалистичные данные для графика
        chartData = new int[7];
        double base = 50 + (Math.random() * 30);

        for (int i = 0; i < 7; i++) {
            if (isRising) {
                base += (Math.random() * 5);
            } else {
                base -= (Math.random() * 5);
            }
            chartData[i] = (int) base;
        }
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getRate() { return rate; }
    public String getFormattedRate() { return String.format("%.4f", rate); }
    public double getChangePercent() { return changePercent; }
    public String getFormattedChange() { return String.format("%+.2f%%", changePercent); }
    public boolean isRising() { return isRising; }
    public int getChartColor() { return chartColor; }
    public void setChartColor(int chartColor) { this.chartColor = chartColor; }
    public int[] getChartData() { return chartData; }
}