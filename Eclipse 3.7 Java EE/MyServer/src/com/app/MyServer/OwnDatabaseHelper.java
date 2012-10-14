package com.app.MyServer;

import java.io.Serializable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OwnDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "suhani_database";
	public static final int DATABASE_VERSION = 1;


	public OwnDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		OwnTable.onCreate(database);
	}

	// Method is called during an upgrade of the database
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		OwnTable.onUpgrade(database, oldVersion, newVersion);
	}
}

