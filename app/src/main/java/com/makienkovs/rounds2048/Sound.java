package com.makienkovs.rounds2048;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class Sound {
    private Context context;
    private SoundPool sounds;
    public static int winSound;
    public static int loseSound;
    public static int moveSound;
    public static int additionSound;

    Sound(Context context) {
        this.context = context;
        createSoundPool();
    }

    private void createSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(10)
                .build();

        winSound = sounds.load(context, R.raw.win, 1);
        loseSound = sounds.load(context, R.raw.lose, 1);
        moveSound = sounds.load(context, R.raw.move, 1);
        additionSound = sounds.load(context, R.raw.addition, 1);
    }

    public void play(int s) {
        if (s > 0 && MainActivity.getSound()) {
            sounds.play(s, 1, 1, 1, 0, 1);
        }
    }

    public void release() {
        sounds.release();
        sounds = null;
    }
}
