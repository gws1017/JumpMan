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
    public final RectF camRect = new RectF();
    private final Paint fillPaint = new Paint();
    private final Paint camOnPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint labelPaint = new Paint();
    private int currentScreen = 1;

    /** 카메라 모드: ON이면 캐릭터 물리(중력·낙하·화면 자동 전환)가 멈추고, UP/DOWN 버튼으로 화면만 이동한다. */
    public static boolean cameraMode = false;

    public DebugCheats() {
        fillPaint.setColor(0x99000000);
        fillPaint.setStyle(Paint.Style.FILL);
        camOnPaint.setColor(0xCC2ECC71);
        camOnPaint.setStyle(Paint.Style.FILL);
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
        camRect.set(x - bw - m, downRect.top, x - m, downRect.bottom);
        textPaint.setTextSize(bh * 0.35f);
        labelPaint.setTextSize(bh * 0.22f);
    }

    public void setCurrentScreen(int screen) {
        currentScreen = screen;
    }

    public enum Action {
        NONE, UP, DOWN, TOP, CAM_TOGGLE
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
        if (camRect.contains(x, y)) {
            return Action.CAM_TOGGLE;
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
        canvas.drawRoundRect(camRect, 12, 12, cameraMode ? camOnPaint : fillPaint);
        canvas.drawText("CAM", camRect.centerX(),
                camRect.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint);
        canvas.drawText("S" + currentScreen, upRect.centerX(),
                upRect.bottom + labelPaint.getTextSize(), labelPaint);
    }

    private void drawButton(Canvas canvas, RectF rect, String label) {
        canvas.drawRoundRect(rect, 12, 12, fillPaint);
        canvas.drawText(label, rect.centerX(),
                rect.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint);
    }
}
