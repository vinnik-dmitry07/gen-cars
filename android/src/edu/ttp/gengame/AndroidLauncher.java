package edu.ttp.gengame;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AndroidLauncher extends AndroidApplication {
    String STORAGEFILE = "storage.json";
    DBHelper dbHelper = new DBHelper(getContext());

    class ResultCallback implements Game.ICallback {
        @Override
        public void call(double result){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ResultContract.ResultEntry.COLUMN_NAME_SCORE, result);
            db.insert(ResultContract.ResultEntry.TABLE_NAME, null, values);
        }
    }
    Game.ICallback callback = new ResultCallback();

    private static class GameData {
        Def[] savedGeneration = Game.generationState.generation;
        int genCounter = Game.generationState.counter;
//        ghost = Game.ghost;
//        topScores = Game.graphState.topScores;
        long floorseed = Game.WordDef.floorseed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        getContext().deleteDatabase(DBHelper.DATABASE_NAME);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        View gameView = initializeForView(new Game(callback, size.x, size.y), config);

        Button infoButton = new Button(this);
        infoButton.setText("Info");
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(AndroidLauncher.this, About.class);
            startActivity(intent);
        });

        Button saveButton = new Button(this);
        saveButton.setText("Save");
        saveButton.setOnClickListener(v -> {
            Game.paused = true;
            write(STORAGEFILE, new Gson().toJson(new GameData()));
            Game.paused = false;
        });

        Button loadButton = new Button(this);
        loadButton.setText("Load");
        loadButton.setOnClickListener(v -> {
            if (isFilePresent(STORAGEFILE)) {
                Game.paused = true;
                GameData data = new Gson().fromJson(read(STORAGEFILE), GameData.class);
                Game.clearPopulationWorld();
                Game.generationState.generation = data.savedGeneration;
                Game.generationState.counter = data.genCounter;
//                Game.ghost = data.ghost;
//                Game.graphState.topScores = data.topScores;
                Game.WordDef.floorseed = data.floorseed;
                Runner.Companion.updateDefs(Game.generationState.generation);
                Game.setupCarUI();
                Game.resetCarUI();
                Game.paused = false;
            }
        });

        Button restartButton = new Button(this);
        restartButton.setText("Restart");
        restartButton.setOnClickListener(v -> {
            Game.paused = true;
            Game.clearPopulationWorld();
            Game.generationZero();
            // TODO: 2/15/2020
            //  ghost = ghost_create_ghost();
            Game.resetCarUI();
            Runner.updateDefs(Game.generationState.generation);
            Game.alivecars = Runner.cars;
            Game.setupCarUI();
            Game.resetCarUI();
            Game.paused = false;
        });

        Button statsButton = new Button(this);
        statsButton.setText("Stats");
        statsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AndroidLauncher.this, StatsActivity.class);
            startActivity(intent);
        });

        Button speedupButton = new Button(this);
        speedupButton.setText("Speedup");
        speedupButton.setOnClickListener(v -> {
            Game.doDraw = !Game.doDraw;
            Game.speed = Game.speed == 1 ? 100 : 1;
        });

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.addView(gameView);
        layout.addView(infoButton, params);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(params);
        params1.rightMargin = 130;
        layout.addView(saveButton, params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(params);
        params2.rightMargin = 280;
        layout.addView(loadButton, params2);
        setContentView(layout);

        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(params);
        params3.rightMargin = 430;
        layout.addView(restartButton, params3);
        setContentView(layout);

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(params);
        params4.rightMargin = 620;
        layout.addView(statsButton, params4);
        setContentView(layout);

        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(params);
        params5.rightMargin = 770;
        layout.addView(speedupButton, params5);
        setContentView(layout);
    }

    public boolean isFilePresent(String fileName) {
        String path = this.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    private String read(String fileName) {
        try {
            FileInputStream fis = this.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException fileNotFound) {
            return null;
        }
    }

    private boolean write(String fileName, String jsonString) {
        try {
            FileOutputStream fos = this.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (IOException fileNotFound) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
