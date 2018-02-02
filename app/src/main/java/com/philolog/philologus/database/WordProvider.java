package com.philolog.philologus.database;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

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

	public static Uri URI_WORDS = GREEK_URI_WORDS;

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

		Log.e("abc", "create greek provider");
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor result = null;
		Log.e("abc", "GREEK query uri: " + uri + ", " + GREEK_URI_WORDS);
		if (GREEK_URI_WORDS.equals(uri)) {
			Log.e("abc", "query 1");
					result = PHDBHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Word.GREEK_TABLE_NAME, Word.FIELDS, null, null, null,
							null, null, null);
			result.setNotificationUri(getContext().getContentResolver(), GREEK_URI_WORDS);
		} else if (uri.toString().startsWith(GREEK_WORD_BASE)) {
			Log.e("abc", "query 2");
			final long id = Long.parseLong(uri.getLastPathSegment());
			result = PHDBHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Word.GREEK_TABLE_NAME, Word.FIELDS,
							Word.COL_ID + " IS ?",
							new String[] { String.valueOf(id) }, null, null,
							null, null);
			result.setNotificationUri(getContext().getContentResolver(), GREEK_URI_WORDS);
		}else if (LATIN_URI_WORDS.equals(uri)) {
			Log.e("abc", "query 1");
			result = PHDBHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Word.LATIN_TABLE_NAME, Word.FIELDS, null, null, null,
							null, null, null);
			result.setNotificationUri(getContext().getContentResolver(), LATIN_URI_WORDS);
		} else if (uri.toString().startsWith(LATIN_WORD_BASE)) {
			Log.e("abc", "query 2");
			final long id = Long.parseLong(uri.getLastPathSegment());
			result = PHDBHandler
					.getInstance(getContext())
					.getReadableDatabase()
					.query(Word.LATIN_TABLE_NAME, Word.FIELDS,
							Word.COL_ID + " IS ?",
							new String[] { String.valueOf(id) }, null, null,
							null, null);
			result.setNotificationUri(getContext().getContentResolver(), LATIN_URI_WORDS);
		} else {
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
