package com.makienkovs.rounds2048;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private boolean start;
    private float xS = 0, yS = 0, xM = 0, yM = 0;
    private Game game;
    private boolean isLose;
    private boolean continueGame;
    private boolean isDialog;
    private static boolean isEnd;
    private static boolean isSound;
    private static boolean isVibration;
    private static boolean isClassic;

    private int best;
    private int score;
    private int movies;
    private int[][] field;
    private long time;
    private Vibration vibration;
    private Sound sound;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ConstraintLayout all = findViewById(R.id.all);
        all.setOnTouchListener(this);
        vibration = new Vibration(this);
        sound = new Sound(this);
        start = false;
        continueGame = false;
        isSound = true;
        isVibration = true;
        isClassic = true;
        isEnd = false;
        isLose = false;
        isDialog = false;
        best = 0;
        score = 0;
        movies = 0;
        time = 0;
        field = new int[4][4];
        readParams();
        game = new Game(this);
        game.setParams(score, movies, field);
        game.start();
        printBest();
        if (time != 0) {
            timer();
        }
        if (isFieldEmpty()) {
            startNewGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateParams();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isEnd = true;
        sound.release();
        game.onStop();
        writeParams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isEnd = false;
        sound = new Sound(this);
        game.onResume();
        readParams();
        timer();
    }

    public static boolean getClassic() {
        return isClassic;
    }

    public static boolean getSound() {
        return isSound;
    }

    public static boolean getVibration() {
        return isVibration;
    }

    public void updateParams() {
        score = game.getScore();
        movies = game.getMovies();
        if (score > best) {
            best = score;
        }
    }

    private void readParams() {
        SharedPreferences settings = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);
        if (settings.contains("APP_PREFERENCES_SOUND")) {
            isSound = settings.getBoolean("APP_PREFERENCES_SOUND", true);
        }
        if (settings.contains("APP_PREFERENCES_VIBRATION")) {
            isVibration = settings.getBoolean("APP_PREFERENCES_VIBRATION", true);
        }
        if (settings.contains("APP_PREFERENCES_CLASSIC")) {
            isClassic = settings.getBoolean("APP_PREFERENCES_CLASSIC", true);
        }
        if (settings.contains("APP_PREFERENCES_LOSE")) {
            isLose = settings.getBoolean("APP_PREFERENCES_LOSE", false);
        }
        if (settings.contains("APP_PREFERENCES_BEST")) {
            best = settings.getInt("APP_PREFERENCES_BEST", 0);
        }
        if (settings.contains("APP_PREFERENCES_SCORE")) {
            score = settings.getInt("APP_PREFERENCES_SCORE", 0);
        }
        if (settings.contains("APP_PREFERENCES_MOVIES")) {
            movies = settings.getInt("APP_PREFERENCES_MOVIES", 0);
        }
        if (settings.contains("APP_PREFERENCES_TIME")) {
            time = settings.getLong("APP_PREFERENCES_TIME", 0);
        }
        if (settings.contains("APP_PREFERENCES_FIELD")) {
            String fieldString = settings.getString("APP_PREFERENCES_FIELD", "0/0/0/0/0/0/0/0/0/0/0/0/0/0/0/0/");
            field = convertField(fieldString);
        }
    }

    private void writeParams() {
        SharedPreferences.Editor editor = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE).edit();
        editor.putBoolean("APP_PREFERENCES_SOUND", isSound);
        editor.putBoolean("APP_PREFERENCES_VIBRATION", isVibration);
        editor.putBoolean("APP_PREFERENCES_CLASSIC", isClassic);
        editor.putBoolean("APP_PREFERENCES_LOSE", isLose);
        editor.putInt("APP_PREFERENCES_BEST", best);
        editor.putInt("APP_PREFERENCES_SCORE", score);
        editor.putInt("APP_PREFERENCES_MOVIES", movies);
        editor.putLong("APP_PREFERENCES_TIME", time);
        editor.putString("APP_PREFERENCES_FIELD", fieldToString());
        editor.apply();
    }

    private String fieldToString() {
        int[][] field = game.getField();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.append(field[i][j]).append("/");
            }
        }
        return result.toString();
    }

    private int[][] convertField(String fieldString) {
        int[][] field = new int[4][4];
        for (int i = 0; i < 4; i++) {
            field[i] = new int[4];
        }
        String[] fieldStringArray = fieldString.split("/");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                field[i][j] = Integer.parseInt(fieldStringArray[i * 4 + j]);
            }
        }
        return field;
    }

    private boolean isFieldEmpty() {
        boolean result = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result &= field[i][j] == 0;
            }
        }
        return result;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final float DISTANCE = 100;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xS = event.getX();
                yS = event.getY();
                xM = xS;
                yM = yS;
                start = true;
                break;
            case MotionEvent.ACTION_MOVE:
                xM = event.getX();
                yM = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                xS = 0;
                yS = 0;
                xM = 0;
                yM = 0;
                start = false;
                break;

        }
        float dX = Math.abs(xS - xM);
        float dY = Math.abs(yS - yM);

        if (dX > dY && dX > DISTANCE && start) {
            start = false;
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(30);
                    if (game.checkWin() && !continueGame) {
                        runOnUiThread(this::winDialog);
                    } else if (game.checkLose()) {
                        runOnUiThread(this::loseDialog);
                    }
                    TimeUnit.MILLISECONDS.sleep(170);
                    updateParams();
                    runOnUiThread(this::printBest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            if (xM < xS) game.left();
            else game.right();
        }
        if (dY > dX && dY > DISTANCE && start) {
            start = false;
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(30);
                    if (game.checkWin() && !continueGame) {
                        runOnUiThread(this::winDialog);
                    } else if (game.checkLose()) {
                        runOnUiThread(this::loseDialog);
                    }
                    TimeUnit.MILLISECONDS.sleep(170);
                    updateParams();
                    runOnUiThread(this::printBest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            if (yM < yS) game.up();
            else game.down();
        }

        return true;
    }

    private void loseDialog() {
        if (isDialog) return;
        isDialog = true;
        isEnd = true;
        isLose = true;
        vibration.vibrate(Vibration.VIBRATION_EXTRA_LONG);
        sound.play(Sound.loseSound);
        new AlertDialog.Builder(this)
                .setTitle(R.string.lose)
                .setPositiveButton(R.string.Ok, (dialog, which) -> startNewGame())
                .setNegativeButton(R.string.cancel, null)
                .setMessage(R.string.loseMessage)
                .setCancelable(false)
                .create()
                .show();
    }

    private void winDialog() {
        if (isDialog) return;
        isDialog = true;
        isEnd = true;
        vibration.vibrate(Vibration.VIBRATION_EXTRA_LONG);
        sound.play(Sound.winSound);
        new AlertDialog.Builder(this)
                .setTitle(R.string.win)
                .setPositiveButton(R.string.newGame, (dialog, which) -> startNewGame())
                .setNegativeButton(R.string.continueString, (dialog, which) -> {
                    continueGame = true;
                    isEnd = false;
                    isDialog = false;
                    timer();
                })
                .setMessage(R.string.winMessage)
                .setCancelable(false)
                .create()
                .show();
    }

    public void newGame(View v) {
        anim(v);
        isEnd = true;
        new AlertDialog.Builder(this)
                .setTitle(R.string.newGame)
                .setPositiveButton(R.string.Ok, (dialog, which) -> startNewGame())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    isEnd = false;
                    timer();
                })
                .setMessage(R.string.newGameMessage)
                .setCancelable(false)
                .create()
                .show();
    }

    private void startNewGame() {
        game.newGame();
        printBest();
        isEnd = false;
        isLose = false;
        isDialog = false;
        continueGame = false;
        time = 0;
        timer();
    }

    @SuppressLint("SetTextI18n")
    private void printBest() {
        TextView bestTextView = findViewById(R.id.best);
        bestTextView.setText(getText(R.string.best) + String.valueOf(best));
    }

    public void settings(View v) {
        anim(v);
        final View settingsLayout = getLayoutInflater().inflate(R.layout.settings, null);
        final Switch sound = settingsLayout.findViewById(R.id.sound);
        final Switch vibration = settingsLayout.findViewById(R.id.vibration);
        final Switch classic = settingsLayout.findViewById(R.id.classic);
        final Switch prog = settingsLayout.findViewById(R.id.prog);

        sound.setChecked(isSound);
        vibration.setChecked(isVibration);
        classic.setChecked(isClassic);
        prog.setChecked(!isClassic);

        sound.setOnCheckedChangeListener((buttonView, isChecked) -> isSound = isChecked);
        vibration.setOnCheckedChangeListener((buttonView, isChecked) -> isVibration = isChecked);
        classic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isClassic = isChecked;
            prog.setChecked(!isChecked);
        });
        prog.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isClassic = !isChecked;
            classic.setChecked(!isChecked);
        });

        Button contact = settingsLayout.findViewById(R.id.contact);
        contact.setOnClickListener(v1 -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
            startActivity(Intent.createChooser(intent, getString(R.string.sending)));
        });
        Button app = settingsLayout.findViewById(R.id.app);
        app.setOnClickListener(v12 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.applink)));
            startActivity(intent);
        });

        isEnd = true;
        new AlertDialog.Builder(this)
                .setTitle(R.string.settings)
                .setPositiveButton(R.string.Ok, (dialog, which) -> {
                    isEnd = false;
                    timer();
                    writeParams();
                })
                .setView(settingsLayout)
                .setIcon(R.drawable.settings)
                .setCancelable(false)
                .create()
                .show();
    }

    private void anim(View v) {
        v.animate()
                .setDuration(400)
                .scaleY(1.2f)
                .scaleX(1.2f)
                .rotation(180f)
                .withEndAction(() -> v.animate().rotation(360f).scaleY(1).scaleX(1).withEndAction(() -> v.animate().setDuration(0).rotation(0)));
    }

    @SuppressLint("DefaultLocale")
    private void timer() {
        long startTime = System.currentTimeMillis() - time;
        new Thread(() -> {
            try {
                do {
                    TimeUnit.MILLISECONDS.sleep(50);
                    time = System.currentTimeMillis() - startTime;
                    runOnUiThread(() -> {
                        TextView timer = findViewById(R.id.timer);
                        timer.setText(String.format("%02d:%02d:%02d", time / 3600 / 1000, time / 1000 / 60 % 60, time / 1000 % 60));
                    });
                } while (!isEnd && !isLose);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}