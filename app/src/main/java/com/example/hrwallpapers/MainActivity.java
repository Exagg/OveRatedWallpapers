package com.example.hrwallpapers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.constraint.motion.MotionScene.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<capturedImages> ImageList = new ArrayList<capturedImages>();

    public static MainActivity ma;
    ExpandableListView expandableListView;
    ExpandableListAdapter menuAdapter;
    HashMap<MenuModel,List<MenuModel>> menuHashmap = new HashMap<>();
    List<MenuModel> menuHeaderList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ma = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        expandableListView = findViewById(R.id.menu_expandable);

        ThreeLevelMenuData();
        //loadMenuData();
        //setEventToExpandableList();
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        MenuModel headerWelcome = new MenuModel("Welcome",false,false,true,R.drawable.ic_nuclear,welcomeQuery);
        MenuModel headerQuickAccess = new MenuModel("Quick Access",true,true,true,R.drawable.ic_hot,null);
        MenuModel headerCategories = new MenuModel("Categories",true,true,true,R.drawable.ic_photo_gallery,null);
        MenuModel headerGallery = new MenuModel("Gallery",false,false,true,R.drawable.ic_camera,null);
        MenuModel headerLastViews = new MenuModel("Last Views",false,false,true,R.drawable.ic_eye,null);


        MenuModel mostViewedModel = new MenuModel("Most Viewed",false,false,false,R.drawable.icon_dot,mostViewedQuery);
        MenuModel topListModel = new MenuModel("Top List",false,false,false,R.drawable.icon_dot,topListQuery);
        MenuModel randomModel = new MenuModel("Random",false,false,false,R.drawable.icon_dot,randomQuery);
        MenuModel latestModel = new MenuModel("Latest",false,false,false,R.drawable.icon_dot,latestQuery);
        MenuModel relevanceModel = new MenuModel("Relevance",false,false,false,R.drawable.icon_dot,relevanceQuery);
        MenuModel favoritesModel = new MenuModel("Favorites",false,false,false,R.drawable.icon_dot,favoritesQuery);

        MenuModel AnimeMangaModel = new MenuModel("Anime & Manga",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel ArtDesignModel = new MenuModel("Art & Design",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel EntertainmentModel = new MenuModel("Entertainment",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel KnowledgeModel = new MenuModel("Knowledge",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel LocationModel = new MenuModel("Location",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel MiscellaneousModel = new MenuModel("Miscellaneous",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel NatureModel = new MenuModel("Nature",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel PeopleModel = new MenuModel("People",true,true,false,R.drawable.ic_menu_right,null);
        MenuModel VehiclesModel = new MenuModel("Vehicles",true,true,false,R.drawable.ic_menu_right,null);


        MenuModel animeCharactersModel = new MenuModel("Characters",false,false,false,R.drawable.icon_dot,null);
        MenuModel animeOtherModel = new MenuModel("Other",false,false,false,R.drawable.icon_dot,null);
        MenuModel animeSeriesModel = new MenuModel("Series",false,false,false,R.drawable.icon_dot,null);
        MenuModel animeVisualNovelsModel = new MenuModel("Visual Novels",false,false,false,R.drawable.icon_dot,null);

        MenuModel[] animeThirdLevelCollector = new MenuModel[] { animeCharactersModel,animeOtherModel,animeSeriesModel,animeVisualNovelsModel};


        MenuModel artArchitectureModel = new MenuModel("Architecture",false,false,false,R.drawable.icon_dot,null);
        MenuModel artDigitallModel = new MenuModel("Digitall",false,false,false,R.drawable.icon_dot,null);
        MenuModel artPhotographyModel = new MenuModel("Photography",false,false,false,R.drawable.icon_dot,null);
        MenuModel artTraditionalModel = new MenuModel("Traditional ",false,false,false,R.drawable.icon_dot,null);

        MenuModel[] artThirdLevelCollector = new MenuModel[] {artArchitectureModel,artDigitallModel,artPhotographyModel,artTraditionalModel};


        MenuModel entertainmentComicBooksModel = new MenuModel("Comick Books",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentGraphicNovelModel = new MenuModel("Graphic Novel",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentEventsModel = new MenuModel("Events",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentGamesModel = new MenuModel("Games",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentLiteratureModel = new MenuModel("Literatures",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentMoviesModel = new MenuModel("Movies",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentMusicModel = new MenuModel("Music",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentSportModel = new MenuModel("Sport",false,false,false,R.drawable.icon_dot,null);
        MenuModel entertainmentTelevisionModel = new MenuModel("Television",false,false,false,R.drawable.icon_dot,null);

        MenuModel[] entertainmentThirdLevelCollector = new MenuModel[] {entertainmentComicBooksModel,entertainmentEventsModel,entertainmentGraphicNovelModel,entertainmentGamesModel,entertainmentLiteratureModel,entertainmentMoviesModel,entertainmentMusicModel,entertainmentSportModel,entertainmentTelevisionModel};


        MenuModel knowledgeHistoryModel = new MenuModel("History",false,false,false,R.drawable.icon_dot,null);
        MenuModel knowledgeHolidayModel = new MenuModel("Holiday",false,false,false,R.drawable.icon_dot,null);
        MenuModel knowledgeMilitaryModel = new MenuModel("Military",false,false,false,R.drawable.icon_dot,null);
        MenuModel knowledgeWeaponsModel = new MenuModel("Weapons",false,false,false,R.drawable.icon_dot,null);
        MenuModel knowledgeQuotesModel = new MenuModel("Quotes",false,false,false,R.drawable.icon_dot,null);
        MenuModel knowledgeReligionModel = new MenuModel("Religion",false,false,false,R.drawable.icon_dot,null);
        MenuModel knowledgeScienceModel = new MenuModel("Science",false,false,false,R.drawable.icon_dot,null);

        MenuModel[] knowledgeThirdLevelCollector = new MenuModel[] {knowledgeHistoryModel,knowledgeHolidayModel,knowledgeMilitaryModel,knowledgeWeaponsModel,knowledgeQuotesModel,knowledgeReligionModel,knowledgeScienceModel};




        MenuModel locationCitiesModel = new MenuModel("Cities",false,false,false,R.drawable.icon_dot,null);
        MenuModel locationCountriesModel = new MenuModel("Countries",false,false,false,R.drawable.icon_dot,null);
        MenuModel locationOtherModel = new MenuModel("Other",false,false,false,R.drawable.icon_dot,null);
        MenuModel locationSpaceModel = new MenuModel("Space",false,false,false,R.drawable.icon_dot,null);

        MenuModel[] locationThirdLevelCollector = new MenuModel[] {locationCitiesModel,locationCountriesModel,locationOtherModel,locationSpaceModel};





        MenuModel miscClothingModel = new MenuModel("Clothing",false,false,false,R.drawable.icon_dot,null);
        MenuModel miscColorsModel = new MenuModel("Colors",false,false,false,R.drawable.icon_dot,null);
        MenuModel miscCompaniesModel = new MenuModel("Companies",false,false,false,R.drawable.icon_dot,null);
        MenuModel miscLogosModel = new MenuModel("Logos",false,false,false,R.drawable.icon_dot,null);
        MenuModel miscFoodModel = new MenuModel("Food",false,false,false,R.drawable.icon_dot,null);
        MenuModel miscTechnologyModel = new MenuModel("Technology",false,false,false,R.drawable.icon_dot,null);


        MenuModel[] miscThirdLevelCollector = new MenuModel[] {miscClothingModel,miscColorsModel,miscCompaniesModel,miscLogosModel,miscFoodModel,miscTechnologyModel};






        MenuModel natureAnimalModel = new MenuModel("Animal",false,false,false,R.drawable.icon_dot,null);
        MenuModel natureLandscapeModel = new MenuModel("Landscape",false,false,false,R.drawable.icon_dot,null);
        MenuModel naturePlantsModel = new MenuModel("Plants",false,false,false,R.drawable.icon_dot,null);

        MenuModel[] natureThirdLevelCollector = new MenuModel[] {natureAnimalModel,natureLandscapeModel,naturePlantsModel};



        MenuModel peopleArtistsModel = new MenuModel("Artists",false,false,false,R.drawable.icon_dot,null);
        MenuModel peopleCelebritiesModel = new MenuModel("Celebrities",false,false,false,R.drawable.icon_dot,null);
        MenuModel peopleFictionalCharactersModel = new MenuModel("Fictional Characters",false,false,false,0,null);
        MenuModel peopleModelsModel = new MenuModel("Models",false,false,false,R.drawable.icon_dot,null);
        MenuModel peopleOtherFigureModel = new MenuModel("Other Figure",false,false,false,R.drawable.icon_dot,null);
        MenuModel peoplePornstarsModel = new MenuModel("Pornstars",false,false,false,R.drawable.icon_dot,null);


        MenuModel[] peopleThirdLevelCollector = new MenuModel[] {peopleArtistsModel,peopleCelebritiesModel,peopleFictionalCharactersModel,peopleModelsModel,peopleOtherFigureModel,peoplePornstarsModel};



        MenuModel vehiclesAircraftModel = new MenuModel("Aircraft",false,false,false,R.drawable.icon_dot,null);
        MenuModel vehiclesCarsModel = new MenuModel("Cars",false,false,false,R.drawable.icon_dot,null);
        MenuModel vehiclesMotorcycleModel = new MenuModel("Motorcycle",false,false,false,R.drawable.icon_dot,null);
        MenuModel vehiclesShipsModel = new MenuModel("Ships",false,false,false,R.drawable.icon_dot,null);
        MenuModel vehiclesSpaceCraftsModel = new MenuModel("Space Crafts",false,false,false,R.drawable.icon_dot,null);
        MenuModel vehiclesTrainsModel = new MenuModel("Trains",false,false,false,R.drawable.icon_dot,null);



        MenuModel[] vehiclesThirdLevelCollector = new MenuModel[] {vehiclesAircraftModel,vehiclesCarsModel,vehiclesMotorcycleModel,vehiclesShipsModel,vehiclesSpaceCraftsModel,vehiclesTrainsModel};


        MenuModel[] welcomeSecondLevel = new MenuModel[]{};
        MenuModel[] quickAccessSecondLevel = new MenuModel[]{mostViewedModel,topListModel,randomModel,latestModel,relevanceModel,favoritesModel};
        MenuModel[] categoriesSecondLevel = new MenuModel[]{AnimeMangaModel,ArtDesignModel,EntertainmentModel,KnowledgeModel,LocationModel,MiscellaneousModel,NatureModel,PeopleModel,VehiclesModel};
        MenuModel[] gallerySecondLevel = new MenuModel[]{};
        MenuModel[] lastViewsSecondLevel = new MenuModel[]{};

        LinkedHashMap<MenuModel, MenuModel[]> welcomeThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> quickAccessThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> categoriesThirdLevel = new LinkedHashMap<>();
        LinkedHashMap<MenuModel, MenuModel[]> galleryThirdLevel = new LinkedHashMap<>();
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
        parentList.add(headerWelcome);
        parentList.add(headerQuickAccess);
        parentList.add(headerCategories);
        parentList.add(headerGallery);
        parentList.add(headerLastViews);




        secondLevel.add(welcomeSecondLevel);
        secondLevel.add(quickAccessSecondLevel);
        secondLevel.add(categoriesSecondLevel);
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
                MainActivity.setMenuClickListener(groupModel[childPosition]);
                return true;
            }
        });
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                MenuModel header = parentList.get(groupPosition);
                MenuModel[] childrens = secondLevel.get(0);
                if(childrens == null)
                    MainActivity.setMenuClickListener(header);
                else if(childrens.length == 0)
                    MainActivity.setMenuClickListener(header);
                return false;
            }
        });
    }

    public static int setPxToDP(int px,Context context)
    {
        float den =context.getResources().getDisplayMetrics().density;

        return Math.round((float) px / den);
    }


    public static void setMenuClickListener(MenuModel menuModel)
    {
        if(menuModel.queryModel != null)
        {
            String url = menuModel.queryModel.getUrl();
            Object[] container = new Object[] {url};
            AsyncTask task = new getRequestOnPage();

            task.execute(container);
        }
    }

    public static void triggerForLoadMor(List<wallpaperModel> wallpaperModels)
    {
        wallpaperRecyclerViewAdapter adapter = new wallpaperRecyclerViewAdapter(wallpaperModels);
        RecyclerView recyclerView = MainActivity.ma.findViewById(R.id.recyclerForWallpapers);

        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.ma,3);


        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    public static void LoadImageFromURL(ImageView im, String url)
    {
        Random r = new Random();
        int width = r.nextInt(1920);
        int height = r.nextInt(1920);

        Glide.with(MainActivity.ma)
                .load(url)
                .centerCrop()
                .into(im);
    }
}

class getRequestOnPage extends AsyncTask<Object,Integer, List<wallpaperModel>> {
    @Override
    protected List<wallpaperModel> doInBackground(Object... objects) {

        List<wallpaperModel> response= new ArrayList<>();

        String url = (String) objects[0];
        Log.i(TAG, "doInBackground: Url : " + url);
        try {

            Document doc = Jsoup.connect(url).get();
            Elements elems = doc.select("figure");

            Log.i("a", String.valueOf(elems.size()));
            for (int i = 0; i < elems.size() ; i++) {
                String id = elems.get(i).attr("data-wallpaper-id");
                String thumbUrl = String.format("https://th.wallhaven.cc/small/%s/%s.jpg",id.substring(0,2),id);
                String originalUrl = String.format("https://w.wallhaven.cc/full/%s/wallhaven-%s.jpg",id.substring(0,2),id);

                wallpaperModel m = new wallpaperModel(thumbUrl,originalUrl,id);
                if(id != "") response.add(m);


            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(List<wallpaperModel> collection) {

        MainActivity.triggerForLoadMor(collection);
    }
}
