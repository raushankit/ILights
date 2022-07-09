package com.raushankit.ILghts.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.raushankit.ILghts.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumberPicker extends View {

    private static final String TAG = "NumberPicker";
    private final Point dimens = new Point();
    private final Paint textPaint = new Paint();
    private final PointF d = new PointF();
    private List<RectF> squares = new ArrayList<>();
    private List<Boolean> isActive = null;
    private int currentSquare = -1;
    private NumberPicker.onSquareClickedListener listener = null;
    private boolean isTouchValid = false;
    private boolean disallowTouch = false;
    private int textColor;
    private float textSizePercentage;
    private int backGroundColor;
    private float gap;
    private int rowCount;
    private int columnCount;
    private int selectedColor;
    private int unselectedColor;
    private int numberOfPins;

    public NumberPicker(Context context) {
        super(context);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        TypedArray tp = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NumberPicker, 0, 0);
        try{
            backGroundColor = tp.getColor(R.styleable.NumberPicker_backgroundColor, Color.TRANSPARENT);
            numberOfPins = tp.getInteger(R.styleable.NumberPicker_numberOfPins, 15);
            gap = tp.getDimension(R.styleable.NumberPicker_gap, 15f);
            columnCount = tp.getInt(R.styleable.NumberPicker_android_columnCount, (int) Math.sqrt(numberOfPins*1.0));
            textSizePercentage = tp.getFloat(R.styleable.NumberPicker_text_size_percentage, 0.9f);
            textSizePercentage = Math.min(1f, Math.max(0.01f, textSizePercentage));
            textColor = tp.getColor(R.styleable.NumberPicker_android_textColor, Color.WHITE);
            selectedColor = tp.getColor(R.styleable.NumberPicker_selectedColor, Color.argb(255,0,159,96));
            unselectedColor = tp.getColor(R.styleable.NumberPicker_unselectedColor, Color.GRAY);
        }finally {
            tp.recycle();
        }
        if(isActive == null){
            isActive = new ArrayList<>();
            for(int i = 0;i < numberOfPins;++i){ isActive.add(Boolean.FALSE); }
        }
        rowCount = (numberOfPins + columnCount - 1)/columnCount;
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        NumberSavedState numberSavedState = new NumberSavedState(superState);
        numberSavedState.savedState = isActive;
        return numberSavedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof NumberSavedState)){
            return;
        }
        NumberSavedState numberSavedState = (NumberSavedState) state;
        super.onRestoreInstanceState(numberSavedState.getSuperState());
        this.isActive = numberSavedState.savedState;
    }

    private static class NumberSavedState extends BaseSavedState {

        List<Boolean> savedState = new ArrayList<>();

        NumberSavedState(Parcelable superState) {
            super(superState);
        }
        private NumberSavedState(Parcel in){
            super(in);
            in.readList(this.savedState, List.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(this.savedState);
        }

        public static final Parcelable.Creator<NumberSavedState> CREATOR = new Creator<NumberSavedState>() {
            @Override
            public NumberSavedState createFromParcel(Parcel parcel) {
                return new NumberSavedState(parcel);
            }

            @Override
            public NumberSavedState[] newArray(int i) {
                return new NumberSavedState[i];
            }
        };
    }

    @SuppressLint("SwitchIntDef")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        dimens.x = MeasureSpec.getSize(widthMeasureSpec);
        dimens.y = getDesiredHeight(dimens.x*1.0);
        setMeasuredDimension(dimens.x, dimens.y);
    }

    private int getDesiredHeight(double width){
        double dx = (width-gap)/columnCount;
        return (int)Math.ceil(gap + dx*rowCount);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dimens.set(w,h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        calculate_borders();
    }

    private void calculate_borders(){
        float dy = (1f*dimens.y-(rowCount+1f)*gap)/rowCount;
        float dx = (1f*dimens.x-(columnCount+1f)*gap)/columnCount;
        d.set(dx,dy);
        adjustTextSize(numberOfPins);
        int count = 0, countY = 0;
        for(float iy = gap ;countY < rowCount;iy += gap){
            float bottom = iy + dy;
            int countX = 0;
            for(float ix = gap;countX < columnCount && count < numberOfPins;ix += gap){
                float right = ix + dx;
                squares.add(new RectF(ix,iy,right,bottom));
                ix = right;
                count++;
                countX++;
            }
            iy = bottom;
            countY++;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(disallowTouch){
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        float y = event.getY();
        int eventType = event.getAction() & event.getActionMasked();
        int which_square = check_which_square_clicked(x,y);
        if(eventType == MotionEvent.ACTION_DOWN){
            currentSquare = which_square;
            if(which_square != -1){
                if(listener != null) listener.whichSquareClicked(which_square);
                isTouchValid = true;
                isActive.set(which_square, isActive.get(which_square)?Boolean.FALSE:Boolean.TRUE);
                invalidate();
            }
        }else if(eventType == MotionEvent.ACTION_MOVE){
            if(listener != null) listener.whichSquareClicked(currentSquare);
            if((which_square != -1) && (which_square != currentSquare)){
                currentSquare = which_square;
                isActive.set(which_square, isActive.get(which_square)?Boolean.FALSE:Boolean.TRUE);
                invalidate();
            }
        }else{
            Log.d(TAG, "onTouchEvent: event of no importance");
            isTouchValid = false;
        }
        getParent().requestDisallowInterceptTouchEvent(isTouchValid);
        return true;
    }

    private int check_which_square_clicked(float x, float y){
        double dx = d.x + gap;
        double dy = d.y + gap;
        int nx = (int) Math.floor((x-gap)/dx);
        int ny = (int) Math.floor((y-gap)/dy);
        int n = ny*columnCount + nx;
        return n < numberOfPins && nx >= 0 && ny >= 0 && squares.get(n).contains(x,y) ? n:-1;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(backGroundColor);
        Rect textBounds = new Rect();
        for(int i = 0;i < numberOfPins;++i){
            textPaint.setColor((isActive != null && isActive.get(i))? selectedColor: unselectedColor);
            RectF sqr = squares.get(i);
            canvas.drawRect(sqr,textPaint);
            String text = String.valueOf(i+1);
            textPaint.setColor(textColor);
            textPaint.getTextBounds(text,0,text.length(), textBounds);
            canvas.drawText(text, sqr.centerX(), sqr.centerY() - textBounds.centerY(), textPaint);
        }
    }

    private void adjustTextSize(int num) {
        int temp = num;
        long limit = 1;
        while(temp > 0){
            limit *= 10;
            temp /= 10;
        }
        String text = String.valueOf(limit-1);
        float textSize = textPaint.getTextSize();
        float size = (float) Math.hypot(d.x, d.y)/2f;
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        textPaint.setTextSize(textSizePercentage * textSize * size / bounds.height());
        if (bounds.width() > size) {
            textPaint.setTextSize(textSizePercentage * textSize * size / bounds.width());
        }
    }

    public List<Boolean> getSelectedPins() {
        return isActive;
    }

    public void setSelectedPins(Boolean[] active){
        isActive.clear();
        isActive.addAll(Arrays.asList(active));
        invalidate();
    }

    public void turnOnPin(int pin){
        if(pin < 0 || pin >= numberOfPins){ return; }
        isActive.set(pin, Boolean.TRUE);
        invalidate();
    }

    public void setNumberOfPins(int numberOfPins, List<Boolean> isActive){
        if (numberOfPins <= 0)return;
        // Log.e(TAG, "setNumberOfPins: n = " + numberOfPins);
        this.numberOfPins = numberOfPins;
        rowCount = (numberOfPins + columnCount - 1)/columnCount;
        if(isActive == null) {
            this.isActive = new ArrayList<>();
            for(int i = 0;i < numberOfPins;++i){ this.isActive.add(Boolean.FALSE); }
        }else{
            this.isActive = isActive;
        }
        squares = new ArrayList<>();
        requestLayout();
        invalidate();
    }

    public void addOnSquareClickedListener(@NonNull onSquareClickedListener listener){
        this.listener = listener;
    }

    public void setDisallowTouch(boolean disallowTouch){
        this.disallowTouch = disallowTouch;
    }

    public interface onSquareClickedListener{
        void whichSquareClicked(int whichSquare);
    }
}
