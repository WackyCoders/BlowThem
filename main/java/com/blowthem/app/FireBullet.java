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
    protected float width, height;
    private int edge_width, edge_height;;
    private int STROKE;
    protected float explodeX, explodeY;
    BulletCore core;

    public Vector enemyPosition;

    public FireBullet(Context context, ProthoTank tank){
        super(context);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        this.tank = tank;
        core = new BulletCore(tank.core.getPosition(), tank.core.getTarget());
        this.width = tank.draw.getX() + tank.getTankWidth() / 2;
        this.height = tank.draw.getY() + tank.getTankHeight() / 2;
    }

    public void init(){
        this.width = tank.draw.getX() + tank.getTankWidth() / 2;
        this.height = tank.draw.getY() + tank.getTankHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStrokeWidth(STROKE);
        if(core.getAlive()) {
            canvas.drawCircle(width, height, 5, paint);
            core.step(enemyPosition);
            width = core.getX() * tank.observableWidth;
            height = core.getY() * tank.observableHeight;
        }else {
            explodeX = this.width;
            explodeY = this.height;
        }
    }

    public void setEdge_width(int edge_width) {
        this.edge_width = edge_width;
    }

    public void setEdge_height(int edge_height) {
        this.edge_height = edge_height;
    }

    public boolean isAlive() {
        return core.getAlive();
    }

    public void setSTROKE(int STROKE) {
        this.STROKE = STROKE;
    }

    public float getWidthCore() {
        return width;
    }

    public float getHeightCore() {
        return height;
    }
}
