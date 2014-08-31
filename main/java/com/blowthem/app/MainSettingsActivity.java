package com.blowthem.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;

/**
 * Created by walter on 27.06.14.
 */
public class MainSettingsActivity extends ActionBarActivity {

    private Intent clientIntent;
    private Handler clientHandler = new Handler();
    private Runnable clientRunnable = new Runnable() {
        @Override
        public void run() {
            startService(clientIntent);
        }
    };
    private ImageView text_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        clientIntent = new Intent(this, SocketService.class);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getActionBar().hide();
        setContentView(R.layout.activity_main_menu);

        Button gamestart = (Button) findViewById(R.id.game_start);
        Button gamesettings = (Button) findViewById(R.id.game_settings);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        currentDisplay.getSize(size);

        ViewGroup.LayoutParams params = gamestart.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        gamestart.setLayoutParams(params);

        params = gamesettings.getLayoutParams();
        params.height = size.y / 10;
        params.width = size.x / 3;
        gamesettings.setLayoutParams(params);

        this.text_logo = (ImageView)findViewById(R.id.text_logo);

        RelativeLayout.LayoutParams marginParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(size.x / 10, size.y / 10, 0, 0);
        marginParams.height = size.y / 5;
        marginParams.width = size.x / 2;
        text_logo.setLayoutParams(marginParams);

        gamestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientIntent.putExtra("start", "$start$");
                clientHandler.post(clientRunnable);
                bridgeGame();
            }
        });
    }

    private void bridgeGame(){
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
