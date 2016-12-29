package com.aspirephile.pedifeed.android.db.async;

import android.database.SQLException;

public interface OnUpdateCompleteListener {
    void onUpdateComplete(int cursor, SQLException e);
}
