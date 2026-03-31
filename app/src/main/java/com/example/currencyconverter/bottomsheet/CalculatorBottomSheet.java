package com.example.currencyconverter.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.currencyconverter.R;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.Transaction;
import com.example.currencyconverter.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.Locale;

public class CalculatorBottomSheet extends BottomSheetDialogFragment {

    private TabLayout tabLayout;
    private LinearLayout loanLayout, investLayout, currencyLayout, mortgageLayout;

    // Loan calculator
    private Slider loanAmountSlider, loanTermSlider, loanRateSlider;
    private TextView tvLoanAmount, tvLoanTerm, tvLoanRate, tvMonthlyPayment, tvTotalPayment, tvOverpayment;

    // Investment calculator
    private Slider investAmountSlider, investTermSlider, investRateSlider;
    private TextView tvInvestAmount, tvInvestTerm, tvInvestRate, tvFinalAmount, tvProfitAmount, tvProfitPercent;

    private Button btnCalculate, btnSave, btnShare, btnClose;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        initializeViews(view);
        setupTabLayout();
        setupLoanCalculator();
        setupInvestmentCalculator();
        setupClickListeners();
    }

    private void initializeViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        loanLayout = view.findViewById(R.id.loanLayout);
        investLayout = view.findViewById(R.id.investLayout);
        currencyLayout = view.findViewById(R.id.currencyLayout);
        mortgageLayout = view.findViewById(R.id.mortgageLayout);

        // Loan views
        loanAmountSlider = view.findViewById(R.id.loanAmountSlider);
        loanTermSlider = view.findViewById(R.id.loanTermSlider);
        loanRateSlider = view.findViewById(R.id.loanRateSlider);
        tvLoanAmount = view.findViewById(R.id.tvLoanAmount);
        tvLoanTerm = view.findViewById(R.id.tvLoanTerm);
        tvLoanRate = view.findViewById(R.id.tvLoanRate);
        tvMonthlyPayment = view.findViewById(R.id.tvMonthlyPayment);
        tvTotalPayment = view.findViewById(R.id.tvTotalPayment);
        tvOverpayment = view.findViewById(R.id.tvOverpayment);

        // Investment views
        investAmountSlider = view.findViewById(R.id.investAmountSlider);
        investTermSlider = view.findViewById(R.id.investTermSlider);
        investRateSlider = view.findViewById(R.id.investRateSlider);
        tvInvestAmount = view.findViewById(R.id.tvInvestAmount);
        tvInvestTerm = view.findViewById(R.id.tvInvestTerm);
        tvInvestRate = view.findViewById(R.id.tvInvestRate);
        tvFinalAmount = view.findViewById(R.id.tvFinalAmount);
        tvProfitAmount = view.findViewById(R.id.tvProfitAmount);
        tvProfitPercent = view.findViewById(R.id.tvProfitPercent);

        btnCalculate = view.findViewById(R.id.btnCalculate);
        btnSave = view.findViewById(R.id.btnSave);
        btnShare = view.findViewById(R.id.btnShare);
        btnClose = view.findViewById(R.id.btnClose);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Скрываем все
                loanLayout.setVisibility(View.GONE);
                investLayout.setVisibility(View.GONE);
                currencyLayout.setVisibility(View.GONE);
                mortgageLayout.setVisibility(View.GONE);

                // Показываем выбранный
                switch (tab.getPosition()) {
                    case 0:
                        loanLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        investLayout.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        currencyLayout.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mortgageLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupLoanCalculator() {
        loanAmountSlider.addOnChangeListener((slider, value, fromUser) -> {
            tvLoanAmount.setText(currencyFormat.format(value * 1000));
            calculateLoan();
        });

        loanTermSlider.addOnChangeListener((slider, value, fromUser) -> {
            tvLoanTerm.setText((int)value + " мес");
            calculateLoan();
        });

        loanRateSlider.addOnChangeListener((slider, value, fromUser) -> {
            tvLoanRate.setText(String.format("%.1f%%", value));
            calculateLoan();
        });

        // Устанавливаем начальные значения
        tvLoanAmount.setText(currencyFormat.format(1000000));
        tvLoanTerm.setText("12 мес");
        tvLoanRate.setText("12.0%");
        calculateLoan();
    }

    private void calculateLoan() {
        double amount = loanAmountSlider.getValue() * 1000;
        int months = (int) loanTermSlider.getValue();
        double monthlyRate = loanRateSlider.getValue() / 100 / 12;

        if (monthlyRate == 0) {
            tvMonthlyPayment.setText(currencyFormat.format(amount / months));
            tvTotalPayment.setText(currencyFormat.format(amount));
            tvOverpayment.setText(currencyFormat.format(0));
            return;
        }

        double monthlyPayment = amount * monthlyRate * Math.pow(1 + monthlyRate, months) /
                (Math.pow(1 + monthlyRate, months) - 1);
        double totalPayment = monthlyPayment * months;
        double overpayment = totalPayment - amount;

        tvMonthlyPayment.setText(currencyFormat.format(monthlyPayment));
        tvTotalPayment.setText(currencyFormat.format(totalPayment));
        tvOverpayment.setText(currencyFormat.format(overpayment));
    }

    private void setupInvestmentCalculator() {
        investAmountSlider.addOnChangeListener((slider, value, fromUser) -> {
            tvInvestAmount.setText(currencyFormat.format(value * 100));
            calculateInvestment();
        });

        investTermSlider.addOnChangeListener((slider, value, fromUser) -> {
            tvInvestTerm.setText((int)value + " лет");
            calculateInvestment();
        });

        investRateSlider.addOnChangeListener((slider, value, fromUser) -> {
            tvInvestRate.setText(String.format("%.1f%%", value));
            calculateInvestment();
        });

        // Устанавливаем начальные значения
        tvInvestAmount.setText(currencyFormat.format(10000));
        tvInvestTerm.setText("5 лет");
        tvInvestRate.setText("10.0%");
        calculateInvestment();
    }

    private void calculateInvestment() {
        double amount = investAmountSlider.getValue() * 100;
        int years = (int) investTermSlider.getValue();
        double rate = investRateSlider.getValue() / 100;

        double finalAmount = amount * Math.pow(1 + rate, years);
        double profit = finalAmount - amount;
        double profitPercent = (profit / amount) * 100;

        tvFinalAmount.setText(currencyFormat.format(finalAmount));
        tvProfitAmount.setText(currencyFormat.format(profit));
        tvProfitPercent.setText(String.format("+%.1f%%", profitPercent));
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(v -> {
            // Пересчет уже происходит автоматически через слайдеры
            Toast.makeText(requireContext(), "Расчет выполнен", Toast.LENGTH_SHORT).show();
            vibrate();
        });

        btnSave.setOnClickListener(v -> {
            if (sessionManager.getUserId() != -1) {
                Transaction transaction = new Transaction(
                        0,
                        sessionManager.getUserId(),
                        "Финансовый расчет",
                        getCurrentCalculationSummary(),
                        0.0,
                        0.0,
                        "",
                        "",
                        "calculation",
                        "completed",
                        System.currentTimeMillis(),
                        R.drawable.ic_calculator
                );
                dbHelper.addTransaction(transaction);
                Toast.makeText(requireContext(), "Расчет сохранен", Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(v -> {
            String shareText = getCurrentCalculationSummary();
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            startActivity(android.content.Intent.createChooser(shareIntent, "Поделиться расчетом"));
        });

        btnClose.setOnClickListener(v -> dismiss());
    }

    private String getCurrentCalculationSummary() {
        int selectedTab = tabLayout.getSelectedTabPosition();
        switch (selectedTab) {
            case 0:
                return String.format("Кредитный расчет: сумма %s, срок %s, ставка %s, платеж %s",
                        tvLoanAmount.getText(), tvLoanTerm.getText(), tvLoanRate.getText(),
                        tvMonthlyPayment.getText());
            case 1:
                return String.format("Инвестиционный расчет: сумма %s, срок %s, ставка %s, результат %s",
                        tvInvestAmount.getText(), tvInvestTerm.getText(), tvInvestRate.getText(),
                        tvFinalAmount.getText());
            default:
                return "Финансовый расчет";
        }
    }

    private void vibrate() {
        try {
            android.os.Vibrator vibrator = (android.os.Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(50);
            }
        } catch (Exception e) {
            // Игнорируем
        }
    }
}