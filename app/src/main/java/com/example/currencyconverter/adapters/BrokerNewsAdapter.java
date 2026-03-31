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
import com.example.currencyconverter.models.BrokerNews;

import java.util.List;

public class BrokerNewsAdapter extends RecyclerView.Adapter<BrokerNewsAdapter.NewsViewHolder> {

    private List<BrokerNews> newsList;
    private OnNewsClickListener listener;

    public interface OnNewsClickListener {
        void onNewsClick(BrokerNews news);
    }

    public BrokerNewsAdapter(List<BrokerNews> newsList, OnNewsClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_broker_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        BrokerNews news = newsList.get(position);

        // Устанавливаем иконку с единым оранжевым цветом
        holder.ivIcon.setImageResource(news.getIconResId());
        holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));

        holder.tvTitle.setText(news.getTitle());
        holder.tvMessage.setText(news.getMessage());
        holder.tvTime.setText(news.getTime());

        int color;
        String impactSymbol;

        switch (news.getImpact()) {
            case "positive":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.success);
                impactSymbol = "▲";
                break;
            case "negative":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.error);
                impactSymbol = "▼";
                break;
            default:
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.warning);
                impactSymbol = "●";
                break;
        }

        holder.tvImpact.setText(impactSymbol);
        holder.tvImpact.setTextColor(color);
        holder.tvImpact.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNewsClick(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvMessage, tvTime, tvImpact;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivNewsIcon);
            tvTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvMessage = itemView.findViewById(R.id.tvNewsMessage);
            tvTime = itemView.findViewById(R.id.tvNewsTime);
            tvImpact = itemView.findViewById(R.id.tvNewsImpact);
        }
    }
}