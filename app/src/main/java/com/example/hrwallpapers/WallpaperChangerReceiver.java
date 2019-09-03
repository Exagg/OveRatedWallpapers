package com.example.hrwallpapers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WallpaperChangerReceiver extends BroadcastReceiver {

    private static String TAG = "WallpaperChangeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: Progress has been stopped.!! it will be run again");
        SpliceWallpaperBackgroundService.startService(context);
    }
}
