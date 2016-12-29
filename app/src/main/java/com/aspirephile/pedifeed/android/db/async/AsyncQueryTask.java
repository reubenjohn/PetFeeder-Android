package com.aspirephile.pedifeed.android.db.async;

import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

class AsyncQueryTask extends AsyncTask<QueryStatement, Void, Cursor> {
    private final OnQueryCompleteListener onQueryCompleteListener;
    private SQLException e;

    AsyncQueryTask(@NonNull OnQueryCompleteListener onQueryCompleteListener) {
        this.onQueryCompleteListener = onQueryCompleteListener;
    }

    @Override
    protected Cursor doInBackground(QueryStatement... params) {
        try {
            return params[0].execute();
        } catch (SQLException e) {
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        if (cursor != null)
            Log.d("AsyncQueryTask", "Retrieved " + cursor.getCount() + " rows, with " + cursor.getColumnCount() + " columns");
        onQueryCompleteListener.onQueryComplete(cursor, e);
    }
}
