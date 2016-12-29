package com.aspirephile.pedifeed.android.db.async;

import android.database.SQLException;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

class AsyncUpdateTask extends AsyncTask<UpdateStatement, Void, Integer> {
    private final OnUpdateCompleteListener onUpdateCompleteListener;
    private SQLException e;

    AsyncUpdateTask(@NonNull OnUpdateCompleteListener onUpdateCompleteListener) {
        this.onUpdateCompleteListener = onUpdateCompleteListener;
    }

    @Override
    protected Integer doInBackground(UpdateStatement... params) {
        try {
            return params[0].execute();
        } catch (SQLException e) {
            this.e = e;
            return -1;
        }
    }

    @Override
    protected void onPostExecute(Integer count) {
        super.onPostExecute(count);
        onUpdateCompleteListener.onUpdateComplete(count, e);
    }
}
