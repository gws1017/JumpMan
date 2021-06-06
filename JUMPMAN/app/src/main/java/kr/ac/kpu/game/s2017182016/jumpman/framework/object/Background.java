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
    public static Bitmap bgbitmap;
    private int[] backmaps = {
            R.mipmap.transparent,
            R.mipmap.transparent,
            R.mipmap.transparent,
            R.mipmap.bg4,
            R.mipmap.bg5,
            R.mipmap.bg6,
            R.mipmap.transparent,
    };


    public Rect srcRect = new Rect();
    public RectF dstRect = new RectF();
    public int num = 0;

    public Background(int resId){

        if(backmaps[num] != 0) {
            bgbitmap = GameBitmap.load(backmaps[num]);
            int w = bgbitmap.getWidth();
            int h = bgbitmap.getHeight();
            srcRect.set(0,0,w,h);
            float l = 0;
            float t = 0;
            float b = GameView.view.getHeight();
            float r = GameView.view.getWidth();

            dstRect.set(l,t,r,b);
        }

    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        if(num>2) canvas.drawBitmap(bgbitmap,srcRect,dstRect,null);
    }

    public void nextimg() {
        ++num;
        if(backmaps[num]!= 0)this.bgbitmap = GameBitmap.load(backmaps[num]);
    }
    public void previmg() {
        --num;
        if(backmaps[num]!= 0)this.bgbitmap = GameBitmap.load(backmaps[num]);
    }
    public boolean isLast() {
        if( num< backmaps.length -1 ) return false;
        else return true;
    }

    public boolean isFirst() {
        if( num > 0 ) return false;
        else return true;
    }
}
