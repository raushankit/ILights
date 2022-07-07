package com.raushankit.ILghts.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.raushankit.ILghts.R;

public class FormFlowLine extends View {

    private final Point dimens = new Point();
    private final Paint strokePaint = new Paint();
    private final Paint textPaint = new Paint();

    private int backGroundColor;
    private float gap;
    private int numOfSteps;
    private float thickness;
    private float circleRadius;
    private Drawable icon = null;
    private int textColor;
    private float textSizePercentage;
    private int inactiveColor;
    private int activeColor;

    private int activeIndex = 0;

    public FormFlowLine(Context context) {
        super(context);
    }

    public FormFlowLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public FormFlowLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    public FormFlowLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context){
        TypedArray tp = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FormFlowLine, 0, 0);
        try{
            backGroundColor = tp.getColor(R.styleable.FormFlowLine_backgroundColor, Color.WHITE);
            numOfSteps = tp.getInteger(R.styleable.FormFlowLine_number_of_steps, 4);
            activeIndex = tp.getInteger(R.styleable.FormFlowLine_active_index, numOfSteps/2);
            thickness = tp.getDimension(R.styleable.FormFlowLine_strokeWidth, 15);
            icon = tp.getDrawable(R.styleable.FormFlowLine_icon);
            circleRadius = tp.getDimension(R.styleable.FormFlowLine_pointer_radius, 0);
            gap = tp.getDimension(R.styleable.FormFlowLine_gap, 0);
            textSizePercentage = tp.getFloat(R.styleable.FormFlowLine_text_size_percentage, 0.60f);
            textColor = tp.getColor(R.styleable.FormFlowLine_android_textColor, Color.WHITE);
            inactiveColor = tp.getColor(R.styleable.FormFlowLine_inactive_color, Color.DKGRAY);
            activeColor = tp.getColor(R.styleable.FormFlowLine_active_color, Color.GREEN);
        }finally {
            tp.recycle();
        }

        if(icon == null){
            icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_check_24);
        }

        textSizePercentage = Math.max(0.01f,Math.min(1f,textSizePercentage));
        strokePaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        dimens.x = View.MeasureSpec.getSize(widthMeasureSpec);
        dimens.y = View.MeasureSpec.getSize(heightMeasureSpec);

        if(circleRadius == 0){
            circleRadius = dimens.y/4f;
        }
        gap = Math.max(gap, circleRadius);
        if(thickness == 0){
            thickness = dimens.y/15f;
        }
        setMeasuredDimension(dimens.x, dimens.y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dimens.set(w,h);
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        activeIndex = Math.max(1,Math.min(numOfSteps,activeIndex));
        canvas.drawColor(backGroundColor);
        strokePaint.setStrokeWidth(thickness);
        drawStroke(canvas);
    }

    private void drawStroke(Canvas canvas) {
        float y = dimens.y/2f;
        float xp = (dimens.x*1f - 2*gap)/(numOfSteps-1);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(inactiveColor);
        canvas.drawLine(gap + (activeIndex-1)*xp, y,dimens.x-gap,y,strokePaint);
        strokePaint.setColor(activeColor);
        canvas.drawLine(gap, y,gap + (activeIndex-1)*xp,y,strokePaint);

        int count = 0;
        float centerX = gap;
        strokePaint.setStyle(Paint.Style.FILL);
        for(;count < activeIndex;++count,centerX+=xp){
            canvas.drawCircle(centerX,y,circleRadius,strokePaint);
        }
        strokePaint.setColor(inactiveColor);
        for(;count < numOfSteps;++count,centerX+=xp){
            canvas.drawCircle(centerX,y,circleRadius,strokePaint);
        }
        centerX = gap;
        for(int i = 1;i < numOfSteps;++i,centerX+=xp){
            String text = String.valueOf(i);
            adjustTextSize(text);
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            canvas.drawText(text, centerX, y + bounds.height() * 0.5f, textPaint);
        }
        icon.setBounds((int) (centerX-textSizePercentage*circleRadius), (int) (y-textSizePercentage*circleRadius),(int) (centerX+textSizePercentage*circleRadius), (int) (y+textSizePercentage*circleRadius));
        icon.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
        icon.draw(canvas);
    }

    public void setActiveIndex(int index){
        this.activeIndex = index;
    }

    private void adjustTextSize(String text) {
        float textSize = textPaint.getTextSize();
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        textPaint.setTextSize(textSizePercentage * textSize * 1.414f * circleRadius / bounds.height());
        if (bounds.width() > 1.414f * circleRadius) {
            textPaint.setTextSize(textSizePercentage * textSize * 1.414f * circleRadius / bounds.width());
        }
    }
}
