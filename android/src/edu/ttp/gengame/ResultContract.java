package edu.ttp.gengame;

import android.provider.BaseColumns;

public final class ResultContract {
    private ResultContract() {}

    public static class ResultEntry implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_NAME_SCORE = "score";
    }
}