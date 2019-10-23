package com.slice.wallpapers.DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.slice.wallpapers.DownloadImageAsync;
import com.slice.wallpapers.FolderModel;
import com.slice.wallpapers.MainActivity;
import com.slice.wallpapers.wallpaperListModel;
import com.slice.wallpapers.wallpaperModel;

import java.util.ArrayList;
import java.util.List;

public class SqliteConnection extends SQLiteOpenHelper {

    public static SqliteConnection connection;
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "Wallpapers";
    private static final String TAG ="SqliteConnection";




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

    public static final String WALLPAPER_LISTS_ID = "ID";
    public static final String WALLPAPER_LISTS_TABLE_NAME = "Lists";
    public static final String WALLPAPER_LISTS_LIST_NAME = "Name";
    public static final String WALLPAPER_LISTS_CREATE_DATE = "Create_Date";
    public static final String WALLPAPER_LISTS_IS_ACTIVE = "Is_Active"; // Default value is 0, this area type is will be integer

    private static final String wallpaperListsQuery = "CREATE TABLE " + WALLPAPER_LISTS_TABLE_NAME + " (\n"+
            WALLPAPER_LISTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
            WALLPAPER_LISTS_LIST_NAME + " TEXT NOT NULL,\n"+
            WALLPAPER_LISTS_CREATE_DATE + " TEXT NOT NULL,\n"+
            WALLPAPER_LISTS_IS_ACTIVE + " INTEGER DEFAULT 0 \n"+
            ");";


    private static final String WALLPAPER_IN_LIST_TABLE_NAME = "Wallpaper_In_List";
    private static final String WALLPAPER_IN_LIST_ID = "ID";
    private static final String WALLPAPER_IN_LIST_LIST_ID = "List_ID";
    private static final String WALLPAPER_IN_LIST_WALLPAPER_ID = "Wallpaper_ID";
    private static final String WALLPAPER_IN_LIST_WALLPAPER_ISPNG = "Is_Png";// Default value is 0, this area type is will be integer

    private static final String wallpaperInListQuery =  "CREATE TABLE " + WALLPAPER_IN_LIST_TABLE_NAME + " (\n"+
            WALLPAPER_IN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
            WALLPAPER_IN_LIST_LIST_ID + " INTEGER NOT NULL,\n"+
            WALLPAPER_IN_LIST_WALLPAPER_ID + " TEXT NOT NULL,\n"+
            WALLPAPER_IN_LIST_WALLPAPER_ISPNG + " INTEGER NOT NULL\n"+
            ");";


    public static final String WALLPAPER_IN_FOLDER_TABLE_NAME = "Folders";
    public static final String WALLPAPER_IN_FOLDER_ID = "ID";
    public static final String WALLPAPER_IN_FOLDER_PATH = "Folder_Path";
    public static final String WALLPAPER_IN_FOLDER_NAME = "Folder_Name";
    public static final String WALLPAPER_IN_FOLDER_IS_ACTIVE = "Is_Active"; // Default value is 0, this area type is will be integer

    private static final String wallpaperInFolderQuery =  "CREATE TABLE " + WALLPAPER_IN_FOLDER_TABLE_NAME + " (\n"+
            WALLPAPER_IN_FOLDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
            WALLPAPER_IN_FOLDER_PATH + " TEXT NOT NULL,\n"+
            WALLPAPER_IN_FOLDER_NAME + " TEXT NOT NULL,\n"+
            WALLPAPER_IN_FOLDER_IS_ACTIVE + " INTEGER DEFAULT 0 \n"+
            ");";

    public SqliteConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(favoritesTableQuery);
        db.execSQL(wallpaperListsQuery);
        db.execSQL(wallpaperInListQuery);
        db.execSQL(wallpaperInFolderQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WALLPAPER_LISTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WALLPAPER_IN_LIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WALLPAPER_IN_FOLDER_TABLE_NAME);
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

