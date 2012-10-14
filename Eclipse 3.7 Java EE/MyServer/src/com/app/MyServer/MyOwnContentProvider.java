package com.app.MyServer;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyOwnContentProvider extends ContentProvider {

	// database
	private OwnDatabaseHelper database;

	private static final String AUTHORITY = "edu.buffalo.cse.cse486_586.provider";

	private static final String BASE_PATH = "msgfile";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" );

	@Override
	public boolean onCreate() {
		System.out.println("Oncreate of CP");
		database = new OwnDatabaseHelper(getContext());
		database.getWritableDatabase();
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Set the table
		queryBuilder.setTables(OwnTable.DATABASE_TABLE);

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, null, selection,
				null, null, null, null);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		
		long id = 0;
		Uri newuri = ContentUris.withAppendedId(uri, id);
		id = sqlDB.insert(OwnTable.DATABASE_TABLE, null, values);
			System.out.println("Result of insertion in db: " + id);

		getContext().getContentResolver().notifyChange(uri, null);
		return newuri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		final String DATABASE_CREATE = "create table " + OwnTable.DATABASE_TABLE + "(" + OwnTable.PROVIDER_KEY + " VARCHAR(5) ," + OwnTable.PROVIDER_VALUE + " VARCHAR(20));";

		sqlDB.execSQL("DROP TABLE IF EXISTS " + OwnTable.DATABASE_TABLE + ";");
		sqlDB.execSQL(DATABASE_CREATE);

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = {
				OwnTable.PROVIDER_KEY,
				OwnTable.PROVIDER_VALUE };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

}
