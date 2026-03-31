package com.example.currencyconverter.bottomsheet;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.utils.SessionManager;
import com.example.currencyconverter.views.SparklineChartView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AnalyticsBottomSheet extends BottomSheetDialogFragment {

    // UI Elements
    private TextView tvTitle, tvSubtitle;
    private TabLayout tabLayout;
    private LinearLayout contentOverview, contentTransactions, contentCategories, contentTrends;
    private Button btnRefresh, btnClose, btnExport;
    private ImageView ivClose;

    // Overview Data
    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense, tvTotalTransactions;
    private TextView tvMonthlyChange, tvWeeklyChange;
    private SparklineChartView chartBalance;

    // Transactions Data
    private RecyclerView rvRecentTransactions;
    private RecentTransactionAdapter transactionAdapter;
    private List<RecentTransaction> recentTransactions;

    // Categories Data
    private LinearLayout categoryInvestments, categoryShopping, categoryEntertainment, categoryFood, categoryTransport;
    private TextView tvInvestmentsAmount, tvShoppingAmount, tvEntertainmentAmount, tvFoodAmount, tvTransportAmount;

    // Trends Data
    private TextView tvBestDay, tvBestHour, tvMostUsedCurrency;
    private LinearLayout trendUp, trendDown;
    private TextView tvTrendUpPercent, tvTrendDownPercent;

    // Data
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Random random = new Random();
    private Handler handler = new Handler();
    private boolean isAnimating = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        initializeViews(view);
        setupTabLayout();
        loadAnalyticsData();
        setupClickListeners();
        startAnimatedChart();
    }

    private void initializeViews(View view) {
        // Header
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);
        ivClose = view.findViewById(R.id.ivClose);

        // Tab Layout
        tabLayout = view.findViewById(R.id.tabLayout);

        // Content containers
        contentOverview = view.findViewById(R.id.contentOverview);
        contentTransactions = view.findViewById(R.id.contentTransactions);
        contentCategories = view.findViewById(R.id.contentCategories);
        contentTrends = view.findViewById(R.id.contentTrends);

        // Overview
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        tvTotalTransactions = view.findViewById(R.id.tvTotalTransactions);
        tvMonthlyChange = view.findViewById(R.id.tvMonthlyChange);
        tvWeeklyChange = view.findViewById(R.id.tvWeeklyChange);
        chartBalance = view.findViewById(R.id.chartBalance);

        // Transactions
        rvRecentTransactions = view.findViewById(R.id.rvRecentTransactions);

        // Categories
        categoryInvestments = view.findViewById(R.id.categoryInvestments);
        categoryShopping = view.findViewById(R.id.categoryShopping);
        categoryEntertainment = view.findViewById(R.id.categoryEntertainment);
        categoryFood = view.findViewById(R.id.categoryFood);
        categoryTransport = view.findViewById(R.id.categoryTransport);

        tvInvestmentsAmount = view.findViewById(R.id.tvInvestmentsAmount);
        tvShoppingAmount = view.findViewById(R.id.tvShoppingAmount);
        tvEntertainmentAmount = view.findViewById(R.id.tvEntertainmentAmount);
        tvFoodAmount = view.findViewById(R.id.tvFoodAmount);
        tvTransportAmount = view.findViewById(R.id.tvTransportAmount);

        // Trends
        tvBestDay = view.findViewById(R.id.tvBestDay);
        tvBestHour = view.findViewById(R.id.tvBestHour);
        tvMostUsedCurrency = view.findViewById(R.id.tvMostUsedCurrency);
        trendUp = view.findViewById(R.id.trendUp);
        trendDown = view.findViewById(R.id.trendDown);
        tvTrendUpPercent = view.findViewById(R.id.tvTrendUpPercent);
        tvTrendDownPercent = view.findViewById(R.id.tvTrendDownPercent);

        // Buttons
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnClose = view.findViewById(R.id.btnClose);
        btnExport = view.findViewById(R.id.btnExport);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                contentOverview.setVisibility(View.GONE);
                contentTransactions.setVisibility(View.GONE);
                contentCategories.setVisibility(View.GONE);
                contentTrends.setVisibility(View.GONE);

                switch (tab.getPosition()) {
                    case 0:
                        contentOverview.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        contentTransactions.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        contentCategories.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        contentTrends.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadAnalyticsData() {
        loadOverviewData();
        loadRecentTransactions();
        loadCategoriesData();
        loadTrendsData();
    }

    private void loadOverviewData() {
        double totalBalance = 12450.75;
        double totalIncome = 8450.32;
        double totalExpense = 4230.15;
        int totalTransactions = 47;
        double monthlyChange = 12.5;
        double weeklyChange = 3.2;

        animateNumber(tvTotalBalance, 0, totalBalance);
        animateNumber(tvTotalIncome, 0, totalIncome);
        animateNumber(tvTotalExpense, 0, totalExpense);
        tvTotalTransactions.setText(String.valueOf(totalTransactions));

        tvMonthlyChange.setText(String.format("+%.1f%%", monthlyChange));
        tvMonthlyChange.setTextColor(monthlyChange >= 0 ?
                ContextCompat.getColor(requireContext(), R.color.success) :
                ContextCompat.getColor(requireContext(), R.color.error));

        tvWeeklyChange.setText(String.format("%+.1f%%", weeklyChange));
        tvWeeklyChange.setTextColor(weeklyChange >= 0 ?
                ContextCompat.getColor(requireContext(), R.color.success) :
                ContextCompat.getColor(requireContext(), R.color.error));

        // Исправлено: передаем 3 параметра в setChartData
        float[] chartData = {12000, 11800, 11900, 12100, 12050, 12200, 12450};
        int chartColor = ContextCompat.getColor(requireContext(), R.color.success);
        chartBalance.setChartData(chartData, true, chartColor);
    }

    private void loadRecentTransactions() {
        recentTransactions = new ArrayList<>();
        recentTransactions.add(new RecentTransaction("Перевод Ивану", "$1,500", "15:30", R.drawable.ic_send, true));
        recentTransactions.add(new RecentTransaction("Пополнение карты", "$500", "12:45", R.drawable.ic_topup, false));
        recentTransactions.add(new RecentTransaction("Оплата услуг", "$120", "10:20", R.drawable.ic_payments, true));
        recentTransactions.add(new RecentTransaction("Конвертация USD/EUR", "$850", "09:15", R.drawable.ic_convert, false));
        recentTransactions.add(new RecentTransaction("Кэшбэк", "$45", "Вчера", R.drawable.ic_gift, false));

        transactionAdapter = new RecentTransactionAdapter(recentTransactions);
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentTransactions.setAdapter(transactionAdapter);
    }

    private void loadCategoriesData() {
        animateCategoryAmount(tvInvestmentsAmount, 3240);
        animateCategoryAmount(tvShoppingAmount, 1250);
        animateCategoryAmount(tvEntertainmentAmount, 890);
        animateCategoryAmount(tvFoodAmount, 560);
        animateCategoryAmount(tvTransportAmount, 320);

        setupCategoryClickListeners();
    }

    private void setupCategoryClickListeners() {
        categoryInvestments.setOnClickListener(v -> showCategoryDetail("Инвестиции", 3240));
        categoryShopping.setOnClickListener(v -> showCategoryDetail("Покупки", 1250));
        categoryEntertainment.setOnClickListener(v -> showCategoryDetail("Развлечения", 890));
        categoryFood.setOnClickListener(v -> showCategoryDetail("Еда", 560));
        categoryTransport.setOnClickListener(v -> showCategoryDetail("Транспорт", 320));
    }

    private void showCategoryDetail(String category, double amount) {
        double totalExpenses = 3240 + 1250 + 890 + 560 + 320;
        Toast.makeText(requireContext(),
                String.format("%s: $%.2f\n%.0f%% от расходов",
                        category, amount, (amount / totalExpenses) * 100),
                Toast.LENGTH_LONG).show();
    }

    private void loadTrendsData() {
        String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

        tvBestDay.setText(days[5]);
        tvBestHour.setText("14:00");
        tvMostUsedCurrency.setText("USD");

        double upTrend = 15.3;
        double downTrend = 8.7;

        tvTrendUpPercent.setText(String.format("+%.1f%%", upTrend));
        tvTrendDownPercent.setText(String.format("-%.1f%%", downTrend));

        trendUp.setOnClickListener(v -> showTrendDetail("Рост", upTrend));
        trendDown.setOnClickListener(v -> showTrendDetail("Падение", downTrend));
    }

    private void showTrendDetail(String trend, double percent) {
        Toast.makeText(requireContext(),
                String.format("%s: %.1f%%\n%s активности за последние 7 дней",
                        trend, percent, trend.equals("Рост") ? "Увеличение" : "Уменьшение"),
                Toast.LENGTH_LONG).show();
    }

    private void animateNumber(TextView textView, double from, double to) {
        ValueAnimator animator = ValueAnimator.ofFloat((float) from, (float) to);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(String.format(Locale.US, "$%,.2f", value));
        });
        animator.start();
    }

    private void animateCategoryAmount(TextView textView, double to) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) to);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(String.format(Locale.US, "$%,.0f", value));
        });
        animator.start();
    }

    private void startAnimatedChart() {
        handler.postDelayed(new Runnable() {
            float[] data = {12000, 11800, 11900, 12100, 12050, 12200, 12450};
            int chartColor = ContextCompat.getColor(requireContext(), R.color.success);

            @Override
            public void run() {
                if (!isAnimating && chartBalance != null) {
                    float[] newData = new float[data.length];
                    for (int i = 0; i < data.length; i++) {
                        newData[i] = (float) (data[i] + (random.nextDouble() - 0.5) * 200);
                    }
                    chartBalance.setChartData(newData, newData[newData.length - 1] > newData[0], chartColor);
                }
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
            loadAnalyticsData();
        });

        btnExport.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Экспорт в PDF", Toast.LENGTH_SHORT).show();
        });

        btnClose.setOnClickListener(v -> dismiss());
        ivClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        isAnimating = false;
    }

    // Внутренний класс для транзакций
    private static class RecentTransaction {
        String title;
        String amount;
        String time;
        int iconRes;
        boolean isOutgoing;

        RecentTransaction(String title, String amount, String time, int iconRes, boolean isOutgoing) {
            this.title = title;
            this.amount = amount;
            this.time = time;
            this.iconRes = iconRes;
            this.isOutgoing = isOutgoing;
        }
    }

    // Адаптер для транзакций
    private class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> {
        private List<RecentTransaction> transactions;

        RecentTransactionAdapter(List<RecentTransaction> transactions) {
            this.transactions = transactions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recent_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RecentTransaction tx = transactions.get(position);
            holder.ivIcon.setImageResource(tx.iconRes);
            holder.ivIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent));
            holder.tvTitle.setText(tx.title);
            holder.tvTime.setText(tx.time);
            holder.tvAmount.setText(tx.amount);
            holder.tvAmount.setTextColor(ContextCompat.getColor(requireContext(),
                    tx.isOutgoing ? R.color.error : R.color.success));
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvTitle, tvTime, tvAmount;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.ivIcon);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvAmount = itemView.findViewById(R.id.tvAmount);
            }
        }
    }
}