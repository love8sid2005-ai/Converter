package com.example.currencyconverter;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.User;
import com.example.currencyconverter.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone, etBirthDate;
    private Spinner spinnerOccupation;
    private Button btnRegister;
    private TextView tvLogin;
    private LinearLayout termsContainer;
    private View loadingOverlay;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_premium);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupSpinner();
        setupClickListeners();
        setupAnimations();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etBirthDate = findViewById(R.id.etBirthDate);
        spinnerOccupation = findViewById(R.id.spinnerOccupation);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        termsContainer = findViewById(R.id.termsContainer);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void setupSpinner() {
        String[] occupations = {"Выберите род деятельности",
                "Студент", "Работающий", "Предприниматель", "Фрилансер", "Пенсионер", "Другое"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_premium, occupations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_premium);
        spinnerOccupation.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> {
            animateButton(v);
            registerUser();
        });

        tvLogin.setOnClickListener(v -> {
            animateText(v);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void setupAnimations() {
        // Анимация появления элементов
        etFullName.setAlpha(0f);
        etFullName.setTranslationY(30f);
        etEmail.setAlpha(0f);
        etEmail.setTranslationY(30f);
        etPassword.setAlpha(0f);
        etPassword.setTranslationY(30f);
        etConfirmPassword.setAlpha(0f);
        etConfirmPassword.setTranslationY(30f);
        btnRegister.setAlpha(0f);
        btnRegister.setTranslationY(30f);
        tvLogin.setAlpha(0f);
        tvLogin.setTranslationY(30f);
        termsContainer.setAlpha(0f);
        termsContainer.setTranslationY(30f);

        etFullName.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(100).start();
        etEmail.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(150).start();
        etPassword.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(200).start();
        etConfirmPassword.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(250).start();
        btnRegister.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(300).start();
        tvLogin.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(350).start();
        termsContainer.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(400).start();
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

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.setAlpha(0f);
        loadingOverlay.animate().alpha(1f).setDuration(200).start();
    }

    private void hideLoading() {
        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> loadingOverlay.setVisibility(View.GONE))
                .start();
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String occupation = spinnerOccupation.getSelectedItem().toString();

        // Валидация
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Введите имя");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите email");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Введите корректный email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Введите пароль");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Пароль должен быть не менее 6 символов");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Пароли не совпадают");
            etConfirmPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Введите телефон");
            etPhone.requestFocus();
            return;
        }

        if (spinnerOccupation.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Выберите род деятельности", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isEmailExists(email)) {
            etEmail.setError("Email уже зарегистрирован");
            etEmail.requestFocus();
            return;
        }

        showLoading();

        // Имитация задержки сети
        new android.os.Handler().postDelayed(() -> {
            User user = new User(fullName, email, password, phone, birthDate, occupation);
            long userId = dbHelper.registerUser(user);

            if (userId > 0) {
                user.setId((int) userId);
                sessionManager.saveSession(user);
                hideLoading();
                Toast.makeText(this, "Регистрация успешна! Добро пожаловать!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                hideLoading();
                Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }
}