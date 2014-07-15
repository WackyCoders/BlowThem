package com.blowthem.app;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.util.*;

import items.TankCore;

/**
 * Created by walter on 09.07.14.
 */
public class StickSpace extends RelativeLayout {

    private ProthoTank tank;
    private MotionEvent event1;
    private JoyStickClass js;

    private Handler mHandler = new Handler();
    private Runnable mUpdateTask = new Runnable(){
        public void run(){
            tank.drawTank(event1);
            tank.drawGun(event1);
        }
    };

    public StickSpace(Context context){
        super(context);
    }

    public StickSpace(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public StickSpace(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    Timer timer = new Timer();
    public boolean onTouchEvent(MotionEvent event){
        event1 = event;
        js.drawStick(event1);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                timer = new Timer();
                mHandler.removeCallbacks(mUpdateTask);
                tank.drawTank(event1);
                tank.drawGun(event1);
                break;

            case MotionEvent.ACTION_MOVE:
                mHandler.removeCallbacks(mUpdateTask);
                mHandler.post(mUpdateTask);
                timer.cancel();
                timer = null;
                timer = new Timer();
                timer.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        //mHandler.removeCallbacks(mUpdateTask);
                        //mHandler.post(mUpdateTask);
                        final float X = event1.getX();
                        final float Y = event1.getY();
                        Thread t = new Thread() {
                            @Override
                            public void run() {
                                while(X == event1.getX() && Y == event1.getY()) {
                                    //try {
                                        //Thread.sleep(0);
                                        mHandler.removeCallbacks(mUpdateTask);
                                        mHandler.post(mUpdateTask);
                                    //} catch (InterruptedException ie) {
                                    //}
                                }
                            }
                        };
                        t.start();
                    }
                }, 0);
                break;

            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mUpdateTask);
                timer.cancel();
                timer = null;
                break;
        }
        return true;
    }

    public void setTank(ProthoTank tank) {
        this.tank = tank;
    }

    public void setJs(JoyStickClass js) {
        this.js = js;
    }
}
