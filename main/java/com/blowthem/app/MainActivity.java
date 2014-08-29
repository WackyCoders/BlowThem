package com.blowthem.app;

import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.animation.*; 
import android.widget.*;

import com.blowthem.app.Dialogs.ExitDialog;
import com.blowthem.app.Dialogs.LostDialog;
import com.blowthem.app.Dialogs.WaitDialog;
import com.blowthem.app.Dialogs.WonDialog;
import com.blowthem.app.GameLoop.MainGamePanel;
import com.blowthem.app.battle.Tank_T34;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import items.Constants;
import poor2D.Vector;
 

public class MainActivity extends ActionBarActivity {

    //LOOPER
    private MainGamePanel gameLooper;

    private boolean weirdWayToAvoidOnBundleSave = true;
    private boolean anotherWeirdWayToAvoidOnBundleSave = true;

    //myHp
    private ProgressBar my_hp;
    private boolean IamHitted = false;

    //Dialog, waiting everyone to start the battle
    private WaitDialog waitDialog;

    private BlockingQueue<Float> queue = new LinkedBlockingDeque<Float>();
    private SocketService clientService = new SocketService();
    private Intent clientIntent;
    private UpdateReceiver updateReceiver = new UpdateReceiver();
    //private JoyStickClass js;
    private JoyStick js;
    private Tank_T34 tank;
    private Tank_T34 enemy;
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
            synchronized (tank) {
                clientHandler.removeCallbacks(clientRunnable);
                if(gameLooper.isAllowed()) {
                    tank.drawFire(fireEvent);
                }
            }
        }
    };

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
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

    //receivable data
    private ArrayList<String> listMotion, listFire;
    private String startedBattle, battlePosition, won;
    private Handler receiveDataHandler = new Handler();
    private Runnable receiveDataRunnable = new Runnable() {
        @Override
        public void run() {

            if(listMotion != null) {
                if(Float.parseFloat(listMotion.get(0)) > 0.0f && Float.parseFloat(listMotion.get(1)) > 0.0f){
                    enemy.drawTank(new Vector(Float.parseFloat(listMotion.get(0)), Float.parseFloat(listMotion.get(1))),
                            new Vector(Float.parseFloat(listMotion.get(3)), Float.parseFloat(listMotion.get(4))), Float.parseFloat(listMotion.get(2)));
                    listMotion = null;

                }
            }

            if(listFire != null){
                enemy.bullet = new FireBullet(getApplicationContext(), enemy);
                enemy.bullet.setSTROKE(bullet_stroke);

                enemyBulletThreadHandler.removeCallbacks(enemyBulletThreadTask);
                enemyBulletThreadHandler.postDelayed(enemyBulletThreadTask, 0);
                listFire = null;
            }

            if(startedBattle != null){
                waitDialog.dismiss();
                waitDialog = null;
                startedBattle = null;
            }

            if(weirdWayToAvoidOnBundleSave) {
                battlePosition = LoginBridge.battlePosition;
            }
            if(battlePosition != null){
                //System.out.println("!!! " + battlePosition);
                if(battlePosition.equals("$first$")){
                    enemy.core.setPosition(new Vector(0.9f, 0.5f));
                    enemy.core.setTarget(Constants.OPPOSIT_HORIZONTAL_VECTOR);

                    tank.core.setPosition(new Vector(0.1f, 0.5f));

                } else if(battlePosition.equals("$second$")){
                    tank.core.setPosition(new Vector(0.9f, 0.5f));
                    tank.core.setTarget(Constants.OPPOSIT_HORIZONTAL_VECTOR);

                    enemy.core.setPosition(new Vector(0.1f, 0.5f));
                }
                battlePosition = null;
                //LoginBridge.battlePosition = null;
                tank.drawTankInit();
                enemy.drawTankInit();
                weirdWayToAvoidOnBundleSave = false;
            }

            if(won != null){
                System.out.println("!!!!! : " + won);
                WonDialog wonDialog = new WonDialog(MainActivity.this);
                wonDialog.show();
                //anotherWeirdWayToAvoidOnBundleSave = false;
                won = null;
            }

            receiveDataHandler.post(this);
        }
    };

    private Handler eFire = new Handler();
    private Runnable eFireTask = new Runnable(){
        public void run(){
            synchronized (tank) {
                if(gameLooper.isAllowed()) {
                    enemy.drawFire();
                }
            }
        }
    };

    private Handler hpHandler = new Handler();
    private Runnable hpRunnable = new Runnable() {
        @Override
        public void run() {
            if(IamHitted){
                if(tank.getInitials().getHp() > 0){
                    Float division = tank.getInitials().getHp() / tank.HP;
                    my_hp.setProgress((int) (division * 100));
                } else {
                    //GameOver
                    my_hp.setProgress(0);
                    clientIntent.putExtra("lost", "$lost$");
                    clientHandler.post(clientRunnable);
                    Handler localHandler = new Handler();
                    localHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clientIntent.removeExtra("won");
                        }
                    }, 3000);
                    isClicked = false;
                    LostDialog lostDialog = new LostDialog(MainActivity.this);
                    lostDialog.show();
                }
                IamHitted = false;
            }
            hpHandler.post(this);
        }
    };

    private Handler enemyBulletThreadHandler = new Handler();
    private Runnable enemyBulletThreadTask = new Runnable() {
        @Override
        public void run() {
            eFire.removeCallbacks(eFireTask);
            eFire.post(eFireTask);
            if(enemy.bullet.isAlive()){
                enemy.bullet.enemyPosition = (Vector) tank.core.getPosition().clone();
                //System.out.println("X = " + enemy.bullet.getWidthCore() + " ; Y = " + enemy.bullet.getHeightCore());
                enemyBulletThreadHandler.postDelayed(this, 0);
            } else {
                if(enemy.bullet.core.isHittedEnemy()){
                    tank.getInitials().setHp(tank.getInitials().getHp() - enemy.getInitials().getDamage());
                    IamHitted = true;
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if(enemy.bullet.height >= 0 && enemy.bullet.width >= 0) {
                    params.setMargins((int) enemy.bullet.width - enemy.getTankWidth(), (int) enemy.bullet.height - enemy.getTankHeight(), 0, 0);
                } else if(enemy.bullet.height < 0 && enemy.bullet.width > 0){
                    params.setMargins((int) enemy.bullet.width - (int)0.5 * enemy.getTankWidth(), (int) enemy.bullet.height + (int)0.5 *enemy.getTankHeight(), 0, 0);
                } else if(enemy.bullet.height > 0 && enemy.bullet.width < 0){
                    params.setMargins((int) enemy.bullet.width + (int)0.5 * enemy.getTankWidth(), (int) enemy.bullet.height - (int)0.5 * enemy.getTankHeight(), 0, 0);
                } else if(enemy.bullet.height < 0 && enemy.bullet.width < 0){
                    params.setMargins((int) enemy.bullet.width + enemy.getTankWidth(), (int) enemy.bullet.height + enemy.getTankHeight(), 0, 0);
                }

                imageView.setLayoutParams(params);
                animateExplode.removeCallbacks(animateExplodeTask);
                animateExplode.postDelayed(animateExplodeTask, 0);

                /*if (sound != null) {
                    sound.release();
                    sound = null;
                }
                sound = MediaPlayer.create(getApplicationContext(), R.raw.bomb_exploding);
                sound.start();*/
            }
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
                if(tank.bullet.core.isHittedEnemy()){
                    enemy.getInitials().setHp(enemy.getInitials().getHp() - tank.getInitials().getDamage());
                }

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
                clientIntent.removeExtra("fire");
            }
        }
    };

    private Handler mHandler = new Handler();
    private Runnable mUpdateTask = new Runnable(){
        public void run(){
            synchronized (tank) {
                clientHandler.removeCallbacks(clientRunnable);
                if(gameLooper.isAllowed() && isClicked) {
                    tank.drawTank(js.getNormalX(), js.getNormalY());
                    ArrayList<String> list = new ArrayList<String>();
                    list.add("$motion$");
                    list.add(String.valueOf(tank.core.getPosition().get(0)));
                    list.add(String.valueOf(tank.core.getPosition().get(1)));
                    list.add(String.valueOf(tank.getBitmapAngle()));
                    //Target should be also sent, I hope
                    list.add(String.valueOf(tank.core.getTarget().get(0)));
                    list.add(String.valueOf(tank.core.getTarget().get(1)));
                    clientIntent.putStringArrayListExtra("motion", list);
                    clientHandler.post(clientRunnable);
                }

                //enemy.drawTank(new Vector(0.5f, 0.5f), new Vector(-0.1f, 0.1f));

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

            switch (event.getAction()) {
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
                //fire coordinates are sent to the opponent
                ArrayList<String> list = new ArrayList<String>();
                list.add("$fire$");
                list.add(String.valueOf(tank.core.getPosition().get(0)));
                list.add(String.valueOf(tank.core.getPosition().get(1)));
                clientIntent.putStringArrayListExtra("fire", list);
                clientHandler.post(clientRunnable);

                flagEnablesToFire = false;
                indicatorHandler.removeCallbacks(indicatorTask);
                indicatorHandler.postDelayed(indicatorTask, 0);

                if(sound != null){
                    sound.release();
                    sound= null;
                }
                sound = MediaPlayer.create(getApplicationContext(), R.raw.laser_blaster);
                sound.start();

                isClicked = false;
                tank.bullet = new FireBullet(getApplicationContext(), tank);
                tank.bullet.setSTROKE(bullet_stroke);

                bulletThreadHandler.removeCallbacks(bulletThreadTask);
                bulletThreadHandler.postDelayed(bulletThreadTask, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //System.out.println("!!!ON CREATE CALLED");

        getActionBar().hide();
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Display currentDisplay = getWindowManager().getDefaultDisplay();
        size = new Point();
        currentDisplay.getSize(size);

        clientIntent = new Intent(MainActivity.this, SocketService.class);

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

        tank = new Tank_T34(getApplicationContext(), main_frame, js, R.drawable.protho_tank, size);
        tank.setTankSize(size.x / 17, size.x / 17);
        //tank.drawTank();


        /////////////////////
        enemy = new Tank_T34(getApplicationContext(), main_frame, js, R.drawable.protho_tank, size);
        enemy.setTankSize(size.x / 17, size.x / 17);
        //enemy.drawTank();
        ////////////////////


        //tank.bullet.init();
        //enemy.bullet.init();
        bullet_stroke = size.x / 100;

        this.my_hp = (ProgressBar) findViewById(R.id.my_hp);
        //my_hp.setCancelable(true);
        //my_hp.setMessage("File downloading ...");
        //my_hp.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        my_hp.setProgress(100);
        my_hp.setMax(100);

        params = my_hp.getLayoutParams();
        params.width = size.x / 7;
        params.height = size.y / 15;
        my_hp.setLayoutParams(params);

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

        //receiveDataHandler.removeCallbacks(receiveDataRunnable);
        receiveDataHandler.post(receiveDataRunnable);
        registerReceiverThread.start();

        gameLooper = new MainGamePanel(this, tank, enemy);
        gameLooper.onStart();

        waitDialog = new WaitDialog(MainActivity.this);
        waitDialog.show();

        hpHandler.post(hpRunnable);
    }

    private Thread registerReceiverThread = new Thread(new Runnable() {
        @Override
        public void run() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("myBroadcast");
            registerReceiver(updateReceiver, intentFilter);
        }
    });

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //waitDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
        //doUnbindService();
        weirdWayToAvoidOnBundleSave = true;
        anotherWeirdWayToAvoidOnBundleSave = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                ExitDialog exitDialog = new ExitDialog(MainActivity.this);
                exitDialog.show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //waitDialog.dismiss();
    }

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

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            listMotion = intent.getStringArrayListExtra("update");
            listFire = intent.getStringArrayListExtra("fireUpdate");
            startedBattle = intent.getStringExtra("battleStarted");
            won = intent.getStringExtra("won");
            //battlePosition = intent.getStringExtra("currentBattlePosition");
        }
    }
}
