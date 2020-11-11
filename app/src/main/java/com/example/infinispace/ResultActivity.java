package com.example.infinispace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.infititest.R;

public class ResultActivity extends AppCompatActivity {
    private int score,lowestScore,lvl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_result);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));

        TextView scorelabel=findViewById(R.id.resultsTV);
        TextView bestlabel=findViewById(R.id.bestresultTV);


        score=getIntent().getIntExtra("SCORE",0);
        scorelabel.setText(score+"");
        lvl=getIntent().getIntExtra("LEVEL",0);

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE",0);
        SharedPreferences pref=getSharedPreferences("score_details",MODE_PRIVATE);
        lowestScore=pref.getInt("best10",0);

        if (score>highScore){
            bestlabel.setText("High Score : "+score);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE",score);
            editor.commit();
        }else {
            bestlabel.setText("High Score : "+highScore);
        }
        if(score>lowestScore){
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("LAST_SCORE",score);
            editor.commit();
        }


    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void retry (View v){
        startActivity(new Intent(getApplicationContext(),GameActivity.class));
    }

    public void backHome(View view){
        Intent intent = new Intent(ResultActivity.this,GameMainActivity.class);
        startActivity(intent);
        finish();
    }

    public void highScores(View v){
        Intent intent=new Intent(ResultActivity.this,HighscoreActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResultActivity.this,GameMainActivity.class);
        startActivity(intent);
        finish();
    }
}

