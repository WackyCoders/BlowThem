package com.blowthem.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by walter on 24.08.14.
 */
public class RegistrationActivity extends Activity {

    private EditText nameField, passwordField, mailField;
    private TextView nicknameText, passwordText, mailText;
    private TextView activitiesMainLabel;
    private Intent mainActivityIntent, serviceIntent;
    private Button registrationButton;
    private Handler clientHandler = new Handler();
    private Runnable clientRunnable = new Runnable() {
        @Override
        public void run() {
            startService(serviceIntent);
        }
    };
    private String key;
    //private UpdateReceiver updateReceiver = new UpdateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_registration);

        this.mainActivityIntent = new Intent(this, MainSettingsActivity.class);
        this.serviceIntent = new Intent(this, SocketService.class);

        this.nameField = (EditText)findViewById(R.id.nameText);
        this.passwordField = (EditText)findViewById(R.id.passwordText);
        this.mailField = (EditText)findViewById(R.id.mailText);
        this.registrationButton = (Button)findViewById(R.id.registration_button);

        this.nicknameText = (TextView) findViewById(R.id.nickname_label);
        this.passwordText = (TextView) findViewById(R.id.password_label);
        this.mailText = (TextView) findViewById(R.id.mail_label);

        this.activitiesMainLabel = (TextView) findViewById(R.id.main_label);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        currentDisplay.getSize(size);

        ViewGroup.LayoutParams params = nameField.getLayoutParams();
        params.height = size.y / 10;
        nameField.setLayoutParams(params);

        params = passwordField.getLayoutParams();
        params.height = size.y / 10;
        passwordField.setLayoutParams(params);

        params = mailField.getLayoutParams();
        params.height = size.y / 10;
        mailField.setLayoutParams(params);

        params = registrationButton.getLayoutParams();
        params.height = size.y / 10;
        registrationButton.setLayoutParams(params);

        params = nicknameText.getLayoutParams();
        params.height = size.y / 10;
        nicknameText.setLayoutParams(params);

        params = passwordText.getLayoutParams();
        params.height = size.y / 10;
        passwordText.setLayoutParams(params);

        params = mailText.getLayoutParams();
        params.height = size.y / 10;
        mailText.setLayoutParams(params);

        RelativeLayout.LayoutParams marginParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(0, size.y / 4, 0, 0);
        //nicknameText.setLayoutParams(marginParams);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            clientHandler.removeCallbacks(clientRunnable);
            ArrayList<String> list = new ArrayList<String>();
            list.add("$registration$");
            list.add(nameField.getText().toString());
            list.add(passwordField.getText().toString());
            list.add(mailField.getText().toString());
            serviceIntent.putStringArrayListExtra("registration", list);
            clientHandler.post(clientRunnable);
            }
        });

        clientHandler.post(startMainActivity);
    }

    private Runnable startMainActivity = new Runnable() {
        @Override
        public void run() {
            if(LoginBridge.registration != null && LoginBridge.registration.equals("$registration_success$")){
                LoginBridge.registration = null;
                startActivity(mainActivityIntent);
                //startMainActivity.interrupt();
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
