package com.blowthem.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;
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

public class LogoActivity extends ActionBarActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_logo);

        intent = new Intent(this, SwitchLoginRegisterActivity.class);

        /*intent = new Intent(this, MainSettingsActivity.class);
        this.serviceIntent = new Intent(this, SocketService.class);
        clientHandler.removeCallbacks(clientRunnable);
        ArrayList<String> list = new ArrayList<String>();
        list.add("$login$");
        list.add("walter");
        list.add("777");
        serviceIntent.putStringArrayListExtra("login", list);
        clientHandler.post(clientRunnable);*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handler.postDelayed(doNextActivity, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(doNextActivity);
    }
}