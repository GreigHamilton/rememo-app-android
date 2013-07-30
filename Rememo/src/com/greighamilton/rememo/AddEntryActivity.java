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
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
    
    private int eventComplete;
    
    private AlarmManager alarmManager;
    
    private Spinner remindPeriod;
    private EditText remindTime;
    
    private CheckBox remindMeBefore;
    
    private boolean edit = false;

    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        db = DatabaseHelper.getInstance(this);

        setContentView(R.layout.activity_addentry);
        
		// check if device is 4.0- or 4.0+
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH && currentapiVersion != android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
        	
			// don't do anything
		} else {
			// hide the circled button (doesn't work unless 4.0+
			CheckBox editEventCircled = (CheckBox) findViewById(R.id.entry_circled);
			editEventCircled.setVisibility(View.GONE);
		}
		
		remindPeriod = (Spinner) findViewById(R.id.remind_before_spinner);
		
		String[] array_spinner = new String[3];
        array_spinner[0]="minutes"; array_spinner[1]="hours"; array_spinner[2]="days";
        
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_spinner);
        remindPeriod.setAdapter(adapter);
        
        remindTime = (EditText) findViewById(R.id.remind_before_time);
        
        remindMeBefore = (CheckBox) findViewById(R.id.remind_me_check);
        
        remindMeBefore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

        	   @Override
        	   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        		   if (isChecked) {
        			   remindMeBefore.setText(" also remind me at " + hour + ":" + minute
        					   + " on " + day + "-" + month + "-" + year + ". ");
        		   }
        		   else {
        			   remindMeBefore.setText(" check to also remind me at time of event. ");
        		   }
        	   }
        	});
       
        TextView circled = (TextView) findViewById(R.id.entry_circled_text);
        // check if device is 4.0- or 4.0+
    	int currentapiVersion1 = android.os.Build.VERSION.SDK_INT;
    	if (currentapiVersion1 >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH && currentapiVersion1 != android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
    		
    		circled.setBackground(getResources().getDrawable(R.drawable.entry_circle));
    		
    	} else{
    	    // don't do anything
    	}
        
    	TextView underlined = (TextView) findViewById(R.id.entry_underlined_text);
        underlined.setPaintFlags(underlined.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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
        
        // check if extras (i.e. it is an edit)
        extras = getIntent().getExtras();
        

        // if there are extras, then this is an update to a current event
        if (extras != null) {
        	
        	edit = true;
        	
        	eventComplete = extras.getInt("eventComplete");
        	
            currentId = extras.getInt("eventId");
            String eventName = extras.getString("eventName");
            String eventDateTime = extras.getString("eventDateTime");
            
            String eventDate = eventDateTime.substring(8, 10) + "-" + eventDateTime.substring(5, 7) + "-" + eventDateTime.substring(0, 4);
            String eventTime = eventDateTime.substring(11, 13) + ":" + eventDateTime.substring(14, 16);
            
            year = Integer.parseInt(eventDateTime.substring(0, 4));
            month = Integer.parseInt(eventDateTime.substring(5, 7));
            day = Integer.parseInt(eventDateTime.substring(8, 10));
            
            minute = Integer.parseInt(eventDateTime.substring(14, 16));
            hour = Integer.parseInt(eventDateTime.substring(11, 13));
           
            
            int eventCircled = extras.getInt("eventCircled");
        	int eventUnderlined = extras.getInt("eventUnderlined");
        	int eventStarred = extras.getInt("eventStarred");
        	String eventNotes = extras.getString("eventNotes");
        	
        	int eventOptions = extras.getInt("eventOptions");
            
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
            
            RadioGroup myRadioGroup = (RadioGroup) findViewById(R.id.entry_radiogroup);
            if (eventOptions == 0) {
            	myRadioGroup.check(R.id.entry_normal);
            }
            else if (eventOptions == 1) {
            	myRadioGroup.check(R.id.entry_important);
            }
            else if (eventOptions == 2) {
            	myRadioGroup.check(R.id.entry_urgent);
            }
        }
        
        else {
        	// get the current date and time
            day = Util.getTodaysDay();
            month = Util.getTodaysMonth();
            year = Util.getTodaysYear();
            
            minute = Util.getCurrentMinute();
            hour = Util.getCurrentHour();
        }
    }

	@SuppressWarnings("static-access")
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
				if (!circledCheckBox.isChecked() || circledCheckBox.GONE == 1)
					circled = 0;
				else
					circled = 1;

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
				
				int options = 0;
				RadioGroup radioGroup = (RadioGroup) findViewById(R.id.entry_radiogroup);
				int id = radioGroup.getCheckedRadioButtonId();
				if (id == -1){
				    //no item selected
				}
				else {
				    if (id == R.id.entry_normal){
				        options = 0;
				    }
				    else if (id == R.id.entry_important){
				        options = 1;
				    }
				    else if (id == R.id.entry_urgent){
				        options = 2;
				    }
				}

				int nextId = db.nextEventID();
				
				if (remindMeBefore.isChecked() || remindTime.getText().toString().equals("")) {
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
				}
				
				int repTime;
				if (remindTime.getText().toString().equals(""))
					repTime = 0;
				else
					repTime = Integer.parseInt(remindTime.getText().toString());
				
				// ---get current date and time---
				Calendar calendar = Calendar.getInstance();

				// ---sets the time for the alarm to trigger---
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, month - 1); // seems to be 1 off
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				calendar.set(Calendar.SECOND, 0);
				
				calendar.add(Calendar.MINUTE, -repTime);
				
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
				
				// check if another reminder is to be created for before the event
				if (!remindTime.getText().toString().matches("")) {
					
					
					int repPeriod = remindPeriod.getSelectedItemPosition();
					
					if (repPeriod == 0) {
						// set reminder repTime minutes before event
						// add alarm for event

						// ---use the AlarmManager to trigger an alarm---
						alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

						

						// ---PendingIntent to launch activity when the alarm
						// triggers---
						Intent i1 = new Intent(
								"com.greighamilton.rememo.reminders.DisplayNotification");

						// ---assign an ID of 1---
						i1.putExtra("EventId", nextId);
						i1.putExtra("EventName", name);
						i1.putExtra("EventDate", dateTimeText);
						i1.putExtra("EventCircled", circled);
						i1.putExtra("EventUnderlined", underlined);
						i1.putExtra("EventStarred", starred);
						i1.putExtra("EventNotes", notes);

						PendingIntent notificationIntent1 = PendingIntent.getActivity(
								getBaseContext(), nextId, i1, 0);

						// ---sets the alarm to trigger---
						alarmManager.set(AlarmManager.RTC_WAKEUP,
								calendar.getTimeInMillis(), notificationIntent1);

						// ---PendingIntent to launch activity when the alarm triggers-
						Intent j1 = new Intent(
								"com.greighamilton.rememo.ReminderActivity");
						j1.putExtra("EventName", name);
						j1.putExtra("EventDate", dateTimeText);
						j1.putExtra("EventId", nextId);
						j1.putExtra("EventCircled", circled);
						j1.putExtra("EventUnderlined", underlined);
						j1.putExtra("EventStarred", starred);
						j1.putExtra("EventNotes", notes);
						
						PendingIntent alarmIntent1 = PendingIntent.getActivity(
								getBaseContext(), nextId, j1, 0);

						// ---sets the alarm to trigger---
						alarmManager.set(AlarmManager.RTC_WAKEUP,
								calendar.getTimeInMillis(), alarmIntent1);

						// ----------------------------------
					}
					else if (repPeriod == 1) {
						// set reminder repTime hours before event
						// add alarm for event

						// ---use the AlarmManager to trigger an alarm---
						alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

						// ---get current date and time---
						Calendar calendar11 = Calendar.getInstance();

						// ---sets the time for the alarm to trigger---
						calendar11.set(Calendar.YEAR, year);
						calendar11.set(Calendar.MONTH, month - 1); // seems to be 1 off
						calendar11.set(Calendar.DAY_OF_MONTH, day);
						calendar11.set(Calendar.HOUR_OF_DAY, hour);
						calendar11.set(Calendar.MINUTE, minute);
						calendar11.set(Calendar.SECOND, 0);
						
						calendar11.add(Calendar.HOUR_OF_DAY, -repTime);
						
						String dateTimeText11 = year + "-";
						if (month < 10)
							dateTimeText11 += "0" + month + "-";
						else
							dateTimeText11 += month + "-";
						if (day < 10)
							dateTimeText11 += "0" + day;
						else
							dateTimeText11 += day;
						dateTimeText11 += " ";
						if (hour < 10)
							dateTimeText11 += "0" + hour;
						else
							dateTimeText11 += hour;
						dateTimeText11 += ":";
						if (minute < 10)
							dateTimeText11 += "0" + minute;
						else
							dateTimeText11 += minute;
						dateTimeText11 += ":00.000";

						// ---PendingIntent to launch activity when the alarm
						// triggers---
						Intent i1 = new Intent(
								"com.greighamilton.rememo.reminders.DisplayNotification");

						// ---assign an ID of 1---
						i1.putExtra("EventId", nextId);
						i1.putExtra("EventName", name);
						i1.putExtra("EventDate", dateTimeText11);
						i1.putExtra("EventCircled", circled);
						i1.putExtra("EventUnderlined", underlined);
						i1.putExtra("EventStarred", starred);
						i1.putExtra("EventNotes", notes);

						PendingIntent notificationIntent1 = PendingIntent.getActivity(
								getBaseContext(), nextId, i1, 0);

						// ---sets the alarm to trigger---
						alarmManager.set(AlarmManager.RTC_WAKEUP,
								calendar11.getTimeInMillis(), notificationIntent1);

						// ---PendingIntent to launch activity when the alarm triggers-
						Intent j1 = new Intent(
								"com.greighamilton.rememo.ReminderActivity");
						j1.putExtra("EventName", name);
						j1.putExtra("EventDate", dateTimeText11);
						j1.putExtra("EventId", nextId);
						j1.putExtra("EventCircled", circled);
						j1.putExtra("EventUnderlined", underlined);
						j1.putExtra("EventStarred", starred);
						j1.putExtra("EventNotes", notes);
						
						PendingIntent alarmIntent1 = PendingIntent.getActivity(
								getBaseContext(), nextId, j1, 0);

						// ---sets the alarm to trigger---
						alarmManager.set(AlarmManager.RTC_WAKEUP,
								calendar11.getTimeInMillis(), alarmIntent1);

						// ----------------------------------
					}
					else {
						// set reminder repTime days before event
						// add alarm for event

						// ---use the AlarmManager to trigger an alarm---
						alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

						// ---get current date and time---
						Calendar calendar11 = Calendar.getInstance();

						// ---sets the time for the alarm to trigger---
						calendar11.set(Calendar.YEAR, year);
						calendar11.set(Calendar.MONTH, month - 1); // seems to be 1 off
						calendar11.set(Calendar.DAY_OF_MONTH, day);
						calendar11.set(Calendar.HOUR_OF_DAY, hour);
						calendar11.set(Calendar.MINUTE, minute);
						calendar11.set(Calendar.SECOND, 0);
						
						calendar11.add(Calendar.DAY_OF_MONTH, -repTime);
						
						String dateTimeText11 = year + "-";
						if (month < 10)
							dateTimeText11 += "0" + month + "-";
						else
							dateTimeText11 += month + "-";
						if (day < 10)
							dateTimeText11 += "0" + day;
						else
							dateTimeText11 += day;
						dateTimeText11 += " ";
						if (hour < 10)
							dateTimeText11 += "0" + hour;
						else
							dateTimeText11 += hour;
						dateTimeText11 += ":";
						if (minute < 10)
							dateTimeText11 += "0" + minute;
						else
							dateTimeText11 += minute;
						dateTimeText11 += ":00.000";

						// ---PendingIntent to launch activity when the alarm
						// triggers---
						Intent i1 = new Intent(
								"com.greighamilton.rememo.reminders.DisplayNotification");

						// ---assign an ID of 1---
						i1.putExtra("EventId", nextId);
						i1.putExtra("EventName", name);
						i1.putExtra("EventDate", dateTimeText11);
						i1.putExtra("EventCircled", circled);
						i1.putExtra("EventUnderlined", underlined);
						i1.putExtra("EventStarred", starred);
						i1.putExtra("EventNotes", notes);

						PendingIntent notificationIntent1 = PendingIntent.getActivity(
								getBaseContext(), nextId, i1, 0);

						// ---sets the alarm to trigger---
						alarmManager.set(AlarmManager.RTC_WAKEUP,
								calendar11.getTimeInMillis(), notificationIntent1);

						// ---PendingIntent to launch activity when the alarm triggers-
						Intent j1 = new Intent(
								"com.greighamilton.rememo.ReminderActivity");
						j1.putExtra("EventName", name);
						j1.putExtra("EventDate", dateTimeText11);
						j1.putExtra("EventId", nextId);
						j1.putExtra("EventCircled", circled);
						j1.putExtra("EventUnderlined", underlined);
						j1.putExtra("EventStarred", starred);
						j1.putExtra("EventNotes", notes);
						
						PendingIntent alarmIntent1 = PendingIntent.getActivity(
								getBaseContext(), nextId, j1, 0);

						// ---sets the alarm to trigger---
						alarmManager.set(AlarmManager.RTC_WAKEUP,
								calendar11.getTimeInMillis(), alarmIntent1);

						// ----------------------------------
					}
				}
				
				
				
				
				

				if (edit) {
					// remove the original reminder and notification
					removeReminder(currentId, name, dateTimeText, circled, underlined, starred, notes, options);
					
					// update the database
					db.updateEvent(currentId, name, date_time, circled, underlined, starred, notes, options);
					
					if (eventComplete == 1)
						try {
							db.deleteCompleteEvent(currentId);
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
				else {
					// add event to db
					db.addEvent(nextId, name, dateTimeText, circled, underlined, starred, notes, options);
					
					try {
						db.deleteCompleteEvent(nextId);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
	private void removeReminder(int id, String name, String dateTimeText, int circled, int underlined, int starred, String notes, int options) {
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
		try {
			alarmManager.cancel(notificationIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		try {
			alarmManager.cancel(alarmIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
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