package com.example.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.currencyconverter.utils.SessionManager;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotifications, switchDarkMode, switchPinCode;
    private LinearLayout tvLanguage, tvCurrency, tvSecurity, tvAbout, tvLogout;
    private TextView tvLanguageValue, tvCurrencyValue, tvPinCodeStatus;
    private Button btnDeleteAccount, btnChangePin;
    private LinearLayout pinCodeContainer;
    private ImageView ivBack;

    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;
    private Vibrator vibrator;
    private String currentPinCode = "";
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_PIN_CODE = "pin_code";
    private static final String KEY_PIN_ENABLED = "pin_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initializeViews();
        loadSettings();
        setupClickListeners();
        loadCurrentSettings();
    }

    private void initializeViews() {
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchPinCode = findViewById(R.id.switchPinCode);

        tvLanguage = findViewById(R.id.tvLanguage);
        tvCurrency = findViewById(R.id.tvCurrency);
        tvSecurity = findViewById(R.id.tvSecurity);
        tvAbout = findViewById(R.id.tvAbout);
        tvLogout = findViewById(R.id.tvLogout);

        tvLanguageValue = findViewById(R.id.tvLanguageValue);
        tvCurrencyValue = findViewById(R.id.tvCurrencyValue);
        tvPinCodeStatus = findViewById(R.id.tvPinCodeStatus);

        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnChangePin = findViewById(R.id.btnChangePin);
        pinCodeContainer = findViewById(R.id.pinCodeContainer);
        ivBack = findViewById(R.id.ivBack);

        TextView pageTitle = findViewById(R.id.pageTitle);
        if (pageTitle != null) {
            pageTitle.setText("Настройки");
        }

        // Кнопка назад для закрытия
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    private void loadSettings() {
        // Уведомления
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            showToast(isChecked ? "Уведомления включены" : "Уведомления отключены");
            vibrate();
        });

        // Темная тема
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode_enabled", false));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode_enabled", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                showToast("Темная тема включена");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                showToast("Светлая тема включена");
            }
            vibrate();
            recreate();
        });

        // PIN-код
        boolean pinEnabled = sharedPreferences.getBoolean(KEY_PIN_ENABLED, false);
        currentPinCode = sharedPreferences.getString(KEY_PIN_CODE, "");
        switchPinCode.setChecked(pinEnabled);
        updatePinCodeStatus();

        switchPinCode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showSetPinDialog();
            } else {
                disablePinCode();
            }
            vibrate();
        });
    }

    private void loadCurrentSettings() {
        // Загрузка языка
        String currentLang = sharedPreferences.getString("language", "ru");
        tvLanguageValue.setText(getLanguageName(currentLang));

        // Загрузка валюты
        String currentCurrency = sharedPreferences.getString("currency", "USD");
        tvCurrencyValue.setText(currentCurrency);
    }

    private void setupClickListeners() {
        // Язык
        if (tvLanguage != null) {
            tvLanguage.setOnClickListener(v -> {
                animateClick(v);
                showLanguageDialog();
            });
        }

        // Валюта
        if (tvCurrency != null) {
            tvCurrency.setOnClickListener(v -> {
                animateClick(v);
                showCurrencyDialog();
            });
        }

        // Безопасность
        if (tvSecurity != null) {
            tvSecurity.setOnClickListener(v -> {
                animateClick(v);
                showSecurityDialog();
            });
        }

        // О приложении
        if (tvAbout != null) {
            tvAbout.setOnClickListener(v -> {
                animateClick(v);
                showAboutDialog();
            });
        }

        // Выход
        if (tvLogout != null) {
            tvLogout.setOnClickListener(v -> {
                animateClick(v);
                showLogoutConfirmation();
            });
        }

        // Удалить аккаунт
        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> {
                animateClick(v);
                showDeleteAccountConfirmation();
            });
        }

        // Изменить PIN-код
        if (btnChangePin != null) {
            btnChangePin.setOnClickListener(v -> {
                animateClick(v);
                if (sharedPreferences.getBoolean(KEY_PIN_ENABLED, false)) {
                    showChangePinDialog();
                } else {
                    showToast("Сначала включите PIN-код");
                }
            });
        }
    }

    private void animateClick(View v) {
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.button_pulse);
        v.startAnimation(pulse);
        vibrate();
    }

    private void vibrate() {
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // ==================== PIN-КОД ====================

    private void showSetPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_set_pin, null);
        builder.setView(dialogView);

        android.widget.EditText etPin1 = dialogView.findViewById(R.id.etPin1);
        android.widget.EditText etPin2 = dialogView.findViewById(R.id.etPin2);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            String pin1 = etPin1.getText().toString().trim();
            String pin2 = etPin2.getText().toString().trim();

            if (pin1.length() != 4) {
                etPin1.setError("PIN-код должен состоять из 4 цифр");
                return;
            }

            if (!pin1.equals(pin2)) {
                etPin2.setError("PIN-коды не совпадают");
                return;
            }

            currentPinCode = pin1;
            sharedPreferences.edit()
                    .putString(KEY_PIN_CODE, pin1)
                    .putBoolean(KEY_PIN_ENABLED, true)
                    .apply();

            updatePinCodeStatus();
            showToast("PIN-код установлен");
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            switchPinCode.setChecked(false);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showChangePinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_pin, null);
        builder.setView(dialogView);

        android.widget.EditText etOldPin = dialogView.findViewById(R.id.etOldPin);
        android.widget.EditText etNewPin1 = dialogView.findViewById(R.id.etNewPin1);
        android.widget.EditText etNewPin2 = dialogView.findViewById(R.id.etNewPin2);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(v -> {
            String oldPin = etOldPin.getText().toString().trim();
            String newPin1 = etNewPin1.getText().toString().trim();
            String newPin2 = etNewPin2.getText().toString().trim();

            if (!oldPin.equals(currentPinCode)) {
                etOldPin.setError("Неверный текущий PIN-код");
                return;
            }

            if (newPin1.length() != 4) {
                etNewPin1.setError("PIN-код должен состоять из 4 цифр");
                return;
            }

            if (!newPin1.equals(newPin2)) {
                etNewPin2.setError("PIN-коды не совпадают");
                return;
            }

            currentPinCode = newPin1;
            sharedPreferences.edit()
                    .putString(KEY_PIN_CODE, newPin1)
                    .apply();

            updatePinCodeStatus();
            showToast("PIN-код изменен");
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void disablePinCode() {
        sharedPreferences.edit()
                .putBoolean(KEY_PIN_ENABLED, false)
                .putString(KEY_PIN_CODE, "")
                .apply();
        currentPinCode = "";
        updatePinCodeStatus();
        showToast("PIN-код отключен");
    }

    private void updatePinCodeStatus() {
        boolean enabled = sharedPreferences.getBoolean(KEY_PIN_ENABLED, false);
        if (tvPinCodeStatus != null) {
            if (enabled) {
                tvPinCodeStatus.setText("Установлен");
                tvPinCodeStatus.setTextColor(getColor(R.color.success));
                if (pinCodeContainer != null) pinCodeContainer.setVisibility(View.VISIBLE);
                if (btnChangePin != null) btnChangePin.setVisibility(View.VISIBLE);
            } else {
                tvPinCodeStatus.setText("Не установлен");
                tvPinCodeStatus.setTextColor(getColor(R.color.text_secondary));
                if (pinCodeContainer != null) pinCodeContainer.setVisibility(View.GONE);
                if (btnChangePin != null) btnChangePin.setVisibility(View.GONE);
            }
        }
    }

    // ==================== ЯЗЫК ====================

    private void showLanguageDialog() {
        String[] languages = {"Русский", "English", "Deutsch", "Français", "Español"};
        String[] languageCodes = {"ru", "en", "de", "fr", "es"};

        new AlertDialog.Builder(this, R.style.PremiumDialogTheme)
                .setTitle("Выберите язык")
                .setItems(languages, (dialog, which) -> {
                    String selectedLang = languageCodes[which];
                    String selectedLangName = languages[which];

                    tvLanguageValue.setText(selectedLangName);
                    sharedPreferences.edit().putString("language", selectedLang).apply();
                    setLocale(selectedLang);
                    showToast("Язык изменен на " + selectedLangName);
                    recreate();
                })
                .show();
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLanguageName(String code) {
        switch (code) {
            case "ru": return "Русский";
            case "en": return "English";
            case "de": return "Deutsch";
            case "fr": return "Français";
            case "es": return "Español";
            default: return "Русский";
        }
    }

    // ==================== ВАЛЮТА ====================

    private void showCurrencyDialog() {
        String[] currencies = {"USD", "EUR", "RUB", "GBP", "JPY", "CNY", "CAD", "AUD", "CHF"};

        new AlertDialog.Builder(this, R.style.PremiumDialogTheme)
                .setTitle("Основная валюта")
                .setItems(currencies, (dialog, which) -> {
                    String selectedCurrency = currencies[which];
                    tvCurrencyValue.setText(selectedCurrency);
                    sharedPreferences.edit().putString("currency", selectedCurrency).apply();
                    showToast("Основная валюта: " + selectedCurrency);
                })
                .show();
    }

    // ==================== ДИАЛОГИ ====================

    private void showSecurityDialog() {
        new AlertDialog.Builder(this, R.style.PremiumDialogTheme)
                .setTitle("Безопасность")
                .setMessage("🔒 Двухфакторная аутентификация: Отключена" +
                        "\n🔐 PIN-код: " + (sharedPreferences.getBoolean(KEY_PIN_ENABLED, false) ? "Установлен" : "Не установлен") +
                        "\n\nНастройте дополнительные методы защиты в разделе выше.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this, R.style.PremiumDialogTheme)
                .setTitle("О приложении")
                .setMessage("Currency Converter\n\nВерсия: 1.0.0\n\nПремиум банкинг с AI поддержкой\nБрокерский кабинет\nАналитика в реальном времени\n\n© 2026 Все права защищены")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this, R.style.PremiumDialogTheme)
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    showToast("Вы вышли из аккаунта");
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(this, R.style.PremiumDialogTheme)
                .setTitle("Удаление аккаунта")
                .setMessage("⚠️ Это действие необратимо. Все ваши данные будут удалены навсегда. Продолжить?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    showToast("Аккаунт будет удален в течение 24 часов");
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}