package me.anonim1133.testpedo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBConquered {
	private static String TAG = "ZACJA_DB_CONQ";
	private static String TABLE_NAME = "conquered";

	private SQLiteDatabase db;

	public DBConquered(SQLiteDatabase database){
		this.db = database;
	}

	public boolean add(int points, String date,  double longitude, double latitude){
		ContentValues values = new ContentValues();
		values.put("points", points);
		values.put("date", date);
		values.put("longitude", longitude);
		values.put("latitude", latitude);

		try{
			long value = db.insertOrThrow(TABLE_NAME, null, values);
			Log.i(TAG, "Dodajno conquered z id: " + value);
			if(value > 0) return true;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return false;
	}

	public void getLast(int limit) {
		Cursor cursor = db.query(TABLE_NAME , new String[] {"id", "points", "date",  "longitude", "latitude"}, null, null, null, "id DESC", "LIMIT " + limit);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(0);
			String ssid = cursor.getString(1);
			int signal = cursor.getInt(2);
			int security = cursor.getInt(3);
			double longitude = cursor.getDouble(4);
			double latitude = cursor.getDouble(5);

			//Wysłać to na serwer

			//Skasować z lokalnej bazy danych
			db.delete(TABLE_NAME, "id = ?", new String[] {"id"});

			cursor.moveToNext();
		}
		cursor.close();
	}
}