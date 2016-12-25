package com.aspirephile.petfeeder.android;

import android.app.Application;

import com.aspirephile.petfeeder.android.db.Db;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Db.initialize(getApplicationContext());
    }
}