package kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main;

import android.media.MediaPlayer;
import android.view.MotionEvent;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.framework.game.Scene;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Foreground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Midground;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;
import kr.ac.kpu.game.s2017182016.jumpman.game.Player;
import kr.ac.kpu.game.s2017182016.jumpman.game.StageMap;

public class MainScene extends Scene {

    public static Background bg;
    public static Midground mg;
    public static Foreground fg;
    private Player player;
    private Joystick joystick;
    private MediaPlayer openingBgm;

    public MediaPlayer forestBgm;
    public MediaPlayer endBgm;
    public enum Layer{
        bg,mg,player,fg,platform,controller,LAYER_COUNT
    }

    public static MainScene scene;
    public void add(Layer layer, GameObject obj) {
        add(layer.ordinal(), obj);
    }
    public ArrayList<GameObject> objectsAt(Layer layer) {
        return objectsAt(layer.ordinal());
    }

    @Override
    public void start(){
        scene = this;
        super.start();

// 180 : 480 = ? : width
        int w = GameView.view.getWidth();
        int h = GameView.view.getHeight();
        initLayers(Layer.LAYER_COUNT.ordinal());
        openingBgm = MediaPlayer.create(GameView.view.getContext(),R.raw.opening_theme);
        forestBgm = MediaPlayer.create(GameView.view.getContext(),R.raw.nb_troll_forest);
        endBgm = MediaPlayer.create(GameView.view.getContext(),R.raw.ending3);
        openingBgm.start();
        openingBgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                forestBgm.setLooping(true);
                forestBgm.start();
            }
        });


        //joystick
        int cx = 70*w/480;
        int cy = h-70*h/360;
        int outRadius = h/20*GameView.MULTIPLIER;
        int inRadius = h/20*GameView.MULTIPLIER /2;

        bg = new Background(0);
        add(Layer.bg,bg);
        mg = new Midground(R.mipmap.mg1);
        add(Layer.mg,mg);

        fg = new Foreground(R.mipmap.fg1);
        add(Layer.fg,fg);
        joystick = new Joystick(cx,cy,outRadius,inRadius);
        add(Layer.controller,joystick);
        add(Layer.controller,new StageMap(mg.num));

        player = new Player(w/2,h-(80)*h/360,joystick);
        add(Layer.player,player);

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
