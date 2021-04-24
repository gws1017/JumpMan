package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.RectF;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.GameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.GameObject;

public class Player implements GameObject, BoxCollidable {

    private final float x;
    private final float y;
    private final GameBitmap bitmap;

    Player(float x, float y){
        this.x= x;
        this.y =y;
        this.bitmap = new GameBitmap(R.mipmap.base);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        bitmap.draw(canvas,x,y);

    }

    @Override
    public void getBoundingRect(RectF rect) {
        bitmap.getBoundingRect(x,y,rect);
    }
}
