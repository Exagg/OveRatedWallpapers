package com.example.hrwallpapers;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SpliceWallpaperBackgroundService extends Service {

    private static List<wallpaperModel> wallpaperList = new ArrayList<>();
    public static SpliceWallpaperBackgroundService activeService;
    private static Timer changerTimer = new Timer();
    private static final String TAG = "AutoWallpaperChanger";

    private static int lastIndex = 0;
    private static WallpaperManager wallpaperManager;

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            setWallpaper();
        }
    };

    public static void startService(Context context) {
        Intent intent = new Intent(context, SpliceWallpaperBackgroundService.class);
        context.startService(intent);
    }

    public static void restoreService(Context context) {
        Intent intent = new Intent(context, SpliceWallpaperBackgroundService.class);
        context.startService(intent);
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
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();

        wallpaperList = MainActivity.findAllexistFileAsModel();
        wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        startSchedule();
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

    private void startSchedule()
    {
        changerTimer.schedule(task,1000,1000);
    }


    private void setWallpaper() {
        if (wallpaperList != null && wallpaperList.size() != 0)
        {
            if (wallpaperList.size() - 1 == lastIndex) lastIndex=0;

            wallpaperModel model = wallpaperList.get(lastIndex);
            File file = MainActivity.findExistFie(model.HQFileName);

            if (file.exists())
            {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                try
                {
                    wallpaperManager.setBitmap(bitmap);
                    lastIndex++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Log.i(TAG, "setWallpaper: Wallpaper is not exist on folder. So wallpaper cants change to it.");
            }
        }
    }

}
