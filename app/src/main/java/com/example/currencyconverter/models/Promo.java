package com.example.currencyconverter.models;

public class Promo {
    private String title;
    private String description;
    private int imageResId;
    private String color;

    // Конструктор для версии с одним языком (старый)
    public Promo(String title, String description, int imageResId, String color) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.color = color;
    }

    // Новый конструктор для двух языков
    public Promo(String titleEn, String titleRu, String descEn, String descRu, int imageResId, String color) {
        // По умолчанию используем русский, но адаптер будет переключать
        this.title = titleRu;
        this.description = descRu;
        this.imageResId = imageResId;
        this.color = color;
        // Сохраняем оба варианта для переключения языка
        this.titleEn = titleEn;
        this.titleRu = titleRu;
        this.descEn = descEn;
        this.descRu = descRu;
    }

    // Поля для двух языков
    private String titleEn;
    private String titleRu;
    private String descEn;
    private String descRu;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getColor() {
        return color;
    }

    // Метод для переключения языка
    public void setLanguage(boolean isEnglish) {
        if (isEnglish) {
            this.title = titleEn;
            this.description = descEn;
        } else {
            this.title = titleRu;
            this.description = descRu;
        }
    }
}