package com.greighamilton.rememo.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Copies an already existing SQLite database into this application. Provides a
 * singleton instance to a database helper which provides cursors to the
 * database.
 * 
 * @see http
 *      ://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android
 *      -applications/
 * 
 * @author Juan-Manuel Flux
 * 
 * @author Greig Hamilton (queries)
 */
@SuppressLint({ "DefaultLocale", "SdCardPath" })
public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final int EVENT_ID = 0;
	public static final int EVENT_NAME = 1;
	public static final int EVENT_DATE_TIME = 2;
	public static final int EVENT_CIRCLED = 3;
	public static final int EVENT_UNDERLINE = 4;
	public static final int EVENT_STARRED = 5;
	public static final int EVENT_NOTES = 6;
	public static final int EVENT_OPTIONS = 7;
	
	public static final int COMPLETE_EVENT_ID = 0;

	private static final int DATABASE_VERSION = 11;
	
	// The Android's default system path of your application database.
	private static final String DB_PATH = "/data/data/com.greighamilton.rememo/databases/";
	private static final String DB_NAME = "rememo.db";
	private static DatabaseHelper instance;
	private static boolean exists;
	private SQLiteDatabase db;
	private final Context context;

	/**
	 * Constructor
	 */
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		this.context = context;

		exists = checkDatabaseExists();
	}

	/**
	 * Returns the singleton DatabaseHelper, instantiating if not yet done so.
	 * 
	 * @param context		Application context.
	 * 
	 * @return The DatabaseHelper singleton.
	 */
	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
			try {
				instance.createDatabase();
			} catch (IOException ioe) {
				/*
				 * Critical error - database could not be created. This is
				 * probably due to lack of space.
				 */
				throw new Error(
						"Unable to create database, probably due to lack of space");
			}

			if (!exists) {
				return null;
			}
		}

		return instance;
	}

	/**
	 * Creates an empty database on the system and populates it from a
	 * pre-prepared database.
	 */
	private void createDatabase() throws IOException {
		if (checkDatabaseExists()) {
			/* Database exists, so we can just open it */
			instance.openDatabase();
		} else {
			/* Database does not exist - create a new one */
			this.getReadableDatabase();

			/*
			 * Set up an AsyncTask to go off and copy our pre-prepared database
			 * into the data section of this application.
			 */
			copyDatabase();
			exists = true;
			instance.openDatabase();
		}
	}

	/**
	 * Checks if the database already exists.
	 */
	public static boolean checkDatabaseExists() {
		SQLiteDatabase checkDB = null;

		/*
		 * Opening the database will fail if it doesn't exist, so we can catch
		 * and ignore that error.
		 */
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			/*
			 * Database does't exist yet - we can ignore this exception. This
			 * function is not for creating the database.
			 */
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies each database file assets-folder to a single file in the data
	 * storage for this app.
	 * 
	 * @see http
	 *      ://stackoverflow.com/questions/2860157/load-files-bigger-than-1m-
	 *      from-assets-folder/3093966#3093966
	 *      
	 * @author Seva Alekseyev
	 */
	private void copyDatabase() throws IOException {
		File dbFile = new File(DB_PATH + DB_NAME);
		AssetManager am = context.getAssets();
		OutputStream os = new FileOutputStream(dbFile);
		dbFile.createNewFile();

		byte[] b = new byte[1024];
		int i, r;

		String[] Files = am.list("");
		Arrays.sort(Files);

		for (i = 1; i < 10; i++) {
			String fn = String.format("%d.db", i);
			if (Arrays.binarySearch(Files, fn) < 0)
				break;
			InputStream is = am.open(fn);
			while ((r = is.read(b)) != -1)
				os.write(b, 0, r);
			is.close();
		}
		os.close();
	}

	/**
	 * Attempts to open the database in read-write mode.
	 */
	private void openDatabase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * Close the open database.
	 */
	@Override
	public synchronized void close() {
		if (db != null)
			db.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// stub if needed
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.i("UPGRADE", "UPGRADE");
		
		db.execSQL("ALTER TABLE EVENT ADD COLUMN options INTEGER DEFAULT 0");
	}
	
	public void onUpgrade(DatabaseHelper db2, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i("UPGRADE DUE", "HIYAZ");
		
		db.execSQL("ALTER TABLE EVENT ADD COLUMN options INTEGER DEFAULT 0");
	}

	/**
	 * Everything following are queries written by
	 * 
	 * @author Greig Hamilton
	 */
	
	/**
	 * Method to add an event to the database.
	 * 
	 * @param nextId		next available id for the event in db
	 * @param name			the name of the event
	 * @param date_time		the date and time of the event
	 * @param circled		if the event is circled (1 if true)
	 * @param underlined	if the event is underlined (1 if true)
	 * @param starred		if the event is starred (1 if true)
	 * @param notes			the notes for the event
	 * 
	 * @return				insertion into the db
	 */
	public int addEvent(int nextId, String name, String date_time, int circled, int underlined, int starred, String notes, int options) {
		ContentValues cv = new ContentValues(8);
		cv.put("_id", nextId);
		cv.put("name", name);
		cv.put("date_time", date_time);
		cv.put("circled", circled);
		cv.put("underlined", underlined);
		cv.put("starred", starred);
		cv.put("notes", notes);
		cv.put("options", options);

		return (int) db.insert("EVENT", null, cv);
	}
	
	/**
	 * Method to find the next available event id in the database.
	 * 
	 * @return		the id
	 */
	public int nextEventID() {
		Cursor c = db.query("EVENT", null, null, null, null, null, "_id desc");
		c.moveToFirst();
		
		return ((!c.isAfterLast()) ? c.getInt(EVENT_ID)+1 : 1);
		}
	
	/**
	 * Method to get the name of an event, using the id
	 * 
	 * @param index		of the event to be found
	 * 
	 * @return			the name of the event
	 */
	public String getEventName(int index) {
		Cursor c = db.rawQuery("SELECT * FROM EVENT WHERE _id = " + index,
				null);
		String name = " ";
		c.moveToFirst();
		
		return (!c.isAfterLast()) ? c.getString(EVENT_NAME) : name;
	}
	
	/**
	 * Method to return a cursor of events in the date range specified.
	 * 
	 * @param fromDate			the earliest date range requested
	 * @param toDate			the latest date range requested
	 * @param ascendingOrder	if the results are to be in ascending order (1 if true)
	 * 
	 * @return					a cursor to the results of the query
	 */
	public Cursor getEventsByDate(String fromDate, String toDate, boolean ascendingOrder) {
		String clause = null;
		String order = (ascendingOrder) ? "date_time asc" : "date_time desc";
		
		if (fromDate != null && toDate != null) {
			clause = " date_time >= '" + fromDate + "'" +
			     " AND date_time <= '" + toDate + "'";
		}
		
		else if (fromDate != null) clause = "date_time >= '" + fromDate + "'";
		
		else if (toDate != null) clause = "date_time <= '" + toDate + "'";
		
		
		return db.query("EVENT", null, clause, null, null, null, order);
	}
	
	public Cursor getEvent(int id) {
		
		return db.rawQuery("SELECT * FROM EVENT WHERE _id=" + id, null);
	}
	
	/**
	 * Method to find if the database has any events currently in its table.
	 * 
	 * @return		true or false
	 */
	public boolean isDatabaseEmpty() {
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM EVENT", null);
	    if (cur != null){
	        cur.moveToFirst();
	        if (cur.getInt(0) == 0) {
	          // Empty 
	        	return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Method to add an id to a table of events that have been completed.
	 * 
	 * @param nextId		the id of the event that is complete
	 * 
	 * @return				insertion int o the database
	 */
	public int addToCompleteEvents(int nextId) {
		ContentValues cv = new ContentValues(1);
		cv.put("_id", nextId);

		return (int) db.insert("COMPLETE_EVENT", null, cv);
	}
	
	/**
	 * Method used to find if an event has been completed yet.
	 * 
	 * @param id		the id to be checked
	 * 
	 * @return			true or false
	 */
	public boolean isEventComplete(int id) {
		
		Cursor c = db.rawQuery("SELECT * FROM COMPLETE_EVENT WHERE _id=" + id, null);
		c.moveToLast();
		
		int count1 = c.getCount();
		if (count1 == 0) {
			return false;

		} else {
			return true;
		}
	}
	
	/**
	 * Method to get events on a specific date.
	 * 
	 * @param date				the date of the query
	 * @param ascendingOrder	if the results are to be in ascending order (1 if true)
	 * 
	 * @return					a cursor pointing to before the first result of the query
	 */
	public Cursor getEventsByDate(String date, boolean ascendingOrder) {
		
		String clause = null;
		String order = (ascendingOrder) ? "date_time asc" : "date_time desc";
		
		if (date != null) {
			clause = " date_time = '" + date + "'";
		}
		
		
		return db.query("EVENT", null, clause, null, null, null, order);
	}
	
	public ArrayList<String[]> searchQuery(String searchString) {
		
		String[] results;
		String[] ids;
		ArrayList<String[]> returned = new ArrayList<String[]>();
		
		Cursor cursor = db.query("EVENT", null, "name LIKE ?", new String[] {"%"+searchString+"%"}, null, null, "date_time asc");
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			results = new String[cursor.getCount()];
			ids = new String[cursor.getCount()];
			
			for (int i = 0; !cursor.isAfterLast(); i++) {
				results[i] = cursor.getString(DatabaseHelper.EVENT_NAME);
				ids[i] = cursor.getString(DatabaseHelper.EVENT_ID);
				cursor.moveToNext();
			}
		}
		
		else {
			results = new String[]{""};
			ids = new String[]{""};
		}
		returned.add(0, results);
		returned.add(1, ids);
		
		return returned;
	}
	
	public ArrayList<String[]> allIncompleteEvents() {
		
		String[] results;
		String[] ids;
		
		String[] finalResults;
		String[] finalIds;
		
		ArrayList<String[]> returned = new ArrayList<String[]>();
		
		Cursor cursor = db.query("EVENT", null, null, null, null, null, "date_time asc");
		
		int count = 0;
		
		if (cursor != null) {
			cursor.moveToFirst();
			
			results = new String[cursor.getCount()];
			ids = new String[cursor.getCount()];
			
			for (int i = 0; !cursor.isAfterLast(); ) {
				int id = Integer.parseInt(cursor.getString(DatabaseHelper.EVENT_ID));
				
				if (isEventComplete(id)) {
					// already complete, don't add to results
					cursor.moveToNext();
				}
				else {
					String name = cursor.getString(DatabaseHelper.EVENT_NAME);
					
					results[i] = name;
					ids[i] = ""+id;
					
					i++;
					cursor.moveToNext();
					count++;
				}				
			}
			
			finalResults = new String[count];
			finalIds = new String[count];
			
			for (int i = 0; i < count; i++) {
				finalResults[i] = results[i];
				finalIds[i] = ids[i];
			}
		}
		
		else {
			finalResults = new String[]{""};
			finalIds = new String[]{""};
		}
		
		returned.add(0, finalResults);
		returned.add(1, finalIds);
		
		return returned;
	}

	/**
	 * Method to update an event that is currently in the database.
	 * 
	 * @param nextId		the id of the event being updated
	 * @param name			the name of the event
	 * @param date_time		the date and time of the event
	 * @param circled		if the event is circled (1 if true)
	 * @param underlined	if the event is underlined (1 if true)
	 * @param starred		if the event is starred (1 if true)
	 * @param notes			the notes of the event
	 */
	public void updateEvent(int nextId, String name, String date_time, int circled, int underlined, int starred, String notes, int options) {
		
		ContentValues cv = new ContentValues(8);
		cv.put("_id", nextId);
		cv.put("name", name);
		cv.put("date_time", date_time);
		cv.put("circled", circled);
		cv.put("underlined", underlined);
		cv.put("starred", starred);
		cv.put("notes", notes);
		cv.put("options", options);

		db.update("EVENT", cv, "_id="+nextId, null);
	}
	
	/**
	 * Method used to delete a particular event from the db using an id
	 * 
	 * @param id		of the event to be deleted
	 */
	public void deleteEvent(int id) {
		db.delete("EVENT", "_id="+id, null);
	}
	
	/**
	 * Method used to delete a particular complete event from the db using an id
	 * 
	 * @param id		of the complete event to be deleted
	 */
	public void deleteCompleteEvent(int id) {
		db.delete("COMPLETE_EVENT", "_id="+id, null);
	}
	
	public Cursor getEvents() {
		return db.query("EVENT", null, null, null, null, null, "_id asc");
	}
	
	
	// --------------------------------------------------------------------------------------------------------
	
	

	/* AsyncTask to create database on first run. */
	@SuppressWarnings("unused")
	private class CreateDatabaseTask extends
			AsyncTask<Integer, Integer, Boolean> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setTitle("Bla");
			dialog.setMessage("Bla 2");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
			Toast.makeText(context, "Pre Execute", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				// Copy database from assets
				copyDatabase();

			} catch (IOException e) {
				/* An error occurred while copying the database */
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				dialog.dismiss();
				Toast.makeText(context, "Post Execute", Toast.LENGTH_SHORT)
						.show();
			} catch (IllegalArgumentException e) {
			}

			exists = true;
			instance.openDatabase();
		}
	}


	


	






	
}