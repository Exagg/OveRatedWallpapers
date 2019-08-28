package com.example.hrwallpapers.DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SqliteConnection extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
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

    public SqliteConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(favoritesTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE_NAME);
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
        return  favoritesList;
    }
}
