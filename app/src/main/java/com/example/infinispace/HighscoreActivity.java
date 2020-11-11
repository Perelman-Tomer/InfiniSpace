package com.example.infinispace;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.infititest.R;
import com.example.infititest.Scorer;
import com.example.infititest.ScorerAdapter;

import java.util.ArrayList;


public class HighscoreActivity extends AppCompatActivity {

    private int temp,lastScore, best1, best2, best3, best4, best5, best6,best7, best8,best9,best10;
    private ListView listView;
    private ScorerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_highscore);

        SharedPreferences pref=getSharedPreferences("score_details",MODE_PRIVATE); //loading the last score
        lastScore=pref.getInt("LAST_SCORE",0);  //saving the last score to an integer


/*---setting the best 10 scores---*/
        best1=pref.getInt("best1",0);
        best2=pref.getInt("best2",0);
        best3=pref.getInt("best3",0);
        best4=pref.getInt("best4",0);
        best5=pref.getInt("best5",0);
        best6=pref.getInt("best6",0);
        best7=pref.getInt("best7",0);
        best8=pref.getInt("best8",0);
        best9=pref.getInt("best9",0);
        best10=pref.getInt("best10",0);


/*---arranging the best scores---*/
        if(lastScore>best10){
            best10=lastScore;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best10", best10);
            editor.commit();
        }
        if(lastScore>best9){
            temp=best9;
            best9=lastScore;
            best10=temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best9", best9);
            editor.putInt("best10", best10);
            editor.commit();
        }
        if(lastScore>best8){
            temp=best8;
            best8=lastScore;
            best9=temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best9", best9);
            editor.putInt("best8", best8);
            editor.commit();
        }
        if(lastScore>best7){
            temp=best7;
            best7=lastScore;
            best8=temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best7", best7);
            editor.putInt("best8", best8);
            editor.commit();
        }
        if(lastScore>best6){
            temp=best6;
            best6=lastScore;
            best7=temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best7", best7);
            editor.putInt("best6", best6);
            editor.commit();
        }
        if (lastScore > best5) {
            temp=best5;
            best5=lastScore;
            best6 = temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best5", best5);
            editor.putInt("best6", best6);
            editor.commit();
        }
        if (lastScore > best4) {
            temp = best4;
            best4 = lastScore;
            best5 = temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best4", best4);
            editor.putInt("best5", best5);
            editor.commit();
        }
        if (lastScore > best3) {
            temp = best3;
            best3 = lastScore;
            best4 = temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best3", best3);
            editor.putInt("best4", best4);
            editor.commit();
        }
        if (lastScore > best2) {
            temp = best2;
            best2 = lastScore;
            best3 = temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best2", best2);
            editor.putInt("best3", best3);
            editor.commit();
        }
        if (lastScore > best1) {
            temp = best1;
            best1 = lastScore;
            best2 = temp;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("best1", best1);
            editor.putInt("best2", best2);
            editor.commit();
        }


        int i=0;
        /*---setting the scores to the CustomView*/
        listView = findViewById(R.id.highscoreLV);
        ArrayList<Scorer> scorerList = new ArrayList<>();
        scorerList.add(new Scorer(++i,best1+""));
        scorerList.add(new Scorer(++i,best2+""));
        scorerList.add(new Scorer(++i,best3+""));
        scorerList.add(new Scorer(++i,best4+""));
        scorerList.add(new Scorer(++i,best5+""));
        scorerList.add(new Scorer(++i,best6+""));
        scorerList.add(new Scorer(++i,best7+""));
        scorerList.add(new Scorer(++i,best8+""));
        scorerList.add(new Scorer(++i,best9+""));
        scorerList.add(new Scorer(++i,best10+""));

/*---saving the best 10 scores---*/
        SharedPreferences settings= getSharedPreferences("GAME_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("LOW_SCORE",best10);
        editor.commit();

        mAdapter = new ScorerAdapter(this,scorerList);
        listView.setAdapter(mAdapter);
    }

    /*---home button is pressed---*/
    public void backHome(View view){
        Intent intent = new Intent(HighscoreActivity.this,GameMainActivity.class);
        startActivity(intent);
        finish();
    }



}
