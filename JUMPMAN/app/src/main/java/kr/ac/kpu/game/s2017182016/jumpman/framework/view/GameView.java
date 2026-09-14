package kr.ac.kpu.game.s2017182016.jumpman.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import kr.ac.kpu.game.s2017182016.jumpman.framework.game.BaseGame;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.GameSettings;
import kr.ac.kpu.game.s2017182016.jumpman.framework.util.Sound;

public class GameView extends View {

    public static final int MULTIPLIER = 3;
    /** 레벨이 480x360(4:3) 기준으로 만들어져서, 화면비가 다른 기기에서도 늘어나지 않도록 이 비율로 레터박스/필러박스 처리한다. */
    private static final float DESIGN_ASPECT = 480f / 360f;
    private static final String TAG = GameView.class.getSimpleName();
    private boolean running;

    private long lastFrame;
    public static GameView view;

    /**
     * 실제 화면 안에서 4:3 비율을 유지하는 게임 영역(뷰포트)의 크기/오프셋.
     * View#getWidth()/getHeight()는 final이라 오버라이드할 수 없어서, 게임 로직 전체가
     * 게임 로직 전체가 기존 GameView.view.getWidth()/getHeight() 대신 이 정적 필드를 참조하도록 바꿨다.
     */
    public static int gameWidth;
    public static int gameHeight;
    private int viewportOffsetX;
    private int viewportOffsetY;

    public GameView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        GameView.view = this;
        GameSettings.init(context);
        Sound.init(context);
        running = true;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        computeViewport(w, h);
        BaseGame game = BaseGame.get();
        Log.d(TAG,"null? "+ game.frameTime);
        boolean justInitialized = game.initResources();
        if (justInitialized) {
            requestFocus();
            requestCallback();
        }
    }

    private void computeViewport(int w, int h) {
        if (w <= 0 || h <= 0) {
            gameWidth = w;
            gameHeight = h;
            viewportOffsetX = 0;
            viewportOffsetY = 0;
            return;
        }
        if ((float) w / h > DESIGN_ASPECT) {
            gameHeight = h;
            gameWidth = Math.round(h * DESIGN_ASPECT);
            viewportOffsetX = (w - gameWidth) / 2;
            viewportOffsetY = 0;
        } else {
            gameWidth = w;
            gameHeight = Math.round(w / DESIGN_ASPECT);
            viewportOffsetX = 0;
            viewportOffsetY = (h - gameHeight) / 2;
        }
    }

    private void update(){
        BaseGame game = BaseGame.get();
        game.update();

        invalidate();
    }
    private void requestCallback() {
        if (!running) {
            Log.d(TAG, "Not running. Not calling Choreographer.postFrameCallback()");
            return;
        }
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long time) {
                if (lastFrame == 0) {
                    lastFrame = time;
                }
                BaseGame game = BaseGame.get();
                game.frameTime = (float) (time - lastFrame) / 1_000_000_000;
                update();
                lastFrame = time;
                requestCallback();
            }
        });
    }



    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.save();
        canvas.translate(viewportOffsetX, viewportOffsetY);
        canvas.clipRect(0, 0, gameWidth, gameHeight);
        BaseGame game = BaseGame.get();
        game.draw(canvas);
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        event.offsetLocation(-viewportOffsetX, -viewportOffsetY);
        BaseGame game = BaseGame.get();
        return game.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BaseGame game = BaseGame.get();
        if (game != null && game.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
