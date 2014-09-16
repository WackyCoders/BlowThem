package com.blowthem.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by walter on 27.06.14.
 */

public class LogoActivity extends Activity {

    private Handler handler = new Handler();
    private Intent intent;
    private Runnable doNextActivity = new Runnable() {
        @Override
        public void run() {
            startActivity(intent);
            finish();
        }
    };

    private Intent mainActivityIntent, serviceIntent;
    private Handler clientHandler = new Handler();
    private Runnable clientRunnable = new Runnable() {
        @Override
        public void run() {
            startService(serviceIntent);
        }
    };
    private LinearLayout main_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_logo);

        main_frame = (LinearLayout)findViewById(R.id.main_frame);

        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(1000);
        alpha.setFillAfter(true);
        main_frame.startAnimation(alpha);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation alpha1 = new AlphaAnimation(1.0f, 0.0f);
                //alpha.setBackgroundColor(Color.BLACK);
                //main_frame.setBackgroundColor(Color.BLACK);
                alpha1.setDuration(1000);
                alpha1.setFillAfter(true);
                main_frame.startAnimation(alpha1);
            }
        }, 1000);

        intent = new Intent(this, GameLogoActivity.class);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handler.postDelayed(doNextActivity, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(doNextActivity);
    }
}