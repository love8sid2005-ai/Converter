package com.example.currencyconverter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.adapters.MarketCurrencyAdapter;
import com.example.currencyconverter.models.MarketCurrency;
import com.example.currencyconverter.services.MarketDataService;
import com.example.currencyconverter.views.AmountEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConverterActivity extends BaseActivity {

    private AmountEditText amountEditText;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private Button convertButton;
    private ImageButton swapButton;
    private TextView resultTextView;
    private TextView resultPreview;
    private Button refreshButton;
    private RecyclerView marketRecyclerView;
    private ImageView ivClosePreview;

    private MarketCurrencyAdapter marketAdapter;
    private MarketDataService marketDataService;
    private Handler handler = new Handler();
    private Runnable marketUpdateRunnable;

    private String[] currencyCodes = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "RUB"};
    private String[] currencyNames = {"Доллар США", "Евро", "Британский фунт", "Японская иена",
            "Канадский доллар", "Австралийский доллар", "Швейцарский франк",
            "Китайский юань", "Российский рубль"};

    private double[] currencyRates = {1.0, 0.92, 0.79, 148.3, 1.35, 1.52, 0.88, 7.2, 90.5};
    private NumberFormat numberFormat;
    private ValueAnimator resultAnimator;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_converter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marketDataService = new MarketDataService();
        numberFormat = NumberFormat.getInstance(Locale.US);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        initializeViews();
        setupSpinners();
        setupRecyclerView();
        setupClickListeners();

        loadMarketData();
        startMarketUpdates();
        updatePreview();
    }

    private void initializeViews() {
        amountEditText = findViewById(R.id.amountEditText);
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner);
        convertButton = findViewById(R.id.convertButton);
        swapButton = findViewById(R.id.swapButton);
        resultTextView = findViewById(R.id.resultTextView);
        resultPreview = findViewById(R.id.resultPreview);
        refreshButton = findViewById(R.id.refreshButton);
        marketRecyclerView = findViewById(R.id.marketRecyclerView);
        ivClosePreview = findViewById(R.id.ivClosePreview);
    }

    private void setupSpinners() {
        // Создаем кастомный адаптер для спиннеров
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item_premium, currencyCodes);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_premium);

        fromCurrencySpinner.setAdapter(spinnerAdapter);
        toCurrencySpinner.setAdapter(spinnerAdapter);

        fromCurrencySpinner.setSelection(0); // USD
        toCurrencySpinner.setSelection(8);   // RUB

        // Анимация при выборе валюты
        fromCurrencySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updatePreview();
                animateSpinnerSelection(view);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        toCurrencySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updatePreview();
                animateSpinnerSelection(view);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void animateSpinnerSelection(View view) {
        view.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(150)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                .start();
    }

    private void setupRecyclerView() {
        marketAdapter = new MarketCurrencyAdapter(new ArrayList<>());
        marketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        marketRecyclerView.setAdapter(marketAdapter);
    }

    private void setupClickListeners() {
        convertButton.setOnClickListener(v -> {
            animateButton(v);
            convertCurrency();
        });

        swapButton.setOnClickListener(v -> {
            animateButton(v);
            swapCurrencies();
        });

        refreshButton.setOnClickListener(v -> {
            animateButton(v);
            loadMarketData();
            Toast.makeText(this, "Рыночные данные обновлены", Toast.LENGTH_SHORT).show();
        });

        if (ivClosePreview != null) {
            ivClosePreview.setOnClickListener(v -> {
                resultTextView.setVisibility(View.GONE);
                animateViewFadeOut(resultTextView);
            });
        }

        amountEditText.setOnAmountChangeListener(amount -> updatePreview());
    }

    private void animateButton(View button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> button.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void animateViewFadeOut(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> view.setVisibility(View.GONE))
                .start();
    }

    private void animateResult(String resultText) {
        if (resultAnimator != null) {
            resultAnimator.cancel();
        }

        resultTextView.setAlpha(0f);
        resultTextView.setVisibility(View.VISIBLE);
        resultTextView.setText(resultText);

        resultTextView.animate()
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void swapCurrencies() {
        int fromPosition = fromCurrencySpinner.getSelectedItemPosition();
        int toPosition = toCurrencySpinner.getSelectedItemPosition();

        fromCurrencySpinner.setSelection(toPosition);
        toCurrencySpinner.setSelection(fromPosition);

        updatePreview();

        // Вибрация при свапе
        try {
            android.os.Vibrator vibrator = (android.os.Vibrator) getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(50);
            }
        } catch (Exception e) {}
    }

    private void updatePreview() {
        try {
            double amount = amountEditText.getAmount();
            if (amount == 0) {
                resultPreview.setText("0.00");
                return;
            }

            String fromCurrency = (String) fromCurrencySpinner.getSelectedItem();
            String toCurrency = (String) toCurrencySpinner.getSelectedItem();

            double result = convertCurrencyAmount(amount, fromCurrency, toCurrency);
            resultPreview.setText(numberFormat.format(result));
        } catch (Exception e) {
            resultPreview.setText("0.00");
        }
    }

    private void convertCurrency() {
        try {
            double amount = amountEditText.getAmount();
            if (amount <= 0) {
                Toast.makeText(this, "Введите сумму больше 0", Toast.LENGTH_SHORT).show();
                return;
            }

            String fromCurrency = (String) fromCurrencySpinner.getSelectedItem();
            String toCurrency = (String) toCurrencySpinner.getSelectedItem();

            double result = convertCurrencyAmount(amount, fromCurrency, toCurrency);

            String resultText = String.format(Locale.US, "%s %s = %s %s",
                    numberFormat.format(amount), fromCurrency,
                    numberFormat.format(result), toCurrency);

            animateResult(resultText);

            // Анимация превью
            resultPreview.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(200)
                    .withEndAction(() -> resultPreview.animate().scaleX(1f).scaleY(1f).setDuration(200).start())
                    .start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверная сумма", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertCurrencyAmount(double amount, String fromCurrency, String toCurrency) {
        double fromRate = getRateForCurrency(fromCurrency);
        double toRate = getRateForCurrency(toCurrency);

        if (fromRate == 0 || toRate == 0) return 0;

        double amountInUSD = amount / fromRate;
        return amountInUSD * toRate;
    }

    private double getRateForCurrency(String currencyCode) {
        for (int i = 0; i < currencyCodes.length; i++) {
            if (currencyCodes[i].equals(currencyCode)) {
                return currencyRates[i];
            }
        }
        return 0;
    }

    private void loadMarketData() {
        List<MarketCurrency> marketData = marketDataService.getMarketData();
        marketAdapter.updateData(marketData);
    }

    private void startMarketUpdates() {
        marketUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                loadMarketData();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(marketUpdateRunnable, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && marketUpdateRunnable != null) {
            handler.removeCallbacks(marketUpdateRunnable);
        }
    }
}