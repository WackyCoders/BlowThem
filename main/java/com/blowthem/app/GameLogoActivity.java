package com.blowthem.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by walter on 31.08.14.
 */
public class GameLogoActivity extends ActionBarActivity {

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
    private ImageView text_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game_logo);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        currentDisplay.getSize(size);

        this.text_logo = (ImageView)findViewById(R.id.text_logo);

        LinearLayout.LayoutParams marginParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        marginParams.setMargins(size.x / 10, size.y / 10, 0, 0);
        marginParams.height = size.y / 5;
        marginParams.width = size.x / 2;
        text_logo.setLayoutParams(marginParams);

        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(3000);
        alpha.setFillAfter(true);
        text_logo.startAnimation(alpha);

        /*animationHandler.post(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation alpha = new AlphaAnimation(animationFrom, animationTo);
                alpha.setDuration(0);
                alpha.setFillAfter(true);
                text_logo.startAnimation(alpha);
            }
        });*/

        intent = new Intent(this, SwitchLoginRegisterActivity.class);
    }

    private Handler animationHandler = new Handler();


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handler.postDelayed(doNextActivity, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(doNextActivity);
    }
}
