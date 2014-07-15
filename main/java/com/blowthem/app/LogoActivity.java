package com.blowthem.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;

/**
 * Created by walter on 27.06.14.
 */

public class LogoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_logo);

        final Handler handler = new Handler();
        final Intent intent = new Intent(this, MainSettingsActivity.class);
        final Runnable doNextActivity = new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };

        new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(1500);
                handler.post(doNextActivity);
            }
        }.start();
    }
}
