package com.blowthem.app.battle;

import android.content.Context;
import android.graphics.Point;
import android.view.ViewGroup;

import com.blowthem.app.JoyStick;
import com.blowthem.app.ProthoTank;

/**
 * Created by walter on 29.08.14.
 */
public class Panzerwagen_Tiger_1 extends ProthoTank {

    private final Integer type;
    private final StaticInitials initials;
    public final Float HP = 380.0f;
    public final Float DAMAGE = 150.0f;

    public Panzerwagen_Tiger_1(Context context, ViewGroup layout, JoyStick joystick, int tank_id, Point size){
        super(context, layout, joystick, tank_id, size);
        this.type = Constants.PanzerkampfwagenVI;
        this.initials = new StaticInitials(HP, DAMAGE);
    }

    public Integer getType() {
        return type;
    }

    public StaticInitials getInitials() {
        return initials;
    }
}