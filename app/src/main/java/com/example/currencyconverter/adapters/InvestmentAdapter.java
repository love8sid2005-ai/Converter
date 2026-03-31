package com.example.currencyconverter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.models.Investment;

import java.util.List;

public class InvestmentAdapter extends RecyclerView.Adapter<InvestmentAdapter.InvestmentViewHolder> {

    private List<Investment> investments;
    private OnInvestmentClickListener listener;

    public interface OnInvestmentClickListener {
        void onInvestmentClick(Investment investment);
    }

    public InvestmentAdapter(List<Investment> investments, OnInvestmentClickListener listener) {
        this.investments = investments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InvestmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_investment, parent, false);
        return new InvestmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestmentViewHolder holder, int position) {
        Investment investment = investments.get(position);

        // Используем стандартную иконку вместо getIconResId()
        holder.ivIcon.setImageResource(R.drawable.ic_invest);
        holder.tvTitle.setText(investment.getName() + " (" + investment.getSymbol() + ")");
        holder.tvReturnRate.setText(investment.getFormattedQuantity() + " шт.");
        holder.tvAmount.setText(investment.getFormattedTotalValue());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInvestmentClick(investment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return investments.size();
    }

    static class InvestmentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvReturnRate, tvAmount;

        InvestmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivInvestmentIcon);
            tvTitle = itemView.findViewById(R.id.tvInvestmentTitle);
            tvReturnRate = itemView.findViewById(R.id.tvInvestmentReturn);
            tvAmount = itemView.findViewById(R.id.tvInvestmentAmount);
        }
    }
}