package kr.ac.kpu.game.s2017182016.jumpman.game;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Random;

import kr.ac.kpu.game.s2017182016.jumpman.framework.object.ImageObject;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.GameView;


public class Platform extends ImageObject {
    private static final String TAG = Platform.class.getSimpleName();
    //public static int UNIT_SIZE = 70;
//    public static int SPEED = 150;



//    enum Type{
//        T_10x2,T_2x2,T_3x1,COUNT,RANDOM;
//
//        float width(){
//            int w = 1;
//            switch(this){
//                case T_10x2: w = 10; break;
//                case T_2x2: w = 2; break;
//                case T_3x1: w = 3; break;
//            }
////            return w * StageMap.UNIT_SIZE * GameView.MULTIPLIER;
//        }
//        float height(){
//            int h = 1;
//            switch(this){
//                case T_10x2: case T_2x2: h = 2; break;
//                case T_3x1: h = 1; break;
//            }
////            return h* StageMap.UNIT_SIZE*GameView.MULTIPLIER;
//        }
//    }
    public Platform(float x, float y,int width, int height){
//        int resId = type ==??...;

//        super(R.mipmap.cookierun_platform_480x48,x,y);
        init(x,y,width, height);
        float w = width;
        float h = height;
        dstRect.set(x,y,x+w,y+h);
       // Log.d(TAG, "Platform(" + type + ") " + dstRect);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void update() {

    }
}
