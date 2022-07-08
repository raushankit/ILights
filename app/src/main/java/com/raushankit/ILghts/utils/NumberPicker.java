package com.raushankit.ILghts.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.raushankit.ILghts.R;

public class NumberPicker extends View {

    private static final String TAG = "NumberPicker";
    /*<attr name="android:textColor"/>
            <attr name="android:textSize"/>
            <attr name="backgroundColor"/>
            <attr name="gap"/>
            <attr name="icon"/>
            <attr name="iconTint"/>
            <attr name="selectedColor"/>
            <attr name="unselectedColor"/>
            <attr name="numberOfPins"/>*/
    private final Point dimens = new Point();
    private final Paint textPaint = new Paint();
    private final PointF d = new PointF();
    private int textColor;
    private float textSize;
    private int backGroundColor;
    private float gap;
    private Drawable icon = null;
    private int iconTint;
    private int rowCount;
    private int selectedColor;
    private int unselectedColor;
    private int numberOfPins;

    public NumberPicker(Context context) {
        super(context);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs,context);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,context);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs,context);
    }

    private void init(AttributeSet attrs, Context context){
        TypedArray tp = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NumberPicker, 0, 0);
        try{
            backGroundColor = tp.getColor(R.styleable.NumberPicker_backgroundColor, Color.WHITE);
            numberOfPins = tp.getInteger(R.styleable.NumberPicker_numberOfPins, 15);
            icon = tp.getDrawable(R.styleable.NumberPicker_icon);
            iconTint = tp.getColor(R.styleable.NumberPicker_iconTint, Color.RED);
            gap = tp.getDimension(R.styleable.NumberPicker_gap, 15f);
            rowCount = tp.getInt(R.styleable.NumberPicker_android_rowCount, 4);
            textSize = tp.getFloat(R.styleable.NumberPicker_android_textSize, 40f);
            textColor = tp.getColor(R.styleable.NumberPicker_android_textColor, Color.WHITE);
            selectedColor = tp.getColor(R.styleable.NumberPicker_selectedColor, Color.argb(255,0,159,96));
            unselectedColor = tp.getColor(R.styleable.NumberPicker_unselectedColor, Color.GRAY);
        }finally {
            tp.recycle();
        }

        if(icon == null){
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_check_circle_24);
        }

        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        dimens.x = View.MeasureSpec.getSize(widthMeasureSpec);
        dimens.y = View.MeasureSpec.getSize(heightMeasureSpec);
        dimens.set(800,800);
        setMeasuredDimension(dimens.x, dimens.y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dimens.set(w,h);
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int eventType = event.getAction() & event.getActionMasked();
        if(eventType == MotionEvent.ACTION_DOWN){

        }else if(eventType == MotionEvent.ACTION_UP){

        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(backGroundColor);

        float dy = (1f*dimens.y-(rowCount+1f)*gap)/rowCount;
        int columnCount = (numberOfPins + rowCount - 1)/rowCount;
        float dx = (1f*dimens.x-(columnCount+1f)*gap)/columnCount;
        d.set(dx,dy);
        int count = 0, countY = 0;
        textPaint.setColor(unselectedColor);
        for(float iy = gap ;countY < rowCount;iy += gap){
            float bottom = iy + dy;
            int countX = 0;
            for(float ix = gap;countX < columnCount && count < numberOfPins;ix += gap){
                float right = ix + dx;
                canvas.drawRect(ix,iy,right,bottom,textPaint);
                ix = right;
                count++;
                countX++;
            }
            iy = bottom;
            countY++;
        }
    }
}
