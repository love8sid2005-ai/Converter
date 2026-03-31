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
import com.example.currencyconverter.models.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnNotificationClickListener listener;
    private SimpleDateFormat timeFormat;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Выбираем иконку в зависимости от типа
        int iconRes;
        switch (notification.getType()) {
            case "important":
                iconRes = R.drawable.ic_premium;
                break;
            case "market":
                iconRes = R.drawable.ic_graph;
                break;
            case "transaction":
                iconRes = R.drawable.ic_transfer;
                break;
            default:
                iconRes = R.drawable.ic_notification;
        }

        holder.ivNotificationIcon.setImageResource(iconRes);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());

        String time = timeFormat.format(new Date(notification.getTimestamp()));

        // Форматируем время более человечно
        long diff = System.currentTimeMillis() - notification.getTimestamp();
        if (diff < 60 * 1000) {
            holder.tvTime.setText("только что");
        } else if (diff < 60 * 60 * 1000) {
            holder.tvTime.setText((diff / (60 * 1000)) + " мин назад");
        } else if (diff < 24 * 60 * 60 * 1000) {
            holder.tvTime.setText((diff / (60 * 60 * 1000)) + " ч назад");
        } else {
            holder.tvTime.setText(time);
        }

        // Показываем точку для непрочитанных
        if (!notification.isRead()) {
            holder.vDot.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.vDot.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.7f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotificationIcon;
        TextView tvTitle, tvMessage, tvTime;
        View vDot;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotificationIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            vDot = itemView.findViewById(R.id.vDot);
        }
    }
}