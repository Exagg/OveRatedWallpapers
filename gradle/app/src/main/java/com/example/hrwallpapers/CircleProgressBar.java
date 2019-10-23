package com.example.hrwallpapers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * A subclass of {@link android.view.View} class for creating a custom circular progressBar
 *
 * Created by Pedram on 2015-01-06.
 */
public class CircleProgressBar extends View {

    private static final String TAG = "CircleProgressBar";
    private onProgressBarLoaded loadedEvent;

    public void setOnLoaded(onProgressBarLoaded listener)
    {
        this.loadedEvent = listener;
        isLoaded = true;
    }

    private float strokeWidth = 1;
    private float progress = 0;
    private int min = 0;
    private int max = 100;
    private int responsibleViewID = 0;
    /**
     * Start the progress at 12 o'clock
     */
    private int startAngle = -90;
    private int color = Color.DKGRAY;
    private int percentageColor;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private View responsibleView;
    public boolean isLoaded = false;
    public boolean isLoading = false;

    private int visibilityType;  //<!-- 1 = Set visibility is visible when percentage is 1f | 0 = set visibility is hide when percentage is 1f-->

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        backgroundPaint.setStrokeWidth(strokeWidth);
        foregroundPaint.setStrokeWidth(strokeWidth);
        invalidate();
        requestLayout();//Because it should recalculate its bounds
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if(progress >= (float)100)
        {
            this.isLoaded = true;
            this.setVisibility(GONE);
            if (loadedEvent != null)
                loadedEvent.progressBarLoaded(this);
        }
        else
        {
            this.isLoaded = false;
            this.isLoading = false;
            if (progress > 0) this.isLoading = true;
        }

        postInvalidate();
        invalidate();
        requestLayout();

    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        foregroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleProgressBar,
                0, 0);
        //Reading values from the XML layout
        try {
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progressValue, progress);
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color);
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min);
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max);
            percentageColor = typedArray.getInt(R.styleable.CircleProgressBar_progressbarPercentageColor, max);
            responsibleViewID = typedArray.getResourceId(R.styleable.CircleProgressBar_progressBarResponsibleObject,0);
            visibilityType = typedArray.getInt(R.styleable.CircleProgressBar_progressBarResponsibleObjectVisibilityType,1);

        } finally {
            typedArray.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(percentageColor);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(rectF, backgroundPaint);
        float angle = 360 * progress / max;
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2);
    }

    @Override
    protected void onAttachedToWindow() {

        if(responsibleViewID !=0 && responsibleView == null)
        {
            responsibleView = ((ViewGroup)this.getParent()).findViewById(responsibleViewID);
            responsibleView.setVisibility(INVISIBLE);
        }
        super.onAttachedToWindow();
    }

    /**
     * Lighten the given color by the factor
     *
     * @param color  The color to lighten
     * @param factor 0 to 4
     * @return A brighter color
     */
    public int lightenColor(int color, float factor) {
        float r = Color.red(color) * factor;
        float g = Color.green(color) * factor;
        float b = Color.blue(color) * factor;
        int ir = Math.min(255, (int) r);
        int ig = Math.min(255, (int) g);
        int ib = Math.min(255, (int) b);
        int ia = Color.alpha(color);
        return (Color.argb(ia, ir, ig, ib));
    }

    /**
     * Transparent the given color by the factor
     * The more the factor closer to zero the more the color gets transparent
     *
     * @param color  The color to transparent
     * @param factor 1.0f to 0.0f
     * @return int - A transplanted color
     */
    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Set the progress with an animation.
     * Note that the {@link android.animation.ObjectAnimator} Class automatically set the progress
     * so don't call the {@link CircleProgressBar#setProgress(float)} directly within this method.
     *
     * @param progress The progress it should animate to it.
     */
    public void setProgressWithAnimation(float progress) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(500);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    public interface onProgressBarLoaded
    {
        public void progressBarLoaded(View view);
    }
}
