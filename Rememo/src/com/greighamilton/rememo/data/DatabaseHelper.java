package com.greighamilton.rememo.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
		super(context, DB_NAME, null, 1);
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
		// stub if needed
	}

	/**
	 * Everything following are queries written by
	 * 
	 * @author Greig Hamilton
	 */
	
	// INCOME queries
//	public Cursor getIncome() {
//		return db.query("INCOME", null, null, null, null, null, "_id asc");
//	}
	
//	public Cursor getIncomeId(String id) {
//		return db.query("INCOME", null, "_id="+id, null, null, null, null);
//	}
	
//	public Cursor getSpecifiedIncome(String month, String year, int catId, boolean allIncomes, boolean allCategories) {
//		
//		if (allIncomes && allCategories)
//			return db.query("INCOME", null, null, null, null, null, "date asc");
//		else if (allIncomes && !allCategories)
//			return db.query("INCOME", null, "category_id="+catId, null, null, null, "date asc");
//		else if (!allIncomes && allCategories)
//			return db.query("INCOME", null, "date LIKE ?", new String[] {year+"-"+month+"-%"}, null, null, "date asc");
//		else
//			return db.query("INCOME", null, "category_id="+catId+" AND date LIKE ?", new String[] {year+"-"+month+"-%"}, null, null, "date asc");
//	}
//	
//	public int getTotalIncomeAmountForMonth(String year, String month) {
//		Cursor c = db.rawQuery("SELECT SUM(amount) FROM INCOME WHERE date LIKE '"+year+"-"+month+"-%'", null);
//		c.moveToFirst();
//		if (!c.isAfterLast()) return c.getInt(0);
//		else return 0;		
//	}
	
//	public Cursor getIncomeByAmount(String month, String year, boolean allReq) {
//		
//		if (allReq)
//			return db.query("INCOME", null, null, null, null, null, "amount desc");
//		else
//			return db.query("INCOME", null, "date LIKE ?", new String[] {year+"-"+month+"-%"}, null, null, "amount desc");
//	}
//	
//	public Cursor getIncomeByDate(String fromDate, String toDate, boolean ascendingOrder) {
//		String clause = null;
//		String order = (ascendingOrder) ? "date asc" : "date desc";
//		
//		if (fromDate != null && toDate != null) {
//			clause = " date >= '" + fromDate + "'" +
//			     " AND date <= '" + toDate + "'";
//		}
//		else if (fromDate != null) clause = "date >= '" + fromDate + "'";
//		else if (toDate != null) clause = "date <= '" + toDate + "'";
//		
//		
//		return db.query("INCOME", null, clause, null, null, null, order);
//	}
//
	public int addEvent(int nextId, String name, String date_time, int circled, int underlined, int starred, String notes) {
		ContentValues cv = new ContentValues(7);
		cv.put("_id", nextId);
		cv.put("name", name);
		cv.put("date_time", date_time);
		cv.put("circled", circled);
		cv.put("underlined", underlined);
		cv.put("starred", starred);
		cv.put("notes", notes);

		return (int) db.insert("EVENT", null, cv);
	}
//	
//	public void updateIncome(String id, String name, float amount, String date, int repetition_period, int repetition_length, String notes, int categoryId, int notification_id) {
//		ContentValues cv = new ContentValues(9);
//		
//		cv.put("name", name);
//		cv.put("amount", amount);
//		cv.put("date", date);
//		cv.put("repetition_period", repetition_period);
//		cv.put("repetition_length", repetition_length);
//		cv.put("notes", notes);
//		cv.put("category_id", categoryId);
//		cv.put("notification_id", notification_id);
//
//		db.update("INCOME", cv, "_id="+id, null);
//	}
//	
//	public void deleteIncome(String id) {
//		db.delete("INCOME", "_id="+id, null);
//	}
//	
//	public void deleteIncomeSeries(String id) {
//		db.delete("INCOME", "_id="+id+" OR (repetition_period = 4 AND repetition_length = "+id+")", null);
//	}
//	
//	public String getIncomeSeriesID(String id) {
//		Cursor c = db.rawQuery("SELECT * FROM INCOME WHERE _id = "+id, null);
//		c.moveToFirst();
//		if (!c.isAfterLast()) {
//			return (c.getInt(INCOME_REPETITION_PERIOD)==4) ? ""+c.getInt(INCOME_REPETITION_LENGTH) : id;
//		}
//		return id;
//	}
//
//	public String getIncomeName(int index) {
//		Cursor c = db.rawQuery("SELECT * FROM INCOME WHERE _id = "+index, null);
//		String name = "nothing :(";
//		c.moveToFirst();
//		return (!c.isAfterLast()) ? c.getString(INCOME_NAME) : name;
//	}
//	
//	public int getIncomeRepetitionPeriod(String index) {
//		Cursor c = db.rawQuery("SELECT * FROM INCOME WHERE _id = "+index, null);
//		c.moveToFirst();
//		return (!c.isAfterLast()) ? c.getInt(INCOME_REPETITION_PERIOD) : 0;
//	}
//	
//	public int getIncomeRepetitionLength(String index) {
//		Cursor c = db.rawQuery("SELECT * FROM INCOME WHERE _id = "+index, null);
//		c.moveToFirst();
//		return (!c.isAfterLast()) ? c.getInt(INCOME_REPETITION_LENGTH) : 0;
//	}
	
	public int nextEventID() {
		Cursor c = db.query("EVENT", null, null, null, null, null, "_id desc");
		c.moveToFirst();
		return ((!c.isAfterLast()) ? c.getInt(EVENT_ID)+1 : 1);
		}
	
	public String getEventName(int index) {
		Cursor c = db.rawQuery("SELECT * FROM EVENT WHERE _id = " + index,
				null);
		String name = "nothing :(";
		c.moveToFirst();
		return (!c.isAfterLast()) ? c.getString(EVENT_NAME) : name;
	}
	
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






	public Cursor getEventsByDate(String date, boolean ascendingOrder) {
		Log.i("DATE DB", date);
		String clause = null;
		String order = (ascendingOrder) ? "date_time asc" : "date_time desc";
		
		if (date != null) {
			clause = " date_time = '" + date + "'";
		}
		
		
		return db.query("EVENT", null, clause, null, null, null, order);
	}







	

}