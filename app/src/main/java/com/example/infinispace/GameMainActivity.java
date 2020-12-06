package com.example.infinispace;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setContentView(R.layout.activity_game_main);
        Button startPlay=findViewById(R.id.start_play);

        startPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //clicking play
                Intent play_intent = new Intent(GameMainActivity.this,GameActivity.class); //moving to GameActivity
                startActivity(play_intent);

            }
        });
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

    /*---moving from main page to highscore page---*/
    public void highScores(View v){
        Intent intent=new Intent(GameMainActivity.this, HighscoreActivity.class);
        startActivity(intent);
    }

    /*---info label---*/
    public void infoAlert(View view){
        new AlertDialog.Builder(GameMainActivity.this)
                .setTitle(R.string.info_title)
                .setMessage(R.string.infoalert)
                .setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
                        }
                        }