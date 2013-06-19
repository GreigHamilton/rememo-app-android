package com.greighamilton.rememo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras(); 
		String dateSelected;

		if (extras != null) {
			dateSelected = extras.getString("date");
			//currentDate = dateSelected;
			Log.i("DAILY DATE", dateSelected);
		}
		
			currentDate = Util.getTodaysDate();
		
		
		selectedDate = currentDate;

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

    @SuppressWarnings("deprecation")
	private void setUpWidgets() {

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
		TextView day = (TextView) widget.findViewById(R.id.day_text);
		TextView date = (TextView) widget.findViewById(R.id.date_text);
		LinearLayout eventsHolder = (LinearLayout) widget.findViewById(R.id.diary_appointments_container);

		// day
		day.setText(" "+Util.getDayOfWeek(selectedDate));
		day.setTypeface(null, Typeface.BOLD);

		// date
		date.setText("  "+selectedDate.substring(8, 10) + " " + Util.getMonthText(selectedDate) + " " + selectedDate.substring(0, 4));
		date.setTypeface(null, Typeface.ITALIC);
		
		// events
        Cursor eventsCursor = db.getEventsByDate(selectedDate, Util.getTomorrowsDate(selectedDate), true);
        eventsCursor.moveToFirst();
        
        while (!eventsCursor.isAfterLast()) {		        	
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
			
			
			eventsCursor.moveToNext();
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
