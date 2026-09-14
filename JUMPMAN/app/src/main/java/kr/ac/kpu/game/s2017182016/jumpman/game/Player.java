package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.GameSettings;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.Sound;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.LeftRightPad;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;

public class Player implements GameObject, BoxCollidable {

    private static final float MAX_SPEED = 300.0f*GameView.gameWidth/2200;
    private static final float SLOPE_SLIDE_SPEED = 420.0f * GameView.gameWidth / 2200;
    /** 얼음 위 가속/감속 스무딩 계수 (초당 목표속도에 도달하는 비율, 클수록 덜 미끄러짐) */
    private static final float ICE_SMOOTH_RATE = 4f;
    /** 디버깅: 치트키 ON일 때 차징 속도 배율 (원하는 세기에서 정확히 떼기 쉽게 느리게) */
    private static final float DEBUG_CHARGE_SLOWDOWN = 0.15f;
    /** 가로 점프력 곡선의 급증 구간 비율(충전량 기준)과 그 지점에서의 배율. xJumpPowerForCharge 참고. */
    private static final float X_RAMP_FRACTION = 0.3f;
    private static final float X_RAMP_BOOST = 2.4f;
    private static final String TAG = Player.class.getSimpleName();
    // 30 → 36: 최대 충전 시 도달 높이가 실제 필요 높이랑 거의 정확히 같아서(여유 0%,
    // 화면3 등에서 확실히 닿아야 할 지점을 못 넘고 모서리에 튕기는 문제) 여유를 두기 위해 상향.
    private static final float JUMPPOWERY = 36;
    private static final float JUMPPOWERX = 18;
    /** 조이스틱을 위로 당긴 정도로 제자리 점프 판정 */
    private static final float JOYSTICK_UP_THRESHOLD = -0.4f;
    private static final float GRAVITY = GameView.gameHeight*2050/1003;
    public static final int MAX_JUMPPOWER = GameView.gameHeight*43/1003;
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

