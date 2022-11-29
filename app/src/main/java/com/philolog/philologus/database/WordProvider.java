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


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.util.Log;

import com.philolog.philologus.PHSimpleCursorAdapter;

public class WordProvider extends ContentProvider {

	// All URIs share these parts
	public static final String AUTHORITY = "com.philolog.philologus.provider";
	public static final String SCHEME = "content://";

	// URIs
	public static final String GREEK_WORDS = SCHEME + AUTHORITY + "/gword";
	public static final Uri GREEK_URI_WORDS = Uri.parse(GREEK_WORDS);
	public static final String GREEK_WORD_BASE = GREEK_WORDS + "/";

	public static final String LATIN_WORDS = SCHEME + AUTHORITY + "/lword";
	public static final Uri LATIN_URI_WORDS = Uri.parse(LATIN_WORDS);
	public static final String LATIN_WORD_BASE = LATIN_WORDS + "/";

    public static final int GREEK_MAX_ID = 116655;
    public static final int LATIN_MAX_ID = 51675;
    public static int LANG_MAX_ID = GREEK_MAX_ID;

	public static Uri URI_WORDS = GREEK_URI_WORDS;

    public  static int selectedSeq = 1;

	public WordProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Delete Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("getType Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Implement this to handle requests to insert a new row.
		throw new UnsupportedOperationException("Insert Not yet implemented");
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor result = null;
        Cursor seqResult = null;
		if (GREEK_URI_WORDS.equals(uri) || LATIN_URI_WORDS.equals(uri))
		{
					result = PHDBHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Word.TABLE_NAME, Word.FIELDS, null, null, null,
							null, Word.COL_ID, String.valueOf(PHSimpleCursorAdapter.pageSize*2));
			selectedSeq = 1;
			result.setNotificationUri(getContext().getContentResolver(), URI_WORDS);
		}
		else if (uri.toString().startsWith(GREEK_WORD_BASE) || uri.toString().startsWith(LATIN_WORD_BASE))
		{
			final String wordPrefix = uri.getLastPathSegment();
			String[] cols = { "_id" };
            seqResult = PHDBHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Word.TABLE_NAME, cols,
							"sortword >= ?",
							new String[] { wordPrefix }, null, null,
                            "sortword", "1");

            int seq;

            if (seqResult.getCount() > 0)
            {
                seqResult.moveToFirst();
                seq = seqResult.getInt(0);
                seqResult.close();
            }
            else
            {
                seq = LANG_MAX_ID;
            }

            int beforePageSize;
            int afterPageSize;
            int startRequestSeq;
            int fullRequestSize;

            if (seq <= PHSimpleCursorAdapter.pageSize)
			{
                beforePageSize = seq - 1;
                afterPageSize = PHSimpleCursorAdapter.pageSize;
                startRequestSeq = 1;
				fullRequestSize = beforePageSize + afterPageSize + 1;
				selectedSeq = seq;
			}
			else if (seq + PHSimpleCursorAdapter.pageSize >= LANG_MAX_ID)
			{
                beforePageSize = PHSimpleCursorAdapter.pageSize;
                afterPageSize = LANG_MAX_ID - seq;
                startRequestSeq = seq - PHSimpleCursorAdapter.pageSize;
                fullRequestSize = beforePageSize + afterPageSize + 1;
                selectedSeq = PHSimpleCursorAdapter.pageSize + 1;
			}
            else
            {
                beforePageSize = PHSimpleCursorAdapter.pageSize;
                afterPageSize = PHSimpleCursorAdapter.pageSize;
                startRequestSeq = seq - PHSimpleCursorAdapter.pageSize;
                fullRequestSize = beforePageSize + afterPageSize + 1;
                selectedSeq = PHSimpleCursorAdapter.pageSize + 1;
            }

            //Log.e("abc", "before: " + beforePageSize + ", after: " + afterPageSize + ", startid: " +startRequestSeq + ", selectedid: " + selectedSeq);

            result = PHDBHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
                    .query(Word.TABLE_NAME, Word.FIELDS,
                            "_id >= ?",
                            new String[] { String.valueOf(startRequestSeq) }, null, null,
                            "_id", String.valueOf(fullRequestSize));

			result.setNotificationUri(getContext().getContentResolver(), URI_WORDS);
		}
		else
        {
			throw new UnsupportedOperationException("query Not yet implemented");
		}

		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
