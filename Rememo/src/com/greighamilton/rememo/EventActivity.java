package com.greighamilton.rememo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.rememo.data.DatabaseHelper;
import com.greighamilton.rememo.data.SettingsActivity;
import com.greighamilton.rememo.util.Util;

public class EventActivity extends Activity {
	
	private int eventId;
	private String eventName;
	private String eventDateTime;
	private int eventCircled;
	private int eventUnderlined;
	private int eventStarred;
	private String eventNotes;
	private int eventOptions;
	
	private int eventComplete;
	
	private DatabaseHelper db;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		db = DatabaseHelper.getInstance(this);
		
		Bundle extras = getIntent().getExtras();
		
		// set all instances from database using extras id
		
		if (extras.getInt("eventComplete") == 1)
			eventComplete = 1;
		else
			eventComplete = 0;
		
		Cursor eventCursor = db.getEvent(extras.getInt("eventId"));
		eventCursor.moveToFirst();
		
		eventId = eventCursor.getInt(DatabaseHelper.EVENT_ID);
		eventName = eventCursor.getString(DatabaseHelper.EVENT_NAME);
		eventDateTime = eventCursor.getString(DatabaseHelper.EVENT_DATE_TIME);
		eventCircled = eventCursor.getInt(DatabaseHelper.EVENT_CIRCLED);
		eventUnderlined = eventCursor.getInt(DatabaseHelper.EVENT_UNDERLINE);
		eventStarred = eventCursor.getInt(DatabaseHelper.EVENT_STARRED);
		eventNotes = eventCursor.getString(DatabaseHelper.EVENT_NOTES);
		eventOptions = eventCursor.getInt(DatabaseHelper.EVENT_OPTIONS);
		
		// set the views
		TextView name = (TextView) findViewById(R.id.event_name_text);
		name.setTypeface(null, Typeface.BOLD);
		name.setText(eventName);
		
		if (eventOptions == 1)
			name.setTextColor(getResources().getColor(R.color.Green));
		if (eventOptions == 2)
			name.setTextColor(getResources().getColor(R.color.Red));
		
		
		
		if (extras.getInt("eventComplete") == 1)
			name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		
		// check for customisation options (underline, circle, star)
    	// check if device is 4.0- or 4.0+
    	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    	if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH && currentapiVersion != android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
    		
    		try {
    			if (eventCircled == 1)
    				name.setBackground(getResources().getDrawable(R.drawable.entry_circle));
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    	} else{
    	    // don't do anything
    	}
    	if (eventUnderlined == 1) {
    		name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    	}
    	if (eventStarred == 1) {
    		name.setText(eventName + " *");
    	}
		
		TextView date = (TextView) findViewById(R.id.event_date_text);
		date.setText(eventDateTime.substring(8, 10) + " " + Util.getMonthText(eventDateTime) + " " + eventDateTime.substring(0, 4));
		
		TextView time = (TextView) findViewById(R.id.event_time_text);
		time.setText(eventDateTime.substring(11, 16));
		
		if (!eventNotes.equals("")) {
			TextView notes = (TextView) findViewById(R.id.event_notes_text);
			notes.setText(eventNotes);
		}
		
		setTitle("Event: " + eventName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_event, menu);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

        switch (item.getItemId()) {

        	case android.R.id.home:
        		finish();
            return true;
            
        	case R.id.action_settings:
                i = new Intent(EventActivity.this,
                        SettingsActivity.class);
                EventActivity.this.startActivity(i);
                break;
            
        	case R.id.action_edit:
        		// edit event in db
        		i = new Intent(EventActivity.this, AddEntryActivity.class);
				i.putExtra("eventId", eventId);
				i.putExtra("eventName", eventName);
				i.putExtra("eventDateTime", eventDateTime);
				i.putExtra("eventCircled", eventCircled);
				i.putExtra("eventUnderlined", eventUnderlined);
				i.putExtra("eventStarred", eventStarred);
				i.putExtra("eventNotes", eventNotes);
				i.putExtra("eventComplete", eventComplete);
				i.putExtra("eventOptions", eventOptions);
				startActivity(i);
				EventActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				
                break;

            case R.id.action_delete:
            	
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
            	 
                // Setting Dialog Title
                alertDialog.setTitle("Confirm Delete");
         
                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want delete this?");
         
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                    Toast.makeText(getApplicationContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    
                    // delete event from db
                	db.deleteEvent(eventId);
                	
                	if (eventComplete == 1) {
                		db.deleteCompleteEvent(eventId);
                	}
                	
                	finish();
                    }
                });
         
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    
                    dialog.cancel();
                    }
                });
         
                // Showing Alert Message
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void alreadyComplete(View v) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventActivity.this);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Done.");
 
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure this is already done?");
 
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            
            // delete event from db
        	db.addToCompleteEvents(eventId);
        	
        	finish();
            }
        });
 
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}
}
