package com.example.currencyconverter.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class MiniChartView extends View {
    private int[] dataPoints;
    private Paint linePaint;
    private Paint fillPaint;
    private Path path;
    private boolean isRising;

    public MiniChartView(Context context) {
        super(context);
        init();
    }

    public MiniChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);
        linePaint.setAntiAlias(true);

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAlpha(50);
        fillPaint.setAntiAlias(true);

        path = new Path();
    }

    public void setChartData(int[] data, boolean isRising, int color) {
        this.dataPoints = data;
        this.isRising = isRising;
        linePaint.setColor(color);
        fillPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints == null || dataPoints.length < 2) return;

        float width = getWidth();
        float height = getHeight();
        float stepX = width / (dataPoints.length - 1);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int value : dataPoints) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        float range = max - min;
        if (range == 0) range = 1;

        path.reset();
        for (int i = 0; i < dataPoints.length; i++) {
            float x = i * stepX;
            float y = height - ((dataPoints[i] - min) / range) * height * 0.7f - height * 0.15f;

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        canvas.drawPath(path, linePaint);

        Path fillPath = new Path(path);
        fillPath.lineTo(width, height);
        fillPath.lineTo(0, height);
        fillPath.close();
        canvas.drawPath(fillPath, fillPaint);
    }
}