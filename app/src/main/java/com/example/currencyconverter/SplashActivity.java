package com.example.currencyconverter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.currencyconverter.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private TextView greetingText, timeText, quoteText, dateText;
    private ImageView logoImage, waveImage;
    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler();
    private Random random = new Random();

    // Массив премиум цитат
    private String[] premiumQuotes = {
            "Финансовая свобода начинается здесь",
            "Управляй своими деньгами с умом",
            "Инвестируй в свое будущее",
            "Путь к финансовому успеху",
            "Каждая секунда на счету",
            "Деньги любят счет",
            "Будущее начинается сегодня",
            "Ваш личный финансовый помощник",
            "Премиум сервис для ваших средств",
            "Достигайте большего с нами"
    };

    // Массив премиум подписей
    private String[] premiumSignatures = {
            "Currency Converter Team",
            "Ваш финансовый партнер",
            "Инновации в финансах",
            "С заботой о ваших деньгах",
            "Premium Financial Service"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_premium);

        sessionManager = new SessionManager(this);
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        initializeViews();
        setupGreeting();
        setupDateAndTime();
        setupQuote();
        startAnimations();

        // Задержка перед переходом
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserSession();
            }
        }, 3500);
    }

    private void initializeViews() {
        greetingText = findViewById(R.id.greetingText);
        timeText = findViewById(R.id.timeText);
        quoteText = findViewById(R.id.quoteText);
        dateText = findViewById(R.id.dateText);
        logoImage = findViewById(R.id.logoImage);
        waveImage = findViewById(R.id.waveImage);
    }

    private void setupGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        String emoji;

        if (hour >= 5 && hour < 12) {
            greeting = "Доброе утро";
            emoji = "🌅";
        } else if (hour >= 12 && hour < 18) {
            greeting = "Добрый день";
            emoji = "☀️";
        } else if (hour >= 18 && hour < 22) {
            greeting = "Добрый вечер";
            emoji = "🌙";
        } else {
            greeting = "Доброй ночи";
            emoji = "✨";
        }

        // Получаем имя пользователя если есть
        String userName = sessionManager.getUserName();
        if (userName != null && !userName.equals("Гость")) {
            greetingText.setText(greeting + ", " + userName + "!");
        } else {
            greetingText.setText(greeting + "!");
        }

        // Добавляем эмодзи
        TextView emojiText = findViewById(R.id.emojiText);
        if (emojiText != null) {
            emojiText.setText(emoji);
        }
    }

    private void setupDateAndTime() {
        // Текущее время
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        timeText.setText(currentTime);

        // Текущая дата
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("ru"));
        String currentDate = dateFormat.format(new Date());
        // Делаем первую букву заглавной
        currentDate = currentDate.substring(0, 1).toUpperCase() + currentDate.substring(1);
        dateText.setText(currentDate);
    }

    private void setupQuote() {
        // Выбираем случайную цитату
        int quoteIndex = sharedPreferences.getInt("last_quote_index", -1);
        int newIndex;
        do {
            newIndex = random.nextInt(premiumQuotes.length);
        } while (newIndex == quoteIndex && premiumQuotes.length > 1);

        sharedPreferences.edit().putInt("last_quote_index", newIndex).apply();

        String quote = premiumQuotes[newIndex];
        String signature = premiumSignatures[random.nextInt(premiumSignatures.length)];

        quoteText.setText("\"" + quote + "\"");

        // Добавляем подпись
        TextView signatureText = findViewById(R.id.signatureText);
        if (signatureText != null) {
            signatureText.setText("— " + signature);
        }
    }

    private void startAnimations() {
        // Анимация логотипа
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_scale);
        logoImage.startAnimation(logoAnim);

        // Анимация волны
        Animation waveAnim = AnimationUtils.loadAnimation(this, R.anim.wave_animation);
        waveImage.startAnimation(waveAnim);

        // Анимация приветствия
        greetingText.setAlpha(0f);
        greetingText.setTranslationY(50f);
        greetingText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Анимация времени и даты
        timeText.setAlpha(0f);
        timeText.setTranslationX(-30f);
        timeText.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(600)
                .setStartDelay(600)
                .start();

        dateText.setAlpha(0f);
        dateText.setTranslationX(30f);
        dateText.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(600)
                .setStartDelay(600)
                .start();

        // Анимация цитаты
        quoteText.setAlpha(0f);
        quoteText.setScaleX(0.9f);
        quoteText.setScaleY(0.9f);
        quoteText.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setStartDelay(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        TextView signatureText = findViewById(R.id.signatureText);
        if (signatureText != null) {
            signatureText.setAlpha(0f);
            signatureText.animate()
                    .alpha(1f)
                    .setDuration(600)
                    .setStartDelay(1200)
                    .start();
        }

        // Анимация пульсации для логотипа
        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1f, 1.05f, 1f);
        pulseAnimator.setDuration(1500);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            logoImage.setScaleX(scale);
            logoImage.setScaleY(scale);
        });
        pulseAnimator.start();
    }

    private void checkUserSession() {
        // Анимация исчезновения
        View rootView = findViewById(android.R.id.content);
        rootView.animate()
                .alpha(0f)
                .setDuration(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    if (sessionManager.isLoggedIn()) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                })
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}