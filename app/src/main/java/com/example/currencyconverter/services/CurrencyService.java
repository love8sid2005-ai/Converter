package com.example.currencyconverter.services;

import com.example.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CurrencyService {

    private Random random = new Random();

    // Имитация получения данных из API
    public List<Currency> getCurrencyRates() {
        List<Currency> currencies = new ArrayList<>();

        // Добавляем основные валюты с реалистичными данными
        currencies.add(new Currency("USD", "Доллар США", 1.0000, "США", getRandomChange()));
        currencies.add(new Currency("EUR", "Евро", 0.9215, "Евросоюз", getRandomChange()));
        currencies.add(new Currency("RUB", "Российский рубль", 90.4567, "Россия", getRandomChange()));
        currencies.add(new Currency("GBP", "Британский фунт", 0.7892, "Великобритания", getRandomChange()));
        currencies.add(new Currency("JPY", "Японская иена", 148.3256, "Япония", getRandomChange()));
        currencies.add(new Currency("CNY", "Китайский юань", 7.1987, "Китай", getRandomChange()));
        currencies.add(new Currency("CAD", "Канадский доллар", 1.3512, "Канада", getRandomChange()));
        currencies.add(new Currency("AUD", "Австралийский доллар", 1.5218, "Австралия", getRandomChange()));
        currencies.add(new Currency("CHF", "Швейцарский франк", 0.8814, "Швейцария", getRandomChange()));
        currencies.add(new Currency("TRY", "Турецкая лира", 32.1456, "Турция", getRandomChange()));
        currencies.add(new Currency("INR", "Индийская рупия", 83.1278, "Индия", getRandomChange()));
        currencies.add(new Currency("BRL", "Бразильский реал", 4.9567, "Бразилия", getRandomChange()));

        return currencies;
    }

    private String getRandomChange() {
        double change = (random.nextDouble() * 0.8) - 0.4; // от -0.4% до +0.4%
        return String.format("%+.2f%%", change);
    }

    // Метод для конвертации валют
    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        // В реальном приложении здесь будет обращение к API
        // Сейчас используем фиксированные курсы относительно USD

        double fromRate = getRateForCurrency(fromCurrency);
        double toRate = getRateForCurrency(toCurrency);

        if (fromRate == 0 || toRate == 0) {
            return 0;
        }

        // Конвертируем через USD
        double amountInUSD = amount / fromRate;
        return amountInUSD * toRate;
    }

    private double getRateForCurrency(String currencyCode) {
        switch (currencyCode) {
            case "USD": return 1.0000;
            case "EUR": return 0.9215;
            case "RUB": return 90.4567;
            case "GBP": return 0.7892;
            case "JPY": return 148.3256;
            case "CNY": return 7.1987;
            case "CAD": return 1.3512;
            case "AUD": return 1.5218;
            case "CHF": return 0.8814;
            case "TRY": return 32.1456;
            case "INR": return 83.1278;
            case "BRL": return 4.9567;
            default: return 0;
        }
    }
}