package com.example.currencyconverter.models;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private long timestamp;
    private int id;

    // Конструктор для новых сообщений (без id)
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    // Конструктор для загрузки из БД (с id)
    public ChatMessage(int id, String message, boolean isUser, long timestamp) {
        this.id = id;
        this.message = message;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}