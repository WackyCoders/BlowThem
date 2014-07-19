package com.blowthem.app;

import android.content.*;
import android.graphics.*;
import android.util.Log;
import android.view.*;

import items.BulletCore;
import poor2D.Operations;
import poor2D.Vector;

/**
 * Created by walter on 11.07.14.
 */
public class FireBullet extends View {
    private Paint paint;
    private ProthoTank tank;
    protected float width;
    protected float height;
    private int edge_width, edge_height;
    //private boolean flag_of_fire_rate = true;
    private int STROKE;
    BulletCore bullet;

    public FireBullet(Context context, ProthoTank tank){
        super(context);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        this.tank = tank;
        bullet = new BulletCore(new Vector(tank.getPosition().get(0), tank.getPosition().get(1)), tank.getTarget());
        this.width = tank.draw.getX() + tank.getTankWidth() / 2;
        this.height = tank.draw.getY() + tank.getTankHeight() / 2;
        //bullet.setFlagOfFireRate(true);
    }

    public void init(){
        this.width = tank.draw.getX() + tank.getTankWidth() / 2;
        this.height = tank.draw.getY() + tank.getTankHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStrokeWidth(STROKE);

        if(bullet.getFlagOfFireRate()) {
            bullet.bulletStep(tank.width, tank.height, 0.03f);
            width = bullet.getX() * tank.width;
            height = bullet.getY() * tank.height;
            Log.d("CoordADSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSs", bullet.getX() + " " + bullet.getY());
            canvas.drawCircle(width, height, 10, paint);

        } /*else {
            bullet.setFlagOfFireRate(false);
        }*/

    }

    public void setEdge_width(int edge_width) {
        this.edge_width = edge_width;
    }

    public void setEdge_height(int edge_height) {
        this.edge_height = edge_height;
    }

    public boolean isFlag_of_fire_rate() {
        //return flag_of_fire_rate;
        return bullet.getFlagOfFireRate();
    }

    public void setSTROKE(int STROKE) {
        this.STROKE = STROKE;
    }
}
