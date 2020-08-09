package com.makienkovs.rounds2048;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CellBack extends View {
    private Paint paint;

    public CellBack(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = this.getWidth();
        float height = this.getHeight();

        @SuppressLint("DrawAllocation")
        RectF rectf = new RectF(width * 0.05f, height * 0.05f, width * 0.95f, height * 0.95f);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.color0));
        canvas.drawRoundRect(rectf, width * 0.5f, height * 0.5f, paint);
    }
}
