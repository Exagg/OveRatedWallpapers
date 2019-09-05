package com.example.hrwallpapers.DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.example.hrwallpapers.MainActivity;
import com.example.hrwallpapers.wallpaperListModel;
import com.example.hrwallpapers.wallpaperModel;

import java.util.ArrayList;
import java.util.List;

public class SqliteConnection extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION =3;
    private static final String DATABASE_NAME = "Wallpapers";




    private static final String FAVORITES_TABLE_NAME = "Favorites";
    private static final String FAVORITES_ID_COLUMN = "ID";
    private static final String FAVORITES_WALLPAPER_ID_COLUMN  = "WALLPAPER_ID";
    private static final String FAVORITES_DATE_COLUMN = "FAVORITE_TIME";
    private static final String FAVORITES_PICTURE_TYPE_COLUMN = "PICTURE_TYPE";

    private static final String favoritesTableQuery = "CREATE TABLE " + FAVORITES_TABLE_NAME + " (\n"+
            FAVORITES_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
            FAVORITES_WALLPAPER_ID_COLUMN + " TEXT NOT NULL,\n"+
            FAVORITES_DATE_COLUMN + " TEXT NOT NULL,\n"+
            FAVORITES_PICTURE_TYPE_COLUMN + " TEXT NOT NULL\n"+
            ");";


    private static final String WALLPAPER_LISTS_TABLE_NAME = "Lists";
    private static final String WALLPAPER_LISTS_LIST_NAME = "Name";
    private static final String WALLPAPER_LISTS_CREATE_DATE = "Create_Date";
    private static final String WALLPAPER_LISTS_ID = "ID";

    private static final String wallpaperListsQuery = "CREATE TABLE " + WALLPAPER_LISTS_TABLE_NAME + " (\n"+
            WALLPAPER_LISTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
            WALLPAPER_LISTS_LIST_NAME + " TEXT NOT NULL,\n"+
            WALLPAPER_LISTS_CREATE_DATE + " TEXT NOT NULL\n"+
            ");";


    private static final String WALLPAPER_IN_LIST_TABLE_NAME = "Wallpaper_In_List";
    private static final String WALLPAPER_IN_LIST_ID = "ID";
    private static final String WALLPAPER_IN_LIST_LIST_ID = "List_ID";
    private static final String WALLPAPER_IN_LIST_WALLPAPER_ID = "Wallpaper_ID";

    private static final String wallpaperInListQuery =  "CREATE TABLE " + WALLPAPER_IN_LIST_TABLE_NAME + " (\n"+
            WALLPAPER_IN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
            WALLPAPER_IN_LIST_LIST_ID + " INTEGER NOT NULL,\n"+
            WALLPAPER_IN_LIST_WALLPAPER_ID + " TEXT NOT NULL\n"+
            ");";

    public SqliteConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(favoritesTableQuery);
        db.execSQL(wallpaperListsQuery);
        db.execSQL(wallpaperInListQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WALLPAPER_LISTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WALLPAPER_IN_LIST_TABLE_NAME);
        onCreate(db);
    }


    public void addFavorite(@NonNull String wallpaperID, @NonNull String dateTime,@NonNull String pictureType)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FAVORITES_WALLPAPER_ID_COLUMN,wallpaperID);
            contentValues.put(FAVORITES_DATE_COLUMN,dateTime);
            contentValues.put(FAVORITES_PICTURE_TYPE_COLUMN,pictureType);
            db.insert(FAVORITES_TABLE_NAME,null,contentValues);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        db.close();
    }

    public void removeFavorite(String wallpaperID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            String[] where ={wallpaperID};
            db.delete(FAVORITES_TABLE_NAME,FAVORITES_WALLPAPER_ID_COLUMN + "=?",where);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        db.close();
    }

    public List<String> getFavorites()
    {
        List<String> favoritesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            String[] selectQuery = {FAVORITES_ID_COLUMN,FAVORITES_WALLPAPER_ID_COLUMN};
            Cursor cursor = db.query(FAVORITES_TABLE_NAME,selectQuery,null,null,null,null,null);
            while (cursor.moveToNext())
            {
                favoritesList.add(cursor.getString(1));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            db.close();
        }
        return  favoritesList;
    }

    public long createNewList(@NonNull String listName) throws Exception {
        long createdID = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WALLPAPER_LISTS_LIST_NAME,listName);
            contentValues.put(WALLPAPER_LISTS_CREATE_DATE, MainActivity.getCurrentDateTime());
            createdID = db.insert(WALLPAPER_LISTS_TABLE_NAME,null,contentValues);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            createdID = -1;
        }
        finally {
            db.close();
            if (createdID < 1) throw new Exception();
            return createdID;
        }
    }

    public List<wallpaperListModel> getWallpaperLists()
    {
        List<wallpaperListModel> wallpaperLists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            String[] selectQuery = {WALLPAPER_LISTS_ID,WALLPAPER_LISTS_LIST_NAME,WALLPAPER_LISTS_CREATE_DATE};
            Cursor cursor = db.query(WALLPAPER_LISTS_LIST_NAME,selectQuery,null,null,null,null,null);
            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String listName = cursor.getString(1);
                wallpaperListModel list = new wallpaperListModel(listName,id);


                String[] childSelectQuery = {WALLPAPER_IN_LIST_ID,WALLPAPER_IN_LIST_WALLPAPER_ID};
                Cursor childCursor = db.query(WALLPAPER_IN_LIST_TABLE_NAME,childSelectQuery,null,null,null,null,null);

                List<wallpaperModel> childList = new ArrayList<>();
                while (cursor.moveToNext())
                {
                    int childID = childCursor.getInt(0);
                    String wallpaperID = childCursor.getString(1);

                    wallpaperModel model = new wallpaperModel(wallpaperID);

                    childList.add(model);
                }
                list.setModelList(childList);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            db.close();
        }
        return wallpaperLists;
    }

    public boolean addToList(@NonNull int ListID,@NonNull wallpaperModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WALLPAPER_IN_LIST_LIST_ID, ListID);
            contentValues.put(WALLPAPER_IN_LIST_WALLPAPER_ID, model.getId());
            db.insert(WALLPAPER_IN_LIST_TABLE_NAME,null,contentValues);

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

    }

}
