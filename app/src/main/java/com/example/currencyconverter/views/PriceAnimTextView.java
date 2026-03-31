package com.example.currencyconverter.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.currencyconverter.R;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceAnimTextView extends AppCompatTextView {

    private ValueAnimator animator;
    private double targetValue = 0.0;
    private double currentValue = 0.0;
    private NumberFormat formatter;

    public PriceAnimTextView(Context context) {
        super(context);
        init();
    }

    public PriceAnimTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PriceAnimTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
    }

    public void animatePrice(double newPrice) {
        targetValue = newPrice;

        if (animator != null) {
            animator.cancel();
        }

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                currentValue = currentValue + (targetValue - currentValue) * fraction;
                setText(formatter.format(currentValue));

                if (fraction < 1f) {
                    int color;
                    if (targetValue > currentValue) {
                        color = getContext().getColor(R.color.bybit_success);
                    } else if (targetValue < currentValue) {
                        color = getContext().getColor(R.color.bybit_error);
                    } else {
                        color = getContext().getColor(R.color.bybit_text_primary);
                    }
                    setTextColor(color);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTextColor(getContext().getColor(R.color.bybit_text_primary));
                        }
                    }, 600);
                }
            }
        });
        animator.start();
    }

    public void setPrice(double price) {
        currentValue = price;
        targetValue = price;
        setText(formatter.format(price));
        setTextColor(getContext().getColor(R.color.bybit_text_primary));
    }
}