package kr.ac.kpu.game.s2017182016.jumpman.framework.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;

/** Small corner button that opens the options scene. */
public class OptionsEntryButton implements GameObject {
    private final RectF rect = new RectF();
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final boolean leftSide;

    public OptionsEntryButton() {
        this(false);
    }

    public OptionsEntryButton(boolean leftSide) {
        this.leftSide = leftSide;
        fill.setColor(0xAA222230);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeWidth(2.5f);
        border.setColor(0xCCD0D0E8);
        text.setColor(Color.WHITE);
        text.setTextAlign(Paint.Align.CENTER);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        layout();
    }

    public void layout() {
        float w = GameView.view.getWidth();
        float h = GameView.view.getHeight();
        float bw = w * 0.16f;
        float bh = h * 0.07f;
        float m = h * 0.02f;
        if (leftSide) {
            rect.set(m, m, m + bw, m + bh);
        } else {
            rect.set(w - bw - m, m, w - m, m + bh);
        }
        text.setTextSize(bh * 0.42f);
    }

    public RectF getRect() {
        return rect;
    }

    public boolean hit(float x, float y) {
        layout();
        return rect.contains(x, y);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        layout();
        canvas.drawRoundRect(rect, 10, 10, fill);
        canvas.drawRoundRect(rect, 10, 10, border);
        canvas.drawText("옵션", rect.centerX(),
                rect.centerY() - (text.ascent() + text.descent()) / 2f, text);
    }
}
