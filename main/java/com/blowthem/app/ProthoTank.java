package com.blowthem.app;

import android.content.*;
import android.graphics.*;;
import android.util.DisplayMetrics;
import android.view.*;

import items.Constants;
import items.TankCore;
import poor2D.Operations; 
import poor2D.Vector;

/**
 * Created by walter on 08.07.14.
 */
public class ProthoTank{

    private Bitmap protho_tank;
    private Paint paint;
    private Context mContext;
    private ViewGroup mLayout;
    private ViewGroup.LayoutParams params;
    protected DrawCanvas draw;
    private int position_x = 100, position_y = 100;
    private float coreAngle = 0;
    private float bitmapAngle = 0; // created to approve the rotation of the bitmap
    private int tankWidth, tankHeight;
    public FireBullet bullet;

    public int width;
    public int height;


    private DisplayMetrics localMetrics;
    public float observableWidth;
    public float observableHeight;

    private JoyStick joystick;

    public TankCore core = new TankCore();

    public FireBullet getBullet() {
        return bullet;
    }

    public ProthoTank(Context context, ViewGroup layout, JoyStick joystick, int protho_tank_id, Point size){
        mContext = context;
        protho_tank = BitmapFactory.decodeResource(mContext.getResources(), protho_tank_id);

        draw = new DrawCanvas(mContext);
        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();
        width = size.x;
        height = size.y;
        bullet = new FireBullet(mContext, this);
        this.joystick = joystick;

        localMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localMetrics);

        //this.observableWidth = draw.getWidth();
        this.observableWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.observableHeight = context.getResources().getDisplayMetrics().heightPixels;

        mLayout.addView(draw);
    }

    /**
     * Joystick data
     */
    public void drawTank(float x, float y){
        coreAngle = (float) calculateAngleForTan(x / observableWidth, y / observableHeight);
        bitmapAngle = (float) Math.toDegrees(calculateAngleForTan(x, y));

        core.turn(coreAngle);
        core.step();

        coreAngle = (float) Math.toDegrees(coreAngle);

        draw.setAngle(coreAngle);
        draw.setX(core.getX() * observableWidth - 0.5f * protho_tank.getWidth());
        draw.setY(core.getY() * observableHeight - 0.5f * protho_tank.getHeight());
        //drawTank();
    }

    /**
     * Base data (the core isn't used)
     */
    public void drawTank(Vector position, Vector target){

        Vector operable = new Vector(target.get(0) * observableWidth, target.get(1) * observableHeight).normalized();

        bitmapAngle = (float) (operable.get(1) > 0.0f ?
                Math.toDegrees(Math.acos(Operations.scalarProduct(operable, Constants.HORIZONTAL_VECTOR))) :
                -Math.toDegrees(Math.acos(Operations.scalarProduct(operable, Constants.HORIZONTAL_VECTOR))));

        draw.setX(position.get(0) * observableWidth - 0.5f * protho_tank.getWidth());
        draw.setY(position.get(1) * observableHeight - 0.5f * protho_tank.getHeight());
        //drawTank();
    }

    /**
     * The preferable one (because the angle is already calculated in the enemy's core)
     */
    public void drawTank(Vector position, Vector target, float bitmapAngle){
        this.bitmapAngle = bitmapAngle;
        //float coreAngle  = (float) calculateAngleForTan(position.get(0), position.get(1));
        //core.turn(coreAngle);
        core.setPosition(position);
        core.setTarget(target);
        draw.setX(position.get(0) * observableWidth - 0.5f * protho_tank.getWidth());
        draw.setY(position.get(1) * observableHeight - 0.5f * protho_tank.getHeight());
        //System.out.println("!!! TARGET = " + core.getTarget());
        //drawTank();
    }

    private double calculateAngleForTan(float x, float y){
        if(x >= 0 && y >= 0)
            return Math.atan(y / x);
        else if(x < 0 && y >= 0)
            return Math.atan(y / x) + Math.PI;
        else if(x < 0 && y < 0)
            return Math.atan(y / x) + Math.PI;
        else if(x >= 0 && y < 0)
            return Math.atan(y / x) + 2.0f * Math.PI;
        return 0;
    }



    public void setTankSize(int width, int height) {
        protho_tank = Bitmap.createScaledBitmap(protho_tank, width, height, false);
        tankWidth = protho_tank.getWidth();
        tankHeight = protho_tank.getHeight();

        bullet.width = draw.getX() + width / 2;
        bullet.height = draw.getY() + height / 2;
    }

    public void drawTankInit(){
        draw.setX(core.getX() * observableWidth - 0.5f * protho_tank.getWidth());
        draw.setY(core.getY() * observableHeight - 0.5f * protho_tank.getHeight());
        draw.invalidate();
    }

    public void drawTank() {
        /*try {
            mLayout.removeView(draw);
        } catch (Exception e) {}
        mLayout.addView(draw);*/
        draw.setX(core.getX() * observableWidth - 0.5f * protho_tank.getWidth());
        draw.setY(core.getY() * observableHeight - 0.5f * protho_tank.getHeight());
        draw.invalidate();
    }

    public void drawFire(){
        try {
            mLayout.removeView(bullet);
        } catch (Exception e) {}
        mLayout.addView(bullet);
    }

    public void drawFire(MotionEvent event){
        bullet.setEdge_height(height - tankHeight * 2);
        bullet.setEdge_width(width - tankWidth * 2);
        drawFire();
    }

    public int getTankHeight() {
        return tankHeight;
    }

    public int getTankWidth() {
        return tankWidth;
    }

    public float getBitmapAngle() {
        return bitmapAngle;
    }

    public float getCoreAngle() {
        return coreAngle;
    }

    protected class DrawCanvas extends View{
        private float x = 0, y = 0;
        private float angle = 0;

        private DrawCanvas(Context context){
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            observableWidth = canvas.getWidth();
            observableHeight = canvas.getHeight();

            canvas.save();
            canvas.rotate(bitmapAngle, x + (protho_tank.getWidth() / 2), y + (protho_tank.getHeight() / 2));
            canvas.drawBitmap(protho_tank, x, y, paint);
            canvas.restore();

            //canvas.drawBitmap(rotateBitmap(protho_tank, bitmapAngle), x, y, paint);
        }

        public Bitmap rotateBitmap(Bitmap source, float angle){
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }


    }
}

