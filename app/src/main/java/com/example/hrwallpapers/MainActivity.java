package com.example.hrwallpapers;

import android.app.Activity;
import android.app.ActivityOptions;
import android.arch.core.executor.TaskExecutor;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    public static final int LOAD_MORE = 0;
    public static final int RECREATE = 1;
    public static final int VIEWPAGER_LOAD_MORE = 2;

    public static final int RECYCLER_VIEW_COLUMN = 2;
    public static final int LOAD_MORE_SCROLL_RANGE = 3000;

    public static final int FULLSCREEN_REQUEST_CODE = 0;


    private static final String TAG = "Mainactivity";

    public ArrayList<capturedImages> ImageList = new ArrayList<capturedImages>();

    public static MainActivity ma;
    ExpandableListView expandableListView;
    ExpandableListAdapter menuAdapter;
    HashMap<MenuModel,List<MenuModel>> menuHashmap = new HashMap<>();
    List<MenuModel> menuHeaderList = new ArrayList<>();
    public static wallpaperRecyclerViewAdapter recyclerViewAdapter;
    public static RecyclerView recyclerView;
    public static wallpaperModel selectedWallpaper;
    public static MenuModel activeMenu;
    public static List<wallpaperModel> activeModelList;
    public static Fragment popupFragment;
    public static FrameLayout fragmentHolder;
    public static View mainContentView;

    public static AsyncTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ma = this;
        setContentView(R.layout.activity_main);
        fragmentHolder = findViewById(R.id.wallpaper_fragment_holder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerForWallpapers);

        recyclerView.getItemAnimator().setChangeDuration(0);
        popupFragment = setFragment(new wallpaperPopupFragment());
        mainContentView = findViewById(R.id.main_content);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        expandableListView = findViewById(R.id.menu_expandable);

        ThreeLevelMenuData();

        if(savedInstanceState != null)
        {
            if(recyclerViewAdapter != null)
            {
                int currentPosition = recyclerViewAdapter.getCurrentViewPosition();
                triggerForLoadMore(activeModelList,this,activeMenu,RECREATE);
                recyclerViewAdapter.notifyDataSetChanged();
                if(currentPosition > 4) recyclerView.scrollToPosition(currentPosition);
            }
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int calculatedYPos = 0;
            private int actualHeight = 0;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                calculatedYPos = recyclerView.computeVerticalScrollOffset();
                actualHeight = recyclerView.computeVerticalScrollRange();



                if(actualHeight - (recyclerView.computeVerticalScrollExtent() + calculatedYPos) < LOAD_MORE_SCROLL_RANGE)
                {
                    Log.i("Scroll", "onScrolled: " + LOAD_MORE_SCROLL_RANGE + " <" + (actualHeight - (recyclerView.computeVerticalScrollExtent() + calculatedYPos)));
                    if(task.getStatus() == AsyncTask.Status.FINISHED)
                    {
                        Log.i("Scroll", "onScrolled: Task status finished olarak geldi.");
                        if(activeMenu.queryModel != null)
                        {
                            Log.i("Scroll", "onScrolled: task çalıştırıldı.");
                            activeMenu.queryModel.setActivePage(activeMenu.queryModel.getActivePage() + 1);
                            activeMenu.queryModel.prepareUrl();
                            setMenuClickListener(activeMenu,MainActivity.ma,LOAD_MORE);
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy);

            }

        });

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


    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances
    protected Fragment setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wallpaper_fragment_holder, fragment);
        fragmentTransaction.commit();
        return fragment;
    }

    public void showFullScreenActivity(wallpaperModel model,Context startContext,final Class<? extends Activity> targetActivity,List<wallpaperModel> modelList)
    {
        Intent i = new Intent(startContext,targetActivity);
        String listData = new Gson().toJson(modelList); // List activity içerisinde yeniden build edilecek. View ve class idleri değişecek.
        String menuData = new Gson().toJson(activeMenu);
        i.putExtra("listIndex",modelList.indexOf(model)); // Modelin indexi viewpagerda görüntülenecek
        i.putExtra("wallpaperList",listData);
        i.putExtra("menuModel",menuData);

        selectedWallpaper = model;

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

                    if(modelList.size() > activeModelList.size())
                    {
                        modelList = modelList.subList(activeModelList.size(),modelList.size());

                        if(modelList.size() > 0)
                        {
                            triggerForLoadMore(modelList,this,activeMenu,LOAD_MORE);
                            recyclerView.scrollToPosition(index);
                            Log.i(TAG, "onActivityResult: " + activeModelList.size()+ " - Index : " + index);
                        } // else no need to update when size is equal to activelist
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
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
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
        });
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

    public static int setPxToDP(int px,Context context)
    {
        float den =context.getResources().getDisplayMetrics().density;

        return Math.round((float) px / den);
    }

    public static void setMenuClickListener(final MenuModel menuModel, final Activity activity,int state)
    {
        getImagesOnHttp(menuModel,activity,state);
    }
    public static void setMenuClickListener(final queryModel queryModel,final Activity activity,int state)
    {
        MenuModel carrierMenuModel = new MenuModel("",false,false,false,0,queryModel);
        getImagesOnHttp(carrierMenuModel,activity,state);
    }

    public static void getImagesOnHttp(final MenuModel menuModel, final Activity activity,final int state)
    {
        if(menuModel.queryModel != null && (task == null || task.getStatus() == AsyncTask.Status.FINISHED))
        {
            task = new HttpGetImagesAsync();

            if(activity.getClass() == MainActivity.class)
            {
                DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
            String url = menuModel.queryModel.getUrl();
            Log.i(TAG, "getImagesOnHttp: " + url);

            Object[] container = new Object[] {url};

            ((HttpGetImagesAsync) task).setTaskFisinhed(new HttpGetImagesAsync.onAsyncTaskFisinhed() {
                @Override
                public void taskFinished(List<wallpaperModel> list) {
                    triggerForLoadMore(list,activity,menuModel,state);
                }
            });
            task.execute(container);
        }
    }

    public static void triggerForLoadMore(List<wallpaperModel> wallpaperModels,Activity activity,MenuModel menuModel,int state)
    {
        //Mainactivity LOAD_MORE,NOTIFY_DATA_CHANGED created for the state
        if(activity.getClass() == MainActivity.class)
        {
            if(MainActivity.activeMenu == menuModel && state == LOAD_MORE)
            {
                //Load more images for the same menu
                MainActivity.activeModelList.addAll(wallpaperModels);
                recyclerViewAdapter.notifyDataSetChanged();
            }
            else if(state == RECREATE )
            {
                //Load new images from the new query
                recyclerViewAdapter = new wallpaperRecyclerViewAdapter(wallpaperModels,fragmentHolder,popupFragment,MainActivity.mainContentView);

                GridLayoutManager layoutManager = new GridLayoutManager(activity.getApplicationContext(),RECYCLER_VIEW_COLUMN);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(recyclerViewAdapter);
                MainActivity.activeModelList = wallpaperModels;
                MainActivity.activeMenu = menuModel;
            }
        }
        else if(activity.getClass() == BaseWallpaperActivity.class)
        {
            BaseWallpaperActivity baseWallpaperActivity = (BaseWallpaperActivity) activity;
            if(menuModel != null && state == VIEWPAGER_LOAD_MORE)
            {
                baseWallpaperActivity.wallpaperModelList.addAll(wallpaperModels);
                baseWallpaperActivity.adapter.notifyDataSetChanged();
            }
            else if (menuModel != null && state == RECREATE)
            {
                baseWallpaperActivity.wallpaperModelList = wallpaperModels;
                baseWallpaperActivity.adapter.notifyDataSetChanged();
            }
        }
    }

    public static void LoadImageFromURL(ImageView im, String url, final CircleProgressBar progressBar, RequestOptions requestOptions,wallpaperModel model)
    {
        new GlideImageLoader(im,progressBar).load(url,requestOptions,model);

    }
}


