package com.example.currencyconverter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.currencyconverter.models.ChatMessage;
import com.example.currencyconverter.models.Transaction;
import com.example.currencyconverter.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_converter.db";
    private static final int DATABASE_VERSION = 2; // Увеличили версию

    // Таблица пользователей
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_FULL_NAME = "full_name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_PHONE = "phone";
    private static final String COLUMN_USER_BIRTH_DATE = "birth_date";
    private static final String COLUMN_USER_OCCUPATION = "occupation";
    private static final String COLUMN_USER_BALANCE = "balance_usd";
    private static final String COLUMN_USER_CURRENCY = "currency";
    private static final String COLUMN_USER_PREMIUM = "is_premium";
    private static final String COLUMN_USER_CREATED_AT = "created_at";

    // Таблица транзакций
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_TRANS_ID = "id";
    private static final String COLUMN_TRANS_USER_ID = "user_id";
    private static final String COLUMN_TRANS_TITLE = "title";
    private static final String COLUMN_TRANS_DESC = "description";
    private static final String COLUMN_TRANS_AMOUNT_OUT = "amount_out";
    private static final String COLUMN_TRANS_AMOUNT_IN = "amount_in";
    private static final String COLUMN_TRANS_CURRENCY_OUT = "currency_out";
    private static final String COLUMN_TRANS_CURRENCY_IN = "currency_in";
    private static final String COLUMN_TRANS_TYPE = "type";
    private static final String COLUMN_TRANS_STATUS = "status";
    private static final String COLUMN_TRANS_TIMESTAMP = "timestamp";
    private static final String COLUMN_TRANS_ICON = "icon_res_id";

    // Таблица чата (связь с пользователем)
    private static final String TABLE_CHAT = "chat_history";
    private static final String COLUMN_CHAT_ID = "id";
    private static final String COLUMN_CHAT_USER_ID = "user_id";
    private static final String COLUMN_CHAT_MESSAGE = "message";
    private static final String COLUMN_CHAT_IS_USER = "is_user";
    private static final String COLUMN_CHAT_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы пользователей
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_FULL_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_PHONE + " TEXT,"
                + COLUMN_USER_BIRTH_DATE + " TEXT,"
                + COLUMN_USER_OCCUPATION + " TEXT,"
                + COLUMN_USER_BALANCE + " REAL DEFAULT 1000.0,"
                + COLUMN_USER_CURRENCY + " TEXT DEFAULT 'RUB',"
                + COLUMN_USER_PREMIUM + " INTEGER DEFAULT 0,"
                + COLUMN_USER_CREATED_AT + " INTEGER"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Создание таблицы транзакций
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + COLUMN_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TRANS_USER_ID + " INTEGER,"
                + COLUMN_TRANS_TITLE + " TEXT,"
                + COLUMN_TRANS_DESC + " TEXT,"
                + COLUMN_TRANS_AMOUNT_OUT + " REAL,"
                + COLUMN_TRANS_AMOUNT_IN + " REAL,"
                + COLUMN_TRANS_CURRENCY_OUT + " TEXT,"
                + COLUMN_TRANS_CURRENCY_IN + " TEXT,"
                + COLUMN_TRANS_TYPE + " TEXT,"
                + COLUMN_TRANS_STATUS + " TEXT,"
                + COLUMN_TRANS_TIMESTAMP + " INTEGER,"
                + COLUMN_TRANS_ICON + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_TRANS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);

        // Создание таблицы чата с привязкой к пользователю
        String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHAT + "("
                + COLUMN_CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CHAT_USER_ID + " INTEGER,"
                + COLUMN_CHAT_MESSAGE + " TEXT,"
                + COLUMN_CHAT_IS_USER + " INTEGER,"
                + COLUMN_CHAT_TIMESTAMP + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_CHAT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_CHAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ========== МЕТОДЫ ДЛЯ ПОЛЬЗОВАТЕЛЕЙ ==========

    // Регистрация нового пользователя
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_FULL_NAME, user.getFullName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword()); // В реальном проекте нужно хешировать!
        values.put(COLUMN_USER_PHONE, user.getPhone());
        values.put(COLUMN_USER_BIRTH_DATE, user.getBirthDate());
        values.put(COLUMN_USER_OCCUPATION, user.getOccupation());
        values.put(COLUMN_USER_BALANCE, user.getBalanceUSD());
        values.put(COLUMN_USER_CURRENCY, user.getCurrency());
        values.put(COLUMN_USER_PREMIUM, user.isPremium() ? 1 : 0);
        values.put(COLUMN_USER_CREATED_AT, user.getCreatedAt());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Вход пользователя (проверка email и пароля)
    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_EMAIL + " = ? AND " +
                COLUMN_USER_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BIRTH_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_OCCUPATION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_BALANCE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_CURRENCY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_PREMIUM)) == 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_CREATED_AT))
            );
        }

        cursor.close();
        db.close();
        return user;
    }

    // Получить пользователя по ID
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BIRTH_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_OCCUPATION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_USER_BALANCE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_CURRENCY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_PREMIUM)) == 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_CREATED_AT))
            );
        }

        cursor.close();
        db.close();
        return user;
    }

    // Обновить баланс пользователя
    public int updateUserBalance(int userId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_BALANCE, newBalance);

        return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    // Проверить существует ли email
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // ========== МЕТОДЫ ДЛЯ ТРАНЗАКЦИЙ ==========

    // Добавить транзакцию
    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TRANS_USER_ID, transaction.getUserId());
        values.put(COLUMN_TRANS_TITLE, transaction.getTitle());
        values.put(COLUMN_TRANS_DESC, transaction.getDescription());
        values.put(COLUMN_TRANS_AMOUNT_OUT, transaction.getAmountOut());
        values.put(COLUMN_TRANS_AMOUNT_IN, transaction.getAmountIn());
        values.put(COLUMN_TRANS_CURRENCY_OUT, transaction.getCurrencyOut());
        values.put(COLUMN_TRANS_CURRENCY_IN, transaction.getCurrencyIn());
        values.put(COLUMN_TRANS_TYPE, transaction.getType());
        values.put(COLUMN_TRANS_STATUS, transaction.getStatus());
        values.put(COLUMN_TRANS_TIMESTAMP, transaction.getTimestamp());
        values.put(COLUMN_TRANS_ICON, transaction.getIconResId());

        long id = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return id;
    }

    // Получить все транзакции пользователя
    public List<Transaction> getUserTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TRANS_USER_ID + " = ? " +
                " ORDER BY " + COLUMN_TRANS_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANS_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANS_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_DESC)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRANS_AMOUNT_OUT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TRANS_AMOUNT_IN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_CURRENCY_OUT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_CURRENCY_IN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_STATUS)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TRANS_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANS_ICON))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactions;
    }

    // Удалить все транзакции пользователя
    public void deleteUserTransactions(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, COLUMN_TRANS_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }

    // ========== МЕТОДЫ ДЛЯ ЧАТА ==========

    // Добавить сообщение чата для пользователя
    public long addChatMessage(int userId, ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CHAT_USER_ID, userId);
        values.put(COLUMN_CHAT_MESSAGE, message.getMessage());
        values.put(COLUMN_CHAT_IS_USER, message.isUser() ? 1 : 0);
        values.put(COLUMN_CHAT_TIMESTAMP, message.getTimestamp());

        long id = db.insert(TABLE_CHAT, null, values);
        db.close();
        return id;
    }

    // Получить все сообщения чата пользователя
    public List<ChatMessage> getUserChatMessages(int userId) {
        List<ChatMessage> messages = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CHAT +
                " WHERE " + COLUMN_CHAT_USER_ID + " = ? " +
                " ORDER BY " + COLUMN_CHAT_TIMESTAMP + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_MESSAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_IS_USER)) == 1,
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CHAT_TIMESTAMP))
                );
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }

    // Удалить все сообщения чата пользователя
    public void deleteUserChatMessages(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHAT, COLUMN_CHAT_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }
}