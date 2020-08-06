package edu.ttp.gengame

import android.app.Activity
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import edu.ttp.gengame.ResultContract.ResultEntry

class StatsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val lineChart:LineChart = findViewById(R.id.line_chart)
        val data = ArrayList<Entry>()

        val dbHelper = DBHelper(applicationContext)
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val cursor: Cursor = db.query(
                ResultEntry.TABLE_NAME,
                arrayOf(ResultEntry.COLUMN_NAME_SCORE),
                null, null, null, null, null
        )
        var i = 0.0f
        while (cursor.moveToNext()) {
            data.add(Entry(i, cursor.getFloat(cursor.getColumnIndexOrThrow(ResultEntry.COLUMN_NAME_SCORE))))
            i += 1
        }
        cursor.close()

        lineChart.data = LineData(LineDataSet(data, "X results"))
    }
}
