package com.example.currencyconverter;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.currencyconverter.adapters.CurrencyAdapter;
import com.example.currencyconverter.adapters.MarketOverviewAdapter;
import com.example.currencyconverter.models.Currency;
import com.example.currencyconverter.models.MarketIndicator;
import com.example.currencyconverter.services.CurrencyService;
import com.example.currencyconverter.views.SparklineChartView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RatesActivity extends BaseActivity {

    private RecyclerView currencyRecyclerView;
    private RecyclerView marketOverviewRecyclerView;
    private CurrencyAdapter currencyAdapter;
    private MarketOverviewAdapter marketOverviewAdapter;
    private TextView lastUpdateTextView;
    private TextView tvMarketTrend, tvMarketVolume, tvMarketDominance;
    private TextView tvFearGreedIndex, tvFearGreedValue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button brokerButton;
    private ImageView ivRefresh;
    private SparklineChartView chartGlobalMarket;
    private CurrencyService currencyService;
    private Handler handler = new Handler();
    private Random random = new Random();

    private List<MarketIndicator> marketIndicators;
    private float[] globalMarketData = {32000, 31800, 32200, 32100, 32500, 32800, 33100};

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_rates;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
        setupRecyclerView();
        setupMarketOverview();
        setupRefreshLayout();
        setupBrokerButton();
        setupClickListeners();

        currencyService = new CurrencyService();
        loadCurrencyData();
        updateLastUpdateTime();
        startMarketUpdates();
        animateGlobalChart();
    }

    private void initializeViews() {
        currencyRecyclerView = findViewById(R.id.currencyRecyclerView);
        marketOverviewRecyclerView = findViewById(R.id.marketOverviewRecyclerView);
        lastUpdateTextView = findViewById(R.id.lastUpdateTextView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        brokerButton = findViewById(R.id.brokerButton);
        ivRefresh = findViewById(R.id.ivRefresh);
        tvMarketTrend = findViewById(R.id.tvMarketTrend);
        tvMarketVolume = findViewById(R.id.tvMarketVolume);
        tvMarketDominance = findViewById(R.id.tvMarketDominance);
        tvFearGreedIndex = findViewById(R.id.tvFearGreedIndex);
        tvFearGreedValue = findViewById(R.id.tvFearGreedValue);
        chartGlobalMarket = findViewById(R.id.chartGlobalMarket);

        TextView pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Рынок");
    }

    private void setupRecyclerView() {
        currencyAdapter = new CurrencyAdapter();
        currencyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        currencyRecyclerView.setAdapter(currencyAdapter);
    }

    private void setupMarketOverview() {
        marketIndicators = new ArrayList<>();
        marketIndicators.add(new MarketIndicator("S&P 500", "5,234.45", "+1.24%", true, R.drawable.ic_stocks));
        marketIndicators.add(new MarketIndicator("NASDAQ", "16,789.32", "+0.87%", true, R.drawable.ic_graph));
        marketIndicators.add(new MarketIndicator("Dow Jones", "37,654.21", "-0.32%", false, R.drawable.ic_stocks));
        marketIndicators.add(new MarketIndicator("Bitcoin", "65,432.89", "+2.34%", true, R.drawable.ic_bitcoin));
        marketIndicators.add(new MarketIndicator("Ethereum", "3,456.78", "-0.56%", false, R.drawable.ic_ethereum));
        marketIndicators.add(new MarketIndicator("Gold", "2,034.56", "+0.45%", true, R.drawable.ic_gold));

        marketOverviewAdapter = new MarketOverviewAdapter(marketIndicators);
        marketOverviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        marketOverviewRecyclerView.setAdapter(marketOverviewAdapter);

        updateMarketStats();
    }

    private void updateMarketStats() {
        tvMarketTrend.setText("+1.24%");
        tvMarketTrend.setTextColor(getColor(R.color.success));

        tvMarketVolume.setText("$42.3B");

        tvMarketDominance.setText("BTC: 48.2%");

        updateFearGreedIndex();
    }

    private void updateFearGreedIndex() {
        int fearGreedValue = 65 + random.nextInt(20);
        tvFearGreedValue.setText(String.valueOf(fearGreedValue));

        if (fearGreedValue >= 75) {
            tvFearGreedIndex.setText("Жадность");
            tvFearGreedIndex.setTextColor(getColor(R.color.success));
            tvFearGreedValue.setTextColor(getColor(R.color.success));
        } else if (fearGreedValue >= 45) {
            tvFearGreedIndex.setText("Нейтрально");
            tvFearGreedIndex.setTextColor(getColor(R.color.warning));
            tvFearGreedValue.setTextColor(getColor(R.color.warning));
        } else {
            tvFearGreedIndex.setText("Страх");
            tvFearGreedIndex.setTextColor(getColor(R.color.error));
            tvFearGreedValue.setTextColor(getColor(R.color.error));
        }
    }

    private void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadCurrencyData();
            updateLastUpdateTime();
            updateMarketStats();
            animateRefresh();
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.accent);
    }

    private void animateRefresh() {
        ivRefresh.animate()
                .rotationBy(360)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateGlobalChart() {
        int chartColor = getColor(R.color.accent);
        chartGlobalMarket.setChartData(globalMarketData, true, chartColor);
    }

    private void startMarketUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateMarketStats();
                animateNumberChange();
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    private void animateNumberChange() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 1.05f);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            tvMarketTrend.setScaleX(scale);
            tvMarketTrend.setScaleY(scale);
        });
        animator.start();
    }

    private void setupBrokerButton() {
        if (brokerButton != null) {
            brokerButton.setOnClickListener(v -> {
                animateButton(v);
                startActivity(new Intent(RatesActivity.this, BrokerActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void setupClickListeners() {
        if (ivRefresh != null) {
            ivRefresh.setOnClickListener(v -> {
                animateRefresh();
                loadCurrencyData();
                updateLastUpdateTime();
                updateMarketStats();
                Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void animateButton(View button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> button.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void loadCurrencyData() {
        List<Currency> currencies = currencyService.getCurrencyRates();
        currencyAdapter.updateData(currencies);
    }

    private void updateLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        lastUpdateTextView.setText("Обновлено в " + currentTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}