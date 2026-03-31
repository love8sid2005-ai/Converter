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
import com.example.currencyconverter.models.MarketCurrency;
import com.example.currencyconverter.views.SparklineChartView;

import java.util.List;

public class MarketCurrencyAdapter extends RecyclerView.Adapter<MarketCurrencyAdapter.MarketViewHolder> {

    private List<MarketCurrency> marketCurrencies;

    public MarketCurrencyAdapter(List<MarketCurrency> marketCurrencies) {
        this.marketCurrencies = marketCurrencies;
    }

    @NonNull
    @Override
    public MarketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market_currency_premium, parent, false);
        return new MarketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketViewHolder holder, int position) {
        MarketCurrency currency = marketCurrencies.get(position);

        holder.tvCryptoCode.setText(currency.getCode());
        holder.tvCryptoName.setText(currency.getName());
        holder.tvCryptoRate.setText(currency.getFormattedRate());
        holder.tvCryptoChange.setText(currency.getFormattedChange());

        int color = currency.isRising() ?
                ContextCompat.getColor(holder.itemView.getContext(), R.color.success) :
                ContextCompat.getColor(holder.itemView.getContext(), R.color.error);
        holder.tvCryptoChange.setTextColor(color);

        holder.ivCryptoTrend.setImageResource(currency.isRising() ?
                R.drawable.ic_trend_up : R.drawable.ic_trend_down);
        holder.ivCryptoTrend.setColorFilter(color);

        holder.ivCryptoIcon.setImageResource(getIconForCurrency(currency.getCode()));
        holder.ivCryptoIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));

        int chartColor = currency.isRising() ?
                ContextCompat.getColor(holder.itemView.getContext(), R.color.success) :
                ContextCompat.getColor(holder.itemView.getContext(), R.color.error);

        // Конвертируем int[] в float[]
        int[] intChartData = currency.getChartData();
        float[] floatChartData = new float[intChartData.length];
        for (int i = 0; i < intChartData.length; i++) {
            floatChartData[i] = (float) intChartData[i];
        }

        holder.sparklineChart.setChartData(floatChartData, currency.isRising(), chartColor);

        String marketCap = getMarketCap(currency.getCode());
        holder.tvMarketCap.setText(marketCap);
    }

    private int getIconForCurrency(String code) {
        switch (code) {
            case "BTC":
                return R.drawable.ic_bitcoin;
            case "ETH":
                return R.drawable.ic_ethereum;
            case "BNB":
                return R.drawable.ic_binance;
            case "SOL":
                return R.drawable.ic_solana;
            case "XRP":
                return R.drawable.ic_xrp;
            default:
                return R.drawable.ic_graph;
        }
    }

    private String getMarketCap(String code) {
        switch (code) {
            case "BTC":
                return "$860B";
            case "ETH":
                return "$280B";
            case "BNB":
                return "$48B";
            case "SOL":
                return "$42B";
            case "XRP":
                return "$29B";
            default:
                return "—";
        }
    }

    @Override
    public int getItemCount() {
        return marketCurrencies.size();
    }

    public void updateData(List<MarketCurrency> newData) {
        this.marketCurrencies = newData;
        notifyDataSetChanged();
    }

    static class MarketViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCryptoIcon, ivCryptoTrend;
        TextView tvCryptoCode, tvCryptoName, tvCryptoRate, tvCryptoChange, tvMarketCap;
        SparklineChartView sparklineChart;

        MarketViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCryptoIcon = itemView.findViewById(R.id.ivCryptoIcon);
            tvCryptoCode = itemView.findViewById(R.id.tvCryptoCode);
            tvCryptoName = itemView.findViewById(R.id.tvCryptoName);
            tvCryptoRate = itemView.findViewById(R.id.tvCryptoRate);
            tvCryptoChange = itemView.findViewById(R.id.tvCryptoChange);
            ivCryptoTrend = itemView.findViewById(R.id.ivCryptoTrend);
            tvMarketCap = itemView.findViewById(R.id.tvMarketCap);
            sparklineChart = itemView.findViewById(R.id.sparklineChart);
        }
    }
}