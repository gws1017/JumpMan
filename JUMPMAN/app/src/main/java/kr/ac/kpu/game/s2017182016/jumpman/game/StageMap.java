package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;

import java.util.List;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;

public class StageMap implements GameObject {
    private final int viewWidth = GameView.view.getWidth();
    private final int viewHeight = GameView.view.getHeight();
    private final int imageWidth = LevelMaskParser.LOGICAL_W;
    private final int imageHeight = LevelMaskParser.LOGICAL_H;

    public StageMap(int screenNumber) {
        LevelMaskParser.ensureLoaded(GameView.view.getContext());

        List<LevelMaskParser.MaskPlatform> platforms = LevelMaskParser.parseScreen(screenNumber);
        for (LevelMaskParser.MaskPlatform mp : platforms) {
            createObject(
                    mp.rect.left,
                    mp.rect.top,
                    mp.rect.width(),
                    mp.rect.height(),
                    mp.slope ? Platform.Kind.SLOPE : Platform.Kind.NORMAL,
                    mp.slopeDir
            );
        }

        if (screenNumber == LevelMaskParser.MAX_SCREEN) {
            MainScene scene = MainScene.scene;
            if (scene != null && scene.forestBgm != null && scene.endBgm != null) {
                try {
                    if (scene.forestBgm.isPlaying()) {
                        scene.forestBgm.stop();
                    }
                    scene.applyAudioSettings();
                    scene.endBgm.start();
                } catch (IllegalStateException ignored) {
                }
            }
        }
    }

    private void createObject(float x, float y, int w, int h, Platform.Kind kind, int slopeDir) {
        MainScene scene = MainScene.scene;
        Platform platform = new Platform(
                x * viewWidth / imageWidth,
                y * viewHeight / imageHeight,
                w * viewWidth / imageWidth,
                h * viewHeight / imageHeight,
                kind,
                slopeDir
        );
        scene.add(MainScene.Layer.platform, platform);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
    }
}
