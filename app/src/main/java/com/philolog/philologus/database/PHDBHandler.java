package com.philolog.philologus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
/**
 * Created by jeremy on 1/30/18.
 */

public class PHDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "philolog_us.db";
    private static final int DATABASE_VERSION = 1;

    private static PHDBHandler singleton;

    public static PHDBHandler getInstance(final Context context) {
        if (singleton == null) {
            singleton = new PHDBHandler(context);
        }
        return singleton;
    }

    public PHDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int scrollTo(String wordPrefix)
    {
        String[] selectionArgs = { wordPrefix };
        String[] columns = {"ZSEQ"};
        String groupBy = null;
        String having = null;
        String orderBy = "zunaccentedword";
        String limit = "1";

        Cursor cursor = singleton.getReadableDatabase().query(Word.TABLE_NAME, columns, "zunaccentedword>=?", selectionArgs, groupBy, having, orderBy, limit);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            int seq = cursor.getInt(0);
            //Log.e("seq", String.valueOf(seq));
            return seq;
        }
        else
        {
            //past last word, return the last + 1 because positions are zero-indexed
            long count = DatabaseUtils.queryNumEntries(singleton.getReadableDatabase(), Word.TABLE_NAME);
            return ((int)count + 1);
        }
    }

    public synchronized Word getWord(final long id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(Word.TABLE_NAME, Word.FIELDS,
                Word.COL_ID + " IS ?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor == null || cursor.isAfterLast()) {
            return null;
        }

        Word item = null;
        if (cursor.moveToFirst()) {
            item = new Word(cursor);
        }
        cursor.close();
        return item;
    }

    public synchronized String getDef(final long id) {
        String[] cols = { "ZDEF" };
        final SQLiteDatabase db = singleton.getReadableDatabase();
        final Cursor cursor = db.query(Word.DEF_TABLE_NAME, cols,
                "ZWORDID" + " IS ?", new String[] { String.valueOf(id) },
                null, null, null, "1");

        //Word item = null;
        String def = "";
        if (cursor != null && cursor.moveToFirst()) {
            def = cursor.getString(0);
            //Log.e("abc", "def: " + def);
        }
        cursor.close();
        return def;
    }
}
