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
import com.example.currencyconverter.models.MarketIndicator;

import java.util.List;

public class MarketOverviewAdapter extends RecyclerView.Adapter<MarketOverviewAdapter.ViewHolder> {

    private List<MarketIndicator> indicators;

    public MarketOverviewAdapter(List<MarketIndicator> indicators) {
        this.indicators = indicators;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market_overview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketIndicator indicator = indicators.get(position);

        holder.ivIcon.setImageResource(indicator.getIconRes());
        holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));
        holder.tvName.setText(indicator.getName());
        holder.tvValue.setText(indicator.getValue());
        holder.tvChange.setText(indicator.getChange());

        int color = indicator.isPositive() ?
                ContextCompat.getColor(holder.itemView.getContext(), R.color.success) :
                ContextCompat.getColor(holder.itemView.getContext(), R.color.error);
        holder.tvChange.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return indicators.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvValue, tvChange;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivMarketIcon);
            tvName = itemView.findViewById(R.id.tvMarketName);
            tvValue = itemView.findViewById(R.id.tvMarketValue);
            tvChange = itemView.findViewById(R.id.tvMarketChange);
        }
    }
}