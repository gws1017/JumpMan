package kr.ac.kpu.game.s2017182016.jumpman.framework.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.kpu.game.s2017182016.jumpman.BuildConfig;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.BoxCollidable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.Recyclable;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;

public class Scene {
    private static final String TAG = Scene.class.getSimpleName();
    ArrayList<ArrayList<GameObject>> layers;
    public ArrayList<ArrayList<GameObject>> getLayers() { return layers; }


    protected void initLayers(int layerCount) {
        layers = new ArrayList<>();
        for (int i = 0; i < layerCount; i++) {
            layers.add(new ArrayList<>());
        }
    }

    public void add(int layerIndex, GameObject gameObject) {
        GameView.view.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<GameObject> objects = layers.get(layerIndex);
                objects.add(gameObject);
            }
        });
    }
    public void remove(GameObject gameObject) {
        remove(gameObject, true);
    }
    public void remove(GameObject gameObject, boolean delayed) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BaseGame game = BaseGame.get();
                for (ArrayList<GameObject> objects: layers) {
                    boolean removed = objects.remove(gameObject);
                    if (removed) {
                        if (gameObject instanceof Recyclable) {
                            ((Recyclable) gameObject).recycle();
                            game.recycle(gameObject);
                        }
                        //Log.d(TAG, "Removed: " + gameObject);
                        break;
                    }
                }
            }
        };
        if (delayed) {
            GameView.view.post(runnable);
        } else {
            runnable.run();
        }
    }

    public ArrayList<GameObject> objectsAt(int layerIndex) {
        return layers.get(layerIndex);
    }


    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


    public void start() {}
    public void end() {}
    public void pause() {}
    public void resume() {}

}
