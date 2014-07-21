package com.blowthem.app;

import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;

import com.blowthem.app.animation.ExplodeView;

import java.io.*;
import java.net.*;


public class MainActivity extends ActionBarActivity {

    private String messageToSend = "Hello!";

    //private JoyStickClass js;
    private JoyStick js;
    private ProthoTank tank;
    private FireIndicator circleProgress;
    private Point size;

    private MotionEvent fireEvent;
    private MotionEvent event1;

    private MediaPlayer sound;
    private int bullet_stroke;
    private static String SERVER_IP = "192.168.1.6";//192.168.56.1
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

    private Handler bulletThreadHandler = new Handler();
    private Runnable bulletThreadTask = new Runnable() {
        @Override
        public void run() {
            mFire.removeCallbacks(mFireTask);
            mFire.post(mFireTask);
            if(tank.bullet.isFlag_of_fire_rate()) {
                bulletThreadHandler.postDelayed(this, 0);
            } else {
                //ExplodeView explode = new ExplodeView(getApplicationContext());
                //explode.setX(tank.bullet.explodeX);
                //explode.setY(tank.bullet.explodeY);
                //main_frame.addView(explode);

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
                //tank.drawTank(event1);
                tank.drawTank(js.TANK_X, js.TANK_Y);
            }
        }
    };

    private Handler serverHandler = new Handler();
    private Runnable serverTask = new Runnable(){
        public void run(){
            //new ClientHandler(MainActivity.this).execute(messageToSend);

        }
    };

    private boolean isClicked = true;

    private View.OnTouchListener layout_stickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            event1 = event;
            js.drawStick(event1);
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
        if(isClicked && flagEnablesToFire) {
            flagEnablesToFire = false;
            messageToSend = "clicked";//server message
            //serverHandler.removeCallbacks(serverTask);
            //serverHandler.post(serverTask);

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
        //js.setStickSize(size.x / 25, size.x / 25);
        //js.setLayoutSize(size.x / 6, size.x / 6);
        //js.setLayoutAlpha(250);
        //js.setStickAlpha(240);
        //js.setOffset(90);
        //js.setMinimumDistance(30);

        tank = new ProthoTank(getApplicationContext(), main_frame, js, R.drawable.protho_tank);
        tank.setTankSize(size.x / 17, size.x / 17);
        tank.drawTank();
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
    }



    private class ClientHandler extends AsyncTask<String, Void, Socket> {
        private Socket socket;
        private String answer;
        private Context context;
        private BufferedWriter out;
        private BufferedReader in;

        public ClientHandler(Context context) {
            this.context = context;
            socket = null;
            out = null;
            in = null;
        }

        @Override
        protected Socket doInBackground(String... params) {
            try {
                socket = new Socket(SERVER_IP, 8060);
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.write(params[0]);
                out.flush();
                answer = in.readLine() + System.getProperty("line.separator");
                return socket;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            if (socket != null) {
                Toast.makeText(context, answer, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Can't connect to server!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
