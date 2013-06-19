package com.greighamilton.rememo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
    
	private DatabaseHelper db;
    
    private String currentWeekStartDate;
    private String currentWeekEndDate;
    
    private String selectedWeekStartDate;
    private String selectedWeekEndDate;

    //private OnNavigationListener mOnNavigationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        String[] thisWeekDates = Util.getCurrentWeekDates().split("#");
        currentWeekStartDate = thisWeekDates[0];
        currentWeekEndDate = thisWeekDates[1];
        
        selectedWeekStartDate = currentWeekStartDate;
        selectedWeekEndDate = currentWeekEndDate;
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = DatabaseHelper.getInstance(this);
        widgets = new ArrayList<LinearLayout>();

        setUpWidgets();
    }

    @SuppressWarnings("deprecation")
	private void setUpWidgets() {

        widgets.clear();

        setContentView(R.layout.activity_main);
        
        Cursor allEventsCursor = db.getEventsByDate(selectedWeekStartDate, selectedWeekEndDate, true);
        allEventsCursor.moveToFirst();
        
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
				TextView date = (TextView) widget.findViewById(R.id.date_text);
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
		        
		        while (!eventsCursor.isAfterLast()) {
		        	LinearLayout diaryLine = new LinearLayout(this);
		        	diaryLine.setOrientation(LinearLayout.HORIZONTAL);
		        	
		        	TextView eventPaddingTime = new TextView(this);
		        	eventPaddingTime.setText("\t \t \t \t \t \t" + (eventsCursor
							.getString(DatabaseHelper.EVENT_DATE_TIME)
							.substring(11, 16)) + " \t \t ");
		        	
		        	TextView eventText = new TextView(this);
					eventText.setText(eventsCursor.getString(DatabaseHelper.EVENT_NAME));
					
					eventText.setPadding(35, 0, 0, 0);
					
					// add to linear layout holder
					diaryLine.addView(eventPaddingTime);
					diaryLine.addView(eventText);
					
					// add line holder to events holder
					eventsHolder.addView(diaryLine);
					eventsCursor.moveToNext();
		        }
		        
		        widget.setTag(R.id.diary_date, date);
				
				// add widget
				widgets.add(widget);
				
				// increment date
				currentCursorDate = Util.incrementDate(currentCursorDate);
				
			}

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
            
            case R.id.action_daily:
                i = new Intent(MainActivity.this,
                        DailyActivity.class);
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
    
    public void clickThisWeek (View v) {
    	String[] thisWeekDates = Util.getCurrentWeekDates().split("#");
    	selectedWeekStartDate = thisWeekDates[0];
        selectedWeekEndDate = thisWeekDates[1];
        
        widgets.clear();
        
        setUpWidgets();
    }
    
    public void clickNextWeek (View v) {
    	String[] nextWeekDates = Util.getNextWeekDates(selectedWeekStartDate).split("#");
    	selectedWeekStartDate = nextWeekDates[0];
    	selectedWeekEndDate = Util.getTomorrowsDate(nextWeekDates[1]);
        
        setUpWidgets();
    }
    
    public void clickLastWeek (View v) {
    	String[] lastWeekDates = Util.getLastWeekDates(selectedWeekStartDate).split("#");
        selectedWeekStartDate = lastWeekDates[0];
        selectedWeekEndDate = Util.getTomorrowsDate(lastWeekDates[1]);
        
        widgets.clear();
        
        setUpWidgets();
    }
    
	public void clickWidget(View v) {
		// TODO
	}
}
