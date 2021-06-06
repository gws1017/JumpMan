package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.bitmap.IndexedAnimationGameBitmap;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Foreground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Midground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.Sound;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;

public class Player implements GameObject, BoxCollidable {

    private static final float MAX_SPEED = 300.0f*GameView.view.getWidth()/2200;
    private static final String TAG = Player.class.getSimpleName();
    private static final float JUMPPOWERY = 30;
    private static final float JUMPPOWERX = 18;
    private static final float GRAVITY = GameView.view.getHeight()*2050/1003;
    public static final int MAX_JUMPPOWER = GameView.view.getHeight()*43/1003;
    private final Background bg;
    private final Midground mg;
    private final Foreground fg;
    private float x;
    private float y;
    private final IndexedAnimationGameBitmap bitmap;
    private final IndexedAnimationGameBitmap bitmap2;
    private final Joystick joystick;
    private double velocityX;
    private double jumpX;
    private double velocityY;
    private int chargetime = 0;
    private int prevchargetime = 0;
    private int isInverse = 1;
    private float ground_y;
    private float ground_y2;
    private int[] ANIM_INDICES_IDLE = {0};
    private int[] ANIM_INDICES_INV_IDLE = {3};
    private int[] ANIM_INDICES_MOVE = {0, 1, 2, 3};
    private int[] ANIM_INDICES_INV_MOVE = {3, 2, 1, 0};
    private int[] ANIM_INDICES_READY = {100};
    private int[] ANIM_INDICES_INV_READY = {103};
    private int[] ANIM_INDICES_Jump = {101};
    private int[] ANIM_INDICES_INV_Jump = {102};
    private int[] ANIM_INDICES_Falling = {102};
    private int[] ANIM_INDICES_INV_Falling = {101};
    private Rect COL_BOX_OFFSETS_IDLE = new Rect(-15, -15, 15, 15);
    private Rect collisionOffsetRect = COL_BOX_OFFSETS_IDLE;
    private float playerWidth = 35;
    private float px;
    private RectF collisionRect = new RectF();
    private int directionX = 1;
    private int directionY = 1;
    private boolean collisionHandle = false;


    private enum State {
        idle, move, ready, jump, falling, land
    }

    public void setState(State state) {
        this.state = state;
        int[] indices = ANIM_INDICES_IDLE;
        if (isInverse == 1) {
            switch (state) {
                case idle:
                    indices = ANIM_INDICES_IDLE;
                    break;
                case move:
                    indices = ANIM_INDICES_MOVE;
                    break;
                case ready:
                    indices = ANIM_INDICES_READY;
                    break;
                case jump:
                    indices = ANIM_INDICES_Jump;
                    break;
                case falling:
                    indices = ANIM_INDICES_Falling;
                    break;
            }
            bitmap.setIndices(indices);
        } else if (isInverse == -1) {
            switch (state) {
                case idle:
                    indices = ANIM_INDICES_INV_IDLE;
                    break;
                case move:
                    indices = ANIM_INDICES_INV_MOVE;
                    break;
                case ready:
                    indices = ANIM_INDICES_INV_READY;
                    break;
                case jump:
                    indices = ANIM_INDICES_INV_Jump;
                    break;
                case falling:
                    indices = ANIM_INDICES_INV_Falling;
                    break;
            }
            bitmap2.setIndices(indices);
        }

    }

    private State state = State.idle;

    public Player(float x, float y, Joystick joystick) {
        this.x = x;
        this.y = y;
        this.bitmap = new IndexedAnimationGameBitmap(R.mipmap.base, 4.5f, 0);
        setState(State.idle);
        this.bitmap2 = new IndexedAnimationGameBitmap(R.mipmap.base2, 4.5f, 0);
        this.isInverse = -1;
        setState(State.idle);
        this.joystick = joystick;
        this.bg = MainScene.bg;
        this.mg = MainScene.mg;
        this.fg = MainScene.fg;
        this.ground_y = y;
        this.ground_y2 = 1500;
        Sound.init(GameView.view.getContext());

    }

