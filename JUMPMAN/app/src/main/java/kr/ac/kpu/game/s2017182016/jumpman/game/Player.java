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
    private static final float JUMPPOWER = 100;
    private static final float GRAVITY = 2000;
    private final Background bg;
    private float x;
    private float y;
    private final IndexedAnimationGameBitmap bitmap;
    private final IndexedAnimationGameBitmap bitmap2;
    private final Joystick joystick;
    private double velocityX;
    private double velocityY;
    private int chargtime = 0;
    private int isInverse = 1;
    private float ground_y;
    private int[] ANIM_INDICES_IDLE = {0};
    private int[] ANIM_INDICES_INV_IDLE = {3};
    private int[] ANIM_INDICES_MOVE = {0,1,2,3};
    private int[] ANIM_INDICES_INV_MOVE = {3,2,1,0};
    private int[] ANIM_INDICES_READY = {100};
    private int[] ANIM_INDICES_INV_READY = {103};
    private int[] ANIM_INDICES_Jump = {101};
    private int[] ANIM_INDICES_INV_Jump = {102};



    private enum State{
        idle,move,ready,jump,land
    }
    public void setState(State state){
        this.state = state;
        int[] indices = ANIM_INDICES_IDLE;
        if(isInverse == 1){
            switch (state){
                case idle: indices = ANIM_INDICES_IDLE; break;
                case move: indices = ANIM_INDICES_MOVE; break;
                case ready: indices =ANIM_INDICES_READY;break;
                case jump: indices = ANIM_INDICES_Jump;break;
            }
            bitmap.setIndices(indices);
        }else if(isInverse == -1){
            switch (state){
                case idle: indices = ANIM_INDICES_INV_IDLE;break;
                case move: indices = ANIM_INDICES_INV_MOVE; break;
                case ready: indices =ANIM_INDICES_INV_READY;break;
                case jump: indices = ANIM_INDICES_INV_Jump;break;
            }
            bitmap2.setIndices(indices);
        }

    }
    private State state = State.idle;

    public Player(float x, float y,Joystick joystick){
        this.x= x;
        this.y =y;
        this.bitmap = new IndexedAnimationGameBitmap(R.mipmap.base,4.5f,0);
        setState(State.idle);
        this.bitmap2 = new IndexedAnimationGameBitmap(R.mipmap.base2,4.5f,0);
        this.isInverse = -1;
        setState(State.idle);
        this.joystick = joystick;
        this.bg = MainGame.bg;
        this.ground_y =y;

    }

    public void update() {
        MainGame game = MainGame.get();
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        x += velocityX;
        if(state == State.ready){
           chargtime++;
           return;
        }
        if(state == State.jump){
            float y = (float) (this.y + velocityY*game.frameTime);
            velocityY += GRAVITY*game.frameTime;
            this.y = y;
        }
        if(this.y>=ground_y)
        {
            this.y = ground_y;
            setState(State.idle);
        }

        //방향전환
        if(velocityX>0) {
            isInverse = 1;
           setState(State.move);
        } else if(velocityX < 0) {
            isInverse = -1;
            setState(State.move);
        }
        else{
            if(this.state != State.jump)setState(State.idle);
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
        Log.d(TAG,"Current State : " + state);
        Log.d(TAG,"y " + this.y + " ground y "+ ground_y);
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
    public void ready() {
        if(state == State.idle) {
            setState(State.ready);
        }else{
            Log.d(TAG,"Not in a state that can't ready " + state);
            return;
        }
    }
    public void jump() {
         if(state == State.ready){
            setState(State.jump);
            velocityY = -JUMPPOWER*this.chargtime;
        }
        else{
            Log.d(TAG,"Not in a state that can't jump " + state);
            return;
        }

            this.chargtime = 0;

    }
}
