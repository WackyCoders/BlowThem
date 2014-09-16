package com.blowthem.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView activitiesMainLabel;
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

        this.activitiesMainLabel = (ImageView) findViewById(R.id.text_logo);

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

        params = mailField.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        mailField.setLayoutParams(params);

        params = registrationButton.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        registrationButton.setLayoutParams(params);

        params = nicknameText.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        nicknameText.setLayoutParams(params);

        params = passwordText.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        passwordText.setLayoutParams(params);

        params = mailText.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        mailText.setLayoutParams(params);

        RelativeLayout.LayoutParams marginParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(size.x / 10, size.y / 10, 0, 0);
        marginParams.height = size.y / 5;
        marginParams.width = size.x / 2;
        activitiesMainLabel.setLayoutParams(marginParams);

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
        activitiesMainLabel.setAnimation(alpha);
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
        doUnbindService();
    }
}
