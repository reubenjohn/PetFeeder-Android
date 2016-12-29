package com.aspirephile.pedifeed.android.db.async;

import android.database.SQLException;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

class AsyncInsertTask extends AsyncTask<InsertStatement, Void, Long> {
    private final OnInsertCompleteListener onInsertCompleteListener;
    private SQLException e;

    AsyncInsertTask(@NonNull OnInsertCompleteListener onInsertCompleteListener) {
        this.onInsertCompleteListener = onInsertCompleteListener;
    }

    @Override
    protected Long doInBackground(InsertStatement... params) {
        try {
            return params[0].execute();
        } catch (SQLException e) {
            this.e = e;
            return -1L;
        }
    }

    @Override
    protected void onPostExecute(Long rowId) {
        super.onPostExecute(rowId);
        onInsertCompleteListener.onInsertComplete(rowId, e);
    }
}
