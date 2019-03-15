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

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class Word {
	public static final int LANG_GREEK = 0;
	public static final int LANG_LATIN = 1;

	public static final String LATIN_TABLE_NAME = "ZLATINWORDS";
	public static final String GREEK_TABLE_NAME = "ZGREEKWORDS";
    public static final String LATIN_DEF_TABLE_NAME = "ZLATINDEFS";
    public static final String GREEK_DEF_TABLE_NAME = "ZGREEKDEFS";
    public static String TABLE_NAME = GREEK_TABLE_NAME;
    public static String DEF_TABLE_NAME = GREEK_DEF_TABLE_NAME;
	// Naming the id column with an underscore is good to be consistent
	// with other Android things. This is ALWAYS needed
	//must be called _id : https://stackoverflow.com/questions/5812030/java-lang-illegalargumentexception-column-id-does-not-exist
	public static final String COL_ID = "_id";
	public static final String COL_WORD = "ZWORD";
	public static final String COL_UNACCENTED_WORD = "ZUNACCENTEDWORD";

	// For database projection so order is consistent
	public static final String[] GREEK_FIELDS = { COL_ID, COL_WORD };
	public static final String[] LATIN_FIELDS = { COL_ID, COL_UNACCENTED_WORD };
	public static String[] FIELDS = GREEK_FIELDS;

	// Fields corresponding to database columns
	public long id = -1;
	public String word = "";

	/**
	 * No need to do anything, fields are already set to default values above
	 */
	public Word() {
	}

	/**
	 * Convert information from the database into a Word object.
	 */
	public Word(final Cursor cursor) {
		Log.e("abc", "word from cursor");
		// Indices expected to match order in FIELDS!
		this.id = cursor.getLong(0);
		this.word = cursor.getString(1);
	}

	/**
	 * Return the fields in a ContentValues object, suitable for insertion
	 * into the database.
	 */
	public ContentValues getContent() {
		final ContentValues values = new ContentValues();
		// Note that ID is NOT included here
		values.put(COL_WORD, word);

		return values;
	}
}
