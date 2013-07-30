package com.greighamilton.rememo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.greighamilton.rememo.data.DatabaseHelper;

/**
 * Class used to update the database after a system update. Migrates old table records to the new format.
 * 
 * @author Greig Hamilton
 *
 */
public class Update {
	
	public static void doUpdate(Context c) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);		
		int version = sp.getInt("VERSION", 0);
		
		// only update the color parser if version is version 0 (do it once only)
		if (version < 5) updateEventDatabaseTable(c, sp);
	}
	
	/**
	 * Method used to update database with new options.
	 * 
	 * @param context		the current context
	 * @param sp			the app shared preferences
	 */
	private static void updateEventDatabaseTable(Context context, SharedPreferences sp) {
		
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		
		db.onUpgrade(db, 1, 2);
		
		
//		Cursor c = db.getEvents();
//		c.moveToFirst();
//		
//		while (c.moveToNext()) {
//			
//			db.updateEvent(c.getInt(DatabaseHelper.EVENT_ID), c.getString(DatabaseHelper.EVENT_NAME), c.getString(DatabaseHelper.EVENT_DATE_TIME), c.getInt(DatabaseHelper.EVENT_CIRCLED), c.getInt(DatabaseHelper.EVENT_UNDERLINE), c.getInt(DatabaseHelper.EVENT_STARRED), c.getString(DatabaseHelper.EVENT_NOTES), 0);
//			
//		}
		sp.edit().putInt("VERSION", 5).commit();
	}
}
