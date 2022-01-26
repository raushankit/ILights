package com.raushankit.ILghts.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.raushankit.ILghts.R;

public class ProfilePic extends View {
    private String name;
    private float radius = 0;
    private final Point dimens = new Point();
    private int backGroundColor;
    private int strokeColor;
    private boolean adjustText = false;
    private float strokeWidth;
    private float gap;
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ProfilePic(Context context) {
        super(context);
    }

    public void setName(String name) {
        this.name = name;
        invalidate();
    }

    public ProfilePic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public ProfilePic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        TypedArray tp = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ProfilePic,0,0);
        int textColor;
        float textSize;
        try{
            backGroundColor = tp.getColor(R.styleable.ProfilePic_backgroundColor, Color.RED);
            strokeColor = tp.getColor(R.styleable.ProfilePic_strokeColor, Color.GREEN);
            strokeWidth = tp.getDimension(R.styleable.ProfilePic_strokeWidth, 10);
            gap = tp.getDimension(R.styleable.ProfilePic_stroke_padding, strokeWidth);
            textColor = tp.getColor(R.styleable.ProfilePic_android_textColor, Color.BLACK);
            name = tp.getString(R.styleable.ProfilePic_android_text);
            textSize = tp.getDimension(R.styleable.ProfilePic_android_textSize, -1);
            adjustText = textSize==-1;
        }finally {
            tp.recycle();
        }
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setAntiAlias(true);

        textPaint.setTextSize(adjustText?30:textSize);
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        dimens.set(width, width);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dimens.set(w, w);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(0.5f*dimens.x, 0.5f*dimens.y, 0.5f*dimens.x-strokeWidth, strokePaint);
        drawBackGround(canvas);
        if(!TextUtils.isEmpty(name)) drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        String text;
        String [] words = name.split(" ",-1);
        text = words[0].toUpperCase().charAt(0) +
                (words.length > 1?words[words.length-1].toUpperCase().substring(0,1):"");
        if(adjustText)adjustTextSize(text);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(),bounds);
        canvas.drawText(text, dimens.x*.5f,dimens.y*0.5f+bounds.height()*0.5f,textPaint);
    }

    public void setBackGroundCol(int backGroundColor) {
        this.backGroundColor = backGroundColor;
        invalidate();
    }

    private void adjustTextSize(String text) {
        float textSize = textPaint.getTextSize();
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(),bounds);
        textPaint.setTextSize(0.95f*textSize*1.414f*radius/bounds.height());
        if(bounds.width() > 1.414f*radius){
            textPaint.setTextSize(0.95f*textSize*1.414f*radius/bounds.width());
        }
    }

    private void drawBackGround(Canvas canvas) {
        strokePaint.setStyle(Paint.Style.FILL);
        strokePaint.setColor(backGroundColor);
        radius = 0.5f*dimens.x-strokeWidth-gap;
        canvas.drawCircle(0.5f*dimens.x, 0.5f*dimens.y, radius, strokePaint);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
    }
}
