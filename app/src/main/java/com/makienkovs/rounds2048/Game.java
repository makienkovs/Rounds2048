package com.makienkovs.rounds2048;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class Game {
    private Cell[][] cells;
    private int[][] field;
    private String TAG = "Game_TAG";
    private MainActivity mainActivity;
    public static final int DELAY = 200;
    private final int DELAY_SHORT = 20;
    private final int DELAY_LONG = 180;
    private int score;
    private int movies;
    private Vibration vibration;
    private Sound sound;

    public Game(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        vibration = new Vibration(mainActivity);
        sound = new Sound(mainActivity);
        score = 0;
        movies = 0;

        cells = new Cell[4][4];
        for (int i = 0; i < 4; i++) {
            cells[i] = new Cell[4];
        }
        field = new int[4][4];
        for (int i = 0; i < 4; i++) {
            field[i] = new int[]{0, 0, 0, 0};
        }
        cells[0][0] = mainActivity.findViewById(R.id.cell00);
        cells[0][1] = mainActivity.findViewById(R.id.cell01);
        cells[0][2] = mainActivity.findViewById(R.id.cell02);
        cells[0][3] = mainActivity.findViewById(R.id.cell03);
        cells[1][0] = mainActivity.findViewById(R.id.cell10);
        cells[1][1] = mainActivity.findViewById(R.id.cell11);
        cells[1][2] = mainActivity.findViewById(R.id.cell12);
        cells[1][3] = mainActivity.findViewById(R.id.cell13);
        cells[2][0] = mainActivity.findViewById(R.id.cell20);
        cells[2][1] = mainActivity.findViewById(R.id.cell21);
        cells[2][2] = mainActivity.findViewById(R.id.cell22);
        cells[2][3] = mainActivity.findViewById(R.id.cell23);
        cells[3][0] = mainActivity.findViewById(R.id.cell30);
        cells[3][1] = mainActivity.findViewById(R.id.cell31);
        cells[3][2] = mainActivity.findViewById(R.id.cell32);
        cells[3][3] = mainActivity.findViewById(R.id.cell33);
    }

    public void onStop() {
        sound.release();
    }

    public void onResume() {
        sound = new Sound(mainActivity);
    }

    public int getScore() {
        return score;
    }

    public int getMovies() {
        return movies;
    }

    public int[][] getField() {
        return field;
    }

    public void setParams(int score, int movies, int[][] field) {
        this.score = score;
        this.movies = movies;
        this.field = field;
    }

    private void reload() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                cells[i][j].invalidate();
            }
        }
    }

    private void setNewVal() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                cells[i][j].setVal(field[i][j]);
            }
        }
    }

    public void newGame() {
        Log.d(TAG, "newGame");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                field[i][j] = 0;
            }
        }
        score = 0;
        movies = 0;
        printScore();
        setNewVal();
        reload();
        addRandom();
        addRandom();
    }


    public void start() {
        printScore();
        setNewVal();
        reload();
    }

    private void addRandom() {
        if (checkFull()) return;
        while (true) {
            int i = (int) Math.floor(Math.random() * 4);
            int j = (int) Math.floor(Math.random() * 4);
            int val = Math.random() >= 0.9 ? 2 : 1;
            if (field[i][j] == 0) {
                field[i][j] = val;
                cells[i][j].setVal(val);
                cells[i][j].invalidate();
                cells[i][j].animAppear();
                break;
            }
        }
    }

    private boolean checkFull() {
        boolean full = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                full &= (field[i][j] != 0);
            }
        }
        return full;
    }

    public boolean checkWin() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (field[i][j] == 2048) return true;
            }
        }
        return false;
    }

    public boolean checkLose() {
        if (!checkFull()) {
            return false;
        } else {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int temp = field[i][j];
                    int up = 0, down = 0, left = 0, right = 0;
                    if (i > 0) {
                        up = field[i - 1][j];
                    }
                    if (i < 3) {
                        down = field[i + 1][j];
                    }
                    if (j > 0) {
                        left = field[i][j - 1];
                    }
                    if (j < 3) {
                        right = field[i][j + 1];
                    }
                    if (temp == up || temp == down || temp == left || temp == right) return false;
                }
            }
            return true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void printScore() {
        TextView textViewScore = mainActivity.findViewById(R.id.score);
        textViewScore.setText(mainActivity.getText(R.string.score) + String.valueOf(score));
        TextView textViewMovies = mainActivity.findViewById(R.id.movies);
        textViewMovies.setText(mainActivity.getText(R.string.movies) + String.valueOf(movies));
    }

    public void up() {
        Log.d(TAG, "up");
        boolean movie = false;
        boolean addition = false;

        //смещение со 2 строки
        for (int j = 0; j < 4; j++) {
            if (field[0][j] != 0 && field[0][j] == field[1][j]) {
                //смещение вверх на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[1][j].animUp(1);
                int temp = field[0][j] * 2;
                score += temp;
                field[1][j] = 0;
                if (MainActivity.getClassic()) field[0][j] = -1;
                else field[0][j] = temp;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[0][finalJ] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[0][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[0][j] == 0 && field[1][j] != 0) {
                //смещение вверх на 1 клетку
                movie = true;
                cells[1][j].animUp(1);
                field[0][j] = field[1][j];
                field[1][j] = 0;
                setNewVal();
            }
        }
        //смещение с 3 строки
        for (int j = 0; j < 4; j++) {
            if (field[0][j] != 0 && field[1][j] == 0 && field[0][j] == field[2][j]) {
                //смещение вверх на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[2][j].animUp(2);
                int temp = field[0][j] * 2;
                score += temp;
                field[2][j] = 0;
                if (MainActivity.getClassic()) field[0][j] = -1;
                else field[0][j] = temp;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[0][finalJ] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[0][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[0][j] == 0 && field[1][j] == 0 && field[2][j] != 0) {
                //смещение вверх на 2 клетки
                movie = true;
                cells[2][j].animUp(2);
                field[0][j] = field[2][j];
                field[2][j] = 0;
                setNewVal();
            } else if (field[0][j] != 0 && field[1][j] != 0 && field[1][j] == field[2][j]) {
                //смещение вверх на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[2][j].animUp(1);
                int temp = field[1][j] * 2;
                score += temp;
                field[2][j] = 0;
                if (MainActivity.getClassic()) field[1][j] = -1;
                else field[1][j] = temp;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[1][finalJ] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[1][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[0][j] != 0 && field[1][j] == 0 && field[2][j] != 0) {
                //смещение вверх на 1 клетку
                movie = true;
                cells[2][j].animUp(1);
                field[1][j] = field[2][j];
                field[2][j] = 0;
                setNewVal();
            }
        }
        //смещение с 4 строки
        for (int j = 0; j < 4; j++) {
            if (field[0][j] != 0 && field[1][j] == 0 && field[2][j] == 0 && field[0][j] == field[3][j]) {
                //смещение вверх на 3 клетки со сложением
                movie = true;
                addition = true;
                cells[3][j].animUp(3);
                field[0][j] = field[0][j] * 2;
                score += field[0][j];
                field[3][j] = 0;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[0][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[0][j] == 0 && field[1][j] == 0 && field[2][j] == 0 && field[3][j] != 0) {
                //смещение вверх на 3 клетки
                movie = true;
                cells[3][j].animUp(3);
                field[0][j] = field[3][j];
                field[3][j] = 0;
                setNewVal();
            } else if (field[0][j] != 0 && field[1][j] != 0 && field[2][j] == 0 && field[1][j] == field[3][j]) {
                //смещение вверх на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[3][j].animUp(2);
                field[1][j] = field[1][j] * 2;
                score += field[1][j];
                field[3][j] = 0;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[1][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[0][j] != 0 && field[1][j] == 0 && field[2][j] == 0 && field[3][j] != 0) {
                //смещение вверх на 2 клетки
                movie = true;
                cells[3][j].animUp(2);
                field[1][j] = field[3][j];
                field[3][j] = 0;
                setNewVal();
            } else if (field[0][j] != 0 && field[1][j] != 0 && field[2][j] != 0 && field[2][j] == field[3][j]) {
                //смещение вверх на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[3][j].animUp(1);
                field[2][j] = field[2][j] * 2;
                score += field[2][j];
                field[3][j] = 0;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[2][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[0][j] != 0 && field[1][j] != 0 && field[2][j] == 0 && field[3][j] != 0) {
                //смещение вверх на 1 клетку
                movie = true;
                cells[3][j].animUp(1);
                field[2][j] = field[3][j];
                field[3][j] = 0;
                setNewVal();
            }
        }

        if (movie) {
            movies++;
            vibration.vibrate(Vibration.VIBRATION_SHORT);
            sound.play(Sound.moveSound);
            boolean finalAddition = addition;
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                    if (MainActivity.getClassic()) setNewVal();
                    TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                    mainActivity.runOnUiThread(() -> {
                        if (finalAddition) {
                            vibration.vibrate(Vibration.VIBRATION_LONG);
                            sound.play(Sound.additionSound);
                        }
                        reload();
                        addRandom();
                        printScore();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void down() {
        Log.d(TAG, "down");
        boolean movie = false;
        boolean addition = false;
        //смещение с 3 строки
        for (int j = 0; j < 4; j++) {
            if (field[3][j] != 0 && field[2][j] == field[3][j]) {
                //смещение вниз на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[2][j].animDown(1);
                int temp = field[3][j] * 2;
                score += temp;
                field[2][j] = 0;
                if (MainActivity.getClassic()) field[3][j] = -1;
                else field[3][j] = temp;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[3][finalJ] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[3][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[3][j] == 0 && field[2][j] != 0) {
                //смещение вниз на 1 клетку
                movie = true;
                cells[2][j].animDown(1);
                field[3][j] = field[2][j];
                field[2][j] = 0;
                setNewVal();
            }
        }

        //смещение со 2 строки
        for (int j = 0; j < 4; j++) {
            if (field[3][j] != 0 && field[2][j] == 0 && field[1][j] == field[3][j]) {
                //смещение вниз на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[1][j].animDown(2);
                int temp = field[3][j] * 2;
                score += temp;
                field[1][j] = 0;
                if (MainActivity.getClassic()) field[3][j] = -1;
                else field[3][j] = temp;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[3][finalJ] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[3][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[3][j] == 0 && field[2][j] == 0 && field[1][j] != 0) {
                //смещение вниз на 2 клетки
                movie = true;
                cells[1][j].animDown(2);
                field[3][j] = field[1][j];
                field[1][j] = 0;
                setNewVal();
            } else if (field[3][j] != 0 && field[2][j] != 0 && field[1][j] == field[2][j]) {
                //смещение вниз на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[1][j].animDown(1);
                int temp = field[2][j] * 2;
                score += temp;
                field[1][j] = 0;
                if (MainActivity.getClassic()) field[2][j] = -1;
                else field[2][j] = temp;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[2][finalJ] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[2][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[3][j] != 0 && field[2][j] == 0 && field[1][j] != 0) {
                //смещение вниз на 1 клетку
                movie = true;
                cells[1][j].animDown(1);
                field[2][j] = field[1][j];
                field[1][j] = 0;
                setNewVal();
            }
        }

        //смещение с 1 строки
        for (int j = 0; j < 4; j++) {
            if (field[3][j] != 0 && field[2][j] == 0 && field[1][j] == 0 && field[0][j] == field[3][j]) {
                //смещение вниз на 3 клетки со сложением
                movie = true;
                addition = true;
                cells[0][j].animDown(3);
                field[3][j] = field[3][j] * 2;
                score += field[3][j];
                field[0][j] = 0;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[3][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[3][j] == 0 && field[2][j] == 0 && field[1][j] == 0 && field[0][j] != 0) {
                //смещение вниз на 3 клетки
                movie = true;
                cells[0][j].animDown(3);
                field[3][j] = field[0][j];
                field[0][j] = 0;
                setNewVal();
            } else if (field[3][j] != 0 && field[2][j] != 0 && field[1][j] == 0 && field[0][j] == field[2][j]) {
                //смещение вниз на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[0][j].animDown(2);
                field[2][j] = field[2][j] * 2;
                score += field[2][j];
                field[0][j] = 0;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[2][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[3][j] != 0 && field[2][j] == 0 && field[1][j] == 0 && field[0][j] != 0) {
                //смещение вниз на 2 клетки
                movie = true;
                cells[0][j].animDown(2);
                field[2][j] = field[0][j];
                field[0][j] = 0;
                setNewVal();
            } else if (field[3][j] != 0 && field[2][j] != 0 && field[1][j] != 0 && field[0][j] == field[1][j]) {
                //смещение вниз на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[0][j].animDown(1);
                field[1][j] = field[1][j] * 2;
                score += field[1][j];
                field[0][j] = 0;
                setNewVal();
                int finalJ = j;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[1][finalJ].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[3][j] != 0 && field[2][j] != 0 && field[1][j] == 0 && field[0][j] != 0) {
                //смещение вниз на 1 клетку
                movie = true;
                cells[0][j].animDown(1);
                field[1][j] = field[0][j];
                field[0][j] = 0;
                setNewVal();
            }
        }

        if (movie) {
            movies++;
            vibration.vibrate(Vibration.VIBRATION_SHORT);
            sound.play(Sound.moveSound);
            boolean finalAddition = addition;
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                    if (MainActivity.getClassic()) setNewVal();
                    TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                    mainActivity.runOnUiThread(() -> {
                        if (finalAddition) {
                            vibration.vibrate(Vibration.VIBRATION_LONG);
                            sound.play(Sound.additionSound);
                        }
                        reload();
                        addRandom();
                        printScore();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void left() {
        Log.d(TAG, "left");
        boolean movie = false;
        boolean addition = false;

        //смещение со 2 столбца
        for (int i = 0; i < 4; i++) {
            if (field[i][0] != 0 && field[i][0] == field[i][1]) {
                //смещение влево на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[i][1].animLeft(1);
                int temp = field[i][0] * 2;
                score += temp;
                field[i][1] = 0;
                if (MainActivity.getClassic()) field[i][0] = -1;
                else field[i][0] = temp;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[finalI][0] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[finalI][0].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][0] == 0 && field[i][1] != 0) {
                //смещение влево на 1 клетку
                movie = true;
                cells[i][1].animLeft(1);
                field[i][0] = field[i][1];
                field[i][1] = 0;
                setNewVal();
            }
        }

        //смещение с 3 столбца
        for (int i = 0; i < 4; i++) {
            if (field[i][0] != 0 && field[i][1] == 0 && field[i][0] == field[i][2]) {
                //смещение влево на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[i][2].animLeft(2);
                int temp = field[i][0] * 2;
                score += temp;
                field[i][2] = 0;
                if (MainActivity.getClassic()) field[i][0] = -1;
                else field[i][0] = temp;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[finalI][0] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[finalI][0].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][0] == 0 && field[i][1] == 0 && field[i][2] != 0) {
                //смещение влево на 2 клетки
                movie = true;
                cells[i][2].animLeft(2);
                field[i][0] = field[i][2];
                field[i][2] = 0;
                setNewVal();
            } else if (field[i][0] != 0 && field[i][1] != 0 && field[i][1] == field[i][2]) {
                //смещение влево на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[i][2].animLeft(1);
                int temp = field[i][1] * 2;
                score += temp;
                field[i][2] = 0;
                if (MainActivity.getClassic()) field[i][1] = -1;
                else field[i][1] = temp;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[finalI][1] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[finalI][1].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][0] != 0 && field[i][1] == 0 && field[i][2] != 0) {
                //смещение влево на 1 клетку
                movie = true;
                cells[i][2].animLeft(1);
                field[i][1] = field[i][2];
                field[i][2] = 0;
                setNewVal();
            }
        }

        //смещение с 4 столбца
        for (int i = 0; i < 4; i++) {
            if (field[i][0] != 0 && field[i][1] == 0 && field[i][2] == 0 && field[i][0] == field[i][3]) {
                //смещение влево на 3 клетки со сложением
                movie = true;
                addition = true;
                cells[i][3].animLeft(3);
                field[i][0] = field[i][0] * 2;
                score += field[i][0];
                field[i][3] = 0;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[finalI][0].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][0] == 0 && field[i][1] == 0 && field[i][2] == 0 && field[i][3] != 0) {
                //смещение влево на 3 клетки
                movie = true;
                cells[i][3].animLeft(3);
                field[i][0] = field[i][3];
                field[i][3] = 0;
                setNewVal();
            } else if (field[i][0] != 0 && field[i][1] != 0 && field[i][2] == 0 && field[i][1] == field[i][3]) {
                //смещение влево на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[i][3].animLeft(2);
                field[i][1] = field[i][1] * 2;
                score += field[i][1];
                field[i][3] = 0;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[finalI][1].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][0] != 0 && field[i][1] == 0 && field[i][2] == 0 && field[i][3] != 0) {
                //смещение влево на 2 клетки
                movie = true;
                cells[i][3].animLeft(2);
                field[i][1] = field[i][3];
                field[i][3] = 0;
                setNewVal();
            } else if (field[i][0] != 0 && field[i][1] != 0 && field[i][2] != 0 && field[i][2] == field[i][3]) {
                //смещение влево на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[i][3].animLeft(1);
                field[i][2] = field[i][2] * 2;
                score += field[i][2];
                field[i][3] = 0;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[finalI][2].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][0] != 0 && field[i][1] != 0 && field[i][2] == 0 && field[i][3] != 0) {
                //смещение влево на 1 клетку
                movie = true;
                cells[i][3].animLeft(1);
                field[i][2] = field[i][3];
                field[i][3] = 0;
                setNewVal();
            }
        }

        if (movie) {
            movies++;
            vibration.vibrate(Vibration.VIBRATION_SHORT);
            sound.play(Sound.moveSound);
            boolean finalAddition = addition;
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                    if (MainActivity.getClassic()) setNewVal();
                    TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                    mainActivity.runOnUiThread(() -> {
                        if (finalAddition) {
                            vibration.vibrate(Vibration.VIBRATION_LONG);
                            sound.play(Sound.additionSound);
                        }
                        reload();
                        addRandom();
                        printScore();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void right() {
        Log.d(TAG, "right");
        boolean movie = false;
        boolean addition = false;

        //смещение с 3 столбца
        for (int i = 0; i < 4; i++) {
            if (field[i][3] != 0 && field[i][2] == field[i][3]) {
                //смещение вправо на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[i][2].animRight(1);
                int temp = field[i][3] * 2;
                score += temp;
                field[i][2] = 0;
                if (MainActivity.getClassic()) field[i][3] = -1;
                else field[i][3] = temp;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[finalI][3] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[finalI][3].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][3] == 0 && field[i][2] != 0) {
                //смещение вправо на 1 клетку
                movie = true;
                cells[i][2].animRight(1);
                field[i][3] = field[i][2];
                field[i][2] = 0;
                setNewVal();
            }
        }

        //смещение со 2 столбца
        for (int i = 0; i < 4; i++) {
            if (field[i][3] != 0 && field[i][2] == 0 && field[i][1] == field[i][3]) {
                //смещение вправо на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[i][1].animRight(2);
                int temp = field[i][3] * 2;
                score += temp;
                field[i][1] = 0;
                if (MainActivity.getClassic()) field[i][3] = -1;
                else field[i][3] = temp;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[finalI][3] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[finalI][3].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][3] == 0 && field[i][2] == 0 && field[i][1] != 0) {
                //смещение вправо на 2 клетки
                movie = true;
                cells[i][1].animRight(2);
                field[i][3] = field[i][1];
                field[i][1] = 0;
                setNewVal();
            } else if (field[i][3] != 0 && field[i][2] != 0 && field[i][1] == field[i][2]) {
                //смещение вправо на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[i][1].animRight(1);
                int temp = field[i][2] * 2;
                score += temp;
                field[i][1] = 0;
                if (MainActivity.getClassic()) field[i][2] = -1;
                else field[i][2] = temp;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                        if (MainActivity.getClassic()) {
                            field[finalI][2] = temp;
                        }
                        TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                        mainActivity.runOnUiThread(() -> cells[finalI][2].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][3] != 0 && field[i][2] == 0 && field[i][1] != 0) {
                //смещение вправо на 1 клетку
                movie = true;
                cells[i][1].animRight(1);
                field[i][2] = field[i][1];
                field[i][1] = 0;
                setNewVal();
            }
        }

        //смещение с 1 столбца
        for (int i = 0; i < 4; i++) {
            if (field[i][3] != 0 && field[i][2] == 0 && field[i][1] == 0 && field[i][0] == field[i][3]) {
                //смещение вправо на 3 клетки со сложением
                movie = true;
                addition = true;
                cells[i][0].animRight(3);
                field[i][3] = field[i][3] * 2;
                score += field[i][3];
                field[i][0] = 0;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[finalI][3].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][3] == 0 && field[i][2] == 0 && field[i][1] == 0 && field[i][0] != 0) {
                //смещение вправо на 3 клетки
                movie = true;
                cells[i][0].animRight(3);
                field[i][3] = field[i][0];
                field[i][0] = 0;
                setNewVal();
            } else if (field[i][3] != 0 && field[i][2] != 0 && field[i][1] == 0 && field[i][0] == field[i][2]) {
                //смещение вправо на 2 клетки со сложением
                movie = true;
                addition = true;
                cells[i][0].animRight(2);
                field[i][2] = field[i][2] * 2;
                score += field[i][2];
                field[i][0] = 0;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[finalI][2].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][3] != 0 && field[i][2] == 0 && field[i][1] == 0 && field[i][0] != 0) {
                //смещение вправо на 2 клетки
                movie = true;
                cells[i][0].animRight(2);
                field[i][2] = field[i][0];
                field[i][0] = 0;
                setNewVal();
            } else if (field[i][3] != 0 && field[i][2] != 0 && field[i][1] != 0 && field[i][0] == field[i][1] && field[i][0] != 0) {
                //смещение вправо на 1 клетку со сложением
                movie = true;
                addition = true;
                cells[i][0].animRight(1);
                field[i][1] = field[i][1] * 2;
                score += field[i][1];
                field[i][0] = 0;
                setNewVal();
                int finalI = i;
                new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DELAY);
                        mainActivity.runOnUiThread(() -> cells[finalI][1].animSet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (field[i][3] != 0 && field[i][2] != 0 && field[i][1] == 0 && field[i][0] != 0) {
                //смещение вправо на 1 клетку
                movie = true;
                cells[i][0].animRight(1);
                field[i][1] = field[i][0];
                field[i][0] = 0;
                setNewVal();
            }
        }

        if (movie) {
            movies++;
            vibration.vibrate(Vibration.VIBRATION_SHORT);
            sound.play(Sound.moveSound);
            boolean finalAddition = addition;
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(DELAY_LONG);
                    if (MainActivity.getClassic()) setNewVal();
                    TimeUnit.MILLISECONDS.sleep(DELAY_SHORT);
                    mainActivity.runOnUiThread(() -> {
                        if (finalAddition) {
                            vibration.vibrate(Vibration.VIBRATION_LONG);
                            sound.play(Sound.additionSound);
                        }
                        reload();
                        addRandom();
                        printScore();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}