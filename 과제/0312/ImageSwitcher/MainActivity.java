package kr.ac.kpu.game.s2017182016.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mainTextView;
    private ImageView mainImageView;
    private ImageButton preButton;
    private ImageButton nextButton;

    private int pageIndex;
    private static int[] resIds = {
            R.mipmap.cat1,
            R.mipmap.cat2,
            R.mipmap.cat3,
            R.mipmap.cat4,
            R.mipmap.cat5,
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageIndex = 0;

        mainTextView = findViewById(R.id.mainTextView);
        mainTextView.setText("Program Started");
        mainImageView = findViewById(R.id.mainImageView);
        preButton = findViewById(R.id.preButton);
        nextButton = findViewById(R.id.nextButton);

        showImage();

//        Button helloButton = findViewById(R.id.helloButton);
//        helloButton.setOnClickListener(this);
//
//        Button worldButton = findViewById(R.id.worldButton);
//        helloButton.setOnClickListener(this);

    }

    public void onBtnPre(View view) {
        if (pageIndex == 0) return;
        pageIndex--;
        showImage();
    }

    public void onBtnNext(View view) {
        if (pageIndex == resIds.length - 1) return;
        pageIndex++;
        showImage();
    }


    public void showImage() {
        mainTextView.setText((pageIndex + 1) + "/" + resIds.length);
        int resId = resIds[pageIndex];
        mainImageView.setImageResource(resId);

        preButton.setEnabled(pageIndex != 0);
        nextButton.setEnabled(pageIndex != resIds.length - 1);
    }
}

