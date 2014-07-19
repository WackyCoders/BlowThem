package com.blowthem.app;

import android.content.*;
import android.graphics.*;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;

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
    protected DrawCanvas draw;
    private int position_x = 100, position_y = 100;
    private float angle = 0;
    private int tankWidth, tankHeight;
    private int stickSize;
    protected FireBullet bullet;

    private Display display;
    private int width;
    private int height;

    private JoyStickClass joystick;

    public TankCore tank = new TankCore();

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
    }

    public void drawTank(int X, int Y) {
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
        //System.out.println(" event in protho tank : " + arg1);
        angle = (float) cal_angle(X - joystick.getJoystickCenterX(), -(Y - joystick.getJoystickCenterY()));
        tank.turn(angle);
        tank.step(5);
        angle = (float) cal_angle_degrees(X - joystick.getJoystickCenterX(), -(Y - joystick.getJoystickCenterY()));
        draw.setAngle(-angle);
        draw.setX(tank.getPosition().get(0));
        draw.setY(-tank.getPosition().get(1));
        drawTank();
    }

    private double cal_angle_degrees(float x, float y) {
        if(x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if(x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
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
        } catch (Exception e) { }
        mLayout.addView(draw);
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
            canvas.drawBitmap(rotateBitmap(protho_tank, angle), x, y, paint);
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

