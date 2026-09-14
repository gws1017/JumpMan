package kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main;

import android.media.MediaPlayer;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.game.Scene;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Foreground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Midground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.ui.OptionsEntryButton;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.GameSettings;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.LeftRightPad;
import kr.ac.kpu.game.s2017182016.jumpman.game.DebugCheats;
import kr.ac.kpu.game.s2017182016.jumpman.game.LevelMaskParser;
import kr.ac.kpu.game.s2017182016.jumpman.game.Player;
import kr.ac.kpu.game.s2017182016.jumpman.game.Platform;
import kr.ac.kpu.game.s2017182016.jumpman.game.StageMap;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.options.OptionsScene;

public class MainScene extends Scene {

    public static Background bg;
    public static Midground mg;
    public static Foreground fg;
    private Player player;
    private Joystick joystick;
    private LeftRightPad movePad;
    private DebugCheats debugCheats;
    private OptionsEntryButton optionsButton;
    private MediaPlayer openingBgm;
    private boolean gestureAllowsJump;
    private int jumpPointerId = -1;
    private boolean bgmPausedByOptions;

    public MediaPlayer forestBgm;
    public MediaPlayer endBgm;

    public enum Layer {
        bg, mg, player, fg, platform, controller, LAYER_COUNT
    }

    public static MainScene scene;

    public void add(Layer layer, GameObject obj) {
        add(layer.ordinal(), obj);
    }

    public ArrayList<GameObject> objectsAt(Layer layer) {
        return objectsAt(layer.ordinal());
    }

