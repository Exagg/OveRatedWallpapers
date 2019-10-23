package com.slice.wallpapers.DataAccessLayer;

import android.app.Application;

import com.slice.wallpapers.AcraReporter;

public class Startup extends Application {
    AcraReporter acraReporter = new AcraReporter();

    @Override
    public void onCreate() {
        super.onCreate();

        acraReporter.init(this,this);
        SqliteConnection.connection = new SqliteConnection(this);

    }
}
