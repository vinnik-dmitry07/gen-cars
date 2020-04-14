package edu.ttp.gengame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FeedReader.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS defs " +
                    "(" +
                    DefContract.DefEntry._ID + " INTEGER PRIMARY KEY," +
                    DefContract.DefEntry.COLUMN_NAME_WHEEL_RADIUSES + " TEXT," +
                    DefContract.DefEntry.COLUMN_NAME_WHEEL_DENSITIES + " TEXT," +
                    DefContract.DefEntry.COLUMN_NAME_CHASSIS_DENSITY + " REAL," +
                    DefContract.DefEntry.COLUMN_NAME_VERTICES + " TEXT," +
                    DefContract.DefEntry.COLUMN_NAME_WHEEL_VERTICES + " TEXT" +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DefContract.DefEntry.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}