    public void update() {
        MainGame game = MainGame.get();
        MainScene scene =MainScene.scene;

        float foot = y + collisionOffsetRect.bottom * GameView.MULTIPLIER;

        if (state == State.ready) {
            chargetime += 60*game.frameTime*GameView.view.getHeight()/1003;
            if (chargetime > MAX_JUMPPOWER) {
                jump();
                return;
            } else return;
        } else if (state == State.jump || state == State.falling) {
            float dy = (float) (velocityY * game.frameTime);
            float platformTop = findNearestPlatformTop();
            float dx = directionX*this.x;
            getBoundingRect(collisionRect);

            if(CollisionDetect(collisionRect)) {
                Sound.play(R.raw.king_bump);
                directionX *= -1;
                setState(State.falling);
                Log.d(TAG,"dx " + jumpX+" directionX " + directionX +" state "+state);

                if(state == State.jump){
                velocityY = -JUMPPOWERY* this.prevchargetime/2;
                dy = (float) (velocityY * game.frameTime);
                this.prevchargetime = 0;
                }

            }

            if (isInverse == 1) {
                if (jumpX < 0) jumpX *= -1;
                dx = (float) (jumpX * game.frameTime);
            if (state == State.falling) dx = (float) (jumpX * game.frameTime /1.5);
            } else if (isInverse == -1) {
                if (jumpX > 0) jumpX *= -1;
                dx = (float) (jumpX * game.frameTime);
            if (state == State.falling) dx = (float) (jumpX * game.frameTime /1.5);
            }


//            if(state == State.falling) jumpX -= jumpX/100;
            this.x = x + directionX * dx;

            if (this.x - playerWidth < 8*GameView.view.getWidth()/480) this.x = 8*GameView.view.getWidth()/480 + playerWidth;
            else if (this.x + playerWidth > 472*GameView.view.getWidth()/480) this.x = 472*GameView.view.getWidth()/480 - playerWidth;
            if (velocityY >= 0) {
                if ((int)(foot + dy) >= (int)platformTop ) {
                    dy = platformTop - foot;

                    setState(State.idle);
                    directionX = 1;
                    Sound.play(R.raw.king_land);

                }
            }
            float c = CollisionDetectY(collisionRect);
            if(c != -100){
                Sound.play(R.raw.king_bump);

                this.y  = c + playerWidth;
                if(dy < 0) dy *= -1;
                if(velocityY< 0) velocityY *= -1;

            }
            this.y = y +  dy;

            velocityY +=  GRAVITY * game.frameTime;

        } else if (state == State.idle || state == State.move) {
            directionX = 1;
            px = x;
            velocityX = joystick.getActuatorX() * MAX_SPEED * game.frameTime;
            x += velocityX;
            getBoundingRect(collisionRect);
            if(CollisionDetect(collisionRect)) x = px;

        }


        //맵이동
        if(mg.num<6){
            if (y < 0) {

                if (!mg.isLast()) {
                    y = GameView.view.getHeight()-100;
                    bg.nextimg();
                    mg.nextimg();
                    fg.nextimg();
                    ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
                    for (GameObject obj : platforms) {
                        Platform platform = (Platform) obj;
                        MainScene.scene.remove(platform);
                    }
                    scene.add(MainScene.Layer.controller,new StageMap(mg.num));
                }
            } else if (y >= GameView.view.getHeight()-100) {
                if (!mg.isFirst()) {

                    y = 10;
                    bg.previmg();
                    mg.previmg();
                    fg.previmg();
                    setState(State.jump);
                    ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
                    for (GameObject obj : platforms) {
                        Platform platform = (Platform) obj;
                        scene.remove(platform);
                    }
                    scene.add(MainScene.Layer.controller,new StageMap(mg.num));

                }
            }
        }


        //방향전환
        if (this.state != State.jump && this.state != State.ready) {
            if (velocityX > 0) {
                isInverse = 1;
                setState(State.move);
            } else if (velocityX < 0) {
                isInverse = -1;
                setState(State.move);
            } else {
                setState(State.idle);
            }
            float platformTop = findNearestPlatformTop();

            if ((int)foot < (int)platformTop) {
                setState(State.falling);
                if(state != State.falling)velocityY = 0; //조건문 안걸면 추락하지않음
//                velocityX = 0;
                //this.y += 0.01;
            }
        }


    }

    private float findNearestPlatformTop() {
        MainGame game = (MainGame) MainGame.get();
        ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
        float top = GameView.view.getHeight();
        for (GameObject obj : platforms) {
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

        return top;
    }

    boolean CollisionDetect(RectF rect) {
        MainGame game = (MainGame) MainGame.get();
        ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
        float top = GameView.view.getHeight();
        for (GameObject obj : platforms) {
                Platform platform = (Platform) obj;
                RectF rect2 = platform.getBoundingRect();
                if (rect.right < rect2.left || rect.left > rect2.right) continue;
                if (rect.top < rect2.top || rect.bottom > rect2.bottom) continue;
               
                return true;
        }
        return false;
    }



    private float CollisionDetectY(RectF rect) {
        MainGame game = (MainGame) MainGame.get();
        ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
        float top = GameView.view.getHeight();
        for (GameObject obj : platforms) {
            Platform platform = (Platform) obj;
            RectF rect2 = platform.getBoundingRect();



            if(rect.left >rect2.left&& rect.right < rect2.right)
                if(rect.top < rect2.bottom && rect.top > rect2.top)
                    return rect2.bottom;
        }
        return -100;
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
            //
            return;
        }
    }
     // 43: 1003 = ? vh2
    public void jump() {
        MainGame game =MainGame.get();

         if(state == State.ready){
             Sound.play(R.raw.king_jump);
            setState(State.jump);
            velocityY = -JUMPPOWERY *this.chargetime;
            jumpX = -JUMPPOWERX *this.chargetime;
        }
        else{
           //Log.d(TAG,"Not in a state that can't jump " + state);
            return;
        }
            this.prevchargetime = this.chargetime;
            this.chargetime = 0;

    }
}
