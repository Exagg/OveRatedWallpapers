package com.slice.wallpapers;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slice.wallpapers.DataAccessLayer.AccesibilityService;
import com.slice.wallpapers.DataAccessLayer.SqliteConnection;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import info.androidhive.fontawesome.FontCache;
import info.androidhive.fontawesome.FontTextView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    public static final File downloadFolder = new File(Environment.getExternalStorageDirectory() + File.separator + MainActivity.DOWNLOAD_FILE_NAME);
    public static Vibrator vibrator;

    public static final int LOAD_TO_RECYCLERVIEW = 1;
    public static final int LOAD_TO_PAGEVIEWER = 2;

    public static final String DOWNLOAD_FILE_NAME = "Splice Wallpapers";
    public static String DOWNLOAD_FILE_PATH;

    public static final int LOAD_MORE_SCROLL_RANGE = 3000;
    public static final int FULLSCREEN_REQUEST_CODE = 1;

    private static final String TAG = "Mainactivity";

    public static List<String> wallpaperInFavorites = new ArrayList<>();


    public static View menuFragmentHolder; //This holder will use DrawerLayout menus
    public static View mainFragmentHolder; //This holder will use only MainFragment

    public static Toast toast;

    public static MainActivity ma;
    public static View mainContentView;
    ExpandableListAdapter menuAdapter;
    HashMap<MenuModel, List<MenuModel>> menuHashmap = new HashMap<>();
    List<MenuModel> menuHeaderList = new ArrayList<>();
    DrawerLayout drawer;
    ImageView searchButton;

    public static HistoryFragment historyFragment = new HistoryFragment();
    public static MainFragment mainFragment = new MainFragment();
    public static FavoritesFragment favoritesFragment = new FavoritesFragment();
    public static SearchFragment searchFragment = new SearchFragment();
    public static Fragment autoWallpaperFragment = new AutoWallpaperFragment();

    private Toolbar toolbar;
    private boolean mDrawerIsOpen =false;


    private AdView bottombannerAdview;
    private boolean permissionsAreGranted = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        permissionsAreGranted = MainActivity.checkPermissions(this,this,1);

        View splashIconContainer = this.findViewById(R.id.splash_icon_container);

        TextView serverStateInfo = this.findViewById(R.id.splash_server_state_info);
        TextView serverStateHeader = this.findViewById(R.id.splash_server_state_header);
        ImageView serverStateIcon = this.findViewById(R.id.splash_server_state_icon);

        TextView downloadStateInfo = this.findViewById(R.id.splash_download_state_info);
        ImageView downloadStateIcon = this.findViewById(R.id.splash_download_state_icon);
        TextView downloadStateHeader = this.findViewById(R.id.splash_download_state_header);


        View maintainceModeContainer = this.findViewById(R.id.splash_maintaince_mode_container);
        View stateContainer = this.findViewById(R.id.splash_state_container);

        final SplashScreenModel serverStateModel = new SplashScreenModel(serverStateHeader,serverStateIcon,serverStateInfo, SplashScreenModel.SplashScreenTypes.SERVER_STATE);
        final SplashScreenModel downloadStateModel = new SplashScreenModel(downloadStateHeader,downloadStateIcon,downloadStateInfo, SplashScreenModel.SplashScreenTypes.DOWNLOAD_STATE);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(AccesibilityService.getServiceState() != AccesibilityService.SERVICE_IS_ACCESIBLE || AccesibilityService.getDownloadServiceState() != AccesibilityService.DOWNLOAD_SERVICE_IS_ACCESIBLE) {

                    if (AccesibilityService.getServiceState() != AccesibilityService.SERVICE_IS_UNACCESIBLE && AccesibilityService.getDownloadServiceState() != AccesibilityService.DOWNLOAD_SERVICE_IS_UNACCESIBLE)
                    {
                        //test is running keep update ui
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                updateSplashUI(serverStateModel);
                                updateSplashUI(downloadStateModel);
                            }
                        });
                    }
                    else if (AccesibilityService.getServiceState() == AccesibilityService.SERVICE_IS_ACCESIBLE && AccesibilityService.getDownloadServiceState() == AccesibilityService.DOWNLOAD_SERVICE_IS_UNACCESIBLE)
                    {
                        // Download service is not available in this network

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.showToast("Your internet connection not support to download content. Please contact your network manager.",Toast.LENGTH_LONG,MainActivity.this);
                            }
                        });
                    }
                    else if (AccesibilityService.getServiceState() == AccesibilityService.SERVICE_IS_UNACCESIBLE && AccesibilityService.getDownloadServiceState() == AccesibilityService.DOWNLOAD_SERVICE_IS_UNACCESIBLE)
                    {
                        // Wallpaper service is not working, probably this web page is maintaince mode.
                        // Check it

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Animations.show(maintainceModeContainer);
                                stateContainer.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.cancel();

                            updateSplashUI(serverStateModel);
                            updateSplashUI(downloadStateModel);
                            loadMain();
                        }
                    });
                }
            }
        };

        // Animate splash icon


        splashIconContainer.post(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Animations.slideUp(splashIconContainer,-1,0,false,0);
                    }
                });
            }
        });

        Runnable callServiceRunnable = new Runnable() {
            @Override
            public void run() {
                timer.schedule(timerTask,100,100);
                if (permissionsAreGranted)AccesibilityService.run();

            }
        };


        new Handler().postDelayed(callServiceRunnable,300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!permissionsAreGranted) permissionsAreGranted = MainActivity.checkPermissions(this,this,1);
        if (permissionsAreGranted)AccesibilityService.run();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void loadMain()
    {
        setContentView(R.layout.activity_main);
        mainFragment.latestQueryModel.setActivePage(0);
        mainFragment.homeQueryModel.setActivePage(0);
        ma = this;
        wallpaperInFavorites = SqliteConnection.connection.getFavorites();

        startService(new Intent(this, SliceWallpaperBackgroundService.class));

        bottombannerAdview = this.findViewById(R.id.main_bottom_banner_ad);

        MobileAds.initialize(this,getResources().getString(R.string.app_ad_id));

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("BA1DA98E60920E7B67AEA47F7BA77E46").build();
        bottombannerAdview.loadAd(adRequest);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainContentView = this.findViewById(R.id.main_content);


        drawer = this.findViewById(R.id.drawer_layout);
        NavigationView navigationView = this.findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        mainFragmentHolder = this.findViewById(R.id.main_fragment_holder);
        menuFragmentHolder = this.findViewById(R.id.main_menu_fragment_holder);
        searchButton = this.findViewById(R.id.main_search_button);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DOWNLOAD_FILE_NAME);
        boolean created = false;
        if(!folder.exists())
        {
            created = folder.mkdirs();
        }
        else created = true;


        if(created)DOWNLOAD_FILE_PATH = folder.getAbsolutePath();
        else  DOWNLOAD_FILE_PATH = null;


        setFragment(mainFragment,mainFragmentHolder,getSupportFragmentManager());


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(searchFragment,menuFragmentHolder,MainActivity.this.getSupportFragmentManager());
                drawer.closeDrawers();
                mainFragmentHolder.setVisibility(View.GONE);
            }
        });

    }

    private void updateSplashUI(SplashScreenModel model)
    {
        if (model.getIcon().getVisibility() == View.INVISIBLE) Animations.show(model.getIcon());
        if (model.getDetailTextview().getVisibility() == View.INVISIBLE) Animations.show(model.getDetailTextview());
        if (model.getHeaderTextView().getVisibility() == View.INVISIBLE) Animations.show(model.getHeaderTextView());

        if (model.getSplashScreenTypes() == SplashScreenModel.SplashScreenTypes.SERVER_STATE)
        {
            switch (AccesibilityService.getServiceState())
            {
                case AccesibilityService.SERVICE_IS_PENDING :
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                    model.getDetailTextview().setText("Pending..");
                    if (model.getCustomRotateAnimation().getAnimator() != null)model.getCustomRotateAnimation().getAnimator().end();
                    model.getIcon().setRotation(0);

                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                    break;
                case AccesibilityService.SERVICE_IS_PROCESSING:
                    model.getDetailTextview().setText("Trying to connect..");
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_loading));

                    int actualRotation = (int) model.getIcon().getRotation();
                    actualRotation++;
                    model.getCustomRotateAnimation().rotateTo(actualRotation + 360,false);

                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
                    break;

                case AccesibilityService.SERVICE_IS_ACCESIBLE:
                    model.getDetailTextview().setText("Service is available");
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
                    if (model.getCustomRotateAnimation().getAnimator() != null)model.getCustomRotateAnimation().getAnimator().end();

                    model.getIcon().setRotation(0);
                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    break;

                case AccesibilityService.SERVICE_IS_UNACCESIBLE:
                    model.getDetailTextview().setText("Service is unavailable");
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel));
                    if (model.getCustomRotateAnimation().getAnimator() != null)model.getCustomRotateAnimation().getAnimator().end();

                    model.getIcon().setRotation(0);
                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    break;

            }
        }
        if (model.getSplashScreenTypes() == SplashScreenModel.SplashScreenTypes.DOWNLOAD_STATE)
        {
            switch (AccesibilityService.getDownloadServiceState())
            {
                case AccesibilityService.DOWNLOAD_SERVICE_IS_PENDING :
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                    model.getDetailTextview().setText("Pending..");
                    if (model.getCustomRotateAnimation().getAnimator() != null)model.getCustomRotateAnimation().getAnimator().end();

                    model.getIcon().setRotation(0);
                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
                    break;

                case AccesibilityService.DOWNLOAD_SERVICE_IS_PROCESSING:
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_loading));
                    model.getDetailTextview().setText("Trying to connect..");

                    int actualRotation = (int) model.getIcon().getRotation();
                    actualRotation++;
                    model.getCustomRotateAnimation().rotateTo(actualRotation + 360,false);
                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
                    break;

                case AccesibilityService.DOWNLOAD_SERVICE_IS_UNACCESIBLE:
                    model.getDetailTextview().setText("Service is unavailable");
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel));
                    if (model.getCustomRotateAnimation().getAnimator() != null)model.getCustomRotateAnimation().getAnimator().end();

                    model.getIcon().setRotation(0);
                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    break;

                case AccesibilityService.DOWNLOAD_SERVICE_IS_ACCESIBLE:
                    model.getDetailTextview().setText("Service is available");
                    model.getIcon().setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
                    if (model.getCustomRotateAnimation().getAnimator() != null)model.getCustomRotateAnimation().getAnimator().end();

                    model.getIcon().setRotation(0);
                    model.getIcon().setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START) && mDrawerIsOpen) {
            drawer.closeDrawer(GravityCompat.START);
            mDrawerIsOpen = false;
        }
        else if (historyFragment.isAdded())
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(historyFragment);
            transaction.commit();

            this.setTitle(R.string.app_name);
            mainFragmentHolder.setVisibility(View.VISIBLE);
        }
        else if (favoritesFragment.isAdded())
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(favoritesFragment);
            transaction.commit();

            this.setTitle(R.string.app_name);
            mainFragmentHolder.setVisibility(View.VISIBLE);
        }
        else if(searchFragment.isAdded())
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(searchFragment);
            transaction.commit();

            this.setTitle(R.string.app_name);
            mainFragmentHolder.setVisibility(View.VISIBLE);
        }
        else if(autoWallpaperFragment.isAdded())
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(autoWallpaperFragment);
            transaction.commit();

            this.setTitle(R.string.app_name);
            mainFragmentHolder.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mDrawerIsOpen= true;
        if (id == R.id.menu_favorites)
        {
            //Favoriye atılanlar getirilecek
            if (menuFragmentHolder != null)
            {
                onBackPressed();
                setFragment(favoritesFragment,menuFragmentHolder,MainActivity.this.getSupportFragmentManager());
                mainFragmentHolder.setVisibility(View.GONE);
            }
        }
        else if (id == R.id.menu_history)
        {
            //Daha once göz atılanlar getirilecek.

            if (menuFragmentHolder != null) {
                onBackPressed();

                setFragment(historyFragment, menuFragmentHolder, MainActivity.this.getSupportFragmentManager());
                mainFragmentHolder.setVisibility(View.GONE);
            }
        }
        else if (id == R.id.menu_home)
        {
            onBackPressed();
            if (mainFragmentHolder.getVisibility() != View.VISIBLE)
            {
                onBackPressed();
            }
        }
        else if(id == R.id.menu_auto_wallpaper)
        {
            if (menuFragmentHolder!= null)
            {
                onBackPressed();
                setFragment(autoWallpaperFragment,menuFragmentHolder,MainActivity.this.getSupportFragmentManager());
                mainFragmentHolder.setVisibility(View.GONE);
            }
        }
        return true;
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (toolbar != null)
            toolbar.setTitle(title);
    }


    public void showResultTab(queryModel queryModel)
    {
        if(MainActivity.mainFragment != null) {

            MainActivity.mainFragment.toggleResultTab(View.VISIBLE);
            MainActivity.mainFragment.viewPager.setCurrentItem(0);
            Fragment fragment = this.mainFragment.viewPagerAdapter.getFragment(0);
            if (fragment.getClass() == ResultFragment.class) {
                ResultFragment resultFragment = (ResultFragment) fragment;
                resultFragment.setActiveQueryModel(queryModel);
                resultFragment.load();
                if (MainFragment.tabLayout != null)
                {
                    MainFragment.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        }
    }

    public void showFullScreenActivity(wallpaperModel model,Context startContext,final Class<? extends Activity> targetActivity,List<wallpaperModel> modelList,queryModel queryModel)
    {
        Intent i = new Intent(startContext,targetActivity);
        String listData = new Gson().toJson(modelList); // List activity içerisinde yeniden build edilecek. View ve class idleri değişecek.
        String queryData = new Gson().toJson(queryModel);
        String modelData = new Gson().toJson(model);
        if(modelList !=null)i.putExtra("listIndex",modelList.indexOf(model)); // Modelin indexi viewpagerda görüntülenecek
        i.putExtra("wallpaperList",listData);
        i.putExtra("queryData",queryData);
        i.putExtra("wallpaperModel",modelData);


        if(Build.VERSION.SDK_INT > 20)
        {

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(i,FULLSCREEN_REQUEST_CODE,options.toBundle());
        }
        else {
            startActivityForResult(i,FULLSCREEN_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FULLSCREEN_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                if(data.getExtras().containsKey("wallpaperList") && data.getExtras().containsKey("listIndex"))
                {
                    Type listType = new TypeToken<List<wallpaperModel>>(){}.getType();
                    int index =data.getIntExtra("listIndex",-1) - 2;
                    String listData = data.getStringExtra("wallpaperList");
                    List<wallpaperModel> modelList = new Gson().fromJson(listData,listType);


                    /*Fragment activeFragment = viewPagerAdapter.getFragment(viewPager.getCurrentItem());
                    RecyclerView activeRecyclerview = null;
                    wallpaperRecyclerViewAdapter activeAdapter= null;

                    if(activeFragment.getClass() == ResultFragment.class)
                    {
                        ResultFragment fragment = (ResultFragment)activeFragment;
                        activeAdapter = fragment.recyclerViewAdapter;
                        activeRecyclerview = fragment.recyclerView;
                    }
                    else if(activeFragment.getClass() == HomeFragment.class)
                    {
                        HomeFragment fragment = (HomeFragment) activeFragment;
                        activeAdapter = fragment.recyclerViewAdapter;
                        activeRecyclerview = fragment.recyclerView;
                    }
                    else if(activeFragment.getClass() == PopularFragment.class)
                    {
                        PopularFragment fragment = (PopularFragment)activeFragment;
                        activeAdapter = fragment.recyclerViewAdapter;
                        activeRecyclerview = fragment.recyclerView;
                    }

                    if(activeAdapter != null && activeRecyclerview != null)
                    {
                        if(modelList.size() > activeAdapter.getItemCount())
                        {
                            modelList = modelList.subList(activeAdapter.getModelList().size(),modelList.size());

                            if(modelList.size() > 0)
                            {
                                activeAdapter.addModelListToList(modelList);
                            } // else no need to update when size is equal to activelist
                        }
                        activeRecyclerview.scrollToPosition(index + 1);

                    }*/
                }
            }
        }
    }

    public static Fragment setFragment(Fragment fragment,View fragmentHolder,FragmentManager fragmentManager) {
        if(fragmentHolder != null )
        {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(fragmentHolder.getId(), fragment);
            fragmentTransaction.commit();
        }
        return fragment;
    }

    public static void setIconToImageView(FontTextView fontTextView, Context context, int resource , boolean isSolid, boolean isBrand, int size, int color)
    {

        if (isBrand)
            fontTextView.setTypeface(FontCache.get(context, "fa-brands-400.ttf"));
        else if (isSolid)
            fontTextView.setTypeface(FontCache.get(context, "fa-solid-900.ttf"));
        else
            fontTextView.setTypeface(FontCache.get(context, "fa-regular-400.ttf"));

        fontTextView.setText(resource);
        fontTextView.setTextSize(size);
        fontTextView.setTextColor(color);
    }

    public static float setDpToPx(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float setPxToDP(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void setMenuClickListenerForViewPager(final queryModel queryModel, final ViewPager viewPager, final BaseWallpaperPagerAdapter adapter,HttpGetImagesAsync task)
    {
        getImagesOnHttp(queryModel,null,LOAD_TO_PAGEVIEWER,adapter,task);
    }
    public static void setMenuClickListenerForRecyclerView(final queryModel queryModel,final RecyclerView recyclerView,final wallpaperRecyclerViewAdapter adapter,HttpGetImagesAsync task)
    {
        getImagesOnHttp(queryModel,adapter,LOAD_TO_RECYCLERVIEW,null,task);
    }

    public static void setMenuClickListenerForRecyclerView(queryModel activeQueryModel, HttpGetImagesAsync task) {
        getImagesOnHttp(activeQueryModel,null,0,null, task);
    }


    public static void getImagesOnHttp(final queryModel queryModel, final wallpaperRecyclerViewAdapter recyclerViewAdapter,
                                       final int loadToWhere, final BaseWallpaperPagerAdapter pagerAdapter, HttpGetImagesAsync _task)
    {
        if(queryModel != null)
        {
            if(_task.getStatus() == AsyncTask.Status.FINISHED) _task = new HttpGetImagesAsync();
            /*if(activity.getClass() == MainActivity.class)
            {
                DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }*/
            if(_task.getStatus() != AsyncTask.Status.RUNNING)
            {
                String url = queryModel.getUrl();

                Object[] container = new Object[] {url};

                if (_task.getTaskFinished() == null)
                {
                    ((HttpGetImagesAsync) _task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                        @Override
                        public void taskFinished(List<wallpaperModel> list) {
                            if(loadToWhere == LOAD_TO_RECYCLERVIEW)
                            {
                                loadWallpaperToRecyclerView(list,recyclerViewAdapter);
                            }
                            else if (loadToWhere == LOAD_TO_PAGEVIEWER)
                            {
                                loadWallpaperToViewPager(list,pagerAdapter);
                            }
                        }

                        @Override
                        public void onOneTagLoaded(List<wallpaperModel> list) {

                        }
                    });
                }

                _task.execute(container);
            }
        }
    }

    public static void loadWallpaperToViewPager(List<wallpaperModel> models, BaseWallpaperPagerAdapter adapter)
    {
        if(models.size() > 0) {
            adapter.addListToList(models);
            adapter.notifyDataSetChanged();
        }
    }


    public static void loadWallpaperToRecyclerView(List<wallpaperModel> wallpaperModels,wallpaperRecyclerViewAdapter adapter)
    {
        if(wallpaperModels.size() > 0)
        {
            int _oldsize = adapter.getItemCount();
            adapter.addModelListToList(wallpaperModels);
            adapter.notifyItemRangeInserted(_oldsize,wallpaperModels.size());
        }
    }

    public static void loadImageAsHQ(ImageView wallpaperImage,CircleProgressBar circleProgressBar, RequestOptions requestOptions, wallpaperModel model) {

        circleProgressBar.setProgress(0);
        if(circleProgressBar.getProgress() < 100)
        {
            new GlideImageLoader(wallpaperImage,circleProgressBar).load(model.originalSrc,requestOptions,model);
        }
    }

    public static void loadImageAsLQ(ImageView wallpaperImage,CircleProgressBar circleProgressBar, RequestOptions requestOptions, wallpaperModel model) {

        circleProgressBar.setProgress(0);
        if(circleProgressBar.getProgress() < 100)
        {
            new GlideImageLoader(wallpaperImage,circleProgressBar).load(model.thumbSrc,requestOptions,model);
        }
    }

    public static void likeWallpaper(@NonNull wallpaperModel model,@NonNull ImageView toggleImage)
    {
        if(!model.isFavorite.isTrue())
        {
            model.isFavorite.setValue(true);
            wallpaperInFavorites.add(model.id);
            String pictureType = model.isPng ? ".png" : "jpg";
            SqliteConnection.connection.addFavorite(model.id, getCurrentDateTime(),pictureType);
            changeImageViewAsLiked(toggleImage);
        }
        else
        {
            model.isFavorite.setValue(false);
            wallpaperInFavorites.remove(model.id);
            SqliteConnection.connection.removeFavorite(model.id);
            changeImageViewAsUnliked(toggleImage);
        }

    }

    public static String getCurrentDateTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());
        return  currentDateandTime;
    }

    public static void changeImageViewAsLiked(@NonNull ImageView im)
    {
        im.setImageResource(R.drawable.ic_favorites_liked);
        ((Drawable) im.getDrawable()).setTint(MainActivity.ma.getResources().getColor(R.color.red));
    }

    public static void changeImageViewAsUnliked(@NonNull ImageView im)
    {
        im.setImageResource(R.drawable.ic_favorites_unlike);
        ((Drawable) im.getDrawable()).setTint(MainActivity.ma.getResources().getColor(R.color.white));

    }

    public static void showKeyboard(EditText mEtSearch, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard(EditText mEtSearch, Context context) {
        mEtSearch.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


    }

    public static void showToast(String message,int duration,Context context)
    {
        if (toast == null)
        {
            toast = new Toast(context);
        }

        toast.cancel();
        toast = toast.makeText(context,message,duration);
        toast.show();
    }


    public static boolean isFileExists(final String fileName)
    {
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals(fileName);
            }
        };
        File[] downloadedImages = MainActivity.downloadFolder.listFiles(fileFilter);

        if(downloadedImages != null)
        {
            if (downloadedImages.length > 0) return true;
        }
        return false;
    }

    public static File findExistFie(final String fileName)
    {
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals(fileName);
            }
        };
        File[] downloadedImages = MainActivity.downloadFolder.listFiles(fileFilter);

        if(downloadedImages != null)
        {
            if (downloadedImages.length > 0) return downloadedImages[0];
        }
        return null;
    }

    public static List<File> findAllExistFile()
    {
        List<File> list = new ArrayList<>();
        File[] allFiles = MainActivity.downloadFolder.listFiles();
        for (File file: allFiles
             ) {
            list.add(file);
            Log.i(TAG, "findAllExistFile: " + file.getPath());
        }
        return list;
    }

    public static List<wallpaperModel> findAllexistFileAsModel(){
        List<wallpaperModel> list = new ArrayList<>();
        File[] allFiles = MainActivity.downloadFolder.listFiles();
        for (File file: allFiles
             ) {

            String name = file.getName();
            String id = wallpaperModel.fileNameToID(name);

            wallpaperModel model = new wallpaperModel(id);
            list.add(model);
        }
        return list;
    }

    public static boolean checkPermissions(Context context,Activity activity,int PERM_REQUEST_CODE)
    {
        String[]  needPerms = new String[]
        {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.VIBRATE
        };

        boolean state = true;

        for (String perm:needPerms
             ) {

            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED)
            {
                Log.i(TAG, "checkPermissions: Perm:" + perm + " not granted.");
                ActivityCompat.requestPermissions(activity,needPerms,PERM_REQUEST_CODE);
                if(state) state = false;
                break;
            }
            else if(ContextCompat.checkSelfPermission(context,perm) == PackageManager.PERMISSION_DENIED)
            {
            }
        }
        return state;
    }

}
class mainViewPagerAdapter extends FragmentPagerAdapter
{
    private static String TAG = "FragmentPagerAdapter";
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<MenuModel> fragmentMenuList = new ArrayList<>();

    public mainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Fragment fragment = (Fragment) object;
        return fragmentList.indexOf(object);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


    public void AddFragment(Fragment fragment,MenuModel menuModel)
    {
        if (!fragment.isAdded())
        {
            fragmentList.add(fragment);
            fragmentMenuList.add(menuModel);
            notifyDataSetChanged();
        }
        else
        {
            Log.i(TAG, "AddFragment: ");
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void AddFragmentAt(Fragment fragment, int index, MenuModel menuModel)
    {
        fragmentList.add(index,fragment);
        fragmentMenuList.add(index,menuModel);
        notifyDataSetChanged();
    }

    public List<MenuModel> getFragmentMenuList()
    {
        return fragmentMenuList;
    }

    public Fragment getFragment(int position)
    {
        return fragmentList.get(position);
    }
}



