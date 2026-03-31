package com.example.currencyconverter;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.adapters.BrokerNewsAdapter;
import com.example.currencyconverter.adapters.InvestmentAdapter;
import com.example.currencyconverter.adapters.SecurityAdapter;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.BrokerNews;
import com.example.currencyconverter.models.Investment;
import com.example.currencyconverter.models.Security;
import com.example.currencyconverter.models.Transaction;
import com.example.currencyconverter.services.BrokerNewsStreamService;
import com.example.currencyconverter.utils.SessionManager;
import com.example.currencyconverter.views.SparklineChartView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BrokerActivity extends BaseActivity {

    // UI Elements
    private RecyclerView rvNews, rvStocks, rvBonds, rvPortfolio;
    private TextView tvLiveIndicator, tvSP500, tvSPChange, tvNasdaq, tvNasdaqChange, tvVix, tvVixChange;
    private TextView tabNews, tabStocks, tabBonds, tabPortfolio, tabAnalytics;
    private LinearLayout layoutNews, layoutStocks, layoutBonds, layoutPortfolio, layoutAnalytics;
    private Button hireBroker1, hireBroker2, hireBroker3;
    private TextView tvTotalPortfolioValue, tvPortfolioChange, tvPortfolioProfit;
    private TextView tvInvestedAmount, tvCurrentValue, tvAssetsCount;
    private SparklineChartView chartPortfolio, chartAnalytics;

    // Analytics indicators
    private TextView tvBestPerformer, tvWorstPerformer, tvDailyChange, tvWeeklyReturn;
    private ImageView ivBestTrend, ivWorstTrend;
    private TextView tvFearGreedValue, tvFearGreedLabel;
    private TextView tvMarketCapChange, tvTotalMarketCap;

    // Adapters
    private BrokerNewsAdapter newsAdapter;
    private SecurityAdapter stocksAdapter, bondsAdapter;
    private InvestmentAdapter portfolioAdapter;
    private List<BrokerNews> newsList;
    private List<Security> stocksList, bondsList;
    private List<Investment> portfolioList;
    private BrokerNewsStreamService newsStreamService;
    private Handler handler = new Handler();
    private Random random = new Random();
    private ValueAnimator portfolioAnimator;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_broker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("BrokerActivity", "BrokerActivity создана");

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        checkNotificationPermission();

        newsStreamService = BrokerNewsStreamService.getInstance(this);

        initializeViews();
        setupMarketData();
        setupNews();
        setupStocks();
        setupBonds();
        setupPortfolio();
        setupAnalytics();
        setupTabs();
        setupBrokerHiring();
        startNewsStreaming();
        blinkLiveIndicator();
        loadPortfolioData();
        startPortfolioAnimation();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    private void initializeViews() {
        // RecyclerViews
        rvNews = findViewById(R.id.rvBrokerNews);
        rvStocks = findViewById(R.id.rvStocks);
        rvBonds = findViewById(R.id.rvBonds);
        rvPortfolio = findViewById(R.id.rvPortfolio);

        // Индикаторы рынка
        tvLiveIndicator = findViewById(R.id.tvLiveIndicator);
        tvSP500 = findViewById(R.id.tvSP500);
        tvSPChange = findViewById(R.id.tvSPChange);
        tvNasdaq = findViewById(R.id.tvNasdaq);
        tvNasdaqChange = findViewById(R.id.tvNasdaqChange);
        tvVix = findViewById(R.id.tvVix);
        tvVixChange = findViewById(R.id.tvVixChange);

        // Табы
        tabNews = findViewById(R.id.tabNews);
        tabStocks = findViewById(R.id.tabStocks);
        tabBonds = findViewById(R.id.tabBonds);
        tabPortfolio = findViewById(R.id.tabPortfolio);
        tabAnalytics = findViewById(R.id.tabAnalytics);

        // Layouts контента
        layoutNews = findViewById(R.id.layoutNews);
        layoutStocks = findViewById(R.id.layoutStocks);
        layoutBonds = findViewById(R.id.layoutBonds);
        layoutPortfolio = findViewById(R.id.layoutPortfolio);
        layoutAnalytics = findViewById(R.id.layoutAnalytics);

        // Портфель
        tvTotalPortfolioValue = findViewById(R.id.tvTotalPortfolioValue);
        tvPortfolioChange = findViewById(R.id.tvPortfolioChange);
        tvPortfolioProfit = findViewById(R.id.tvPortfolioProfit);
        chartPortfolio = findViewById(R.id.chartPortfolio);
        tvInvestedAmount = findViewById(R.id.tvInvestedAmount);
        tvCurrentValue = findViewById(R.id.tvCurrentValue);
        tvAssetsCount = findViewById(R.id.tvAssetsCount);

        // Аналитика
        chartAnalytics = findViewById(R.id.chartAnalytics);
        tvBestPerformer = findViewById(R.id.tvBestPerformer);
        tvWorstPerformer = findViewById(R.id.tvWorstPerformer);
        tvDailyChange = findViewById(R.id.tvDailyChange);
        tvWeeklyReturn = findViewById(R.id.tvWeeklyReturn);
        ivBestTrend = findViewById(R.id.ivBestTrend);
        ivWorstTrend = findViewById(R.id.ivWorstTrend);
        tvFearGreedValue = findViewById(R.id.tvFearGreedValue);
        tvFearGreedLabel = findViewById(R.id.tvFearGreedLabel);
        tvMarketCapChange = findViewById(R.id.tvMarketCapChange);
        tvTotalMarketCap = findViewById(R.id.tvTotalMarketCap);

        // Кнопки найма брокеров
        hireBroker1 = findViewById(R.id.hireBroker1);
        hireBroker2 = findViewById(R.id.hireBroker2);
        hireBroker3 = findViewById(R.id.hireBroker3);
    }

    private void setupBrokerHiring() {
        if (hireBroker1 != null) {
            hireBroker1.setOnClickListener(v -> {
                animateButton(v);
                Toast.makeText(this, "Брокер Александр Петров нанят! Теперь вы получаете персональные рекомендации.", Toast.LENGTH_LONG).show();
                addDemoInvestment();
                updateAnalytics();
            });
        }
        if (hireBroker2 != null) {
            hireBroker2.setOnClickListener(v -> {
                animateButton(v);
                Toast.makeText(this, "Трейдер Екатерина Соколова нанята! Отслеживание рынка улучшено.", Toast.LENGTH_LONG).show();
                updateMarketData();
            });
        }
        if (hireBroker3 != null) {
            hireBroker3.setOnClickListener(v -> {
                animateButton(v);
                Toast.makeText(this, "Аналитик Михаил Иванов нанят! Получайте эксклюзивные обзоры.", Toast.LENGTH_LONG).show();
                showAnalyticsNotification();
            });
        }
    }

    private void animateButton(View v) {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.button_pulse);
        v.startAnimation(pulse);
    }

    private void setupTabs() {
        tabNews.setOnClickListener(v -> switchTab("news"));
        tabStocks.setOnClickListener(v -> switchTab("stocks"));
        tabBonds.setOnClickListener(v -> switchTab("bonds"));
        tabPortfolio.setOnClickListener(v -> switchTab("portfolio"));
        tabAnalytics.setOnClickListener(v -> switchTab("analytics"));
    }

    private void switchTab(String tab) {
        resetTabColors();

        layoutNews.setVisibility(View.GONE);
        layoutStocks.setVisibility(View.GONE);
        layoutBonds.setVisibility(View.GONE);
        layoutPortfolio.setVisibility(View.GONE);
        layoutAnalytics.setVisibility(View.GONE);

        switch (tab) {
            case "news":
                tabNews.setTextColor(ContextCompat.getColor(this, R.color.white));
                tabNews.setBackgroundResource(R.drawable.tab_active);
                layoutNews.setVisibility(View.VISIBLE);
                animateContentFadeIn(layoutNews);
                break;
            case "stocks":
                tabStocks.setTextColor(ContextCompat.getColor(this, R.color.white));
                tabStocks.setBackgroundResource(R.drawable.tab_active);
                layoutStocks.setVisibility(View.VISIBLE);
                animateContentFadeIn(layoutStocks);
                break;
            case "bonds":
                tabBonds.setTextColor(ContextCompat.getColor(this, R.color.white));
                tabBonds.setBackgroundResource(R.drawable.tab_active);
                layoutBonds.setVisibility(View.VISIBLE);
                animateContentFadeIn(layoutBonds);
                break;
            case "portfolio":
                tabPortfolio.setTextColor(ContextCompat.getColor(this, R.color.white));
                tabPortfolio.setBackgroundResource(R.drawable.tab_active);
                layoutPortfolio.setVisibility(View.VISIBLE);
                animateContentFadeIn(layoutPortfolio);
                loadPortfolioData();
                break;
            case "analytics":
                tabAnalytics.setTextColor(ContextCompat.getColor(this, R.color.white));
                tabAnalytics.setBackgroundResource(R.drawable.tab_active);
                layoutAnalytics.setVisibility(View.VISIBLE);
                animateContentFadeIn(layoutAnalytics);
                updateAnalytics();
                break;
        }
    }

    private void animateContentFadeIn(View view) {
        view.setAlpha(0f);
        view.animate()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void resetTabColors() {
        tabNews.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tabStocks.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tabBonds.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tabPortfolio.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tabAnalytics.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        tabNews.setBackground(null);
        tabStocks.setBackground(null);
        tabBonds.setBackground(null);
        tabPortfolio.setBackground(null);
        tabAnalytics.setBackground(null);
    }

    private void blinkLiveIndicator() {
        handler.post(new Runnable() {
            boolean isRed = false;
            @Override
            public void run() {
                if (tvLiveIndicator != null) {
                    tvLiveIndicator.setTextColor(isRed ?
                            getColor(R.color.error) : getColor(R.color.success));
                    isRed = !isRed;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void setupMarketData() {
        if (tvSP500 != null) tvSP500.setText("5,234.45");
        if (tvSPChange != null) {
            tvSPChange.setText("+1.24%");
            tvSPChange.setTextColor(getColor(R.color.success));
        }
        if (tvNasdaq != null) tvNasdaq.setText("16,789.32");
        if (tvNasdaqChange != null) {
            tvNasdaqChange.setText("+0.87%");
            tvNasdaqChange.setTextColor(getColor(R.color.success));
        }
        if (tvVix != null) tvVix.setText("14.23");
        if (tvVixChange != null) {
            tvVixChange.setText("-2.1%");
            tvVixChange.setTextColor(getColor(R.color.success));
        }
    }

    private void setupNews() {
        newsList = new ArrayList<>();

        // Добавляем 50+ новостей
        String[] titles = {
                "Добро пожаловать в премиум брокерский кабинет!",
                "🚀 Биткоин обновляет максимум",
                "📊 S&P 500 закрылся в плюсе",
                "⚠️ ФРС сохраняет ставку",
                "📈 NVIDIA отчет побил ожидания",
                "💰 Apple запускает новые продукты",
                "📉 Нефть дешевеет на 3%",
                "🏦 Европейский центробанк снижает ставку",
                "💎 Золото достигает $2,100",
                "🤖 ИИ-бум продолжается",
                "🇨🇳 Китай стимулирует экономику",
                "🇺🇸 Данные по инфляции лучше прогнозов",
                "📊 Рынок труда США охлаждается",
                "🚗 Tesla представляет новый электромобиль",
                "💳 Visa и Mastercard снижают комиссии",
                "🏥 Pfizer разрабатывает новое лекарство",
                "📱 Meta запускает новую соцсеть",
                "🎮 Microsoft покупает студию игр",
                "🔋 Электромобили бьют рекорды продаж",
                "☁️ Облачные технологии растут на 20%",
                "🔐 Кибербезопасность в тренде",
                "🏭 Промышленное производство растет",
                "✈️ Авиаперевозки восстанавливаются",
                "🏨 Туризм бьет рекорды",
                "💼 IPO стартапов активизируется",
                "📉 Nasdaq корректируется",
                "📈 Ралли на рынке облигаций",
                "💶 Евро укрепляется к доллару",
                "💷 Фунт стерлингов падает",
                "💴 Иена достигла минимумов",
                "🇷🇺 Рубль укрепляется на нефти",
                "🇨🇳 Юань девальвируется",
                "🇮🇳 Индия обгоняет Китай по росту",
                "🇧🇷 Бразилия снижает ставку",
                "🇿🇦 ЮАР повышает ставку",
                "🇹🇷 Турция вводит контроль капитала",
                "🇲🇽 Мексика привлекает инвестиции",
                "🇦🇪 ОАЭ запускают CBDC",
                "🇸🇬 Сингапур смягчает политику",
                "🇨🇭 Швейцария укрепляет франк",
                "🇸🇪 Швеция запускает цифровую крону",
                "🇳🇴 Норвегия повышает ставку",
                "🇩🇰 Дания следует за ЕЦБ",
                "🇵🇱 Польша привлекает инвесторов",
                "🇭🇺 Венгрия снижает ставку",
                "🇨🇿 Чехия повышает ставку",
                "🇦🇺 Австралия сохраняет ставку",
                "🇳🇿 Новая Зеландия повышает ставку"
        };

        String[] messages = {
                "Следите за рыночными новостями в реальном времени и управляйте своим портфелем",
                "BTC достиг $68,000 на фоне институционального спроса",
                "Индекс вырос на 1.2% благодаря сильным отчетам",
                "Рынок ожидает снижения в сентябре",
                "Акции выросли на 8% после публикации отчета",
                "Презентация iPhone 16 вызвала ажиотаж",
                "Brent падает до $80 на опасениях спроса",
                "ЕЦБ снижает ключевую ставку до 3.5%",
                "Драгоценный металл обновляет исторический максимум",
                "Акции AI-компаний растут на 15% за месяц",
                "Пекин объявляет о новых мерах поддержки",
                "Инфляция в США замедляется до 3%",
                "Число рабочих мест выросло на 150K",
                "Новая модель Cybertruck получила 1 млн предзаказов",
                "Комиссии снижены на 0.5% для малого бизнеса",
                "Новый препарат проходит успешные испытания",
                "Threads достиг 200 млн пользователей",
                "Activision приобретена за $68 млрд",
                "Продажи электромобилей выросли на 40%",
                "AWS, Azure и Google Cloud показывают рост",
                "Инвестиции в кибербезопасность выросли на 25%",
                "PMI в США достиг 55 пунктов",
                "Пассажиропоток превысил допандемийный уровень",
                "Количество туристов выросло на 30%",
                "Выход на биржу планируют 50+ компаний",
                "Технологический сектор корректируется на 5%",
                "Доходность 10-летних облигаций падает",
                "EUR/USD достиг 1.12",
                "GBP/USD упал до 1.25 на политической нестабильности",
                "USD/JPY достиг 155",
                "Курс рубля укрепился до 90 за доллар",
                "Китайский юань ослаб до 7.3",
                "ВВП Индии вырос на 8%",
                "Selic снижена до 10.5%",
                "Ставка повышена до 8.25%",
                "Лира обвалилась на 15%",
                "Прямые инвестиции выросли на 20%",
                "Цифровой дирхам запущен в тестовом режиме",
                "MAS смягчает денежно-кредитную политику",
                "Швейцарский франк укрепился на 3%",
                "Цифровая крона будет запущена в 2025",
                "Norges Bank повышает ставку до 4.5%",
                "Национальный банк Дании следует за ЕЦБ",
                "WIG20 вырос на 10% за месяц",
                "Национальный банк Венгрии снижает ставку",
                "Чешская крона укрепляется",
                "РБА сохраняет ставку на уровне 4.35%",
                "РБНЗ повышает ставку до 5.5%"
        };

        String[] times = {
                "сейчас", "2 мин назад", "15 мин назад", "32 мин назад", "45 мин назад",
                "1 час назад", "1 час назад", "2 часа назад", "2 часа назад", "3 часа назад",
                "3 часа назад", "4 часа назад", "5 часов назад", "6 часов назад", "7 часов назад",
                "8 часов назад", "9 часов назад", "10 часов назад", "11 часов назад", "12 часов назад",
                "13 часов назад", "14 часов назад", "15 часов назад", "16 часов назад", "17 часов назад",
                "18 часов назад", "19 часов назад", "20 часов назад", "21 час назад", "22 часа назад",
                "23 часа назад", "1 день назад", "1 день назад", "2 дня назад", "2 дня назад",
                "3 дня назад", "3 дня назад", "4 дня назад", "4 дня назад", "5 дней назад",
                "5 дней назад", "6 дней назад", "1 неделю назад", "1 неделю назад", "2 недели назад",
                "2 недели назад", "3 недели назад", "3 недели назад", "1 месяц назад", "1 месяц назад"
        };

        String[] impacts = {"neutral", "positive", "positive", "neutral", "positive",
                "positive", "negative", "positive", "positive", "positive",
                "neutral", "positive", "neutral", "positive", "positive",
                "positive", "positive", "positive", "positive", "positive",
                "positive", "positive", "positive", "positive", "positive",
                "negative", "positive", "positive", "negative", "negative",
                "positive", "neutral", "positive", "positive", "neutral",
                "positive", "negative", "positive", "positive", "positive",
                "positive", "positive", "positive", "neutral", "positive",
                "positive", "neutral", "positive", "positive", "neutral"};

        String[] assetTypes = {"info", "crypto", "stocks", "economy", "stocks",
                "stocks", "commodity", "economy", "commodity", "tech",
                "economy", "economy", "economy", "auto", "fintech",
                "health", "tech", "gaming", "auto", "tech",
                "tech", "industrial", "transport", "tourism", "finance",
                "stocks", "bonds", "forex", "forex", "forex",
                "forex", "forex", "economy", "economy", "economy",
                "economy", "economy", "economy", "economy", "economy",
                "economy", "economy", "economy", "economy", "economy",
                "economy", "economy", "economy", "economy", "economy"};

        int[] icons = {android.R.drawable.ic_dialog_info, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.ic_dialog_info,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_down_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.ic_dialog_info, android.R.drawable.arrow_up_float,
                android.R.drawable.ic_dialog_info, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_down_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_down_float, android.R.drawable.arrow_down_float,
                android.R.drawable.arrow_up_float, android.R.drawable.ic_dialog_info,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.ic_dialog_info, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_down_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.ic_dialog_info,
                android.R.drawable.arrow_up_float, android.R.drawable.arrow_up_float,
                android.R.drawable.ic_dialog_info, android.R.drawable.arrow_up_float,
                android.R.drawable.arrow_up_float, android.R.drawable.ic_dialog_info};

        for (int i = 0; i < titles.length; i++) {
            newsList.add(new BrokerNews(
                    titles[i],
                    messages[i],
                    times[i],
                    impacts[i],
                    assetTypes[i],
                    icons[i]
            ));
        }

        newsAdapter = new BrokerNewsAdapter(newsList, news -> {
            showNewsDetail(news);
        });

        if (rvNews != null) {
            rvNews.setLayoutManager(new LinearLayoutManager(this));
            rvNews.setAdapter(newsAdapter);
        }
    }

    private void showNewsDetail(BrokerNews news) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_news_detail, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvNewsTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvNewsMessage);
        TextView tvTime = dialogView.findViewById(R.id.tvNewsTime);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        tvTitle.setText(news.getTitle());
        tvMessage.setText(news.getMessage());
        tvTime.setText(news.getTime());

        AlertDialog dialog = builder.create();
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setupStocks() {
        stocksList = new ArrayList<>();

        // Технологический сектор
        stocksList.add(new Security("AAPL", "Apple Inc.", 175.43, 2.34, 152300000, "stock", "Технологии", "Крупнейшая технологическая компания", R.drawable.ic_bitcoin));
        stocksList.add(new Security("MSFT", "Microsoft", 378.85, 1.56, 132100000, "stock", "Технологии", "Облачные технологии и ПО", R.drawable.ic_bitcoin));
        stocksList.add(new Security("GOOGL", "Alphabet", 142.56, -0.78, 88900000, "stock", "Технологии", "Материнская компания Google", R.drawable.ic_bitcoin));
        stocksList.add(new Security("META", "Meta Platforms", 312.45, 3.45, 56700000, "stock", "Технологии", "Социальные сети и реклама", R.drawable.ic_bitcoin));
        stocksList.add(new Security("NFLX", "Netflix", 456.78, -1.23, 23400000, "stock", "Технологии", "Стриминговый сервис", R.drawable.ic_bitcoin));
        stocksList.add(new Security("NVDA", "NVIDIA", 789.12, 5.67, 189000000, "stock", "Технологии", "Процессоры и AI", R.drawable.ic_bitcoin));
        stocksList.add(new Security("AMD", "AMD", 123.45, -2.34, 45600000, "stock", "Технологии", "Полупроводники", R.drawable.ic_bitcoin));
        stocksList.add(new Security("INTC", "Intel", 34.56, 0.12, 78900000, "stock", "Технологии", "Процессоры", R.drawable.ic_bitcoin));
        stocksList.add(new Security("TSM", "TSMC", 98.76, 1.23, 34500000, "stock", "Технологии", "Производство чипов", R.drawable.ic_bitcoin));
        stocksList.add(new Security("ADBE", "Adobe", 456.78, 0.45, 8900000, "stock", "Технологии", "Софт для дизайна", R.drawable.ic_bitcoin));
        stocksList.add(new Security("CRM", "Salesforce", 234.56, -0.67, 12300000, "stock", "Технологии", "Облачные CRM", R.drawable.ic_bitcoin));
        stocksList.add(new Security("ORCL", "Oracle", 112.34, 0.23, 15600000, "stock", "Технологии", "Базы данных", R.drawable.ic_bitcoin));

        // Потребительский сектор
        stocksList.add(new Security("AMZN", "Amazon", 145.32, 3.21, 126700000, "stock", "Потребительский", "Электронная коммерция", R.drawable.ic_bitcoin));
        stocksList.add(new Security("TSLA", "Tesla", 245.67, -5.43, 187600000, "stock", "Автомобили", "Электромобили", R.drawable.ic_bitcoin));
        stocksList.add(new Security("WMT", "Walmart", 67.89, 0.45, 23400000, "stock", "Потребительский", "Розничная торговля", R.drawable.ic_bitcoin));
        stocksList.add(new Security("KO", "Coca-Cola", 56.78, -0.23, 34500000, "stock", "Потребительский", "Напитки", R.drawable.ic_bitcoin));
        stocksList.add(new Security("PEP", "PepsiCo", 167.89, 1.34, 12300000, "stock", "Потребительский", "Напитки и закуски", R.drawable.ic_bitcoin));
        stocksList.add(new Security("MCD", "McDonald's", 278.90, 2.56, 8900000, "stock", "Потребительский", "Рестораны", R.drawable.ic_bitcoin));
        stocksList.add(new Security("SBUX", "Starbucks", 89.12, -0.67, 15600000, "stock", "Потребительский", "Кофейни", R.drawable.ic_bitcoin));
        stocksList.add(new Security("NKE", "Nike", 98.76, 1.23, 18700000, "stock", "Потребительский", "Спортивная одежда", R.drawable.ic_bitcoin));
        stocksList.add(new Security("DIS", "Disney", 89.45, -0.34, 23400000, "stock", "Потребительский", "Медиа и развлечения", R.drawable.ic_bitcoin));
        stocksList.add(new Security("HD", "Home Depot", 345.67, 2.34, 8900000, "stock", "Потребительский", "Строительные материалы", R.drawable.ic_bitcoin));

        // Финансовый сектор
        stocksList.add(new Security("JPM", "JPMorgan Chase", 189.34, 1.23, 8900000, "stock", "Финансы", "Банковские услуги", R.drawable.ic_bitcoin));
        stocksList.add(new Security("GS", "Goldman Sachs", 345.67, 2.34, 5600000, "stock", "Финансы", "Инвестиционный банкинг", R.drawable.ic_bitcoin));
        stocksList.add(new Security("V", "Visa", 256.78, 0.89, 12300000, "stock", "Финансы", "Платежные системы", R.drawable.ic_bitcoin));
        stocksList.add(new Security("MA", "Mastercard", 412.34, -1.23, 8900000, "stock", "Финансы", "Платежные системы", R.drawable.ic_bitcoin));
        stocksList.add(new Security("BAC", "Bank of America", 34.56, -0.12, 45600000, "stock", "Финансы", "Банковские услуги", R.drawable.ic_bitcoin));
        stocksList.add(new Security("WFC", "Wells Fargo", 45.67, 0.34, 23400000, "stock", "Финансы", "Банковские услуги", R.drawable.ic_bitcoin));
        stocksList.add(new Security("C", "Citigroup", 52.34, -0.23, 18900000, "stock", "Финансы", "Банковские услуги", R.drawable.ic_bitcoin));

        // Энергетика
        stocksList.add(new Security("XOM", "Exxon Mobil", 112.34, 0.56, 23400000, "stock", "Энергетика", "Нефть и газ", R.drawable.ic_bitcoin));
        stocksList.add(new Security("CVX", "Chevron", 156.78, -0.34, 16700000, "stock", "Энергетика", "Нефть и газ", R.drawable.ic_bitcoin));
        stocksList.add(new Security("COP", "ConocoPhillips", 112.34, 1.23, 12300000, "stock", "Энергетика", "Нефть и газ", R.drawable.ic_bitcoin));
        stocksList.add(new Security("SLB", "Schlumberger", 45.67, -0.56, 8900000, "stock", "Энергетика", "Нефтесервис", R.drawable.ic_bitcoin));

        // Здравоохранение
        stocksList.add(new Security("JNJ", "Johnson & Johnson", 156.78, 0.45, 15600000, "stock", "Здравоохранение", "Фармацевтика", R.drawable.ic_bitcoin));
        stocksList.add(new Security("PFE", "Pfizer", 34.56, -0.23, 45600000, "stock", "Здравоохранение", "Фармацевтика", R.drawable.ic_bitcoin));
        stocksList.add(new Security("UNH", "UnitedHealth", 489.12, 1.34, 8900000, "stock", "Здравоохранение", "Медицинское страхование", R.drawable.ic_bitcoin));
        stocksList.add(new Security("MRK", "Merck", 112.34, -0.45, 12300000, "stock", "Здравоохранение", "Фармацевтика", R.drawable.ic_bitcoin));
        stocksList.add(new Security("ABBV", "AbbVie", 145.67, 0.78, 8900000, "stock", "Здравоохранение", "Биофармацевтика", R.drawable.ic_bitcoin));

        // Телекоммуникации
        stocksList.add(new Security("T", "AT&T", 17.89, -0.12, 45600000, "stock", "Телеком", "Телекоммуникации", R.drawable.ic_bitcoin));
        stocksList.add(new Security("VZ", "Verizon", 34.56, 0.23, 23400000, "stock", "Телеком", "Телекоммуникации", R.drawable.ic_bitcoin));
        stocksList.add(new Security("TMUS", "T-Mobile", 156.78, 1.34, 8900000, "stock", "Телеком", "Телекоммуникации", R.drawable.ic_bitcoin));

        // Индустриальный сектор
        stocksList.add(new Security("BA", "Boeing", 189.34, -2.34, 12300000, "stock", "Индустриальный", "Авиастроение", R.drawable.ic_bitcoin));
        stocksList.add(new Security("CAT", "Caterpillar", 289.12, 1.56, 8900000, "stock", "Индустриальный", "Строительная техника", R.drawable.ic_bitcoin));
        stocksList.add(new Security("GE", "General Electric", 112.34, 0.78, 23400000, "stock", "Индустриальный", "Промышленность", R.drawable.ic_bitcoin));
        stocksList.add(new Security("UPS", "UPS", 145.67, -0.34, 8900000, "stock", "Индустриальный", "Логистика", R.drawable.ic_bitcoin));

        // Криптовалютные ETF
        stocksList.add(new Security("IBIT", "iShares Bitcoin Trust", 32.45, 2.34, 45600000, "stock", "Крипто", "Bitcoin ETF", R.drawable.ic_bitcoin));
        stocksList.add(new Security("GBTC", "Grayscale Bitcoin", 45.67, 3.45, 23400000, "stock", "Крипто", "Bitcoin Trust", R.drawable.ic_bitcoin));
        stocksList.add(new Security("ETHE", "Grayscale Ethereum", 23.45, 1.23, 12300000, "stock", "Крипто", "Ethereum Trust", R.drawable.ic_bitcoin));
        stocksList.add(new Security("BITO", "ProShares Bitcoin", 18.90, 2.56, 18900000, "stock", "Крипто", "Bitcoin Strategy ETF", R.drawable.ic_bitcoin));

        stocksAdapter = new SecurityAdapter(stocksList, security -> {
            showBuyDialog(security);
        });

        if (rvStocks != null) {
            rvStocks.setLayoutManager(new LinearLayoutManager(this));
            rvStocks.setAdapter(stocksAdapter);
        }
    }

    private void setupBonds() {
        bondsList = new ArrayList<>();

        // Государственные облигации
        bondsList.add(new Security("US10Y", "Казначейские облигации США 10Y", 98.45, 0.12, 15200000, "bond", "Государственные", "10-летние облигации США", R.drawable.ic_graph));
        bondsList.add(new Security("US30Y", "Казначейские облигации США 30Y", 94.23, -0.08, 8900000, "bond", "Государственные", "30-летние облигации США", R.drawable.ic_graph));
        bondsList.add(new Security("DE10Y", "Немецкие облигации 10Y", 101.34, -0.05, 5600000, "bond", "Государственные", "Bundesanleihen", R.drawable.ic_graph));
        bondsList.add(new Security("UK10Y", "Британские облигации 10Y", 97.56, 0.23, 4300000, "bond", "Государственные", "Gilts", R.drawable.ic_graph));
        bondsList.add(new Security("JP10Y", "Японские облигации 10Y", 99.87, -0.12, 6700000, "bond", "Государственные", "JGB", R.drawable.ic_graph));
        bondsList.add(new Security("FR10Y", "Французские облигации 10Y", 100.23, 0.08, 3900000, "bond", "Государственные", "OAT", R.drawable.ic_graph));
        bondsList.add(new Security("IT10Y", "Итальянские облигации 10Y", 95.67, -0.34, 7800000, "bond", "Государственные", "BTP", R.drawable.ic_graph));
        bondsList.add(new Security("ES10Y", "Испанские облигации 10Y", 96.45, 0.15, 3400000, "bond", "Государственные", "Bonos", R.drawable.ic_graph));
        bondsList.add(new Security("RU30", "ОФЗ-26238", 97.23, -0.08, 8900000, "bond", "Государственные", "Облигации РФ", R.drawable.ic_graph));
        bondsList.add(new Security("RU40", "ОФЗ-26240", 94.56, 0.15, 6700000, "bond", "Государственные", "Облигации РФ", R.drawable.ic_graph));
        bondsList.add(new Security("CN10Y", "Китайские облигации 10Y", 102.34, 0.05, 12300000, "bond", "Государственные", "Chinese Bonds", R.drawable.ic_graph));

        // Корпоративные облигации
        bondsList.add(new Security("AAPL30", "Apple 3.5% 2030", 103.45, 0.34, 2300000, "bond", "Корпоративные", "Облигации Apple", R.drawable.ic_graph));
        bondsList.add(new Security("MSFT30", "Microsoft 2.8% 2032", 102.67, 0.23, 1900000, "bond", "Корпоративные", "Облигации Microsoft", R.drawable.ic_graph));
        bondsList.add(new Security("GOOG30", "Google 2.5% 2033", 101.89, 0.12, 1800000, "bond", "Корпоративные", "Облигации Alphabet", R.drawable.ic_graph));
        bondsList.add(new Security("AMZN30", "Amazon 3.0% 2031", 102.34, 0.18, 2100000, "bond", "Корпоративные", "Облигации Amazon", R.drawable.ic_graph));
        bondsList.add(new Security("TSLA30", "Tesla 5.5% 2030", 98.76, -0.45, 1500000, "bond", "Корпоративные", "Облигации Tesla", R.drawable.ic_graph));
        bondsList.add(new Security("NVDA30", "NVIDIA 2.8% 2032", 104.56, 0.56, 1700000, "bond", "Корпоративные", "Облигации NVIDIA", R.drawable.ic_graph));
        bondsList.add(new Security("JPM30", "JPMorgan 4.0% 2030", 101.23, 0.08, 2800000, "bond", "Корпоративные", "Облигации JPMorgan", R.drawable.ic_graph));
        bondsList.add(new Security("GS30", "Goldman Sachs 3.8% 2031", 100.89, -0.05, 2400000, "bond", "Корпоративные", "Облигации Goldman Sachs", R.drawable.ic_graph));
        bondsList.add(new Security("VZ30", "Verizon 4.2% 2030", 99.56, -0.12, 3200000, "bond", "Корпоративные", "Облигации Verizon", R.drawable.ic_graph));
        bondsList.add(new Security("T30", "AT&T 4.5% 2030", 98.34, -0.23, 4500000, "bond", "Корпоративные", "Облигации AT&T", R.drawable.ic_graph));

        // Корпоративные облигации (продолжение)
        bondsList.add(new Security("WMT30", "Walmart 2.9% 2031", 102.45, 0.15, 1900000, "bond", "Корпоративные", "Облигации Walmart", R.drawable.ic_graph));
        bondsList.add(new Security("KO30", "Coca-Cola 2.7% 2032", 101.78, 0.09, 1600000, "bond", "Корпоративные", "Облигации Coca-Cola", R.drawable.ic_graph));
        bondsList.add(new Security("PEP30", "PepsiCo 2.9% 2031", 102.12, 0.11, 1700000, "bond", "Корпоративные", "Облигации PepsiCo", R.drawable.ic_graph));
        bondsList.add(new Security("MCD30", "McDonald's 3.2% 2030", 101.45, 0.06, 1500000, "bond", "Корпоративные", "Облигации McDonald's", R.drawable.ic_graph));

        // Высокодоходные облигации
        bondsList.add(new Security("HY5Y", "High Yield 5Y", 78.45, 1.23, 890000, "bond", "Высокодоходные", "Мусорные облигации", R.drawable.ic_graph));
        bondsList.add(new Security("HY10Y", "High Yield 10Y", 72.34, -0.45, 670000, "bond", "Высокодоходные", "Мусорные облигации", R.drawable.ic_graph));
        bondsList.add(new Security("HY20Y", "High Yield 20Y", 65.67, 0.89, 450000, "bond", "Высокодоходные", "Мусорные облигации", R.drawable.ic_graph));

        // Развивающиеся рынки
        bondsList.add(new Security("EMB", "Emerging Markets", 89.67, -0.23, 4500000, "bond", "Развивающиеся рынки", "Облигации EM", R.drawable.ic_graph));
        bondsList.add(new Security("BZL30", "Бразилия 2030", 87.45, 0.56, 1200000, "bond", "Развивающиеся рынки", "Бразильские облигации", R.drawable.ic_graph));
        bondsList.add(new Security("IND30", "Индия 2030", 92.34, 0.78, 1500000, "bond", "Развивающиеся рынки", "Индийские облигации", R.drawable.ic_graph));
        bondsList.add(new Security("MEX30", "Мексика 2030", 88.90, 0.34, 980000, "bond", "Развивающиеся рынки", "Мексиканские облигации", R.drawable.ic_graph));
        bondsList.add(new Security("TUR30", "Турция 2030", 76.54, -1.23, 1100000, "bond", "Развивающиеся рынки", "Турецкие облигации", R.drawable.ic_graph));
        bondsList.add(new Security("ZAF30", "ЮАР 2030", 85.67, 0.45, 890000, "bond", "Развивающиеся рынки", "Южноафриканские облигации", R.drawable.ic_graph));

        bondsAdapter = new SecurityAdapter(bondsList, security -> {
            showBuyDialog(security);
        });

        if (rvBonds != null) {
            rvBonds.setLayoutManager(new LinearLayoutManager(this));
            rvBonds.setAdapter(bondsAdapter);
        }
    }

    private void setupPortfolio() {
        portfolioList = new ArrayList<>();
        portfolioAdapter = new InvestmentAdapter(portfolioList, investment -> {
            showInvestmentDetail(investment);
        });

        if (rvPortfolio != null) {
            rvPortfolio.setLayoutManager(new LinearLayoutManager(this));
            rvPortfolio.setAdapter(portfolioAdapter);
        }
    }

    private void setupAnalytics() {
        float[] analyticsData = {32000, 31800, 32200, 32100, 32500, 32800, 33100};
        if (chartAnalytics != null) {
            chartAnalytics.setChartData(analyticsData, true, getColor(R.color.accent));
        }
    }

    private void updateAnalytics() {
        // Лучший и худший исполнитель из портфеля
        if (portfolioList.isEmpty()) {
            tvBestPerformer.setText("—");
            tvWorstPerformer.setText("—");
            tvDailyChange.setText("0.00%");
            tvWeeklyReturn.setText("0.00%");

            // Обновление индекса страха и жадности (даже если портфель пуст)
            updateFearGreedIndex();
            updateMarketCapStats();
            return;
        }

        double bestReturn = -Double.MAX_VALUE;
        double worstReturn = Double.MAX_VALUE;
        String bestName = "";
        String worstName = "";

        for (Investment inv : portfolioList) {
            double currentPrice = inv.getPrice() * (1 + (random.nextDouble() - 0.5) * 0.2);
            double returnPct = ((currentPrice - inv.getPrice()) / inv.getPrice()) * 100;

            if (returnPct > bestReturn) {
                bestReturn = returnPct;
                bestName = inv.getSymbol();
            }
            if (returnPct < worstReturn) {
                worstReturn = returnPct;
                worstName = inv.getSymbol();
            }
        }

        tvBestPerformer.setText(String.format("%s +%.1f%%", bestName, bestReturn));
        tvBestPerformer.setTextColor(getColor(R.color.success));
        ivBestTrend.setImageResource(R.drawable.ic_trend_up);
        ivBestTrend.setColorFilter(getColor(R.color.success));

        tvWorstPerformer.setText(String.format("%s %.1f%%", worstName, worstReturn));
        tvWorstPerformer.setTextColor(getColor(R.color.error));
        ivWorstTrend.setImageResource(R.drawable.ic_trend_down);
        ivWorstTrend.setColorFilter(getColor(R.color.error));

        double dailyChange = (random.nextDouble() * 3) - 1.5;
        double weeklyReturn = (random.nextDouble() * 8) - 2;

        tvDailyChange.setText(String.format("%+.2f%%", dailyChange));
        tvDailyChange.setTextColor(dailyChange >= 0 ? getColor(R.color.success) : getColor(R.color.error));

        tvWeeklyReturn.setText(String.format("%+.2f%%", weeklyReturn));
        tvWeeklyReturn.setTextColor(weeklyReturn >= 0 ? getColor(R.color.success) : getColor(R.color.error));

        // Обновление индекса страха и жадности
        updateFearGreedIndex();

        // Обновление рыночной капитализации
        updateMarketCapStats();
    }

    private void updateFearGreedIndex() {
        int fearGreed = 45 + random.nextInt(50);
        TextView tvFearGreedValue = findViewById(R.id.tvFearGreedValue);
        TextView tvFearGreedLabel = findViewById(R.id.tvFearGreedLabel);

        if (tvFearGreedValue != null && tvFearGreedLabel != null) {
            tvFearGreedValue.setText(String.valueOf(fearGreed));
            if (fearGreed >= 70) {
                tvFearGreedLabel.setText("Жадность");
                tvFearGreedLabel.setTextColor(getColor(R.color.success));
                tvFearGreedValue.setTextColor(getColor(R.color.success));
            } else if (fearGreed <= 30) {
                tvFearGreedLabel.setText("Страх");
                tvFearGreedLabel.setTextColor(getColor(R.color.error));
                tvFearGreedValue.setTextColor(getColor(R.color.error));
            } else {
                tvFearGreedLabel.setText("Нейтрально");
                tvFearGreedLabel.setTextColor(getColor(R.color.warning));
                tvFearGreedValue.setTextColor(getColor(R.color.warning));
            }
        }
    }

    private void updateMarketCapStats() {
        double marketCap = 2.45 + (random.nextDouble() * 0.3);
        double marketCapChange = (random.nextDouble() * 6) - 2;

        TextView tvTotalMarketCap = findViewById(R.id.tvTotalMarketCap);
        TextView tvMarketCapChange = findViewById(R.id.tvMarketCapChange);

        if (tvTotalMarketCap != null) {
            tvTotalMarketCap.setText(String.format("$%.2fT", marketCap));
        }
        if (tvMarketCapChange != null) {
            tvMarketCapChange.setText(String.format("%+.2f%%", marketCapChange));
            tvMarketCapChange.setTextColor(marketCapChange >= 0 ? getColor(R.color.success) : getColor(R.color.error));
        }
    }

    private void showAnalyticsNotification() {
        Toast.makeText(this, "📊 Эксклюзивный обзор рынка отправлен в уведомления", Toast.LENGTH_LONG).show();
    }

    private void loadPortfolioData() {
        if (portfolioList.isEmpty()) {
            addDemoInvestments();
        }

        updatePortfolioStats();
        updatePortfolioChart();
    }

    private void updatePortfolioChart() {
        if (portfolioList.isEmpty()) {
            float[] emptyData = {0, 0, 0, 0, 0, 0, 0};
            chartPortfolio.setChartData(emptyData, false, getColor(R.color.text_disabled));
            return;
        }

        float[] chartData = new float[7];
        double baseValue = getTotalPortfolioValue();

        for (int i = 0; i < 7; i++) {
            double change = (random.nextDouble() - 0.5) * 0.1 * baseValue;
            baseValue += change;
            chartData[i] = (float) baseValue;
            if (chartData[i] < 0) chartData[i] = 0;
        }

        boolean isRising = chartData[6] > chartData[0];
        chartPortfolio.setChartData(chartData, isRising,
                isRising ? getColor(R.color.success) : getColor(R.color.error));
    }

    private void startPortfolioAnimation() {
        handler.postDelayed(() -> {
            if (portfolioList != null && !portfolioList.isEmpty()) {
                updatePortfolioChart();
            }
            handler.postDelayed(this::startPortfolioAnimation, 10000);
        }, 10000);
    }

    private double getTotalPortfolioValue() {
        double total = 0;
        for (Investment inv : portfolioList) {
            total += inv.getPrice() * inv.getQuantity();
        }
        return total;
    }

    private void addDemoInvestments() {
        portfolioList.add(new Investment("AAPL", "Apple Inc.", 175.43, 10, "stock"));
        portfolioList.add(new Investment("MSFT", "Microsoft", 378.85, 5, "stock"));
        portfolioList.add(new Investment("BTC", "Bitcoin", 65432, 0.5, "crypto"));
        portfolioList.add(new Investment("ETH", "Ethereum", 2345.67, 1.2, "crypto"));
        portfolioAdapter.notifyDataSetChanged();
    }

    private void addDemoInvestment() {
        String[] symbols = {"NVDA", "AMD", "META", "TSLA"};
        String[] names = {"NVIDIA", "AMD", "Meta Platforms", "Tesla"};
        double[] prices = {789.12, 123.45, 312.45, 245.67};
        int index = random.nextInt(symbols.length);

        Investment demo = new Investment(symbols[index], names[index], prices[index], 2, "stock");
        portfolioList.add(demo);
        portfolioAdapter.notifyDataSetChanged();
        updatePortfolioStats();
        updatePortfolioChart();
        updateAnalytics();
        Toast.makeText(this, "Добавлено в портфель: " + names[index] + " x2", Toast.LENGTH_SHORT).show();
    }

    private void updatePortfolioStats() {
        double totalInvested = 0;
        double totalCurrent = 0;

        for (Investment inv : portfolioList) {
            totalInvested += inv.getTotalValue();
            totalCurrent += inv.getPrice() * inv.getQuantity();
        }

        double profit = totalCurrent - totalInvested;
        double profitPercent = totalInvested > 0 ? (profit / totalInvested) * 100 : 0;

        animateNumberValue(tvTotalPortfolioValue, 0, totalCurrent);
        tvInvestedAmount.setText(currencyFormat.format(totalInvested));
        tvCurrentValue.setText(currencyFormat.format(totalCurrent));
        tvAssetsCount.setText(String.valueOf(portfolioList.size()));

        if (profit >= 0) {
            tvPortfolioChange.setText(String.format("+%.2f%%", profitPercent));
            tvPortfolioChange.setTextColor(getColor(R.color.success));
            tvPortfolioProfit.setText(String.format("+%s", currencyFormat.format(profit)));
            tvPortfolioProfit.setTextColor(getColor(R.color.success));
        } else {
            tvPortfolioChange.setText(String.format("%.2f%%", profitPercent));
            tvPortfolioChange.setTextColor(getColor(R.color.error));
            tvPortfolioProfit.setText(currencyFormat.format(profit));
            tvPortfolioProfit.setTextColor(getColor(R.color.error));
        }

        animatePortfolioValue();
    }

    private void animateNumberValue(TextView textView, double from, double to) {
        ValueAnimator animator = ValueAnimator.ofFloat((float) from, (float) to);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(currencyFormat.format(value));
        });
        animator.start();
    }

    private void animatePortfolioValue() {
        tvTotalPortfolioValue.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(200)
                .withEndAction(() -> tvTotalPortfolioValue.animate().scaleX(1f).scaleY(1f).setDuration(200).start())
                .start();
    }

    private void showInvestmentDetail(Investment investment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_investment_detail, null);
        builder.setView(dialogView);

        TextView tvSymbol = dialogView.findViewById(R.id.tvSymbol);
        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvQuantity = dialogView.findViewById(R.id.tvQuantity);
        TextView tvAvgPrice = dialogView.findViewById(R.id.tvAvgPrice);
        TextView tvTotalValue = dialogView.findViewById(R.id.tvTotalValue);
        TextView tvCurrentPrice = dialogView.findViewById(R.id.tvCurrentPrice);
        TextView tvProfit = dialogView.findViewById(R.id.tvProfit);
        Button btnClose = dialogView.findViewById(R.id.btnClose);
        Button btnSell = dialogView.findViewById(R.id.btnSell);

        tvSymbol.setText(investment.getSymbol());
        tvName.setText(investment.getName());
        tvQuantity.setText(formatQuantity(investment.getQuantity()) + " шт.");
        tvAvgPrice.setText(currencyFormat.format(investment.getPrice()));
        tvTotalValue.setText(currencyFormat.format(investment.getTotalValue()));

        double currentPrice = investment.getPrice() * (1 + (random.nextDouble() - 0.5) * 0.1);
        tvCurrentPrice.setText(currencyFormat.format(currentPrice));

        double profit = (currentPrice - investment.getPrice()) * investment.getQuantity();
        double profitPercent = (profit / investment.getTotalValue()) * 100;

        if (profit >= 0) {
            tvProfit.setText(String.format("+%s (+%.2f%%)", currencyFormat.format(profit), profitPercent));
            tvProfit.setTextColor(getColor(R.color.success));
        } else {
            tvProfit.setText(String.format("%s (%.2f%%)", currencyFormat.format(profit), profitPercent));
            tvProfit.setTextColor(getColor(R.color.error));
        }

        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnSell.setOnClickListener(v -> {
            showSellDialog(investment);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showSellDialog(Investment investment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_sell_security, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvPrice = dialogView.findViewById(R.id.tvPrice);
        TextView tvQuantity = dialogView.findViewById(R.id.tvQuantity);
        TextView tvTotal = dialogView.findViewById(R.id.tvTotal);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        Button btnDecrease = dialogView.findViewById(R.id.btnDecrease);
        Button btnIncrease = dialogView.findViewById(R.id.btnIncrease);
        Button btnSell = dialogView.findViewById(R.id.btnSell);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        TextView preset25 = dialogView.findViewById(R.id.preset25);
        TextView preset50 = dialogView.findViewById(R.id.preset50);
        TextView preset75 = dialogView.findViewById(R.id.preset75);
        TextView preset100 = dialogView.findViewById(R.id.preset100);

        tvTitle.setText(investment.getName() + " (" + investment.getSymbol() + ")");
        tvPrice.setText(currencyFormat.format(investment.getPrice()));

        final double[] quantity = {1.0};
        final double maxQuantity = investment.getQuantity();
        tvQuantity.setText("Доступно: " + formatQuantity(maxQuantity) + " шт.");
        etQuantity.setText("1");
        tvTotal.setText(currencyFormat.format(investment.getPrice()));

        if (preset25 != null) {
            preset25.setOnClickListener(v -> {
                quantity[0] = maxQuantity * 0.25;
                etQuantity.setText(formatQuantity(quantity[0]));
                tvTotal.setText(currencyFormat.format(investment.getPrice() * quantity[0]));
            });
        }
        if (preset50 != null) {
            preset50.setOnClickListener(v -> {
                quantity[0] = maxQuantity * 0.5;
                etQuantity.setText(formatQuantity(quantity[0]));
                tvTotal.setText(currencyFormat.format(investment.getPrice() * quantity[0]));
            });
        }
        if (preset75 != null) {
            preset75.setOnClickListener(v -> {
                quantity[0] = maxQuantity * 0.75;
                etQuantity.setText(formatQuantity(quantity[0]));
                tvTotal.setText(currencyFormat.format(investment.getPrice() * quantity[0]));
            });
        }
        if (preset100 != null) {
            preset100.setOnClickListener(v -> {
                quantity[0] = maxQuantity;
                etQuantity.setText(formatQuantity(quantity[0]));
                tvTotal.setText(currencyFormat.format(investment.getPrice() * quantity[0]));
            });
        }

        btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 0.01) {
                double step = maxQuantity * 0.01;
                quantity[0] = Math.max(0.01, quantity[0] - step);
                etQuantity.setText(formatQuantity(quantity[0]));
                tvTotal.setText(currencyFormat.format(investment.getPrice() * quantity[0]));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity[0] < maxQuantity) {
                double step = maxQuantity * 0.01;
                quantity[0] = Math.min(maxQuantity, quantity[0] + step);
                etQuantity.setText(formatQuantity(quantity[0]));
                tvTotal.setText(currencyFormat.format(investment.getPrice() * quantity[0]));
            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double val = Double.parseDouble(s.toString());
                    if (val > 0 && val <= maxQuantity) {
                        quantity[0] = val;
                        tvTotal.setText(currencyFormat.format(investment.getPrice() * quantity[0]));
                    }
                } catch (NumberFormatException e) {}
            }
        });

        AlertDialog dialog = builder.create();

        btnSell.setOnClickListener(v -> {
            double total = investment.getPrice() * quantity[0];

            if (quantity[0] >= maxQuantity) {
                portfolioList.remove(investment);
            } else {
                Investment updated = new Investment(
                        investment.getSymbol(),
                        investment.getName(),
                        investment.getPrice(),
                        maxQuantity - quantity[0],
                        investment.getType()
                );
                int index = portfolioList.indexOf(investment);
                portfolioList.set(index, updated);
            }

            portfolioAdapter.notifyDataSetChanged();
            updatePortfolioStats();
            updatePortfolioChart();
            updateAnalytics();

            Transaction transaction = new Transaction(
                    0,
                    sessionManager.getUserId(),
                    "Продажа " + investment.getSymbol(),
                    investment.getName() + " - " + formatQuantity(quantity[0]) + " шт.",
                    0.0,
                    total,
                    "",
                    "USD",
                    "sell",
                    "completed",
                    System.currentTimeMillis(),
                    R.drawable.ic_send
            );
            dbHelper.addTransaction(transaction);

            Toast.makeText(this, "Продано на сумму " + currencyFormat.format(total), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showBuyDialog(Security security) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_buy_security, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvPrice = dialogView.findViewById(R.id.tvPrice);
        TextView tvAvailable = dialogView.findViewById(R.id.tvAvailable);
        TextView tvTotal = dialogView.findViewById(R.id.tvTotal);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        Button btnDecrease = dialogView.findViewById(R.id.btnDecrease);
        Button btnIncrease = dialogView.findViewById(R.id.btnIncrease);
        Button btnBuy = dialogView.findViewById(R.id.btnBuy);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        TextView preset10 = dialogView.findViewById(R.id.preset10);
        TextView preset50 = dialogView.findViewById(R.id.preset50);
        TextView preset100 = dialogView.findViewById(R.id.preset100);
        TextView preset500 = dialogView.findViewById(R.id.preset500);

        tvTitle.setText(security.getName() + " (" + security.getSymbol() + ")");
        tvPrice.setText(currencyFormat.format(security.getPrice()));
        tvAvailable.setText(String.format("%,d", security.getAvailableQuantity()));

        final int[] quantity = {1};
        etQuantity.setText("1");
        updateTotal(tvTotal, security.getPrice(), quantity[0]);

        if (preset10 != null) {
            preset10.setOnClickListener(v -> {
                quantity[0] = 10;
                etQuantity.setText("10");
                updateTotal(tvTotal, security.getPrice(), quantity[0]);
            });
        }
        if (preset50 != null) {
            preset50.setOnClickListener(v -> {
                quantity[0] = 50;
                etQuantity.setText("50");
                updateTotal(tvTotal, security.getPrice(), quantity[0]);
            });
        }
        if (preset100 != null) {
            preset100.setOnClickListener(v -> {
                quantity[0] = 100;
                etQuantity.setText("100");
                updateTotal(tvTotal, security.getPrice(), quantity[0]);
            });
        }
        if (preset500 != null) {
            preset500.setOnClickListener(v -> {
                quantity[0] = 500;
                etQuantity.setText("500");
                updateTotal(tvTotal, security.getPrice(), quantity[0]);
            });
        }

        btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                etQuantity.setText(String.valueOf(quantity[0]));
                updateTotal(tvTotal, security.getPrice(), quantity[0]);
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity[0] < security.getAvailableQuantity()) {
                quantity[0]++;
                etQuantity.setText(String.valueOf(quantity[0]));
                updateTotal(tvTotal, security.getPrice(), quantity[0]);
            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int val = Integer.parseInt(s.toString());
                    if (val > 0 && val <= security.getAvailableQuantity()) {
                        quantity[0] = val;
                        updateTotal(tvTotal, security.getPrice(), quantity[0]);
                    }
                } catch (NumberFormatException e) {}
            }
        });

        AlertDialog dialog = builder.create();

        btnBuy.setOnClickListener(v -> {
            double total = security.getPrice() * quantity[0];

            int newOwnedQuantity = security.getOwnedQuantity() + quantity[0];
            security.setOwnedQuantity(newOwnedQuantity);

            if (security.getType().equals("stock") && stocksAdapter != null) {
                stocksAdapter.updateSecurityOwnership(security);
            } else if (security.getType().equals("bond") && bondsAdapter != null) {
                bondsAdapter.updateSecurityOwnership(security);
            }

            Investment investment = new Investment(
                    security.getSymbol(),
                    security.getName(),
                    security.getPrice(),
                    quantity[0],
                    security.getType()
            );

            portfolioList.add(investment);
            portfolioAdapter.notifyDataSetChanged();
            updatePortfolioStats();
            updatePortfolioChart();
            updateAnalytics();

            saveInvestmentToHistory(investment);

            String message = String.format("Куплено %d шт. %s на сумму %s",
                    quantity[0], security.getSymbol(), currencyFormat.format(total));
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateTotal(TextView tvTotal, double price, int quantity) {
        double total = price * quantity;
        tvTotal.setText(currencyFormat.format(total));
    }

    private void saveInvestmentToHistory(Investment investment) {
        if (sessionManager.getUserId() == -1) return;

        Transaction transaction = new Transaction(
                0,
                sessionManager.getUserId(),
                "Покупка " + investment.getSymbol(),
                investment.getName() + " - " + formatQuantity(investment.getQuantity()) + " шт.",
                investment.getTotalValue(),
                0.0,
                "USD",
                "",
                "investment",
                "completed",
                System.currentTimeMillis(),
                R.drawable.ic_invest
        );

        dbHelper.addTransaction(transaction);
    }

    private void startNewsStreaming() {
        newsStreamService.addListener(news -> {
            runOnUiThread(() -> {
                newsList.add(0, news);
                if (newsList.size() > 20) {
                    newsList.remove(newsList.size() - 1);
                }
                if (newsAdapter != null) {
                    newsAdapter.notifyItemInserted(0);
                }
                if (rvNews != null) {
                    rvNews.smoothScrollToPosition(0);
                }
                updateMarketData();
            });
        });
        newsStreamService.startStreaming();
    }

    private void updateMarketData() {
        double spChange = 1.24 + (random.nextDouble() * 2 - 1);
        double nasdaqChange = 0.87 + (random.nextDouble() * 2 - 1);
        double vixChange = -2.1 + (random.nextDouble() * 4 - 2);

        if (tvSPChange != null) {
            tvSPChange.setText(String.format("%+.2f%%", spChange));
            tvSPChange.setTextColor(spChange >= 0 ?
                    getColor(R.color.success) : getColor(R.color.error));
        }
        if (tvNasdaqChange != null) {
            tvNasdaqChange.setText(String.format("%+.2f%%", nasdaqChange));
            tvNasdaqChange.setTextColor(nasdaqChange >= 0 ?
                    getColor(R.color.success) : getColor(R.color.error));
        }
        if (tvVixChange != null) {
            tvVixChange.setText(String.format("%+.2f%%", vixChange));
            tvVixChange.setTextColor(vixChange <= 0 ?
                    getColor(R.color.success) : getColor(R.color.error));
        }
    }

    private String formatQuantity(double quantity) {
        if (quantity >= 1000) {
            return String.format("%.1fK", quantity / 1000.0);
        } else if (quantity >= 1) {
            return String.format("%.0f", quantity);
        } else {
            return String.format("%.4f", quantity);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsStreamService != null) {
            newsStreamService.stopStreaming();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (portfolioAnimator != null) {
            portfolioAnimator.cancel();
        }
    }
}