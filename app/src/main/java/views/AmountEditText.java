package com.example.currencyconverter.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.example.currencyconverter.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AmountEditText extends AppCompatEditText {

    private Paint borderPaint;
    private Paint glowPaint;
    private boolean isFocused = false;
    private float glowAlpha = 0f;
    private ValueAnimator glowAnimator;
    private DecimalFormat decimalFormat;
    private String lastFormattedValue = "";
    private OnAmountChangeListener amountChangeListener;

    public interface OnAmountChangeListener {
        void onAmountChanged(double amount);
    }

    public AmountEditText(Context context) {
        super(context);
        init();
    }

    public AmountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Настройка ввода только цифр и точки
        setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        // Формат для отображения
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("#,##0.00", symbols);

        // Подсказка
        setHint("0.00");
        setHintTextColor(ContextCompat.getColor(getContext(), R.color.text_disabled));

        // Цвет текста
        setTextColor(ContextCompat.getColor(getContext(), R.color.accent));

        // Размер шрифта
        setTextSize(24);

        // Жирный шрифт
        setTypeface(getTypeface(), android.graphics.Typeface.BOLD);

        // Шрифт
        setTypeface(android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.BOLD));

        // Фон
        setBackgroundColor(Color.TRANSPARENT);

        // Отступы
        setPadding(60, 16, 60, 16);

        // Гравитация
        setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.CENTER_HORIZONTAL);

        // Курсор
        setCursorVisible(true);

        // Автоматическое форматирование
        addTextChangedListener(new TextWatcher() {
            private String lastText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.equals(lastText)) return;

                lastText = text;

                // Убираем форматирование для редактирования
                String cleanText = text.replaceAll("[^0-9.]", "");

                try {
                    if (!cleanText.isEmpty() && !cleanText.equals(".")) {
                        double value = Double.parseDouble(cleanText);
                        lastFormattedValue = decimalFormat.format(value);

                        // Временно убираем слушатель, чтобы избежать рекурсии
                        removeTextChangedListener(this);
                        setText(lastFormattedValue);
                        setSelection(lastFormattedValue.length());
                        addTextChangedListener(this);

                        // Уведомляем слушателя
                        if (amountChangeListener != null) {
                            amountChangeListener.onAmountChanged(value);
                        }
                    } else {
                        if (amountChangeListener != null) {
                            amountChangeListener.onAmountChanged(0);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем
                }
            }
        });

        // Анимация при фокусе
        setOnFocusChangeListener((v, hasFocus) -> {
            isFocused = hasFocus;
            animateGlow(hasFocus);

            if (hasFocus) {
                // Вибрация при получении фокуса
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                // Выделяем весь текст
                selectAll();
            } else {
                // При потере фокуса форматируем
                formatOnBlur();
            }
        });

        // Инициализация рисования
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);
        borderPaint.setColor(ContextCompat.getColor(getContext(), R.color.accent));

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.FILL);
        glowPaint.setColor(ContextCompat.getColor(getContext(), R.color.accent));
        glowPaint.setAlpha(0);
    }

    private void formatOnBlur() {
        String text = getText().toString();
        if (text.isEmpty() || text.equals(".")) {
            setText("");
            return;
        }

        try {
            String cleanText = text.replaceAll("[^0-9.]", "");
            if (!cleanText.isEmpty()) {
                double value = Double.parseDouble(cleanText);
                setText(decimalFormat.format(value));
            }
        } catch (NumberFormatException e) {
            // Игнорируем
        }
    }

    private void animateGlow(boolean show) {
        if (glowAnimator != null) {
            glowAnimator.cancel();
        }

        float targetAlpha = show ? 0.15f : 0f;
        float startAlpha = glowAlpha;

        glowAnimator = ValueAnimator.ofFloat(startAlpha, targetAlpha);
        glowAnimator.setDuration(300);
        glowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        glowAnimator.addUpdateListener(animation -> {
            glowAlpha = (float) animation.getAnimatedValue();
            invalidate();
        });
        glowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    glowAlpha = 0;
                    invalidate();
                }
            }
        });
        glowAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Рисуем свечение
        if (glowAlpha > 0) {
            glowPaint.setAlpha((int) (glowAlpha * 255));
            canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 16, 16, glowPaint);
        }

        // Рисуем границу
        borderPaint.setColor(ContextCompat.getColor(getContext(), isFocused ? R.color.accent : R.color.card_border));
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 16, 16, borderPaint);

        // Рисуем содержимое
        super.onDraw(canvas);

        // Рисуем символ доллара
        Paint dollarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dollarPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        dollarPaint.setTextSize(20);
        dollarPaint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        canvas.drawText("$", 20, getHeight() / 2 + 8, dollarPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        }
        return super.onTouchEvent(event);
    }

    public void setAmount(double amount) {
        setText(decimalFormat.format(amount));
    }

    public double getAmount() {
        String text = getText().toString();
        if (text.isEmpty()) return 0;

        try {
            return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setOnAmountChangeListener(OnAmountChangeListener listener) {
        this.amountChangeListener = listener;
    }
}