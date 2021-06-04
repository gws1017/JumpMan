package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;

import java.util.ArrayList;

import kr.ac.kpu.game.s2017182016.jumpman.framework.iface.GameObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainScene;


public class StageMap implements GameObject {
    public static int UNIT_SIZE = 70;
    private static final String TAG = StageMap.class.getSimpleName();
    private final ArrayList<String> lines = new ArrayList<String>();
    private MainGame game = MainGame.get();
    private int viewWidth = GameView.view.getWidth();
    private int viewHeight = GameView.view.getHeight();
    private int imageWidth = 480;
    private int imageHeight = 360;
    private int columns;
    private int rows;
    private int current;
    public StageMap(int num){
        switch (num)
        {
            case 0:
                createObject(0,0,7,370);
                createObject(472,0,10,370);
                createObject(0,184,130,108);
                createObject(0,283,75,90);
                createObject(350,184,130,180);
                createObject(0,325,480,30);
                createObject(185,40,110,50);
                break;
            case 1:
                createObject(0,0,7,370);
                createObject(472,0,10,370);
                createObject(295,295,98,34);
                createObject(407,198,65,32);
                createObject(254,198,76,34);
                createObject(118,102,76,65);
                createObject(7,78,75,89);
                break;
            case 2:
                createObject(0,0,7,370);
                createObject(472,0,10,370);
                createObject(207,304,50,15);
                createObject(320,304,58,17);
                createObject(424,255,58,15);
                createObject(190,222,98,33);
                createObject(288,207,49,20);
                createObject(158,118,60,50);
                createObject(8,96,58,17);
                createObject(135,0,73,17);
                break;
            case 3:
                createObject(0,0,7,370);
                createObject(472,0,10,370);
                createObject(135,319,75,42);
                createObject(8,216,56,16);
                createObject(135,216,75,16);
                createObject(296,160,30,70);
                createObject(328,72,16,88);
                createObject(344,72,57,16);
                createObject(432,127,42,18);
                createObject(135,0,18,168);
                createObject(153,88,25,80);
                createObject(328,0,17,17);
                break;
            case 4:
                createObject(0,0,7,370);
                createObject(472,0,10,370);
                createObject(112,312,41,49);
                createObject(328,312,41,49);
                createObject(328,240,42,15);
                createObject(439,160,33,16);
                createObject(288,88,33,16);
                createObject(224,71,33,16);
                createObject(160,56,33,16);
                createObject(40,88,33,16);
                createObject(152,0,176,15);
                break;
            case 5:
                createObject(0,0,7,370);
                createObject(472,0,10,370);
                createObject(327,152,176,33);
                createObject(127,241,50,15);
                createObject(8,184,48,16);
                createObject(56,73,40,15);
                createObject(80,88,16,40);
                createObject(96,112,97,16);
                createObject(135,16,57,40);
                createObject(289,80,191,175);
                createObject(289,0,40,24);
                createObject(329,0,95,40);
                break;
        }

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
        MainScene scene = MainScene.scene;
        scene.add(MainScene.Layer.platform,new Platform(0,180*viewHeight/imageHeight,130*viewWidth/imageWidth,viewHeight));
        scene.add(MainScene.Layer.platform,new Platform(viewWidth,180*viewHeight/imageHeight,-130*viewWidth/imageWidth,viewHeight));


    }

    private void createObject(float x, float y,int w, int h) {
        MainGame game = (MainGame) MainGame.get();
        MainScene scene = MainScene.scene;

        Platform platform = new Platform(x*viewWidth/imageWidth,y*viewHeight/imageHeight,w*viewWidth/imageWidth,h*viewHeight/imageHeight);
        scene.add(MainScene.Layer.platform,platform);
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
