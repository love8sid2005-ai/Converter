package com.example.currencyconverter.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.currencyconverter.models.User;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Сохранить сессию пользователя
    public void saveSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.apply();
    }

    // Получить ID текущего пользователя
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    // Получить email текущего пользователя
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    // Получить имя текущего пользователя
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "Пользователь");
    }

    // Проверить, залогинен ли пользователь
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Выйти из аккаунта
    public void logout() {
        editor.clear();
        editor.apply();
    }
}