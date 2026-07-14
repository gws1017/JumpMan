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
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.LeftRightPad;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;

public class Player implements GameObject, BoxCollidable {

    private static final float MAX_SPEED = 300.0f*GameView.view.getWidth()/2200;
    private static final float SLOPE_SLIDE_SPEED = 420.0f * GameView.view.getWidth() / 2200;
    private static final String TAG = Player.class.getSimpleName();
    private static final float JUMPPOWERY = 30;
    private static final float JUMPPOWERX = 18;
    /** 조이스틱을 위로 당긴 정도로 제자리 점프 판정 */
    private static final float JOYSTICK_UP_THRESHOLD = -0.4f;
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
    private LeftRightPad movePad;
    private double velocityX;
    private double jumpX;
    private double velocityY;
    private float chargetime = 0;
    private float prevchargetime = 0;
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
    private Rect COL_BOX_OFFSETS_IDLE = new Rect(-15, -12, 15, 15);
    private Rect collisionOffsetRect = COL_BOX_OFFSETS_IDLE;
    private float playerWidth = 35;
    private float px;
    private RectF collisionRect = new RectF();
    private int directionX = 1;
    private int directionY = 1;
    private boolean collisionHandle = false;
    private float bumpSoundCooldown = 0f;


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

    public void setMovePad(LeftRightPad movePad) {
        this.movePad = movePad;
    }

    /** 좌우 패드 우선, 없으면 조이스틱 X. -1~1 */
    public float getMoveInputX() {
        if (movePad != null && movePad.isEnabled()) {
            int dir = movePad.getDirection();
            if (dir != 0) {
                return dir;
            }
            return 0f;
        }
        return (float) joystick.getActuatorX();
    }

    /** 상단 유지(십자 ▲) 또는 조이스틱 위로 드래그 */
    public boolean wantsVerticalJump() {
        if (movePad != null && movePad.isEnabled()) {
            return movePad.isHoldingUp();
        }
        return joystick.getActuatorY() <= JOYSTICK_UP_THRESHOLD;
    }

