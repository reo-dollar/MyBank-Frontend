package com.rohit.mybank.activities.admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.rohit.mybank.R;

/**
 * Native line-chart preview for the Admin Dashboard.
 *
 * IMPORTANT:
 * The displayed curves are illustrative preview data only. They are not
 * real banking history. Connect this view to a backend time-series endpoint
 * before using it to represent actual historical transactions.
 */
public class AdminTransactionTrendChartView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    private final int[] seriesColors;
    private final String[] seriesNames = {
            "Deposits", "Withdrawals", "Transfers", "Payments"
    };

    // Illustrative-only preview points, normalized to the 0..1 chart range.
    private final float[][] previewValues = {
            {0.32f, 0.42f, 0.38f, 0.58f, 0.52f, 0.72f, 0.82f},
            {0.18f, 0.28f, 0.24f, 0.34f, 0.31f, 0.44f, 0.40f},
            {0.12f, 0.20f, 0.34f, 0.30f, 0.48f, 0.45f, 0.60f},
            {0.24f, 0.18f, 0.30f, 0.42f, 0.38f, 0.55f, 0.50f}
    };

    public AdminTransactionTrendChartView(Context context) {
        this(context, null);
    }

    public AdminTransactionTrendChartView(
            Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);

        seriesColors = new int[]{
                ContextCompat.getColor(context, R.color.admin_success),
                ContextCompat.getColor(context, R.color.admin_error),
                ContextCompat.getColor(context, R.color.admin_primary),
                ContextCompat.getColor(context, R.color.admin_transaction)
        };

        setContentDescription(
                "Illustrative line chart preview for deposits, withdrawals, transfers and payments"
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().density;
        float left = 34 * density;
        float right = getWidth() - 12 * density;
        float top = 12 * density;
        float bottom = getHeight() - 28 * density;

        if (right <= left || bottom <= top) {
            return;
        }

        drawGrid(canvas, left, top, right, bottom, density);
        drawPreviewLines(canvas, left, top, right, bottom, density);
        drawXAxisLabels(canvas, left, bottom, right, density);
    }

    private void drawGrid(
            Canvas canvas,
            float left,
            float top,
            float right,
            float bottom,
            float density
    ) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(1f, density));
        paint.setColor(0xFF35465D);

        for (int i = 0; i <= 4; i++) {
            float y = top + (bottom - top) * i / 4f;
            canvas.drawLine(left, y, right, y, paint);
        }

        for (int i = 0; i <= 6; i++) {
            float x = left + (right - left) * i / 6f;
            canvas.drawLine(x, top, x, bottom, paint);
        }

        // Small neutral y-axis labels; no dates or values are fabricated.
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(9 * density);
        paint.setColor(0xFFB9C6D8);

        String[] labels = {"High", "", "", "", "Low"};
        for (int i = 0; i < labels.length; i++) {
            if (!labels[i].isEmpty()) {
                float y = top + (bottom - top) * i / 4f + 3 * density;
                canvas.drawText(labels[i], left - 6 * density, y, paint);
            }
        }
    }

    private void drawPreviewLines(
            Canvas canvas,
            float left,
            float top,
            float right,
            float bottom,
            float density
    ) {
        for (int series = 0; series < previewValues.length; series++) {
            float[] points = previewValues[series];

            path.reset();
            for (int i = 0; i < points.length; i++) {
                float x = left + (right - left) * i / (points.length - 1f);
                float y = bottom - points[i] * (bottom - top);
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.2f * density);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(seriesColors[series]);
            canvas.drawPath(path, paint);

            // Point markers.
            paint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < points.length; i++) {
                float x = left + (right - left) * i / (points.length - 1f);
                float y = bottom - points[i] * (bottom - top);
                canvas.drawCircle(x, y, 2.5f * density, paint);
            }
        }
    }

    private void drawXAxisLabels(
            Canvas canvas,
            float left,
            float bottom,
            float right,
            float density
    ) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(9 * density);
        paint.setColor(0xFFB9C6D8);

        String[] labels = {"1", "2", "3", "4", "5", "6", "7"};
        for (int i = 0; i < labels.length; i++) {
            float x = left + (right - left) * i / (labels.length - 1f);
            canvas.drawText(labels[i], x, getHeight() - 8 * density, paint);
        }
    }
}
