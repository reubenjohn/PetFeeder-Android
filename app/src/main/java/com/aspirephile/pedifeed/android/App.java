package com.aspirephile.pedifeed.android;

import android.app.Application;

import com.aspirephile.pedifeed.android.db.Db;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Db.initialize(getApplicationContext());
    }
}