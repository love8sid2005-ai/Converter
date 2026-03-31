package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.currencyconverter.adapters.QuickActionAdapter;
import com.example.currencyconverter.adapters.NewsAdapter;
import com.example.currencyconverter.adapters.PromoAdapter;
import com.example.currencyconverter.bottomsheet.AnalyticsBottomSheet;
import com.example.currencyconverter.bottomsheet.CalculatorBottomSheet;
import com.example.currencyconverter.bottomsheet.QrScannerBottomSheet;
import com.example.currencyconverter.bottomsheet.SendMoneyBottomSheet;
import com.example.currencyconverter.bottomsheet.ReceiveMoneyBottomSheet;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.News;
import com.example.currencyconverter.models.Promo;
import com.example.currencyconverter.models.QuickAction;
import com.example.currencyconverter.models.User;
import com.example.currencyconverter.utils.SessionManager;
import com.example.currencyconverter.bottomsheet.NotificationsBottomSheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends BaseActivity {

    // UI Elements
    private TextView greetingTextView;
    private TextView userNameTextView;
    private TextView weatherTextView;
    private TextView totalBalanceTextView;
    private ImageView weatherIcon;
    private ViewPager2 promoViewPager;
    private LinearLayout promoIndicator;
    private RecyclerView newsRecyclerView;
    private RecyclerView quickActionsRecyclerView;
    private LinearLayout cryptoCard, stocksCard, goldCard, weatherCard, newsCard, tipsCard;
    private TextView tvCryptoPrice, tvStocksPrice, tvGoldPrice, tvWeatherTemp, tvNewsCount;

    // Quick Access Titles
    private TextView tvQuickAccess, tvQuickAccessItems;
    private TextView tvMarketData, tvLatestNews, tvViewAll;

    // Adapters
    private PromoAdapter promoAdapter;
    private NewsAdapter newsAdapter;
    private QuickActionAdapter quickActionAdapter;

    // Data Lists
    private List<Promo> promoList;
    private List<News> newsList;
    private List<QuickAction> quickActions;

    // Timers
    private Timer promoAutoScrollTimer;
    private Handler languageSwitchHandler = new Handler();
    private int currentPromo = 0;
    private boolean isEnglish = true;

    // Language switching runnable (каждые 2 секунды)
    private Runnable languageSwitchRunnable = new Runnable() {
        @Override
        public void run() {
            isEnglish = !isEnglish;
            updateAllTexts();
            languageSwitchHandler.postDelayed(this, 2000);
        }
    };

    // Other
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;
    private String[] weatherConditions = {"Солнечно", "Облачно", "Дождь", "Ясно", "Снег"};
    private String[] weatherConditionsEn = {"Sunny", "Cloudy", "Rain", "Clear", "Snow"};
    private int[] weatherIcons = {R.drawable.ic_sun, R.drawable.ic_cloud,
            R.drawable.ic_rain, R.drawable.ic_sun,
            R.drawable.ic_snow};
    private Random random = new Random();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        int userId = sessionManager.getUserId();
        if (userId != -1) {
            currentUser = dbHelper.getUserById(userId);
        }

        initializeViews();
        setupGreeting();
        setupWeather();
        setupPromoCarousel();
        setupNews();
        setupQuickActions();
        setupPremiumCards();
        startPromoAutoScroll();
        updateBalance();

        // Запускаем переключение языка
        languageSwitchHandler.post(languageSwitchRunnable);
    }

    private void initializeViews() {
        greetingTextView = findViewById(R.id.greetingTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        totalBalanceTextView = findViewById(R.id.totalBalanceTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        weatherIcon = findViewById(R.id.weatherIcon);
        promoViewPager = findViewById(R.id.promoViewPager);
        promoIndicator = findViewById(R.id.promoIndicator);
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        quickActionsRecyclerView = findViewById(R.id.quickActionsRecyclerView);

        cryptoCard = findViewById(R.id.cryptoCard);
        stocksCard = findViewById(R.id.stocksCard);
        goldCard = findViewById(R.id.goldCard);
        weatherCard = findViewById(R.id.weatherCard);
        newsCard = findViewById(R.id.newsCard);
        tipsCard = findViewById(R.id.tipsCard);

        tvCryptoPrice = findViewById(R.id.tvCryptoPrice);
        tvStocksPrice = findViewById(R.id.tvStocksPrice);
        tvGoldPrice = findViewById(R.id.tvGoldPrice);
        tvWeatherTemp = findViewById(R.id.tvWeatherTemp);
        tvNewsCount = findViewById(R.id.tvNewsCount);

        tvQuickAccess = findViewById(R.id.tvQuickAccess);
        tvQuickAccessItems = findViewById(R.id.tvQuickAccessItems);
        tvMarketData = findViewById(R.id.tvMarketData);
        tvLatestNews = findViewById(R.id.tvLatestNews);
        tvViewAll = findViewById(R.id.tvViewAll);

        // Иконка уведомлений в хедере
        ImageView ivNotification = findViewById(R.id.ivNotification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> {
                NotificationsBottomSheet notifSheet = new NotificationsBottomSheet();
                notifSheet.show(getSupportFragmentManager(), "Notifications");
            });
        }
    }

    private void updateAllTexts() {
        updateGreeting();
        updateWeatherText();

        // Обновляем язык для промо
        if (promoList != null) {
            for (Promo promo : promoList) {
                promo.setLanguage(isEnglish);
            }
            if (promoAdapter != null) {
                promoAdapter.notifyDataSetChanged();
            }
        }

        // Обновляем язык для новостей
        if (newsAdapter != null) {
            newsAdapter.setLanguage(isEnglish);
        }

        // Обновляем быстрые действия
        if (quickActions != null) {
            for (int i = 0; i < quickActions.size(); i++) {
                String newTitle;
                switch (i) {
                    case 0:
                        newTitle = isEnglish ? "SEND" : "ОТПРАВИТЬ";
                        break;
                    case 1:
                        newTitle = isEnglish ? "SCAN" : "СКАНЕР";
                        break;
                    case 2:
                        newTitle = isEnglish ? "QR CODE" : "QR КОД";
                        break;
                    case 3:
                        newTitle = isEnglish ? "CURRENCY" : "ВАЛЮТЫ";
                        break;
                    case 4:
                        newTitle = isEnglish ? "CALC" : "КАЛЬК";
                        break;
                    case 5:
                        newTitle = isEnglish ? "ANALYTICS" : "АНАЛИТИКА";
                        break;
                    default:
                        newTitle = quickActions.get(i).getTitle();
                }
                quickActions.get(i).setTitle(newTitle);
            }
            if (quickActionAdapter != null) {
                quickActionAdapter.notifyDataSetChanged();
            }
        }

        if (tvQuickAccess != null) {
            tvQuickAccess.setText(isEnglish ? "QUICK ACCESS" : "Быстрый доступ");
        }
        if (tvQuickAccessItems != null) {
            tvQuickAccessItems.setText(isEnglish ? "6 ITEMS" : "6 пунктов");
        }

        if (tvMarketData != null) {
            tvMarketData.setText(isEnglish ? "MARKET DATA" : "Рыночные данные");
        }

        if (tvLatestNews != null) {
            tvLatestNews.setText(isEnglish ? "LATEST NEWS" : "Последние новости");
        }
        if (tvViewAll != null) {
            tvViewAll.setText(isEnglish ? "VIEW ALL ›" : "Все новости ›");
        }

        if (tvNewsCount != null) {
            tvNewsCount.setText(newsList.size() + (isEnglish ? " NEW" : " новых"));
        }

        // Обновляем новости
        updateNewsList();
    }

    private void updateBalance() {
        if (currentUser != null && totalBalanceTextView != null) {
            totalBalanceTextView.setText(String.format("$%,.2f", currentUser.getBalanceUSD()));
        }
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = isEnglish ? "GOOD MORNING" : "Доброе утро";
        } else if (hour >= 12 && hour < 18) {
            greeting = isEnglish ? "GOOD AFTERNOON" : "Добрый день";
        } else if (hour >= 18 && hour < 22) {
            greeting = isEnglish ? "GOOD EVENING" : "Добрый вечер";
        } else {
            greeting = isEnglish ? "GOOD NIGHT" : "Доброй ночи";
        }

        String userName = isEnglish ? "GUEST" : "Гость";
        if (currentUser != null && currentUser.getFullName() != null) {
            String[] nameParts = currentUser.getFullName().split(" ");
            if (nameParts.length > 0) {
                // ПРОБЛЕМА ЗДЕСЬ - имя меняется в зависимости от языка
                userName = isEnglish ? nameParts[0].toUpperCase() : nameParts[0];
            }
        }

        greetingTextView.setText(greeting);
        userNameTextView.setText(userName + "!");
    }

    private void updateWeatherText() {
        int index = random.nextInt(weatherConditions.length);
        int temp = random.nextInt(15) + 10;
        String condition = isEnglish ? weatherConditionsEn[index] : weatherConditions[index];
        weatherTextView.setText(condition + " · +" + temp + "°C");
        weatherIcon.setImageResource(weatherIcons[index]);
        if (tvWeatherTemp != null) {
            tvWeatherTemp.setText("+" + temp + "°C");
        }
    }

    private void setupGreeting() {
        updateGreeting();
    }

    private void setupWeather() {
        updateWeatherText();
    }

    private void setupPromoCarousel() {
        promoList = new ArrayList<>();

        // Премиум акции с двумя языками
        Promo promo1 = new Promo("5% CASHBACK", "5% КЭШБЭК", "ALL OPERATIONS", "ВСЕ ОПЕРАЦИИ", R.drawable.ic_cashback, "#F7A600");
        Promo promo2 = new Promo("0% COMMISSION", "0% КОМИССИЯ", "FIRST 3 CONVERSIONS", "ПЕРВЫЕ 3 ОБМЕНА", R.drawable.ic_exchange, "#F7A600");
        Promo promo3 = new Promo("7 DAYS FREE", "7 ДНЕЙ БЕСПЛАТНО", "PREMIUM STATUS", "ПРЕМИУМ СТАТУС", R.drawable.ic_premium, "#F7A600");
        Promo promo4 = new Promo("+10% PROFIT", "+10% ДОХОДА", "INVESTMENT BONUS", "ИНВЕСТИЦИОННЫЙ БОНУС", R.drawable.ic_invest, "#F7A600");

        promoList.add(promo1);
        promoList.add(promo2);
        promoList.add(promo3);
        promoList.add(promo4);

        // Устанавливаем язык для всех промо
        for (Promo promo : promoList) {
            promo.setLanguage(isEnglish);
        }

        promoAdapter = new PromoAdapter(promoList);
        promoViewPager.setAdapter(promoAdapter);

        setupPromoIndicator();

        // Настраиваем отступы между страницами
        promoViewPager.setOffscreenPageLimit(1);

        int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.05);
        promoViewPager.setPadding(padding, 0, padding, 0);
        promoViewPager.setClipToPadding(false);

        // Анимация переключения
        promoViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setAlpha(0.8f + 0.2f * (1 - Math.abs(position)));
                page.setScaleX(0.95f + 0.05f * (1 - Math.abs(position)));
                page.setScaleY(0.95f + 0.05f * (1 - Math.abs(position)));
            }
        });

        promoViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePremiumPromoIndicator(position);
                currentPromo = position;
            }
        });
    }

    private void setupPromoIndicator() {
        promoIndicator.removeAllViews();
        for (int i = 0; i < promoList.size(); i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.dot_indicator_premium);
            dot.setSelected(i == 0);
            promoIndicator.addView(dot);
        }
    }

    private void updatePremiumPromoIndicator(int position) {
        for (int i = 0; i < promoIndicator.getChildCount(); i++) {
            promoIndicator.getChildAt(i).setSelected(i == position);
        }
    }

    private void startPromoAutoScroll() {
        promoAutoScrollTimer = new Timer();
        promoAutoScrollTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (currentPromo < promoList.size() - 1) {
                        currentPromo++;
                    } else {
                        currentPromo = 0;
                    }
                    promoViewPager.setCurrentItem(currentPromo, true);
                });
            }
        }, 4000, 4000);
    }

    private void setupNews() {
        newsList = new ArrayList<>();
        updateNewsList();

        newsAdapter = new NewsAdapter(newsList, news -> {
            Toast.makeText(this, news.getTitle(), Toast.LENGTH_SHORT).show();
        });
        newsAdapter.setLanguage(isEnglish);

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);
    }

    private void updateNewsList() {
        newsList.clear();

        // Новость 1 - Биткоин
        newsList.add(new News(
                "BTC ALL-TIME HIGH",
                "БИТКОИН ИСТОРИЧЕСКИЙ МАКСИМУМ",
                "BITCOIN REACHES $65,000",
                "БИТКОИН ДОСТИГ $65,000",
                "15 MIN AGO",
                "15 МИН НАЗАД",
                R.drawable.news_btc
        ));

        // Новость 2 - ФРС
        newsList.add(new News(
                "FED RATE DECISION",
                "РЕШЕНИЕ ФРС",
                "INTEREST RATES UNCHANGED",
                "СТАВКИ БЕЗ ИЗМЕНЕНИЙ",
                "32 MIN AGO",
                "32 МИН НАЗАД",
                R.drawable.news_fed
        ));

        // Новость 3 - Новые функции
        newsList.add(new News(
                "NEW FEATURES",
                "НОВЫЕ ФУНКЦИИ",
                "BROKER CABINET ADDED",
                "ДОБАВЛЕН БРОКЕРСКИЙ КАБИНЕТ",
                "1 HOUR AGO",
                "1 ЧАС НАЗАД",
                R.drawable.news_app
        ));

        // Новость 4 - Обновление рынка
        newsList.add(new News(
                "MARKET UPDATE",
                "ОБНОВЛЕНИЕ РЫНКА",
                "S&P 500 AT RECORD HIGH",
                "S&P 500 НА РЕКОРДЕ",
                "2 HOURS AGO",
                "2 ЧАСА НАЗАД",
                R.drawable.news_stocks
        ));

        if (newsAdapter != null) {
            newsAdapter.notifyDataSetChanged();
        }

        if (tvNewsCount != null) {
            tvNewsCount.setText(newsList.size() + (isEnglish ? " NEW" : " новых"));
        }
    }

    private void setupQuickActions() {
        quickActions = new ArrayList<>();
        quickActions.add(new QuickAction(R.drawable.ic_send, isEnglish ? "SEND" : "ОТПРАВИТЬ"));
        quickActions.add(new QuickAction(R.drawable.ic_scan, isEnglish ? "SCAN" : "СКАНЕР"));
        quickActions.add(new QuickAction(R.drawable.ic_qr, isEnglish ? "QR CODE" : "QR КОД"));
        quickActions.add(new QuickAction(R.drawable.ic_currency, isEnglish ? "CURRENCY" : "ВАЛЮТЫ"));
        quickActions.add(new QuickAction(R.drawable.ic_calculator, isEnglish ? "CALC" : "КАЛЬК"));
        quickActions.add(new QuickAction(R.drawable.ic_analytics, isEnglish ? "ANALYTICS" : "АНАЛИТИКА"));

        quickActionAdapter = new QuickActionAdapter(quickActions, action -> {
            handleQuickAction(action.getTitle());
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        quickActionsRecyclerView.setLayoutManager(layoutManager);
        quickActionsRecyclerView.setAdapter(quickActionAdapter);
    }

    private void setupPremiumCards() {
        if (tvCryptoPrice != null) {
            tvCryptoPrice.setText("$65,432");
        }
        cryptoCard.setOnClickListener(v -> {
            Toast.makeText(this, isEnglish ? "CRYPTO WALLET" : "КРИПТО КОШЕЛЕК", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, BrokerActivity.class));
        });

        if (tvStocksPrice != null) {
            tvStocksPrice.setText("▲ 1.24%");
        }
        stocksCard.setOnClickListener(v -> {
            Toast.makeText(this, isEnglish ? "STOCK MARKET" : "ФОНДОВЫЙ РЫНОК", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, BrokerActivity.class));
        });

        if (tvGoldPrice != null) {
            tvGoldPrice.setText("$2,034");
        }
        goldCard.setOnClickListener(v -> {
            Toast.makeText(this, isEnglish ? "PRECIOUS METALS" : "ДРАГМЕТАЛЛЫ", Toast.LENGTH_SHORT).show();
        });

        weatherCard.setOnClickListener(v -> {
            setupWeather();
            Toast.makeText(this, isEnglish ? "WEATHER UPDATED" : "ПОГОДА ОБНОВЛЕНА", Toast.LENGTH_SHORT).show();
        });

        newsCard.setOnClickListener(v -> {
            Toast.makeText(this, isEnglish ? "ALL NEWS" : "ВСЕ НОВОСТИ", Toast.LENGTH_SHORT).show();
        });

        tipsCard.setOnClickListener(v -> {
            showDailyTip();
        });
    }

    private void showDailyTip() {
        String[] tipsEn = {
                "Invest $10 in ETF to start",
                "Use limit orders for best rates",
                "Diversify your portfolio",
                "Follow FED news for USD trends",
                "Crypto is a high risk asset",
                "Gold protects from inflation",
                "Check conversion fees regularly"
        };

        String[] tipsRu = {
                "Инвестируйте $10 в ETF для начала",
                "Используйте лимитные ордера для лучших курсов",
                "Диверсифицируйте свой портфель",
                "Следите за новостями ФРС для трендов USD",
                "Криптовалюта - высокорисковый актив",
                "Золото защищает от инфляции",
                "Регулярно проверяйте комиссии за конвертацию"
        };

        int index = random.nextInt(tipsEn.length);
        String tipText = isEnglish ? tipsEn[index] : tipsRu[index];
        Toast.makeText(this, (isEnglish ? "💡 TIP: " : "💡 СОВЕТ: ") + tipText, Toast.LENGTH_LONG).show();
    }

    private void handleQuickAction(String actionTitle) {
        Log.d("QuickAction", "Нажато: " + actionTitle);

        switch (actionTitle) {
            case "SEND":
            case "ОТПРАВИТЬ":
                SendMoneyBottomSheet sendSheet = SendMoneyBottomSheet.newInstance();
                sendSheet.setOnTransactionCompleteListener(() -> {
                    if (currentUser != null) {
                        currentUser = dbHelper.getUserById(currentUser.getId());
                        updateBalance();
                    }
                });
                sendSheet.show(getSupportFragmentManager(), "SendMoney");
                break;

            case "SCAN":
            case "СКАНЕР":
                QrScannerBottomSheet qrSheet = new QrScannerBottomSheet();
                qrSheet.setOnQrScannedListener(result -> {
                    Toast.makeText(this, "QR отсканирован: " + result, Toast.LENGTH_LONG).show();
                });
                qrSheet.show(getSupportFragmentManager(), "QrScanner");
                break;

            case "QR CODE":
            case "QR КОД":
                ReceiveMoneyBottomSheet receiveSheet = new ReceiveMoneyBottomSheet();
                receiveSheet.show(getSupportFragmentManager(), "ReceiveMoney");
                break;

            case "CURRENCY":
            case "ВАЛЮТЫ":
                startActivity(new Intent(HomeActivity.this, RatesActivity.class));
                break;

            case "CALC":
            case "КАЛЬК":
                CalculatorBottomSheet calcSheet = new CalculatorBottomSheet();
                calcSheet.show(getSupportFragmentManager(), "Calculator");
                break;

            case "ANALYTICS":
            case "АНАЛИТИКА":
                AnalyticsBottomSheet analyticsSheet = new AnalyticsBottomSheet();
                analyticsSheet.show(getSupportFragmentManager(), "Analytics");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (promoAutoScrollTimer != null) {
            promoAutoScrollTimer.cancel();
        }
        languageSwitchHandler.removeCallbacks(languageSwitchRunnable);
    }
}