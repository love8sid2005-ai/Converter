package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private List<Currency> currencyList;

    public CurrencyAdapter() {
        this.currencyList = new ArrayList<>();
    }

    public CurrencyAdapter(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency_rates, parent, false);
        return new CurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        Currency currency = currencyList.get(position);

        holder.currencyCode.setText(currency.getCode());
        holder.currencyName.setText(currency.getName());
        holder.currencyRate.setText(currency.getDisplayRate());
        holder.currencyChange.setText(currency.getChange());
        holder.currencyCountry.setText(currency.getCountry());

        if (currency.getCode().length() > 0) {
            holder.currencyIcon.setText(currency.getCode().substring(0, 1));
        }

        // Цвет для изменения курса
        if (currency.getChange().startsWith("+")) {
            holder.currencyChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
        } else if (currency.getChange().startsWith("-")) {
            holder.currencyChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.error));
        } else {
            holder.currencyChange.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
        }
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public void updateData(List<Currency> newCurrencyList) {
        this.currencyList = newCurrencyList;
        notifyDataSetChanged();
    }

    static class CurrencyViewHolder extends RecyclerView.ViewHolder {
        TextView currencyIcon, currencyCode, currencyName, currencyCountry, currencyRate, currencyChange;

        public CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyIcon = itemView.findViewById(R.id.currencyIcon);
            currencyCode = itemView.findViewById(R.id.currencyCode);
            currencyName = itemView.findViewById(R.id.currencyName);
            currencyCountry = itemView.findViewById(R.id.currencyCountry);
            currencyRate = itemView.findViewById(R.id.currencyRate);
            currencyChange = itemView.findViewById(R.id.currencyChange);
        }
    }
}