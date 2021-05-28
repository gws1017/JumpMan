package kr.ac.kpu.game.s2017182016.jumpman.framework.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.bitmap.GameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;

public class Background implements GameObject {

    public static final int MAG = 5;
    //    private static int w;
//    private static int h;
//    private static int imageWidth;
//    private static int imageHeight;
//    private static int bgLeft;
//    private static int bgRight;
    public static Bitmap bitmap;
    private int[] maps= {
            R.mipmap.bg_1,
            R.mipmap.bg_2,
    };
    public Rect srcRect = new Rect();
    public RectF dstRect = new RectF();
    private int num = 0;

    public Background(int resId){
        bitmap = GameBitmap.load(resId);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        srcRect.set(0,0,w,h);
        float l = 0;
        float t = 0;
        float b = GameView.view.getHeight();
        float r = GameView.view.getWidth();

        dstRect.set(l,t,r,b);
//        if(bitmap == null)
//        {
//            Resources res = GameView.view.getResources();
//            bitmap = BitmapFactory.decodeResource(res, R.mipmap.bg_1);
//
//            w = GameView.view.getWidth();
//            h = GameView.view.getHeight();
//
//            imageWidth = bitmap.getWidth();
//            imageHeight = bitmap.getHeight();
//
//            bgLeft = w/2 - imageWidth*MAG/2;
//            bgRight = w/2 + imageWidth*MAG/2;
//            dstRect = new Rect(bgLeft,0,bgRight,h);
//
//        }
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap,srcRect,dstRect,null);
    }

    public void nextimg() {
        this.bitmap = GameBitmap.load(maps[++num]);
    }
    public void previmg() {
        this.bitmap = GameBitmap.load(maps[--num]);
    }
    public boolean isLast() {
        if( num< maps.length -1 ) return false;
        else return true;
    }

    public boolean isFirst() {
        if( num > 0 ) return false;
        else return true;
    }
}
