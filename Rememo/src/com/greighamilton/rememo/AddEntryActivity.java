package com.greighamilton.rememo;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.greighamilton.rememo.data.DatabaseHelper;
import com.greighamilton.rememo.util.Util;

/**
 * Created by Greig Hamilton on 10/06/13.
 * 
 * Class activity for adding new events to the diary.
 */
public class AddEntryActivity extends Activity {

    private int day;
    private int month;
    private int year;
    
    private static int minute;
    private static int hour;
    
    private DatabaseHelper db;
    
    private Bundle extras;
    private int currentId;
    
    private AlarmManager alarmManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        db = DatabaseHelper.getInstance(this);

        setContentView(R.layout.activity_addentry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addentry, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        db = DatabaseHelper.getInstance(this);

        // get the current date and time
        day = Util.getTodaysDay();
        month = Util.getTodaysMonth();
        year = Util.getTodaysYear();
        
        minute = Util.getCurrentMinute();
        hour = Util.getCurrentHour();
        
        // check if extras (i.e. it is an edit)
        extras = getIntent().getExtras();

        // if there are extras, then this is an update to a current event
        if (extras != null) {
        	
            currentId = extras.getInt("eventId");
            String eventName = extras.getString("eventName");
            String eventDateTime = extras.getString("eventDateTime");
            String eventDate = eventDateTime.substring(8, 10) + "-" + eventDateTime.substring(5, 7) + "-" + eventDateTime.substring(0, 4);
            String eventTime = eventDateTime.substring(11, 13) + ":" + eventDateTime.substring(14, 16);
            int eventCircled = extras.getInt("eventCircled");
        	int eventUnderlined = extras.getInt("eventUnderlined");
        	int eventStarred = extras.getInt("eventStarred");
        	String eventNotes = extras.getString("eventNotes");
            
        	TextView editEventName = (TextView) findViewById(R.id.entry_name);
        	editEventName.setText(eventName);
            
            Button editEventDate = (Button) findViewById(R.id.entry_date);
            editEventDate.setText(eventDate);
            
            Button editEventTime = (Button) findViewById(R.id.entry_time);
            editEventTime.setText(eventTime);
           
            CheckBox editEventCircled = (CheckBox) findViewById(R.id.entry_circled);
            if (eventCircled == 1) editEventCircled.setChecked(true);
            else editEventCircled.setChecked(false);
            	
            CheckBox editEventUnderlined = (CheckBox) findViewById(R.id.entry_underlined);
            if (eventUnderlined == 1) editEventUnderlined.setChecked(true);
            else editEventUnderlined.setChecked(false);
            
            CheckBox editEventStarred = (CheckBox) findViewById(R.id.entry_starred);
            if (eventStarred == 1) editEventStarred.setChecked(true);
            else editEventStarred.setChecked(false);
            
            TextView editEventNotes = (TextView) findViewById(R.id.entry_notes);
            editEventNotes.setText(eventNotes);
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.addentry_menu_cancel:
			finish();
			break;

		case R.id.addentry_menu_save:

			// get all the date entered by the user
			EditText nameBox = (EditText) findViewById(R.id.entry_name);
			Button dateButton = (Button) findViewById(R.id.entry_date);
			Button timeButton = (Button) findViewById(R.id.entry_time);

			// set errors if required information hasn't been completed by the user
			if (nameBox.getText().toString().length() <= 0 || dateButton.getText().equals("Date") || timeButton.getText().equals("Time")) {
				if (nameBox.getText().toString().length() == 0) nameBox.setError("Name is required.");
				if (dateButton.getText().equals("Date")) dateButton.setError("Date is required.");
				if (timeButton.getText().equals("Time")) timeButton.setError("Time is required.");
			}
			
			// if everything has been entered correctly, continue
			else {

				// Get name data
				String name = ((EditText) findViewById(R.id.entry_name))
						.getText().toString();

				// Get date and time
				String date = Util.makeDateString(day, month, year);
				String time = Util.makeTimeString(hour, minute);

				String date_time = date + " " + time + ":00.000";

				// Get highlighting options selected
				int circled;
				final CheckBox circledCheckBox = (CheckBox) findViewById(R.id.entry_circled);
				if (circledCheckBox.isChecked())
					circled = 1;
				else
					circled = 0;

				int underlined;
				final CheckBox underlinedCheckBox = (CheckBox) findViewById(R.id.entry_underlined);
				if (underlinedCheckBox.isChecked())
					underlined = 1;
				else
					underlined = 0;

				int starred;
				final CheckBox starredCheckBox = (CheckBox) findViewById(R.id.entry_starred);
				if (starredCheckBox.isChecked())
					starred = 1;
				else
					starred = 0;

				// Get notes data
				String notes;
				if (((EditText) findViewById(R.id.entry_notes)).getText()
						.length() == 0)
					notes = "";
				else
					notes = ((EditText) findViewById(R.id.entry_notes))
							.getText().toString();

				// ------------------------------------
				// add alarm for event

				// ---use the AlarmManager to trigger an alarm---
				alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				// ---get current date and time---
				Calendar calendar = Calendar.getInstance();

				// ---sets the time for the alarm to trigger---
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, month - 1); // seems to be 1 off
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				calendar.set(Calendar.SECOND, 0);
				
				String dateTimeText = year + "-";
				if (month < 10)
					dateTimeText += "0" + month + "-";
				else
					dateTimeText += month + "-";
				if (day < 10)
					dateTimeText += "0" + day;
				else
					dateTimeText += day;
				dateTimeText += " ";
				if (hour < 10)
					dateTimeText += "0" + hour;
				else
					dateTimeText += hour;
				dateTimeText += ":";
				if (minute < 10)
					dateTimeText += "0" + minute;
				else
					dateTimeText += minute;
				dateTimeText += ":00.000";

				// ---PendingIntent to launch activity when the alarm
				// triggers---
				Intent i = new Intent(
						"com.greighamilton.rememo.reminders.DisplayNotification");

				// ---assign an ID of 1---
				int nextId = db.nextEventID();
				i.putExtra("EventId", nextId);
				i.putExtra("EventName", name);
				i.putExtra("EventDate", dateTimeText);
				i.putExtra("EventCircled", circled);
				i.putExtra("EventUnderlined", underlined);
				i.putExtra("EventStarred", starred);
				i.putExtra("EventNotes", notes);

				PendingIntent notificationIntent = PendingIntent.getActivity(
						getBaseContext(), nextId, i, 0);

				// ---sets the alarm to trigger---
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), notificationIntent);

