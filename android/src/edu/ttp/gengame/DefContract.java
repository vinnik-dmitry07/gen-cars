package edu.ttp.gengame;

import android.provider.BaseColumns;

public final class DefContract {
    private DefContract() {}

    public static class DefEntry implements BaseColumns {
        public static final String TABLE_NAME = "defs";
        public static final String COLUMN_NAME_WHEEL_RADIUSES = "wheel_radiuses";
        public static final String COLUMN_NAME_WHEEL_DENSITIES = "wheel_densities";
        public static final String COLUMN_NAME_CHASSIS_DENSITY = "chassis_density";
        public static final String COLUMN_NAME_VERTICES = "vertices";
        public static final String COLUMN_NAME_WHEEL_VERTICES = "wheel_vertices";
    }
}