package com.aspirephile.pedifeed.android.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;

import com.aspirephile.pedifeed.android.db.async.InsertStatement;
import com.aspirephile.pedifeed.android.db.async.QueryStatement;
import com.aspirephile.pedifeed.android.record.Record.Content;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.aspirephile.pedifeed.android.db.Contract.Record.NAME;
import static com.aspirephile.pedifeed.android.db.Contract.Record.QUANTITY;
import static com.aspirephile.pedifeed.android.db.Contract.Record.TABLE_NAME;
import static com.aspirephile.pedifeed.android.db.Contract.Record.TIMESTAMP;

public class RecordManager extends TableManager {
    RecordManager(Db dbHelper) {
        super(dbHelper);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.Record.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onDestroy(SQLiteDatabase db) {
        db.execSQL(Contract.Record.SQL_DELETE_ENTRIES);

    }

    public QueryStatement getListQuery() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        String query = qb.buildQuery(new String[]{
                        _ID,
                        NAME,
                        QUANTITY,
                        TIMESTAMP},
                null,
                null,
                null,
                null,
                null);
        return new QueryStatement(dbHelper, query, null);
    }

    @NonNull
    public List<Content> getListFromResult(@NonNull Cursor c) {
        ArrayList<Content> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Content item = new Content();
                item._id = c.getInt(0);
                item.name = c.getString(1);
                item.quantity = c.getInt(2);
                item.timestamp = c.getInt(3);
                list.add(item);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public QueryStatement getService(long _id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        String query = qb.buildQuery(new String[]{
                        _ID,
                        NAME,
                        QUANTITY,
                        TIMESTAMP},
                Contract.Record._ID + "=?",
                null,
                null,
                null,
                null);
        return new QueryStatement(dbHelper, query, new String[]{String.valueOf(_id)});
    }

    public Content getServiceFromResult(@NonNull Cursor c) {
        List<Content> services = getListFromResult(c);
        if (services.size() > 0)
            return services.get(0);
        else
            return null;
    }

    public InsertStatement insert(Content item) {
        return new InsertStatement(dbHelper, TABLE_NAME, item.getContentValues());
    }
}