				// ---PendingIntent to launch activity when the alarm triggers-
				Intent j = new Intent(
						"com.greighamilton.rememo.ReminderActivity");
				j.putExtra("EventName", name);
				j.putExtra("EventDate", dateTimeText);
				j.putExtra("EventId", nextId);
				j.putExtra("EventCircled", circled);
				j.putExtra("EventUnderlined", underlined);
				j.putExtra("EventStarred", starred);
				j.putExtra("EventNotes", notes);
				
				PendingIntent alarmIntent = PendingIntent.getActivity(
						getBaseContext(), nextId, j, 0);

				// ---sets the alarm to trigger---
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), alarmIntent);

				// ----------------------------------

				if (extras != null) {
					// remove the original reminder and notification
					removeReminder(currentId, name, dateTimeText, circled, underlined, starred, notes);
					
					// update the database
					db.updateEvent(currentId, name, date_time, circled, underlined, starred, notes);
				}
				else {
					// add event to db
					db.addEvent(nextId, name, dateTimeText, circled, underlined, starred, notes);
				}
				

				finish();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method to remove an existing reminder and set a new one (after an event update)
	 * 
	 * @param id				the event id
	 * @param name				the event name
	 * @param dateTimeText		the event date and time
	 * @param circled			event circled (1 if true)
	 * @param underlined		event underlined (1 if true)
	 * @param starred			event starred (1 if true)
	 * @param notes				the event notes
	 */
	private void removeReminder(int id, String name, String dateTimeText, int circled, int underlined, int starred, String notes) {
		// remove the original reminder and notification
		Intent i = new Intent(
				"com.greighamilton.rememo.reminders.DisplayNotification");

		// ---assign an ID of 1---
		i.putExtra("EventId", id);
		i.putExtra("EventName", name);
		i.putExtra("EventDate", dateTimeText);
		i.putExtra("EventCircled", circled);
		i.putExtra("EventUnderlined", underlined);
		i.putExtra("EventStarred", starred);
		i.putExtra("EventNotes", notes);

		PendingIntent notificationIntent = PendingIntent.getActivity(
				getBaseContext(), id, i, 0);

		// ---sets the alarm to trigger---
		alarmManager.cancel(notificationIntent);

		// ---PendingIntent to launch activity when the alarm triggers-
		Intent j = new Intent(
				"com.greighamilton.rememo.ReminderActivity");
		j.putExtra("EventName", name);
		j.putExtra("EventDate", dateTimeText);
		j.putExtra("EventId", id);
		j.putExtra("EventCircled", circled);
		j.putExtra("EventUnderlined", underlined);
		j.putExtra("EventStarred", starred);
		j.putExtra("EventNotes", notes);
		
		PendingIntent alarmIntent = PendingIntent.getActivity(
				getBaseContext(), id, j, 0);

		// ---sets the alarm to trigger---
		alarmManager.cancel(alarmIntent);
	}

	/**
	 * Method called when the user clicks on the date button.
	 * Calls a dialog fragment date picker
	 * 
	 * @param view		View
	 */
	public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
    }
    
	/**
	 * Method called when the user clicks on the time button.
	 * Calls a dialog fragment date picker
	 * 
	 * @param view		View
	 */
    public void selectTime(View view) {
    	DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * Method that populates the date button text, after the user picks a date.
     * 
     * @param year		year selected by user
     * @param month		month selected by user
     * @param day		day selected by user
     */
    public void populateSetDate(int year, int month, int day) {
        // Add selected date text to button
        Button button = (Button) findViewById(R.id.entry_date);
        button.setText(day + "-" + month + "-" + year);
    }
    
    /**
     * Method that populates the time button text, after the user picks a time.
     * 
     * @param hour		hour selected by user
     * @param minute	minute selected by user
     */
    public void populateSetTime(int hour, int minute) {
        // Add selected time text to button
        Button button = (Button) findViewById(R.id.entry_time);
        String hhh;
        String mmm;
        if (hour < 10) hhh = "0"+hour; else hhh = ""+hour;
        if (minute < 10) mmm = "0"+minute; else mmm = ""+minute;
        button.setText(hhh + ":" + mmm);
    }

    /**
     * Date picker fragment class.
     *
     * @author Greig Hamilton
     *
     */
    @SuppressLint("ValidFragment")
	public class SelectDateFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            year = yy;
            month = mm + 1;
            day = dd;
            populateSetDate(yy, mm + 1, dd);
        }
    }
    
    /**
	 * Time picker fragment class.
	 * 
	 * @author Greig Hamilton
	 * 
	 */
	@SuppressLint("ValidFragment")
	public class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			// Do something with the time chosen by the user
			hour = hourOfDay;
			minute = min;
			
			int h = hourOfDay;
			int m = min;
			populateSetTime(h, m);
		}
	}
}