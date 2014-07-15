package com.blowthem.app;

import android.content.*;
import android.graphics.*;
import android.util.Log;
import android.view.*;

import items.TankCore;
import poor2D.Vector;

/**
 * Created by walter on 08.07.14.
 */
public class ProthoTank {
    private Bitmap protho_tank;
    private Paint paint;
    private Context mContext;
    private ViewGroup mLayout;
    private ViewGroup.LayoutParams params;
    private DrawCanvas draw;
    private int position_x = 100, position_y = 100;
    private float angle = 0;
    private int tankWidth, tankHeight;
    private int stickSize;
    private Bitmap tankGun;
    private int gun_width, gun_height;
    private DrawGun gun;
    protected FireBullet bullet;

    private Display display;
    private int width;
    private int height;

    private JoyStickClass joystick;

    private TankCore tank = new TankCore();

    public ProthoTank(Context context, ViewGroup layout, JoyStickClass joystick, int protho_tank_id, int stickSize, int tank_gun_id){
        mContext = context;
        protho_tank = BitmapFactory.decodeResource(mContext.getResources(), protho_tank_id);

        tankGun = BitmapFactory.decodeResource(mContext.getResources(), tank_gun_id);

        gun_width = tankGun.getWidth();
        gun_height = tankGun.getHeight();
        gun = new DrawGun(mContext);

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
    }

    public void drawTank(MotionEvent arg1) {

        /**
         * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         * Слава, заебашь в JoystickCenterX и JoystickCenterY точные координаты центра
         * джойстика. Я их определил на глазок, а это не камильфо
         * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         */

        //final float joystickCenterX = 140.0f, joystickCenterY = 400.0f;//32 389

        /**
         * Это временное решение, т.к. на движение влияют касания экрана не только
         * внутри джойстика, но и вне его. Нужно дорабатывать джойстик:)
         */
        //System.out.println("X : " + arg1.getX() + " Y : " + arg1.getY());
        //System.out.println(joystick + " joystick center : (" + joystick.getJoystickCenterX() + " , " + joystick.getJoystickCenterY() + "); ");
        angle = (float) cal_angle(arg1.getX() - joystick.getJoystickCenterX(), -(arg1.getY() - joystick.getJoystickCenterY()));
        //Log.d(String.valueOf(Math.toDegrees(angle)), "Angle");
        //tank.turn( (float)(45.0f * Math.PI / 180.0f));
        //tank.setTarget(new Vector(0.0f, -1.0f));
        tank.turn(angle);
        tank.step(5);
        draw.setX(tank.getPosition().get(0));
        draw.setY(-tank.getPosition().get(1));
        //System.out.println(tank);
        drawTank();
    }

    public void turnTank(int angle){
        tank.turn(angle);
        draw.setX(tank.getTarget().get(0));
        draw.setY(-tank.getTarget().get(1));
        //System.out.println(tank);
        drawTank();
    }

    private double cal_angle(float x, float y) {
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

    public void drawGun(MotionEvent arg1) {
        gun.setX(draw.getX() + tankWidth / 2);
        gun.setY(draw.getY() + tankHeight / 2);
        //System.out.println("GUN width : " + gun.getX() + " height : " + gun.getY());
        drawGun();
    }

    public void setTankSize(int width, int height) {
        protho_tank = Bitmap.createScaledBitmap(protho_tank, width, height, false);
        tankWidth = protho_tank.getWidth();
        tankHeight = protho_tank.getHeight();
        gun.setY(draw.getX() + width / 2);
        gun.setX(draw.getX() + height / 2);

        bullet.width = draw.getX() + width / 2;
        bullet.height = draw.getY() + height / 2;
    }

    public void drawTank() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) { }
        mLayout.addView(draw);
    }

    public void drawGun(){
        try {
            mLayout.removeView(gun);
        } catch (Exception e) { }
        mLayout.addView(gun);
    }

    public void drawFire(){
        try {
            mLayout.removeView(bullet);
        } catch (Exception e) { }
        mLayout.addView(bullet);
    }

    public void drawFire(MotionEvent event){
        bullet.setEdge_height(height - tankHeight * 2);
        bullet.setEdge_width(width - tankWidth * 2);
        drawFire();
    }

    private class DrawCanvas extends View{
        private float x = 0, y = 0;

        private DrawCanvas(Context context){
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(protho_tank, x, y, paint);
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
    }

    protected class DrawGun extends View{
        private float x, y;

        private DrawGun(Context context){
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(tankGun, x, y, paint);
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
    }

    public DrawGun getGun() {
        return gun;
    }
}
