package com.example.currencyconverter.services;

import com.example.currencyconverter.models.FaqItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AISupportService {

    private Map<String, String> knowledgeBase;
    private Random random = new Random();

    public AISupportService() {
        knowledgeBase = new HashMap<>();
        initializeKnowledgeBase();
    }

    private void initializeKnowledgeBase() {
        // ========== ВАЛЮТНЫЕ ОПЕРАЦИИ ==========
        knowledgeBase.put("как конвертировать валюту",
                "Для конвертации валюты перейдите на вкладку 'Конвертер'. Введите сумму, выберите исходную и целевую валюту, затем нажмите 'CONVERT'.");

        knowledgeBase.put("какой курс доллара",
                "Актуальный курс USD: 1 USD = 90.5 RUB. Курс обновляется в реальном времени.");

        knowledgeBase.put("какой курс евро",
                "Актуальный курс EUR: 1 EUR = 98.2 RUB. Данные обновляются каждые 5 минут.");

        knowledgeBase.put("как поменять рубли на доллары",
                "В разделе 'Конвертер' выберите RUB в поле FROM и USD в поле TO. Введите сумму в рублях и нажмите 'CONVERT'.");

        knowledgeBase.put("почему курс меняется",
                "Курсы валют меняются в реальном времени из-за рыночных условий и торгов на биржах.");

        knowledgeBase.put("как часто обновляются курсы",
                "Мы обновляем курсы валют каждые 5 минут от надежных финансовых источников.");

        knowledgeBase.put("какие валюты поддерживаются",
                "Приложение поддерживает USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY, RUB и другие валюты.");

        // ========== КОШЕЛЕК И БАЛАНС ==========
        knowledgeBase.put("как пополнить кошелек",
                "В разделе 'Кошелек' нажмите кнопку 'Пополнить'. Доступны карты, банковский перевод и криптовалюта.");

        knowledgeBase.put("как вывести деньги",
                "Для вывода перейдите в 'Кошелек', нажмите 'Вывести' и выберите способ получения.");

        knowledgeBase.put("где посмотреть баланс",
                "Общий баланс отображается на главном экране. Детализация в разделе 'Кошелек'.");

        knowledgeBase.put("как добавить карту",
                "Перейдите в 'Кошелек', нажмите на иконку карты в быстрых действиях.");

        knowledgeBase.put("безопасно ли хранить деньги",
                "Мы используем банковский уровень шифрования и двухфакторную аутентификацию.");

        knowledgeBase.put("как верифицировать аккаунт",
                "Перейдите в настройки профиля, выберите 'Верификация' и загрузите фото паспорта.");

        // ========== ТРАНЗАКЦИИ ==========
        knowledgeBase.put("где посмотреть историю операций",
                "История транзакций доступна на главном экране и в разделе 'Кошелек'.");

        knowledgeBase.put("как отменить перевод",
                "Отменить перевод можно в течение 30 секунд после отправки в истории операций.");

        knowledgeBase.put("почему транзакция зависла",
                "Обычно транзакция обрабатывается до 15 минут. Если дольше - напишите в поддержку.");

        // ========== БЕЗОПАСНОСТЬ ==========
        knowledgeBase.put("как включить двухфакторку",
                "Профиль → Безопасность → Двухфакторная аутентификация. Выберите SMS или Google Authenticator.");

        knowledgeBase.put("что делать если забыл пароль",
                "На экране входа нажмите 'Забыли пароль?' и следуйте инструкции.");

        knowledgeBase.put("как сменить пароль",
                "В настройках профиля выберите 'Безопасность' → 'Сменить пароль'.");

        knowledgeBase.put("как защитить аккаунт",
                "Используйте сложный пароль, включите двухфакторку и не сообщайте коды никому.");

        knowledgeBase.put("что делать если украли телефон",
                "Немедленно заблокируйте доступ через другой телефон или позвоните в поддержку 24/7.");

        // ========== ТЕХНИЧЕСКИЕ ВОПРОСЫ ==========
        knowledgeBase.put("приложение не открывается",
                "Попробуйте перезапустить устройство или очистить кэш приложения.");

        knowledgeBase.put("как обновить приложение",
                "Новые версии выходят автоматически через Google Play.");

        knowledgeBase.put("как связаться с поддержкой",
                "Вы уже в чате поддержки! Также можете написать на support@currencyconverter.com");

        knowledgeBase.put("работает ли приложение офлайн",
                "Основные функции доступны офлайн. Для актуальных курсов нужен интернет.");

        knowledgeBase.put("можно ли сменить язык",
                "Приложение поддерживает русский и английский языки. Язык меняется автоматически.");

        knowledgeBase.put("как сменить тему",
                "В настройках профиля выберите 'Тема оформления': светлая, темная или как в системе.");
    }

    public String getAIResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Пожалуйста, введите сообщение.";
        }

        String message = userMessage.toLowerCase().trim();

        // Проверяем приветствия
        if (message.contains("привет") || message.contains("здравствуйте") || message.contains("добрый")) {
            return getRandomGreeting();
        }

        // Проверяем благодарности
        if (message.contains("спасибо") || message.contains("благодарю")) {
            return getRandomThankYou();
        }

        // Проверяем точные совпадения
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (message.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Проверяем по ключевым словам
        if (message.contains("курс") || message.contains("сколько стоит")) {
            if (message.contains("доллар") || message.contains("usd")) {
                return knowledgeBase.get("какой курс доллара");
            }
            if (message.contains("евро") || message.contains("eur")) {
                return knowledgeBase.get("какой курс евро");
            }
            return "Какую валюту вас интересует? USD, EUR, GBP или другая?";
        }

        if (message.contains("конверт") || message.contains("обмен")) {
            return knowledgeBase.get("как конвертировать валюту");
        }

        if (message.contains("кошелек") || message.contains("баланс")) {
            return knowledgeBase.get("где посмотреть баланс");
        }

        if (message.contains("пополн") || message.contains("ввести")) {
            return knowledgeBase.get("как пополнить кошелек");
        }

        if (message.contains("вывест") || message.contains("снять")) {
            return knowledgeBase.get("как вывести деньги");
        }

        if (message.contains("безопасн") || message.contains("защит")) {
            return knowledgeBase.get("безопасно ли хранить деньги");
        }

        if (message.contains("пароль") || message.contains("вход")) {
            return knowledgeBase.get("что делать если забыл пароль");
        }

        // Если не нашли ответ
        return getRandomDefaultResponse();
    }

    private String getRandomGreeting() {
        String[] greetings = {
                "Здравствуйте! Чем могу помочь?",
                "Привет! Рад вас видеть. Задавайте вопросы!",
                "Добрый день! Как я могу вам помочь?",
                "Здравствуйте! Я AI-ассистент. Чем могу быть полезен?"
        };
        return greetings[random.nextInt(greetings.length)];
    }

    private String getRandomThankYou() {
        String[] thanks = {
                "Пожалуйста! Обращайтесь, если будут еще вопросы.",
                "Всегда рад помочь!",
                "На здоровье! Хорошего дня!",
                "Обращайтесь, я всегда на связи!"
        };
        return thanks[random.nextInt(thanks.length)];
    }

    private String getRandomDefaultResponse() {
        String[] defaults = {
                "Извините, я не совсем понял вопрос. Можете переформулировать?",
                "Попробуйте задать вопрос иначе или выберите из популярных вопросов.",
                "Я не нашел точного ответа. Уточните, пожалуйста.",
                "Возможно, вы имели в виду что-то другое? Попробуйте написать кратко."
        };
        return defaults[random.nextInt(defaults.length)];
    }

    public List<FaqItem> getPopularFaqs() {
        List<FaqItem> faqs = new ArrayList<>();
        faqs.add(new FaqItem("Как конвертировать валюту?", knowledgeBase.get("как конвертировать валюту")));
        faqs.add(new FaqItem("Какой курс доллара?", knowledgeBase.get("какой курс доллара")));
        faqs.add(new FaqItem("Как пополнить кошелек?", knowledgeBase.get("как пополнить кошелек")));
        faqs.add(new FaqItem("Как вывести деньги?", knowledgeBase.get("как вывести деньги")));
        faqs.add(new FaqItem("Как включить двухфакторку?", knowledgeBase.get("как включить двухфакторку")));
        faqs.add(new FaqItem("Безопасно ли хранить деньги?", knowledgeBase.get("безопасно ли хранить деньги")));
        faqs.add(new FaqItem("Как часто обновляются курсы?", knowledgeBase.get("как часто обновляются курсы")));
        faqs.add(new FaqItem("Что делать если забыл пароль?", knowledgeBase.get("что делать если забыл пароль")));
        faqs.add(new FaqItem("Как связаться с поддержкой?", knowledgeBase.get("как связаться с поддержкой")));
        return faqs;
    }
}