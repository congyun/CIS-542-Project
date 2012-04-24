package edu.upenn.cis542;

import edu.upenn.cis542.route.Point;
import edu.upenn.cis542.route.Road;
import edu.upenn.cis542.route.RoadProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {


	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "PastRoadDB";
	private static final String ROAD_TABLE_NAME = "road";

	private static final String KEY_ID = "id";
	private static final String KEY_TIME = "TravelTime";
	private static final String KEY_POINT = "RoadPoints";

	private static final String ROAD_TABLE_CREATE = "CREATE TABLE "
			+ ROAD_TABLE_NAME + " (" + KEY_ID
			+ " integer primary key autoincrement," + KEY_TIME + " TEXT, "
			+ KEY_POINT + " TEXT);";
	

	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// called during creation of database
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ROAD_TABLE_CREATE);
	}

	// called during upgrade of database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ROAD_TABLE_NAME);
		onCreate(db);
	}

	// Insert new record
	public void insertRecord(String time, String pointInfo) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(KEY_TIME, time);
		cv.put(KEY_POINT, pointInfo);
		db.insert(ROAD_TABLE_NAME, null, cv); // Inserting Row
		db.close(); // Closing database connection
		 Log.d("Insert: ", "Inside Database."+ time +":"+ pointInfo);

	}

	// Get single record
	public Cursor getRecord(int id) {

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(ROAD_TABLE_NAME, new String[] { KEY_ID,
				KEY_TIME, KEY_POINT }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		//if (cursor != null)
		//	cursor.moveToFirst();

		// Road roadRecord = new Road();
		// roadRecord.time = Long.parseLong(cursor.getString(1));
		// roadRecord.mPoints[0] = parsePoint(cursor.getString(2));
		// return contact
		return cursor;
	}

	

	// Getting All records
	public Cursor getAllRecords() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + ROAD_TABLE_NAME, null);
		return cursor;

	}

	
	// Getting records Count 
	public int getRecordsCount() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + ROAD_TABLE_NAME, null);
		cursor.close();

		return cursor.getCount();
	}
	
	// Deleting single record
	public void deleteContact(int id) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(ROAD_TABLE_NAME, KEY_ID + " = ?",
	            new String[] { String.valueOf(id) });
	    db.close();
	}

	
}
