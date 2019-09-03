package com.example.hrwallpapers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseWallpaperActivity extends AppCompatActivity {

    private int ANIMATION_DURATION = 250;
    private int onPausedPosition = 0;

    private static final String TAG ="BaseWallpaperTAG";
    private static final int VIEW_PAGER_LOAD_LIMIT = 5;
    private static BaseWallpaperActivity baseWallpaper;


    private View customActionBar;
    private View rightArea;
    private View leftArea;
    private View mainView;
    private View progressingArea;
    public ViewPager viewPager;
    private Activity thisActivity;

    private boolean mVisible;


    private ImageView leftIcon;
    private ImageView rightIcon;
    private ImageView shareImageView;
    private ImageView likeImageView;
    private ImageView backImageView;
    private ImageView wizardImageView;
    private ImageView setAsImageView;
    private ImageView downloadImageView;
    private ImageView slideUpButton;
    public wallpaperModel model;


    public List<wallpaperModel>wallpaperModelList;
    public queryModel queryModel;

    public BaseWallpaperPagerAdapter adapter;

    private RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(true)
            .priority(Priority.HIGH)
            .fitCenter();


    private wallpaperPopupFragment popupFragment;

    private SlidingUpPanelLayout mSlidingPanel;
    private FlexboxLayout tagsContainer;
    private TextView resolutionTextView;
    private RecyclerView popupRecyclerView;
    private FrameLayout fragmentHolder;
    public wallpaperRecyclerViewAdapter similiarAdapter;
    private ExpandableLinearLayout expandableArea;
    private CircleProgressBar progressingAreaCircleBar;

    private HttpGetTagsAsync getTagsAsync = new HttpGetTagsAsync();
    private HttpGetImagesAsync getSimiliarAsync = new HttpGetImagesAsync();
    private final Context baseWallpaperContext = this;
    private static HttpGetImagesAsync task = new HttpGetImagesAsync();
    private GestureDetector tapDetector;




    BottomDownloadDialog downloadDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.activity_base_wallpaper);
        baseWallpaper = this;
        popupFragment = (wallpaperPopupFragment) setFragment(new wallpaperPopupFragment(),R.id.base_wallpaper_fragment_holder);
        thisActivity = this;
        if(this.getIntent().getExtras().containsKey("wallpaperList"))
        {
            Type listType = new TypeToken<List<wallpaperModel>>(){}.getType();
            String listData = getIntent().getStringExtra("wallpaperList");

            wallpaperModelList = new Gson().fromJson(listData,listType);
        }
        if(this.getIntent().getExtras().containsKey("listIndex"))
        {
            int index = getIntent().getIntExtra("listIndex",0);
            if(index == -1) index = 0;
            model = wallpaperModelList.get(index);
            if (MainActivity.wallpaperInFavorites.contains(model.id)) model.isFavorite.setValue(true);



        }
        if(this.getIntent().getExtras().containsKey("queryData"))
        {
            String menuData = getIntent().getStringExtra("queryData");
            queryModel = new Gson().fromJson(menuData, queryModel.class);
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
        leftArea = findViewById(R.id.base_wallpaper_left_area);
        rightArea = findViewById(R.id.base_wallpaper_right_area);
        leftIcon = findViewById(R.id.base_wallpaper_left_icon);
        rightIcon = findViewById(R.id.base_wallpaper_right_icon);
        viewPager =findViewById(R.id.base_wallpaper_viewPager);
        mainView = findViewById(R.id.base_wallpaper_container);
        mSlidingPanel = findViewById(R.id.base_wallpaper_slidingpanel);
        tagsContainer = findViewById(R.id.base_wallpaper_tags_container);
        resolutionTextView = findViewById(R.id.base_wallpaper_resolution_textview);
        shareImageView = findViewById(R.id.fullscreen_share_button);
        expandableArea = findViewById(R.id.base_wallpaper_tags_expandable);
        customActionBar = findViewById(R.id.base_wallpaper_custom_actionbar);
        likeImageView = findViewById(R.id.base_wallpaper_like);
        backImageView = findViewById(R.id.base_wallpaper_back_button);
        downloadImageView =findViewById(R.id.fullscreen_download_button);
        wizardImageView =findViewById(R.id.base_wallpaper_wizard_button);
        setAsImageView = findViewById(R.id.base_wallpaper_setas);
        progressingArea = findViewById(R.id.base_wallpaper_progressing_area);
        progressingAreaCircleBar = findViewById(R.id.base_wallpaper_progressing_area_circle);
        downloadDialog = new BottomDownloadDialog(this.getContentResolver());
        slideUpButton = findViewById(R.id.base_wallpaper_slide_up);

        mSlidingPanel.setAnchorPoint(0.7f); // it will up to %70 of screen size


        if(model.isFavorite.isTrue())MainActivity.changeImageViewAsLiked(likeImageView);
        else MainActivity.changeImageViewAsUnliked(likeImageView);


        adapter = new BaseWallpaperPagerAdapter(this,wallpaperModelList,viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(wallpaperModelList.indexOf(model));
        viewPager.setOffscreenPageLimit(2);
        loadTags(model);

        popupRecyclerView = findViewById(R.id.base_Wallpaper_similiar_recyclerView);
        fragmentHolder = findViewById(R.id.base_wallpaper_fragment_holder);

        List<wallpaperModel> similiarList =new ArrayList<>();
        similiarAdapter = new wallpaperRecyclerViewAdapter(similiarList,fragmentHolder,popupFragment,mainView,this,queryModel,popupRecyclerView);
        popupRecyclerView.setAdapter(similiarAdapter);

        tapDetector = new GestureDetector(this, new GestureListener());


        if (MainActivity.wallpaperInFavorites.contains(model.id))model.isFavorite.setValue(true);
        else model.isFavorite.setValue(false);
        //Eğer liked olarak geldiyse likeimageview ona göre şekillenecek.
        if(model.isFavorite.isTrue()) MainActivity.changeImageViewAsLiked(likeImageView);
        else MainActivity.changeImageViewAsUnliked(likeImageView); // Eğer bir önceki wallpaper liked durumda ise yeni görüntü unliked olarak açılacak.


        popupRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        popupRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int calculatedYPos = 0;
            private int actualHeight = 0;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                calculatedYPos = recyclerView.computeVerticalScrollOffset();
                actualHeight = recyclerView.computeVerticalScrollRange();



                if(actualHeight - (recyclerView.computeVerticalScrollExtent() + calculatedYPos) < MainActivity.LOAD_MORE_SCROLL_RANGE)
                {
                    Log.i("Scroll", "onScrolled: " + MainActivity.LOAD_MORE_SCROLL_RANGE + " < " + (actualHeight - (recyclerView.computeVerticalScrollExtent() + calculatedYPos)));
                    wallpaperModel activeModel = wallpaperModelList.get(viewPager.getCurrentItem());
                    //Load smiliar images


                    if(activeModel.tagList.size() > 0 && mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
                    {
                        if((task == null || task.getStatus() != AsyncTask.Status.RUNNING))
                        {
                            task = new HttpGetImagesAsync();

                            Object[] container = new Object[] {activeModel.tagList,activeModel};


                            ((HttpGetImagesAsync)  task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                                @Override
                                public void taskFinished(List<wallpaperModel> list) {
                                }

                                @Override
                                public void onOneTagLoaded(List<wallpaperModel> list) {
                                    similiarAdapter.addModelListToList(list);
                                }
                            });
                            task.execute(container);
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy);

            }

        });

        mSlidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            float offset = 0;
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                offset = slideOffset;
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                {
                    expandableArea.setSTATE(expandableArea.COLLAPSED); // CLOSE TAGS AREA
                }
                else if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                {
                    if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    {
                        cleanSimiliars();
                        mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);// Fix the bug to the fixed state as ANCHORED
                    }
                }
                else if(newState == SlidingUpPanelLayout.PanelState.ANCHORED && offset < 0.2f)
                {
                    mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this.getApplicationContext(),2);
        layoutManager.setRecycleChildrenOnDetach(true);
        popupRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) popupRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);



        if(wallpaperModelList != null && model != null)
        {
            if(wallpaperModelList.size() - wallpaperModelList.indexOf(model) < VIEW_PAGER_LOAD_LIMIT)
            {

                if(task.getStatus() == AsyncTask.Status.FINISHED && queryModel != null)
                {
                    queryModel.setActivePage(queryModel.getActivePage() + 1);
                    queryModel.prepareUrl();
                    MainActivity.setMenuClickListenerForViewPager(queryModel,viewPager,adapter,task);
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


        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                View view = adapter.getLastActiveView();
                if(view != null)
                {
                    ImageView im = view.findViewById(R.id.base_wallpaper_main_image);
                    if(im !=null)
                    {
                        BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
                        if (drawable != null)
                        {
                            if (MainActivity.isFileExists(model.HQFileName))
                            {
                                downloadDialog.share(MainActivity.findExistFie(model.HQFileName),BaseWallpaperActivity.this);
                            }
                            else
                            {
                                downloadDialog.setActiveBitmap(drawable.getBitmap());
                                downloadDialog.setActiveModel(model);
                                downloadDialog.setDialogType(BottomDownloadDialog.BottomDownloadDialogType.SHARE);
                                downloadDialog.show(getSupportFragmentManager(),"Share");
                            }
                        }
                        else
                        {
                            Toast.makeText(thisActivity, "Please wait for the load", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });



        downloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                View view = adapter.getLastActiveView();
                if(view != null)
                {
                    ImageView im = view.findViewById(R.id.base_wallpaper_main_image);
                    if(im !=null)
                    {
                        BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
                        if (drawable != null)
                        {
                            if (!MainActivity.isFileExists(model.HQFileName) || !MainActivity.isFileExists(model.LQFileName))
                            {
                                downloadDialog.setActiveBitmap(drawable.getBitmap());
                                downloadDialog.setActiveModel(model);
                                downloadDialog.setDialogType(BottomDownloadDialog.BottomDownloadDialogType.DOWNLOAD);
                                downloadDialog.show(getSupportFragmentManager(),"Share");
                            }
                            else MainActivity.showToast("This wallpaper is already downloaded..", Toast.LENGTH_SHORT,BaseWallpaperActivity.this);
                        }
                        else
                        {
                            Toast.makeText(thisActivity, "Please wait for the load", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        setAsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                View view = adapter.getLastActiveView();

                if(view != null)
                {
                    ImageView im = view.findViewById(R.id.base_wallpaper_main_image);
                    if(im !=null)
                    {
                        BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
                        if (drawable != null)
                        {
                            downloadDialog.setDialogType(BottomDownloadDialog.BottomDownloadDialogType.SETASWALLPAPER);
                            if (MainActivity.isFileExists(model.HQFileName))
                            {
                                // Dont call show function when this image is loaded as hqfilename
                                downloadDialog.setAs(MainActivity.findExistFie(model.HQFileName),BaseWallpaperActivity.this);
                            }
                            else {
                                //Call show function include exists LQ file. Cause user must want download as HQ
                                downloadDialog.setActiveBitmap(drawable.getBitmap());
                                downloadDialog.setActiveModel(model);
                                downloadDialog.show(getSupportFragmentManager(),"Set As Wallpaper");
                            }

                        }
                        else
                        {
                            Toast.makeText(thisActivity, "Please wait for the load", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                wallpaperModel selectedModel = wallpaperModelList.get(viewPager.getCurrentItem());
                if(model != selectedModel)
                {
                    model = selectedModel;
                    model.tagsCurrentPage = 0;
                    resolutionTextView.setText("");
                    similiarAdapter.clearModels();
                    tagsContainer.removeAllViews();
                    getSimiliarAsync.cancel(true);
                    getTagsAsync.cancel(true);
                    loadTags(selectedModel);
                }

                if(wallpaperModelList.size() - i < VIEW_PAGER_LOAD_LIMIT)
                {
                    if(task == null) task = new HttpGetImagesAsync();
                    if(task.getStatus() != AsyncTask.Status.RUNNING && queryModel != null)
                    {
                       queryModel.setActivePage(queryModel.getActivePage() + 1);
                       MainActivity.setMenuClickListenerForViewPager(queryModel,viewPager,adapter,task);
                    }
                }

                if (MainActivity.wallpaperInFavorites.contains(model.id)) model.isFavorite.setValue(true);
                //Eğer liked olarak geldiyse likeimageview ona göre şekillenecek.
                if(model.isFavorite.isTrue()) MainActivity.changeImageViewAsLiked(likeImageView);
                else MainActivity.changeImageViewAsUnliked(likeImageView); // Eğer bir önceki wallpaper liked durumda ise yeni görüntü unliked olarak açılacak.
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
                MainActivity.likeWallpaper(model,likeImageView);
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        slideUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeUp();
            }
        });

        Log.i(TAG, "onCreate:  on create event is triggered");


    }

    private void cleanSimiliars()
    {
        similiarAdapter.modelList.clear();
        similiarAdapter.notifyDataSetChanged();
        wallpaperModel activeModel = this.wallpaperModelList.get(viewPager.getCurrentItem());
        activeModel.tagsCurrentPage = 0;
    }

    @Override
    protected void onPause() {

        this.onPausedPosition = viewPager.getCurrentItem();
        if(adapter != null) viewPager.setAdapter(null);
        if(similiarAdapter != null) popupRecyclerView.setAdapter(null);
        Log.i(TAG, "onPause: " + popupRecyclerView.getAdapter());

        super.onPause();
    }

    @Override
    protected void onResume() {
        if(adapter != null && viewPager.getAdapter() == null) viewPager.setAdapter(adapter);
        Log.i(TAG, "onResume: " + popupRecyclerView.getAdapter());
        if(similiarAdapter != null && popupRecyclerView.getAdapter() == null)
        {
            popupRecyclerView.setAdapter(similiarAdapter);
        }

        Log.i(TAG, "onResume: " + popupRecyclerView.getAdapter());

        if(onPausedPosition != 0)viewPager.setCurrentItem(this.onPausedPosition);
        super.onResume();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        popupRecyclerView.setAdapter(similiarAdapter);
    }


    private wallpaperModel getModelOnViewPager()
    {
        if(this.wallpaperModelList.size() >= this.viewPager.getCurrentItem())
        {
            return this.wallpaperModelList.get(viewPager.getCurrentItem());
        }
        else  return null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected Fragment setFragment(Fragment fragment, int layoutID) {
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
            //Animations.slideDown(wizardImageView,1,Animations.TOGGLE_HIDE);
            Animations.slideDown(downloadImageView,1,Animations.TOGGLE_HIDE);
            Animations.slideDown(shareImageView,1,Animations.TOGGLE_HIDE);
            Animations.slideLeft(leftArea,-1,Animations.TOGGLE_HIDE);
            Animations.slideLeft(rightArea,1,Animations.TOGGLE_HIDE);
            Animations.slideDown(customActionBar,-1,Animations.TOGGLE_HIDE);
        }
        else {

            //Animations.slideUp(wizardImageView,1,Animations.TOGGLE_SHOW);
            Animations.slideUp(downloadImageView,1,Animations.TOGGLE_SHOW);
            Animations.slideUp(shareImageView,1,Animations.TOGGLE_SHOW);
            Animations.slideRight(leftArea, -1,Animations.TOGGLE_SHOW);
            Animations.slideRight(rightArea, 1,Animations.TOGGLE_SHOW);
            Animations.slideUp(customActionBar,-1,Animations.TOGGLE_SHOW);
        }
        mVisible = mVisible ? false : true;
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

    public void onSwipeDown() {
        if(mSlidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)
        {
            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            wallpaperModel model = wallpaperModelList.get(viewPager.getCurrentItem());
            model.tagsCurrentPage = 0;
            getSimiliarAsync.cancel(true);
        }
    }

    public void onSwipeUp() {
        if (mSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            final wallpaperModel activeModel = wallpaperModelList.get(viewPager.getCurrentItem());
            if(activeModel.resolution != null)
            {
                resolutionTextView.setText(activeModel.resolution); //resolution textview content*/
            }
            mSlidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            loadSimiliars(activeModel);
        }
    }

    private void loadTagViews(String s,final wallpaperModel activeModel) {
        LayoutInflater layoutInflater = (LayoutInflater) baseWallpaperContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newTag = layoutInflater.inflate(R.layout.tag_textviews, null);
        TextView tag = newTag.findViewById(R.id.tag_view);
        tag.setText(s);
        tagsContainer.addView(newTag);
        final queryModel tagQueryModel = activeModel.getTagQueryModel(activeModel.tagList.indexOf(s));
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: " + tagQueryModel);
                if (tagQueryModel != null && (task == null || task.getStatus() != AsyncTask.Status.RUNNING)) {


                    tagQueryModel.setActivePage(1);
                    task = new HttpGetImagesAsync();

                    String url = tagQueryModel.getUrl();
                    Log.i(TAG, "getImagesOnHttp: " + url);

                    Object[] container = new Object[]{url};

                    ((HttpGetImagesAsync) task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                        @Override
                        public void taskFinished(List<wallpaperModel> list) {
                            MainActivity.ma.showFullScreenActivity(activeModel, baseWallpaperContext, BaseWallpaperActivity.class, list, tagQueryModel);
                        }

                        @Override
                        public void onOneTagLoaded(List<wallpaperModel> list) {

                        }
                    });
                    task.execute(container);
                }
            }
        });
    }

    private void loadTags(final wallpaperModel mModel)
    {

        if(mModel.tagList.size() == 0)
        {
            if(getTagsAsync.getStatus() == AsyncTask.Status.RUNNING || getTagsAsync.getStatus() == AsyncTask.Status.PENDING)
            {
                getTagsAsync.cancel(true);
            }
            resolutionTextView.setText("");
            getTagsAsync = new HttpGetTagsAsync();
            getTagsAsync.setTaskFisinhed(new HttpGetTagsAsync.onAsyncTaskFisinhed() {
                @Override
                public void taskFinished(wallpaperModel model) {
                    mModel.tagList = model.tagList;

                    if(mModel.id == model.id)
                    {
                        tagsContainer.removeAllViews();
                        for (String s :
                                mModel.tagList) {
                            if (s != "") {
                                loadTagViews(s,mModel);
                            }
                        }
                    }

                    if(mSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mSlidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
                    {
                        loadSimiliars(model);
                    }
                }
            });
            getTagsAsync.execute(mModel);
        }
        else
        {
            Log.i(TAG, "loadTags: model has tags");
        }
    }

    private void loadSimiliars(final wallpaperModel mModel)
    {
        if (mModel.tagList.size() != 0) {

            if(mModel.tagList.size() == 0)
            {
                tagsContainer.removeAllViews();
            }

            if (mModel != null) {
                if (mModel.tagList.size() > 0 ) {
                    if(popupRecyclerView.getAdapter() == null) popupRecyclerView.setAdapter(similiarAdapter);
                    expandableArea.setSTATE(ExpandableLinearLayout.EXPANDED);
                    if(getSimiliarAsync.getStatus() != AsyncTask.Status.PENDING || getSimiliarAsync.getStatus() == AsyncTask.Status.RUNNING)
                    {
                        getSimiliarAsync.cancel(true);
                    }
                    if ((getSimiliarAsync != null || getSimiliarAsync.getStatus() != AsyncTask.Status.RUNNING)) {
                        getSimiliarAsync = new HttpGetImagesAsync();

                        (getSimiliarAsync).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                            @Override
                            public void taskFinished(List<wallpaperModel> list) {
                            }

                            @Override
                            public void onOneTagLoaded(List<wallpaperModel> list) {
                                int firstIndex = similiarAdapter.getItemCount() - 1;
                                similiarAdapter.addModelListToList(list);
                                similiarAdapter.notifyItemRangeChanged(firstIndex,list.size());
                            }
                        });
                        getSimiliarAsync.execute(mModel.similiarsUrl);
                    }
                }
            }
        }
    }


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
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
                            result =true;
                        } else {
                            onSwipeUp();
                            result =true;
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }


    }
    public class BaseWallpaperPagerAdapter extends PagerAdapter {

        private static final String TAG = "BaseWallpaperPagerAdap";
        private Context mContext;
        private List<wallpaperModel> wallpaperList;
        private ViewPager viewPager;
        private View lastActiveView;
        public BaseWallpaperPagerAdapter(Context context, List<wallpaperModel> wallpaperList, ViewPager viewPager)
        {
            this.wallpaperList = wallpaperList;
            this.mContext = context;
            this.viewPager = viewPager;
        }

        @Override
        public void destroyItem(@NonNull final ViewGroup container, final int position, @NonNull final Object object) {
            final ImageView im = container.findViewById(R.id.base_wallpaper_main_image);
            final CircleProgressBar circleProgressBar = container.findViewById(R.id.base_wallpaper_circleProgressBar);

            ProgressAppGlideModule.forget(getWallpaperModel(position).originalSrc);
            Glide.with(mContext).clear(im); // OOM handler it must be in the detached!! dont delete
            circleProgressBar.setProgress(0);
            container.removeView((View)object);

            Log.i(TAG, "destroyItem: " + position);

        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View currentView = (View) object;
            ImageView im = currentView.findViewById(R.id.base_wallpaper_main_image);
            CircleProgressBar circleProgressBar = currentView.findViewById(R.id.base_wallpaper_circleProgressBar);

            lastActiveView = currentView;

            if(im.getDrawable() == null && !circleProgressBar.isLoading)
            {
                wallpaperModel model = getWallpaperModel(position);

                circleProgressBar.setProgress(1);

                RequestOptions requestOptions = new RequestOptions();
                if (position == 0) requestOptions.priority(Priority.HIGH);
                else requestOptions.priority(Priority.NORMAL);
                MainActivity.loadImageAsHQ(im,circleProgressBar,requestOptions,model);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            wallpaperModel model = (wallpaperModel)wallpaperList.get(position);
            return super.getPageTitle(position);
        }

        public wallpaperModel getWallpaperModel(int position)
        {
            wallpaperModel model = (wallpaperModel)wallpaperList.get(position);
            return model;
        }

        @Override
        public int getCount() {
            return wallpaperList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }


        public View getLastActiveView()
        {
            return this.lastActiveView;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.i(TAG, "instantiateItem: " + position);
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View layout = layoutInflater.inflate(R.layout.base_wallpaper_viewpager,container,false);
            final PhotoView im = layout.findViewById(R.id.base_wallpaper_main_image);

            final CircleProgressBar circleProgressBar = layout.findViewById(R.id.base_wallpaper_circleProgressBar);

            final wallpaperModel model = getWallpaperModel(position);



            if(model != null && im.getDrawable() == null && !circleProgressBar.isLoading)
            {
                circleProgressBar.setProgress(0);

                RequestOptions requestOptions = new RequestOptions();
                if (position == 0) requestOptions.priority(Priority.HIGH);
                else requestOptions.priority(Priority.NORMAL);
                MainActivity.loadImageAsHQ(im,circleProgressBar,requestOptions,model);
            }
            container.addView(layout);
            return layout;
        }

        public void addListToList(List<wallpaperModel> models)
        {
            this.wallpaperList.addAll(models);
        }
    }

}