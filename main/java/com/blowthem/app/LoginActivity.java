package com.blowthem.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import p2p.P2pActivity;

/**
 * Created by walter on 21.08.14.
 */
public class LoginActivity extends Activity {

    private EditText nameField, passwordField;
    private Intent mainActivityIntent, serviceIntent;
    private Button loginButton;
    private Handler clientHandler = new Handler();
    private Runnable clientRunnable = new Runnable() {
        @Override
        public void run() {
            startService(serviceIntent);
        }
    };
    private String key;
    private boolean isLogined = true;
    private TextView loginLabel;
    //private UpdateReceiver updateReceiver = new UpdateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_login);

        this.mainActivityIntent = new Intent(this, MainSettingsActivity.class);
        this.serviceIntent = new Intent(this, SocketService.class);

        this.nameField = (EditText)findViewById(R.id.nameText);
        this.passwordField = (EditText)findViewById(R.id.passwordText);
        this.loginButton = (Button)findViewById(R.id.login_button);
        this.loginLabel = (TextView) findViewById(R.id.login_label);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            clientHandler.removeCallbacks(clientRunnable);
            ArrayList<String> list = new ArrayList<String>();
            list.add("$login$");
            list.add(nameField.getText().toString());
            list.add(passwordField.getText().toString());
            serviceIntent.putStringArrayListExtra("login", list);
            clientHandler.post(clientRunnable);
            }
        });

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        currentDisplay.getSize(size);

        ViewGroup.LayoutParams params = nameField.getLayoutParams();
        params.height = size.y / 10;
        nameField.setLayoutParams(params);

        params = passwordField.getLayoutParams();
        params.height = size.y / 10;
        passwordField.setLayoutParams(params);

        params = loginButton.getLayoutParams();
        params.height = size.y / 10;
        loginButton.setLayoutParams(params);

        params = loginLabel.getLayoutParams();
        params.height = size.y / 5;
        loginLabel.setLayoutParams(params);

        clientHandler.post(startMainActivity);
    }

    private Runnable startMainActivity = new Runnable() {
        @Override
        public void run() {
            if (LoginBridge.login != null && LoginBridge.login.equals("$login_success$")) {
                LoginBridge.login = null;
                startActivity(mainActivityIntent);
            } else {
                clientHandler.post(this);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
