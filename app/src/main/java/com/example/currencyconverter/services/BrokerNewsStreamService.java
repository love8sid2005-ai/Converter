package com.example.currencyconverter.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.currencyconverter.models.BrokerNews;
import com.example.currencyconverter.utils.BrokerNotificationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrokerNewsStreamService {
    private static BrokerNewsStreamService instance;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();
    private boolean isStreaming = false;
    private List<NewsListener> listeners = new ArrayList<>();
    private Context context;
    private BrokerNotificationManager notificationManager;

    private String[] titles = {
            "S&P 500 обновляет максимум",
            "ФРС сохраняет ставку",
            "Технологический сектор лидирует",
            "Нефть дешевеет на 2.5%",
            "Рубль укрепляется",
            "Apple отчиталась лучше прогнозов",
            "Биткоин пробил $60,000",
            "ЕЦБ готовится к снижению ставки",
            "Китай стимулирует экономику",
            "Золото растет на геополитике"
    };

    private String[] messages = {
            "Индекс достиг исторического максимума на фоне сильных отчетов",
            "Решение соответствует ожиданиям, доллар укрепляется",
            "Акции технологических компаний выросли на 3.2%",
            "Опасения по поводу спроса давят на цены",
            "Российская валюта показывает лучшую динамику среди EM",
            "Выручка превысила прогнозы на 8%",
            "Крупнейшая криптовалюта обновляет исторический максимум",
            "Рынки ожидают смягчения монетарной политики",
            "Пекин объявил о новых мерах поддержки",
            "Драгоценный металл привлекает инвесторов"
    };

    private String[] impacts = {"positive", "negative", "neutral"};

    public interface NewsListener {
        void onNewNews(BrokerNews news);
    }

    private BrokerNewsStreamService(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = new BrokerNotificationManager(context);
    }

    public static synchronized BrokerNewsStreamService getInstance(Context context) {
        if (instance == null) {
            instance = new BrokerNewsStreamService(context);
        }
        return instance;
    }

    public void startStreaming() {
        if (isStreaming) return;
        isStreaming = true;
        streamNews();
    }

    public void stopStreaming() {
        isStreaming = false;
        handler.removeCallbacksAndMessages(null);
    }

    public void addListener(NewsListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(NewsListener listener) {
        listeners.remove(listener);
    }

    private void streamNews() {
        if (!isStreaming) return;

        // Генерируем случайную новость
        int index = random.nextInt(titles.length);
        String impact = impacts[random.nextInt(impacts.length)];

        // Определяем иконку на основе impact (используем числовые значения)
        int iconResId;
        switch (impact) {
            case "positive":
                iconResId = android.R.drawable.arrow_up_float; // Системная иконка
                break;
            case "negative":
                iconResId = android.R.drawable.arrow_down_float; // Системная иконка
                break;
            default:
                iconResId = android.R.drawable.ic_dialog_info; // Системная иконка
                break;
        }

        BrokerNews news = new BrokerNews(
                titles[index],
                messages[index],
                "только что",
                impact,
                "market",
                iconResId
        );

        // Уведомляем всех слушателей
        for (NewsListener listener : listeners) {
            listener.onNewNews(news);
        }

        // Показываем системное уведомление
        notificationManager.showMarketNotification(news.getTitle(), news.getMessage(), news.getImpact());

        // Следующая новость через случайный интервал (15-45 секунд)
        int nextDelay = 15000 + random.nextInt(30000);
        handler.postDelayed(this::streamNews, nextDelay);
    }

    public void forceNews() {
        handler.removeCallbacksAndMessages(null);
        streamNews();
    }
}