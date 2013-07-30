package com.greighamilton.rememo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.greighamilton.rememo.data.DatabaseHelper;
import com.greighamilton.rememo.data.SettingsActivity;
import com.greighamilton.rememo.util.Util;

/**
 * Class activity for daily diary view.
 * 
 * @author Greig Hamilton
 *
 */
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
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	
	
	@Override
    protected void onResume() {
        super.onResume();

        db = DatabaseHelper.getInstance(this);
        widgets = new ArrayList<LinearLayout>();
        //selectedWidget = null;

        setUpWidgets();
    }

	/**
	 * Method that refreshes the widgets on the grid view.
	 * 
	 * Called every time a change occurs on the view.
	 */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setUpWidgets() {
    	
    	setTitle(Util.getDayOfWeek(selectedDate) + ": " + selectedDate.substring(8, 10) + " " + Util.getMonthText(selectedDate) + " " + selectedDate.substring(0, 4));

    	// clear any current widgets
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
		day.setText("Still To Do Today");
		day.setTypeface(null, Typeface.BOLD);
		
		// events
        Cursor eventsCursor = db.getEventsByDate(selectedDate, Util.getTomorrowsDate(selectedDate), true);
        eventsCursor.moveToFirst();
        
        TextView incompleteNoEvents = (TextView) widget.findViewById(R.id.noevents_text_incomplete);
        incompleteNoEvents.setVisibility(View.GONE);
        
        // if nothing to show, show default text
        if (eventsCursor.isAfterLast())
        	incompleteNoEvents.setVisibility(View.VISIBLE);
        
        // whilst there is something available for the day to show
        while (!eventsCursor.isAfterLast()) {
        	
        	incompleteNoEvents.setVisibility(View.GONE);
        	
        	if (!db.isEventComplete(eventsCursor.getInt(DatabaseHelper.EVENT_ID))) {
        		
        		// create new layouts for each diary line
        		LinearLayout diaryLine = new LinearLayout(this);
    	    	diaryLine.setOrientation(LinearLayout.HORIZONTAL);
    	    	
    	    	// create new layouts for each notes line
    	    	LinearLayout notesLine = new LinearLayout(this);
    	    	notesLine.setOrientation(LinearLayout.HORIZONTAL);
    	    	
    	    	// add some padding to be used to space out the data
    	    	TextView eventPaddingTime = new TextView(this);
    	    	eventPaddingTime.setText("\t \t \t \t \t \t" + (eventsCursor
    					.getString(DatabaseHelper.EVENT_DATE_TIME)
    					.substring(11, 16)) + " \t \t ");
    	    	
    	    	// set the event text to that from the current database item
    	    	TextView eventText = new TextView(this);
    			eventText.setText(eventsCursor.getString(DatabaseHelper.EVENT_NAME));
    			
    			int options = eventsCursor.getInt(DatabaseHelper.EVENT_OPTIONS);
				if (options == 1)
					eventText.setTextColor(getResources().getColor(R.color.Green));
				if (options == 2)
					eventText.setTextColor(getResources().getColor(R.color.Red));
    			
    			// set some padding for the notes line
    			TextView eventPaddingNotes = new TextView(this);
    			eventPaddingNotes.setText("\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t ");
    	    	
    			// set the event notes text to that from the current database item
    	    	TextView notesText = new TextView(this);
    	    	notesText.setText("Notes: " + eventsCursor.getString(DatabaseHelper.EVENT_NOTES));
    			
    			eventText.setPadding(35, 0, 0, 0);
    			
    	    	
    	    	// check for customisation options (underline, circle, star)
    			// check if device is 4.0- or 4.0+
	        	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	        	if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
	        		if (eventsCursor.getInt(DatabaseHelper.EVENT_CIRCLED) == 1) {
		        		eventText.setBackground(getResources().getDrawable(R.drawable.entry_circle));
		        	}
	        	} else{
	        	    // don't do anything
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
    	    	diaryLine.setTag(R.id.diary_daily_options, eventsCursor.getInt(DatabaseHelper.EVENT_OPTIONS));
    	    	
    	    	
    	    	// set an onclick listener for each of the diary lines
    	    	diaryLine.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	
                    	// all the data that is associated with each line (or entry)
                    	final int eventId = (Integer) v.getTag(R.id.diary_daily_id);
                    	final String eventName = (String) v.getTag(R.id.diary_daily_name);
                    	final String eventDateTime = (String) v.getTag(R.id.diary_daily_datetime);
                    	final int eventCircled = (Integer) v.getTag(R.id.diary_daily_circled);
                    	final int eventUnderlined = (Integer) v.getTag(R.id.diary_daily_underlined);
                    	final int eventStarred = (Integer) v.getTag(R.id.diary_daily_starred);
						final String eventNotes = (String) v.getTag(R.id.diary_daily_notes);
						final int eventOptions = (Integer) v.getTag(R.id.diary_daily_options);

						Intent i = new Intent(DailyActivity.this, EventActivity.class);
						i.putExtra("eventId", eventId);
						startActivity(i);
						
						DailyActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        	
        	// get the next event
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
 		complete.setText("Already Done Today");
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
	        	// check if device is 4.0- or 4.0+
	        	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	        	if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH && currentapiVersion != android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
	        		
	        		try {
	        			if (eventsCursor.getInt(DatabaseHelper.EVENT_CIRCLED) == 1)
	        				eventText.setBackground(getResources().getDrawable(R.drawable.entry_circle));
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
	        		
	        	} else{
	        	    // don't do anything
	        	}
    	    	if (eventsCursorComplete.getInt(DatabaseHelper.EVENT_UNDERLINE) == 1) {
    	    		eventText.setPaintFlags(eventText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    	    	}
    	    	if (eventsCursorComplete.getInt(DatabaseHelper.EVENT_STARRED) == 1) {
    	    		eventText.setText(eventsCursorComplete.getString(DatabaseHelper.EVENT_NAME) + " *");
    	    	}
    	    	
    	    	// set on click listener for diary line
    	    	diaryLine.setTag(R.id.diary_daily_id, eventsCursorComplete.getInt(DatabaseHelper.EVENT_ID));
    	    	diaryLine.setTag(R.id.diary_daily_name, eventsCursorComplete.getString(DatabaseHelper.EVENT_NAME));
    	    	diaryLine.setTag(R.id.diary_daily_datetime, eventsCursorComplete.getString(DatabaseHelper.EVENT_DATE_TIME));
    	    	diaryLine.setTag(R.id.diary_daily_circled, eventsCursorComplete.getInt(DatabaseHelper.EVENT_CIRCLED));
    	    	diaryLine.setTag(R.id.diary_daily_underlined, eventsCursorComplete.getInt(DatabaseHelper.EVENT_UNDERLINE));
    	    	diaryLine.setTag(R.id.diary_daily_starred, eventsCursorComplete.getInt(DatabaseHelper.EVENT_STARRED));
    	    	diaryLine.setTag(R.id.diary_daily_notes, eventsCursorComplete.getString(DatabaseHelper.EVENT_NOTES));
    	    	diaryLine.setTag(R.id.diary_daily_options, eventsCursorComplete.getInt(DatabaseHelper.EVENT_OPTIONS));
    	    	
    	    	// set an onclick listener for each of the diary lines
    	    	diaryLine.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    	
                    	// all the data that is associated with each line (or entry)
                    	final int eventId = (Integer) v.getTag(R.id.diary_daily_id);
                    	final String eventName = (String) v.getTag(R.id.diary_daily_name);
                    	final String eventDateTime = (String) v.getTag(R.id.diary_daily_datetime);
                    	final int eventCircled = (Integer) v.getTag(R.id.diary_daily_circled);
                    	final int eventUnderlined = (Integer) v.getTag(R.id.diary_daily_underlined);
                    	final int eventStarred = (Integer) v.getTag(R.id.diary_daily_starred);
						final String eventNotes = (String) v.getTag(R.id.diary_daily_notes);
						final int eventOptions = (Integer) v.getTag(R.id.diary_daily_options);

						Intent i = new Intent(DailyActivity.this, EventActivity.class);
						i.putExtra("eventId", eventId);
						i.putExtra("eventComplete", 1);
						startActivity(i);
						
						DailyActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
		
		// generate the grid layout
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
		
		// Associate searchable configuration with the SearchView
        SearchManager searchManager =
               (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

        switch (item.getItemId()) {

        	case android.R.id.home:
        		finish();
            return true;
        
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
    
    /**
     * Method called when user clicks on the today button.
     * 
     * @param v		View
     */
    public void clickToday (View v) {
    	
    	// updates the selected date to today
    	selectedDate = Util.getTodaysDate();
        
        widgets.clear();
        
        setUpWidgets();
    }
    
    /**
     * Method called when user clicks on the tomorrow button.
     * 
     * @param v		View
     */
    public void clickTomorrow (View v) {

    	// updates the selected date to the next day
    	selectedDate = Util.getTomorrowsDate(selectedDate);
    	
        setUpWidgets();
    }
    
    /**
     * Method called when user clicks on the yesterday button.
     * 
     * @param v		View
     */
    public void clickYesterday (View v) {
    	
    	// updates the selected date to the previous date
    	selectedDate = Util.getYesterdaysDate(selectedDate);
    	
        widgets.clear();
        
        setUpWidgets();
    }
}
