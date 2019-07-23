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

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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


    public static final int LOAD_MORE_SCROLL_RANGE = 3000;

    public static final int FULLSCREEN_REQUEST_CODE = 1;


    private static final String TAG = "Mainactivity";

    private static final queryModel homeQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","toplist");
    private static final queryModel popularQueryModel = new queryModel(true,true,true,true,true,false,
            0,0,0,0,0,
            "","desc","","date_added");

    public ArrayList<capturedImages> ImageList = new ArrayList<capturedImages>();

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


        MenuModel categoriesMenuModel = new MenuModel("Categories",false,false,true,R.drawable.ic_list,null);
        MenuModel resultMenuModel = new MenuModel("Result",false,false,true,R.drawable.ic_search,null);
        MenuModel homeMenuModel = new MenuModel("Home",false,false,true,R.drawable.ic_home,null);
        MenuModel popularMenuModel = new MenuModel("Popular",false,false,true,R.drawable.ic_hot,null);

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



    }


    public static void drawTabMenu(Context context,TabLayout tabLayout,List<MenuModel> menuModels)
    {
        for (MenuModel model:menuModels
             ) {
            int index = menuModels.indexOf(model);
            LinearLayout view =(LinearLayout) LayoutInflater.from(context).inflate(R.layout.custom_tab,null);
            tabLayout.getTabAt(index).setCustomView(view);

            ImageView imageView = view.findViewById(R.id.custom_tab_image);

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
        i.putExtra("listIndex",modelList.indexOf(model)); // Modelin indexi viewpagerda görüntülenecek
        i.putExtra("wallpaperList",listData);
        i.putExtra("queryData",queryData);


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

    private void ThreeLevelMenuData()
    {
        queryModel welcomeQuery = new queryModel(true,true,true,true,true,false,
                0,0,0,0,0,
                "","desc","","random");
        queryModel mostViewedQuery = new queryModel(true,true,true,true,true,false,
                0,0,0,0,0,
                "","desc","","views");
        queryModel randomQuery = new queryModel(true,true,true,true,true,false,
                0,0,0,0,0,
                "","desc","","random");
        queryModel latestQuery = new queryModel(true,true,true,true,true,false,
                0,0,0,0,0,
                "","desc","","date_added");
        queryModel relevanceQuery = new queryModel(true,true,true,true,true,false,
                0,0,0,0,0,
                "","desc","","relevance");
        queryModel favoritesQuery = new queryModel(true,true,true,true,true,false,
                0,0,0,0,0,
                "","desc","","favorites");
        queryModel topListQuery = new queryModel(true,true,true,true,true,false,
                0,0,0,0,0,
                "","desc","","toplist");

        MenuModel headerRandom = new MenuModel("Random",false,false,true,R.string.fa_random_solid,welcomeQuery);
        MenuModel headerQuickAccess = new MenuModel("Quick Access",true,true,true,R.string.fa_star,null);
        MenuModel headerCategories = new MenuModel("Categories",true,true,true,R.string.fa_list_alt,null);
        MenuModel headerGallery = new MenuModel("Gallery",false,false,true,R.string.fa_camera_retro_solid,null);
        MenuModel headerLastViews = new MenuModel("Last Views",false,false,true,R.string.fa_eye,null);
        MenuModel headerFavorires = new MenuModel("Favorites",false,false,true,R.string.fa_star,null);


        MenuModel mostViewedModel = new MenuModel("Most Viewed",false,false,false,R.string.fa_dot_circle_solid,mostViewedQuery);
        MenuModel topListModel = new MenuModel("Top List",false,false,false,R.string.fa_dot_circle_solid,topListQuery);
        MenuModel randomModel = new MenuModel("Random",false,false,false,R.string.fa_dot_circle_solid,randomQuery);
        MenuModel latestModel = new MenuModel("Latest",false,false,false,R.string.fa_dot_circle_solid,latestQuery);
        MenuModel relevanceModel = new MenuModel("Relevance",false,false,false,R.string.fa_dot_circle_solid,relevanceQuery);
        MenuModel favoritesModel = new MenuModel("Favorites",false,false,false,R.string.fa_dot_circle_solid,favoritesQuery);

        MenuModel AnimeMangaModel = new MenuModel("Anime & Manga",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel ArtDesignModel = new MenuModel("Art & Design",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel EntertainmentModel = new MenuModel("Entertainment",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel KnowledgeModel = new MenuModel("Knowledge",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel LocationModel = new MenuModel("Location",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel MiscellaneousModel = new MenuModel("Miscellaneous",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel NatureModel = new MenuModel("Nature",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel PeopleModel = new MenuModel("People",true,true,false,R.string.fa_chevron_right_solid,null);
        MenuModel VehiclesModel = new MenuModel("Vehicles",true,true,false,R.string.fa_chevron_right_solid,null);


        MenuModel animeCharactersModel = new MenuModel("Characters",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,true,false,true,true,true,0,0,0,0,0,"","desc","Characters","date_added"));
        MenuModel animeOtherModel = new MenuModel("Other",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,true,false,true,true,true,0,0,0,0,0,"","desc","","date_added"));
        MenuModel animeSeriesModel = new MenuModel("Series",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,true,false,true,true,true,0,0,0,0,0,"","desc","Series","date_added"));
        MenuModel animeVisualNovelsModel = new MenuModel("Visual Novels",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,true,false,true,true,true,0,0,0,0,0,"","desc","Visual Novels","date_added"));
        MenuModel[] animeThirdLevelCollector = new MenuModel[] { animeCharactersModel,animeOtherModel,animeSeriesModel,animeVisualNovelsModel};


        MenuModel artArchitectureModel = new MenuModel("Architecture",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,false,true,true,true,0,0,0,0,0,"","desc","Architecture","date_added"));
        MenuModel artDigitallModel = new MenuModel("Digitall",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,false,true,true,true,0,0,0,0,0,"","desc","Digitall","date_added"));
        MenuModel artPhotographyModel = new MenuModel("Photography",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,false,true,true,true,0,0,0,0,0,"","desc","Photography","date_added"));
        MenuModel artTraditionalModel = new MenuModel("Traditional ",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,false,true,true,true,0,0,0,0,0,"","desc","Traditional","date_added"));

        MenuModel[] artThirdLevelCollector = new MenuModel[] {artArchitectureModel,artDigitallModel,artPhotographyModel,artTraditionalModel};


        MenuModel entertainmentComicBooksModel = new MenuModel("Comick Books",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Comic Books","date_added"));
        MenuModel entertainmentGraphicNovelModel = new MenuModel("Graphic Novel",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Graphich Novel","date_added"));
        MenuModel entertainmentEventsModel = new MenuModel("Events",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Event","date_added"));
        MenuModel entertainmentGamesModel = new MenuModel("Games",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Games","date_added"));
        MenuModel entertainmentLiteratureModel = new MenuModel("Literatures",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Literature","date_added"));
        MenuModel entertainmentMoviesModel = new MenuModel("Movies",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Movies","date_added"));
        MenuModel entertainmentMusicModel = new MenuModel("Music",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Muisc","date_added"));
        MenuModel entertainmentSportModel = new MenuModel("Sport",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Sport","date_added"));
        MenuModel entertainmentTelevisionModel = new MenuModel("Television",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Television","date_added"));

        MenuModel[] entertainmentThirdLevelCollector = new MenuModel[] {entertainmentComicBooksModel,entertainmentEventsModel,entertainmentGraphicNovelModel,entertainmentGamesModel,entertainmentLiteratureModel,entertainmentMoviesModel,entertainmentMusicModel,entertainmentSportModel,entertainmentTelevisionModel};


        MenuModel knowledgeHistoryModel = new MenuModel("History",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","History","date_added"));
        MenuModel knowledgeHolidayModel = new MenuModel("Holiday",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Holiday","date_added"));
        MenuModel knowledgeMilitaryModel = new MenuModel("Military",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Military","date_added"));
        MenuModel knowledgeWeaponsModel = new MenuModel("Weapons",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Weapon","date_added"));
        MenuModel knowledgeQuotesModel = new MenuModel("Quotes",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Quote","date_added"));
        MenuModel knowledgeReligionModel = new MenuModel("Religion",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Religion","date_added"));
        MenuModel knowledgeScienceModel = new MenuModel("Science",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Science","date_added"));

        MenuModel[] knowledgeThirdLevelCollector = new MenuModel[] {knowledgeHistoryModel,knowledgeHolidayModel,knowledgeMilitaryModel,knowledgeWeaponsModel,knowledgeQuotesModel,knowledgeReligionModel,knowledgeScienceModel};




        MenuModel locationCitiesModel = new MenuModel("Cities",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","City","date_added"));
        MenuModel locationCountriesModel = new MenuModel("Countries",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Country","date_added"));
        MenuModel locationOtherModel = new MenuModel("Other",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Location","date_added"));
        MenuModel locationSpaceModel = new MenuModel("Space",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Space","date_added"));

        MenuModel[] locationThirdLevelCollector = new MenuModel[] {locationCitiesModel,locationCountriesModel,locationOtherModel,locationSpaceModel};





        MenuModel miscClothingModel = new MenuModel("Clothing",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Clothing","date_added"));
        MenuModel miscColorsModel = new MenuModel("Colors",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Colors","date_added"));
        MenuModel miscCompaniesModel = new MenuModel("Companies",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Company","date_added"));
        MenuModel miscLogosModel = new MenuModel("Logos",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Logo","date_added"));
        MenuModel miscFoodModel = new MenuModel("Food",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Food","date_added"));
        MenuModel miscTechnologyModel = new MenuModel("Technology",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Technology","date_added"));


        MenuModel[] miscThirdLevelCollector = new MenuModel[] {miscClothingModel,miscColorsModel,miscCompaniesModel,miscLogosModel,miscFoodModel,miscTechnologyModel};






        MenuModel natureAnimalModel = new MenuModel("Animal",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Animal","date_added"));
        MenuModel natureLandscapeModel = new MenuModel("Landscape",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Landscape","date_added"));
        MenuModel naturePlantsModel = new MenuModel("Plants",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Plant","date_added"));

        MenuModel[] natureThirdLevelCollector = new MenuModel[] {natureAnimalModel,natureLandscapeModel,naturePlantsModel};



        MenuModel peopleArtistsModel = new MenuModel("Artists",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,false,true,true,true,true,0,0,0,0,0,"","desc","Artists","date_added"));
        MenuModel peopleCelebritiesModel = new MenuModel("Celebrities",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,false,true,true,true,true,0,0,0,0,0,"","desc","Celebrities","date_added"));
        MenuModel peopleFictionalCharactersModel = new MenuModel("Fictional Characters",false,false,false,0,
                new queryModel(false,false,true,true,true,true,0,0,0,0,0,"","desc","Fictional","date_added"));
        MenuModel peopleModelsModel = new MenuModel("Models",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,false,true,true,true,true,0,0,0,0,0,"","desc","Models","date_added"));
        MenuModel peopleOtherFigureModel = new MenuModel("Other Figure",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,false,true,true,true,true,0,0,0,0,0,"","desc","Figure","date_added"));
        MenuModel peoplePornstarsModel = new MenuModel("Pornstars",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(false,false,true,true,true,true,0,0,0,0,0,"","desc","Pornstars","date_added"));


        MenuModel[] peopleThirdLevelCollector = new MenuModel[] {peopleArtistsModel,peopleCelebritiesModel,peopleFictionalCharactersModel,peopleModelsModel,peopleOtherFigureModel,peoplePornstarsModel};



        MenuModel vehiclesAircraftModel = new MenuModel("Aircraft",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Aircraft","date_added"));
        MenuModel vehiclesCarsModel = new MenuModel("Cars",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Cars","date_added"));
        MenuModel vehiclesMotorcycleModel = new MenuModel("Motorcycle",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Motorcycle","date_added"));
        MenuModel vehiclesShipsModel = new MenuModel("Ships",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Ships","date_added"));
        MenuModel vehiclesSpaceCraftsModel = new MenuModel("Space Crafts",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Space Crafts","date_added"));
        MenuModel vehiclesTrainsModel = new MenuModel("Trains",false,false,false,R.string.fa_dot_circle_solid,
                new queryModel(true,false,true,true,true,true,0,0,0,0,0,"","desc","Trains","date_added"));



        MenuModel[] vehiclesThirdLevelCollector = new MenuModel[] {vehiclesAircraftModel,vehiclesCarsModel,vehiclesMotorcycleModel,vehiclesShipsModel,vehiclesSpaceCraftsModel,vehiclesTrainsModel};


        MenuModel[] welcomeSecondLevel = new MenuModel[]{};
        MenuModel[] quickAccessSecondLevel = new MenuModel[]{mostViewedModel,topListModel,randomModel,latestModel,relevanceModel,favoritesModel};
        MenuModel[] categoriesSecondLevel = new MenuModel[]{AnimeMangaModel,ArtDesignModel,EntertainmentModel,KnowledgeModel,LocationModel,MiscellaneousModel,NatureModel,PeopleModel,VehiclesModel};
        MenuModel[] gallerySecondLevel = new MenuModel[]{};
        MenuModel[] favoritesSecondLevel = new MenuModel[]{};
        MenuModel[] lastViewsSecondLevel = new MenuModel[]{};

        LinkedHashMap<MenuModel, MenuModel[]> welcomeThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> quickAccessThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> categoriesThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> galleryThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> favoritesThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> lastViewsThirdLevel = new LinkedHashMap<>();
        /**
         * Second level array list
         */
        final List<MenuModel[]> secondLevel = new ArrayList<>();
        /**
         * Inner level data
         */
        List<LinkedHashMap<MenuModel, MenuModel[]>> data = new ArrayList<>();

        final List<MenuModel> parentList = new ArrayList<>();
        parentList.add(headerRandom);
        parentList.add(headerQuickAccess);
        parentList.add(headerCategories);
        parentList.add(headerFavorires);
        parentList.add(headerGallery);
        parentList.add(headerLastViews);




        secondLevel.add(welcomeSecondLevel);
        secondLevel.add(quickAccessSecondLevel);
        secondLevel.add(categoriesSecondLevel);
        secondLevel.add(favoritesSecondLevel);
        secondLevel.add(gallerySecondLevel);
        secondLevel.add(lastViewsSecondLevel);
        quickAccessThirdLevel.put(null, null);
        categoriesThirdLevel.put(AnimeMangaModel, animeThirdLevelCollector);
        categoriesThirdLevel.put(ArtDesignModel, artThirdLevelCollector);
        categoriesThirdLevel.put(EntertainmentModel, entertainmentThirdLevelCollector);
        categoriesThirdLevel.put(KnowledgeModel, knowledgeThirdLevelCollector);
        categoriesThirdLevel.put(LocationModel, locationThirdLevelCollector);
        categoriesThirdLevel.put(MiscellaneousModel, miscThirdLevelCollector);
        categoriesThirdLevel.put(NatureModel, natureThirdLevelCollector);
        categoriesThirdLevel.put(PeopleModel, peopleThirdLevelCollector);
        categoriesThirdLevel.put(VehiclesModel, vehiclesThirdLevelCollector);

        data.add(welcomeThirdLevel);
        data.add(quickAccessThirdLevel);
        data.add(categoriesThirdLevel);
        data.add(favoritesThirdLevel);
        data.add(galleryThirdLevel);
        data.add(lastViewsThirdLevel);
        expandableListView = (ExpandableListView) findViewById(R.id.menu_expandable);
        //passing three level of information to constructor
        ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(this, parentList, secondLevel, data);
        expandableListView.setAdapter(threeLevelListAdapterAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
        /*expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                MenuModel[] groupModel = secondLevel.get(groupPosition);
                MainActivity.setMenuClickListener(groupModel[childPosition],MainActivity.ma,RECREATE);
                return true;
            }
        });
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                MenuModel header = parentList.get(groupPosition);
                MenuModel[] childrens = secondLevel.get(0);
                if(childrens == null)
                    MainActivity.setMenuClickListener(header,MainActivity.ma, RECREATE);
                else if(childrens.length == 0)
                    MainActivity.setMenuClickListener(header,MainActivity.ma,RECREATE);
                return false;
            }
        })*/
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
        if(progressBar.getProgress() < 100)
        {
            new GlideImageLoader(im,progressBar).load(url,requestOptions,model);
        }

    }
    public static void LoadImageFromURL(ImageView im, String url, final CircleProgressBar progressBar, RequestOptions requestOptions,wallpaperModel model,Context context)
    {
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



