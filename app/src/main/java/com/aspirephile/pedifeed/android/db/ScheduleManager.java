package com.aspirephile.pedifeed.android.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;

import com.aspirephile.pedifeed.android.db.async.InsertStatement;
import com.aspirephile.pedifeed.android.db.async.QueryStatement;
import com.aspirephile.pedifeed.android.schedule.Schedule;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.aspirephile.pedifeed.android.db.Contract.Schedule.DATETIME;
import static com.aspirephile.pedifeed.android.db.Contract.Schedule.NAME;
import static com.aspirephile.pedifeed.android.db.Contract.Schedule.QUANTITY;
import static com.aspirephile.pedifeed.android.db.Contract.Schedule.TABLE_NAME;

public class ScheduleManager extends TableManager {
    ScheduleManager(Db dbHelper) {
        super(dbHelper);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.Schedule.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onDestroy(SQLiteDatabase db) {
        db.execSQL(Contract.Schedule.SQL_DELETE_ENTRIES);

    }

    public QueryStatement getListQuery() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        String query = qb.buildQuery(new String[]{
                        _ID,
                        NAME,
                        QUANTITY,
                        DATETIME},
                null,
                null,
                null,
                null,
                null);
        return new QueryStatement(dbHelper, query, null);
    }

    @NonNull
    public List<Schedule.Content> getListFromResult(@NonNull Cursor c) {
        ArrayList<Schedule.Content> list = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                Schedule.Content schedule = new Schedule.Content();
                schedule._id = c.getInt(0);
                schedule.name = c.getString(1);
                int quantity = c.getInt(2);
                schedule.setQuantity(quantity);
                long calendar = c.getLong(3);
                schedule.setEncodedCalendar(calendar);
                list.add(schedule);
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
                        DATETIME},
                Contract.Schedule._ID + "=?",
                null,
                null,
                null,
                null);
        return new QueryStatement(dbHelper, query, new String[]{String.valueOf(_id)});
    }

    public Schedule.Content getServiceFromResult(@NonNull Cursor c) {
        List<Schedule.Content> services = getListFromResult(c);
        if (services.size() > 0)
            return services.get(0);
        else
            return null;
    }

    public InsertStatement insert(Schedule.Content schedule) {
        return new InsertStatement(dbHelper, TABLE_NAME, schedule.getContentValues());
    }

    public void deleteSchedule(int id) {
        dbHelper.getWritableDatabase().execSQL("delete from Schedule where _id=?", new String[]{String.valueOf(id)});
    }
}
