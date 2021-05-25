package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

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
    private static final float JUMPPOWERX = 20;
    private static final float GRAVITY = 2000;
    public static final int MAX_JUMPPOWER = 43;
    private final Background bg;
    private float x;
    private float y;
    private final IndexedAnimationGameBitmap bitmap;
    private final IndexedAnimationGameBitmap bitmap2;
    private final Joystick joystick;
    private double velocityX;
    private double velocityY;
    private int chargetime = 0;
    private int isInverse = 1;
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

        if(state == State.ready){
           chargetime++;
           if(chargetime > MAX_JUMPPOWER)
           {
               jump();
               return;
           }
           else return;
        } else if(state == State.jump){
            float y = (float) (this.y + velocityY*game.frameTime);
            float x = (float) (this.x - isInverse*velocityX*game.frameTime);
            velocityY += GRAVITY*game.frameTime;
//            velocityX += GRAVITY*game.frameTime;
            this.y = y;
            this.x = x;
        } else{
            velocityX = joystick.getActuatorX()* MAX_SPEED *game.frameTime;
            x += velocityX;

        }
        if(this.y>=ground_y)
        {
            if(!bg.isFirst())this.y = ground_y2;
            else this.y = ground_y;
            setState(State.idle);
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
            velocityX = -JUMPPOWERX *this.chargetime;
        }
        else{
            Log.d(TAG,"Not in a state that can't jump " + state);
            return;
        }
        Log.d(TAG,"y " + chargetime);

            this.chargetime = 0;

    }
}
