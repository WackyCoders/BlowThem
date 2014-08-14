package com.blowthem.app;

import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.animation.*; 
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import bt.BTServer;
import poor2D.Vector;
 

public class MainActivity extends ActionBarActivity {

    private SocketService clientService = new SocketService();
    private Intent clientIntent;
    //private JoyStickClass js;
    private JoyStick js;
    private ProthoTank tank;
    private ProthoTank enemy;
    private FireIndicator circleProgress;
    private Point size;
    private ImageView imageView;
    private static int[] animation_array = {R.drawable.fire_1, R.drawable.fire_2, R.drawable.fire_3, R.drawable.fire_4, R.drawable.fire_5,
            R.drawable.fire_6, R.drawable.fire_7, R.drawable.fire_8, R.drawable.fire_9, R.drawable.fire_10, R.drawable.fire_11, R.drawable.fire_12};

    private MotionEvent fireEvent;
    private MotionEvent event1;

    private MediaPlayer sound;
    private int bullet_stroke;
    private RelativeLayout fire_indificator, main_frame;

    private FireButton fire_button;
    private int percent = 0;

    private boolean flagEnablesToFire = true;

    private Handler mFire = new Handler();
    private Runnable mFireTask = new Runnable(){
        public void run(){
            //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            synchronized (tank) {
                tank.drawFire(fireEvent);
                //new BulletClientHandler(MainActivity.this).execute(String.valueOf(3) + "\n", messageToSend,
                //        String.valueOf(tank.bullet.core.getX()) + "\n", String.valueOf(tank.bullet.core.getY()) + "\n");
            }
        }
    };

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            //final float X = event1.getX();
            //final float Y = event1.getY();
            mHandler.removeCallbacks(mUpdateTask);
            mHandler.post(mUpdateTask);
            timerHandler.postDelayed(this, 0);
        }
    };

    private Handler indicatorHandler = new Handler();
    private Runnable indicatorTask = new Runnable(){
        @Override
        public void run() {
            percent += 1;
            fillIndicator.removeCallbacks(fillIndicatorTask);
            fillIndicator.post(fillIndicatorTask);
            if(percent != 100){
                indicatorHandler.postDelayed(this, 30);
            } else {
                flagEnablesToFire = true;
                fillIndicator.removeCallbacks(fillIndicatorTask);
                fillIndicator.post(fillIndicatorTask);
                percent = 0;
            }
        }
    };

    private Handler fillIndicator = new Handler();
    private Runnable fillIndicatorTask = new Runnable() {
        @Override
        public void run() {
            circleProgress.setPercent(percent);
            circleProgress.invalidate();
        }
    };

    private Handler animateExplode = new Handler();
    private Runnable animateExplodeTask = new Runnable() {
        @Override
        public void run() {
            animate(imageView, animation_array, 0, true);
        }
    };


    private Handler clientHandler = new Handler();
    private Runnable clientRunnable = new Runnable() {
        @Override
        public void run() {
            startService(clientIntent);
        }
    };
    private Handler receiveDataHandler = new Handler();
    private Runnable receiveDataRunnable = new Runnable() {
        @Override
        public void run() {
            if(clientService.getX() != null && clientService.getY() != null) {
                enemy.drawTank(clientService.getX(), clientService.getY());
            }
            receiveDataHandler.post(this);
        }
    };

    private Handler bulletThreadHandler = new Handler();
    private Runnable bulletThreadTask = new Runnable() {
        @Override
        public void run() {
            mFire.removeCallbacks(mFireTask);
            mFire.post(mFireTask);
            if(tank.bullet.isAlive()) {

                ///////////////////////
                ////////////////////// The right side are the coords of enemy vector (core)
                tank.bullet.enemyPosition = (Vector) enemy.core.getPosition().clone();
                ///////////////////////

                bulletThreadHandler.postDelayed(this, 0);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if(tank.bullet.height >= 0 && tank.bullet.width >= 0) {
                    params.setMargins((int) tank.bullet.width - tank.getTankWidth(), (int) tank.bullet.height - tank.getTankHeight(), 0, 0);
                } else if(tank.bullet.height < 0 && tank.bullet.width > 0){
                    params.setMargins((int) tank.bullet.width - (int)0.5 * tank.getTankWidth(), (int) tank.bullet.height + (int)0.5 *tank.getTankHeight(), 0, 0);
                } else if(tank.bullet.height > 0 && tank.bullet.width < 0){
                    params.setMargins((int) tank.bullet.width + (int)0.5 * tank.getTankWidth(), (int) tank.bullet.height - (int)0.5 * tank.getTankHeight(), 0, 0);
                } else if(tank.bullet.height < 0 && tank.bullet.width < 0){
                    params.setMargins((int) tank.bullet.width + tank.getTankWidth(), (int) tank.bullet.height + tank.getTankHeight(), 0, 0);
                }
                //System.out.println("explode X : " + (int)tank.bullet.width + " explode Y : " + (int)tank.bullet.height);
                imageView.setLayoutParams(params);
                //animate(imageView, animation_array, 0, true);
                animateExplode.removeCallbacks(animateExplodeTask);
                animateExplode.postDelayed(animateExplodeTask, 0);

                if (sound != null) {
                    sound.release();
                    sound = null;
                }
                sound = MediaPlayer.create(getApplicationContext(), R.raw.bomb_exploding);
                sound.start();
                isClicked = true;
            }
        }
    };

    private Handler mHandler = new Handler();
    private Runnable mUpdateTask = new Runnable(){
        public void run(){
            synchronized (tank) {
                tank.drawTank(js.getNormalX(), js.getNormalY());

                enemy.drawTank(new Vector(0.5f, 0.5f), new Vector(-0.1f, 0.1f));

                //new TankClientHandler(MainActivity.this).execute("$motion$",
                //        String.valueOf(tank.core.getX()), String.valueOf(tank.core.getY()));
            }
        }
    };

    private boolean isClicked = true;

    private View.OnTouchListener layout_stickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            event1 = event;
            js.drawStick(event1);

            ArrayList<String> list = new ArrayList<String>();
            list.add("$motion$");
            list.add(String.valueOf(tank.core.getX()));
            list.add(String.valueOf(tank.core.getY()));
            clientIntent.putStringArrayListExtra("motion", list);
            clientHandler.post(clientRunnable);

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mHandler.removeCallbacks(mUpdateTask);
                    mHandler.post(mUpdateTask);
                    break;

                case MotionEvent.ACTION_MOVE:
                    timerHandler.removeCallbacks(timerRunnable);
                    timerHandler.postDelayed(timerRunnable, 0);
                    break;

                case MotionEvent.ACTION_UP:
                    timerHandler.removeCallbacks(timerRunnable);
                    mHandler.removeCallbacks(mUpdateTask);
                    break;
            }
            return true;
        }
    };

    private View.OnClickListener fireButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //startService(new Intent(MainActivity.this, mBoundService.getClass()));
            if(isClicked && flagEnablesToFire) {
                flagEnablesToFire = false;
                indicatorHandler.removeCallbacks(indicatorTask);
                indicatorHandler.postDelayed(indicatorTask, 30);

                if(sound != null){
                    sound.release();
                    sound= null;
                }
                sound = MediaPlayer.create(getApplicationContext(), R.raw.laser_blaster);
                sound.start();

                isClicked = false;
                tank.bullet = new FireBullet(getApplicationContext(), tank);
                tank.bullet.setSTROKE(bullet_stroke);
                mFire.removeCallbacks(mFireTask);
                mFire.post(mFireTask);
                bulletThreadHandler.removeCallbacks(bulletThreadTask);
                bulletThreadHandler.postDelayed(bulletThreadTask, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientIntent = new Intent(this, clientService.getClass());

        getActionBar().hide();
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Display currentDisplay  = getWindowManager().getDefaultDisplay();
        size = new Point();
        currentDisplay.getSize(size);

        main_frame = (RelativeLayout) findViewById(R.id.main_frame);

        js = (JoyStick) findViewById(R.id.layout_joystick);
        ViewGroup.LayoutParams params = js.getLayoutParams();
        params.width = size.x / 6;
        params.height = size.x / 6;
        js.setLayoutParams(params);
        js.setStickRadius(size.x / 55);
        js.setOFFSET(size.x / 60);
        js.setOnTouchListener(layout_stickListener);
        ViewTreeObserver observer = js.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                js.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                js.init();
            }
        });

        tank = new ProthoTank(getApplicationContext(), main_frame, js, R.drawable.protho_tank, size);
        tank.setTankSize(size.x / 17, size.x / 17);
        tank.drawTank();

        /////////////////////
        enemy = new ProthoTank(getApplicationContext(), main_frame, js, R.drawable.protho_tank, size);
        enemy.setTankSize(size.x / 17, size.x / 17);
        enemy.drawTank();
        ////////////////////


        tank.bullet.init();
        bullet_stroke = size.x / 100;

        fire_indificator = (RelativeLayout) findViewById(R.id.fire_indificator_and_button);
        params = fire_indificator.getLayoutParams();
        params.width = size.x / 8;
        params.height = size.x / 8;
        fire_indificator.setLayoutParams(params);

        fire_button = (FireButton) findViewById(R.id.fire_button);
        params = fire_button.getLayoutParams();
        params.width = size.x / 16;
        params.height = size.x / 16;
        fire_button.setLayoutParams(params);
        fire_button.setOnClickListener(fireButtonListener);

        circleProgress = (FireIndicator) findViewById(R.id.fire_indicator);

        imageView = (ImageView) findViewById(R.id.animation_field);
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = size.x / 17;
                params.height = size.y / 17;
                imageView.setLayoutParams(params);
            }
        });

        receiveDataHandler.removeCallbacks(receiveDataRunnable);
        receiveDataHandler.post(receiveDataRunnable);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //doUnbindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((SocketService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };

    private void doBindService(){
        bindService(new Intent(MainActivity.this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }*/

    //Animation probe
    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever){
        int preDuration = 20;
        int slideDuration = 20;
        int postDuration = 20;

        imageView.setVisibility(View.INVISIBLE);
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(preDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(preDuration + slideDuration);
        fadeOut.setDuration(postDuration);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                if(images.length - 1 > imageIndex){
                    animate(imageView, images, imageIndex + 1, forever);
                }
                else{
                    if(forever == false){
                        animate(imageView, images, 0, forever);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }
}
