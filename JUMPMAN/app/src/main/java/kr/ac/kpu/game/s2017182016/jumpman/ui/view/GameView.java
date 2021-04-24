package kr.ac.kpu.game.s2017182016.jumpman.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;

import androidx.annotation.Nullable;

import kr.ac.kpu.game.s2017182016.jumpman.game.MainGame;

public class GameView extends View {

    public static final int MULTIPLIER = 1;
    private long lastFrame;

    public static GameView view;

    public GameView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        GameView.view = this;

        startUpdating();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        MainGame game = MainGame.get();
        game.initResources();
    }

    private void startUpdating() { doGameFrame();}

    private void doGameFrame() {
        // update
        MainGame game = MainGame.get();
        game.update();
        //draw
        invalidate(); //화면 갱신

        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long time) {
                if (lastFrame == 0) {
                    lastFrame = time;
                }
                game.frameTime = (float) (time - lastFrame) / 1_000_000_000;
                doGameFrame();
                lastFrame = time;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        MainGame game = MainGame.get();
        game.draw(canvas);
    }
}
