package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Устанавливаем базовый layout с навигацией
        setContentView(R.layout.activity_base);

        // Инфлейтим контент конкретной Activity
        View contentView = LayoutInflater.from(this)
                .inflate(getContentLayoutId(), null);

        FrameLayout contentContainer = findViewById(R.id.contentContainer);
        if (contentContainer != null) {
            contentContainer.addView(contentView);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupBottomNavigation();
    }

    // Метод для получения ID layout контента (без навигации)
    protected abstract int getContentLayoutId();

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    if (!(this instanceof HomeActivity)) {
                        openActivity(HomeActivity.class);
                    }
                    return true;
                } else if (id == R.id.nav_converter) {
                    if (!(this instanceof ConverterActivity)) {
                        openActivity(ConverterActivity.class);
                    }
                    return true;
                } else if (id == R.id.nav_rates) {
                    if (!(this instanceof RatesActivity) && !(this instanceof BrokerActivity)) {
                        openActivity(RatesActivity.class);
                    }
                    return true;
                } else if (id == R.id.nav_wallet) {
                    if (!(this instanceof WalletActivity)) {
                        openActivity(WalletActivity.class);
                    }
                    return true;
                } else if (id == R.id.nav_more) {
                    // MoreActivity и все связанные с ней активности
                    if (!(this instanceof MoreActivity) &&
                            !(this instanceof ChatSupportActivity) &&
                            !(this instanceof AIAssistantActivity) &&
                            !(this instanceof TransactionsHistoryActivity)) {
                        openActivity(MoreActivity.class);
                    }
                    return true;
                }
                return false;
            });

            // Устанавливаем текущий выбранный пункт
            if (this instanceof HomeActivity) {
                bottomNav.setSelectedItemId(R.id.nav_home);
            } else if (this instanceof ConverterActivity) {
                bottomNav.setSelectedItemId(R.id.nav_converter);
            } else if (this instanceof RatesActivity || this instanceof BrokerActivity) {
                bottomNav.setSelectedItemId(R.id.nav_rates);
            } else if (this instanceof WalletActivity) {
                bottomNav.setSelectedItemId(R.id.nav_wallet);
            } else if (this instanceof MoreActivity || this instanceof ChatSupportActivity ||
                    this instanceof TransactionsHistoryActivity || this instanceof AIAssistantActivity) {
                bottomNav.setSelectedItemId(R.id.nav_more);
            }
            // SettingsActivity не устанавливаем выделение, так как она не имеет нижней навигации
        }
    }

    protected void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}