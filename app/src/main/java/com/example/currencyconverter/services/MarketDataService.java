package com.example.currencyconverter.services;

import com.example.currencyconverter.models.MarketCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarketDataService {

    private Random random = new Random();

    public List<MarketCurrency> getMarketData() {
        List<MarketCurrency> marketData = new ArrayList<>();

        // Криптовалюты
        marketData.add(new MarketCurrency("BTC", "Bitcoin", 43567.89, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("ETH", "Ethereum", 2345.67, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("BNB", "Binance Coin", 312.45, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("SOL", "Solana", 98.76, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("XRP", "Ripple", 0.5432, getRandomChange(), random.nextBoolean()));

        // Индексы
        marketData.add(new MarketCurrency("S&P 500", "S&P 500 Index", 4789.32, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("NASDAQ", "NASDAQ Composite", 15678.43, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("DOW J", "Dow Jones", 37654.21, getRandomChange(), random.nextBoolean()));

        // Товары
        marketData.add(new MarketCurrency("GOLD", "Золото", 2034.56, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("OIL", "Нефть Brent", 82.34, getRandomChange(), random.nextBoolean()));
        marketData.add(new MarketCurrency("SILVER", "Серебро", 23.45, getRandomChange(), random.nextBoolean()));

        return marketData;
    }

    private double getRandomChange() {
        return (random.nextDouble() * 10) - 5; // от -5% до +5%
    }
}