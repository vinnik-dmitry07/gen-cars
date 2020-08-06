package edu.ttp.gengame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void changeActivity(View view) {
        finish();
    }
}
