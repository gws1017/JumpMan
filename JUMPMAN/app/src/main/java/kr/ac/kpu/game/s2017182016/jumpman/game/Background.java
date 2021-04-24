package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.ui.view.GameView;

public class Background implements GameObject {

    public static final int MAG = 5;
    private static int w;
    private static int h;
    private static int imageWidth;
    private static int imageHeight;
    private static int bgLeft;
    private static int bgRight;
    private static Bitmap bitmap;
    public Rect dstRect;

    public Background(){
        if(bitmap == null)
        {
            Resources res = GameView.view.getResources();
            bitmap = BitmapFactory.decodeResource(res, R.mipmap.bg_1);

            w = GameView.view.getWidth();
            h = GameView.view.getHeight();

            imageWidth = bitmap.getWidth();
            imageHeight = bitmap.getHeight();

            bgLeft = w/2 - imageWidth*MAG/2;
            bgRight = w/2 + imageWidth*MAG/2;
            dstRect = new Rect(bgLeft,0,bgRight,h);

        }
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap,null,dstRect,null);
    }
}
