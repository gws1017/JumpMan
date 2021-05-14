package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.GameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.IndexedAnimationGameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.MainGame;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;

public class Player implements GameObject, BoxCollidable {

    private static final float MAX_SPEED = 3.5f;
    private static final String TAG = Player.class.getSimpleName();
    private static final float JUMPPOWER = 1200;
    private static final float GRAVITY = 2000;
    private final Background bg;
    private float x;
    private float y;
    private final IndexedAnimationGameBitmap bitmap;
    private final IndexedAnimationGameBitmap bitmap2;
    private final Joystick joystick;
    private double velocityX;
    private double velocityY;
    private int isInverse = 1;
    private float ground_y;

    private enum State{
        idle,jump,land
    }

    private State state = State.idle;

    public Player(float x, float y,Joystick joystick){
        this.x= x;
        this.y =y;
        this.ground_y =y;
        this.bitmap = new IndexedAnimationGameBitmap(R.mipmap.base,4.5f,0);
        this.bitmap.setIndices(0,1,2,3);
        this.bitmap2 = new IndexedAnimationGameBitmap(R.mipmap.base2,4.5f,0);
        this.bitmap2.setIndices(3,2,1,0);
        this.joystick = joystick;
        this.bg = MainGame.bg;

    }

    public void update() {
        MainGame game = MainGame.get();
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        x += velocityX;
        if(state == State.jump){
            float y = (float) (this.y + velocityY*game.frameTime);
            velocityY += GRAVITY*game.frameTime;
            this.y = y;
        }
        if(this.y>=ground_y)
        {
            y = ground_y;
            state = State.idle;
        }

        //방향전환
        if(velocityX>0) {
            isInverse = 1;
            if(state == State.jump) this.bitmap.setIndices(100,101);
            else this.bitmap.setIndices(0,1,2,3);
        } else if(velocityX < 0) {
            isInverse = -1;
            if(state == State.jump) this.bitmap2.setIndices(103,102);
            else this.bitmap2.setIndices(3,2,1,0);
        }
        else{

            this.bitmap.setIndices(0);
            this.bitmap2.setIndices(3);
            if(state == State.jump) this.bitmap.setIndices(101);
            if(state == State.jump) this.bitmap2.setIndices(102);
        }

        //맵이동
        if(y<0) {
            if(bg.isLast()) {
                y = 900;
                bg.nextimg();
            }
        }
        else if(y >GameView.view.getHeight()) {
            if(bg.isFirst()) {
                y = 10;
                bg.previmg();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(isInverse== 1){

            bitmap.draw(canvas,x,y);

        }else if(isInverse== -1){

            bitmap2.draw(canvas,x,y);

        }

    }

    @Override
    public void getBoundingRect(RectF rect) {

        bitmap.getBoundingRect(x,y,rect);
    }

    public void jump() {
        if(state != State.idle){
            Log.d(TAG,"Not in a state that can't jump " + state);
            return;
        }
        state = State.jump;
        velocityY = -JUMPPOWER;
    }
}
