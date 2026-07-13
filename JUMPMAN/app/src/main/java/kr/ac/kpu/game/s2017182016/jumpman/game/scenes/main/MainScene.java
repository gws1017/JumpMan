package kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main;

import android.media.MediaPlayer;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.BuildConfig;
import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.game.Scene;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Foreground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Midground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;
import kr.ac.kpu.game.s2017182016.jumpman.game.DebugCheats;
import kr.ac.kpu.game.s2017182016.jumpman.game.LevelMaskParser;
import kr.ac.kpu.game.s2017182016.jumpman.game.Player;
import kr.ac.kpu.game.s2017182016.jumpman.game.Platform;
import kr.ac.kpu.game.s2017182016.jumpman.game.StageMap;

public class MainScene extends Scene {

    public static Background bg;
    public static Midground mg;
    public static Foreground fg;
    private Player player;
    private Joystick joystick;
    private DebugCheats debugCheats;
    private MediaPlayer openingBgm;
    private boolean gestureAllowsJump;

    public MediaPlayer forestBgm;
    public MediaPlayer endBgm;
    public enum Layer{
        bg,mg,player,fg,platform,controller,LAYER_COUNT
    }

    public static MainScene scene;
    public void add(Layer layer, GameObject obj) {
        add(layer.ordinal(), obj);
    }
    public ArrayList<GameObject> objectsAt(Layer layer) {
        return objectsAt(layer.ordinal());
    }

    @Override
    public void start(){
        scene = this;
        super.start();

        int w = GameView.view.getWidth();
        int h = GameView.view.getHeight();
        initLayers(Layer.LAYER_COUNT.ordinal());
        openingBgm = MediaPlayer.create(GameView.view.getContext(),R.raw.opening_theme);
        forestBgm = MediaPlayer.create(GameView.view.getContext(),R.raw.nb_troll_forest);
        endBgm = MediaPlayer.create(GameView.view.getContext(),R.raw.ending3);
        openingBgm.start();
        openingBgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                forestBgm.setLooping(true);
                forestBgm.start();
            }
        });

        int cx = 70*w/480;
        int cy = h-70*h/360;
        int outRadius = h/20*GameView.MULTIPLIER;
        int inRadius = h/20*GameView.MULTIPLIER /2;

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
        add(Layer.controller, new StageMap(startScreen));

        player = new Player(w / 2, h - (80) * h / 360, joystick);
        add(Layer.player, player);

        if (BuildConfig.DEBUG) {
            debugCheats = new DebugCheats();
            debugCheats.setCurrentScreen(startScreen);
            add(Layer.controller, debugCheats);
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
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (debugCheats != null) {
                    DebugCheats.Action action = debugCheats.hit(x, y);
                    if (action != DebugCheats.Action.NONE) {
                        gestureAllowsJump = false;
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
                        }
                        return true;
                    }
                }
                if (joystick.isPressed(x, y)) {
                    joystick.setIsPressed(true);
                    gestureAllowsJump = false;
                    player.cancelReady();
                } else if (joystick.blocksJump(x, y)) {
                    gestureAllowsJump = false;
                    player.cancelReady();
                } else {
                    gestureAllowsJump = true;
                    player.ready();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (joystick.getIsPressed()) {
                    joystick.setActuator(x, y);
                } else if (gestureAllowsJump) {
                    player.ready();
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                boolean wasJoystick = joystick.getIsPressed();
                joystick.setIsPressed(false);
                joystick.resetActuator();
                if (gestureAllowsJump && !wasJoystick) {
                    player.jump();
                } else {
                    player.cancelReady();
                }
                gestureAllowsJump = false;
                return true;
        }
        return false;
    }

    public boolean onKeyDown(int keyCode) {
        if (!BuildConfig.DEBUG) {
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
