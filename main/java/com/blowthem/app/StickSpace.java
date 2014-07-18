package com.blowthem.app;

import android.app.Activity;
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
    private JoyStickClass js;

    public StickSpace(Context context){
        super(context);
    }

    public StickSpace(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public StickSpace(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void setTank(ProthoTank tank) {
        this.tank = tank;
    }

    public void setJs(JoyStickClass js) {
        this.js = js;
    }
}