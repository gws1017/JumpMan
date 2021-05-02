package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.RectF;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.GameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.ui.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.ui.view.Joystick;

public class Player implements GameObject, BoxCollidable {

    private static final float MAX_SPEED = 3.5f;
    private float x;
    private final float y;
    private final GameBitmap bitmap;
    private final Joystick joystick;
    private double velocityX;

    Player(float x, float y,Joystick joystick){
        this.x= x;
        this.y =y;
        this.bitmap = new GameBitmap(R.mipmap.base);
        this.joystick = joystick;
    }

    public void update() {
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        x += velocityX;
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
