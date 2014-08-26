package com.blowthem.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_login);

        this.mainActivityIntent = new Intent(this, MainActivity.class);
        this.serviceIntent = new Intent(this, SocketService.class);

        this.nameField = (EditText)findViewById(R.id.nameText);
        this.passwordField = (EditText)findViewById(R.id.passwordText);
        this.loginButton = (Button)findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainActivityIntent);
                clientHandler.removeCallbacks(clientRunnable);
                ArrayList<String> list = new ArrayList<String>();
                list.add("$login$");
                list.add(nameField.getText().toString());
                list.add(passwordField.getText().toString());
                serviceIntent.putStringArrayListExtra("login", list);
                clientHandler.post(clientRunnable);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //finish();
    }
}