    @Override
    public void start() {
        scene = this;
        super.start();

        int w = GameView.gameWidth;
        int h = GameView.gameHeight;
        initLayers(Layer.LAYER_COUNT.ordinal());
        openingBgm = MediaPlayer.create(GameView.view.getContext(), R.raw.opening_theme);
        forestBgm = MediaPlayer.create(GameView.view.getContext(), R.raw.nb_troll_forest);
        endBgm = MediaPlayer.create(GameView.view.getContext(), R.raw.ending3);

        GameSettings settings = GameSettings.get();
        settings.applyBgmVolume(openingBgm);
        settings.applyBgmVolume(forestBgm);
        settings.applyBgmVolume(endBgm);

        openingBgm.start();
        openingBgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                GameSettings.get().applyBgmVolume(forestBgm);
                forestBgm.setLooping(true);
                forestBgm.start();
            }
        });

        float btnW = 58f * w / 480f;
        float btnH = 58f * h / 360f;
        float gap = 8f * w / 480f;
        float padLeft = 12f * w / 480f;
        // 십자: 위 1칸 + 아래 좌우 → 전체 높이 = 2*btnH + gap
        float padTop = h - (btnH * 2f + gap) - 14f * h / 360f;
        movePad = new LeftRightPad(padLeft, padTop, btnW, btnH, gap);

        // 조이패드 = 십자패드와 같은 중심/영역 (모드만 교체)
        float padW = movePad.getRight() - movePad.getLeft();
        float padH = movePad.getBottom() - movePad.getTop();
        int cx = Math.round((movePad.getLeft() + movePad.getRight()) / 2f);
        int cy = Math.round((movePad.getTop() + movePad.getBottom()) / 2f);
        int outRadius = Math.round(Math.min(padW, padH) * 0.48f);
        int inRadius = Math.round(outRadius * 0.42f);

        LevelMaskParser.ensureLoaded(GameView.view.getContext());
        int startScreen = LevelMaskParser.START_SCREEN;

        bg = new Background(startScreen);
        add(Layer.bg, bg);
        mg = new Midground(startScreen);
        add(Layer.mg, mg);
        fg = new Foreground(startScreen);
        add(Layer.fg, fg);

        joystick = new Joystick(cx, cy, outRadius, inRadius);
        add(Layer.controller, joystick);
        add(Layer.controller, movePad);
        add(Layer.controller, new StageMap(startScreen));

        optionsButton = new OptionsEntryButton(true);
        add(Layer.controller, optionsButton);

        player = new Player(w / 2, h - (80) * h / 360, joystick);
        player.setMovePad(movePad);
        add(Layer.player, player);

        debugCheats = new DebugCheats();
        debugCheats.setCurrentScreen(startScreen);
        add(Layer.controller, debugCheats);

        applyControlMode();
    }

    private void applyControlMode() {
        GameSettings.PadMode mode = GameSettings.get().getPadMode();
        boolean useDpad = mode == GameSettings.PadMode.DPAD;
        if (movePad != null) {
            movePad.setEnabled(useDpad);
        }
        if (joystick != null) {
            joystick.setEnabled(!useDpad);
        }
        if (player != null) {
            player.setMovePad(useDpad ? movePad : null);
        }
    }

    public void applyAudioSettings() {
        GameSettings s = GameSettings.get();
        s.applyBgmVolume(openingBgm);
        s.applyBgmVolume(forestBgm);
        s.applyBgmVolume(endBgm);
    }

    @Override
    public void pause() {
        bgmPausedByOptions = true;
        pauseIfPlaying(openingBgm);
        pauseIfPlaying(forestBgm);
        pauseIfPlaying(endBgm);
    }

    @Override
    public void resume() {
        applyControlMode();
        applyAudioSettings();
        if (bgmPausedByOptions) {
            bgmPausedByOptions = false;
            resumeBgm();
        }
    }

    private void resumeBgm() {
        try {
            if (endBgm != null && endBgm.getCurrentPosition() > 0 && !endBgm.isPlaying()) {
                // if ending was active leave it; heuristic weak — prefer forest/opening
            }
            if (forestBgm != null && forestBgm.isLooping()) {
                GameSettings.get().applyBgmVolume(forestBgm);
                if (!forestBgm.isPlaying()) {
                    forestBgm.start();
                }
                return;
            }
            if (openingBgm != null && !openingBgm.isPlaying()) {
                GameSettings.get().applyBgmVolume(openingBgm);
                openingBgm.start();
            }
        } catch (IllegalStateException ignored) {
        }
    }

    private void pauseIfPlaying(MediaPlayer mp) {
        if (mp == null) {
            return;
        }
        try {
            if (mp.isPlaying()) {
                mp.pause();
            }
        } catch (IllegalStateException ignored) {
        }
    }

    public void warpToScreen(int screen) {
        if (!LevelMaskParser.screenAssetExists(screen)) {
            return;
        }
        if (!LevelMaskParser.hasCollisionPixels(screen)) {
            int alt = LevelMaskParser.screenAbove(screen);
            if (alt < 0) {
                alt = LevelMaskParser.screenBelow(screen);
            }
            if (alt < 0) {
                return;
            }
            screen = alt;
        }
        bg.setScreen(screen);
        mg.setScreen(screen);
        fg.setScreen(screen);
        clearPlatforms();
        add(Layer.controller, new StageMap(screen));
        player.resetSpawn();
        if (debugCheats != null) {
            debugCheats.setCurrentScreen(screen);
        }
    }

    public void warpUp() {
        int next = LevelMaskParser.screenAbove(mg.num);
        if (next > 0) {
            warpToScreen(next);
        }
    }

    public void warpDown() {
        int prev = LevelMaskParser.screenBelow(mg.num);
        if (prev > 0) {
            warpToScreen(prev);
        }
    }


    public void warpBy(int delta) {
        int target = mg.num + delta;
        if (delta > 0) {
            while (target <= LevelMaskParser.MAX_SCREEN) {
                if (LevelMaskParser.screenAssetExists(target)
                        && LevelMaskParser.hasCollisionPixels(target)) {
                    warpToScreen(target);
                    return;
                }
                target++;
            }
        } else {
            while (target >= LevelMaskParser.MIN_SCREEN) {
                if (LevelMaskParser.screenAssetExists(target)
                        && LevelMaskParser.hasCollisionPixels(target)) {
                    warpToScreen(target);
                    return;
                }
                target--;
            }
        }
    }

    private void clearPlatforms() {
        ArrayList<GameObject> platforms = objectsAt(Layer.platform);
        for (GameObject obj : new ArrayList<>(platforms)) {
            remove(obj, false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        float x = event.getX(index);
        float y = event.getY(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(pointerId, x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    onPointerMove(event.getPointerId(i), event.getX(i), event.getY(i));
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(pointerId);
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (movePad != null) {
                    movePad.reset();
                }
                joystick.setIsPressed(false);
                joystick.resetActuator();
                if (jumpPointerId >= 0) {
                    player.cancelReady();
                    jumpPointerId = -1;
                    gestureAllowsJump = false;
                }
                return true;
        }
        return false;
    }

    private void onPointerDown(int pointerId, float x, float y) {
        if (optionsButton != null && optionsButton.hit(x, y)) {
            MainGame.get().push(new OptionsScene());
            return;
        }

        if (debugCheats != null) {
            DebugCheats.Action action = debugCheats.hit(x, y);
            if (action != DebugCheats.Action.NONE) {
                switch (action) {
                    case UP:
                        warpUp();
                        break;
                    case DOWN:
                        warpDown();
                        break;
                    case TOP:
                        warpToScreen(LevelMaskParser.MAX_SCREEN);
                        break;
                    case CAM_TOGGLE:
                        DebugCheats.cameraMode = !DebugCheats.cameraMode;
                        break;
                }
                return;
            }
        }

        if (movePad != null && movePad.onPointerDown(pointerId, x, y)) {
            return;
        }
        if (joystick != null && joystick.isPressed(x, y)) {
            joystick.setIsPressed(true, pointerId);
            joystick.setActuator(x, y);
            return;
        }
        if ((joystick != null && joystick.blocksJump(x, y))
                || (movePad != null && movePad.blocksJump(x, y))) {
            return;
        }

        jumpPointerId = pointerId;
        gestureAllowsJump = true;
        player.ready();
    }

    private void onPointerMove(int pointerId, float x, float y) {
        if (movePad != null) {
            movePad.onPointerMove(pointerId, x, y);
        }
        if (joystick != null && joystick.getIsPressed() && joystick.getPointerId() == pointerId) {
            joystick.setActuator(x, y);
        }
    }

    private void onPointerUp(int pointerId) {
        if (movePad != null) {
            movePad.onPointerUp(pointerId);
        }
        if (joystick != null && joystick.getPointerId() == pointerId) {
            joystick.setIsPressed(false);
            joystick.resetActuator();
        }
        if (jumpPointerId == pointerId) {
            if (gestureAllowsJump) {
                player.jump();
            } else {
                player.cancelReady();
            }
            jumpPointerId = -1;
            gestureAllowsJump = false;
        }
    }

    public boolean onKeyDown(int keyCode) {
        if (!DebugCheats.isActive()) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_W:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
                warpUp();
                return true;
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                warpDown();
                return true;
            case KeyEvent.KEYCODE_E:
                warpBy(5);
                return true;
            case KeyEvent.KEYCODE_Q:
                warpBy(-5);
                return true;
            case KeyEvent.KEYCODE_T:
                warpToScreen(LevelMaskParser.MAX_SCREEN);
                return true;
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                warpToScreen(keyCode - KeyEvent.KEYCODE_0);
                return true;
            default:
                return false;
        }
    }
}
