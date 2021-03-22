package kr.ac.kpu.game.s2017182016.pairgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int[] buttonIds = {
            R.id.card_01,R.id.card_02,R.id.card_03,R.id.card_04,
            R.id.card_05,R.id.card_06,R.id.card_07,R.id.card_08,
            R.id.card_09,R.id.card_10,R.id.card_11,R.id.card_12,
            R.id.card_13,R.id.card_14,R.id.card_15,R.id.card_16,
            R.id.card_17,R.id.card_18,R.id.card_19,R.id.card_20
    };

    private int[] cards = {
            R.mipmap.card_2c,R.mipmap.card_2c,R.mipmap.card_3d,R.mipmap.card_3d,
            R.mipmap.card_4h,R.mipmap.card_4h,R.mipmap.card_5s,R.mipmap.card_5s,
            R.mipmap.card_as,R.mipmap.card_as,R.mipmap.card_jc,R.mipmap.card_jc,
            R.mipmap.card_qh,R.mipmap.card_qh,R.mipmap.card_kd,R.mipmap.card_kd,
            R.mipmap.card_hh,R.mipmap.card_hh,R.mipmap.card_yn,R.mipmap.card_yn

    };
    private ImageButton prevButton;
    private int visibleCardCount;
    private TextView scoreTextView;

    public void setScore(int score) {
        this.score = score;
        scoreTextView.setText("Flip :"+score);
    }

    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreTextView = findViewById(R.id.scoreTextView);
        startGame();
    }

    public void onBtnCard(View view) {
        if(view == prevButton){ //Current pressed Button == Previous pressed Button don't anything
            return;
        }
        int prevCard = 0;
        if(prevButton != null) {
            prevButton.setImageResource(R.mipmap.card_blue_back);
            prevCard = (Integer)prevButton.getTag();
        }
        int buttonIndex = getButtononIndex(view.getId());
        Log.d(TAG,"onBtnCard() has been called"+ view.getId()+" buttonIndex ="+buttonIndex);

        int card = cards[buttonIndex];
        ImageButton imageButton = (ImageButton)view; //타입 캐스팅(형변환)
        imageButton.setImageResource(card);

        if(card == prevCard){
            imageButton.setVisibility(View.INVISIBLE);
            prevButton.setVisibility(View.INVISIBLE);
            prevButton = null;
            visibleCardCount -=2;
            if(visibleCardCount == 0) {
                askRestart();
            }
            return;
        }
        if(prevButton != null){
            setScore(score + 1);
        }

        prevButton = imageButton;
    }
    private int getButtononIndex(int resId){
        for(int i = 0; i< buttonIds.length; i++){
            if(buttonIds[i] == resId){
                return i;
            }
        }
        return -1;
    }

    public void onBtnRestart(View view) {
        askRestart();
    }

    private void askRestart() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("한번더?");
        builder.setMessage("게임 다시?");
        builder.setPositiveButton("그렇다", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startGame();
            }
        });
        builder.setNegativeButton("아니오?",null);
        AlertDialog alert =builder.create();
        alert.show();

    }

    private void startGame() {

        Random random = new Random();
        for(int i = 0; i< cards.length; i++){
            int ri = random.nextInt(cards.length);
            int t = cards[i];
            cards[i] = cards[ri];
            cards[ri] = t;
        }

        for(int i = 0; i <buttonIds.length; i++){
            ImageButton b = findViewById(buttonIds[i]);
            b.setTag(cards[i]);
            b.setVisibility(View.VISIBLE);
            b.setImageResource(R.mipmap.card_blue_back);
        }
        prevButton = null;
        visibleCardCount = cards.length;
        setScore(0);
    }
}