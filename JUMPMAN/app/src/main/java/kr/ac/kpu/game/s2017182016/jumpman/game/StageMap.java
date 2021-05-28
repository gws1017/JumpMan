package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;


public class StageMap implements GameObject {
    public static int UNIT_SIZE = 70;
    private static final String TAG = StageMap.class.getSimpleName();
    private final ArrayList<String> lines = new ArrayList<String>();
    private MainGame game = MainGame.get();
    private int viewWidth = GameView.view.getWidth();
    private int viewHeight = GameView.view.getHeight();
    private int imageWidth = game.bg.bitmap.getWidth();
    private int imageHeight = game.bg.bitmap.getHeight();
    private int columns;
    private int rows;
    private int current;

    public StageMap(){
        createObject();
        createObject(185,40,110,50);
    }

//    public StageMap(String filename) {
//        AssetManager assets = GameView.view.getContext().getAssets();
//        try {
//            InputStream is = assets.open(filename);
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader reader = new BufferedReader(isr);
//            String header = reader.readLine();
//            String[] comps = header.split(" ");
//            columns = Integer.parseInt(comps[0]);
//            rows = Integer.parseInt(comps[1]);
//            UNIT_SIZE = (int) Math.ceil(GameView.view.getHeight() / rows / GameView.MULTIPLIER);
//            Log.d(TAG, "Col=" + columns + " Row="  + rows + " UnitSize=" + UNIT_SIZE);
//            while (true) {
//                String line = reader.readLine();
//                if (line == null) {
//                    break;
//                }
//                Log.d(TAG,  "-row=" + line);
//                lines.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void update() {

    }

    private float xPos;
    private void createColumn() {
            float y = 0;
            for (int row = 0; row < rows; row++) {
                char ch = getAt(current, row);
                y += UNIT_SIZE * GameView.MULTIPLIER;
            }
        current++;
    }

    private void createObject() {
        MainGame game = (MainGame) MainGame.get();

        game.add(new Platform(0,180*viewHeight/imageHeight,130*viewWidth/imageWidth,viewHeight));
        game.add(new Platform(viewWidth,180*viewHeight/imageHeight,-130*viewWidth/imageWidth,viewHeight));
        game.add(new Platform(130*viewWidth/imageWidth,viewHeight,220*viewWidth/imageWidth,-30*viewHeight/imageHeight));


    }

    private void createObject(float x, float y,int w, int h) {
        MainGame game = (MainGame) MainGame.get();
        game.add(new Platform(x*viewWidth/imageWidth,y*viewHeight/imageHeight,w*viewWidth/imageWidth,h*viewHeight/imageHeight));
    }

    private char getAt(int x, int y) {
        try {
            int lineIndex = x / columns * rows + y;
            String line = lines.get(lineIndex);
            return line.charAt(x % columns);
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public void draw(Canvas canvas) {
    }

    public boolean isDone() {
        int lineIndex = current / columns * rows;
        return lines.size() <= lineIndex;
    }
}
