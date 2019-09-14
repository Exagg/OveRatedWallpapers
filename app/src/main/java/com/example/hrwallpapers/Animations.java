package com.example.hrwallpapers;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Animations {

    private static int ANIMATION_DURATION = 300;

    public static final int TOGGLE_HIDE = 1;
    public static final int TOGGLE_SHOW = 0;


    public static void slideUp(final View view, final int direction,final int state) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int height = view.getHeight() + params.bottomMargin + params.topMargin;
        view.setClickable(true);
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                height * direction,  // fromYDelta
                0);                // toYDelta

        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (state == TOGGLE_SHOW) view.setClickable(true);
                else if(state == TOGGLE_HIDE) view.setClickable(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void slideDown(final View view, int direction, final int state) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int height = view.getHeight() + params.bottomMargin + params.topMargin;
        final TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                height * direction); // toYDelta
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (state == TOGGLE_HIDE)view.setClickable(false); //Animasyon ile çerçeve dışına çıktıntna sonra click eventini tetiklemeye devam etmemesi için
                else if(state == TOGGLE_SHOW) view.setClickable(true);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);

    }

    public static void slideRight(final View view, int direction, final int state) {
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth() * direction,
                0,
                0,
                0
        );

        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (state == TOGGLE_SHOW) view.setClickable(true);
                else if(state == TOGGLE_HIDE)view.setClickable(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void slideLeft(final View view, int direction, final int state) {
        TranslateAnimation animate = new TranslateAnimation(
                0,
                view.getWidth() * direction,
                0,
                0
        );
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (state == TOGGLE_SHOW) view.setClickable(true);
                else if (state == TOGGLE_HIDE) view.setClickable(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

}

class CustomRotateAnimation
{
    private View view;
    private int ANIMATE_DURATION;
    private ObjectAnimator animator;
    private boolean colorAnimationIsEnabled;

    private CustomColorAnimation colorAnimation;

    public CustomRotateAnimation (View view, int ANIMATE_DURATION, @Nullable boolean colorAnimationIsEnabled, @Nullable int firstColor, @Nullable int secondColor,@Nullable boolean isTintColor )
    {
        this.view = view;
        this.ANIMATE_DURATION = ANIMATE_DURATION;
        this.colorAnimationIsEnabled = colorAnimationIsEnabled;

        if (this.colorAnimationIsEnabled && (firstColor == 0 || secondColor == 0))
        {
            throw new NullPointerException("if color animation came as true, then you send first and second color values..Please check your declares");
        }


        if (this.colorAnimationIsEnabled)
        {
            this.colorAnimation = new CustomColorAnimation(this.view,this.ANIMATE_DURATION,firstColor,secondColor,isTintColor);
        }

    }

    public void rotateTo(float degree,boolean isChecked)
    {
        if (animator != null && animator.isRunning()) animator.pause();
        animator = ObjectAnimator.ofFloat(view,View.ROTATION,view.getRotation(),degree).setDuration(ANIMATE_DURATION);
        animator.start();

        if (this.colorAnimationIsEnabled)
        {
            if (isChecked) this.colorAnimation.animateToSecondColor();
            else this.colorAnimation.animateToFirstColor();
        }
    }
}
class CustomColorAnimation implements ValueAnimator.AnimatorUpdateListener
{
    private int lastAnimatedValue = 0;
    private View view;
    private int ANIMATE_DURATION;
    private ValueAnimator valueAnimator;
    private int firstColor;
    private int secondColor;
    private boolean isTintColor;

    public CustomColorAnimation(@NonNull View view,@NonNull int ANIMATE_DURATION, @NonNull int firstColor,@NonNull int secondColor,@NonNull boolean isTintColor)
    {
        this.ANIMATE_DURATION = ANIMATE_DURATION;
        this.view = view;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.isTintColor = isTintColor;

        this.valueAnimator = new ValueAnimator();
        this.valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setDuration(this.ANIMATE_DURATION);
        valueAnimator.addUpdateListener(this);
    }

    public void animateToSecondColor()
    {
        if (valueAnimator.isRunning()) valueAnimator.pause();
        if (lastAnimatedValue != 0) this.firstColor = lastAnimatedValue;
        valueAnimator.setIntValues(lastAnimatedValue,secondColor);
        valueAnimator.start();
    }
    public void animateToFirstColor()
    {
        if (valueAnimator.isRunning()) valueAnimator.pause();
        if (lastAnimatedValue != 0) this.secondColor = lastAnimatedValue;

        valueAnimator.setIntValues(this.secondColor,this.firstColor);
        valueAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (view != null)
        {
            if (this.isTintColor)
            {
                this.view.setBackgroundTintList(ColorStateList.valueOf((Integer)animation.getAnimatedValue()));
            }
            else
            {
                this.view.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        }
    }
}