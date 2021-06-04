package kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main;

import kr.ac.kpu.game.s2017182016.jumpman.framework.game.BaseGame;
import kr.ac.kpu.game.s2017182016.jumpman.framework.object.Background;
import kr.ac.kpu.game.s2017182016.jumpman.framework.view.Joystick;
import kr.ac.kpu.game.s2017182016.jumpman.game.Player;

public class MainGame extends BaseGame {

    public static Background bg;
    private Player player;
    private Joystick joystick;


    public static MainGame get(){
        return (MainGame) instance;
    }


    private boolean initialized;

    @Override
    public boolean initResources(){
        if(initialized){
            return false;
        }
        push(new MainScene());
        initialized = true;
        return true;
    }


}
