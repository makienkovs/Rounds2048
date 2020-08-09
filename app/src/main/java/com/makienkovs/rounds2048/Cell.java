package com.makienkovs.rounds2048;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Cell extends View {
    private int val;
    private Paint paint;
    private Typeface tf;

    public void setVal(int val) {
        this.val = val;
    }

    public Cell(Context context) {
        super(context);
        init(context);
    }

    public Cell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        tf = Typeface.createFromAsset(context.getAssets(), "NotoSansBold.ttf");
        val = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = this.getWidth();
        float height = this.getHeight();

        @SuppressLint("DrawAllocation")
        RectF rectf = new RectF(width * 0.05f, height * 0.05f, width * 0.95f, height * 0.95f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(tf);
        paint.setAntiAlias(true);

        switch (val) {
            case 1:
                paint.setColor(getResources().getColor(R.color.color1));
                break;
            case 2:
                paint.setColor(getResources().getColor(R.color.color2));
                break;
            case 4:
                paint.setColor(getResources().getColor(R.color.color4));
                break;
            case 8:
                paint.setColor(getResources().getColor(R.color.color8));
                break;
            case 16:
                paint.setColor(getResources().getColor(R.color.color16));
                break;
            case 32:
                paint.setColor(getResources().getColor(R.color.color32));
                break;
            case 64:
                paint.setColor(getResources().getColor(R.color.color64));
                break;
            case 128:
                paint.setColor(getResources().getColor(R.color.color128));
                break;
            case 256:
                paint.setColor(getResources().getColor(R.color.color256));
                break;
            case 512:
                paint.setColor(getResources().getColor(R.color.color512));
                break;
            case 1024:
                paint.setColor(getResources().getColor(R.color.color1024));
                break;
            case 2048:
                paint.setColor(getResources().getColor(R.color.color2048));
                break;
            case 4096:
                paint.setColor(getResources().getColor(R.color.color4096));
                break;
            case 8192:
                paint.setColor(getResources().getColor(R.color.color8192));
                break;
            case 16384:
                paint.setColor(getResources().getColor(R.color.color16384));
                break;
            case 32768:
                paint.setColor(getResources().getColor(R.color.color32768));
                break;
            case 65536:
                paint.setColor(getResources().getColor(R.color.color65536));
                break;
            case 131072:
                paint.setColor(getResources().getColor(R.color.color131072));
                break;
            default:
                paint.setColor(getResources().getColor(R.color.colorTransparent));
        }

        canvas.drawRoundRect(rectf, width * 0.5f, height * 0.5f, paint);

        switch (val) {
            case 1:
            case 2:
            case 4:
                paint.setColor(getResources().getColor(R.color.colorTextDark));
                paint.setTextSize(height * 0.5f);
                break;
            case 8:
            case 16:
            case 32:
            case 64:
                paint.setColor(getResources().getColor(R.color.colorTextLight));
                paint.setTextSize(height * 0.5f);
                break;
            case 128:
            case 256:
            case 512:
                paint.setColor(getResources().getColor(R.color.colorTextLight));
                paint.setTextSize(height * 0.42f);
                break;
            case 1024:
            case 2048:
            case 4096:
            case 8192:
                paint.setColor(getResources().getColor(R.color.colorTextLight));
                paint.setTextSize(height * 0.32f);
                break;
            case 16384:
            case 32768:
            case 65536:
                paint.setColor(getResources().getColor(R.color.colorTextLight));
                paint.setTextSize(height * 0.26f);
                break;
            case 131072:
                paint.setColor(getResources().getColor(R.color.colorTextLight));
                paint.setTextSize(height * 0.22f);
                break;
            default:
                paint.setColor(getResources().getColor(R.color.colorTransparent));
        }

        float xPos = width / 2;
        float yPos = height / 2 - ((paint.descent() + paint.ascent()) / 2);

        canvas.drawText("" + val, xPos, yPos, paint);
    }

    public void animSet() {
        this.animate()
                .setDuration(Game.DELAY)
                .scaleX(1.1f)
                .scaleY(1.1f)
                .withEndAction(() -> this.animate().scaleX(1f).scaleY(1f).setDuration(Game.DELAY).start())
                .start();
    }

    public void animAppear() {
        this.animate()
                .setDuration(0)
                .scaleX(0)
                .scaleY(0)
                .withEndAction(() -> this.animate().scaleX(1f).scaleY(1f).setDuration(Game.DELAY).start())
                .start();
    }

    public void animUp(int cells) {
        final float height = this.getHeight();
        this.animate()
                .setDuration(Game.DELAY)
                .translationY(-height * cells)
                .withEndAction(this::animCancel)
                .start();
    }

    public void animDown(int cells) {
        final float height = this.getHeight();
        this.animate()
                .setDuration(Game.DELAY)
                .translationY(height * cells)
                .withEndAction(this::animCancel)
                .start();
    }

    public void animLeft(int cells) {
        final float width = this.getWidth();
        this.animate()
                .setDuration(Game.DELAY)
                .translationX(-width * cells)
                .withEndAction(this::animCancel)
                .start();
    }

    public void animRight(int cells) {
        final float width = this.getWidth();
        this.animate()
                .setDuration(Game.DELAY)
                .translationX(width * cells)
                .withEndAction(this::animCancel)
                .start();
    }

    public void animCancel() {
        this.animate()
                .setDuration(0)
                .alpha(0)
                .translationY(0)
                .translationX(0)
                .withEndAction(()->this.animate().setDuration(0).alpha(1).start())
                .start();
        this.invalidate();
    }
}