package com.aspirephile.pedifeed.android.db.async;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.aspirephile.pedifeed.android.db.Db;

public class InsertStatement {
    public final ContentValues values;
    private final Db dbHelper;
    private final String tableName;
    private final String nullColumnHack;

    public InsertStatement(Db dbHelper, String tableName, ContentValues values) {
        this.dbHelper = dbHelper;
        this.tableName = tableName;
        this.values = values;
        this.nullColumnHack = null;
    }

    public long execute() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(tableName, nullColumnHack, values);
        return rowId;
    }

    public void executeInBackground(OnInsertCompleteListener onInsertCompleteListener) {
        AsyncInsertTask asyncInsertTask = new AsyncInsertTask(onInsertCompleteListener);
        asyncInsertTask.execute(this);
    }
}
