package com.example.colortiles;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int darkColor;
    int darkCount = 0; //Переменная отвечает за количество красных плиток (цвет dark)

    int brightColor;
    int brightCount = 16; //Переменная отвечает за количество белых плиток (цвет bright)

    View[][] tiles = new View[4][4];

    int clickCount = 0; //Переменная отвечает за количество кликов за игру

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Следующий код устанавливает соотношение 1:1 (форма квадрата) для поля плиток
        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.post(() -> {
            mainLayout.getLayoutParams().height = mainLayout.getWidth();
            mainLayout.requestLayout();
        });

        brightColor = getResources().getColor(R.color.bright);
        darkColor = getResources().getColor(R.color.dark);

        //Заполнение массива
        tiles[0][0] = findViewById(R.id.t00);
        tiles[0][1] = findViewById(R.id.t01);
        tiles[0][2] = findViewById(R.id.t02);
        tiles[0][3] = findViewById(R.id.t03);
        tiles[1][0] = findViewById(R.id.t10);
        tiles[1][1] = findViewById(R.id.t11);
        tiles[1][2] = findViewById(R.id.t12);
        tiles[1][3] = findViewById(R.id.t13);
        tiles[2][0] = findViewById(R.id.t20);
        tiles[2][1] = findViewById(R.id.t21);
        tiles[2][2] = findViewById(R.id.t22);
        tiles[2][3] = findViewById(R.id.t23);
        tiles[3][0] = findViewById(R.id.t30);
        tiles[3][1] = findViewById(R.id.t31);
        tiles[3][2] = findViewById(R.id.t32);
        tiles[3][3] = findViewById(R.id.t33);
        initTiles();
        setRecord();
    }

    //Смена цвета на противоположный
    public void changeColor(View v) {
        ColorDrawable d = (ColorDrawable) v.getBackground();
        if (d.getColor() == brightColor) {
            v.setBackgroundColor(darkColor);
            brightCount--;
            darkCount++;
        } else {
            v.setBackgroundColor(brightColor);
            brightCount++;
            darkCount--;
        }
    }

    public void onClick(View v) {
        clickCount++;
        String[] tag = v.getTag().toString().split("");
        int x = Integer.parseInt(tag[0]);
        int y = Integer.parseInt(tag[1]);
        changeColor(tiles[x][y]);

        for (View[] tile : tiles) {
            changeColor(tile[y]);
        }

        for (View tile : tiles[x]) {
            changeColor(tile);
        }

        checkVictory(v);
    }

    //метод проверяет, выйграл ли игрок по количеству закрашенных плиток
    //Если победа засчитана, идёт сравнение прошлого рекорда игрока и его замена, если новый рекорд лучше
    private void checkVictory(View v) {
        TextView tvClickCount = findViewById(R.id.click_count);
        if (brightCount == 16 || darkCount == 16) {
            Log.d("VICTORY", "YOU WINNER");
            Toast.makeText(v.getContext(), "ВЫ ПОБЕДИЛИ!", Toast.LENGTH_SHORT).show();
            tvClickCount.setText(String.format("Вы победили за %d кликов!", clickCount));

            SharedPreferences sharedPreferences = getSharedPreferences("bestRecord", MODE_PRIVATE);

            int bestRecord = sharedPreferences.getInt("bestCountClicks", Integer.MAX_VALUE);
            if (clickCount < bestRecord) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                TextView tvRecord = findViewById(R.id.record);
                tvRecord.setText(String.format(Locale.getDefault(), "Ваш новый рекорд: %d кликов!",clickCount));
                editor.putInt("bestCountClicks", clickCount);
                editor.apply();
            }
        }
        else {
            tvClickCount.setText(String.format("Количество кликов: %d", clickCount));
        }
    }

    // Случайная раскраска поля
    public void initTiles() {
        Random random = new Random();
        for (View[] sub_tiles : tiles) {
            for (View tile : sub_tiles) {
                if (random.nextBoolean()) {
                    changeColor(tile);
                }
            }
        }
    }

    //Обновление поля
    public void resetGame(View view) {
        TextView tvClickCount = findViewById(R.id.click_count);
        tvClickCount.setText(String.format("Количество ходов: 0", clickCount));
        clickCount = 0;
        initTiles();
    }

    //Установка надписи рекорда
    private void setRecord() {
        SharedPreferences sharedPreferences = getSharedPreferences("bestRecord", MODE_PRIVATE);
        int bestRecord = sharedPreferences.getInt("bestCountClicks", Integer.MAX_VALUE);
        TextView tvRecord = findViewById(R.id.record);
        if (bestRecord == Integer.MAX_VALUE) {
            tvRecord.setText("У вас ещё нет рекорда");
        }
        else {
            tvRecord.setText(String.format(Locale.getDefault(), "Ваш рекорд: %d кликов!",bestRecord));
        }
    }

    int clear_approve = 0; //переменная отвечает за подтверждение очистки рекорда

    // метод очищает информацию о рекорде пользователя (для очистки необходимо нажать дважды)
    public void clearRecord(View view) {
        clear_approve++;
        if (clear_approve == 1) {
            Toast.makeText(view.getContext(), "Нажмите ещё раз для сброса рекорда", Toast.LENGTH_SHORT).show();
        }
        else if (clear_approve == 2) {
            clear_approve = 0;
            SharedPreferences sharedPreferences = getSharedPreferences("bestRecord", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("bestCountClicks", Integer.MAX_VALUE);
            editor.apply();
            setRecord();
        }
        else {
            clear_approve = 0;
        }
    }
}