package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.bitmap.IndexedAnimationGameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;

public class Player implements GameObject, BoxCollidable {

    private static final float MAX_SPEED = 300.0f;
    private static final String TAG = Player.class.getSimpleName();
    private static final float JUMPPOWERY = 30;
    private static final float JUMPPOWERX = 18;
    private static final float GRAVITY = 2000;
    public static final int MAX_JUMPPOWER = 43;
    private final Background bg;
    private float x;
    private float y;
    private final IndexedAnimationGameBitmap bitmap;
    private final IndexedAnimationGameBitmap bitmap2;
    private final Joystick joystick;
    private double velocityX;
    private double jumpX;
    private double velocityY;
    private int chargetime = 0;
    private int isInverse = 1;
    private int temp = 0;
    private float ground_y;
    private float ground_y2;
    private int[] ANIM_INDICES_IDLE = {0};
    private int[] ANIM_INDICES_INV_IDLE = {3};
    private int[] ANIM_INDICES_MOVE = {0,1,2,3};
    private int[] ANIM_INDICES_INV_MOVE = {3,2,1,0};
    private int[] ANIM_INDICES_READY = {100};
    private int[] ANIM_INDICES_INV_READY = {103};
    private int[] ANIM_INDICES_Jump = {101};
    private int[] ANIM_INDICES_INV_Jump = {102};
    private Rect COL_BOX_OFFSETS_IDLE = new Rect(-15,-15,15,15);
    private Rect collisionOffsetRect = COL_BOX_OFFSETS_IDLE;
    private float playerWidth = 35;
    private float px;


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
        this.ground_y2 = 1500;

    }

    public void update() {
        MainGame game = MainGame.get();
        px = this.x;
        if(state == State.ready){
           chargetime++;
           if(chargetime > MAX_JUMPPOWER)
           {
               jump();
               return;
           }
           else return;
        } else if(state == State.jump){
            float dy = (float) (this.y + velocityY*game.frameTime);
            float dx = this.x;
            if(isInverse == 1){
                if(jumpX < 0 ) jumpX *= -1;
                dx = (float) (this.x + jumpX*game.frameTime);
            }else if(isInverse == -1){
                if(jumpX > 0 ) jumpX *= -1;
                dx = (float) (this.x + jumpX*game.frameTime);
            }

            velocityY += GRAVITY*game.frameTime;
//            velocityX += GRAVITY*game.frameTime;
            this.y = dy;
            this.x = dx;
        } else{
            velocityX = joystick.getActuatorX()* MAX_SPEED *game.frameTime;
            x += velocityX;
        }


        //맵이동
        if(y<0) {
            if(!bg.isLast()) {
                y = 900;
                bg.nextimg();
            }
        }
        else if(y >GameView.view.getHeight()) {
            if(!bg.isFirst()) {
                y = 10;
                bg.previmg();
                setState(State.jump);
            }
        }

        //방향전환
        if(this.state != State.jump && this.state != State.ready) {
            if (velocityX > 0) {
                isInverse = 1;
                setState(State.move);
            } else if (velocityX < 0) {
                isInverse = -1;
                setState(State.move);
            } else {
                setState(State.idle);
            }
        }
        if (velocityY >= 0) {
            float foot = y + collisionOffsetRect.bottom * GameView.MULTIPLIER;
            float platformTop = findNearestPlatformTop();
            if (foot >= platformTop) {
                y -= foot - platformTop+5;
                setState(State.idle);
            }
        }
        if(this.x-playerWidth < 0 ) this.x = playerWidth;
        else if(this.x+playerWidth > GameView.view.getWidth()) this.x =GameView.view.getWidth()-playerWidth;
    }

    private float findNearestPlatformTop() {
        MainGame game = (MainGame)MainGame.get();
        ArrayList<GameObject> objects = game.objects;
        float top = GameView.view.getHeight();
        for (GameObject obj: objects) {
            if(obj instanceof Platform)
            {
                Platform platform = (Platform) obj;
                RectF rect = platform.getBoundingRect();

                if (rect.left > x || x > rect.right) {
                    continue;
                }
                if (rect.top < y) {
                    continue;
                }
                if (top > rect.top) {
                    top = rect.top;
                }

            }
        }
        return top;
    }
    boolean CollisionDetect(RectF rect){
      
        if(x+playerWidth <rect.left|| x -playerWidth > rect.right) return false;
        if(y+playerWidth <rect.top|| y -playerWidth > rect.bottom) return false;
        return true;
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
        float mult = GameView.MULTIPLIER;
        rect.set(
                x + collisionOffsetRect.left * mult,
                y + collisionOffsetRect.top * mult,
                x + collisionOffsetRect.right * mult,
                y + collisionOffsetRect.bottom * mult
        );
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
            velocityY = -JUMPPOWERY *this.chargetime;
            jumpX = -JUMPPOWERX *this.chargetime;
        }
        else{
            Log.d(TAG,"Not in a state that can't jump " + state);
            return;
        }
        Log.d(TAG,"y " + chargetime);

            this.chargetime = 0;

    }
}
