package com.greighamilton.rememo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greighamilton.rememo.data.DatabaseHelper;
import com.greighamilton.rememo.data.SettingsActivity;
import com.greighamilton.rememo.util.Util;

public class DailyActivity extends Activity {

	private List<LinearLayout> widgets;
	
	private DatabaseHelper db;
    
    private String currentDate;
    
    private String selectedDate;
    
    private AlarmManager alarmManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras(); 
		String dateSelected;

		if (extras != null) {
			dateSelected = extras.getString("SelectedDate");
			selectedDate = dateSelected;
		}
		else {
			currentDate = Util.getTodaysDate();
			selectedDate = currentDate;
		}
		
		setContentView(R.layout.activity_daily);
	}

	
	
	@Override
    protected void onResume() {
        super.onResume();

        db = DatabaseHelper.getInstance(this);
        widgets = new ArrayList<LinearLayout>();
        //selectedWidget = null;

        setUpWidgets();
    }

    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setUpWidgets() {
    	
    	setTitle(Util.getDayOfWeek(selectedDate) + ": " + selectedDate.substring(8, 10) + " " + Util.getMonthText(selectedDate) + " " + selectedDate.substring(0, 4));

        widgets.clear();

		setContentView(R.layout.activity_daily);

		Cursor allEventsCursor = db.getEventsByDate(selectedDate, Util.getTomorrowsDate(selectedDate), true); 
		allEventsCursor.moveToFirst();
		
		// inflate layout for the day
		LinearLayout widget = (LinearLayout) getLayoutInflater().inflate(R.layout.diary_widget_daily, null);

		
		// add blue border for today
		if (selectedDate.equals(Util.getTodaysDate())) {
			widget.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.diary_border_today));
		}
		else
			widget.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.diary_border));
		
		// identify subwidgets
		TextView day = (TextView) widget.findViewById(R.id.day_text_daily);
		LinearLayout eventsHolder = (LinearLayout) widget.findViewById(R.id.diary_appointments_container_daily);

		// day
		day.setText("Incomplete Tasks");
		day.setTypeface(null, Typeface.BOLD);
		
		// events
        Cursor eventsCursor = db.getEventsByDate(selectedDate, Util.getTomorrowsDate(selectedDate), true);
        eventsCursor.moveToFirst();
        
        TextView incompleteNoEvents = (TextView) widget.findViewById(R.id.noevents_text_incomplete);
        incompleteNoEvents.setVisibility(View.GONE);
        
        if (eventsCursor.isAfterLast())
        	incompleteNoEvents.setVisibility(View.VISIBLE);
        
        while (!eventsCursor.isAfterLast()) {
        	
        	incompleteNoEvents.setVisibility(View.GONE);
        	
        	
        	if (!db.isEventComplete(eventsCursor.getInt(DatabaseHelper.EVENT_ID))) {
        		LinearLayout diaryLine = new LinearLayout(this);
    	    	diaryLine.setOrientation(LinearLayout.HORIZONTAL);
    	    	
    	    	
    	    	LinearLayout notesLine = new LinearLayout(this);
    	    	notesLine.setOrientation(LinearLayout.HORIZONTAL);
    	    	
    	    	TextView eventPaddingTime = new TextView(this);
    	    	eventPaddingTime.setText("\t \t \t \t \t \t" + (eventsCursor
    					.getString(DatabaseHelper.EVENT_DATE_TIME)
    					.substring(11, 16)) + " \t \t ");
    	    	
    	    	TextView eventText = new TextView(this);
    			eventText.setText(eventsCursor.getString(DatabaseHelper.EVENT_NAME));
    			
    			TextView eventPaddingNotes = new TextView(this);
    			eventPaddingNotes.setText("\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
    	    	
    	    	TextView notesText = new TextView(this);
    	    	notesText.setText("Notes: " + eventsCursor.getString(DatabaseHelper.EVENT_NOTES));
    			
    			eventText.setPadding(35, 0, 0, 0);
    			
    	    	
    	    	// check for customisation options (underline, circle, star)
    	    	if (eventsCursor.getInt(DatabaseHelper.EVENT_CIRCLED) == 1) {
    	    		eventText.setBackground(getResources().getDrawable(R.drawable.entry_circle));
    	    	}
    	    	if (eventsCursor.getInt(DatabaseHelper.EVENT_UNDERLINE) == 1) {
    	    		eventText.setPaintFlags(eventText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    	    	}
    	    	if (eventsCursor.getInt(DatabaseHelper.EVENT_STARRED) == 1) {
    	    		eventText.setText(eventsCursor.getString(DatabaseHelper.EVENT_NAME) + " *");
    	    	}
    	    	
    	    	// set on click listener for diary line
    	    	diaryLine.setTag(R.id.diary_daily_id, eventsCursor.getInt(DatabaseHelper.EVENT_ID));
    	    	diaryLine.setTag(R.id.diary_daily_name, eventsCursor.getString(DatabaseHelper.EVENT_NAME));
    	    	diaryLine.setTag(R.id.diary_daily_datetime, eventsCursor.getString(DatabaseHelper.EVENT_DATE_TIME));
    	    	diaryLine.setTag(R.id.diary_daily_circled, eventsCursor.getInt(DatabaseHelper.EVENT_CIRCLED));
    	    	diaryLine.setTag(R.id.diary_daily_underlined, eventsCursor.getInt(DatabaseHelper.EVENT_UNDERLINE));
    	    	diaryLine.setTag(R.id.diary_daily_starred, eventsCursor.getInt(DatabaseHelper.EVENT_STARRED));
    	    	diaryLine.setTag(R.id.diary_daily_notes, eventsCursor.getString(DatabaseHelper.EVENT_NOTES));
    	    	
    	    	diaryLine.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	
                    	final int eventId = (Integer) v.getTag(R.id.diary_daily_id);
                    	final String eventName = (String) v.getTag(R.id.diary_daily_name);
                    	final String eventDateTime = (String) v.getTag(R.id.diary_daily_datetime);
                    	final int eventCircled = (Integer) v.getTag(R.id.diary_daily_circled);
                    	final int eventUnderlined = (Integer) v.getTag(R.id.diary_daily_underlined);
                    	final int eventStarred = (Integer) v.getTag(R.id.diary_daily_starred);
                    	final String eventNotes = (String) v.getTag(R.id.diary_daily_notes);
                    	
                    	// show dialog box for edit / delete options
                    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                				v.getContext());
                 
                			// set title
                			alertDialogBuilder.setTitle(eventName);
                 
                			// set dialog message
                			alertDialogBuilder
                				.setMessage("Would you like to edit or delete your reminder for " + eventName+"?")
                				.setCancelable(false)
                				.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                					public void onClick(DialogInterface dialog,int id) {
                						
                						// TODO delete the notification and reminder!!
                						
                						
                						// delete event from db and close
                						db.deleteEvent(eventId);
                						setUpWidgets();
                					}
                				  })
                				.setNegativeButton("Edit",new DialogInterface.OnClickListener() {
                					public void onClick(DialogInterface dialog,int id) {
                						// if this button is clicked, just close
                						// the dialog box and do nothing
                						dialog.cancel();
                						
                						// launch add activity with details pre-filled and update database event
                						
                						Intent i = new Intent(DailyActivity.this, AddEntryActivity.class);
                						i.putExtra("eventId", eventId);
                						i.putExtra("eventName", eventName);
                						i.putExtra("eventDateTime", eventDateTime);
                						i.putExtra("eventCircled", eventCircled);
                						i.putExtra("eventUnderlined", eventUnderlined);
                						i.putExtra("eventStarred", eventStarred);
                						i.putExtra("eventNotes", eventNotes);
                						startActivity(i);
                						DailyActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                					}
                				});
                 
                				// create alert dialog
                				AlertDialog alertDialog = alertDialogBuilder.create();
                 
                				// show it
                				alertDialog.show();
                    	
                     }
                    });
    			
    			// add to linear layout holder
    			diaryLine.addView(eventPaddingTime);
    			diaryLine.addView(eventText);
    			
    			notesLine.addView(eventPaddingNotes);
    			notesLine.addView(notesText);
    			
    			TextView paddingNotes = new TextView(this);
    			paddingNotes.setText("\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
    			
    			// add line holder to events holder
    			eventsHolder.addView(diaryLine);
    			if (!eventsCursor.getString(DatabaseHelper.EVENT_NOTES).equals("")) eventsHolder.addView(notesLine);
    			else eventsHolder.addView(paddingNotes);
        	}
        		
			eventsCursor.moveToNext();
			
			if (eventsHolder.getChildCount() == 0)
				incompleteNoEvents.setVisibility(View.VISIBLE);
        }
        
        
        // -------------------------------------------------------------------------------------
        
        // add completed tasks to layout
        
        LinearLayout completeEventsHolder = (LinearLayout) widget.findViewById(R.id.diary_appointments_container_daily_complete);

        // identify subwidgets
 		TextView complete = (TextView) widget.findViewById(R.id.day_text_daily_complete);
 		complete.setTextColor(Color.GRAY);

 		// day
 		complete.setText("Complete Tasks");
 		complete.setTypeface(null, Typeface.BOLD);
		
 		Cursor eventsCursorComplete = db.getEventsByDate(selectedDate, Util.getTomorrowsDate(selectedDate), true);
 		eventsCursorComplete.moveToFirst();
        
 		TextView completeNoEvents = (TextView) widget.findViewById(R.id.noevents_text_complete);
 		completeNoEvents.setVisibility(View.GONE);
 		completeNoEvents.setTextColor(Color.GRAY);
 		
 		if (eventsCursorComplete.isAfterLast())
 			completeNoEvents.setVisibility(View.VISIBLE);
        
        while (!eventsCursorComplete.isAfterLast()) {
        	
        	if (db.isEventComplete(eventsCursorComplete.getInt(DatabaseHelper.EVENT_ID))) {
        		LinearLayout diaryLine = new LinearLayout(this);
    	    	diaryLine.setOrientation(LinearLayout.HORIZONTAL);
    	    	
    	    	LinearLayout notesLine = new LinearLayout(this);
    	    	notesLine.setOrientation(LinearLayout.HORIZONTAL);
    	    	
    	    	TextView eventPaddingTime = new TextView(this);
    	    	eventPaddingTime.setText("\t \t \t \t \t \t" + (eventsCursorComplete
    					.getString(DatabaseHelper.EVENT_DATE_TIME)
    					.substring(11, 16)) + " \t \t ");
    	    	eventPaddingTime.setTextColor(Color.GRAY);
    	    	
    	    	TextView eventText = new TextView(this);
    			eventText.setText(eventsCursorComplete.getString(DatabaseHelper.EVENT_NAME));
    			eventText.setTextColor(Color.GRAY);
    			
    			TextView eventPaddingNotes = new TextView(this);
    			eventPaddingNotes.setText("\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
    	    	
    	    	TextView notesText = new TextView(this);
    	    	notesText.setText("Notes: " + eventsCursorComplete.getString(DatabaseHelper.EVENT_NOTES));
    	    	notesText.setTextColor(Color.GRAY);
    			
    			eventText.setPadding(35, 0, 0, 0);
    			
    			// check if event has been done
	        	if (db.isEventComplete(eventsCursorComplete.getInt(DatabaseHelper.EVENT_ID))) {
	        		eventText.setPaintFlags(eventText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	        	}
    			
    	    	
    	    	// check for customisation options (underline, circle, star)
    	    	if (eventsCursorComplete.getInt(DatabaseHelper.EVENT_CIRCLED) == 1) {
    	    		eventText.setBackground(getResources().getDrawable(R.drawable.entry_circle));
    	    	}
    	    	if (eventsCursorComplete.getInt(DatabaseHelper.EVENT_UNDERLINE) == 1) {
    	    		eventText.setPaintFlags(eventText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    	    	}
    	    	if (eventsCursorComplete.getInt(DatabaseHelper.EVENT_STARRED) == 1) {
    	    		eventText.setText(eventsCursorComplete.getString(DatabaseHelper.EVENT_NAME) + " *");
    	    	}
    			
    			// add to linear layout holder
    			diaryLine.addView(eventPaddingTime);
    			diaryLine.addView(eventText);
    			
    			notesLine.addView(eventPaddingNotes);
    			notesLine.addView(notesText);
    			
    			TextView paddingNotes = new TextView(this);
    			paddingNotes.setText("\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
    			
    			// add line holder to events holder
    			completeEventsHolder.addView(diaryLine);
    			if (!eventsCursorComplete.getString(DatabaseHelper.EVENT_NOTES).equals("")) completeEventsHolder.addView(notesLine);
    			else completeEventsHolder.addView(paddingNotes);
        	}
        	
        	eventsCursorComplete.moveToNext();
        	
        	if (completeEventsHolder.getChildCount() == 0)
        		completeNoEvents.setVisibility(View.VISIBLE);
        }
		
		// add widget
		widgets.add(widget);
		
		GridView grid = (GridView) findViewById(R.id.grid_of_widgets_daily);
		if (grid.getAdapter() == null) {
			grid.setAdapter(new WidgetAdapter(widgets));
		} else {
			((WidgetAdapter) grid.getAdapter()).setWidgets(widgets);
			grid.invalidateViews();
		}
    }
    


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_daily, menu);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

        switch (item.getItemId()) {

            case R.id.action_addentry:
                i = new Intent(DailyActivity.this,
                        AddEntryActivity.class);
                DailyActivity.this.startActivity(i);
                break;

            case R.id.action_settings:
                i = new Intent(DailyActivity.this,
                        SettingsActivity.class);
                DailyActivity.this.startActivity(i);
                break;
                
            case R.id.action_reminder:
                i = new Intent(DailyActivity.this,
                        ReminderActivity.class);
                DailyActivity.this.startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Private class for a WidgetAdapter object for use of the widgets on the dashboard.
     *
     * @author Greig Hamilton
     *
     */
    public class WidgetAdapter extends BaseAdapter {

        private List<LinearLayout> widgets;

        // Constructor (needs sorted lists)
        public WidgetAdapter(List<LinearLayout> w) {
            widgets = w;
        }

        @Override
        public int getCount() {
            return widgets.size();
        }

        @Override
        public Object getItem(int position) {
            return widgets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return widgets.get(position);
        }

        public void setWidgets(List<LinearLayout> w) {
            widgets = w;
        }

    }
    
    public void clickToday (View v) {
    	
    	selectedDate = Util.getTodaysDate();
        
        widgets.clear();
        
        setUpWidgets();
    }
    
    public void clickTomorrow (View v) {

    	selectedDate = Util.getTomorrowsDate(selectedDate);
        setUpWidgets();
    }
    
    public void clickYesterday (View v) {
    	
    	selectedDate = Util.getYesterdaysDate(selectedDate);
        widgets.clear();
        
        setUpWidgets();
    }
}