    public void update() {
        MainGame game = MainGame.get();
        MainScene scene =MainScene.scene;

        float foot = y + collisionOffsetRect.bottom * GameView.MULTIPLIER;

        if (state == State.ready) {
            // 점프 차징 중: 좌우 이동 정지, 바라보는 방향만 갱신
            float aimX = getMoveInputX();
            int face = isInverse;
            if (aimX > 0.2f) {
                face = 1;
            } else if (aimX < -0.2f) {
                face = -1;
            }
            if (face != isInverse) {
                isInverse = face;
                setState(State.ready);
            }
            chargetime += 60 * game.frameTime * GameView.view.getHeight() / 1003;
            if (chargetime > MAX_JUMPPOWER) {
                jump();
                return;
            } else return;
        }
        else if (state == State.jump || state == State.falling) {
            if (bumpSoundCooldown > 0) {
                bumpSoundCooldown -= game.frameTime;
            }

            // Horizontal speed from charge; facing is directionX / isInverse
            float dx;
            if (isInverse == 1) {
                dx = (float) (Math.abs(jumpX) * game.frameTime);
            } else {
                dx = (float) (Math.abs(jumpX) * game.frameTime);
            }
            if (state == State.falling) {
                dx /= 1.5f;
            }

            float dy = (float) (velocityY * game.frameTime);

            // --- X axis: move, then resolve. Bounce only when we hit while moving into a wall. ---
            float prevX = x;
            x += directionX * dx;
            float leftBound = 8f * GameView.view.getWidth() / 480f + playerWidth;
            float rightBound = 472f * GameView.view.getWidth() / 480f - playerWidth;
            if (x < leftBound) x = leftBound;
            if (x > rightBound) x = rightBound;

            getBoundingRect(collisionRect);
            if (CollisionDetect(collisionRect)) {
                x = prevX;
                getBoundingRect(collisionRect);
                // Still inside (spawned/embedded): nudge out instead of flipping every frame
                if (CollisionDetect(collisionRect)) {
                    separateFromWalls();
                } else {
                    // Clean side hit → bounce once
                    directionX *= -1;
                    isInverse *= -1;
                    setState(State.falling);
                    playBumpSound();
                }
            }

            // --- Y axis ---
            float prevY = y;
            foot = y + collisionOffsetRect.bottom * GameView.MULTIPLIER;
            float platformTop = findNearestPlatformTop();

            if (velocityY >= 0 && (foot + dy) >= platformTop) {
                dy = platformTop - foot;
                y += dy;
                velocityY = 0;
                Sound.play(R.raw.king_land);
                if (!tryStartSlopeSlide(game.frameTime)) {
                    setState(State.idle);
                }
            } else {
                y += dy;
                getBoundingRect(collisionRect);
                if (CollisionDetect(collisionRect)) {
                    // Ceiling or embedded vertically
                    if (velocityY < 0) {
                        // Hit underside → bounce down a bit
                        y = prevY;
                        velocityY = Math.abs(velocityY) * 0.35f;
                        setState(State.falling);
                        playBumpSound();
                    } else {
                        // Falling into geometry: snap onto nearest top if possible
                        y = prevY;
                        foot = y + collisionOffsetRect.bottom * GameView.MULTIPLIER;
                        platformTop = findNearestPlatformTop();
                        if (foot + 4 >= platformTop) {
                            y = platformTop - collisionOffsetRect.bottom * GameView.MULTIPLIER;
                            velocityY = 0;
                            Sound.play(R.raw.king_land);
                            if (!tryStartSlopeSlide(game.frameTime)) {
                                setState(State.idle);
                            }
                        } else {
                            separateFromWalls();
                        }
                    }
                }
            }

            if (state == State.jump || state == State.falling) {
                velocityY += GRAVITY * game.frameTime;
            }
        }
        else if (state == State.idle || state == State.move) {
            if (tryStartSlopeSlide(game.frameTime)) {
                // Red slope: keep sliding, no stand still
            } else {
                px = x;
                float moveX = getMoveInputX();
                velocityX = moveX * MAX_SPEED * game.frameTime;
                x += velocityX;
                getBoundingRect(collisionRect);
                if (CollisionDetect(collisionRect)) x = px;

                if (moveX > 0.2f) {
                    isInverse = 1;
                    directionX = 1;
                    setState(State.move);
                } else if (moveX < -0.2f) {
                    isInverse = -1;
                    directionX = -1;
                    setState(State.move);
                } else {
                    setState(State.idle);
                }
                float platformTop = findNearestPlatformTop();
                if ((int) foot < (int) platformTop) {
                    setState(State.falling);
                    if (state != State.falling) velocityY = 0;
                }
            }
        }


        // 맵 이동 (level.png 스크린 단위)
        if (y < 0) {
            if (!mg.isLast()) {
                y = GameView.view.getHeight() - 30 * GameView.view.getHeight() / 360;
                bg.nextimg();
                mg.nextimg();
                fg.nextimg();
                clearPlatforms(scene);
                scene.add(MainScene.Layer.controller, new StageMap(mg.num));
            }
        } else if (y >= GameView.view.getHeight() - 30 * GameView.view.getHeight() / 360) {
            if (!mg.isFirst()) {
                y = 10;
                bg.previmg();
                mg.previmg();
                fg.previmg();
                setState(State.jump);
                clearPlatforms(scene);
                scene.add(MainScene.Layer.controller, new StageMap(mg.num));
            }
        }


        //방향전환
        if (this.state != State.jump && this.state != State.ready && this.state != State.falling) {

        }


    }

    private void clearPlatforms(MainScene scene) {
        ArrayList<GameObject> platforms = scene.objectsAt(MainScene.Layer.platform);
        for (GameObject obj : platforms) {
            scene.remove((Platform) obj);
        }
    }

    private float findNearestPlatformTop() {
        Platform platform = findNearestPlatform();
        if (platform == null) {
            return GameView.view.getHeight() - 3;
        }
        return platform.getBoundingRect().top - 3;
    }

