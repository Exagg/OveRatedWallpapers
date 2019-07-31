package com.example.hrwallpapers;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.transform.Result;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final int LOAD_TO_RECYCLERVIEW = 1;
    public static final int LOAD_TO_PAGEVIEWER = 2;

    public static final String DOWNLOAD_FILE_NAME = "Splice Wallpapers";
    public static String DOWNLOAD_FILE_PATH;


    public static final int LOAD_MORE_SCROLL_RANGE = 3000;

    public static final int FULLSCREEN_REQUEST_CODE = 1;


    private static final String TAG = "Mainactivity";

    private static final queryModel homeQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","toplist","3d");
    private static final queryModel popularQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","date_added",null);


    ExpandableListAdapter menuAdapter;
    public static MainActivity ma;
    ExpandableListView expandableListView;
    HashMap<MenuModel,List<MenuModel>> menuHashmap = new HashMap<>();
    List<MenuModel> menuHeaderList = new ArrayList<>();
    public static View mainContentView;
    public static mainViewPagerAdapter viewPagerAdapter;
    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    public static Context context;
    public static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ma = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainContentView = findViewById(R.id.main_content);
        context = getApplicationContext();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DOWNLOAD_FILE_NAME);
        boolean created = false;
        if(!folder.exists())
        {
            created = folder.mkdirs();
        }
        else created = true;

        if(created)DOWNLOAD_FILE_PATH = folder.getAbsolutePath();
        else  DOWNLOAD_FILE_PATH = null;

        MenuModel categoriesMenuModel = new MenuModel("Categories",false,false,true,R.drawable.ic_list,null);
        MenuModel resultMenuModel = new MenuModel("Result",false,false,true,R.drawable.ic_search,null);
        MenuModel homeMenuModel = new MenuModel("Popular",false,false,true,R.drawable.ic_home,null);
        MenuModel popularMenuModel = new MenuModel("Latest",false,false,true,R.drawable.ic_hot,null);

        HomeFragment homeFragment= new HomeFragment();
        homeFragment.setActiveQueryModel(homeQueryModel);

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        PopularFragment popularFragment = new PopularFragment();
        popularFragment.setActiveQueryModel(popularQueryModel);

        ResultFragment resultFragment = new ResultFragment();

        tabLayout= findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.main_view_pager);
        viewPagerAdapter = new mainViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.AddFragment(resultFragment,resultMenuModel);
        viewPagerAdapter.AddFragment(categoriesFragment,categoriesMenuModel);
        viewPagerAdapter.AddFragment(homeFragment,homeMenuModel);
        viewPagerAdapter.AddFragment(popularFragment,popularMenuModel);

        viewPager.setAdapter(viewPagerAdapter);


        tabLayout.setupWithViewPager(viewPager);
        drawTabMenu(this,tabLayout,viewPagerAdapter.getFragmentMenuList());

        expandableListView = findViewById(R.id.menu_expandable);

        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

    }


    public static void drawTabMenu(Context context,TabLayout tabLayout,List<MenuModel> menuModels)
    {
        for (MenuModel model:menuModels
             ) {
            int index = menuModels.indexOf(model);
            LinearLayout view =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.custom_tab,null);
            tabLayout.getTabAt(index).setCustomView(view);

            ImageView imageView = view.findViewById(R.id.custom_tab_image);
            TextView textview = view.findViewById(R.id.custom_tab_textview);


            textview.setText(model.name);

            Drawable listDrawable = context.getDrawable(model.drawableID);
            imageView.setImageDrawable(listDrawable);


        }
        if(tabLayout.getChildCount() > 0)
        {
            toggleResultTab(View.GONE);
        }
        if(tabLayout.getTabCount() > 1)
        {
            tabLayout.getTabAt(1).select();
        }
    }

    public static void toggleResultTab(int visibility)
    {
        ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0).setVisibility(visibility);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

        /*if (id == R.id.nav_home) {
            // Handle the camera action
            new getRequestOnPage().execute();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        return true;
    }


    public void showFullScreenActivity(wallpaperModel model,Context startContext,final Class<? extends Activity> targetActivity,List<wallpaperModel> modelList)
    {
        Intent i = new Intent(startContext,targetActivity);
        String listData = new Gson().toJson(modelList); // List activity içerisinde yeniden build edilecek. View ve class idleri değişecek.
        i.putExtra("listIndex",modelList.indexOf(model)); // Modelin indexi viewpagerda görüntülenecek
        i.putExtra("wallpaperList",listData);


        if(Build.VERSION.SDK_INT > 20)
        {

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(i,FULLSCREEN_REQUEST_CODE,options.toBundle());
        }
        else {
            startActivityForResult(i,FULLSCREEN_REQUEST_CODE);
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


                    Fragment activeFragment = viewPagerAdapter.getFragment(viewPager.getCurrentItem());
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

                    }
                }
            }
        }
    }



    public static void setIconToImageView(ImageView imageView, Context context,int resource ,boolean isSolid,boolean isBrand,int size)
    {
        FontDrawable drawable = new FontDrawable(context,resource,isSolid,isBrand);
        drawable.setTextSize(MainActivity.setPxToDP(size,context));
        imageView.setImageDrawable(drawable);
    }


    public static void setIconToImageView(ImageView imageView, Context context, int resource , boolean isSolid, boolean isBrand, int size, int color)
    {
        FontDrawable drawable = new FontDrawable(context,resource,isSolid,isBrand);
        drawable.setTextSize(MainActivity.setPxToDP(size,context));
        drawable.setTextColor(color);
        imageView.setImageDrawable(drawable);
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

    public static void getImagesOnHttp(final queryModel queryModel,final wallpaperRecyclerViewAdapter recyclerViewAdapter,
                                       final int loadToWhere,final BaseWallpaperPagerAdapter pagerAdapter,HttpGetImagesAsync _task)
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
                _task.execute(container);
            }
        }
    }

    public static void loadWallpaperToViewPager(List<wallpaperModel> models,BaseWallpaperPagerAdapter adapter)
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

    public static void LoadImageFromURL(ImageView im, String url, final CircleProgressBar progressBar, RequestOptions requestOptions,wallpaperModel model)
    {
        progressBar.setProgress(0);
        if(progressBar.getProgress() < 100)
        {
            new GlideImageLoader(im,progressBar).load(url,requestOptions,model);
        }

    }
    public static void LoadImageFromURL(ImageView im, String url, final CircleProgressBar progressBar, RequestOptions requestOptions,wallpaperModel model,Context context)
    {
        progressBar.setProgress(0);
        if(progressBar.getProgress() < 100)
        {
            new GlideImageLoader(im,progressBar,context).load(url,requestOptions,model);
        }

    }


    public static void likeWallpaper(@NonNull wallpaperModel model,@NonNull ImageView toggleImage)
    {
        if(!model.isFavorite.isTrue())
        {
            model.isFavorite.setValue(true);
            changeImageViewAsLiked(toggleImage);
        }
        else
        {
            model.isFavorite.setValue(false);
            changeImageViewAsUnliked(toggleImage);
        }

    }

    public static void changeImageViewAsLiked(@NonNull ImageView im)
    {
        im.setImageResource(R.drawable.ic_favorites_liked);
        ((Drawable) im.getDrawable()).setTint(MainActivity.context.getResources().getColor(R.color.red));
    }

    public static void changeImageViewAsUnliked(@NonNull ImageView im)
    {
        im.setImageResource(R.drawable.ic_favorites_unlike);
        ((Drawable) im.getDrawable()).setTint(MainActivity.context.getResources().getColor(R.color.white));

    }
    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances


    public static void showToast(String message,int duration,Context context)
    {
        toast.cancel();
        toast = toast.makeText(context,message,duration);
        toast.show();
    }

}
class mainViewPagerAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<MenuModel> fragmentMenuList = new ArrayList<>();

    public mainViewPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
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
        fragmentList.add(fragment);
        fragmentMenuList.add(menuModel);
        notifyDataSetChanged();
    }

    public void AddFragmentAt(Fragment fragment,int index,MenuModel menuModel)
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