    // --- 디버그: 점프 게이지 / 궤적·착지 예측 (치트키 ON일 때만 표시) ---
    private final Paint dbgGaugeBg = new Paint();
    private final Paint dbgGaugeFill = new Paint();
    private final Paint dbgText = new Paint();
    private final Paint dbgTrajectory = new Paint();
    private final Paint dbgLanding = new Paint();
    {
        dbgGaugeBg.setColor(0x99000000);
        dbgGaugeBg.setStyle(Paint.Style.FILL);
        dbgGaugeFill.setStyle(Paint.Style.FILL);
        dbgText.setColor(Color.WHITE);
        dbgText.setTextAlign(Paint.Align.CENTER);
        dbgText.setTextSize(11f * GameView.MULTIPLIER);
        dbgTrajectory.setColor(0xCC00E5FF);
        dbgTrajectory.setStyle(Paint.Style.FILL);
        dbgLanding.setColor(0xFFFF3B30);
        dbgLanding.setStyle(Paint.Style.STROKE);
        dbgLanding.setStrokeWidth(3f);
    }


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
        if (DebugCheats.cameraMode) {
            // 카메라 모드: 캐릭터 물리(중력·낙하·화면 자동 전환) 정지, 디버그 버튼으로만 화면 이동
            return;
        }

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
            // 보조모드: 원하는 차징 지점에서 정확히 뗄 수 있도록 차징 속도를 늦춰준다
            float chargeSpeedScale = isAssistModeOn() ? DEBUG_CHARGE_SLOWDOWN : 1f;
            chargetime += 60 * game.frameTime * GameView.gameHeight / 1003 * chargeSpeedScale;
            if (chargetime > MAX_JUMPPOWER) {
                if (isAssistModeOn()) {
                    // 보조모드: 꽉 차도 자동 점프하지 않고 게이지/궤적을 계속 볼 수 있게 유지, 손 뗄 때만 점프
                    chargetime = MAX_JUMPPOWER;
                    return;
                }
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
            float leftBound = 8f * GameView.gameWidth / 480f + playerWidth;
            float rightBound = 472f * GameView.gameWidth / 480f - playerWidth;
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
                    landOnGround(dx);
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
                                landOnGround(dx);
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
                Platform standingOn = findNearestPlatform();
                float targetVelX = moveX * MAX_SPEED * game.frameTime;
                if (standingOn != null && standingOn.isIce()) {
                    // 얼음: 목표 속도로 서서히 가속/감속 (관성으로 미끄러짐)
                    float smoothing = Math.min(1f, ICE_SMOOTH_RATE * game.frameTime);
                    velocityX += (targetVelX - velocityX) * smoothing;
                } else {
                    velocityX = targetVelX;
                }
                x += velocityX;
                getBoundingRect(collisionRect);
                if (CollisionDetect(collisionRect)) {
                    x = px;
                    velocityX = 0;
                }

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
                y = GameView.gameHeight - 30 * GameView.gameHeight / 360;
                bg.nextimg();
                mg.nextimg();
                fg.nextimg();
                clearPlatforms(scene);
                scene.add(MainScene.Layer.controller, new StageMap(mg.num));
            }
        } else if (y >= GameView.gameHeight - 30 * GameView.gameHeight / 360) {
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
        return findPlatformTopAt(x, y);
    }

    private Platform findNearestPlatform() {
        return findNearestPlatformAt(x, y);
    }

    private float findPlatformTopAt(float px, float py) {
        Platform platform = findNearestPlatformAt(px, py);
        if (platform == null) {
            return GameView.gameHeight - 3;
        }
        return platform.getBoundingRect().top - 3;
    }

    private Platform findNearestPlatformAt(float px, float py) {
        ArrayList<GameObject> platforms = MainScene.scene.objectsAt(MainScene.Layer.platform);
        float top = GameView.gameHeight;
        float offset = 5;
        Platform nearest = null;
        for (GameObject obj : platforms) {
            Platform platform = (Platform) obj;
            RectF rect = platform.getBoundingRect();
            if (px + collisionOffsetRect.right * GameView.MULTIPLIER - offset < rect.left) {
                continue;
            }
            if (px + collisionOffsetRect.left * GameView.MULTIPLIER + offset > rect.right) {
                continue;
            }
            if (rect.top < py) {
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
        float top = GameView.gameHeight;
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
        float top = GameView.gameHeight;
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

        if (isAssistModeOn()) {
            drawJumpDebug(canvas);
        }
    }

    private static boolean isAssistModeOn() {
        return GameSettings.get() != null && GameSettings.get().isAssistModeEnabled();
    }

    private void drawJumpDebug(Canvas canvas) {
        if (state == State.ready) {
            drawChargeGauge(canvas);
            float previewCharge = Math.min(chargetime, MAX_JUMPPOWER);
            int dir = isInverse == 0 ? 1 : isInverse;
            boolean vertical = wantsVerticalJump();
            double previewJumpX = vertical ? 0 : xJumpPowerForCharge(previewCharge);
            simulateAndDrawTrajectory(canvas, -JUMPPOWERY * previewCharge, previewJumpX, dir, false);
        } else if (state == State.jump || state == State.falling) {
            simulateAndDrawTrajectory(canvas, (float) velocityY, jumpX, directionX, state == State.falling);
        }
    }

    private void drawChargeGauge(Canvas canvas) {
        float mult = GameView.MULTIPLIER;
        float w = 50f * mult;
        float h = 8f * mult;
        float gx = x - w / 2f;
        float gy = y - 55f * mult;
        canvas.drawRect(gx, gy, gx + w, gy + h, dbgGaugeBg);
        float ratio = Math.min(1f, chargetime / MAX_JUMPPOWER);
        dbgGaugeFill.setColor(chargeColor(ratio));
        canvas.drawRect(gx, gy, gx + w * ratio, gy + h, dbgGaugeFill);
        canvas.drawText(Math.round(ratio * 100) + "%", gx + w / 2f, gy - 4f * mult, dbgText);
    }

    private int chargeColor(float ratio) {
        int r = (int) (ratio * 255);
        int g = (int) ((1f - ratio) * 255);
        return 0xFF000000 | (r << 16) | (g << 8);
    }

    /** 현재 발사 조건으로 착지 지점까지 궤적을 시뮬레이션해 점선+착지 마커로 그린다. */
    private void simulateAndDrawTrajectory(Canvas canvas, float launchVelY, double launchJumpX, int launchDir, boolean startFalling) {
        float mult = GameView.MULTIPLIER;
        float leftBound = 8f * GameView.gameWidth / 480f + playerWidth;
        float rightBound = 472f * GameView.gameWidth / 480f - playerWidth;
        float screenBottom = GameView.gameHeight - 30f * GameView.gameHeight / 360f;
        float dt = 1f / 60f;
        int maxSteps = 180;

        float simX = x;
        float simY = y;
        float simVelY = launchVelY;
        int dir = launchDir;
        boolean fallingPhase = startFalling;
        RectF simRect = new RectF();

        float landX = Float.NaN;
        float landY = Float.NaN;
        int step = 0;
        for (; step < maxSteps; step++) {
            float dx = (float) (Math.abs(launchJumpX) * dt);
            if (fallingPhase) dx /= 1.5f;

            float prevX = simX;
            simX += dir * dx;
            if (simX < leftBound) simX = leftBound;
            if (simX > rightBound) simX = rightBound;
            buildBoundingRectAt(simRect, simX, simY, mult);
            if (CollisionDetect(simRect)) {
                simX = prevX;
                dir *= -1;
                fallingPhase = true;
            }

            float foot = simY + collisionOffsetRect.bottom * mult;
            float platformTop = findPlatformTopAt(simX, simY);
            float dy = simVelY * dt;
            if (simVelY >= 0 && (foot + dy) >= platformTop) {
                simY += platformTop - foot;
                landX = simX;
                landY = simY;
                step++;
                break;
            }

            simY += dy;
            buildBoundingRectAt(simRect, simX, simY, mult);
            if (CollisionDetect(simRect)) {
                simY -= dy;
                landX = simX;
                landY = simY;
                step++;
                break;
            }

            simVelY += GRAVITY * dt;

            if (simY < 0 || simY >= screenBottom) {
                landX = simX;
                landY = simY;
                step++;
                break;
            }

            if (step % 3 == 0) {
                canvas.drawCircle(simX, simY, 3f * mult, dbgTrajectory);
            }
        }

        if (!Float.isNaN(landX)) {
            canvas.drawCircle(landX, landY, 10f * mult, dbgLanding);
            canvas.drawLine(landX - 14f * mult, landY, landX + 14f * mult, landY, dbgLanding);
            canvas.drawLine(landX, landY - 14f * mult, landX, landY + 14f * mult, dbgLanding);
        }
    }

    private void buildBoundingRectAt(RectF rect, float px, float py, float mult) {
        rect.set(
                px + collisionOffsetRect.left * mult,
                py + collisionOffsetRect.top * mult,
                px + collisionOffsetRect.right * mult,
                py + collisionOffsetRect.bottom * mult
        );
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

    /** 착지 처리: 얼음 위라면 방금 비행 중이던 수평 속도를 이어받아 살짝 미끄러지게 한다. */
    private void landOnGround(float lastFlightDx) {
        Platform landed = findNearestPlatform();
        if (landed != null && landed.isIce()) {
            velocityX = directionX * lastFlightDx;
        } else {
            velocityX = 0;
        }
        setState(State.idle);
    }

    public void cancelReady() {
        if (state == State.ready) {
            chargetime = 0;
            setState(State.idle);
        }
    }

    public void resetSpawn() {
        x = GameView.gameWidth / 2f;
        y = GameView.gameHeight - 80f * GameView.gameHeight / 360f;
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
                jumpX = xJumpPowerForCharge(this.chargetime);
            }
            this.prevchargetime = this.chargetime;
            this.chargetime = 0;
        }
    }

    /**
     * 가로 점프력 곡선: 0~30% 충전까지는 급격히 늘어나 X_RAMP_BOOST 배 지점까지 도달하고,
     * 그 이후 100%까지는 완만하게 (충전량에 선형으로) 최대치까지 늘어난다.
     * 그냥 선형으로만 스케일하면 "가깝고 높은" 점프(고충전)와 "멀고 낮은" 점프(저충전)의
     * 요구 조건이 서로 상충해서 둘 다 만족하는 값이 없었음 — 짧게 눌러도 멀리 나가게 하고,
     * 오래 눌렀을 때는 가로거리보다 세로(Y)만 더 늘도록 분리한 것.
     */
    private static float xJumpPowerForCharge(float chargetime) {
        float rampCap = MAX_JUMPPOWER * X_RAMP_FRACTION;
        float peakX = JUMPPOWERX * rampCap * X_RAMP_BOOST;
        if (chargetime <= rampCap) {
            return peakX * (chargetime / rampCap);
        }
        float fullLinear = JUMPPOWERX * MAX_JUMPPOWER;
        float frac = (chargetime - rampCap) / (MAX_JUMPPOWER - rampCap);
        return peakX + (fullLinear - peakX) * frac;
    }
}
