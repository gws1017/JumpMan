package kr.ac.kpu.game.s2017182016.jumpman.framework;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.HashMap;

import kr.ac.kpu.game.s2017182016.jumpman.ui.view.GameView;

public class GameBitmap {

    private static HashMap<Integer,Bitmap> bitmaps = new HashMap<>();

    public static Bitmap load(int resId){
        Bitmap bitmap = bitmaps.get(resId);
        if(bitmap == null){
            Resources res = GameView.view.getResources();
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            bitmap = BitmapFactory.decodeResource(res,resId,opts);
            bitmaps.put(resId,bitmap);
        }
        return bitmap;
    }

    protected Bitmap bitmap;
    protected Rect srcRect = new Rect();
    protected RectF dstRect = new RectF();
    public GameBitmap(int resId) {
        bitmap = load(resId);
    }

    public void draw(Canvas canvas, float x, float y){
        int hw = getWidth() /2;
        int hh = getHeight() /2;

        float dl = x - hw *GameView.MULTIPLIER;
        float dt = y - hw *GameView.MULTIPLIER;
        float dr = x + hw *GameView.MULTIPLIER;
        float db = y + hw *GameView.MULTIPLIER;

        srcRect.set(0,0,getWidth()/4,getHeight()/3);
        dstRect.set(dl,dt,dr,db);
        canvas.drawBitmap(bitmap,srcRect,dstRect,null);
    }

    private int getHeight() {
        return bitmap.getHeight();
    }

    private int getWidth() {
        return bitmap.getWidth();
    }
    public void getBoundingRect(float x, float y, RectF rect) {
        int hw = getWidth() / 2;
        int hh = getHeight() / 2;

        float dl = x - hw * GameView.MULTIPLIER;
        float dt = y - hh * GameView.MULTIPLIER;
        float dr = x + hw * GameView.MULTIPLIER;
        float db = y + hh * GameView.MULTIPLIER;
        rect.set(dl, dt, dr, db);
    }
}
