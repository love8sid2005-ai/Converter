package com.example.currencyconverter.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

public class SparklineChartView extends View {

    private float[] dataPoints;
    private ValueAnimator animator;
    private float animationProgress = 0f;
    private boolean isRising = true;
    private int chartColor;

    private Paint linePaint;
    private Paint fillPaint;
    private Path path;
    private Path fillPath;

    public SparklineChartView(Context context) {
        super(context);
        init();
    }

    public SparklineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SparklineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        path = new Path();
        fillPath = new Path();
    }

    public void setChartData(float[] data, boolean isRising, int color) {
        this.dataPoints = data;
        this.isRising = isRising;
        this.chartColor = color;

        linePaint.setColor(color);

        fillPaint.setShader(new LinearGradient(0, 0, 0, getHeight(),
                color,
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP));

        startAnimation();
    }

    private void startAnimation() {
        if (animator != null) {
            animator.cancel();
        }

        animationProgress = 0f;

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints == null || dataPoints.length < 2) return;

        float width = getWidth();
        float height = getHeight();
        float stepX = width / (dataPoints.length - 1);

        float max = dataPoints[0];
        float min = dataPoints[0];
        for (float v : dataPoints) {
            if (v > max) max = v;
            if (v < min) min = v;
        }

        float range = (max == min) ? 1f : max - min;

        path.reset();
        fillPath.reset();

        for (int i = 0; i < dataPoints.length; i++) {
            float x = i * stepX;
            float y = height - ((dataPoints[i] - min) / range) * height * 0.7f - height * 0.15f;

            if (i == 0) {
                path.moveTo(x, y);
                fillPath.moveTo(x, y);
            } else {
                path.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
        }

        canvas.drawPath(path, linePaint);

        fillPath.lineTo(width, height);
        fillPath.lineTo(0, height);
        fillPath.close();
        canvas.drawPath(fillPath, fillPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}