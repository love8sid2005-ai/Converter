package com.example.currencyconverter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.adapters.WalletCurrencyAdapter;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.Transaction;
import com.example.currencyconverter.models.User;
import com.example.currencyconverter.models.WalletCurrency;
import com.example.currencyconverter.utils.SessionManager;
import com.example.currencyconverter.views.SparklineChartView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WalletActivity extends BaseActivity {

    // UI Elements
    private TextView totalBalanceTextView;
    private TextView totalInRUBTextView;
    private TextView mainAccountBalanceTextView;
    private TextView savingsAccountBalanceTextView;
    private TextView cryptoBalanceTextView;
    private TextView monthlyIncomeTextView;
    private TextView monthlyExpenseTextView;
    private Button addMoneyButton;
    private Button withdrawButton;
    private LinearLayout exchangeButton;
    private LinearLayout historyButton;
    private LinearLayout cardButton;
    private LinearLayout statsButton;
    private LinearLayout transferButton;
    private LinearLayout aiSupportButton;
    private RecyclerView walletCurrenciesRecyclerView;
    private SparklineChartView balanceChart;
    private CardView mainCard, savingsCard, cryptoCard;
    private ImageView ivAiAvatar;

    // Data
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;
    private WalletCurrencyAdapter walletCurrencyAdapter;
    private Random random = new Random();
    private Handler handler = new Handler();

    private double mainAccountBalance = 8450.32;
    private double savingsAccountBalance = 4093.35;
    private double cryptoBalance = 3240.00;
    private double monthlyIncome = 3420.50;
    private double monthlyExpense = 2150.75;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        int userId = sessionManager.getUserId();
        if (userId != -1) {
            currentUser = dbHelper.getUserById(userId);
            loadAccountBalances();
        }

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadWalletData();
        updateAllBalances();
        startBalanceAnimation();
        setupAIAssistant();
    }

    private void loadAccountBalances() {
        if (currentUser != null) {
            mainAccountBalance = currentUser.getBalanceUSD() * 0.7;
            savingsAccountBalance = currentUser.getBalanceUSD() * 0.3;
        }
    }

    private void initializeViews() {
        totalBalanceTextView = findViewById(R.id.totalBalanceTextView);
        totalInRUBTextView = findViewById(R.id.totalInRUBTextView);
        addMoneyButton = findViewById(R.id.addMoneyButton);
        withdrawButton = findViewById(R.id.withdrawButton);

        exchangeButton = findViewById(R.id.exchangeButton);
        historyButton = findViewById(R.id.historyButton);
        cardButton = findViewById(R.id.cardButton);
        statsButton = findViewById(R.id.statsButton);
        transferButton = findViewById(R.id.transferButton);
        aiSupportButton = findViewById(R.id.aiSupportButton);

        walletCurrenciesRecyclerView = findViewById(R.id.walletCurrenciesRecyclerView);

        mainAccountBalanceTextView = findViewById(R.id.mainAccountBalance);
        savingsAccountBalanceTextView = findViewById(R.id.savingsAccountBalance);
        cryptoBalanceTextView = findViewById(R.id.cryptoBalanceTextView);
        monthlyIncomeTextView = findViewById(R.id.monthlyIncomeTextView);
        monthlyExpenseTextView = findViewById(R.id.monthlyExpenseTextView);
        balanceChart = findViewById(R.id.balanceChart);

        mainCard = findViewById(R.id.mainCard);
        savingsCard = findViewById(R.id.savingsCard);
        cryptoCard = findViewById(R.id.cryptoCard);
        ivAiAvatar = findViewById(R.id.ivAiAvatar);

        TextView pageTitle = findViewById(R.id.pageTitle);
        if (pageTitle != null) {
            pageTitle.setText("Мой кошелек");
        }

        // Установка графика баланса
        float[] chartData = {12000, 11800, 11900, 12100, 12050, 12200, 12450};
        balanceChart.setChartData(chartData, true, ContextCompat.getColor(this, R.color.accent));
    }

    private void setupRecyclerView() {
        walletCurrencyAdapter = new WalletCurrencyAdapter();
        walletCurrenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        walletCurrenciesRecyclerView.setAdapter(walletCurrencyAdapter);
    }

    private void setupClickListeners() {
        // Основные кнопки
        addMoneyButton.setOnClickListener(v -> {
            animateButton(v);
            showAmountDialog("Пополнить", true);
        });

        withdrawButton.setOnClickListener(v -> {
            animateButton(v);
            showAmountDialog("Вывести", false);
        });

        // Премиум кнопки
        if (exchangeButton != null) {
            exchangeButton.setOnClickListener(v -> {
                animateButton(v);
                showExchangeOptions();
            });
        }

        if (historyButton != null) {
            historyButton.setOnClickListener(v -> {
                animateButton(v);
                startActivity(new Intent(this, TransactionsHistoryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardButton != null) {
            cardButton.setOnClickListener(v -> {
                animateButton(v);
                showCardManagement();
            });
        }

        if (statsButton != null) {
            statsButton.setOnClickListener(v -> {
                animateButton(v);
                showStatistics();
            });
        }

        if (transferButton != null) {
            transferButton.setOnClickListener(v -> {
                animateButton(v);
                showTransferDialog();
            });
        }

        // AI Ассистент
        if (aiSupportButton != null) {
            aiSupportButton.setOnClickListener(v -> {
                animateButton(v);
                openAISupport();
            });
        }

        // Карточки счетов
        if (mainCard != null) {
            mainCard.setOnClickListener(v -> {
                animateCard(v);
                showAccountDetail("Основной счет", mainAccountBalance, "USD");
            });
        }

        if (savingsCard != null) {
            savingsCard.setOnClickListener(v -> {
                animateCard(v);
                showAccountDetail("Сберегательный счет", savingsAccountBalance, "EUR");
            });
        }

        if (cryptoCard != null) {
            cryptoCard.setOnClickListener(v -> {
                animateCard(v);
                showAccountDetail("Крипто-кошелек", cryptoBalance, "BTC/ETH");
            });
        }
    }

    private void setupAIAssistant() {
        if (ivAiAvatar != null) {
            // Анимация пульсации для AI аватара
            ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1f, 1.1f, 1f);
            pulseAnimator.setDuration(2000);
            pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
            pulseAnimator.addUpdateListener(animation -> {
                float scale = (float) animation.getAnimatedValue();
                ivAiAvatar.setScaleX(scale);
                ivAiAvatar.setScaleY(scale);
            });
            pulseAnimator.start();
        }
    }

    private void openAISupport() {
        Intent intent = new Intent(this, AIAssistantActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void animateCard(View view) {
        view.animate()
                .scaleX(0.98f)
                .scaleY(0.98f)
                .setDuration(150)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                .start();
    }

    private void showAccountDetail(String accountName, double balance, String currency) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        Toast.makeText(this,
                String.format("%s\n%s %s\nДоступно для операций",
                        accountName, formatter.format(balance), currency),
                Toast.LENGTH_LONG).show();
    }

    private void startBalanceAnimation() {
        handler.postDelayed(() -> {
            float[] newData = {12000, 11900, 12050, 12100, 12250, 12300, 12480};
            balanceChart.setChartData(newData, true, ContextCompat.getColor(this, R.color.accent));
            handler.postDelayed(this::startBalanceAnimation, 30000);
        }, 30000);
    }

    private void animateButton(View v) {
        v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void showExchangeOptions() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exchange, null);
        builder.setView(dialogView);

        LinearLayout quickExchange = dialogView.findViewById(R.id.quickExchange);
        LinearLayout limitExchange = dialogView.findViewById(R.id.limitExchange);
        LinearLayout bestRate = dialogView.findViewById(R.id.bestRate);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        android.app.AlertDialog dialog = builder.create();

        quickExchange.setOnClickListener(v -> {
            animateButton(v);
            Toast.makeText(this, "Быстрый обмен по рыночному курсу", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ConverterActivity.class));
            dialog.dismiss();
        });

        limitExchange.setOnClickListener(v -> {
            animateButton(v);
            showLimitOrderDialog();
            dialog.dismiss();
        });

        bestRate.setOnClickListener(v -> {
            animateButton(v);
            showBestRateDialog();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showBestRateDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_best_rate, null);
        builder.setView(dialogView);

        Button btnClose = dialogView.findViewById(R.id.btnClose);
        Button btnActivate = dialogView.findViewById(R.id.btnActivate);

        // Анимация появления
        dialogView.setAlpha(0f);
        dialogView.setScaleX(0.95f);
        dialogView.setScaleY(0.95f);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Анимация
        dialogView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        btnClose.setOnClickListener(v -> {
            dialogView.animate()
                    .alpha(0f)
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(200)
                    .withEndAction(dialog::dismiss)
                    .start();
        });

        btnActivate.setOnClickListener(v -> {
            Toast.makeText(this, "Premium активирован на 7 дней бесплатно!", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });
    }

    private void showLimitOrderDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_limit_order, null);
        builder.setView(dialogView);

        android.widget.EditText etFromAmount = dialogView.findViewById(R.id.etFromAmount);
        android.widget.EditText etTargetRate = dialogView.findViewById(R.id.etTargetRate);
        Button btnCreate = dialogView.findViewById(R.id.btnCreate);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        android.app.AlertDialog dialog = builder.create();

        btnCreate.setOnClickListener(v -> {
            String amount = etFromAmount.getText().toString();
            String rate = etTargetRate.getText().toString();

            if (amount.isEmpty() || rate.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Лимитный ордер создан! Уведомим при достижении курса " + rate, Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showCardManagement() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cards, null);
        builder.setView(dialogView);

        LinearLayout addCard = dialogView.findViewById(R.id.addCard);
        LinearLayout cardSettings = dialogView.findViewById(R.id.cardSettings);
        LinearLayout cardLimits = dialogView.findViewById(R.id.cardLimits);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        android.app.AlertDialog dialog = builder.create();

        addCard.setOnClickListener(v -> {
            animateButton(v);
            showAddCardDialog();
            dialog.dismiss();
        });

        cardSettings.setOnClickListener(v -> {
            animateButton(v);
            Toast.makeText(this, "Настройки карты", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        cardLimits.setOnClickListener(v -> {
            animateButton(v);
            showCardLimitsDialog();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showAddCardDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_card, null);
        builder.setView(dialogView);

        android.widget.EditText etCardNumber = dialogView.findViewById(R.id.etCardNumber);
        android.widget.EditText etCardHolder = dialogView.findViewById(R.id.etCardHolder);
        android.widget.EditText etExpiryDate = dialogView.findViewById(R.id.etExpiryDate);
        android.widget.EditText etCvv = dialogView.findViewById(R.id.etCvv);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        android.app.AlertDialog dialog = builder.create();

        btnAdd.setOnClickListener(v -> {
            String cardNumber = etCardNumber.getText().toString();
            if (cardNumber.length() < 16) {
                Toast.makeText(this, "Введите корректный номер карты", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Карта успешно добавлена", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showCardLimitsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        builder.setTitle("Лимиты карты")
                .setMessage("Суточный лимит: $5,000\nМесячный лимит: $50,000\nЛимит на снятие: $1,000\n\nИзменить лимиты можно в приложении банка")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showStatistics() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_statistics, null);
        builder.setView(dialogView);

        Button btnClose = dialogView.findViewById(R.id.btnClose);

        android.app.AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showTransferDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_transfer, null);
        builder.setView(dialogView);

        android.widget.EditText etAmount = dialogView.findViewById(R.id.etAmount);
        android.widget.EditText etRecipient = dialogView.findViewById(R.id.etRecipient);
        Button btnTransfer = dialogView.findViewById(R.id.btnTransfer);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        android.app.AlertDialog dialog = builder.create();

        btnTransfer.setOnClickListener(v -> {
            String amount = etAmount.getText().toString();
            String recipient = etRecipient.getText().toString();

            if (amount.isEmpty() || recipient.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double amountValue;
            try {
                amountValue = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Неверная сумма", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amountValue > mainAccountBalance) {
                Toast.makeText(this, "Недостаточно средств", Toast.LENGTH_SHORT).show();
                return;
            }

            processTransfer(amountValue, recipient);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void processTransfer(double amount, String recipient) {
        mainAccountBalance -= amount;
        updateAllBalances();

        Transaction transaction = new Transaction(
                0,
                currentUser.getId(),
                "Перевод " + recipient,
                "Сумма: $" + amount,
                amount,
                0.0,
                "USD",
                "",
                "transfer",
                "completed",
                System.currentTimeMillis(),
                R.drawable.ic_send
        );
        dbHelper.addTransaction(transaction);

        Toast.makeText(this, String.format("Перевод %.2f USD выполнен", amount), Toast.LENGTH_LONG).show();
    }

    private void showAmountDialog(String title, boolean isDeposit) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_amount, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        android.widget.EditText etAmount = dialogView.findViewById(R.id.etAmount);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        tvTitle.setText(title);

        android.app.AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Неверный формат суммы", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "Сумма должна быть больше 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isDeposit) {
                processDeposit(amount);
            } else {
                processWithdrawal(amount);
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void processDeposit(double amount) {
        double newBalance = currentUser.getBalanceUSD() + amount;
        currentUser.setBalanceUSD(newBalance);
        dbHelper.updateUserBalance(currentUser.getId(), newBalance);

        double mainAmount = amount * 0.7;
        double savingsAmount = amount * 0.3;

        mainAccountBalance += mainAmount;
        savingsAccountBalance += savingsAmount;

        Transaction transaction = new Transaction(
                0,
                currentUser.getId(),
                "Пополнение кошелька",
                String.format("Основной: $%.2f, Сберегательный: $%.2f", mainAmount, savingsAmount),
                0.0,
                amount,
                "",
                "USD",
                "deposit",
                "completed",
                System.currentTimeMillis(),
                R.drawable.ic_topup
        );

        dbHelper.addTransaction(transaction);

        animateBalanceChange(totalBalanceTextView, currentUser.getBalanceUSD() - amount, currentUser.getBalanceUSD());
        updateAllBalances();
        Toast.makeText(this, "Счет пополнен на $" + amount, Toast.LENGTH_SHORT).show();
    }

    private void animateBalanceChange(TextView textView, double from, double to) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        ValueAnimator animator = ValueAnimator.ofFloat((float) from, (float) to);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(formatter.format(value));
        });
        animator.start();
    }

    private void processWithdrawal(double amount) {
        if (currentUser.getBalanceUSD() >= amount) {
            if (mainAccountBalance >= amount) {
                mainAccountBalance -= amount;
            } else {
                double remaining = amount - mainAccountBalance;
                mainAccountBalance = 0;
                savingsAccountBalance -= remaining;
                cryptoBalance -= remaining * 0.3;
                if (cryptoBalance < 0) cryptoBalance = 0;
            }

            double newBalance = currentUser.getBalanceUSD() - amount;
            currentUser.setBalanceUSD(newBalance);
            dbHelper.updateUserBalance(currentUser.getId(), newBalance);

            Transaction transaction = new Transaction(
                    0,
                    currentUser.getId(),
                    "Вывод средств",
                    "Снятие на карту",
                    amount,
                    0.0,
                    "USD",
                    "",
                    "withdrawal",
                    "completed",
                    System.currentTimeMillis(),
                    R.drawable.ic_send
            );

            dbHelper.addTransaction(transaction);

            animateBalanceChange(totalBalanceTextView, currentUser.getBalanceUSD() + amount, currentUser.getBalanceUSD());
            updateAllBalances();
            Toast.makeText(this, "Выведено $" + amount, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Недостаточно средств", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAllBalances() {
        NumberFormat usdFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        double totalBalance = currentUser != null ? currentUser.getBalanceUSD() : mainAccountBalance + savingsAccountBalance + cryptoBalance;

        if (currentUser != null) {
            totalBalance = currentUser.getBalanceUSD();
        }

        totalBalanceTextView.setText(usdFormatter.format(totalBalance));

        if (mainAccountBalanceTextView != null) {
            mainAccountBalanceTextView.setText(usdFormatter.format(mainAccountBalance));
        }
        if (savingsAccountBalanceTextView != null) {
            savingsAccountBalanceTextView.setText(usdFormatter.format(savingsAccountBalance));
        }
        if (cryptoBalanceTextView != null) {
            cryptoBalanceTextView.setText(usdFormatter.format(cryptoBalance));
        }

        NumberFormat rubFormatter = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        double totalRUB = totalBalance * 90.5;
        totalInRUBTextView.setText("≈ " + rubFormatter.format(totalRUB));

        // Обновляем ежемесячную статистику
        monthlyIncomeTextView.setText(usdFormatter.format(monthlyIncome));
        monthlyExpenseTextView.setText(usdFormatter.format(monthlyExpense));
    }

    private void loadWalletData() {
        List<WalletCurrency> walletCurrencies = new ArrayList<>();

        // Основные валюты с актуальными данными
        walletCurrencies.add(new WalletCurrency("USD", "Доллар США", mainAccountBalance, 1.0, R.drawable.ic_dollar));
        walletCurrencies.add(new WalletCurrency("EUR", "Евро", savingsAccountBalance / 1.08, 0.92, R.drawable.ic_euro));
        walletCurrencies.add(new WalletCurrency("RUB", "Российский рубль", mainAccountBalance * 90.5, 90.5, R.drawable.ic_ruble));
        walletCurrencies.add(new WalletCurrency("GBP", "Британский фунт", mainAccountBalance * 0.79, 0.79, R.drawable.ic_pound));
        walletCurrencies.add(new WalletCurrency("JPY", "Японская иена", mainAccountBalance * 148.3, 148.3, R.drawable.ic_yen));
        walletCurrencies.add(new WalletCurrency("CNY", "Китайский юань", mainAccountBalance * 7.2, 7.2, R.drawable.ic_yuan));

        // Криптовалюты
        walletCurrencies.add(new WalletCurrency("BTC", "Bitcoin", cryptoBalance / 65000, 65000, R.drawable.ic_bitcoin));
        walletCurrencies.add(new WalletCurrency("ETH", "Ethereum", cryptoBalance / 3000, 3000, R.drawable.ic_ethereum));

        walletCurrencyAdapter.updateData(walletCurrencies);

        // Устанавливаем слушатель нажатия на кнопку обмена
        walletCurrencyAdapter.setOnCurrencyActionListener(currency -> {
            Toast.makeText(this, "Обмен " + currency.getCode(), Toast.LENGTH_SHORT).show();
            showExchangeOptions();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}