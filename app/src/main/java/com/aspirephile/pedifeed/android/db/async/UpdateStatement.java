package com.aspirephile.pedifeed.android.db.async;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.aspirephile.pedifeed.android.db.Db;

public class UpdateStatement {
    public final ContentValues values;
    private final Db dbHelper;
    private final String tableName;
    private final String whereClause;
    private final String[] whereArgs;

    public UpdateStatement(Db dbHelper, String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        this.dbHelper = dbHelper;
        this.tableName = tableName;
        this.values = values;
        this.whereClause = whereClause;
        this.whereArgs = whereArgs;
    }

    public int execute() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(tableName, values, whereClause, whereArgs);
        return count;
    }

    public void executeInBackground(OnUpdateCompleteListener onQueryCompleteListener) {
        AsyncUpdateTask asyncUpdateTask = new AsyncUpdateTask(onQueryCompleteListener);
        asyncUpdateTask.execute(this);
    }
}
