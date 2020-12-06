package com.example.infinispace;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;


public class PauseActivity extends GameActivity {
    private boolean mute_flg = false;
    private static boolean mute_sfx_flg = false;
    private SoundPlayer sound;
    private Button muteunmutebtn;
    private Button sfxbtn;
    private static boolean mute_touched=false,sfx_touch=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.alert_pause);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sound = new SoundPlayer(this);

        muteunmutebtn = findViewById(R.id.pause_sound_btn);
        sfxbtn = findViewById(R.id.mute_sfx_btn);

        if(mute_touched==false){
            muteunmutebtn.setBackground(getResources().getDrawable(R.drawable.musicon));
        }else{
            muteunmutebtn.setBackground(getResources().getDrawable(R.drawable.musicoff));
        }
        if(sfx_touch==false){
            sfxbtn.setBackground(getResources().getDrawable(R.drawable.sfxbt));
        }else{
            sfxbtn.setBackground(getResources().getDrawable(R.drawable.sfxppbt));
        }

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        sfxbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sfx_touch==false){
                    if(!mute_sfx_flg)
                        sound.playPushBtn();
                    sfx_touch=true;
                    sfxbtn.setBackground(getResources().getDrawable(R.drawable.sfxppbt));
                    mute_sfx_flg = true;
                }else{
                    if(!mute_sfx_flg)
                        sound.playPushBtn();
                    sfx_touch=false;
                    sfxbtn.setBackground(getResources().getDrawable(R.drawable.sfxbt));
                    mute_sfx_flg = false;
                }
            }
        });
        muteunmutebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mute_touched==false){
                    if(!mute_sfx_flg)
                        sound.playPushBtn();
                    mute_touched=true;
                    muteunmutebtn.setBackground(getResources().getDrawable(R.drawable.musicoff));
                    mServ.mPlayer.setVolume(0, 0);
                }else{
                    if(!mute_sfx_flg)
                        sound.playPushBtn();
                    mute_touched=false;
                    muteunmutebtn.setBackground(getResources().getDrawable(R.drawable.musicon));
                    mServ.mPlayer.setVolume(1, 1);

                }

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

    //Music
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);

    }

    public void muteMusic(View view) {
        if(!mute_sfx_flg)
            sound.playPushBtn();
    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }

    }


    public void menuResumeGame(View view) {
        if(!mute_sfx_flg)
            sound.playPushBtn();
        super.onBackPressed();
    }

    public void menuRestartGame(View view) {
        if(!mute_sfx_flg)
            sound.playPushBtn();
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
        finish();
    }

    public static Boolean changeSFX(){
        return mute_sfx_flg;
    }

    public void muteSfxGame (View view){
        if(!mute_sfx_flg)
            sound.playPushBtn();
    }

    public void backHome(View view){
        Intent intent = new Intent(PauseActivity.this,GameMainActivity.class);
        startActivity(intent);
        finish();
    }

}
