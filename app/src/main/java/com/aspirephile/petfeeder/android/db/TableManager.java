package com.aspirephile.petfeeder.android.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Reuben John on 9/24/2016.
 */
public abstract class TableManager {
    protected final Db dbHelper;

    public TableManager(Db dbHelper) {
        this.dbHelper = dbHelper;
    }

    public abstract void onCreate(SQLiteDatabase db);

    public abstract void onDestroy(SQLiteDatabase db);
}
