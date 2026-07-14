package kr.ac.kpu.game.s2017182016.jumpman.game.scenes.title;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.MotionEvent;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.game.Scene;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.ImageObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Midground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.ui.OptionsEntryButton;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.GameSettings;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.options.OptionsScene;

public class TitleScene extends Scene {

    enum Layer {
        bg, logo, ui, COUNT
    }

    public static TitleScene scene;
    MediaPlayer titleBgm;
    MediaPlayer startBgm;
    private OptionsEntryButton optionsButton;
    private boolean starting;

    public void add(Layer layer, GameObject obj) {
        add(layer.ordinal(), obj);
    }

    @Override
    public void start() {
        scene = this;
        starting = false;
        super.start();
        int w = GameView.view.getWidth();
        int h = GameView.view.getHeight();
        int iw = 480;
        int ih = 350;
        titleBgm = MediaPlayer.create(GameView.view.getContext(), R.raw.menu_intro);
        startBgm = MediaPlayer.create(GameView.view.getContext(), R.raw.press_start);
        initLayers(Layer.COUNT.ordinal());

        add(Layer.bg, new Midground(R.mipmap.title_bg, true));
        int lx = (iw / 2) * w / iw;
        int ly = 82 * h / ih;
        add(Layer.logo, new ImageObject(R.mipmap.title_logo, lx, ly));

        int tx = (iw / 2) * w / iw;
        int ty = 231 * h / ih;
        add(Layer.logo, new ImageObject(R.mipmap.title_text, tx, ty));

        optionsButton = new OptionsEntryButton();
        add(Layer.ui, optionsButton);
        add(Layer.ui, new StartHint());

        GameSettings.get().applyBgmVolume(titleBgm);
        titleBgm.setLooping(true);
        titleBgm.start();
    }

    public void applyAudioSettings() {
        if (titleBgm != null) {
            GameSettings.get().applyBgmVolume(titleBgm);
        }
        if (startBgm != null) {
            GameSettings.get().applyBgmVolume(startBgm);
        }
    }

    @Override
    public void pause() {
        if (titleBgm != null) {
            try {
                if (titleBgm.isPlaying()) {
                    titleBgm.pause();
                }
            } catch (IllegalStateException ignored) {
            }
        }
    }

    @Override
    public void resume() {
        if (starting) {
            return;
        }
        if (titleBgm != null) {
            GameSettings.get().applyBgmVolume(titleBgm);
            try {
                titleBgm.start();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    @Override
    public void end() {
        releasePlayer(titleBgm);
        releasePlayer(startBgm);
        titleBgm = null;
        startBgm = null;
    }

    private void releasePlayer(MediaPlayer mp) {
        if (mp == null) {
            return;
        }
        try {
            mp.release();
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_DOWN || starting) {
            return true;
        }
        float x = e.getX();
        float y = e.getY();
        if (optionsButton != null && optionsButton.hit(x, y)) {
            MainGame.get().push(new OptionsScene());
            return true;
        }

        starting = true;
        try {
            titleBgm.stop();
        } catch (IllegalStateException ignored) {
        }
        GameSettings.get().applyBgmVolume(startBgm);
        startBgm.start();
        startBgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MainGame game = MainGame.get();
                game.popScene();
                game.push(new MainScene());
            }
        });
        return true;
    }

    private class StartHint implements GameObject {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        StartHint() {
            paint.setColor(0xCCFFFFFF);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        }

        @Override
        public void update() {
        }

        @Override
        public void draw(Canvas canvas) {
            float w = GameView.view.getWidth();
            float h = GameView.view.getHeight();
            paint.setTextSize(h * 0.035f);
            canvas.drawText("화면을 눌러 시작  ·  우측 상단 옵션", w / 2f, h * 0.92f, paint);
        }
    }
}
