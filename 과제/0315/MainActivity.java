package kr.ac.kpu.game.s2017182016.pairgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int[] buttonIds = {
            R.id.card_01,R.id.card_02,R.id.card_03,R.id.card_04,
            R.id.card_05,R.id.card_06,R.id.card_07,R.id.card_08,
            R.id.card_09,R.id.card_10,R.id.card_11,R.id.card_12,
            R.id.card_13,R.id.card_14,R.id.card_15,R.id.card_16
    };

    private int[] cards = {
            R.mipmap.card_2c,R.mipmap.card_2c,R.mipmap.card_3d,R.mipmap.card_3d,
            R.mipmap.card_4h,R.mipmap.card_4h,R.mipmap.card_5s,R.mipmap.card_5s,
            R.mipmap.card_as,R.mipmap.card_as,R.mipmap.card_jc,R.mipmap.card_jc,
            R.mipmap.card_qh,R.mipmap.card_qh,R.mipmap.card_kd,R.mipmap.card_kd,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnCard(View view) {
        int buttonIndex = getButtononIndex(view.getId());
        Log.d(TAG,"onBtnCard() has been called"+ view.getId()+" buttonIndex ="+buttonIndex);

        int card = cards[buttonIndex];
        ImageButton imageButton = (ImageButton)view; //타입 캐스팅(형변환)
        imageButton.setImageResource(card);
    }
    private int getButtononIndex(int resId){
        for(int i = 0; i< buttonIds.length; i++){
            if(buttonIds[i] == resId){
                return i;
            }
        }
        return -1;
    }
}