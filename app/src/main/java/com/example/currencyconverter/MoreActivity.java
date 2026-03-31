package com.example.currencyconverter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.currencyconverter.utils.SessionManager;
import com.example.currencyconverter.views.SparklineChartView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MoreActivity extends BaseActivity {

    // UI Elements
    private LinearLayout btnSupport, btnSettings, btnBroker, btnAbout, btnRate, btnShare, btnLogout;
    private LinearLayout btnTelegram, btnVk, btnMaxApp, btnPhone, btnEmail, btnWebsite;
    private CardView statsCard, referralsCard, securityCard, updatesCard;
    private TextView tvAppVersion, tvLastUpdate, tvReferralCode, tvReferralCount, tvReferralBonus;
    private TextView tvSecurityStatus, tvSecurityScore;
    private SparklineChartView chartAppUsage;
    private ImageView ivSecurityIcon;
    private TextView tvDailyUsers, tvWeeklyActive;

    private SessionManager sessionManager;
    private Random random = new Random();
    private Handler handler = new Handler();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_more;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        initializeViews();
        setupClickListeners();
        loadAppStats();
        startStatsAnimation();
        generateReferralCode();
        checkSecurityStatus();
    }

    private void initializeViews() {
        // Основные кнопки
        btnSupport = findViewById(R.id.btnSupport);
        btnSettings = findViewById(R.id.btnSettings);
        btnBroker = findViewById(R.id.btnBroker);
        btnAbout = findViewById(R.id.btnAbout);
        btnRate = findViewById(R.id.btnRate);
        btnShare = findViewById(R.id.btnShare);
        btnLogout = findViewById(R.id.btnLogout);

        // Социальные сети и контакты
        btnTelegram = findViewById(R.id.btnTelegram);
        btnVk = findViewById(R.id.btnVk);
        btnMaxApp = findViewById(R.id.btnMaxApp);
        btnPhone = findViewById(R.id.btnPhone);
        btnEmail = findViewById(R.id.btnEmail);
        btnWebsite = findViewById(R.id.btnWebsite);

        // Карточки с фичами
        statsCard = findViewById(R.id.statsCard);
        referralsCard = findViewById(R.id.referralsCard);
        securityCard = findViewById(R.id.securityCard);
        updatesCard = findViewById(R.id.updatesCard);

        // Статистика
        tvAppVersion = findViewById(R.id.tvAppVersion);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        chartAppUsage = findViewById(R.id.chartAppUsage);
        tvDailyUsers = findViewById(R.id.tvDailyUsers);
        tvWeeklyActive = findViewById(R.id.tvWeeklyActive);

        // Реферальная система
        tvReferralCode = findViewById(R.id.tvReferralCode);
        tvReferralCount = findViewById(R.id.tvReferralCount);
        tvReferralBonus = findViewById(R.id.tvReferralBonus);

        // Безопасность
        tvSecurityStatus = findViewById(R.id.tvSecurityStatus);
        tvSecurityScore = findViewById(R.id.tvSecurityScore);
        ivSecurityIcon = findViewById(R.id.ivSecurityIcon);

        TextView pageTitle = findViewById(R.id.pageTitle);
        if (pageTitle != null) {
            pageTitle.setText("Еще");
        }

        // Устанавливаем версию приложения
        if (tvAppVersion != null) {
            tvAppVersion.setText("Версия 1.0.0");
        }

        // Устанавливаем дату последнего обновления
        if (tvLastUpdate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            tvLastUpdate.setText("Обновлено: " + sdf.format(new Date()));
        }

        // График использования
        if (chartAppUsage != null) {
            float[] usageData = {120, 145, 132, 168, 175, 189, 210};
            chartAppUsage.setChartData(usageData, true, ContextCompat.getColor(this, R.color.accent));
        }
    }

    private void setupClickListeners() {
        if (btnSupport != null) btnSupport.setOnClickListener(v -> showSupportDialog());
        if (btnSettings != null) btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        if (btnBroker != null) btnBroker.setOnClickListener(v -> startActivity(new Intent(this, BrokerActivity.class)));
        if (btnAbout != null) btnAbout.setOnClickListener(v -> showAboutDialog());

        if (btnRate != null) {
            btnRate.setOnClickListener(v -> {
                animateButton(v);
                showRateDialog();
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                animateButton(v);
                showShareDialog();
            });
        }

        if (btnLogout != null) btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        if (btnTelegram != null) btnTelegram.setOnClickListener(v -> openLink("https://t.me/currencyconverter"));
        if (btnVk != null) btnVk.setOnClickListener(v -> openLink("https://vk.com/currencyconverter"));
        if (btnMaxApp != null) btnMaxApp.setOnClickListener(v -> showMaxAppDialog());

        if (btnPhone != null) {
            btnPhone.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+78001234567"));
                startActivity(intent);
            });
        }

        if (btnEmail != null) {
            btnEmail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@currencyconverter.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Поддержка Currency Converter");
                startActivity(intent);
            });
        }

        if (btnWebsite != null) btnWebsite.setOnClickListener(v -> openLink("https://currencyconverter.com"));

        // Карточки
        if (statsCard != null) statsCard.setOnClickListener(v -> showStatsDetail());
        if (referralsCard != null) referralsCard.setOnClickListener(v -> showReferralDetail());
        if (securityCard != null) securityCard.setOnClickListener(v -> showSecurityDetail());
        if (updatesCard != null) updatesCard.setOnClickListener(v -> showUpdatesDetail());
    }

    private void animateButton(View v) {
        v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void animateCard(CardView card) {
        if (card == null) return;
        card.animate()
                .scaleX(0.98f)
                .scaleY(0.98f)
                .setDuration(150)
                .withEndAction(() -> card.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                .start();
    }

    private void loadAppStats() {
        int dailyUsers = 12500 + random.nextInt(5000);
        int weeklyActive = 45000 + random.nextInt(10000);

        if (tvDailyUsers != null) tvDailyUsers.setText(formatNumber(dailyUsers));
        if (tvWeeklyActive != null) tvWeeklyActive.setText(formatNumber(weeklyActive));
    }

    private String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }

    private void startStatsAnimation() {
        handler.postDelayed(new Runnable() {
            float[] newData = {120, 145, 132, 168, 175, 189, 210};

            @Override
            public void run() {
                if (chartAppUsage != null) {
                    float[] updatedData = new float[newData.length];
                    for (int i = 0; i < newData.length; i++) {
                        updatedData[i] = (float) (newData[i] + (random.nextDouble() - 0.5) * 20);
                    }
                    chartAppUsage.setChartData(updatedData, updatedData[6] > updatedData[0],
                            ContextCompat.getColor(MoreActivity.this, R.color.accent));
                }
                handler.postDelayed(this, 15000);
            }
        }, 15000);
    }

    private void generateReferralCode() {
        int userId = sessionManager.getUserId();
        String code = "CUR" + userId + random.nextInt(1000);
        if (tvReferralCode != null) tvReferralCode.setText(code);

        int referrals = random.nextInt(25);
        double bonus = referrals * 5.0;

        if (tvReferralCount != null) tvReferralCount.setText(String.valueOf(referrals));
        if (tvReferralBonus != null) tvReferralBonus.setText(String.format("$%.2f", bonus));
    }

    private void checkSecurityStatus() {
        boolean isSecure = true;

        if (tvSecurityStatus != null && tvSecurityScore != null && ivSecurityIcon != null) {
            if (isSecure) {
                tvSecurityStatus.setText("Защита активна");
                tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
                tvSecurityScore.setText("98/100");
                tvSecurityScore.setTextColor(ContextCompat.getColor(this, R.color.success));
                ivSecurityIcon.setImageResource(R.drawable.ic_security);
                ivSecurityIcon.setColorFilter(ContextCompat.getColor(this, R.color.success));
            } else {
                tvSecurityStatus.setText("Требуется внимание");
                tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.error));
                tvSecurityScore.setText("42/100");
                tvSecurityScore.setTextColor(ContextCompat.getColor(this, R.color.error));
                ivSecurityIcon.setImageResource(R.drawable.ic_warning);
                ivSecurityIcon.setColorFilter(ContextCompat.getColor(this, R.color.error));
            }
        }
    }

    private void showMaxAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_max_app, null);
        builder.setView(dialogView);

        Button btnClose = dialogView.findViewById(R.id.btnClose);
        Button btnWebsite = dialogView.findViewById(R.id.btnWebsite);

        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnWebsite.setOnClickListener(v -> {
            openLink("https://currencyconverter.com");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_support, null);
        builder.setView(dialogView);

        LinearLayout btnPhone = dialogView.findViewById(R.id.btnPhone);
        LinearLayout btnEmail = dialogView.findViewById(R.id.btnEmail);
        LinearLayout btnTelegram = dialogView.findViewById(R.id.btnTelegram);
        LinearLayout btnChat = dialogView.findViewById(R.id.btnChat);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        AlertDialog dialog = builder.create();

        btnPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+78001234567"));
            startActivity(intent);
            dialog.dismiss();
        });

        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@currencyconverter.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Поддержка Currency Converter");
            startActivity(intent);
            dialog.dismiss();
        });

        btnTelegram.setOnClickListener(v -> {
            openLink("https://t.me/currencyconverter");
            dialog.dismiss();
        });

        btnChat.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatSupportActivity.class));
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showStatsDetail() {
        if (statsCard != null) animateCard(statsCard);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_stats_premium, null);
        builder.setView(dialogView);

        TextView tvDaily = dialogView.findViewById(R.id.tvDailyUsers);
        TextView tvWeekly = dialogView.findViewById(R.id.tvWeeklyActive);
        Button btnClose = dialogView.findViewById(R.id.btnCloseStats);

        tvDaily.setText(tvDailyUsers != null ? tvDailyUsers.getText() : "12.5K");
        tvWeekly.setText(tvWeeklyActive != null ? tvWeeklyActive.getText() : "45K");

        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showReferralDetail() {
        if (referralsCard != null) animateCard(referralsCard);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_referral_premium, null);
        builder.setView(dialogView);

        TextView tvCode = dialogView.findViewById(R.id.tvReferralCodeDetail);
        TextView tvCount = dialogView.findViewById(R.id.tvReferralCount);
        TextView tvBonus = dialogView.findViewById(R.id.tvReferralBonus);
        Button btnCopy = dialogView.findViewById(R.id.btnCopy);
        Button btnShare = dialogView.findViewById(R.id.btnShareReferral);
        Button btnClose = dialogView.findViewById(R.id.btnCloseReferral);

        tvCode.setText(tvReferralCode != null ? tvReferralCode.getText() : "CUR123");
        tvCount.setText(tvReferralCount != null ? tvReferralCount.getText() : "0");
        tvBonus.setText(tvReferralBonus != null ? tvReferralBonus.getText() : "$0.00");

        AlertDialog dialog = builder.create();

        btnCopy.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("referral_code", tvCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Код скопирован!", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Присоединяйся к Currency Converter по моей реферальной ссылке! Получи бонус $5 на счет. Код: " + tvCode.getText());
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showSecurityDetail() {
        if (securityCard != null) animateCard(securityCard);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_security_premium, null);
        builder.setView(dialogView);

        TextView tvScore = dialogView.findViewById(R.id.tvSecurityScore);
        Button btnSettings = dialogView.findViewById(R.id.btnSecuritySettings);
        Button btnClose = dialogView.findViewById(R.id.btnCloseSecurity);

        tvScore.setText("98/100");

        AlertDialog dialog = builder.create();

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showUpdatesDetail() {
        if (updatesCard != null) animateCard(updatesCard);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_updates_premium, null);
        builder.setView(dialogView);

        Button btnClose = dialogView.findViewById(R.id.btnCloseUpdates);

        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_about_premium, null);
        builder.setView(dialogView);

        Button btnClose = dialogView.findViewById(R.id.btnCloseAbout);

        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showShareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_share_premium, null);
        builder.setView(dialogView);

        ImageView shareTelegram = dialogView.findViewById(R.id.shareTelegram);
        ImageView shareVk = dialogView.findViewById(R.id.shareVk);
        ImageView shareWhatsapp = dialogView.findViewById(R.id.shareWhatsapp);
        Button btnShareSystem = dialogView.findViewById(R.id.btnShareSystem);
        Button btnClose = dialogView.findViewById(R.id.btnShareClose);

        String shareText = "Скачай Currency Converter - лучшее приложение для конвертации валют! https://play.google.com/store/apps/details?id=" + getPackageName();

        AlertDialog dialog = builder.create();

        shareTelegram.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://t.me/share/url?url=" + shareText));
            startActivity(intent);
            dialog.dismiss();
        });

        shareVk.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://vk.com/share.php?url=" + shareText));
            startActivity(intent);
            dialog.dismiss();
        });

        shareWhatsapp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/?text=" + shareText));
            startActivity(intent);
            dialog.dismiss();
        });

        btnShareSystem.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rate_premium, null);
        builder.setView(dialogView);

        ImageView star1 = dialogView.findViewById(R.id.star1);
        ImageView star2 = dialogView.findViewById(R.id.star2);
        ImageView star3 = dialogView.findViewById(R.id.star3);
        ImageView star4 = dialogView.findViewById(R.id.star4);
        ImageView star5 = dialogView.findViewById(R.id.star5);
        TextView tvMessage = dialogView.findViewById(R.id.tvRateMessage);
        LinearLayout bonusSection = dialogView.findViewById(R.id.bonusSection);
        LinearLayout feedbackSection = dialogView.findViewById(R.id.feedbackSection);
        EditText etFeedback = dialogView.findViewById(R.id.etFeedback);
        Button btnRate = dialogView.findViewById(R.id.btnRate);
        Button btnSendFeedback = dialogView.findViewById(R.id.btnSendFeedback);
        Button btnLater = dialogView.findViewById(R.id.btnLater);
        Button btnNever = dialogView.findViewById(R.id.btnNever);

        final int[] rating = {0};

        View.OnClickListener starClickListener = v -> {
            int id = v.getId();
            if (id == R.id.star1) rating[0] = 1;
            else if (id == R.id.star2) rating[0] = 2;
            else if (id == R.id.star3) rating[0] = 3;
            else if (id == R.id.star4) rating[0] = 4;
            else if (id == R.id.star5) rating[0] = 5;

            updateStars(star1, star2, star3, star4, star5, rating[0]);

            if (rating[0] == 5) {
                tvMessage.setText("Отлично! Спасибо за высокую оценку!");
                bonusSection.setVisibility(View.VISIBLE);
                feedbackSection.setVisibility(View.GONE);
                btnRate.setVisibility(View.VISIBLE);
                btnSendFeedback.setVisibility(View.GONE);
            } else if (rating[0] >= 3) {
                tvMessage.setText("Спасибо! Расскажите, что можно улучшить?");
                bonusSection.setVisibility(View.GONE);
                feedbackSection.setVisibility(View.VISIBLE);
                btnRate.setVisibility(View.GONE);
                btnSendFeedback.setVisibility(View.VISIBLE);
            } else {
                tvMessage.setText("Нам жаль. Поделитесь, что пошло не так?");
                bonusSection.setVisibility(View.GONE);
                feedbackSection.setVisibility(View.VISIBLE);
                btnRate.setVisibility(View.GONE);
                btnSendFeedback.setVisibility(View.VISIBLE);
            }
        };

        star1.setOnClickListener(starClickListener);
        star2.setOnClickListener(starClickListener);
        star3.setOnClickListener(starClickListener);
        star4.setOnClickListener(starClickListener);
        star5.setOnClickListener(starClickListener);

        AlertDialog dialog = builder.create();

        btnRate.setOnClickListener(v -> {
            if (rating[0] == 0) {
                Toast.makeText(this, "Пожалуйста, выберите оценку", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating[0] >= 4) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
                Toast.makeText(this, "Спасибо! Бонусы уже начислены!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Спасибо за отзыв! Мы обязательно станем лучше!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        btnSendFeedback.setOnClickListener(v -> {
            String feedback = etFeedback.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(this, "Напишите ваш отзыв", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:feedback@currencyconverter.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Отзыв о приложении (" + rating[0] + " звезд)");
            intent.putExtra(Intent.EXTRA_TEXT, feedback);
            startActivity(intent);
            Toast.makeText(this, "Спасибо за отзыв!", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        btnLater.setOnClickListener(v -> {
            Toast.makeText(this, "Напомним через 3 дня", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnNever.setOnClickListener(v -> {
            Toast.makeText(this, "Напоминание отключено", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateStars(ImageView s1, ImageView s2, ImageView s3, ImageView s4, ImageView s5, int rating) {
        int filled = R.drawable.ic_star_gold;
        int outline = R.drawable.ic_star_outline;

        s1.setImageResource(rating >= 1 ? filled : outline);
        s1.setColorFilter(rating >= 1 ? getColor(R.color.accent) : getColor(R.color.text_disabled));

        s2.setImageResource(rating >= 2 ? filled : outline);
        s2.setColorFilter(rating >= 2 ? getColor(R.color.accent) : getColor(R.color.text_disabled));

        s3.setImageResource(rating >= 3 ? filled : outline);
        s3.setColorFilter(rating >= 3 ? getColor(R.color.accent) : getColor(R.color.text_disabled));

        s4.setImageResource(rating >= 4 ? filled : outline);
        s4.setColorFilter(rating >= 4 ? getColor(R.color.accent) : getColor(R.color.text_disabled));

        s5.setImageResource(rating >= 5 ? filled : outline);
        s5.setColorFilter(rating >= 5 ? getColor(R.color.accent) : getColor(R.color.text_disabled));
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout_premium, null);
        builder.setView(dialogView);

        Button btnConfirm = dialogView.findViewById(R.id.btnLogoutConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnLogoutCancel);

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}