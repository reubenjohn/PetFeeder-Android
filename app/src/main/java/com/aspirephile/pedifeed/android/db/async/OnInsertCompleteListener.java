package com.aspirephile.pedifeed.android.db.async;

import android.database.SQLException;

public interface OnInsertCompleteListener {
    void onInsertComplete(long rowId, SQLException e);
}
