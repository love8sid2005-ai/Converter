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
import com.example.currencyconverter.models.QuickAction;

import java.util.List;

public class QuickActionAdapter extends RecyclerView.Adapter<QuickActionAdapter.QuickActionViewHolder> {

    private List<QuickAction> quickActions;
    private OnQuickActionClickListener listener;

    public interface OnQuickActionClickListener {
        void onQuickActionClick(QuickAction action);
    }

    public QuickActionAdapter(List<QuickAction> quickActions, OnQuickActionClickListener listener) {
        this.quickActions = quickActions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuickActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_action, parent, false);
        return new QuickActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickActionViewHolder holder, int position) {
        QuickAction action = quickActions.get(position);

        // Устанавливаем иконку - цвет задается через tint в layout, не меняем здесь
        holder.iconImageView.setImageResource(action.getIconResId());

        holder.titleTextView.setText(action.getTitle());
        holder.titleTextView.setGravity(android.view.Gravity.CENTER);
        // Цвет текста уже задан в layout

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuickActionClick(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quickActions.size();
    }

    static class QuickActionViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;

        public QuickActionViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
}