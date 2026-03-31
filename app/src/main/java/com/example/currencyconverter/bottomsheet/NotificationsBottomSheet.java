package com.example.currencyconverter.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.R;
import com.example.currencyconverter.adapters.NotificationAdapter;
import com.example.currencyconverter.models.Notification;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationsBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView rvNotifications;
    private Button btnClose;
    private TextView tvUnreadCount;
    private ImageView ivMarkAllRead, ivSettings;
    private TextView filterAll, filterImportant, filterMarket, filterTransactions;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private List<Notification> filteredNotifications;
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupNotifications();
        setupClickListeners();
        updateUnreadCount();
    }

    private void initializeViews(View view) {
        rvNotifications = view.findViewById(R.id.rvNotifications);
        btnClose = view.findViewById(R.id.btnClose);
        tvUnreadCount = view.findViewById(R.id.tvUnreadCount);
        ivMarkAllRead = view.findViewById(R.id.ivMarkAllRead);
        ivSettings = view.findViewById(R.id.ivSettings);

        filterAll = view.findViewById(R.id.filterAll);
        filterImportant = view.findViewById(R.id.filterImportant);
        filterMarket = view.findViewById(R.id.filterMarket);
        filterTransactions = view.findViewById(R.id.filterTransactions);
    }

    private void setupNotifications() {
        notifications = new ArrayList<>();

        // Добавляем премиум уведомления
        notifications.add(new Notification(
                "🚀 Биткоин обновляет максимум",
                "BTC достиг $68,000. Исторический рекорд!",
                System.currentTimeMillis() - 5 * 60 * 1000,
                "market",
                false,
                "positive"
        ));

        notifications.add(new Notification(
                "💰 Получен перевод",
                "Вы получили $1,500 от Ивана Петрова",
                System.currentTimeMillis() - 30 * 60 * 1000,
                "transaction",
                true,
                "positive"
        ));

        notifications.add(new Notification(
                "✨ Новая функция",
                "Брокерский кабинет теперь доступен!",
                System.currentTimeMillis() - 2 * 60 * 60 * 1000,
                "important",
                false,
                "info"
        ));

        notifications.add(new Notification(
                "📈 S&P 500 на рекорде",
                "Индекс вырос на 1.24% за день",
                System.currentTimeMillis() - 3 * 60 * 60 * 1000,
                "market",
                false,
                "positive"
        ));

        notifications.add(new Notification(
                "💎 Premium статус активирован",
                "Спасибо за подписку! Ваши привилегии активны",
                System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                "important",
                true,
                "premium"
        ));

        notifications.add(new Notification(
                "🔄 Конвертация выполнена",
                "1000 USD → 90,500 RUB",
                System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                "transaction",
                true,
                "neutral"
        ));

        filteredNotifications = new ArrayList<>(notifications);
        applyFilter();
    }

    private void applyFilter() {
        filteredNotifications.clear();
        for (Notification n : notifications) {
            switch (currentFilter) {
                case "important":
                    if (n.getType().equals("important")) {
                        filteredNotifications.add(n);
                    }
                    break;
                case "market":
                    if (n.getType().equals("market")) {
                        filteredNotifications.add(n);
                    }
                    break;
                case "transaction":
                    if (n.getType().equals("transaction")) {
                        filteredNotifications.add(n);
                    }
                    break;
                default:
                    filteredNotifications.add(n);
                    break;
            }
        }

        adapter = new NotificationAdapter(filteredNotifications, notification -> {
            notification.setRead(true);
            adapter.notifyDataSetChanged();
            updateUnreadCount();

            // Показываем детали уведомления
            showNotificationDetail(notification);
        });

        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(adapter);
    }

    private void showNotificationDetail(Notification notification) {
        // Можно показать диалог с деталями
        Toast.makeText(getContext(), notification.getTitle() + "\n" + notification.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void updateUnreadCount() {
        int unreadCount = 0;
        for (Notification n : notifications) {
            if (!n.isRead()) unreadCount++;
        }

        if (tvUnreadCount != null) {
            if (unreadCount > 0) {
                tvUnreadCount.setText(unreadCount + " новых");
                tvUnreadCount.setTextColor(getResources().getColor(R.color.accent));
                tvUnreadCount.setBackgroundResource(R.drawable.badge_unread);
                tvUnreadCount.setPadding(
                        (int) (16 * getResources().getDisplayMetrics().density),
                        (int) (4 * getResources().getDisplayMetrics().density),
                        (int) (16 * getResources().getDisplayMetrics().density),
                        (int) (4 * getResources().getDisplayMetrics().density)
                );
            } else {
                tvUnreadCount.setText("Все прочитано");
                tvUnreadCount.setTextColor(getResources().getColor(R.color.text_secondary));
                tvUnreadCount.setBackgroundResource(android.R.color.transparent);
                tvUnreadCount.setPadding(0, 0, 0, 0);
            }
        }
    }

    private void markAllAsRead() {
        for (Notification n : notifications) {
            n.setRead(true);
        }
        applyFilter();
        updateUnreadCount();
        Toast.makeText(getContext(), "Все уведомления отмечены как прочитанные", Toast.LENGTH_SHORT).show();
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> dismiss());

        ivMarkAllRead.setOnClickListener(v -> markAllAsRead());

        ivSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Настройки уведомлений", Toast.LENGTH_SHORT).show();
        });

        // Фильтры
        filterAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterUI(filterAll);
            applyFilter();
        });

        filterImportant.setOnClickListener(v -> {
            currentFilter = "important";
            updateFilterUI(filterImportant);
            applyFilter();
        });

        filterMarket.setOnClickListener(v -> {
            currentFilter = "market";
            updateFilterUI(filterMarket);
            applyFilter();
        });

        filterTransactions.setOnClickListener(v -> {
            currentFilter = "transaction";
            updateFilterUI(filterTransactions);
            applyFilter();
        });
    }

    private void updateFilterUI(TextView selectedFilter) {
        // Сбрасываем все фильтры
        filterAll.setBackgroundResource(R.drawable.filter_chip_inactive);
        filterAll.setTextColor(getResources().getColor(R.color.text_secondary));

        filterImportant.setBackgroundResource(R.drawable.filter_chip_inactive);
        filterImportant.setTextColor(getResources().getColor(R.color.text_secondary));

        filterMarket.setBackgroundResource(R.drawable.filter_chip_inactive);
        filterMarket.setTextColor(getResources().getColor(R.color.text_secondary));

        filterTransactions.setBackgroundResource(R.drawable.filter_chip_inactive);
        filterTransactions.setTextColor(getResources().getColor(R.color.text_secondary));

        // Активируем выбранный фильтр
        selectedFilter.setBackgroundResource(R.drawable.filter_chip_active);
        selectedFilter.setTextColor(getResources().getColor(R.color.text_primary));
    }
}