package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.BuildConfig;
import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;

public class MainGame {

    public static MainGame instance;
    public static Background bg;
    private Player player;
    private Joystick joystick;
    private RectF collisionRect;
    private Paint collisionPaint;

    protected MainGame(){
        instance = this;
        if(BuildConfig.showsCollisionBox){
            collisionRect = new RectF();
            collisionPaint = new Paint();
            collisionPaint.setStyle(Paint.Style.STROKE);
            collisionPaint.setColor(Color.RED);
        }
    }
    public static MainGame get(){
        if(instance == null){
            instance = new MainGame();
        }
        return instance;
    }


    ArrayList<GameObject> objects = new ArrayList<>();
    public static float frameTime;
    private boolean initialized;

    public void initResources(){
        if(initialized){
            return;
        }


        joystick = new Joystick(400,800,100,50);
        bg = new Background(R.mipmap.bg_1);
        objects.add(bg);
        objects.add(joystick);
        int w = GameView.view.getWidth();//GameView.view.getWidth();//배경이 그려지는위치를 가져오기
        int h = GameView.view.getHeight();

        player = new Player(w/2,h-140,joystick);
        objects.add(player);
        initialized = true;
    }

    public void update() {
        if(!initialized) return;
        for(GameObject o : objects){
            o.update();
        }

    }
    public void draw(Canvas canvas){
        for(GameObject o : objects){
            o.draw(canvas);
        }
        if(BuildConfig.showsCollisionBox){
                for(GameObject o : objects){
                    if(!(o instanceof BoxCollidable)){
                        continue;
                    }
                    ((BoxCollidable)o).getBoundingRect(collisionRect);
                    canvas.drawRect(collisionRect,collisionPaint);
                }

        }
    }


    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                if(joystick.isPressed((double)event.getX(),(double)event.getY())){
                    joystick.setIsPressed(true);
                }
                else player.ready();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(joystick.getIsPressed()){
                    joystick.setActuator((double)event.getX(),(double)event.getY());
                }
                else player.ready();
                return true;
            case MotionEvent.ACTION_UP:
                joystick.setIsPressed(false);
                joystick.resetActuator();
                player.jump();
                return true;
        }
        return false;
    }
    public void add(GameObject gameObject) {
        GameView.view.post(new Runnable() {
            @Override
            public void run() {
                objects.add(gameObject);
            }
        });

    }
    public void remove(GameObject gameObject){
        GameView.view.post(new Runnable() {
            @Override
            public void run() {
                objects.remove(gameObject);
            }
        });

    }
}
