package com.blowthem.app;

import android.content.*;
import android.graphics.*;
import android.view.*;

/**
 * Created by walter on 11.07.14.
 */
public class FireBullet extends View {
    private Paint paint;
    private ProthoTank gun;
    protected float width;
    protected float height;
    private int edge_width, edge_height;
    private boolean flag_of_fire_rate = true;
    private int STROKE;

    public FireBullet(Context context, ProthoTank gun){
        super(context);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        this.gun = gun;
        this.width = gun.getGun().getX();
        this.height = gun.getGun().getY();
        flag_of_fire_rate = true;
    }

    public void init(){
        this.width = gun.getGun().getX();
        this.height = gun.getGun().getY();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStrokeWidth(STROKE);
        if(width + 10 < edge_width && height + 10 < edge_height) {
            canvas.drawLine(width, height, width + 10, height + 10, paint);
            width += 15;
            height += 15;
        } else {
            flag_of_fire_rate = false;
        }
    }

    public void setEdge_width(int edge_width) {
        this.edge_width = edge_width;
    }

    public void setEdge_height(int edge_height) {
        this.edge_height = edge_height;
    }

    public boolean isFlag_of_fire_rate() {
        return flag_of_fire_rate;
    }

    public void setSTROKE(int STROKE) {
        this.STROKE = STROKE;
    }
}
