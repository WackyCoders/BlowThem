package com.blowthem.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blowthem.app.Dialogs.ExitDialog;

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
    Button musicButton;
    private TextView switchText;
    private ImageView text_logo;
    MediaPlayer clickPlayer;

    private boolean mIsBound = false;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_switch);

        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        startService(music);


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
        loginButton.playSoundEffect(R.raw.button_click);
        loginButton.setSoundEffectsEnabled(false);

        params = registrationButton.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        registrationButton.setLayoutParams(params);
        registrationButton.setSoundEffectsEnabled(false);

        params = p2pbutton.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        p2pbutton.setLayoutParams(params);
        p2pbutton.setSoundEffectsEnabled(false);

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

        musicButton = (Button) findViewById(R.id.music);
        params = musicButton.getLayoutParams();
        params.width = size.x / 20;
        params.height = size.x / 20;
        musicButton.setLayoutParams(params);
        musicButton.setSoundEffectsEnabled(false);

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

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(musicService != null) {
                    if (musicService.mediaPlayer.isPlaying()) {
                        musicButton.setBackgroundResource(R.drawable.music);
                    } else {
                        musicButton.setBackgroundResource(R.drawable.none_music);
                    }
                }
                //handler.removeCallbacks(this);
                handler.post(this);
            }
        });
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
        doUnbindService();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                ExitDialog exitDialog = new ExitDialog(SwitchLoginRegisterActivity.this);
                exitDialog.show();
                musicService.pauseMusic();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
