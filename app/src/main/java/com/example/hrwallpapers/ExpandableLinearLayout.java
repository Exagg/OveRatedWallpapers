package com.example.hrwallpapers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.icu.util.Measure;
import android.media.Image;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;


public class ExpandableLinearLayout extends LinearLayout {
    public static int COLLAPSED = 0;
    public static int EXPANDED = 1;
    public static int ANIMATING = 2;
    private final int ANIMATION_DURATION = 300;
    private final static int TOGGLE_HEIGHT = 25;
    private final static int TOGGLE_WIDHH = 25;
    public static final int DIRECTION_UP =  1 ;
    public static final int DIRECTION_DOWN = 3;

    private static final String TAG = "Expandable Layout";

    private View staticView;
    private View dynamicView;
    private View toggleView;

    private int maxHeight;



    private int STATE = EXPANDED;


    private int toggleColor;
    private int toggleWidth;
    private int toggleHeight;
    private Drawable toggleDrawable;
    private boolean toggleAnimatable;
    private int toggleDirection;
    private int toggleDefaultState;
    private int toggleViewID;


    public ExpandableLinearLayout(Context context)
    {
        super(context);
    }

    public ExpandableLinearLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context,attrs);
    }


    public ExpandableLinearLayout(Context context,AttributeSet attrs,int defStyle)
    {
        super(context,attrs,defStyle);
        initialize(context,attrs);
    }

    private void initialize(Context context,AttributeSet attrs)
    {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExpandableLinearLayout,
                0, 0);

        try {
            toggleHeight = typedArray.getDimensionPixelSize(R.styleable.ExpandableLinearLayout_toggleHeight,TOGGLE_HEIGHT);
            toggleWidth = typedArray.getDimensionPixelSize(R.styleable.ExpandableLinearLayout_toggleWidth,TOGGLE_WIDHH);
            toggleAnimatable = typedArray.getBoolean(R.styleable.ExpandableLinearLayout_toggleAnimatable,true);
            int drawableID = typedArray.getResourceId(R.styleable.ExpandableLinearLayout_toggleDrawable,R.drawable.ic_hot);
            toggleDrawable = ContextCompat.getDrawable(context,drawableID);
            toggleColor = typedArray.getResourceId(R.styleable.ExpandableLinearLayout_toggleColor,R.color.white);
            toggleDirection = typedArray.getInteger(R.styleable.ExpandableLinearLayout_toggleDirection,-1);
            toggleDefaultState = typedArray.getInteger(R.styleable.ExpandableLinearLayout_toggleDefaultState,0);
            toggleViewID = typedArray.getResourceId(R.styleable.ExpandableLinearLayout_toggleView,0);


        }
        finally {
            typedArray.recycle();
        }





    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        try {
            if((staticView == null || dynamicView == null) && toggleDirection == DIRECTION_DOWN) {
                staticView = getChildAt(0);
                dynamicView = getChildAt(1);
                maxHeight = dynamicView.getMeasuredHeight();
            }
            else if((staticView == null || dynamicView == null) && toggleDirection == DIRECTION_UP)
            {
                staticView = getChildAt(1);
                dynamicView = getChildAt(0);
                maxHeight = dynamicView.getMeasuredHeight();
            }
        }
        catch (Exception ex)
        {

        }


        if(staticView != null)
        {
            staticView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    View parent = (View) staticView.getParent();
                    if(parent.isClickable())
                    {
                        if(getSTATE() == COLLAPSED)
                        {
                            setSTATE(EXPANDED);
                        }
                        else if(getSTATE() == EXPANDED)
                        {
                            setSTATE(COLLAPSED);
                        }
                    }
                }
            });


            setSTATE(toggleDefaultState);
        }



        if(toggleViewID != 0)
        {
            View root = this;
            toggleView = root.findViewById(toggleViewID);
        }
        else toggleView = null;
    }

    public void setSTATE(int STATE) {
        this.STATE = STATE;
        if(dynamicView != null)
        {
            if(this.STATE == COLLAPSED)
            {
                maxHeight = dynamicView.getMeasuredHeight();
                collapseArea(dynamicView);

            }
            else if(this.STATE == EXPANDED)
            {
                expandArea(dynamicView);
            }
        }
    }

    public int getSTATE() {
        return STATE;
    }


    ValueAnimator animator;
    ObjectAnimator animation;
    private void collapseArea(final View view){
        if(toggleDirection == DIRECTION_DOWN)
        {
            animator = ValueAnimator.ofInt(view.getMeasuredHeight(), 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = val;
                    view.setLayoutParams(layoutParams);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(GONE);
                    view.requestLayout();
                    view.invalidate();
                    super.onAnimationEnd(animation);
                }
            });
            animator.setDuration(ANIMATION_DURATION);
            animator.start();
        }
        else if(toggleDirection == DIRECTION_UP)
        {
            final int actualHeight = view.getMeasuredHeight();
            animator = ValueAnimator.ofInt(view.getMeasuredHeight(),0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int height = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = height;
                    if(layoutParams instanceof FrameLayout.LayoutParams)
                    {
                        ((FrameLayout.LayoutParams) layoutParams).setMargins(0,actualHeight - height,0,0);
                    }
                    view.setLayoutParams(layoutParams);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(GONE);
                    view.requestLayout();
                    view.invalidate();
                    super.onAnimationEnd(animation);
                }
            });
            animator.setDuration(ANIMATION_DURATION);
            animator.start();
        }


        if(toggleAnimatable && toggleView != null)
        {
            animation = ObjectAnimator.ofFloat(toggleView,View.ROTATION,toggleView.getRotation(),90f).setDuration(ANIMATION_DURATION);
            animation.start();
        }

    }
    private void expandArea(final View view){
        if(animator != null)
        {
            animator.end();
        }
        if(animation != null)
        {
            animation.end();
        }
        view.setVisibility(VISIBLE);
        view.requestLayout();
        view.invalidate();

        view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if(animation != null && animation.isRunning())
        {
            animation.end();
        }
        if(toggleView != null && toggleAnimatable)
        {
            toggleView.setRotation(0f);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            if((staticView == null || dynamicView == null) && toggleDirection == DIRECTION_DOWN) {
                staticView = getChildAt(0);
                dynamicView = getChildAt(1);
                maxHeight = dynamicView.getMeasuredHeight();
            }
            else if((staticView == null || dynamicView == null) && toggleDirection == DIRECTION_UP)
            {
                staticView = getChildAt(1);
                dynamicView = getChildAt(0);
                maxHeight = dynamicView.getMeasuredHeight();
            }
        }
        catch (Exception ex)
        {

        }
        super.onLayout(changed, l, t, r, b);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
