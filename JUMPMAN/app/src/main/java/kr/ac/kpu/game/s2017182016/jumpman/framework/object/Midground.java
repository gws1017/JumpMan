package kr.ac.kpu.game.s2017182016.jumpman.framework.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.bitmap.GameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;

public class Midground implements GameObject {

    public static final int MAG = 5;
    //    private static int w;
//    private static int h;
//    private static int imageWidth;
//    private static int imageHeight;
//    private static int bgLeft;
//    private static int bgRight;
    public static Bitmap mgbitmap;

    private int[] midmaps = {
            R.mipmap.mg1,
            R.mipmap.mg2,
            R.mipmap.mg3,
            R.mipmap.mg4,
            R.mipmap.mg5,
            R.mipmap.mg6,
    };


    public Rect srcRect = new Rect();
    public RectF dstRect = new RectF();
    public int num = 0;

    public Midground(int resId){

        mgbitmap = GameBitmap.load(resId);
        int w = mgbitmap.getWidth();
        int h = mgbitmap.getHeight();
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
        canvas.drawBitmap(mgbitmap,srcRect,dstRect,null);
    }

    public void nextimg() {
        this.mgbitmap = GameBitmap.load(midmaps[++num]);
    }
    public void previmg() {
        this.mgbitmap = GameBitmap.load(midmaps[--num]);
    }
    public boolean isLast() {
        if( num< midmaps.length -1 ) return false;
        else return true;
    }

    public boolean isFirst() {
        if( num > 0 ) return false;
        else return true;
    }
}
