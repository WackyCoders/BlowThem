package com.blowthem.app;

import android.content.*;
import android.content.res.Resources;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

/**
 * Created by walter on 14.07.14.
 */
public class RotationRate {

    private ViewGroup.LayoutParams params;
    private Bitmap mBack;
    private Bitmap ring;
    private RectF mOval;
    private Paint mPaint;
    private ViewGroup mlayout;
    private Context mcontext;
    private DrawRing draw;

    public RotationRate(Context context, ViewGroup layout){
        this.mcontext = context;
        this.mlayout = layout;
        draw = new DrawRing(mcontext);
    }

    public void drawRing(){
        try {
            mlayout.removeView(draw);
        } catch (Exception e){}
        mlayout.addView(draw);
    }

    private class DrawRing extends View{
        public DrawRing(Context context){
            super(context);
            Resources res = getResources();
            mBack = BitmapFactory.decodeResource(res, R.drawable.back);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Bitmap ring = BitmapFactory.decodeResource(res, R.drawable.ring);
            mPaint.setShader(new BitmapShader(ring, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            mOval = new RectF(0, 0, mBack.getWidth(), mBack.getHeight());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.translate((getWidth() - mBack.getWidth()) / 2, (getHeight() - mBack.getHeight()) / 2);
            canvas.drawBitmap(mBack, 0, 0, null);
            float angle = 220;
            canvas.drawArc(mOval, -90, angle, true, mPaint);
            canvas.drawText("Text",
                    mBack.getWidth() / 2,
                    (mBack.getHeight() - mTextPaint.ascent()) / 2,
                    mTextPaint);
        }
}
