package com.example.currencyconverter.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.currencyconverter.BrokerActivity;
import com.example.currencyconverter.R;

public class BrokerNotificationManager {
    private static final String CHANNEL_ID = "broker_notifications";
    private static final String CHANNEL_NAME = "Брокерские уведомления";
    private static final int NOTIFICATION_ID = 1001;

    private Context context;
    private NotificationManager notificationManager;

    public BrokerNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Уведомления о рыночных событиях");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showMarketNotification(String title, String message, String impact) {
        // Проверяем разрешение для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Нет разрешения - не показываем уведомление
                return;
            }
        }

        Intent intent = new Intent(context, BrokerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        int color;
        if (impact.equals("positive")) {
            color = R.color.success;
        } else if (impact.equals("negative")) {
            color = R.color.error;
        } else {
            color = R.color.warning;
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Используем системную иконку
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(context, color))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        }
    }
}