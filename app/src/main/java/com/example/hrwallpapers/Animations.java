package com.example.hrwallpapers;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import static android.content.ContentValues.TAG;

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