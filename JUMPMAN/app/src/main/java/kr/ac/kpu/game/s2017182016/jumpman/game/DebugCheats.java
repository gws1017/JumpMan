package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.GameSettings;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;

/**
 * On-screen map warp buttons (right edge). Shown when Options → 치트키 ON.
 */
public class DebugCheats implements GameObject {
    public final RectF upRect = new RectF();
    public final RectF downRect = new RectF();
    public final RectF topRect = new RectF();
    private final Paint fillPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint labelPaint = new Paint();
    private int currentScreen = 1;

    public DebugCheats() {
        fillPaint.setColor(0x99000000);
        fillPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        labelPaint.setColor(0xCCFFFF00);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        layout();
    }

    public void layout() {
        float w = GameView.view.getWidth();
        float h = GameView.view.getHeight();
        float bw = w * 0.12f;
        float bh = h * 0.12f;
        float m = h * 0.03f;
        float x = w - bw - m;
        upRect.set(x, m + bh + m, x + bw, m + bh + m + bh);
        topRect.set(x, m, x + bw, m + bh);
        downRect.set(x, h - m - bh, x + bw, h - m);
        textPaint.setTextSize(bh * 0.35f);
        labelPaint.setTextSize(bh * 0.22f);
    }

    public void setCurrentScreen(int screen) {
        currentScreen = screen;
    }

    public enum Action {
        NONE, UP, DOWN, TOP
    }

    public Action hit(float x, float y) {
        if (!isActive()) {
            return Action.NONE;
        }
        if (topRect.contains(x, y)) {
            return Action.TOP;
        }
        if (upRect.contains(x, y)) {
            return Action.UP;
        }
        if (downRect.contains(x, y)) {
            return Action.DOWN;
        }
        return Action.NONE;
    }

    public static boolean isActive() {
        return GameSettings.get() != null && GameSettings.get().isCheatEnabled();
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isActive()) {
            return;
        }
        if (MainScene.mg != null) {
            currentScreen = MainScene.mg.num;
        }
        drawButton(canvas, topRect, "TOP");
        drawButton(canvas, upRect, "▲");
        drawButton(canvas, downRect, "▼");
        canvas.drawText("S" + currentScreen, upRect.centerX(),
                upRect.bottom + labelPaint.getTextSize(), labelPaint);
    }

    private void drawButton(Canvas canvas, RectF rect, String label) {
        canvas.drawRoundRect(rect, 12, 12, fillPaint);
        canvas.drawText(label, rect.centerX(),
                rect.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint);
    }
}
