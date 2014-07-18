package com.blowthem.app;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;

public class RotationRate extends View {
    private float circleCenterX;
    private float circleCenterY;
    private int roadColor;
    private float roadStrokeWidth;
    private float roadRadius;
    private int roadInnerCircleColor;
    private float roadInnerCircleStrokeWidth;
    private float roadInnerCircleRadius;
    private int roadOuterCircleColor;
    private float roadOuterCircleStrokeWidth;
    private float roadOuterCircleRadius;
    private int arcLoadingColor;
    private float arcLoadingStrokeWidth;
    private float arcLoadingDashLength;
    private float arcLoadingDistanceBetweenDashes;
    private float arcLoadingStartAngle;

    private int CenterX;
    private int CenterY;
    private int arcScrolled = 0;

    private int rotateAngle;

    private ProthoTank tank;

    public RotationRate(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(context, attrs);
    }

    public void setRoadRadius(float roadRadius) {
        this.roadRadius = roadRadius;
    }

    public void setRoadInnerCircleRadius(float roadInnerCircleRadius) {
        this.roadInnerCircleRadius = roadInnerCircleRadius;
    }

    public void setRoadOuterCircleRadius(float roadOuterCircleRadius) {
        this.roadOuterCircleRadius = roadOuterCircleRadius;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circleCenterX = w / 2;
        circleCenterY = h / 2;

        int paddingInContainer = 3;
        roadRadius = (w / 2) - (roadStrokeWidth / 2) - paddingInContainer;

        int innerCirclesPadding = 3;
        roadOuterCircleRadius = (w / 2) - paddingInContainer -
                (roadOuterCircleStrokeWidth / 2) - innerCirclesPadding;

        roadInnerCircleRadius = roadRadius - (roadStrokeWidth / 2)
                + (roadInnerCircleStrokeWidth / 2) + innerCirclesPadding;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG |
                Paint.ANTI_ALIAS_FLAG);

        drawRoad(paint, canvas);
        drawRoadInnerCircle(paint, canvas);
        drawRoadOuterCircle(paint, canvas);
        drawArcTouched(paint, canvas);
    }

    private void initializeAttributes(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleRoadProgressWidget);
        circleCenterX = ta.getFloat(R.styleable.CircleRoadProgressWidget_circleCenterPointX, 54f);
        circleCenterY = ta.getFloat(R.styleable.CircleRoadProgressWidget_circleCenterPointY, 54f);
        roadColor = ta.getColor(R.styleable.CircleRoadProgressWidget_roadColor, Color.parseColor("#575757"));
        roadStrokeWidth = ta.getFloat(R.styleable.CircleRoadProgressWidget_roadStrokeWidth, 20f);
        roadRadius = ta.getFloat(R.styleable.CircleRoadProgressWidget_roadRadius, 42f);
        roadInnerCircleColor = ta.getColor(R.styleable.CircleRoadProgressWidget_roadInnerCircleColor, Color.parseColor("#ffffff"));
        roadInnerCircleStrokeWidth = ta.getFloat(R.styleable.CircleRoadProgressWidget_roadInnerCircleStrokeWidth, 1f);
        roadInnerCircleRadius = ta.getFloat(R.styleable.CircleRoadProgressWidget_roadInnerCircleRadius, 42f);
        roadOuterCircleColor = ta.getColor(R.styleable.CircleRoadProgressWidget_roadOuterCircleColor, Color.parseColor("#ffffff"));
        roadOuterCircleStrokeWidth = ta.getFloat(R.styleable.CircleRoadProgressWidget_roadOuterCircleStrokeWidth, 1f);
        roadOuterCircleRadius = ta.getFloat(R.styleable.CircleRoadProgressWidget_roadOuterCircleRadius, 42f);
        arcLoadingColor = ta.getColor(R.styleable.CircleRoadProgressWidget_arcLoadingColor, Color.parseColor("#f5d600"));
        arcLoadingStrokeWidth = roadStrokeWidth;//ta.getFloat(R.styleable.CircleRoadProgressWidget_arcLoadingStrokeWidth, 3f);
        arcLoadingDashLength = ta.getFloat(R.styleable.CircleRoadProgressWidget_arcLoadingDashLength, 1f);
        arcLoadingDistanceBetweenDashes = 0;//ta.getFloat(R.styleable.CircleRoadProgressWidget_arcLoadingDistanceBetweenDashes, 5f);
        arcLoadingStartAngle = ta.getFloat(R.styleable.CircleRoadProgressWidget_arcLoadingStartAngle, 90f);
        ta.recycle();

    }

    private void drawRoad(Paint paint, Canvas canvas){
        paint.setDither(true);
        paint.setColor(roadColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roadStrokeWidth);
        canvas.drawCircle(circleCenterX, circleCenterY, roadRadius, paint);
    }

    private void drawRoadInnerCircle(Paint paint, Canvas canvas){
        paint.setDither(true);
        paint.setColor(roadInnerCircleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roadInnerCircleStrokeWidth);
        canvas.drawCircle(circleCenterX, circleCenterY, roadInnerCircleRadius, paint);
    }

    private void drawRoadOuterCircle(Paint paint, Canvas canvas){
        paint.setDither(true);
        paint.setColor(roadOuterCircleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roadOuterCircleStrokeWidth);
        canvas.drawCircle(circleCenterX, circleCenterY, roadOuterCircleRadius, paint);
    }

    private void drawArcTouched(Paint paint, Canvas canvas){
        paint.setColor(arcLoadingColor);
        paint.setStrokeWidth(arcLoadingStrokeWidth);
        paint.setPathEffect(new DashPathEffect(new float[] {arcLoadingDashLength, arcLoadingDistanceBetweenDashes}, 0));
        float delta = circleCenterX - roadRadius;
        float arcSize = (circleCenterX - (delta / 2f)) * 2f;
        RectF box = new RectF(delta, delta, arcSize, arcSize);
        //System.out.println("Angle : " + arcScrolled);
        float sweep = this.arcScrolled * 0.01f;
        canvas.drawArc(box, arcLoadingStartAngle + 90, this.arcScrolled, false, paint);
        rotateAngle = arcScrolled;
        //tank.turnTank(rotateAngle);
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
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //System.out.println("Center : ( " + event.getX() + " , " + event.getY() + " );");
                int[] coordinates = new int[2];
                ViewGroup.LayoutParams params = getLayoutParams();
                this.getLocationInWindow(coordinates);
                CenterX = params.width / 2 * 10 / 21;
                CenterY = params.height / 2 * 10 / 21;

                this.arcScrolled = (int) cal_angle(event.getX() - CenterX, event.getY() - CenterX);
                //System.out.println("new center : ( " + CenterX + " , " + CenterY + " );");
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                coordinates = new int[2];
                params = getLayoutParams();
                this.getLocationInWindow(coordinates);
                CenterX = params.width / 2 * 10 / 21;
                CenterY = params.height / 2 * 10 / 21;
                this.arcScrolled = (int) cal_angle(event.getX() - CenterX, event.getY() - CenterY);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                this.arcScrolled = 0;//(int)cal_angle(this.getWidth() - circleCenterX, this.getHeight() - circleCenterY);
                invalidate();
                break;
        }
        return true;
    }

    public int getRotateAngle() {
        return rotateAngle;
    }

    public void setTank(ProthoTank tank) {
        this.tank = tank;
    }
}
