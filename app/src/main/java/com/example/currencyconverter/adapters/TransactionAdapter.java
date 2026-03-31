package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private SimpleDateFormat timeFormat;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.iconImageView.setImageResource(transaction.getIconResId());
        holder.titleTextView.setText(transaction.getTitle());
        holder.descriptionTextView.setText(transaction.getDescription());

        // Форматируем время
        String time = timeFormat.format(new Date(transaction.getTimestamp()));
        holder.timeTextView.setText(time);

        // Отображаем исходящую сумму (если есть)
        String amountOut = transaction.getFormattedAmountOut();
        if (!amountOut.isEmpty()) {
            holder.amountOutTextView.setText(amountOut);
            holder.amountOutTextView.setVisibility(View.VISIBLE);
            holder.amountOutTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.error));
        } else {
            holder.amountOutTextView.setVisibility(View.GONE);
        }

        // Отображаем входящую сумму (если есть)
        String amountIn = transaction.getFormattedAmountIn();
        if (!amountIn.isEmpty()) {
            holder.amountInTextView.setText(amountIn);
            holder.amountInTextView.setVisibility(View.VISIBLE);
            holder.amountInTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
        } else {
            holder.amountInTextView.setVisibility(View.GONE);
        }

        // Устанавливаем статус
        if (transaction.getStatus().equals("completed")) {
            // Все ок
        } else if (transaction.getStatus().equals("pending")) {
            // Можно добавить индикатор ожидания
        } else if (transaction.getStatus().equals("failed")) {
            // Можно добавить индикатор ошибки
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateData(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView amountOutTextView;
        TextView amountInTextView;
        TextView timeTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            amountOutTextView = itemView.findViewById(R.id.amountOutTextView);
            amountInTextView = itemView.findViewById(R.id.amountInTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}