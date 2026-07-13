package kr.ac.kpu.game.s2017182016.jumpman.framework.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import kr.ac.kpu.game.s2017182016.jumpman.framework.bitmap.GameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.AssetBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.LevelMaskParser;

public class Midground implements GameObject {
    public static Bitmap mgbitmap;
    public Rect srcRect = new Rect();
    public RectF dstRect = new RectF();
    /** Jump King screen number (1-based, Hitbox2Screens order). */
    public int num;
    private final boolean fromMipmap;

    public Midground(int screenNumber) {
        this.fromMipmap = false;
        this.num = screenNumber;
        loadCurrent();
    }

    /** Fullscreen layer from mipmap (title screen). */
    public Midground(int resId, boolean fromMipmap) {
        this.fromMipmap = fromMipmap;
        this.num = 0;
        if (fromMipmap) {
            mgbitmap = GameBitmap.load(resId);
            srcRect.set(0, 0, mgbitmap.getWidth(), mgbitmap.getHeight());
            dstRect.set(0, 0, GameView.view.getWidth(), GameView.view.getHeight());
        } else {
            this.num = resId;
            loadCurrent();
        }
    }

    private void loadCurrent() {
        Bitmap bmp = AssetBitmap.load(GameView.view.getContext(), "midground/" + num + ".png");
        mgbitmap = bmp;
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
        if (mgbitmap != null) {
            canvas.drawBitmap(mgbitmap, srcRect, dstRect, null);
        }
    }

    public void nextimg() {
        if (fromMipmap) {
            return;
        }
        int above = LevelMaskParser.screenAbove(num);
        if (above < 0) {
            return;
        }
        setScreen(above);
    }

    public void previmg() {
        if (fromMipmap) {
            return;
        }
        int below = LevelMaskParser.screenBelow(num);
        if (below < 0) {
            return;
        }
        setScreen(below);
    }

    public void setScreen(int screenNumber) {
        if (fromMipmap) {
            return;
        }
        num = screenNumber;
        loadCurrent();
    }

    public boolean isLast() {
        if (fromMipmap) {
            return true;
        }
        return LevelMaskParser.screenAbove(num) < 0;
    }

    public boolean isFirst() {
        if (fromMipmap) {
            return true;
        }
        return LevelMaskParser.screenBelow(num) < 0;
    }
}
