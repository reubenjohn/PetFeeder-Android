package com.aspirephile.pedifeed.android.db.async;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aspirephile.pedifeed.android.db.Db;

public class QueryStatement {
    private final Db dbHelper;
    private final String query;
    private final String[] selectionArgs;

    public QueryStatement(Db dbHelper, String sql, String[] selectionArgs) {
        this.dbHelper = dbHelper;
        this.query = sql;
        this.selectionArgs = selectionArgs;
    }

    public Cursor execute() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(query, selectionArgs);
    }

    public void queryInBackground(OnQueryCompleteListener onQueryCompleteListener) {
        AsyncQueryTask asyncQueryTask = new AsyncQueryTask(onQueryCompleteListener);
        asyncQueryTask.execute(this);
    }
}
