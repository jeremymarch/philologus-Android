package com.philolog.philologus.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class Word {
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

	// For database projection so order is consistent
	public static final String[] FIELDS = { COL_ID, COL_WORD };

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
