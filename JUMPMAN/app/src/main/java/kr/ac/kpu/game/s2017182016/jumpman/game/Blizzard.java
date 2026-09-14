package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;

/**
 * 바람 구간(눈보라) 연출 + 상태. 화면 전체에 날리는 눈 파티클을 그리고, 주기적으로
 * 좌우 방향이 바뀌는 바람 방향/세기를 정적 상태로 노출해서 Player의 이동/점프에 반영한다.
 */
public class Blizzard implements GameObject {
    private static final float DIRECTION_PERIOD = 5.0f;
    private static final int PARTICLE_COUNT = 50;
    private static final float FALL_SPEED = 60f;
    private static final float DRIFT_SPEED = 260f;
    private static final float DOT_RADIUS = 1.6f;

    /** +1 오른쪽, -1 왼쪽. Player가 이동/점프 계산에 참조한다. */
    public static int direction = 1;
    /** 바람 구간에 들어와 있는 동안 true. */
    public static boolean active = false;

    private float timer = 0f;
    private final float[] px = new float[PARTICLE_COUNT];
    private final float[] py = new float[PARTICLE_COUNT];
    private final float[] speedScale = new float[PARTICLE_COUNT];
    private final Paint paint = new Paint();
    private final Random rnd = new Random();

    public Blizzard() {
        active = true;
        paint.setColor(0xB0F5FAFF);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            respawn(i, true);
        }
    }

    private void respawn(int i, boolean randomX) {
        float w = GameView.gameWidth;
        float h = GameView.gameHeight;
        py[i] = rnd.nextFloat() * h;
        px[i] = randomX ? rnd.nextFloat() * w : (direction > 0 ? -20f : w + 20f);
        speedScale[i] = 0.6f + rnd.nextFloat() * 0.8f;
    }

    @Override
    public void update() {
        float dt = MainGame.get().frameTime;
        timer += dt;
        if (timer >= DIRECTION_PERIOD) {
            timer = 0f;
            direction *= -1;
        }
        float w = GameView.gameWidth;
        float h = GameView.gameHeight;
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] += direction * speedScale[i] * DRIFT_SPEED * dt;
            py[i] += speedScale[i] * FALL_SPEED * dt;
            if (py[i] > h) {
                py[i] = 0f;
                px[i] = rnd.nextFloat() * w;
            }
            if (px[i] < -25f || px[i] > w + 25f) {
                respawn(i, false);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            canvas.drawCircle(px[i], py[i], DOT_RADIUS * GameView.MULTIPLIER * speedScale[i], paint);
        }
    }

    public static void deactivate() {
        active = false;
    }
}
