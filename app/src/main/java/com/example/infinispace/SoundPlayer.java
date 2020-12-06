package com.example.infinispace;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;


public class SoundPlayer {

    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 2;

    private static SoundPool soundPool;
    private static int hitSound;
    private static int coinSound;
    private static int pushBtnSound;
    private static int gameOverSound;
    private static int newRecordSound;



    public SoundPlayer(Context context) {

        // SoundPool is deprecated in API level 21. (Lollipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            //SoundPool (int maxStreams, int streamType, int srcQuality)
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }


        hitSound = soundPool.load(context, R.raw.hitsound, 1);
        coinSound = soundPool.load(context, R.raw.coinsound, 1);
        pushBtnSound = soundPool.load(context, R.raw.pushbtn, 1);
        gameOverSound = soundPool.load(context, R.raw.gover, 1);
        newRecordSound = soundPool.load(context, R.raw.newrecord, 1);


    }

    public void playHitSound() {

        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playCoinSound() {

        soundPool.play(coinSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playGoSound() {

        soundPool.play(gameOverSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playNewRecord() {

        soundPool.play(newRecordSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playPushBtn() {

        soundPool.play(pushBtnSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }


}