package com.example.infinispace;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {

    private boolean mute_sfx_flg = false;
    private int shipSpeed = 10;
    private Chronometer chronometer;
    private long AnimationDuration;
    private ImageView bg1, bg2;
    private ImageView c1, c2;
    private ImageView s1, s2, s3, astroid, scoreball, small_astro;
    private ImageButton pause;
    private TextView scorelabel, besttxt, startlabel, actualscore, actualbest;
    private TextView lvlcount;
    private ImageView ship;
    private int shipY, shipX = 50;
    private int frameW;
    private int screenW, screenH;
    private int shipW, shipH;
    private int small_astroX, small_astroY, small_astro_speed = 23;
    private int astroX, astroY;
    private double astroSpeed = 20;
    private int scoreX, scoreY;
    private int scoreSpeed = 15;
    private int score;
    private int best;
    private int lvl;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private Boolean pause_flg = false;
    private FrameLayout frameLayout;
    private SoundPlayer sound;
    private MusicService mServ;
    private Boolean mute_sfx = false;
    private boolean mIsBound = false;
    private long timeWhenStopped;
    private Boolean newRecordSound = false;

    private boolean action_flg = false;
    private boolean start_flg = false;
    private int shield_count = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        AnimationDuration = 10000L; //BG speed
        AnimateBG(); //animating the BG
        AnimateClouds(); //animating the clouds
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        /*-----getting references to objects*/
        ship = findViewById(R.id.spaceShip);
        s1 = findViewById(R.id.shield1);
        s2 = findViewById(R.id.shield2);
        s3 = findViewById(R.id.shield3);
        small_astro = findViewById(R.id.smallastro);
        astroid = findViewById(R.id.astro);
        scoreball = findViewById(R.id.scoreball);
        pause = findViewById(R.id.pauseIB);
        startlabel = findViewById(R.id.taptostartTV);
        scorelabel = findViewById(R.id.scoreTV);
        besttxt = findViewById(R.id.bestTV);
        actualscore = findViewById(R.id.realscore);
        actualbest = findViewById(R.id.realbest);
        frameLayout = findViewById(R.id.frame);
        chronometer = findViewById(R.id.timer);
        lvlcount = findViewById(R.id.lvlTV);
        sound = new SoundPlayer(this);
        /*-----getting references to objects*/

        setAnim(); //animating the text view "Tap to start"


        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);//getting the max score from local device
        best = settings.getInt("HIGH_SCORE", 0);
        actualbest.setText("" + best); //setting the textView with the high score


        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        pause.setVisibility(View.GONE); //making sure pause isn't clickable before starting

        //Music Services
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);


        HomeWatcher mHomeWatcher; //to control actions during home press

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                    pauseGame(null);
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                    pauseGame(null);
                }
            }
        });
        mHomeWatcher.startWatch();

        timeWhenStopped = 0;

        pause.setOnClickListener(new View.OnClickListener() {  //pause is clicked
            @Override
            public void onClick(View v) {
                pause.setVisibility(View.GONE);
                pauseGame(null);
            }
        });
        ship.setX(shipX);
        shipW = ship.getWidth();
        shipH = ship.getHeight();

        /*---setting the lvl at start---*/
        lvl = 1;
        lvlcount.setText(lvl + "");

        /*---getting device's screen size---*/
        screenW = size.x;
        screenH = size.y;

        /*---setting the first locations of game objects---*/
        small_astro.setX(screenW + 200);
        small_astro.setY(-280);
        astroid.setX(screenW + 500);
        astroid.setY(-280);
        scoreball.setX(screenW + 800);
        scoreball.setY(-280);


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

    /*---override the back button*/
    @Override
    public void onBackPressed() {
        if (start_flg == true) {
            pause.setVisibility(View.VISIBLE);
            pauseGame(null);
        } else
            onBackPressed();
    }

    //Music service
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

    /*---override the default onPause---*/
    @Override
    protected void onPause() {
        super.onPause();
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE); //using PowerManager to make sure music only play within game
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }
        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
                pauseGame(null);
            }
        }
    }

    public void gamePlay() {
        /*---defining the mid part of the objects for hits*/
        int smallastroxCeter = small_astroX + small_astro.getWidth() / 2;
        int smallastroycenter = small_astroY + small_astro.getHeight() / 2;
        int astroxCeter = astroX + astroid.getWidth() / 2;
        int astroycenter = astroY + astroid.getHeight() / 2;

        /*---setting actions relating to the small asteroid---*/
        small_astroX -= small_astro_speed; //speed
        if (hitCheck(smallastroxCeter, smallastroycenter)) { //making sure the ship has hit the astroid
            small_astroX = -400; //sending the astroid away from screen
            /*---checking which shield to remove---*/
            if (shield_count == 3) {
                s1.setBackground(null);
            }
            if (shield_count == 2) {
                s2.setBackground(null);
            }
            if (shield_count == 1) {
                s3.setBackground(null);
            }
            shield_count--; //lowering shield amount
            if (!mute_sfx_flg)
                sound.playHitSound(); //playing the hit sound

            if (shield_count == -1) { //if 0 shields left and ship got hit
                mServ.pauseMusic();
                if (!mute_sfx_flg)
                    sound.playGoSound();

                chronometer.stop(); //stopping the running timer
                timer.cancel(); //stopping the game
                timer = null;
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class); //move to result screen
                intent.putExtra("SCORE", score); //saving the current score
                startActivity(intent);
            }
        }
        if (small_astroX < -150) { //if the small asteroid has move past the screen
            small_astroX = screenW + 150;
            small_astroY = (int) Math.floor(Math.random() * (1080 - small_astro.getHeight()));
        }
        small_astro.setX(small_astroX); //setting the new X for the small asteroid
        small_astro.setY(small_astroY); //setting the new Y for the small asteroid

        /*---setting actions relating to the asteroid---*/
        astroX -= astroSpeed; //speed
        if (hitCheck(astroxCeter, astroycenter)) {//making sure the ship has hit the astroid
            astroX = -800;//sending the astroid away from screen
            /*---checking which shield to remove---*/
            if (shield_count == 3) {
                s1.setBackground(null);
            }
            if (shield_count == 2) {
                s2.setBackground(null);
            }
            if (shield_count == 1) {
                s3.setBackground(null);
            }
            shield_count--;//lowering shield amount
            if (!mute_sfx_flg)
                sound.playHitSound();

            if (shield_count == -1) { //if 0 shields left and ship got hit
                mServ.pauseMusic();
                if (!mute_sfx_flg)
                    sound.playGoSound();

                chronometer.stop();  //stopping the running timer
                timer.cancel(); //stopping the game
                timer = null;
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class); //move to result screen
                intent.putExtra("SCORE", score); //saving the current score
                startActivity(intent);
            }
        }
        if (astroX < -300) { //if the asteroid has move past the screen
            astroX = screenW + 500;
            astroY = (int) Math.floor(Math.random() * (1080 - astroid.getHeight()));
        }
        astroid.setX(astroX); //setting the new X for the asteroid
        astroid.setY(astroY); //setting the new Y for the asteroid

        /*---setting actions relating to the score ball---*/
        scoreX -= scoreSpeed;//speed
        if (hitCheck(scoreX, scoreY)) {//making sure the ship has catched the scoreball
            if (!mute_sfx_flg)
                sound.playCoinSound(); //sound
            scoreX = -900; //removing scoreball from screen
            score += 100; //adding score
            if (score % 1000 == 0) {  //level increase
                small_astro_speed += 5;
                astroSpeed += 5;
                scoreSpeed += 5;
                shipSpeed += 2;
                lvl++;
                lvlcount.setText(lvl + "");
            }
            actualscore.setText(Integer.toString(score)); //setting the score at the TextView
            if (score > best) { //the score is higher than the best
                if (!mute_sfx_flg)
                    if (!newRecordSound) {
                        sound.playNewRecord(); //sound for passing the high score
                        newRecordSound = true; //making sure the sound will be one time only
                    }
                best = score; //setting the best TextView to be the same as the score TextView
                actualbest.setText(Integer.toString(best));
            }
        }
        if (scoreX < -50) { //scoresball has passed the screen
            scoreX = screenW + 100;
            scoreY = (int) Math.floor(Math.random() * (1080 - astroid.getHeight()));
        }
        scoreball.setX(scoreX); //setting new X for scoreball
        scoreball.setY(scoreY); //setting new Y for scoreball


        if (action_flg) { //screen is touched
            shipY -= shipSpeed;
        } else {          //screen is not touched
            shipY += shipSpeed;
        }
        if (shipY < 0) shipY = 4;
        if (shipY > frameW - shipY) shipY = frameW - shipY - 1;
        ship.setY(shipY);
    }

    /*---checking for hits algorithm---*/
    public boolean hitCheck(int x, int y) {
        if ((shipX <= x) && (x <= (shipX + ship.getWidth()) &&
                (shipY <= y) && (y <= (shipY + ship.getHeight())))) {
            return true;
        }
        return false;
    }

    /*---pausing the game---*/
    public void pauseGame(View view) { //game is paused
        if (pause_flg == false) {
            pause_flg = true;
            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime(); //making sure the running timer will not continue
            chronometer.stop();
            timer.cancel(); //stopping the game
            timer = null;

            Intent intent = new Intent(getApplicationContext(), PauseActivity.class); //opening the pauseactivity
            startActivity(intent);
        } else {
            onResume(); //return to game
        }
    }

    /*---overriding the default onResume---*/
    @Override
    protected void onResume() {
        super.onResume();

        mute_sfx_flg = PauseActivity.changeSFX();
        if (mServ != null) {
            mServ.resumeMusic();
        }
        if (pause_flg == true) {
            pause_flg = false;
            pause.setVisibility(View.VISIBLE); //allowing the game to be paused
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped); //returing to the last running time
            chronometer.start(); //resuming the running timer
            timer = new Timer(); //start moving the game again
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            gamePlay();
                        }
                    });
                }
            }, 0, 20);
        }
    }

    /*---overriding the default onDestroy---*/
    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);

    }

    /*---handling all the touch events---*/
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg == false) { //game has not been started

            start_flg = true;
            frameW = frameLayout.getWidth();
            chronometer.start(); //initialize the running timer

            shipY = (int) ship.getY();
            startlabel.clearAnimation(); //removing the label "Tap to start"
            startlabel.setVisibility(View.GONE);

            pause.setVisibility(View.VISIBLE); //showing the pause button

            timer.schedule(new TimerTask() { //starting the game
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            gamePlay();
                        }
                    });

                }
            }, 0, 20);
            /*---setting location for all moving objects---*/
            small_astroX = -150;
            small_astro.setX(small_astroX);
            small_astro.setVisibility(View.VISIBLE);
            astroX = -400;
            astroid.setX(astroX);
            astroid.setVisibility(View.VISIBLE);
            scoreX = -100;
            scoreball.setX(scoreX);
            scoreball.setVisibility(View.VISIBLE);
        } else { //controlling the ship
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;
    }

    /*---cloud animation---*/
    public void AnimateClouds() {
        c1 = findViewById(R.id.clouds1);
        c2 = findViewById(R.id.clouds2);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(2500L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float prog = (float) animation.getAnimatedValue();
                final float wid = c1.getWidth();
                final float translationX = wid * prog;
                c1.setTranslationX(translationX);
                c2.setTranslationX(translationX - wid);
            }
        });
        animator.start();
    }

    /*---BG animation---*/
    public void AnimateBG() {
        bg1 = findViewById(R.id.bg1);
        bg2 = findViewById(R.id.bg2);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(AnimationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float prog = (float) animation.getAnimatedValue();
                final float wid = bg1.getWidth();
                final float translationX = wid * prog;
                bg1.setTranslationX(translationX);
                bg2.setTranslationX(translationX - wid);
            }
        });
        animator.start();
    }

    /*---text animation---*/
    public void setAnim() {
        Animation fadeIn;
        startlabel = findViewById(R.id.taptostartTV);

        fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1200);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setRepeatCount(Animation.INFINITE);
        fadeIn.setRepeatMode(Animation.REVERSE);
        startlabel.startAnimation(fadeIn);
    }
}