    private Platform findNearestPlatform() {
        ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
        float top = GameView.view.getHeight();
        float offset = 5;
        Platform nearest = null;
        for (GameObject obj : platforms) {
            Platform platform = (Platform) obj;
            RectF rect = platform.getBoundingRect();
            if (x + collisionOffsetRect.right * GameView.MULTIPLIER - offset < rect.left) {
                continue;
            }
            if (x + collisionOffsetRect.left * GameView.MULTIPLIER + offset > rect.right) {
                continue;
            }
            if (rect.top < y) {
                continue;
            }
            if (top > rect.top) {
                top = rect.top;
                nearest = platform;
            }
        }
        return nearest;
    }

    /** If standing on a red slope, push along slopeDir and enter falling. */
    private boolean tryStartSlopeSlide(float frameTime) {
        Platform platform = findNearestPlatform();
        if (platform == null || !platform.isSlope()) {
            return false;
        }
        float foot = y + collisionOffsetRect.bottom * GameView.MULTIPLIER;
        float top = platform.getBoundingRect().top;
        if (foot > top + 12) {
            return false;
        }
        int dir = platform.slopeDir;
        directionX = dir;
        isInverse = dir;
        // falling uses abs(jumpX)*dt / 1.5 — keep enough speed to actually slide
        jumpX = SLOPE_SLIDE_SPEED * 1.8;
        velocityX = SLOPE_SLIDE_SPEED * dir * frameTime;
        x += velocityX;
        getBoundingRect(collisionRect);
        if (CollisionDetect(collisionRect)) {
            x -= velocityX;
        }
        if (velocityY < 80) {
            velocityY = 140;
        }
        setState(State.falling);
        return true;
    }

    private void playBumpSound() {
        if (bumpSoundCooldown <= 0) {
            Sound.play(R.raw.king_bump);
            bumpSoundCooldown = 0.12f;
        }
    }

    /** Push player out of overlapping platforms horizontally (stops jitter). */
    private void separateFromWalls() {
        getBoundingRect(collisionRect);
        if (!CollisionDetect(collisionRect)) {
            return;
        }
        float step = 2f;
        for (int i = 0; i < 48; i++) {
            x -= step;
            getBoundingRect(collisionRect);
            if (!CollisionDetect(collisionRect)) {
                directionX = -1;
                isInverse = -1;
                setState(State.falling);
                playBumpSound();
                return;
            }
            x += step;
            x += step;
            getBoundingRect(collisionRect);
            if (!CollisionDetect(collisionRect)) {
                directionX = 1;
                isInverse = 1;
                setState(State.falling);
                playBumpSound();
                return;
            }
            x -= step;
            step += 1f;
        }
    }

    boolean CollisionDetect(RectF rect) {
        MainGame game = (MainGame) MainGame.get();
        ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
        float top = GameView.view.getHeight();
        for (GameObject obj : platforms) {
                Platform platform = (Platform) obj;
                RectF rect2 = platform.getBoundingRect();
                if (rect.right < rect2.left || rect.left > rect2.right) continue;
                if (rect.bottom <= rect2.top || rect.top >= rect2.bottom) continue;

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
        if (state == State.idle || state == State.move) {
            setState(State.ready);
        }
    }

    public void cancelReady() {
        if (state == State.ready) {
            chargetime = 0;
            setState(State.idle);
        }
    }

    public void resetSpawn() {
        x = GameView.view.getWidth() / 2f;
        y = GameView.view.getHeight() - 80f * GameView.view.getHeight() / 360f;
        velocityX = 0;
        velocityY = 0;
        jumpX = 0;
        chargetime = 0;
        prevchargetime = 0;
        directionX = 1;
        setState(State.idle);
    }

    public void jump() {
        if (state == State.ready) {
            Sound.play(R.raw.king_jump);
            setState(State.jump);
            velocityY = -JUMPPOWERY * this.chargetime;

            // 기본: 바라보는 방향으로 점프. ▲/조이 위 = 제자리(수직)
            directionX = isInverse == 0 ? 1 : isInverse;
            if (wantsVerticalJump()) {
                jumpX = 0;
            } else {
                jumpX = JUMPPOWERX * this.chargetime;
                if (MAX_JUMPPOWER * 0.6 > chargetime) {
                    jumpX *= 1.8;
                }
            }
            this.prevchargetime = this.chargetime;
            this.chargetime = 0;
        }
    }
}
