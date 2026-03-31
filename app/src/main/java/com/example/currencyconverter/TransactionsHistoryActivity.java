package com.example.currencyconverter;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.adapters.TransactionAdapter;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.Transaction;
import com.example.currencyconverter.models.User;
import com.example.currencyconverter.utils.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionsHistoryActivity extends BaseActivity {

    private RecyclerView transactionsRecyclerView;
    private TextView emptyView, tvTotalTransactions, tvTotalVolume, tvFilterActive;
    private ImageView ivBack, ivFilter, ivSearch;
    private LinearLayout filterAll, filterDeposit, filterWithdrawal, filterTransfer, filterInvestment;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private List<Transaction> filteredTransactions;
    private String currentFilter = "all";

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_transactions_history;
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
        setupClickListeners();
        loadTransactions();
        updateStatistics();
    }

    private void initializeViews() {
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        tvTotalVolume = findViewById(R.id.tvTotalVolume);
        tvFilterActive = findViewById(R.id.tvFilterActive);
        ivBack = findViewById(R.id.ivBack);
        ivFilter = findViewById(R.id.ivFilter);
        ivSearch = findViewById(R.id.ivSearch);

        // Исправлено: эти элементы - LinearLayout, а не TextView
        filterAll = findViewById(R.id.filterAll);
        filterDeposit = findViewById(R.id.filterDeposit);
        filterWithdrawal = findViewById(R.id.filterWithdrawal);
        filterTransfer = findViewById(R.id.filterTransfer);
        filterInvestment = findViewById(R.id.filterInvestment);

        TextView title = findViewById(R.id.pageTitle);
        if (title != null) {
            title.setText("История операций");
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ivFilter.setOnClickListener(v -> showFilterDialog());
        ivSearch.setOnClickListener(v -> showSearchDialog());

        filterAll.setOnClickListener(v -> applyFilter("all"));
        filterDeposit.setOnClickListener(v -> applyFilter("deposit"));
        filterWithdrawal.setOnClickListener(v -> applyFilter("withdrawal"));
        filterTransfer.setOnClickListener(v -> applyFilter("transfer"));
        filterInvestment.setOnClickListener(v -> applyFilter("investment"));
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        filteredTransactions.clear();

        for (Transaction t : allTransactions) {
            switch (filter) {
                case "deposit":
                    if (t.getType().equals("deposit")) filteredTransactions.add(t);
                    break;
                case "withdrawal":
                    if (t.getType().equals("withdrawal")) filteredTransactions.add(t);
                    break;
                case "transfer":
                    if (t.getType().equals("transfer")) filteredTransactions.add(t);
                    break;
                case "investment":
                    if (t.getType().equals("investment") || t.getType().equals("sell")) filteredTransactions.add(t);
                    break;
                default:
                    filteredTransactions.add(t);
                    break;
            }
        }

        updateFilterUI();
        updateAdapter();
        animateFilterChange();
    }

    private void updateFilterUI() {
        // Сбрасываем все фильтры
        resetFilterStyle(filterAll);
        resetFilterStyle(filterDeposit);
        resetFilterStyle(filterWithdrawal);
        resetFilterStyle(filterTransfer);
        resetFilterStyle(filterInvestment);

        // Активируем выбранный фильтр
        switch (currentFilter) {
            case "deposit":
                setActiveFilterStyle(filterDeposit, "Пополнения");
                break;
            case "withdrawal":
                setActiveFilterStyle(filterWithdrawal, "Выводы");
                break;
            case "transfer":
                setActiveFilterStyle(filterTransfer, "Переводы");
                break;
            case "investment":
                setActiveFilterStyle(filterInvestment, "Инвестиции");
                break;
            default:
                setActiveFilterStyle(filterAll, "Все операции");
                break;
        }
    }

    private void resetFilterStyle(LinearLayout filter) {
        if (filter != null) {
            filter.setBackgroundResource(R.drawable.filter_chip_inactive);
            // Получаем TextView из LinearLayout
            if (filter.getChildCount() > 0 && filter.getChildAt(0) instanceof TextView) {
                TextView textView = (TextView) filter.getChildAt(0);
                textView.setTextColor(getColor(R.color.text_secondary));
            }
        }
    }

    private void setActiveFilterStyle(LinearLayout filter, String title) {
        if (filter != null) {
            filter.setBackgroundResource(R.drawable.filter_chip_active);
            if (filter.getChildCount() > 0 && filter.getChildAt(0) instanceof TextView) {
                TextView textView = (TextView) filter.getChildAt(0);
                textView.setTextColor(getColor(R.color.text_primary));
            }
            if (tvFilterActive != null) {
                tvFilterActive.setText(title);
            }
        }
    }

    private void animateFilterChange() {
        if (transactionsRecyclerView != null) {
            transactionsRecyclerView.setAlpha(0f);
            transactionsRecyclerView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    private void showFilterDialog() {
        LinearLayout filtersContainer = findViewById(R.id.filtersContainer);
        if (filtersContainer != null) {
            if (filtersContainer.getVisibility() == View.GONE) {
                filtersContainer.setVisibility(View.VISIBLE);
                filtersContainer.animate()
                        .translationY(0)
                        .alpha(1f)
                        .setDuration(300)
                        .start();
            } else {
                filtersContainer.animate()
                        .translationY(-filtersContainer.getHeight())
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> filtersContainer.setVisibility(View.GONE))
                        .start();
            }
        }
    }

    private void showSearchDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search_transaction, null);
        builder.setView(dialogView);

        EditText etSearch = dialogView.findViewById(R.id.etSearch);
        Button btnSearch = dialogView.findViewById(R.id.btnSearch);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim().toLowerCase();
            if (query.isEmpty()) {
                applyFilter(currentFilter);
            } else {
                List<Transaction> searchResults = new ArrayList<>();
                for (Transaction t : allTransactions) {
                    if (t.getTitle().toLowerCase().contains(query) ||
                            t.getDescription().toLowerCase().contains(query)) {
                        searchResults.add(t);
                    }
                }
                filteredTransactions = searchResults;
                updateAdapter();
                Toast.makeText(this, "Найдено " + searchResults.size() + " операций", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadTransactions() {
        if (currentUser == null) {
            allTransactions = new ArrayList<>();
        } else {
            allTransactions = dbHelper.getUserTransactions(currentUser.getId());
        }

        if (allTransactions.isEmpty()) {
            addDemoTransactions();
            if (currentUser != null) {
                allTransactions = dbHelper.getUserTransactions(currentUser.getId());
            }
        }

        filteredTransactions = new ArrayList<>(allTransactions);
        updateAdapter();
        updateStatistics();
    }

    private void addDemoTransactions() {
        if (currentUser == null) return;

        long now = System.currentTimeMillis();

        dbHelper.addTransaction(new Transaction(
                0, currentUser.getId(), "Пополнение с карты", "Visa **** 4832",
                0.0, 1500.0, "", "USD", "deposit", "completed", now - 2 * 24 * 60 * 60 * 1000, R.drawable.ic_topup
        ));

        dbHelper.addTransaction(new Transaction(
                0, currentUser.getId(), "Перевод Ивану Петрову", "Оплата услуг",
                500.0, 0.0, "USD", "", "transfer", "completed", now - 3 * 24 * 60 * 60 * 1000, R.drawable.ic_send
        ));

        dbHelper.addTransaction(new Transaction(
                0, currentUser.getId(), "Покупка акций Apple", "AAPL - 10 шт.",
                1754.30, 0.0, "USD", "", "investment", "completed", now - 5 * 24 * 60 * 60 * 1000, R.drawable.ic_invest
        ));

        dbHelper.addTransaction(new Transaction(
                0, currentUser.getId(), "Вывод на карту", "Mastercard **** 2914",
                200.0, 0.0, "USD", "", "withdrawal", "completed", now - 7 * 24 * 60 * 60 * 1000, R.drawable.ic_send
        ));

        dbHelper.addTransaction(new Transaction(
                0, currentUser.getId(), "Кэшбэк", "5% за покупки",
                0.0, 45.50, "", "USD", "deposit", "completed", now - 10 * 24 * 60 * 60 * 1000, R.drawable.ic_gift
        ));
    }

    private void updateStatistics() {
        double totalVolume = 0;
        for (Transaction t : allTransactions) {
            totalVolume += t.getAmountOut() + t.getAmountIn();
        }

        animateNumber(tvTotalTransactions, 0, allTransactions.size());
        animateNumberValue(tvTotalVolume, 0, totalVolume);
    }

    private void animateNumber(TextView textView, int from, int to) {
        if (textView == null) return;
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            textView.setText(String.valueOf(value));
        });
        animator.start();
    }

    private void animateNumberValue(TextView textView, double from, double to) {
        if (textView == null) return;
        ValueAnimator animator = ValueAnimator.ofFloat((float) from, (float) to);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(currencyFormat.format(value));
        });
        animator.start();
    }

    private void updateAdapter() {
        if (filteredTransactions == null || filteredTransactions.isEmpty()) {
            if (transactionsRecyclerView != null) {
                transactionsRecyclerView.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if (transactionsRecyclerView != null) {
                transactionsRecyclerView.setVisibility(View.VISIBLE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }

            adapter = new TransactionAdapter(filteredTransactions);
            transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            transactionsRecyclerView.setAdapter(adapter);
        }
    }
}