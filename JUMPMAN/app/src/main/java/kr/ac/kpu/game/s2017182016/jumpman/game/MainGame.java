package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.BuildConfig;
import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.game.BaseGame;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;

public class MainGame extends BaseGame {

    public static Background bg;
    private Player player;
    private Joystick joystick;

    public enum Layer{
        bg,player,platform,controller,LAYER_COUNT
    }
    public static MainGame get(){
        return (MainGame) instance;
    }

    public ArrayList<GameObject> objectsAt(Layer layer) {
        return objectsAt(layer.ordinal());
    }
    public void add(Layer layer, GameObject obj){
        add(layer.ordinal(),obj);
    }


    private boolean initialized;

    @Override
    public boolean initResources(){
        if(initialized){
            return false;
        }

// 180 : 480 = ? : width
        int w = GameView.view.getWidth();
        int h = GameView.view.getHeight();
        initLayers(Layer.LAYER_COUNT.ordinal());

        //joystick
        int cx = 70*w/480;
        int cy = h-70*h/350;
        int outRadius = h/20*GameView.MULTIPLIER;
        int inRadius = h/20*GameView.MULTIPLIER /2;

        bg = new Background(R.mipmap.bg_1);
        add(Layer.bg,bg);
        joystick = new Joystick(cx,cy,outRadius,inRadius);
        add(Layer.controller,joystick);
        add(Layer.controller,new StageMap(bg.num));

        player = new Player(w/2,h-140,joystick);
        add(Layer.player,player);

        initialized = true;
        return true;
    }

    @Override
    public void update() {
       super.update();
    }


    @Override
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

}
