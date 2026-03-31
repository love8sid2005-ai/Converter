package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.WalletCurrency;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WalletCurrencyAdapter extends RecyclerView.Adapter<WalletCurrencyAdapter.WalletCurrencyViewHolder> {

    private List<WalletCurrency> walletCurrencies;
    private OnCurrencyActionListener actionListener;
    private Random random = new Random();

    public interface OnCurrencyActionListener {
        void onExchangeClick(WalletCurrency currency);
    }

    public WalletCurrencyAdapter() {
        this.walletCurrencies = new ArrayList<>();
    }

    public void setOnCurrencyActionListener(OnCurrencyActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public WalletCurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet_currency, parent, false);
        return new WalletCurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletCurrencyViewHolder holder, int position) {
        WalletCurrency currency = walletCurrencies.get(position);

        // Символ валюты
        String symbol = getCurrencySymbol(currency.getCode());
        holder.tvCurrencySymbol.setText(symbol);

        holder.currencyCode.setText(currency.getCode());
        holder.currencyName.setText(currency.getName());

        NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);

        holder.currencyAmount.setText(formatter.format(currency.getAmount()) + " " + currency.getCode());
        holder.currencyInUSD.setText(currency.getFormattedAmountInUSD());

        // Генерируем случайное изменение за день (от -5% до +5%)
        double dailyChange = (random.nextDouble() * 10) - 5;
        String changeText = String.format("%+.2f%%", dailyChange);
        holder.tvDailyChange.setText(changeText);

        // Цвет изменения
        if (dailyChange >= 0) {
            holder.tvDailyChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
        } else {
            holder.tvDailyChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.error));
        }

        // Обработчик нажатия на кнопку обмена
        holder.actionButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onExchangeClick(currency);
            }
        });
    }

    private String getCurrencySymbol(String code) {
        switch (code) {
            case "USD": return "$";
            case "EUR": return "€";
            case "RUB": return "₽";
            case "GBP": return "£";
            case "JPY": return "¥";
            case "CNY": return "¥";
            case "BTC": return "₿";
            case "ETH": return "Ξ";
            default: return code.substring(0, 1);
        }
    }

    @Override
    public int getItemCount() {
        return walletCurrencies.size();
    }

    public void updateData(List<WalletCurrency> newWalletCurrencies) {
        this.walletCurrencies = newWalletCurrencies;
        notifyDataSetChanged();
    }

    static class WalletCurrencyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrencySymbol;
        TextView currencyCode;
        TextView currencyName;
        TextView currencyAmount;
        TextView currencyInUSD;
        TextView tvDailyChange;
        LinearLayout actionButton;

        public WalletCurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurrencySymbol = itemView.findViewById(R.id.tvCurrencySymbol);
            currencyCode = itemView.findViewById(R.id.currencyCode);
            currencyName = itemView.findViewById(R.id.currencyName);
            currencyAmount = itemView.findViewById(R.id.currencyAmount);
            currencyInUSD = itemView.findViewById(R.id.currencyInUSD);
            tvDailyChange = itemView.findViewById(R.id.tvDailyChange);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }
}