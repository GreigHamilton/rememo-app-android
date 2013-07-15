package com.greighamilton.rememo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

/**
 * Main Activity class.
 *
 * @author Greig Hamilton
 *
 */
public class MainActivity extends Activity {

    private List<LinearLayout> widgets;
    
    private String selectedDate;
    
	private DatabaseHelper db;
    
    private String currentWeekStartDate;
    private String currentWeekEndDate;
    
    private String selectedWeekStartDate;
    private String selectedWeekEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        // get the current week start and end dates
        String[] thisWeekDates = Util.getCurrentWeekDates().split("#");
        currentWeekStartDate = thisWeekDates[0];
        currentWeekEndDate = thisWeekDates[1];
        
        // set the selected week start and end dates as the current week start and end dates
        selectedWeekStartDate = currentWeekStartDate;
        selectedWeekEndDate = currentWeekEndDate;
        
        // currently selected date is null
        selectedDate = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = DatabaseHelper.getInstance(this);
        
        widgets = new ArrayList<LinearLayout>();
        
        // get the current week start and end dates
        String[] thisWeekDates = Util.getCurrentWeekDates().split("#");
        currentWeekStartDate = thisWeekDates[0];
        currentWeekEndDate = thisWeekDates[1];
        
        selectedDate = null;

        setUpWidgets();
    }

    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setUpWidgets() {

    	// clear any widgets currently on display
        widgets.clear();

        setContentView(R.layout.activity_main);
        
        // get all the event sin the database for the selected week
        Cursor allEventsCursor = db.getEventsByDate(selectedWeekStartDate, selectedWeekEndDate, true);
        allEventsCursor.moveToFirst();
        
        
        // if there is nothing in the db, show a splash screen to welcome the user
        if (db.isDatabaseEmpty()) setContentView(R.layout.activity_main_welcome);
        
		else {
			
			// start at first date
			String currentCursorDate = selectedWeekStartDate;
			
			// for each date between start and end
			while (!currentCursorDate.equals(selectedWeekEndDate)) {
				
				// inflate layout for the day
				LinearLayout widget = (LinearLayout) getLayoutInflater().inflate(R.layout.diary_widget, null);
				
				// add blue border for today
				if (currentCursorDate.equals(Util.getTodaysDate())) {
					widget.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.diary_border_today));
				}
				else
					widget.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.diary_border));

				// identify subwidgets
				TextView day = (TextView) widget.findViewById(R.id.day_text);
				TextView date = (TextView) widget.findViewById(R.id.main_date_text);
				LinearLayout eventsHolder = (LinearLayout) widget.findViewById(R.id.diary_appointments_container);

				// day
				day.setText(" "+Util.getDayOfWeek(currentCursorDate));
				day.setTypeface(null, Typeface.BOLD);

				// date
				date.setText("  "+currentCursorDate.substring(8, 10) + " " + Util.getMonthText(currentCursorDate) + " " + currentCursorDate.substring(0, 4));
				date.setTypeface(null, Typeface.ITALIC);
				
				// events
		        Cursor eventsCursor = db.getEventsByDate(currentCursorDate, Util.getTomorrowsDate(currentCursorDate), true);
		        eventsCursor.moveToFirst();
		        
		        
		        // loop through all the events from the db (via cursor) until there are no more
		        while (!eventsCursor.isAfterLast()) {
		        	
		        	// create a new line for the diary (has time, name)
		        	LinearLayout diaryLine = new LinearLayout(this);
		        	diaryLine.setOrientation(LinearLayout.HORIZONTAL);
		        	
		        	// create a new text view for the time of event
		        	TextView eventPaddingTime = new TextView(this);
		        	eventPaddingTime.setText("\t \t \t \t \t \t" + (eventsCursor
							.getString(DatabaseHelper.EVENT_DATE_TIME)
							.substring(11, 16)) + " \t \t ");
		        	
		        	// create a new text view for the name of the event
		        	TextView eventText = new TextView(this);
		        	eventText.setText(eventsCursor.getString(DatabaseHelper.EVENT_NAME));
					
					eventText.setPadding(35, 0, 0, 0);
					
					// check if event has been done, if it has strikethrough the text
		        	if (db.isEventComplete(eventsCursor.getInt(DatabaseHelper.EVENT_ID))) {
		        		eventText.setPaintFlags(eventText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		        	}
		        	
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
		        	
					
					// add to linear layout holder
					diaryLine.addView(eventPaddingTime);
					diaryLine.addView(eventText);
					
					// add line holder to events holder
					eventsHolder.addView(diaryLine);
					eventsCursor.moveToNext();
		        }
		        
		        // tag the widget for use later when edit / delete
		        widget.setTag(R.id.diary_date, currentCursorDate);
				
				// add widget
				widgets.add(widget);
				
				// increment date
				currentCursorDate = Util.incrementDate(currentCursorDate);
				
			}

			// build the main grid view, single column, for the diary
			GridView grid = (GridView) findViewById(R.id.grid_of_widgets);
			if (grid.getAdapter() == null) {
				grid.setAdapter(new WidgetAdapter(widgets));
			} else {
				((WidgetAdapter) grid.getAdapter()).setWidgets(widgets);
				grid.invalidateViews();
			}
		}
        
        
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

        switch (item.getItemId()) {

            case R.id.action_addentry:
                i = new Intent(MainActivity.this,
                        AddEntryActivity.class);
                MainActivity.this.startActivity(i);
                break;

            case R.id.action_settings:
                i = new Intent(MainActivity.this,
                        SettingsActivity.class);
                MainActivity.this.startActivity(i);
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
     * Method that deals with onClick events for This Week.
     * 
     * @param v		View
     */
    public void clickThisWeek (View v) {
    	
    	// get the current week start and end dates
    	String[] thisWeekDates = Util.getCurrentWeekDates().split("#");
    	
    	// set the selected week start and end dates as this weeks start and end dates
    	selectedWeekStartDate = thisWeekDates[0];
        selectedWeekEndDate = thisWeekDates[1];
        
        widgets.clear();
        
        setUpWidgets();
    }
    
    /**
     * Method that deals with onClick events for Next Week.
     * 
     * @param v		View
     */
    public void clickNextWeek (View v) {
    	
    	// get next weeks start and end dates
    	String[] nextWeekDates = Util.getNextWeekDates(selectedWeekStartDate).split("#");
    	
    	// set the selected week start and end dates as next weeks start and end dates
    	selectedWeekStartDate = nextWeekDates[0];
    	selectedWeekEndDate = Util.getTomorrowsDate(nextWeekDates[1]);
        
        setUpWidgets();
    }
    
    /**
     * Method that deals with onClick events for Last Week.
     * 
     * @param v		View
     */
    public void clickLastWeek (View v) {
    	
    	// get last weeks start and end dates
    	String[] lastWeekDates = Util.getLastWeekDates(selectedWeekStartDate).split("#");
    	
    	// set the selected week start and end dates as last weeks start and end dates
        selectedWeekStartDate = lastWeekDates[0];
        selectedWeekEndDate = Util.getTomorrowsDate(lastWeekDates[1]);
        
        widgets.clear();
        
        setUpWidgets();
    }
    
    /**
     * Method that deals with onClick events when the user clicks on a widget.
     * 
     * Launches the daily activity.
     * 
     * @param v		View
     */
	public void clickWidget(View v) {
		
		// get info from widget view
		LinearLayout selectedWidget = (LinearLayout) v;
		String date = (String) selectedWidget.getTag(R.id.diary_date);
		
		// set the selected date to the date of the widget selected
		selectedDate = date;
		
		// launch the daily activity
		Intent i = new Intent(MainActivity.this, DailyActivity.class);
		i.putExtra("SelectedDate", selectedDate);
		startActivity(i);
		MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
