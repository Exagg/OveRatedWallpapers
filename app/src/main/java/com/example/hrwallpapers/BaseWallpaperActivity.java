package com.example.hrwallpapers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Layout;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseWallpaperActivity extends AppCompatActivity {

    private int ANIMATION_DURATION = 250;

    private static final String TAG ="BaseWallpaperTAG";
    private static final int VIEW_PAGER_LOAD_LIMIT = 5;
    private static BaseWallpaperActivity baseWallpaper;


    private ActionBar actionBar;
    private View bottomArea;
    private View rightArea;
    private View leftArea;
    private View mainView;
    public ViewPager viewPager;

    private boolean mVisible;

    private ImageView leftIcon;
    private ImageView rightIcon;
    public wallpaperModel model;

    public List<wallpaperModel>wallpaperModelList;
    public MenuModel menuModel;

    public BaseWallpaperPagerAdapter adapter;

    private RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .fitCenter();

    private Fragment popupFragment;

    private SlidingUpPanelLayout mSlidingPanel;
    private FlexboxLayout tagsContainer;
    private TextView resolutionTextView;
    private RecyclerView popupRecyclerView;
    private FrameLayout fragmentHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_base_wallpaper);
        baseWallpaper = this;
        popupFragment = setFragment(new wallpaperPopupFragment(),R.id.base_wallpaper_fragment_holder);
        if(this.getIntent().getExtras().containsKey("wallpaperList"))
        {
            Type listType = new TypeToken<List<wallpaperModel>>(){}.getType();
            String listData = getIntent().getStringExtra("wallpaperList");

            wallpaperModelList = new Gson().fromJson(listData,listType);
        }
        if(this.getIntent().getExtras().containsKey("listIndex"))
        {
            int index = getIntent().getIntExtra("listIndex",1);
            if(index != -1) model = wallpaperModelList.get(index);
        }
        if(this.getIntent().getExtras().containsKey("menuModel"))
        {
            String menuData = getIntent().getStringExtra("menuModel");
            menuModel = new Gson().fromJson(menuData, MenuModel.class);
        }


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
        mSlidingPanel = findViewById(R.id.base_wallpaper_slidingpanel);
        tagsContainer = findViewById(R.id.base_wallpaper_tags_container);
        resolutionTextView = findViewById(R.id.base_wallpaper_resolution_textview);



        mSlidingPanel.setAnchorPoint(0.7f); // it will up to %70 of screen size

        //Set fontawesome icons to imageviews

        MainActivity.setIconToImageView(leftIcon,this,R.string.fa_chevron_left_solid,true,false,100,getResources().getColor(R.color.white));
        MainActivity.setIconToImageView(rightIcon,this,R.string.fa_chevron_right_solid,true,false,100,getResources().getColor(R.color.white));


        adapter = new BaseWallpaperPagerAdapter(this,wallpaperModelList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(wallpaperModelList.indexOf(model));


        popupRecyclerView = findViewById(R.id.base_Wallpaper_similiar_recyclerView);
        fragmentHolder = findViewById(R.id.base_wallpaper_fragment_holder);

        popupRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });


        GridLayoutManager layoutManager = new GridLayoutManager(this.getApplicationContext(),2);
        popupRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) popupRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        final GestureDetector tapDetector = new GestureDetector(this, new GestureListener());


        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tapDetector.onTouchEvent(event);
                return false;
            }
        });


        if(wallpaperModelList != null && model != null)
        {
            if(wallpaperModelList.size() - wallpaperModelList.indexOf(model) < VIEW_PAGER_LOAD_LIMIT)
            {

                if(MainActivity.task.getStatus() == AsyncTask.Status.FINISHED)
                {
                    menuModel.queryModel.setActivePage(menuModel.queryModel.getActivePage() + 1);
                    menuModel.queryModel.prepareUrl();
                    MainActivity.setMenuClickListener(menuModel,baseWallpaper,MainActivity.VIEWPAGER_LOAD_MORE);
                }
            }
        }

        leftArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = viewPager.getCurrentItem();
                index--;

                if(index > -1 && index <= adapter.getCount())
                {
                    viewPager.setCurrentItem(index);
                }
            }
        });

        rightArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = viewPager.getCurrentItem();
                index++;


                if(index > -1 && index <= adapter.getCount())
                {
                    viewPager.setCurrentItem(index);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                Log.i(TAG, "onPageSelected: " + i);

                if(wallpaperModelList.size() - i < VIEW_PAGER_LOAD_LIMIT)
                {
                    if(MainActivity.task.getStatus() == AsyncTask.Status.FINISHED)
                    {
                        menuModel.queryModel.setActivePage(menuModel.queryModel.getActivePage() + 1);
                        menuModel.queryModel.prepareUrl();
                        MainActivity.setMenuClickListener(menuModel,baseWallpaper,MainActivity.VIEWPAGER_LOAD_MORE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        if(mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
        {
            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
        {
            int currentIndex =viewPager.getCurrentItem();
            String listData = new Gson().toJson(wallpaperModelList);
            Intent intent = new Intent();


            intent.putExtra("wallpaperList",listData);
            intent.putExtra("listIndex",currentIndex);
            setResult(RESULT_OK,intent);
            finish();
            super.onBackPressed();
        }
    }

    protected Fragment setFragment(Fragment fragment,int layoutID) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(layoutID, fragment);
        fragmentTransaction.commit();
        return fragment;
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
            onBackPressed();
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

    public void onSwipeDown()
    {
        if(mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
        {
            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }
    public void onSwipeUp()
    {

        if (mSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
        {
            final wallpaperModel activeModel = wallpaperModelList.get(viewPager.getCurrentItem());

            if(activeModel.tagList.size() == 0)
            {
                final Context context = getApplicationContext();
                final Activity activity = this;
                tagsContainer.removeAllViews();
                resolutionTextView.setText("");
                HttpGetTagsAsync task = new HttpGetTagsAsync();
                task.setTaskFisinhed(new HttpGetTagsAsync.onAsyncTaskFisinhed() {
                    @Override
                    public void taskFinished(wallpaperModel model) {
                        if(model != null)
                        {
                            activeModel.tagList = model.tagList;

                            for (String s :
                                    activeModel.tagList)
                            {
                                if(s != "")
                                {
                                    LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View newTag = layoutInflater.inflate(R.layout.tag_textviews,null);
                                    TextView tag = (TextView) newTag.findViewById(R.id.tag_view);
                                    tag.setText(s);
                                    tagsContainer.addView(newTag);
                                    final queryModel queryModel = activeModel.getTagQueryModel(activeModel.tagList.indexOf(s));
                                    if(queryModel != null) // Tags Click listener.
                                    {
                                        newTag.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                MenuModel menu = menuModel;
                                                menu.queryModel = queryModel;

                                                if(menu.queryModel != null && (MainActivity.task == null || MainActivity.task.getStatus() == AsyncTask.Status.FINISHED))
                                                {
                                                    MainActivity.task = new HttpGetImagesAsync();

                                                    String url = menu.queryModel.getUrl();
                                                    Log.i(TAG, "getImagesOnHttp: " + url);

                                                    Object[] container = new Object[] {url};

                                                    ((HttpGetImagesAsync) MainActivity.task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                                                        @Override
                                                        public void taskFinished(List<wallpaperModel> list) {
                                                            MainActivity.ma.showFullScreenActivity(activeModel,activity,BaseWallpaperActivity.class,list);
                                                        }
                                                    });
                                                    MainActivity.task.execute(container);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                            resolutionTextView.setText(model.resolution); //resolution textview content

                            if(activeModel.tagList.size() > 0)
                            {
                                List<wallpaperModel> similiarList =new ArrayList<>();
                                final wallpaperRecyclerViewAdapter similiarAdapter = new wallpaperRecyclerViewAdapter(similiarList,fragmentHolder,popupFragment,popupRecyclerView);

                                //Load smiliar images
                                for (String s :
                                        activeModel.tagList) {
                                    HttpGetImagesAsync task = new HttpGetImagesAsync();
                                    queryModel queryModel = activeModel.getTagQueryModel(activeModel.tagList.indexOf(s));
                                    MenuModel menu = menuModel;
                                    menu.queryModel = queryModel;

                                    if(menu.queryModel != null && task != null)
                                    {


                                        String url = menu.queryModel.getUrl();
                                        Log.i(TAG, "loading similiar: " + url);

                                        Object[] container = new Object[] {url};

                                        ((HttpGetImagesAsync) task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                                            @Override
                                            public void taskFinished(List<wallpaperModel> list) {
                                                similiarAdapter.addModelListToList(list);
                                                similiarAdapter.notifyDataSetChanged();
                                            }
                                        });
                                        task.execute(container);
                                    }
                                }
                                popupRecyclerView.setAdapter(similiarAdapter);
                            }
                        }
                        Log.i(TAG, "taskFinished: " + model.thumbSrc);
                    }
                });
                task.execute(activeModel);
            }

            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }
    }

    private void loadTags(final MenuModel menuModel, final Activity activity,final wallpaperModel model)
    {

    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 300;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            toogleUI();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            //onSwipeRight();
                        } else {
                            //onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeDown();
                        } else {
                            onSwipeUp();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }


}