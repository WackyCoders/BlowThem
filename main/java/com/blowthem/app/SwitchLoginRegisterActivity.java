package com.blowthem.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.View; 
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

//import p2p.P2pActivity;

/**
 * Created by walter on 21.08.14.
 */
public class SwitchLoginRegisterActivity extends ActionBarActivity {

    private Intent localIntent, serviceIntent, registrationIntent, loginIntent, p2pIntent;

    private Handler clientHandler = new Handler();
    private Runnable clientRunnable = new Runnable() {
        @Override
        public void run() {
            startService(serviceIntent);
        }
    };

    private Button loginButton, registrationButton, p2pbutton;
    private TextView switchText;
    private ImageView text_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_switch);

        localIntent = new Intent(this, MainSettingsActivity.class);
        loginIntent = new Intent(this, LoginActivity.class);
        registrationIntent = new Intent(this, RegistrationActivity.class);
        //p2pIntent = new Intent(this, P2pActivity.class);

        this.loginButton = (Button)findViewById(R.id.login);
        this.registrationButton = (Button)findViewById(R.id.registration);
        this.p2pbutton = (Button)findViewById(R.id.p2p_button);
        //this.switchText = (TextView) findViewById(R.id.menulabel);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(loginIntent);
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registrationIntent);
            }
        });

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        currentDisplay.getSize(size);

        ViewGroup.LayoutParams params = loginButton.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        loginButton.setLayoutParams(params);

        params = registrationButton.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        registrationButton.setLayoutParams(params);

        params = p2pbutton.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        p2pbutton.setLayoutParams(params);

        this.text_logo = (ImageView)findViewById(R.id.text_logo);

        RelativeLayout.LayoutParams marginParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(size.x / 10, size.y / 10, 0, 0);
        marginParams.height = size.y / 5;
        marginParams.width = size.x / 2;
        text_logo.setLayoutParams(marginParams);

        /*params = switchText.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        switchText.setLayoutParams(params);*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    /*public void startP2p(View view){
        startActivity(p2pIntent);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //finish();
    }
}
