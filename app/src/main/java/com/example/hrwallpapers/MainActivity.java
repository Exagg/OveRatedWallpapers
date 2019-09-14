package com.example.hrwallpapers;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
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
import com.example.hrwallpapers.DataAccessLayer.SqliteConnection;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import info.androidhive.fontawesome.FontCache;
import info.androidhive.fontawesome.FontTextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final File downloadFolder = new File(Environment.getExternalStorageDirectory() + File.separator + MainActivity.DOWNLOAD_FILE_NAME);
    public static SpliceWallpaperBackgroundService autoBackgroundService;

    public static SqliteConnection database;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ma = this;
        database = new SqliteConnection(MainActivity.this);
        wallpaperInFavorites =database.getFavorites();

        startService(new Intent(this,SpliceWallpaperBackgroundService.class));

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainContentView = findViewById(R.id.main_content);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) && mDrawerIsOpen) {
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


    public static void setIconToImageView(FontTextView fontTextView, Context context, int resource , boolean isSolid, boolean isBrand)
    {
        if (isBrand)
            fontTextView.setTypeface(FontCache.get(context, "fa-brands-400.ttf"));
        else if (isSolid)
            fontTextView.setTypeface(FontCache.get(context, "fa-solid-900.ttf"));
        else
            fontTextView.setTypeface(FontCache.get(context, "fa-regular-400.ttf"));

        fontTextView.setText(resource);
    }
    public static void setIconToImageView(FontTextView fontTextView, Context context, int resource , boolean isSolid, boolean isBrand, int size)
    {
        if (isBrand)
            fontTextView.setTypeface(FontCache.get(context, "fa-brands-400.ttf"));
        else if (isSolid)
            fontTextView.setTypeface(FontCache.get(context, "fa-solid-900.ttf"));
        else
            fontTextView.setTypeface(FontCache.get(context, "fa-regular-400.ttf"));

        fontTextView.setText(resource);
        fontTextView.setTextSize(size);
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

    public static void setMenuClickListenerForViewPager(final queryModel queryModel, final ViewPager viewPager, final BaseWallpaperActivity.BaseWallpaperPagerAdapter adapter,HttpGetImagesAsync task)
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
                                       final int loadToWhere, final BaseWallpaperActivity.BaseWallpaperPagerAdapter pagerAdapter, HttpGetImagesAsync _task)
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

    public static void loadWallpaperToViewPager(List<wallpaperModel> models, BaseWallpaperActivity.BaseWallpaperPagerAdapter adapter)
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
            database.addFavorite(model.id, getCurrentDateTime(),pictureType);
            changeImageViewAsLiked(toggleImage);
        }
        else
        {
            model.isFavorite.setValue(false);
            wallpaperInFavorites.remove(model.id);
            database.removeFavorite(model.id);
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
    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances


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

    public static void checkPermissions(Context context,Activity activity,int PERM_REQUEST_CODE)
    {
        String[]  needPerms = new String[]
        {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
        };

        for (String perm:needPerms
             ) {

            if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(activity,needPerms,PERM_REQUEST_CODE);
                break;
            }

        }
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



