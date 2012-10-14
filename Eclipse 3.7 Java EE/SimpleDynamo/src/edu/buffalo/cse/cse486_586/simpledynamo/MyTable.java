package edu.buffalo.cse.cse486_586.simpledynamo;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyTable {

	// Database table
		public static final String DATABASE_TABLE = "mytable";
		public static final String ID = "id";
		public static final String PROVIDER_KEY = "key";
		public static final String PROVIDER_VALUE = "value";

		// Database creation SQL statement
		private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + "(" + PROVIDER_KEY + " VARCHAR(5) ," + PROVIDER_VALUE + " VARCHAR(20));";

		public static void onCreate(SQLiteDatabase database) {
			//System.out.println("OnCreate function call");
			database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + ";");
			database.execSQL(DATABASE_CREATE);
		}

		public static void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {
			Log.w(MyTable.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(database);
		}
}
