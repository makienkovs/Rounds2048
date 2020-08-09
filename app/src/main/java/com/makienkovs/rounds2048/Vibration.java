package com.makienkovs.rounds2048;

import android.content.Context;
import android.os.Vibrator;

public class Vibration {

    private Context context;

    final static int VIBRATION_SHORT = 50;
    final static int VIBRATION_LONG = 100;
    final static int VIBRATION_EXTRA_LONG = 1000;

    Vibration(Context context) {
        this.context = context;
    }

    void vibrate(int duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator() || !MainActivity.getVibration()) return;
        vibrator.vibrate(duration);
    }
}
