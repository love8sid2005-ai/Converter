package com.example.currencyconverter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.adapters.ChatAdapter;
import com.example.currencyconverter.database.DatabaseHelper;
import com.example.currencyconverter.models.ChatMessage;
import com.example.currencyconverter.models.FaqItem;
import com.example.currencyconverter.models.User;
import com.example.currencyconverter.services.AISupportService;
import com.example.currencyconverter.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ChatSupportActivity extends BaseActivity {

    private static final String TAG = "ChatSupport";

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageView backButton, clearChatButton;
    private LinearLayout faqContainer;
    private HorizontalScrollView faqScrollView;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private AISupportService aiService;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int currentUserId;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_chat_support;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем БД и сессию
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        initializeViews();
        setupChat();
        setupFaq();
        setupClickListeners();

        // Загружаем сохраненные сообщения
        loadChatHistory();
    }

    private void initializeViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        clearChatButton = findViewById(R.id.clearChatButton);
        faqContainer = findViewById(R.id.faqContainer);
        faqScrollView = findViewById(R.id.faqScrollView);
    }

    private void setupChat() {
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        aiService = new AISupportService();
        Log.d(TAG, "Chat и AI сервис инициализированы");
    }

    // Загрузка истории из БД для текущего пользователя
    private void loadChatHistory() {
        messages.clear();

        if (currentUserId == -1) {
            Log.e(TAG, "Пользователь не авторизован");
            addWelcomeMessage();
            return;
        }

        List<ChatMessage> savedMessages = dbHelper.getUserChatMessages(currentUserId);

        if (savedMessages.isEmpty()) {
            // Если истории нет - добавляем приветствие
            addWelcomeMessage();
        } else {
            // Загружаем сохраненные сообщения
            messages.addAll(savedMessages);
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.smoothScrollToPosition(messages.size() - 1);

            // Если сообщений много - скрываем FAQ
            if (messages.size() > 1) {
                faqScrollView.setVisibility(View.GONE);
            }

            Log.d(TAG, "Загружено сообщений: " + messages.size());
        }
    }

    private void setupFaq() {
        List<FaqItem> popularFaqs = aiService.getPopularFaqs();

        for (FaqItem faq : popularFaqs) {
            View faqChip = getLayoutInflater().inflate(R.layout.item_faq, faqContainer, false);
            TextView questionText = faqChip.findViewById(R.id.questionText);
            questionText.setText(faq.getQuestion());

            faqChip.setOnClickListener(v -> {
                messageInput.setText(faq.getQuestion());
                sendButton.performClick();
            });

            faqContainer.addView(faqChip);
        }
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();

                if (!TextUtils.isEmpty(message)) {
                    // Создаем сообщение пользователя
                    ChatMessage userMessage = new ChatMessage(message, true);

                    // Сохраняем в БД для текущего пользователя
                    if (currentUserId != -1) {
                        long id = dbHelper.addChatMessage(currentUserId, userMessage);
                        userMessage.setId((int) id);
                    }

                    messages.add(userMessage);
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.smoothScrollToPosition(messages.size() - 1);

                    messageInput.setText("");

                    if (messages.size() > 1) {
                        faqScrollView.setVisibility(View.GONE);
                    }

                    // Показываем индикатор печати
                    final ChatMessage typingIndicator = new ChatMessage("...", false);
                    messages.add(typingIndicator);
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.smoothScrollToPosition(messages.size() - 1);

                    // Ответ AI с задержкой
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Удаляем индикатор
                            messages.remove(typingIndicator);
                            chatAdapter.notifyItemRemoved(messages.size());

                            // Получаем ответ
                            String response = aiService.getAIResponse(message);
                            ChatMessage aiMessage = new ChatMessage(response, false);

                            // Сохраняем в БД
                            if (currentUserId != -1) {
                                long aiId = dbHelper.addChatMessage(currentUserId, aiMessage);
                                aiMessage.setId((int) aiId);
                            }

                            messages.add(aiMessage);
                            chatAdapter.notifyItemInserted(messages.size() - 1);
                            chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
                        }
                    }, 1500);

                } else {
                    Toast.makeText(ChatSupportActivity.this, "Введите сообщение", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(v -> finish());

        clearChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Очищаем чат для текущего пользователя
                if (currentUserId != -1) {
                    dbHelper.deleteUserChatMessages(currentUserId);
                }

                messages.clear();
                chatAdapter.notifyDataSetChanged();
                addWelcomeMessage();
                faqScrollView.setVisibility(View.VISIBLE);
                Toast.makeText(ChatSupportActivity.this, "Чат очищен", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addWelcomeMessage() {
        String welcome = "Здравствуйте! Я AI-ассистент Currency Converter. Задайте любой вопрос о конвертации валют, кошельке, безопасности или выберите популярный вопрос ниже. Я отвечу мгновенно!";

        ChatMessage welcomeMessage = new ChatMessage(welcome, false);

        // Сохраняем в БД если пользователь авторизован
        if (currentUserId != -1) {
            long id = dbHelper.addChatMessage(currentUserId, welcomeMessage);
            welcomeMessage.setId((int) id);
        }

        messages.add(welcomeMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}