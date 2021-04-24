package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.framework.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.ui.view.GameView;

public class MainGame {

    public static MainGame instance;
    public Background bg;
    private Player player;

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



        bg = new Background();
        objects.add(bg);
        int w = bg.dstRect.right;//GameView.view.getWidth();//배경이 그려지는위치를 가져오기
        int h = bg.dstRect.bottom;

        player = new Player(w/2,h-100);
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
