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
import com.example.currencyconverter.models.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;
    private OnNewsClickListener listener;
    private boolean isEnglish;

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    public NewsAdapter(List<News> newsList, OnNewsClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
        this.isEnglish = true; // по умолчанию
    }

    public void setLanguage(boolean isEnglish) {
        this.isEnglish = isEnglish;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        // Устанавливаем иконку с единым оранжевым цветом
        holder.ivImage.setImageResource(news.getImageResId());
        holder.ivImage.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));

        // Если у новости есть два варианта заголовка, выбираем по языку
        if (news.getTitleEn() != null && news.getTitleRu() != null) {
            holder.tvTitle.setText(isEnglish ? news.getTitleEn() : news.getTitleRu());
            holder.tvDescription.setText(isEnglish ? news.getDescriptionEn() : news.getDescriptionRu());
            holder.tvTime.setText(isEnglish ? news.getTimeEn() : news.getTimeRu());
        } else {
            holder.tvTitle.setText(news.getTitle());
            holder.tvDescription.setText(news.getDescription());
            holder.tvTime.setText(news.getTime());
        }

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
        ImageView ivImage;
        TextView tvTitle, tvDescription, tvTime;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivNewsImage);
            tvTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvDescription = itemView.findViewById(R.id.tvNewsDescription);
            tvTime = itemView.findViewById(R.id.tvNewsTime);
        }
    }
}