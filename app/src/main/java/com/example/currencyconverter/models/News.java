package com.example.currencyconverter.models;

public class News {
    private String title;
    private String description;
    private String time;
    private int imageResId;

    // Поля для двух языков
    private String titleEn;
    private String titleRu;
    private String descriptionEn;
    private String descriptionRu;
    private String timeEn;
    private String timeRu;

    // Конструктор для одного языка (старый)
    public News(String title, String description, String time, int imageResId) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.imageResId = imageResId;
    }

    // Новый конструктор для двух языков
    public News(String titleEn, String titleRu, String descriptionEn, String descriptionRu,
                String timeEn, String timeRu, int imageResId) {
        this.titleEn = titleEn;
        this.titleRu = titleRu;
        this.descriptionEn = descriptionEn;
        this.descriptionRu = descriptionRu;
        this.timeEn = timeEn;
        this.timeRu = timeRu;
        this.imageResId = imageResId;
        this.title = titleRu; // по умолчанию русский
        this.description = descriptionRu;
        this.time = timeRu;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTime() { return time; }
    public int getImageResId() { return imageResId; }

    public String getTitleEn() { return titleEn; }
    public String getTitleRu() { return titleRu; }
    public String getDescriptionEn() { return descriptionEn; }
    public String getDescriptionRu() { return descriptionRu; }
    public String getTimeEn() { return timeEn; }
    public String getTimeRu() { return timeRu; }

    public void setLanguage(boolean isEnglish) {
        if (isEnglish && titleEn != null) {
            this.title = titleEn;
            this.description = descriptionEn;
            this.time = timeEn;
        } else if (titleRu != null) {
            this.title = titleRu;
            this.description = descriptionRu;
            this.time = timeRu;
        }
    }
}