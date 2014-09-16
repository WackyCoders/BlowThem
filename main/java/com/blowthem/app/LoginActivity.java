package com.blowthem.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

//import p2p.P2pActivity;

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
    private ImageView loginLabel;
    private boolean mIsBound= false;
    //private UpdateReceiver updateReceiver = new UpdateReceiver();

    private MusicService musicService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            musicService = ((MusicService.ServiceBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    public void doBindService(){
        bindService(new Intent(this, MusicService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void doUnbindService(){
        if(mIsBound){
            unbindService(serviceConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_login);

        this.mainActivityIntent = new Intent(this, MainSettingsActivity.class);
        this.serviceIntent = new Intent(this, SocketService.class);

        this.nameField = (EditText)findViewById(R.id.nameText);
        this.passwordField = (EditText)findViewById(R.id.passwordText);
        this.loginButton = (Button)findViewById(R.id.login_button);
        this.loginLabel = (ImageView) findViewById(R.id.login_label);

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
        params.width = size.x / 3;
        nameField.setLayoutParams(params);

        params = passwordField.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        passwordField.setLayoutParams(params);

        params = loginButton.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        loginButton.setLayoutParams(params);

        RelativeLayout.LayoutParams marginParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(size.x / 10 * 7, size.y / 10, 0, 0);
        marginParams.height = size.y / 5;
        marginParams.width = size.x / 2;
        loginLabel.setLayoutParams(marginParams);

        clientHandler.post(startMainActivity);

        final Button musicButton = (Button) findViewById(R.id.music);
        params = musicButton.getLayoutParams();
        params.width = size.x / 20;
        params.height = size.x / 20;
        musicButton.setLayoutParams(params);

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.music).getConstantState())) {
                    musicButton.setBackgroundResource(R.drawable.none_music);
                    musicService.pauseMusic();
                } else if(musicButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.none_music).getConstantState())){
                    musicButton.setBackgroundResource(R.drawable.music);
                    musicService.resumeMusic();
                }
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(musicService.mediaPlayer.isPlaying()){
                    musicButton.setBackgroundResource(R.drawable.music);
                } else {
                    musicButton.setBackgroundResource(R.drawable.none_music);
                }
            }
        });

        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(1000);
        alpha.setFillAfter(true);
        loginLabel.setAnimation(alpha);
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

            //startActivity(mainActivityIntent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
