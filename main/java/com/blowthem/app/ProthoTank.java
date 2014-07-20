package com.blowthem.app;

import android.content.*;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;

import items.TankCore;
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
    private int stickSize;
    protected FireBullet bullet;

    private Display display;
    public int width;
    public int height;

    private DisplayMetrics localMetrics;
    public float observableWidth;
    public float observableHeight;

    private JoyStickClass joystick;

    TankCore core = new TankCore();

    public ProthoTank(Context context, ViewGroup layout, JoyStickClass joystick, int protho_tank_id, int stickSize, int tank_gun_id){
        mContext = context;
        protho_tank = BitmapFactory.decodeResource(mContext.getResources(), protho_tank_id);

        draw = new DrawCanvas(mContext);
        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();
        this.stickSize = stickSize;
        display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        bullet = new FireBullet(mContext, this);
        this.joystick = joystick;

        localMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localMetrics);
        //System.out.println(localMetrics);

        /**
         * !!!!!!!!!!!!!
         * Предлагаю вынести observable* вне танка, т.к. это не часть
         * танка, а скорее общая инфа
         */
        this.observableWidth = draw.getWidth();
        this.observableHeight = context.getResources().getDisplayMetrics().heightPixels;
        //System.out.println(" X : " + observableWidth + "\n" + " Y : " + observableHeight);

        //this.observableWidth *= 0.8;
        //this.observableHeight *= 0.8;

    }

    public void drawTank(int X, int Y){

        float x = X - joystick.getJoystickCenterX(), y = Y - joystick.getJoystickCenterY();
        coreAngle = (float) cal_angle(x / observableWidth, y / observableHeight);
        bitmapAngle = (float) Math.toDegrees(cal_angle(x, y));

        core.turn(coreAngle);
        core.step();

        coreAngle = (float) Math.toDegrees(coreAngle);

        draw.setAngle(coreAngle);
        draw.setX(core.getX() * observableWidth);
        draw.setY(core.getY() * observableHeight);

        drawTank();
    }

    private double cal_angle(float x, float y){
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

    public void drawTank() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) {}
        mLayout.addView(draw);
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

    protected class DrawCanvas extends View{
        private float x = 0, y = 0;
        private float angle = 0;

        private DrawCanvas(Context context){
            super(context);
            //setBackgroundColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //System.out.println(" X : " + canvas.getWidth() + " Y : " + canvas.getHeight());
            observableWidth = canvas.getWidth() - (0.5f * protho_tank.getWidth());
            observableHeight = canvas.getHeight() - (0.5f * protho_tank.getHeight());
            canvas.drawBitmap(rotateBitmap(protho_tank, bitmapAngle), x, y, paint);
            //canvas.drawBitmap(protho_tank, x, y, paint);
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

