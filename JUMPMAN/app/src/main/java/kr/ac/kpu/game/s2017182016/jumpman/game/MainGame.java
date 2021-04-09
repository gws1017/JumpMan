package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.framework.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.ui.view.GameView;

public class MainGame {
    public static MainGame instance;
    public static MainGame get(){
        if(instance == null){
            instance = new MainGame();
        }
        return instance;
    }
    Background bg;
    ArrayList<GameObject> objects = new ArrayList<>();
    public static float frameTime;
    private boolean initialized;

    public void initResources(){
        if(initialized){
            return;
        }
        bg = new Background();
        objects.add(bg);

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

    public void add(GameObject gameObject) {objects.add(gameObject);}
    public void remove(GameObject gameObject){
        GameView.view.post(new Runnable() {
            @Override
            public void run() {
                objects.remove(gameObject);
            }
        });

    }
}
