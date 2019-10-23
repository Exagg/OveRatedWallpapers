package com.slice.wallpapers;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.slice.wallpapers.DataAccessLayer.SqliteConnection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SliceWallpaperBackgroundService extends Service{

    private static List<File> wallpaperList = new ArrayList<>();
    public static SliceWallpaperBackgroundService activeService;
    private static Timer changerTimer = new Timer();
    private static final String TAG = "AutoWallpaperChanger";
    private static SharedPreferences sharedPreferences;
    private static boolean isServiceActive = false;
    private static int interval = 0;
    private static boolean isFolder = false;
    private static boolean isList = false;

    private static int lastIndex = 0;
    private static WallpaperManager wallpaperManager;

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            setWallpaper();
        }
    };

    private static int width;
    private static int height;

    public static Intent startService(Context context) {
        Intent intent = new Intent(context, SliceWallpaperBackgroundService.class);
        context.startService(intent);
        return intent;
    }

    public static Intent restoreService(Context context) {
        Intent intent = new Intent(context, SliceWallpaperBackgroundService.class);
        context.startService(intent);
        return intent;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activeService = this;
        run();
    }

    public void stop()
    {
        changerTimer.cancel();
    }

    public void run() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();

        wm.getDefaultDisplay().getMetrics(displayMetrics);

        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        wallpaperList.clear();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isServiceActive = sharedPreferences.getBoolean(AutoWallpaperFragment.IsEnabledKey,false);
        if (isServiceActive)
        {

            //This area will works with defined folder and list. if there is no list or folder, function will pass.
            /* TODO
             * Check folder and list
             * if wallpaper in the list not exists main folder, try download it.
             * if selection as folder, scan images in the selected folder and use it.
             * Work with the selected interval value, not default!!.
             * */

            interval = sharedPreferences.getInt(AutoWallpaperFragment.IntervalKey,0);
            isFolder = sharedPreferences.getBoolean(AutoWallpaperFragment.IsFolderActiveKey,false);
            isList = sharedPreferences.getBoolean(AutoWallpaperFragment.IsListActiveKey,false);

            if (isList || isFolder)
            {
                getAllBitmaps(isList,isFolder);

                wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                int delay = interval * 60 * 1000;
                startSchedule(delay);
            }
        }
    }

    private void getAllBitmaps(boolean isList, boolean isFolder) {
        wallpaperList.clear();
        if (isFolder)
        {
            String folderFilter = SqliteConnection.WALLPAPER_IN_FOLDER_IS_ACTIVE + "=?";
            String[] folderFilterArgs = new String[]{String.valueOf(1)}; // if this are value is 1 then its selected.Explantation in sqliteconnection.
            List<FolderModel> activeFolders = SqliteConnection.connection.getAllFolders(folderFilter,folderFilterArgs);

            for (FolderModel model: activeFolders
            ) {
                List<File> foundedImages = findAllImagesFromFolder(model.getFolderPath(),model.getFolderName());
                wallpaperList.addAll(foundedImages);
            }
        }
        else
        {
            String listFilter = SqliteConnection.WALLPAPER_LISTS_IS_ACTIVE + "=?";
            String[] listFilerArgs = new String[]{String.valueOf(1)}; // if this are value is 1 then its selected.Explantation in sqliteconnection.
            List<wallpaperListModel> activeWallpaperLists = SqliteConnection.connection.getWallpaperLists(listFilter,listFilerArgs);

            for (wallpaperListModel listModel:activeWallpaperLists
            ) {
                for (wallpaperModel model : listModel.getWallpaperModels()
                ) {
                    List<File> foundedImages = findAllDownloadedImages(model.getFolderPath(),model.HQFileName);

                    wallpaperList.addAll(foundedImages);;
                }
            }
        }
    }

    private List<File> findAllImagesFromFolder(String filePath, String fileName) {
        List<File> foundedImages = new ArrayList<>();
        fileName = fileName.replace("primary:","").replaceAll(":",File.separator);

        Log.i(TAG, "findAllImagesFromFolder: Main Folder : " + filePath + File.separator+fileName);
        File folder = new File(filePath + File.separator + fileName);
        if (folder != null)
        {
            if (folder.list() != null )
            {
                for (File subFile: folder.listFiles()
                ) {
                    Log.i(TAG, "findAllImagesFromFolder: SubFolder : " + subFile.getName());
                    if (subFile.getAbsolutePath().endsWith("jpg") || subFile.getAbsolutePath().endsWith("png"))
                    {
                        foundedImages.add(subFile);
                    }
                }
            }
            else
            {
                Log.i(TAG, "findAllImagesFromFolder: Listfiles are null");
            }
        }
        return foundedImages;
    }

    private List<File> findAllDownloadedImages(String filePath, String fileName) {
        List<File> foundedImages = new ArrayList<>();

        
        fileName = fileName.replace("primary:","").replaceAll(":",File.separator);

        Log.i(TAG, "findAllImagesFromFolder: File : " + filePath + File.separator+fileName);
        File file = new File(filePath + File.separator + fileName);
        if (file != null)
        {
            if (file.getAbsolutePath().endsWith("jpg") || file.getAbsolutePath().endsWith("png"))
            {
                foundedImages.add(file);
            }
            else
            {
                Log.i(TAG, "findAllImagesFromFolder: Listfiles are null");
            }
        }
        return foundedImages;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent receiverIntent = new Intent("android.intent.action.wallpaper.SERVICE_STOPPED");
        sendBroadcast(receiverIntent);
    }

    private void startSchedule(@NonNull int delay)
    {
        changerTimer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                setWallpaper();
            }
        };
        changerTimer.scheduleAtFixedRate(task,0,delay);
    }


    private void setWallpaper() {
        if (wallpaperList != null && wallpaperList.size() > 0)
        {
            if (wallpaperList.size() <= lastIndex) lastIndex=0;
            try
            {
                Log.i(TAG, "setWallpaper: Wallpaper is changed as ->" + wallpaperList.get(lastIndex).getAbsolutePath() + ". List Size :" + wallpaperList.size() + " Last Index:" + lastIndex);
                Bitmap bitmap = BitmapFactory.decodeFile(wallpaperList.get(lastIndex).getAbsolutePath());

                if (width + 100 < bitmap.getWidth())
                {
                    //This wallpaper not fit to screen.So it will be corp as centered and then set
                    int paddingLeftAndRight = (bitmap.getWidth() - width) / 2;
                    bitmap = Bitmap.createBitmap(bitmap,paddingLeftAndRight,0,width,bitmap.getHeight());
                }
                if(bitmap != null)
                {
                    wallpaperManager.setBitmap(bitmap);
                }

                lastIndex++;
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

}
