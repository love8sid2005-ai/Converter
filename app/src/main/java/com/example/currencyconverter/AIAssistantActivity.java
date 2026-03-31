package com.example.currencyconverter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.adapters.ChatAdapter;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.ChatMessage;
import com.example.currencyconverter.services.AISupportService;
import com.example.currencyconverter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIAssistantActivity extends BaseActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageView sendButton, backButton, clearChatButton;
    private LinearLayout quickActionsContainer;
    private TextView tvAiStatus, tvAiTyping;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private AISupportService aiService;
    private Handler handler = new Handler();
    private Random random = new Random();

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int currentUserId;

    // Quick actions
    private String[] quickActions = {
            "Как конвертировать валюту?",
            "Какой курс доллара?",
            "Как пополнить кошелек?",
            "Как вывести деньги?",
            "Как купить акции?",
            "Безопасно ли хранить деньги?"
    };

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_ai_assistant;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        initializeViews();
        setupChat();
        setupQuickActions();
        setupClickListeners();
        loadChatHistory();
        startAiAnimation();
    }

    private void initializeViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        clearChatButton = findViewById(R.id.clearChatButton);
        quickActionsContainer = findViewById(R.id.quickActionsContainer);
        tvAiStatus = findViewById(R.id.tvAiStatus);
        tvAiTyping = findViewById(R.id.tvAiTyping);
    }

    private void setupChat() {
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        aiService = new AISupportService();

        // Анимация статуса AI
        startStatusAnimation();
    }

    private void startStatusAnimation() {
        handler.postDelayed(new Runnable() {
            String[] statuses = {"Онлайн", "Отвечает мгновенно", "24/7 поддержка", "AI ассистент"};
            int index = 0;

            @Override
            public void run() {
                if (tvAiStatus != null) {
                    tvAiStatus.setText(statuses[index % statuses.length]);
                    index++;
                }
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void startAiAnimation() {
        // Анимация пульсации для иконки AI
        ImageView ivAiAvatar = findViewById(R.id.ivAiAvatar);
        if (ivAiAvatar != null) {
            ValueAnimator pulseAnimator = ValueAnimator.ofFloat(1f, 1.1f, 1f);
            pulseAnimator.setDuration(2000);
            pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
            pulseAnimator.addUpdateListener(animation -> {
                float scale = (float) animation.getAnimatedValue();
                ivAiAvatar.setScaleX(scale);
                ivAiAvatar.setScaleY(scale);
            });
            pulseAnimator.start();
        }
    }

    private void setupQuickActions() {
        quickActionsContainer.removeAllViews();
        for (String action : quickActions) {
            TextView chip = new TextView(this);
            chip.setText(action);
            chip.setTextSize(12);
            chip.setTextColor(getColor(R.color.text_primary));
            chip.setBackgroundResource(R.drawable.filter_chip_inactive);
            chip.setPadding(40, 12, 40, 12);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(12);
            chip.setLayoutParams(params);
            chip.setOnClickListener(v -> {
                messageInput.setText(action);
                sendButton.performClick();
            });
            quickActionsContainer.addView(chip);
        }
    }

    private void loadChatHistory() {
        messages.clear();

        if (currentUserId == -1) {
            addWelcomeMessage();
            return;
        }

        List<ChatMessage> savedMessages = dbHelper.getUserChatMessages(currentUserId);

        if (savedMessages.isEmpty()) {
            addWelcomeMessage();
        } else {
            messages.addAll(savedMessages);
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.smoothScrollToPosition(messages.size() - 1);

            if (messages.size() > 1) {
                quickActionsContainer.setVisibility(View.GONE);
            }
        }
    }

    private void addWelcomeMessage() {
        String welcome = "Привет! Я AI-ассистент Currency Converter. Задайте любой вопрос о конвертации валют, кошельке, инвестициях или выберите популярный вопрос ниже. Я отвечу мгновенно!";

        ChatMessage welcomeMessage = new ChatMessage(welcome, false);

        if (currentUserId != -1) {
            long id = dbHelper.addChatMessage(currentUserId, welcomeMessage);
            welcomeMessage.setId((int) id);
        }

        messages.add(welcomeMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();

            if (!TextUtils.isEmpty(message)) {
                sendMessage(message);
            } else {
                Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());

        clearChatButton.setOnClickListener(v -> clearChat());
    }

    private void sendMessage(String message) {
        // Добавляем сообщение пользователя
        ChatMessage userMessage = new ChatMessage(message, true);

        if (currentUserId != -1) {
            long id = dbHelper.addChatMessage(currentUserId, userMessage);
            userMessage.setId((int) id);
        }

        messages.add(userMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.smoothScrollToPosition(messages.size() - 1);

        messageInput.setText("");

        // Скрываем быстрые действия после первого сообщения
        if (messages.size() > 1) {
            quickActionsContainer.setVisibility(View.GONE);
        }

        // Показываем индикатор печати
        showTypingIndicator();

        // Получаем ответ AI с задержкой
        handler.postDelayed(() -> {
            removeTypingIndicator();

            String response = aiService.getAIResponse(message);
            ChatMessage aiMessage = new ChatMessage(response, false);

            if (currentUserId != -1) {
                long aiId = dbHelper.addChatMessage(currentUserId, aiMessage);
                aiMessage.setId((int) aiId);
            }

            messages.add(aiMessage);
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        }, 1000);
    }

    private void showTypingIndicator() {
        tvAiTyping.setVisibility(View.VISIBLE);
        tvAiTyping.setText("AI печатает");

        // Анимация точек
        handler.postDelayed(new Runnable() {
            int dots = 0;
            @Override
            public void run() {
                if (tvAiTyping.getVisibility() == View.VISIBLE) {
                    dots = (dots + 1) % 4;
                    String text = "AI печатает";
                    for (int i = 0; i < dots; i++) {
                        text += ".";
                    }
                    tvAiTyping.setText(text);
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    private void removeTypingIndicator() {
        tvAiTyping.setVisibility(View.GONE);
    }

    private void clearChat() {
        if (currentUserId != -1) {
            dbHelper.deleteUserChatMessages(currentUserId);
        }

        messages.clear();
        chatAdapter.notifyDataSetChanged();
        addWelcomeMessage();
        quickActionsContainer.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Чат очищен", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}