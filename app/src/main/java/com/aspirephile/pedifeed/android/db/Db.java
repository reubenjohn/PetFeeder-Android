package com.aspirephile.pedifeed.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Reuben John on 9/9/2016.
 */
public class Db extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Db.db";
    private static Db dbHelper = null;

    private static ScheduleManager scheduleManager;
    private static RecordManager recordManager;

    public Db(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void initialize(@NonNull Context applicationContext) {
        dbHelper = new Db(applicationContext);
    }

    @NonNull
    public static ScheduleManager getScheduleManager() {
        if (scheduleManager != null)
            return scheduleManager;
        else
            return (scheduleManager = new ScheduleManager(dbHelper));
    }

    @NonNull
    public static RecordManager getRecordManager() {
        if (recordManager != null)
            return recordManager;
        else
            return (recordManager = new RecordManager(dbHelper));
    }

    public void onCreate(SQLiteDatabase db) {
        getScheduleManager().onCreate(db);
        getRecordManager().onCreate(db);

        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        String[] insertStatements = {
                //"insert into User (email,name) values (\"reubenvjohn@gmail.com\",\"Reuben John\");"
        };
        for (String sql :
                insertStatements) {
            Log.d("SampleDataInsertion", sql);

            db.execSQL(sql);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        getScheduleManager().onDestroy(db);
        getRecordManager().onDestroy(db);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}