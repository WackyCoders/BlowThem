package com.blowthem.app.GameLoop;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;

import com.blowthem.app.*;

/**
 * Created by walter on 19.08.14.
 *
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private Context currentContext;
    private JoyStick js;
    private ProthoTank tank;
    private ProthoTank enemy;

    private boolean allowed = true; // motion allowed

    private static final String TAG = MainGamePanel.class.getSimpleName();

    private LoopThread thread;

    public MainGamePanel(Context context, ProthoTank tank, ProthoTank enemy) {
        super(context);
        this.tank = tank;
        this.enemy = enemy;
        this.currentContext = context;
        getHolder().addCallback(this);
        thread = new LoopThread(getHolder(), this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void onStart(){
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //thread.setRunning(true);
        //thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is being destroyed");
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }

    private Handler drawTankHandler = new Handler();
    private Runnable drawTankRunnable = new Runnable() {
        @Override
        public void run() {
            tank.drawTank();
            enemy.drawTank();
        }
    };

    public void render(Canvas canvas) {
        drawTankHandler.removeCallbacks(drawTankRunnable);
        drawTankHandler.post(drawTankRunnable);
    }

    public void update() {
        allowed = true;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
