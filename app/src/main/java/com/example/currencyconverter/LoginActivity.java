package com.example.currencyconverter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.User;
import com.example.currencyconverter.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private LinearLayout socialLoginContainer;
    private ImageView ivGoogle, ivApple, ivPhone;
    private LinearLayout biometricLogin;
    private TextView tvBiometricText;
    private View loadingOverlay;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_premium);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            goToHome();
            return;
        }

        initViews();
        setupClickListeners();
        setupAnimations();
        checkBiometricAvailability();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        socialLoginContainer = findViewById(R.id.socialLoginContainer);
        ivGoogle = findViewById(R.id.ivGoogle);
        ivApple = findViewById(R.id.ivApple);
        ivPhone = findViewById(R.id.ivPhone);
        biometricLogin = findViewById(R.id.biometricLogin);
        tvBiometricText = findViewById(R.id.tvBiometricText);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            animateButton(v);
            loginUser();
        });

        tvRegister.setOnClickListener(v -> {
            animateText(v);
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        tvForgotPassword.setOnClickListener(v -> {
            animateText(v);
            showForgotPasswordDialog();
        });

        ivGoogle.setOnClickListener(v -> {
            animateSocialIcon(v);
            socialLogin("Google");
        });

        ivApple.setOnClickListener(v -> {
            animateSocialIcon(v);
            socialLogin("Apple");
        });

        ivPhone.setOnClickListener(v -> {
            animateSocialIcon(v);
            socialLogin("Phone");
        });

        biometricLogin.setOnClickListener(v -> {
            animateButton(v);
            attemptBiometricLogin();
        });
    }

    private void setupAnimations() {
        // Анимация появления элементов
        etEmail.setAlpha(0f);
        etEmail.setTranslationY(50f);
        etPassword.setAlpha(0f);
        etPassword.setTranslationY(50f);
        btnLogin.setAlpha(0f);
        btnLogin.setTranslationY(50f);
        socialLoginContainer.setAlpha(0f);
        socialLoginContainer.setTranslationY(50f);
        biometricLogin.setAlpha(0f);
        biometricLogin.setTranslationY(50f);

        etEmail.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(100)
                .start();

        etPassword.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(200)
                .start();

        btnLogin.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(300)
                .start();

        socialLoginContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(400)
                .start();

        biometricLogin.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(500)
                .start();
    }

    private void animateButton(View v) {
        v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void animateText(View v) {
        v.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(150)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                .start();
    }

    private void animateSocialIcon(View v) {
        v.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                .start();
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.setAlpha(0f);
        loadingOverlay.animate()
                .alpha(1f)
                .setDuration(200)
                .start();
    }

    private void hideLoading() {
        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> loadingOverlay.setVisibility(View.GONE))
                .start();
    }

    private void checkBiometricAvailability() {
        // Проверка наличия биометрии на устройстве
        boolean hasBiometric = false;
        // Здесь должна быть проверка BiometricManager

        if (hasBiometric) {
            biometricLogin.setVisibility(View.VISIBLE);
            tvBiometricText.setText("Войти по отпечатку пальца");
        } else {
            biometricLogin.setVisibility(View.GONE);
        }
    }

    private void attemptBiometricLogin() {
        // Здесь логика биометрической аутентификации
        Toast.makeText(this, "Биометрический вход", Toast.LENGTH_SHORT).show();
        // В демо-режиме просто показываем уведомление
    }

    private void socialLogin(String provider) {
        Toast.makeText(this, "Вход через " + provider, Toast.LENGTH_SHORT).show();
        // Здесь будет интеграция с социальными сетями
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.PremiumDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        builder.setView(dialogView);

        EditText etEmailReset = dialogView.findViewById(R.id.etEmailReset);
        Button btnSend = dialogView.findViewById(R.id.btnSend);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSend.setOnClickListener(v -> {
            String email = etEmailReset.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmailReset.setError("Введите email");
                return;
            }

            if (dbHelper.isEmailExists(email)) {
                Toast.makeText(this, "Инструкции по восстановлению отправлены на " + email, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } else {
                etEmailReset.setError("Email не найден");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Введите пароль");
            etPassword.requestFocus();
            return;
        }

        showLoading();

        // Имитация задержки сети
        new android.os.Handler().postDelayed(() -> {
            User user = dbHelper.loginUser(email, password);

            if (user != null) {
                sessionManager.saveSession(user);
                hideLoading();
                Toast.makeText(this, "Добро пожаловать, " + user.getFullName() + "!", Toast.LENGTH_SHORT).show();
                goToHome();
            } else {
                hideLoading();
                Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}