package kr.ac.kpu.game.s2017182016.jumpman.framework.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.AssetBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.LevelMaskParser;

public class Foreground implements GameObject {
    public static Bitmap fgbitmap;
    public Rect srcRect = new Rect();
    public RectF dstRect = new RectF();
    public int num;

    public Foreground(int screenNumber) {
        this.num = screenNumber;
        loadCurrent();
    }

    private void loadCurrent() {
        Bitmap bmp = AssetBitmap.load(GameView.view.getContext(), "foreground/fg" + num + ".png");
        fgbitmap = bmp;
        if (bmp == null) {
            srcRect.set(0, 0, 1, 1);
        } else {
            srcRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
        }
        dstRect.set(0, 0, GameView.view.getWidth(), GameView.view.getHeight());
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (fgbitmap != null) {
            canvas.drawBitmap(fgbitmap, srcRect, dstRect, null);
        }
    }

    public void nextimg() {
        int above = LevelMaskParser.screenAbove(num);
        if (above < 0) {
            return;
        }
        setScreen(above);
    }

    public void previmg() {
        int below = LevelMaskParser.screenBelow(num);
        if (below < 0) {
            return;
        }
        setScreen(below);
    }

    public void setScreen(int screenNumber) {
        num = screenNumber;
        loadCurrent();
    }

    public boolean isLast() {
        return LevelMaskParser.screenAbove(num) < 0;
    }

    public boolean isFirst() {
        return LevelMaskParser.screenBelow(num) < 0;
    }
}
