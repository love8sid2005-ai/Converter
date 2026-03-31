package com.example.currencyconverter.bottomsheet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.currencyconverter.R;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.Transaction;
import com.example.currencyconverter.models.User;
import com.example.currencyconverter.utils.SessionManager;
import com.example.currencyconverter.views.AmountEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class SendMoneyBottomSheet extends BaseBottomSheet {

    private AmountEditText etAmount;
    private EditText etRecipient, etDescription;
    private Button btnSend, btnCancel;
    private TextView tvCurrentBalance, tvFee, tvTotalAmount;
    private ImageView ivClose;
    private TextView preset10, preset50, preset100, preset500;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;
    private OnTransactionCompleteListener listener;

    public interface OnTransactionCompleteListener {
        void onTransactionCompleted();
    }

    public static SendMoneyBottomSheet newInstance() {
        return new SendMoneyBottomSheet();
    }

    public void setOnTransactionCompleteListener(OnTransactionCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bottom_sheet_send_money;
    }

    @Override
    protected void setupViews(@NonNull View view) {
        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        int userId = sessionManager.getUserId();
        if (userId != -1) {
            currentUser = dbHelper.getUserById(userId);
        }

        initializeViews(view);
        setupPresets();
        updateBalanceDisplay();
        setupClickListeners();
        setupAmountListener();
    }

    private void initializeViews(View view) {
        etAmount = view.findViewById(R.id.etAmount);
        etRecipient = view.findViewById(R.id.etRecipient);
        etDescription = view.findViewById(R.id.etDescription);
        btnSend = view.findViewById(R.id.btnSend);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvCurrentBalance = view.findViewById(R.id.tvCurrentBalance);
        tvFee = view.findViewById(R.id.tvFee);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        ivClose = view.findViewById(R.id.ivClose);

        preset10 = view.findViewById(R.id.preset10);
        preset50 = view.findViewById(R.id.preset50);
        preset100 = view.findViewById(R.id.preset100);
        preset500 = view.findViewById(R.id.preset500);
    }

    private void setupPresets() {
        preset10.setOnClickListener(v -> etAmount.setAmount(10));
        preset50.setOnClickListener(v -> etAmount.setAmount(50));
        preset100.setOnClickListener(v -> etAmount.setAmount(100));
        preset500.setOnClickListener(v -> etAmount.setAmount(500));
    }

    private void setupAmountListener() {
        etAmount.setOnAmountChangeListener(amount -> {
            updateFeeAndTotal();
            updateBalanceDisplay();
        });
    }

    private void updateFeeAndTotal() {
        double amount = etAmount.getAmount();
        double fee = amount * 0.005;
        double total = amount + fee;

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        tvFee.setText(formatter.format(fee));
        tvTotalAmount.setText(formatter.format(total));
    }

    private void updateBalanceDisplay() {
        if (currentUser != null) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            tvCurrentBalance.setText(formatter.format(currentUser.getBalanceUSD()));

            double amount = etAmount.getAmount();
            double fee = amount * 0.005;
            double total = amount + fee;

            if (amount > 0 && total > currentUser.getBalanceUSD()) {
                tvCurrentBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
                btnSend.setEnabled(false);
                btnSend.setAlpha(0.5f);
            } else {
                tvCurrentBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
                btnSend.setEnabled(true);
                btnSend.setAlpha(1f);
            }
        }
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> processSendMoney());
        btnCancel.setOnClickListener(v -> dismissSheet());
        ivClose.setOnClickListener(v -> dismissSheet());
    }

    private void processSendMoney() {
        double amount = etAmount.getAmount();
        String recipient = etRecipient.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (amount <= 0) {
            Toast.makeText(getContext(), "Введите сумму больше 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(recipient)) {
            etRecipient.setError("Введите получателя");
            return;
        }

        double fee = amount * 0.005;
        double total = amount + fee;

        if (currentUser.getBalanceUSD() < total) {
            Toast.makeText(getContext(), "Недостаточно средств (с учетом комиссии)", Toast.LENGTH_SHORT).show();
            return;
        }

        double newBalance = currentUser.getBalanceUSD() - total;
        dbHelper.updateUserBalance(currentUser.getId(), newBalance);
        currentUser.setBalanceUSD(newBalance);

        Transaction transaction = new Transaction(
                0,
                currentUser.getId(),
                "Перевод " + recipient,
                TextUtils.isEmpty(description) ? "Перевод средств" : description,
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

        Toast.makeText(getContext(), String.format("Перевод выполнен!\nСумма: $%.2f\nКомиссия: $%.2f", amount, fee), Toast.LENGTH_LONG).show();

        if (listener != null) {
            listener.onTransactionCompleted();
        }

        dismissSheet();
    }
}