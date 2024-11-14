package com.example.dailybite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {

    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private int progress = 0;
    private int maxProgress = 1000; // Default max progress (can be updated dynamically)

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint for the background circle
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE0E0E0); // Gray color for the background
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(30); // Adjust stroke width for appearance
        backgroundPaint.setAntiAlias(true);

        // Paint for the foreground (progress) circle
        foregroundPaint = new Paint();
        foregroundPaint.setColor(0xFF00C853); // Green color for progress
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(30); // Adjust stroke width for appearance
        foregroundPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 15; // Adjust for padding

        // Draw the background circle
        canvas.drawCircle(width / 2, height / 2, radius, backgroundPaint);

        // Calculate the sweep angle for the progress
        float sweepAngle = 360f * progress / maxProgress;

        // Draw the foreground (progress) arc
        canvas.drawArc(
                width / 2 - radius,
                height / 2 - radius,
                width / 2 + radius,
                height / 2 + radius,
                -90, // Start from top (12 o'clock)
                sweepAngle,
                false,
                foregroundPaint
        );
    }

    // Method to set progress
    public void setProgress(int progress) {
        this.progress = Math.min(progress, maxProgress); // Ensure progress doesn't exceed maxProgress
        invalidate(); // Redraw the view when progress is updated
    }

    // Method to set max progress (total step count target)
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
}
