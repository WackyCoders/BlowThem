package com.blowthem.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by walter on 21.08.14.
 */
public class SwitchLoginRegisterActivity extends ActionBarActivity {

    private Intent localIntent, serviceIntent, registrationIntent, loginIntent;
    private SocketService socketService = new SocketService();

    private Handler clientHandler = new Handler();
    private Runnable clientRunnable = new Runnable() {
        @Override
        public void run() {
            startService(serviceIntent);
        }
    };

    private Button loginButton, registrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_switch);

        localIntent = new Intent(this, MainSettingsActivity.class);
        serviceIntent = new Intent(this, socketService.getClass());
        loginIntent = new Intent(this, LoginActivity.class);
        System.out.println("ACTIVITY IS UPPED!!!");

        this.loginButton = (Button)findViewById(R.id.login);
        this.registrationButton = (Button)findViewById(R.id.registration);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginIntent);
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*clientHandler.removeCallbacks(clientRunnable);
                ArrayList<String> list = new ArrayList<String>();
                list.add("$registration$");
                list.add("coon");
                list.add("532");
                serviceIntent.putStringArrayListExtra("login", list);
                clientHandler.post(clientRunnable);*/
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //finish();
    }
}
