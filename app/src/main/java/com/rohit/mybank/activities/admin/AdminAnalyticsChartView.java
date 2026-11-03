package com.rohit.mybank.activities.admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.rohit.mybank.R;

import java.util.Locale;

/**
 * Lightweight native dashboard charts.
 *
 * Transaction mode displays a donut chart of transaction counts.
 * Financial mode displays horizontal bars for financial totals.
 * Values are supplied by AdminDashboardActivity; this view does not fabricate
 * historical or time-series data.
 */
public class AdminAnalyticsChartView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();

    private final String[] labels = {
            "Deposits", "Withdrawals", "Transfers", "Payments"
    };

    private final int[] colors;

    private double[] values = {0, 0, 0, 0};
    private boolean financialMode = false;

    public AdminAnalyticsChartView(Context context) {
        this(context, null);
    }

    public AdminAnalyticsChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        colors = new int[]{
                ContextCompat.getColor(context, R.color.admin_success),
                ContextCompat.getColor(context, R.color.admin_error),
                ContextCompat.getColor(context, R.color.admin_primary),
                ContextCompat.getColor(context, R.color.admin_transaction)
        };

        paint.setStrokeCap(Paint.Cap.ROUND);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * Updates the donut chart with transaction counts.
     */
    public void setTransactionData(
            double deposits,
            double withdrawals,
            double transfers,
            double payments
    ) {
        financialMode = false;
        values = sanitize(deposits, withdrawals, transfers, payments);
        invalidate();
    }

    /**
     * Updates the horizontal bar chart with financial totals.
     */
    public void setFinancialData(
            double deposits,
            double withdrawals,
            double transfers,
            double payments
    ) {
        financialMode = true;
        values = sanitize(deposits, withdrawals, transfers, payments);
        invalidate();
    }

    private double[] sanitize(double... input) {
        double[] result = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            double value = input[i];

            if (Double.isNaN(value) || Double.isInfinite(value)) {
                result[i] = 0;
            } else {
                result[i] = Math.max(0, value);
            }
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        if (financialMode) {
            drawBars(canvas);
        } else {
            drawDonut(canvas);
        }
    }

    private void drawDonut(Canvas canvas) {
        float density = getResources().getDisplayMetrics().density;

        float centerX = getWidth() * 0.5f;
        float centerY = Math.min(getHeight() * 0.43f, 88 * density);
        float radius = Math.min(getWidth() * 0.28f, 76 * density);
        float stroke = 19 * density;

        // Background ring.
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(0xFF35465D);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Data segments.
        double total = sum();

        if (total > 0) {
            arcBounds.set(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius
            );

            float startAngle = -90f;

            for (int i = 0; i < values.length; i++) {
                if (values[i] <= 0) {
                    continue;
                }

                float sweepAngle = (float) (360.0 * values[i] / total);

                paint.setColor(colors[i]);
                canvas.drawArc(
                        arcBounds,
                        startAngle,
                        sweepAngle,
                        false,
                        paint
                );

                startAngle += sweepAngle;
            }
        }

        // Center labels.
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xFFF4F7FC);
        paint.setTextSize(20 * density);
        canvas.drawText(formatCount(total), centerX, centerY + 3 * density, paint);

        paint.setTextSize(10 * density);
        paint.setColor(0xFFB9C6D8);
        canvas.drawText("TRANSACTIONS", centerX, centerY + 20 * density, paint);
    }

    private void drawBars(Canvas canvas) {
        float density = getResources().getDisplayMetrics().density;

        float left = 8 * density;
        float right = getWidth() - 8 * density;
        float labelHeight = 17 * density;
        float barHeight = 8 * density;
        float rowHeight = (getHeight() - 16 * density) / 4f;

        if (right <= left || rowHeight <= 0) {
            return;
        }

        double max = 0;

        for (double value : values) {
            max = Math.max(max, value);
        }

        for (int i = 0; i < values.length; i++) {
            float y = 8 * density + i * rowHeight;

            // Category label.
            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(12 * density);
            paint.setColor(0xFFD7E0ED);
            canvas.drawText(labels[i], left, y + labelHeight, paint);

            // Value label.
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setColor(colors[i]);
            canvas.drawText(shortAmount(values[i]), right, y + labelHeight, paint);

            // Bar track.
            float top = y + labelHeight + 8 * density;

            paint.setColor(0xFF45556B);
            canvas.drawRoundRect(
                    left,
                    top,
                    right,
                    top + barHeight,
                    barHeight / 2f,
                    barHeight / 2f,
                    paint
            );

            // Filled bar.
            if (max > 0 && values[i] > 0) {
                float availableWidth = right - left;
                float width = (float) (availableWidth * values[i] / max);
                float filledRight = left + Math.min(availableWidth, Math.max(barHeight, width));

                paint.setColor(colors[i]);
                canvas.drawRoundRect(
                        left,
                        top,
                        filledRight,
                        top + barHeight,
                        barHeight / 2f,
                        barHeight / 2f,
                        paint
                );
            }
        }
    }

    private double sum() {
        double total = 0;

        for (double value : values) {
            total += value;
        }

        return total;
    }

    private String formatCount(double value) {
        return String.format(Locale.getDefault(), "%.0f", value);
    }

    private String shortAmount(double value) {
        if (value >= 10_000_000) {
            return String.format(Locale.getDefault(), "₹%.1fCr", value / 10_000_000);
        }

        if (value >= 100_000) {
            return String.format(Locale.getDefault(), "₹%.1fL", value / 100_000);
        }

        if (value >= 1_000) {
            return String.format(Locale.getDefault(), "₹%.1fK", value / 1_000);
        }

        return String.format(Locale.getDefault(), "₹%.0f", value);
    }
}
