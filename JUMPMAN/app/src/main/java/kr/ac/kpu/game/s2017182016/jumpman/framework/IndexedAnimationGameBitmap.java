package kr.ac.kpu.game.s2017182016.jumpman.framework;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;


public class IndexedAnimationGameBitmap extends AnimationGameBitmap{

    private static final String TAG = IndexedAnimationGameBitmap.class.getSimpleName();
    private final int frameHeight;

    public IndexedAnimationGameBitmap(int resId, float framesPerSecond, int frameCount) {
        super(resId, framesPerSecond, frameCount);
        this.frameWidth = 50;
        this.frameHeight = 50;
    }
    protected ArrayList<Rect> srcRects;
    public void setIndices(int... indices){
        srcRects = new ArrayList<>();
        for(int index : indices){
            int x = index % 100;
            int y = index / 100;
            int l =  x * 50; //270 + 2 (경계)
            int t =  y * 50;
            int r = l + 50;
            int b = t + 50;

            srcRects.add(new Rect(l,t,r,b));
        }
        frameCount = indices.length;
    }

    @Override
    public void draw(Canvas canvas, float x, float y) {
        int elapsed = (int)(System.currentTimeMillis() - createdOn);
        frameIndex = Math.round(elapsed * 0.001f * framesPerSecond) % frameCount;

        int fw = frameWidth;
        int h = frameHeight;
        float hw = fw / 2 * GameView.MULTIPLIER;
        float hh = h / 2 * GameView.MULTIPLIER;
        //srcRect.set(fw * frameIndex, 0, fw * frameIndex + fw, h);
        dstRect.set(x - hw, y - hh, x + hw, y + hh);
        canvas.drawBitmap(bitmap, srcRects.get(frameIndex), dstRect, null);
    }
}
