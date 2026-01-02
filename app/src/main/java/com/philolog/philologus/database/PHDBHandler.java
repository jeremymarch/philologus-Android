/*
  Copyright © 2017 Jeremy March. All rights reserved.

This file is part of philologus-Android.

    philologus-Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <https://www.gnu.org/licenses/>.

 */

package com.philolog.philologus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.philolog.philologus.SQLiteAssetHelper.SQLiteAssetHelper;
import java.nio.charset.StandardCharsets;

/**
 * Created by jeremy on 1/30/18.
 */

public class PHDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "philolog_us.db";

    /**
     * increment DATABASE_VERSION each time we need to copy db from assets again
     * To set it in sqlite: PRAGMA user_version = 3;
     * v1.1 = 1
     * v1.2 = 2
     * v1.3 = 3
     */
    private static final int DATABASE_VERSION = 3;

    private static PHDBHandler singleton;
    public static PHDBHandler getInstance(final Context context) {
        if (singleton == null) {
            singleton = new PHDBHandler(context);
            //Log.e("jwm", "db user_version 1: " + singleton.getWritableDatabase().getVersion());
            //singleton.getWritableDatabase().setVersion((3));
            Log.e("jwm", "db user_version: " + singleton.getWritableDatabase().getVersion());
        }
        return singleton;
    }

    public PHDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
        Log.e("jwm", "PHDBHandler: version " + DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //getNewPoems(mContext, db); //<<<<<<<<<< get the new poems when upgraded
        Log.e("jwm", "PHDBHandler: upgrade old " + oldVersion + " to new " + newVersion);
    }

    public synchronized String getDef(final long id) {
        String[] cols = { "def" };
        final SQLiteDatabase db = singleton.getReadableDatabase();
        try (Cursor cursor = db.query(Word.TABLE_NAME, cols,
                "_id" + " IS ?", new String[] { String.valueOf(id) },
                null, null, null, "1")) {

            byte[] def_blob = new byte[0];
            if (cursor.moveToFirst()) {
                def_blob = cursor.getBlob(0);
            }
            return new String(def_blob, StandardCharsets.UTF_8);
        }
    }
}
