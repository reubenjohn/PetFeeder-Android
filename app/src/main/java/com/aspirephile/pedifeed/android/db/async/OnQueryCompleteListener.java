package com.aspirephile.pedifeed.android.db.async;

import android.database.Cursor;
import android.database.SQLException;

public interface OnQueryCompleteListener {
    void onQueryComplete(Cursor c, SQLException e);
}
