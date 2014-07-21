package com.blowthem.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by walter on 20.07.14.
 */
public class JoyStick extends View {

    private float circleCenterX;
    private float circleCenterY;
    private int roadColor;
    private float roadRadius;
    private int roadOuterCircleColor;
    private float roadOuterCircleStrokeWidth;
    private float roadOuterCircleRadius;
    private boolean touch_state = false;
    private int stickRadius = 0; //make it 10 times less than main view
    private int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;
    private int OFFSET = 0;
    private float X, Y; // don't forget to give them initial coordinates view view being created
    private float joystickCenterX, joystickCenterY;
    private float localCenterX, localCenterY;
    protected int TANK_X, TANK_Y;

    public void setOFFSET(int OFFSET) {
        this.OFFSET = OFFSET;
    }

    public void setX(float x) {
        X = x;
    }

    public void setY(float y) {
        Y = y;
    }

    public JoyStick(Context context){
        super(context);
        init();
    }

    public JoyStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        this.X = getWidth() / 2;
        this.Y = getHeight() / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        this.roadRadius = getWidth() / 2 * 9 / 10;
        this.circleCenterX = getWidth() / 2;
        this.circleCenterY = getHeight() / 2;
        this.roadColor = Color.RED;
        this.roadOuterCircleColor = Color.YELLOW;
        this.roadOuterCircleStrokeWidth = 7.0f;
        this.roadOuterCircleRadius = this.roadRadius + 2;


        drawMainCircle(paint, canvas);
        drawMainCircleOuterCircle(paint, canvas);
        drawStick(paint, canvas, X, Y);
    }

    private void drawMainCircle(Paint paint, Canvas canvas){
        paint.setColor(roadColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(circleCenterX, circleCenterY, this.roadRadius, paint);
    }

    private void drawMainCircleOuterCircle(Paint paint, Canvas canvas){
        paint.setColor(roadOuterCircleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roadOuterCircleStrokeWidth);
        canvas.drawCircle(circleCenterX, circleCenterY, roadOuterCircleRadius, paint);
    }

    private void drawStick(Paint paint, Canvas canvas, float coordinateX, float coordinateY){
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(coordinateX, coordinateY, this.stickRadius, paint);
    }

    public void drawStick(MotionEvent arg1) {
        this.TANK_X = (int)arg1.getRawX();
        this.TANK_Y = (int)arg1.getRawY();

        position_x = (int) (arg1.getX() - (getWidth() / 2));
        position_y = (int) (arg1.getY() - (getHeight() / 2));
        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle = (float) cal_angle(position_x, position_y);
        if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
            if(distance <= (getWidth() / 2) - OFFSET) {
                this.X = arg1.getX();
                this.Y = arg1.getY();
                int[] coordinates = new int[2];
                getLocationInWindow(coordinates);
                localCenterX = getWidth() / 2;
                localCenterY = getHeight() / 2;
                joystickCenterX = coordinates[0] + getWidth() / 2;
                joystickCenterY = coordinates[1] + getHeight() / 2;
                invalidate();
                touch_state = true;
            }

        } else if(arg1.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            int[] coordinates = new int[2];
            getLocationInWindow(coordinates);
            localCenterX = getWidth() / 2;
            localCenterY = getHeight() / 2;
            joystickCenterX = coordinates[0] + getWidth() / 2;
            joystickCenterY = coordinates[1] + getHeight() / 2;
            if(distance <= (getWidth() / 2) - OFFSET) {
                this.X = arg1.getX();
                this.Y = arg1.getY();
                invalidate();
            } else if(distance > (getWidth() / 2) - OFFSET){
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) * ((getWidth() / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) * ((getHeight() / 2) - OFFSET));
                x += (getWidth() / 2);
                y += (getHeight() / 2);
                this.X = x;
                this.Y = y;
                invalidate();
            } else {
                //mLayout.removeView(draw);
            }
        } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
            this.X = getWidth() / 2;
            this.Y = getHeight() / 2;
            int[] coordinates = new int[2];
            getLocationInWindow(coordinates);
            localCenterX = getWidth() / 2;
            localCenterY = getHeight() / 2;
            joystickCenterX = coordinates[0] + getWidth() / 2;
            joystickCenterY = coordinates[1] + getHeight() / 2;
            invalidate();
            touch_state = false;
        }
    }

    private double cal_angle(float x, float y) {
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        drawStick(event);
        return true;
    }

    public float getJoystickCenterX() {
        return joystickCenterX;
    }

    public float getJoystickCenterY() {
        return joystickCenterY;
    }

    public void setStickRadius(int stickRadius) {
        this.stickRadius = stickRadius;
    }
}