    public long createNewList(@NonNull String listName) {
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
            return createdID;
        }
    }

    public List<wallpaperListModel> getWallpaperLists(@Nullable String selection,@Nullable String[] selectionArgs)
    {
        List<wallpaperListModel> wallpaperLists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            String[] selectQuery = {WALLPAPER_LISTS_ID,WALLPAPER_LISTS_LIST_NAME,WALLPAPER_LISTS_CREATE_DATE,WALLPAPER_LISTS_IS_ACTIVE};
            Cursor cursor = db.query(WALLPAPER_LISTS_TABLE_NAME,selectQuery,selection,selectionArgs,null,null,null,null);
            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String listName = cursor.getString(1);
                int isActive = cursor.getInt(3);
                wallpaperListModel list = new wallpaperListModel(listName,id,isActive == 0 ? false : true);

                String[] childSelectQuery = {WALLPAPER_IN_LIST_ID,WALLPAPER_IN_LIST_WALLPAPER_ID,WALLPAPER_IN_LIST_WALLPAPER_ISPNG};
                Cursor childCursor = db.query(WALLPAPER_IN_LIST_TABLE_NAME,childSelectQuery,WALLPAPER_IN_LIST_LIST_ID + "=?",new String[]{String.valueOf(id)},null,null,null);

                List<wallpaperModel> childList = new ArrayList<>();
                while (childCursor.moveToNext())
                {
                    int childID = childCursor.getInt(0);
                    String wallpaperID = childCursor.getString(1);
                    boolean isPng = childCursor.getInt(2) == 1 ? true : false;
                    final wallpaperModel model = new wallpaperModel(wallpaperID);
                    model.setIsPng(isPng);
                    if (!MainActivity.isFileExists(model.HQFileName))
                    {
                        DownloadImageAsync downloadImageAsync = new DownloadImageAsync();
                        downloadImageAsync.setTaskFisinhed(new DownloadImageAsync.onTaskFinished() {
                            @Override
                            public void Downloading(int percentage) {
                            }

                            @Override
                            public void Finished(String imagePath) {
                                model.setImageFile(MainActivity.findExistFie(model.HQFileName));
                                Log.i(TAG, "Finished: " + model.id + " is downloaded");
                            }
                        });
                        downloadImageAsync.execute(model);
                    }
                    else{
                        model.setImageFile(MainActivity.findExistFie(model.HQFileName));
                    }

                    childList.add(model);
                }
                list.setModelList(childList);

                wallpaperLists.add(list);
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
            contentValues.put(WALLPAPER_IN_LIST_WALLPAPER_ISPNG,model.isPng() ? 1 : 0);
            db.insert(WALLPAPER_IN_LIST_TABLE_NAME,null,contentValues);
            Log.i(TAG, "addToList: " + model.getId() + " - " + model.isPng());
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally {
            db.close();
        }
    }

    public boolean isWallpaperAddtoList (@NonNull int ListID,@NonNull wallpaperModel model)
    {
        SQLiteDatabase database =this.getReadableDatabase();
        try
        {
            String[] selectQuery = {WALLPAPER_IN_LIST_ID};
            Cursor cursor = database.query(WALLPAPER_IN_LIST_TABLE_NAME,selectQuery,
                    WALLPAPER_IN_LIST_LIST_ID + "=? AND " + WALLPAPER_IN_LIST_WALLPAPER_ID + "=?",
                    new String[]{String.valueOf(ListID),model.getId()},null,null,null);
            if (cursor.getCount() > 0) return true;
            else return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally {
            database.close();
        }
    }

    public boolean isWallpaperListCreatedBefore (@NonNull String listName)
    {
        SQLiteDatabase database =this.getReadableDatabase();
        try
        {
            String[] selectQuery = {WALLPAPER_LISTS_ID};
            Cursor cursor = database.query(WALLPAPER_LISTS_TABLE_NAME,selectQuery,
                    WALLPAPER_LISTS_LIST_NAME + "=?",
                    new String[]{listName},null,null,null);
            if (cursor.getCount() > 0) return true;
            else return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally {
            database.close();
        }
    }

    public boolean deleteFromList(@NonNull int ListID,@NonNull wallpaperModel model)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String[] where ={String.valueOf(ListID),model.getId()};
            db.delete(WALLPAPER_IN_LIST_TABLE_NAME,WALLPAPER_IN_LIST_LIST_ID + "=? AND " + WALLPAPER_IN_LIST_WALLPAPER_ID + "=?",where);
            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        finally {
            db.close();
        }
    }

    public boolean deleteList(@NonNull int ListID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            String[] where ={String.valueOf(ListID)};
            db.delete(WALLPAPER_LISTS_TABLE_NAME,WALLPAPER_LISTS_ID + "=?",where);
            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        finally {
            db.close();
        }
    }

    public boolean setActiveList(@NonNull int ListID,@NonNull boolean newState)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WALLPAPER_LISTS_IS_ACTIVE,newState == true ? 1 : 0);
            database.update(WALLPAPER_LISTS_TABLE_NAME,contentValues,
                    WALLPAPER_LISTS_ID + "=?",new String[]{String.valueOf(ListID)});
            isListActive(ListID);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally {
            database.close();
        }
    }

    public boolean isListActive(@NonNull int ListID) {
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            String[] selectQuery = new String[]{WALLPAPER_LISTS_ID,WALLPAPER_LISTS_IS_ACTIVE};
            Cursor cursor = database.query(WALLPAPER_LISTS_TABLE_NAME, selectQuery, WALLPAPER_LISTS_ID + "=?", new String[]{String.valueOf(ListID)},
                    null, null, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                if (cursor.getInt(1) == 1) return true;
                else return false;
            }else
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            database.close();
        }
    }

    /*
    * This selection and selection args will be filter the result.
    * If develepor want to get all folder, should send as null.
    * */
    public List<FolderModel> getAllFolders(@Nullable String selection,@Nullable String[] selectionArgs)
    {
        SQLiteDatabase database = this.getReadableDatabase();
        List<FolderModel> list = new ArrayList<>();
        try{
            String[] columns = new String[]{WALLPAPER_IN_FOLDER_ID,WALLPAPER_IN_FOLDER_PATH,WALLPAPER_IN_FOLDER_NAME,WALLPAPER_IN_FOLDER_IS_ACTIVE};
            Cursor cursor = database.query(WALLPAPER_IN_FOLDER_TABLE_NAME,columns,selection,selectionArgs,null,null,null);

            while (cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String folderPath = cursor.getString(1);
                String folderName = cursor.getString(2);
                boolean isActive = cursor.getInt(3) == 0 ? false : true;

                FolderModel model = new FolderModel(id,folderPath,folderName,isActive);
                list.add(model);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            database.close();
            return list;
        }
    }

    public boolean setActiveFolder(@NonNull int FolderID, @NonNull boolean newState)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WALLPAPER_IN_FOLDER_IS_ACTIVE,newState == true ? 1 : 0);
            database.update(WALLPAPER_IN_FOLDER_TABLE_NAME,contentValues,
                    WALLPAPER_IN_FOLDER_ID + "=?",new String[]{String.valueOf(FolderID)});

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally {
            database.close();
        }
    }

    public boolean addFolder(@NonNull String folderName,@NonNull String folderPath)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WALLPAPER_IN_FOLDER_NAME,folderName);
            contentValues.put(WALLPAPER_IN_FOLDER_PATH,folderPath);
            long id = database.insert(WALLPAPER_IN_FOLDER_TABLE_NAME,null,contentValues);
            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        finally {
            database.close();
        }
    }

    public boolean isFolderAddedBefore(@NonNull String folderDest,@NonNull String folderName) {
        SQLiteDatabase database =this.getReadableDatabase();
        try
        {
            String[] selectQuery = {WALLPAPER_IN_FOLDER_ID};
            Cursor cursor = database.query(WALLPAPER_IN_FOLDER_TABLE_NAME,selectQuery,
                    WALLPAPER_IN_FOLDER_NAME + "=? AND " + WALLPAPER_IN_FOLDER_PATH + "=?",
                    new String[]{folderName,folderDest},null,null,null);
            if (cursor.getCount() > 0) return true;
            else return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally {
            database.close();
        }
    }

    public boolean deleteFolder(@NonNull int folderID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String[] where = {String.valueOf(folderID)};
            db.delete(WALLPAPER_IN_FOLDER_TABLE_NAME, WALLPAPER_IN_FOLDER_ID + "=?", where);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }
}
