package com.example.hrwallpapers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.gesture.Gesture;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.motion.MotionScene.TAG;

public class BaseWallpaperActivity extends AppCompatActivity {

    private int ANIMATION_DURATION = 250;

    private static final String TAG ="BaseWallpaperTAG";
    private ActionBar actionBar;
    private View bottomArea;
    private View rightArea;
    private View leftArea;
    private View mainView;
    private ViewPager viewPager;

    private boolean mVisible;

    private ImageView leftIcon;
    private ImageView rightIcon;
    public wallpaperModel model;

    public List<wallpaperModel>wallpaperModelList;
    public MenuModel menuModel;

    private RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .fitCenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_base_wallpaper);


        model= MainActivity.selectedWallpaper;
        wallpaperModelList = MainActivity.activeModelList;
        menuModel = MainActivity.activeMenu;

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.fullScreenActionBarBackground)));
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }


        mVisible = false;
        bottomArea = findViewById(R.id.base_wallpaper_bottom_area);
        leftArea = findViewById(R.id.base_wallpaper_left_area);
        rightArea = findViewById(R.id.base_wallpaper_right_area);
        leftIcon = findViewById(R.id.base_wallpaper_left_icon);
        rightIcon = findViewById(R.id.base_wallpaper_right_icon);

        viewPager =findViewById(R.id.base_wallpaper_viewPager);

        mainView = findViewById(R.id.base_wallpaper_container);


        //Set fontawesome icons to imageviews

        MainActivity.setIconToImageView(leftIcon,this,R.string.fa_chevron_left_solid,true,false,100,getResources().getColor(R.color.white));
        MainActivity.setIconToImageView(rightIcon,this,R.string.fa_chevron_right_solid,true,false,100,getResources().getColor(R.color.white));



        viewPager.setAdapter(new BaseWallpaperPagerAdapter(this,wallpaperModelList));
        viewPager.setCurrentItem(wallpaperModelList.indexOf(model));

        final GestureDetector tapDetector = new GestureDetector(this, new TapGestureListener());


        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tapDetector.onTouchEvent(event);
                return false;
            }
        });


        leftArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    public void setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.RIGHT);
            slide.setDuration(200);
            slide.setInterpolator(new AccelerateDecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }
    }


    private void toogleUI()
    {
        if(mVisible)
        {
            slideDown(bottomArea);
            slideLeft(leftArea,-1);
            slideLeft(rightArea,1);
            actionBar.hide();
        }
        else {
            slideUp(bottomArea);
            slideRight(leftArea, -1);
            slideRight(rightArea, 1);
            actionBar.show();
        }
        mVisible = mVisible ? false : true;
    }
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideRight(View view,int direction)
    {
        TranslateAnimation animate = new TranslateAnimation(
                view.getWidth() * direction,
                0,
                0,
                0
        );
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public void slideLeft(View view,int direction)
    {
        TranslateAnimation animate = new TranslateAnimation(
                0,
                view.getWidth()* direction,
                0,
                0
        );
        animate.setDuration(ANIMATION_DURATION);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        toogleUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            toogleUI();
            return false;
        }


    }

}