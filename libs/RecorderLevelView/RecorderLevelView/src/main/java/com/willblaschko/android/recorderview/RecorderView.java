package com.willblaschko.android.recorderview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author willb_000 on 5/5/2016.
 */
public class RecorderView extends View {

    private final static String TAG = "RecorderView";

    private static final int ROTATION_SPEED = 1;

    public static final int COLOR_INDICATOR_DEFAULT = 0xff3F51B5;
    public static final int COLOR_INDICATOR_GONE = 0x00000000;

    private float rmsdbLevel = 0;
    private float rotation = 0;

    Paint backgroundPaint;
    Paint wavePaint;

    int width = 0;
    int height = 0;
    int min = 0;
    int imageSize;
    int waveRotation = 30;

    Drawable microphone;

    public RecorderView(Context context) {
        super(context);
        init();
    }

    public RecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecorderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        min = Math.min(width, height);

        imageSize = (int) (min * .45);
        setRmsdbLevel(1);
    }

    private void init(){
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0x66000000);
        backgroundPaint.setStyle(Paint.Style.FILL);

        wavePaint = new Paint();
        wavePaint.setColor(COLOR_INDICATOR_DEFAULT);
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);
    }

    public void setRmsdbLevel(float level){
        rmsdbLevel = level;
        postInvalidate();
    }

    public void setIndicatorColor(int color){
        wavePaint.setColor(color);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(width / 2, height / 2, getRadius(), wavePaint);

        if(microphone == null){
            microphone = ContextCompat.getDrawable(getContext(), R.drawable.microphone);
            microphone.setFilterBitmap(true);
            microphone.setBounds((width - imageSize) / 2, (height - imageSize) / 2, width - ((width - imageSize) / 2), height - ((height - imageSize) / 2));
        }

        microphone.draw(canvas);

//
//        rotation+=ROTATION_SPEED;
//        postInvalidateDelayed(100);
    }

    private float getRadius(){
        float percent = (float) (rmsdbLevel * Math.log(rmsdbLevel)) * .01f;
        percent = Math.min(Math.max(percent, 0f), 1f);
        percent = .55f  + .45f * percent;
        return percent * ((float) min) / 2f;
    }
//
//    Path wavePath;
//    private Path getPath(){
//
//        wavePath = new Path();
//
//        wavePath.moveTo();
//
//        for(int i = 0; i < 360 / waveRotation; i++){
//
//        }
//
//        return wavePath;
//    }
//
//    private void getArcPoint(int degree, float value){
//
//    }
}
