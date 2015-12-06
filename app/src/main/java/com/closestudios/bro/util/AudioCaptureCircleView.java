package com.closestudios.bro.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.closestudios.bro.R;

/**
 * Created by closestudios on 12/5/15.
 */
public class AudioCaptureCircleView extends View {

    float percentFilled;
    boolean active;
    boolean init = true;
    int offsetPixels;

    Paint paint;
    RectF oval;

    public AudioCaptureCircleView(Context context) {
        super(context);
    }

    public AudioCaptureCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioCaptureCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setActive(boolean active) {
        this.active = active;
        invalidate();
    }

    public void setPercentFilled(float percentFilled) {
        this.percentFilled = percentFilled;
        invalidate();
    }

    public void initView(Canvas canvas) {
        init = false;

        offsetPixels = (int)pxFromDp(getContext(), 8);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);


    }

    public Paint getOuterPaint() {
        paint.setColor(active ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.red_orange));
        return paint;
    }

    public Paint getOuterPaintLine() {
        paint.setColor(getResources().getColor(R.color.red));
        return paint;
    }

    public Paint getInnerPaint() {
        paint.setColor(active ? getResources().getColor(R.color.light_blue) : getResources().getColor(R.color.dark_green));
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(init) {
            initView(canvas);
        }

        oval = new RectF(0,0, canvas.getWidth(), canvas.getHeight());

        // Draw outer circle
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2, getOuterPaint());

        // Draw Arc
        canvas.drawArc(oval, 0, 360 * percentFilled, true, getOuterPaintLine());

        // Draw inner
        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/2 - offsetPixels, getInnerPaint());

    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


}
