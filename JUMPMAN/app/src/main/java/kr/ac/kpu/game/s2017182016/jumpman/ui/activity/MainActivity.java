package kr.ac.kpu.game.s2017182016.jumpman.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import kr.ac.kpu.game.s2017182016.jumpman.R;
import kr.ac.kpu.game.s2017182016.jumpman.game.scenes.main.MainGame;

public class MainActivity extends AppCompatActivity {

    private MainGame mainGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainGame = new MainGame();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}