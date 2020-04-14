package edu.ttp.gengame;

import android.content.Context;
import android.content.Intent;
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        View gameView = initializeForView(new Game(size.x, size.y), config);

        Button infoButton = new Button(this);
        infoButton.setText("Info");
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(AndroidLauncher.this, About.class);
            startActivity(intent);
        });

        Button saveButton = new Button(this);
        saveButton.setText("Save");
        saveButton.setOnClickListener(v -> {
            write(STORAGEFILE, new Gson().toJson(new GameData()));
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put(DefContract.DefEntry.COLUMN_NAME_WHEEL_RADIUSES, "2,3");
//            values.put(DefContract.DefEntry.COLUMN_NAME_WHEEL_DENSITIES, "0.5,0.6");
//            values.put(DefContract.DefEntry.COLUMN_NAME_CHASSIS_DENSITY, 0.5);
//            values.put(DefContract.DefEntry.COLUMN_NAME_VERTICES, "4,5");
//            values.put(DefContract.DefEntry.COLUMN_NAME_WHEEL_VERTICES, "6,7");

//            db.insert(DefContract.DefEntry.TABLE_NAME, null, values);
        });

        Button loadButton = new Button(this);
        loadButton.setText("Load");
        loadButton.setOnClickListener(v -> {
            if (isFilePresent(STORAGEFILE)) {
                Game.paused = true;
                GameData data = new Gson().fromJson(read(STORAGEFILE), GameData.class);
                Game.generationState.generation = data.savedGeneration;
                Game.generationState.counter = data.genCounter;
//                Game.ghost = data.ghost;
//                Game.graphState.topScores = data.topScores;
                Game.WordDef.floorseed = data.floorseed;
                Runner.Companion.updateDefs(Game.generationState.generation);
                Game.Companion.setupCarUI();
                Game.Companion.resetCarUI();
                Game.paused = false;
            }


//            SQLiteDatabase db = dbHelper.getReadableDatabase();
//            String[] projection = {
//                    DefContract.DefEntry.COLUMN_NAME_WHEEL_RADIUSES,
//                    DefContract.DefEntry.COLUMN_NAME_WHEEL_DENSITIES,
//                    DefContract.DefEntry.COLUMN_NAME_CHASSIS_DENSITY,
//                    DefContract.DefEntry.COLUMN_NAME_VERTICES,
//                    DefContract.DefEntry.COLUMN_NAME_WHEEL_VERTICES
//            };
//            Cursor cursor = db.query(
//                    DefContract.DefEntry.TABLE_NAME,
//                    projection,
//                    null, null, null, null, null
//            );
//            while (cursor.moveToNext()) {
//                String radiuses = cursor.getString(cursor.getColumnIndexOrThrow(DefContract.DefEntry.COLUMN_NAME_WHEEL_RADIUSES));
//                String densities = cursor.getString(cursor.getColumnIndexOrThrow(DefContract.DefEntry.COLUMN_NAME_WHEEL_DENSITIES));
//                float chassis_density = cursor.getFloat(cursor.getColumnIndexOrThrow(DefContract.DefEntry.COLUMN_NAME_CHASSIS_DENSITY));
//                String vertices = cursor.getString(cursor.getColumnIndexOrThrow(DefContract.DefEntry.COLUMN_NAME_VERTICES));
//                String wheelVertices = cursor.getString(cursor.getColumnIndexOrThrow(DefContract.DefEntry.COLUMN_NAME_WHEEL_VERTICES));
//            }
//
//            Game.Companion.resetCarUI();
            Game.paused = false;
        });

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams textViewParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        textViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.addView(gameView);
        layout.addView(infoButton, textViewParams);

        RelativeLayout.LayoutParams textViewParams1 = new RelativeLayout.LayoutParams(textViewParams);
        textViewParams1.rightMargin = 150;
        layout.addView(saveButton, textViewParams1);

        RelativeLayout.LayoutParams textViewParams2 = new RelativeLayout.LayoutParams(textViewParams);
        textViewParams2.rightMargin = 300;
        layout.addView(loadButton, textViewParams2);
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
