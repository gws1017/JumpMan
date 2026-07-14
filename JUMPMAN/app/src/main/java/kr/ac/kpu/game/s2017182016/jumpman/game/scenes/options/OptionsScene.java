package kr.ac.kpu.game.s2017182016.jumpman.game.scenes.options;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;

import kr.ac.kpu.game.s2017182016.jumpman.framework.game.Scene;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.GameSettings;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.Sound;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.title.TitleScene;
import kr.ac.kpu.game.s2017182016.jumpman.R;

/**
 * In-game options overlay (pushed on the scene stack).
 */
public class OptionsScene extends Scene {

    enum Layer {
        ui, COUNT
    }

    private final OptionsPanel panel = new OptionsPanel();

    @Override
    public void start() {
        super.start();
        initLayers(Layer.COUNT.ordinal());
        add(Layer.ui.ordinal(), panel);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return panel.onTap(event.getX(), event.getY());
        }
        return true;
    }

    private static class OptionsPanel implements GameObject {
        private final Paint dimPaint = new Paint();
        private final Paint panelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint btnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint btnOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private final RectF panelRect = new RectF();
        private final RectF joyBtn = new RectF();
        private final RectF dpadBtn = new RectF();
        private final RectF cheatBtn = new RectF();
        private final RectF collBtn = new RectF();
        private final RectF bgmMinus = new RectF();
        private final RectF bgmPlus = new RectF();
        private final RectF sfxMinus = new RectF();
        private final RectF sfxPlus = new RectF();
        private final RectF backBtn = new RectF();
        private final RectF quitBtn = new RectF();

        OptionsPanel() {
            dimPaint.setColor(0xCC101018);
            panelPaint.setColor(0xEE2A2A34);
            btnPaint.setColor(0xFF4A4A58);
            btnOnPaint.setColor(0xFF3B6EA5);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(3f);
            borderPaint.setColor(0xFFD0D0E0);
            titlePaint.setColor(Color.WHITE);
            titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            layout();
        }

        private void layout() {
            float w = GameView.view.getWidth();
            float h = GameView.view.getHeight();
            float pw = w * 0.82f;
            float ph = h * 0.90f;
            panelRect.set((w - pw) / 2f, (h - ph) / 2f, (w + pw) / 2f, (h + ph) / 2f);

            float pad = pw * 0.07f;
            float left = panelRect.left + pad;
            float right = panelRect.right - pad;
            float rowH = ph * 0.068f;
            float y = panelRect.top + ph * 0.11f;
            float gap = ph * 0.035f;
            float sectionGap = ph * 0.055f;
            float mid = (left + right) / 2f;

            joyBtn.set(left, y, mid - 10, y + rowH);
            dpadBtn.set(mid + 10, y, right, y + rowH);
            y += rowH + sectionGap;

            cheatBtn.set(left, y, right, y + rowH);
            y += rowH + gap;
            collBtn.set(left, y, right, y + rowH);
            y += rowH + sectionGap;

            float sq = rowH;
            bgmMinus.set(left, y, left + sq, y + rowH);
            bgmPlus.set(right - sq, y, right, y + rowH);
            y += rowH + gap;
            sfxMinus.set(left, y, left + sq, y + rowH);
            sfxPlus.set(right - sq, y, right, y + rowH);
            y += rowH + sectionGap;

            backBtn.set(left, y, right, y + rowH);
            y += rowH + gap;
            quitBtn.set(left, y, right, y + rowH);

            titlePaint.setTextSize(ph * 0.05f);
            textPaint.setTextSize(ph * 0.032f);
        }

        boolean onTap(float x, float y) {
            GameSettings s = GameSettings.get();
            if (joyBtn.contains(x, y)) {
                s.setPadMode(GameSettings.PadMode.JOYSTICK);
                return true;
            }
            if (dpadBtn.contains(x, y)) {
                s.setPadMode(GameSettings.PadMode.DPAD);
                return true;
            }
            if (cheatBtn.contains(x, y)) {
                s.setCheatEnabled(!s.isCheatEnabled());
                return true;
            }
            if (collBtn.contains(x, y)) {
                s.setShowCollision(!s.isShowCollision());
                return true;
            }
            if (bgmMinus.contains(x, y)) {
                s.adjustBgmVolume(-0.1f);
                notifyAudioChanged();
                return true;
            }
            if (bgmPlus.contains(x, y)) {
                s.adjustBgmVolume(0.1f);
                notifyAudioChanged();
                return true;
            }
            if (sfxMinus.contains(x, y)) {
                s.adjustSfxVolume(-0.1f);
                Sound.play(R.raw.king_land);
                return true;
            }
            if (sfxPlus.contains(x, y)) {
                s.adjustSfxVolume(0.1f);
                Sound.play(R.raw.king_land);
                return true;
            }
            if (backBtn.contains(x, y)) {
                MainGame.get().popScene();
                return true;
            }
            if (quitBtn.contains(x, y)) {
                Activity activity = (Activity) GameView.view.getContext();
                activity.finish();
                return true;
            }
            return true;
        }

        private void notifyAudioChanged() {
            if (MainScene.scene != null) {
                MainScene.scene.applyAudioSettings();
            }
            if (TitleScene.scene != null) {
                TitleScene.scene.applyAudioSettings();
            }
        }

        @Override
        public void update() {
        }

        @Override
        public void draw(Canvas canvas) {
            layout();
            GameSettings s = GameSettings.get();
            canvas.drawRect(0, 0, GameView.view.getWidth(), GameView.view.getHeight(), dimPaint);
            float r = 18f;
            canvas.drawRoundRect(panelRect, r, r, panelPaint);
            canvas.drawRoundRect(panelRect, r, r, borderPaint);

            canvas.drawText("옵션", panelRect.centerX(),
                    panelRect.top + panelRect.height() * 0.08f, titlePaint);

            canvas.drawText("조작", panelRect.centerX(),
                    joyBtn.top - textPaint.getTextSize() * 0.4f, textPaint);
            drawToggle(canvas, joyBtn, "조이스틱", s.getPadMode() == GameSettings.PadMode.JOYSTICK);
            drawToggle(canvas, dpadBtn, "십자 패드", s.getPadMode() == GameSettings.PadMode.DPAD);

            canvas.drawText("디버그", panelRect.centerX(),
                    cheatBtn.top - textPaint.getTextSize() * 0.4f, textPaint);
            drawToggle(canvas, cheatBtn, "치트키  " + onOff(s.isCheatEnabled()), s.isCheatEnabled());
            drawToggle(canvas, collBtn, "콜리전 박스  " + onOff(s.isShowCollision()), s.isShowCollision());

            drawVolumeRow(canvas, bgmMinus, bgmPlus, "배경음", s.getBgmVolume());
            drawVolumeRow(canvas, sfxMinus, sfxPlus, "효과음", s.getSfxVolume());

            drawToggle(canvas, backBtn, "돌아가기", false);
            drawToggle(canvas, quitBtn, "게임 종료", false);
        }

        private String onOff(boolean v) {
            return v ? "ON" : "OFF";
        }

        private void drawToggle(Canvas canvas, RectF rect, String label, boolean on) {
            canvas.drawRoundRect(rect, 12, 12, on ? btnOnPaint : btnPaint);
            canvas.drawRoundRect(rect, 12, 12, borderPaint);
            canvas.drawText(label, rect.centerX(),
                    rect.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint);
        }

        private void drawVolumeRow(Canvas canvas, RectF minus, RectF plus, String label, float vol) {
            canvas.drawRoundRect(minus, 12, 12, btnPaint);
            canvas.drawRoundRect(plus, 12, 12, btnPaint);
            canvas.drawRoundRect(minus, 12, 12, borderPaint);
            canvas.drawRoundRect(plus, 12, 12, borderPaint);
            canvas.drawText("−", minus.centerX(),
                    minus.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint);
            canvas.drawText("+", plus.centerX(),
                    plus.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint);
            int pct = Math.round(vol * 100f);
            canvas.drawText(label + "  " + pct + "%", (minus.right + plus.left) / 2f,
                    minus.centerY() - (textPaint.ascent() + textPaint.descent()) / 2f, textPaint);
        }
    }
}
