package com.rohit.mybank.activities.qr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * =========================================================
 * QR SCANNER OVERLAY
 * =========================================================
 *
 * Draws the visual scanning frame over the CameraX preview.
 *
 * Design:
 * - Dark transparent outside area
 * - Blue corner brackets
 * - Rounded scanning area
 * - Clean banking-app appearance
 *
 * =========================================================
 */
public class QrScannerOverlayView extends View {

    // =========================================================
    // COLORS
    // =========================================================

    private static final int OVERLAY_COLOR =
            Color.argb(125, 0, 0, 0);

    private static final int FRAME_COLOR =
            Color.rgb(33, 150, 243);

    // =========================================================
    // PAINT
    // =========================================================

    private final Paint overlayPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint framePaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    // =========================================================
    // FRAME
    // =========================================================

    private final RectF frameRect =
            new RectF();

    private float cornerLength;
    private float cornerRadius;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public QrScannerOverlayView(Context context) {
        super(context);
        initialize();
    }

    public QrScannerOverlayView(
            Context context,
            @Nullable AttributeSet attrs) {

        super(context, attrs);
        initialize();
    }

    public QrScannerOverlayView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        initialize();
    }

    // =========================================================
    // INITIALIZE
    // =========================================================

    private void initialize() {

        setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                null
        );

        overlayPaint.setStyle(
                Paint.Style.FILL
        );

        overlayPaint.setColor(
                OVERLAY_COLOR
        );

        framePaint.setStyle(
                Paint.Style.STROKE
        );

        framePaint.setColor(
                FRAME_COLOR
        );

        framePaint.setStrokeWidth(
                dp(4)
        );

        framePaint.setStrokeCap(
                Paint.Cap.ROUND
        );

        cornerLength =
                dp(42);

        cornerRadius =
                dp(22);
    }

    // =========================================================
    // DRAW
    // =========================================================

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width =
                getWidth();

        float height =
                getHeight();

        // -----------------------------------------------------
        // FRAME SIZE
        // -----------------------------------------------------

        float frameSize =
                Math.min(
                        width * 0.76f,
                        height * 0.48f
                );

        float left =
                (width - frameSize) / 2f;

        float top =
                (height - frameSize) / 2f;

        float right =
                left + frameSize;

        float bottom =
                top + frameSize;

        frameRect.set(
                left,
                top,
                right,
                bottom
        );

        // -----------------------------------------------------
        // DARK OUTSIDE AREA
        // -----------------------------------------------------

        canvas.drawColor(
                Color.TRANSPARENT
        );

        // Top
        canvas.drawRect(
                0,
                0,
                width,
                top,
                overlayPaint
        );

        // Bottom
        canvas.drawRect(
                0,
                bottom,
                width,
                height,
                overlayPaint
        );

        // Left
        canvas.drawRect(
                0,
                top,
                left,
                bottom,
                overlayPaint
        );

        // Right
        canvas.drawRect(
                right,
                top,
                width,
                bottom,
                overlayPaint
        );

        // -----------------------------------------------------
        // CORNER BRACKETS
        // -----------------------------------------------------

        drawTopLeft(
                canvas,
                left,
                top
        );

        drawTopRight(
                canvas,
                right,
                top
        );

        drawBottomLeft(
                canvas,
                left,
                bottom
        );

        drawBottomRight(
                canvas,
                right,
                bottom
        );
    }

    // =========================================================
    // TOP LEFT
    // =========================================================

    private void drawTopLeft(
            Canvas canvas,
            float left,
            float top) {

        canvas.drawLine(
                left,
                top + cornerLength,
                left,
                top + cornerRadius,
                framePaint
        );

        canvas.drawLine(
                left + cornerRadius,
                top,
                left + cornerLength,
                top,
                framePaint
        );
    }

    // =========================================================
    // TOP RIGHT
    // =========================================================

    private void drawTopRight(
            Canvas canvas,
            float right,
            float top) {

        canvas.drawLine(
                right,
                top + cornerRadius,
                right,
                top + cornerLength,
                framePaint
        );

        canvas.drawLine(
                right - cornerLength,
                top,
                right - cornerRadius,
                top,
                framePaint
        );
    }

    // =========================================================
    // BOTTOM LEFT
    // =========================================================

    private void drawBottomLeft(
            Canvas canvas,
            float left,
            float bottom) {

        canvas.drawLine(
                left,
                bottom - cornerLength,
                left,
                bottom - cornerRadius,
                framePaint
        );

        canvas.drawLine(
                left + cornerRadius,
                bottom,
                left + cornerLength,
                bottom,
                framePaint
        );
    }

    // =========================================================
    // BOTTOM RIGHT
    // =========================================================

    private void drawBottomRight(
            Canvas canvas,
            float right,
            float bottom) {

        canvas.drawLine(
                right,
                bottom - cornerLength,
                right,
                bottom - cornerRadius,
                framePaint
        );

        canvas.drawLine(
                right - cornerLength,
                bottom,
                right - cornerRadius,
                bottom,
                framePaint
        );
    }

    // =========================================================
    // DP
    // =========================================================

    private float dp(float value) {

        return value *
                getResources()
                        .getDisplayMetrics()
                        .density;
    }
}