package com.creditcard.simplevalidation.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

/**
 * A {@link android.text.style.ReplacementSpan} used for spacing in {@link android.widget.EditText}
 * to space things out. Adds ' 's
 */
public class SpaceSpan extends ReplacementSpan {

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        float padding = paint.measureText(" ", 0, 1);
        float textSize = paint.measureText(text, start, end);
        return (int) (padding + textSize);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, @NonNull Paint paint) {
        canvas.drawText(text.subSequence(start, end) + " ", x, y, paint);
    }
}
