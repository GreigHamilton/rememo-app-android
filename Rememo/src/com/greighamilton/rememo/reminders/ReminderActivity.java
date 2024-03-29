package com.greighamilton.rememo.reminders;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greighamilton.rememo.MainActivity;
import com.greighamilton.rememo.R;
import com.greighamilton.rememo.data.DatabaseHelper;
import com.greighamilton.rememo.util.Util;

/**
 * Created by Greig Hamilton on 10/06/13.
 * 
 * Class activity for reminders.
 */
public class ReminderActivity extends Activity {
	
	private DatabaseHelper db;
	
	private AlarmManager alarmManager;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	
	private String colourString;
	private String textColourString;
	
	private int eventId;
	private String eventName;
	private String eventDate;
	private int circled;
	private int underlined;
	private int starred;
	private String eventNotes;
	
	private SharedPreferences sp;
	
	private Bundle extras;
	
	private TextView reminderText;
	private LinearLayout reminderBackground;
	
	private Button changeButton;
	private Button doneButton;
	private Button laterButton;
	private TextView tapMeText;
	
	private MediaPlayer mp;
	
	private Handler mHandler;
	
	private int eventOptions;
	
	private boolean reminderNotClicked;
	
	private int soundRepPeriod;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        extras = getIntent().getExtras();
        
        // set that reminder hasnt been clicked yet
        reminderNotClicked = true;
        
        // set the view
        setContentView(R.layout.activity_reminder);
        
        // get shared preferences
        sp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());	
        colourString = sp.getString("COLOUR", "");
        
        textColourString = sp.getString("TEXTCOLOUR", "");
        
        db = DatabaseHelper.getInstance(this);
        
        // set visibility of widgets
        tapMeText = (TextView) findViewById(R.id.reminder_taphere);
        try {
        	tapMeText.setTextColor(Color.parseColor(textColourString));
        } catch (Exception e) {
        	e.printStackTrace();
        	tapMeText.setTextColor(Color.BLACK);
        }
        tapMeText.setVisibility(View.VISIBLE);
        
        reminderText = (TextView) findViewById(R.id.reminder_text);
        reminderText.setText(getIntent().getExtras().getString("EventName"));
        try {
        	reminderText.setTextColor(Color.parseColor(textColourString));
        } catch (Exception e) {
        	e.printStackTrace();
        	reminderText.setTextColor(Color.BLACK);
        }
        
        reminderBackground = (LinearLayout) findViewById(R.id.reminder_colour);
        try {
        	reminderBackground.setBackgroundColor(Color.parseColor(colourString));
        } catch (Exception e) {
        	e.printStackTrace();
        	reminderBackground.setBackgroundColor(Color.GRAY);
        }
        
        // get all the extras associated with the reminder
        eventDate = extras.getString("EventDate");
        eventId = extras.getInt("EventId");
        eventName = extras.getString("EventName");
        circled = extras.getInt("EventCircled");
		underlined = extras.getInt("EventUnderlined");
		starred = extras.getInt("EventStarred");
		eventNotes = extras.getString("EventNotes");
		
		Log.i("REM EXTRAS NAME", eventName);
		
        
		// get the date and time information
        year = Integer.parseInt(eventDate.substring(0, 4));
        month = Integer.parseInt(eventDate.substring(5, 7));
        day = Integer.parseInt(eventDate.substring(8, 10));
        hour = Integer.parseInt(eventDate.substring(11, 13));
        minute = Integer.parseInt(eventDate.substring(14, 16));
        
        // ---look up the notification manager service---
     	NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

     	// ---cancel the notification---
     	nm.cancel(getIntent().getExtras().getInt("EventId"));
     	
     	
     	// hide buttons and text on screen until clicked
     	changeButton = (Button) findViewById(R.id.reminder_change);
     	changeButton.setVisibility(View.GONE);
     	
    	doneButton = (Button) findViewById(R.id.reminder_done);
    	doneButton.setVisibility(View.GONE);
    	
    	laterButton = (Button) findViewById(R.id.reminder_later);
    	laterButton.setVisibility(View.GONE);
    	
    	reminderText.setVisibility(View.GONE);
    	
    	// start music and vibration TODO
    	final int resource = sp.getInt("MUSIC", 0); // set default
    	final int resourceTime = sp.getInt("MUSIC_TIME", 10);
    	
    	soundRepPeriod = sp.getInt("MUSIC_REP", 0);
    	
    	mHandler = new Handler();
    	
    	// check if music is to be played
    	if (resource != 0) {
    		
    		// if repetition is set
    		if (soundRepPeriod != 0) {
    			// create a new thread for the audio in reminder
            	new Thread(new Runnable() {
                    @Override
                    public void run() {
                    	
                        while (reminderNotClicked) {
                        	
                        	mp = MediaPlayer.create(getApplicationContext(), resource);
                        	
                        	try {
                    			mp.prepare();
                    		} catch (IllegalStateException e) {
                    			// TODO Auto-generated catch block
                    			e.printStackTrace();
                    		} catch (IOException e) {
                    			// TODO Auto-generated catch block
                    			e.printStackTrace();
                    		}
                        	
                        	// start the audio stream
                        	mp.start();
                        	
                        	// timer task to enable customisable timing of audio
                            TimerTask task = new TimerTask() {

                                @Override
                                public void run() {
                                	
                                    // stop the timer after a set period of time
                                    mp.stop();
                                }
                            };
                            
                            Timer t = new Timer();
                            
                            // check if the audio is an earcon, if so then set schedule time to 1 sec default.
                            if (resource == R.raw.earcon1 || resource == R.raw.earcon2 || resource == R.raw.earcon3 || resource == R.raw.earcon4 || resource == R.raw.earcon5) {
                                t.schedule(task, 1000);
                            }
                            else {
                            	
                            	// set time (second parameter) for the reminder
                                t.schedule(task, resourceTime*1000);
                            }
                        	
                            try {
                            	// set the time for it to repeat for each case (original time + interval)
                            	if (soundRepPeriod == 1) Thread.sleep((resourceTime*1000) + 30000); // 30 secs
                            	else if (soundRepPeriod == 2) Thread.sleep((resourceTime*1000) + 60000); // 1 min
                            	else if (soundRepPeriod == 3) Thread.sleep((resourceTime*1000) + 300000); // 5 min
                            	else if (soundRepPeriod == 4) Thread.sleep((resourceTime*1000) + 600000); // 10 min
                                
                                mHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                    	
                                    	// TODO
                                    }
                                });
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                    }
                }).start();
    		}
    		
    		// no repetition wanted
    		else {
    			
    			mp = MediaPlayer.create(getApplicationContext(), resource);
            	
            	try {
        			mp.prepare();
        		} catch (IllegalStateException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            	
    			// start the audio stream
            	mp.start();
            	
            	// timer task to enable customisable timing of audio
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                    	
                        // stop the timer after a set period of time
                        mp.stop();
                    }
                };
                
                Timer t = new Timer();
                
                // check if the audio is an earcon, if so then set schedule time to 1 sec default.
                if (resource == R.raw.earcon1 || resource == R.raw.earcon2 || resource == R.raw.earcon3 || resource == R.raw.earcon4 || resource == R.raw.earcon5) {
                    t.schedule(task, 1000);
                }
                else {
                	
                	// set time (second parameter) for the reminder
                    t.schedule(task, resourceTime*1000);
                }
    		}
    		
    	}
    }
    
    /**
     * Method called when user clicks the reminder screen.
     * Sets the text and buttons to visible.
     * 
     * @param v		View
     */
    public void clickReminderScreen(View v) {
    	
    	reminderNotClicked = false;
    	
    	changeButton.setVisibility(View.VISIBLE);
     	
    	doneButton.setVisibility(View.VISIBLE);
    	
    	laterButton.setVisibility(View.VISIBLE);
    	
    	reminderText.setVisibility(View.VISIBLE);
    	
    	tapMeText.setVisibility(View.GONE);
    	
    	extras.clear();
    }
    
    /**
     * Method called when the user clicks on the done button.
     * Finds the original alarm and notification created, and removes it.
     * 
     * @param v		View
     */
    public void clickReminderDone(View v) {
    	
    	alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    	
    	// ---PendingIntent to launch activity when the alarm
		// triggers---
		Intent i = new Intent(
				"com.greighamilton.rememo.reminders.DisplayNotification");

		// ---assign an ID ---
		i.putExtra("EventId", eventId);
		i.putExtra("EventName", eventName);
		i.putExtra("EventDate", eventDate);
		i.putExtra("EventCircled", circled);
		i.putExtra("EventUnderlined", underlined);
		i.putExtra("EventStarred", starred);
		i.putExtra("EventNotes", eventNotes);

		PendingIntent notificationIntent = PendingIntent.getActivity(
				getBaseContext(), eventId, i, 0);


		// ---PendingIntent to launch activity when the alarm triggers-
		Intent j = new Intent(
				"com.greighamilton.rememo.ReminderActivity");
		j.putExtra("EventName", eventName);
		j.putExtra("EventId", eventId);
		j.putExtra("EventDate", eventDate);
		j.putExtra("EventCircled", circled);
		j.putExtra("EventUnderlined", underlined);
		j.putExtra("EventStarred", starred);
		j.putExtra("EventNotes", eventNotes);
		
		PendingIntent alarmIntent = PendingIntent.getActivity(
				getBaseContext(), eventId, j, 0);


		// ---deletes the alarm to trigger---
		
		try {
			alarmManager.cancel(alarmIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			alarmManager.cancel(notificationIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// add this event to complete events
		db.addToCompleteEvents(eventId);
		
		
		finish();
    	
    	Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
    
    /**
     * Method called when user clicks on later button
     * Finds the original alarm and notification and resets it to the new time
     * selected by the user.
     * 
     * @param v		View
     */
    public void clickReminderLater(View v) {
    	
    	alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    	
    	// ---PendingIntent to launch activity when the alarm
		// triggers---
		Intent i = new Intent(
				"com.greighamilton.rememo.reminders.DisplayNotification");

		// ---assign an ID ---
		i.putExtra("EventId", eventId);
		i.putExtra("EventName", eventName);
		i.putExtra("EventDate", eventDate);
		i.putExtra("EventCircled", circled);
		i.putExtra("EventUnderlined", underlined);
		i.putExtra("EventStarred", starred);
		i.putExtra("EventNotes", eventNotes);

		PendingIntent notificationIntent = PendingIntent.getActivity(
				getBaseContext(), eventId, i, 0);


		// ---PendingIntent to launch activity when the alarm triggers-
		Intent j = new Intent(
				"com.greighamilton.rememo.ReminderActivity");
		j.putExtra("EventName", eventName);
		j.putExtra("EventId", eventId);
		j.putExtra("EventDate", eventDate);
		j.putExtra("EventCircled", circled);
		j.putExtra("EventUnderlined", underlined);
		j.putExtra("EventStarred", starred);
		j.putExtra("EventNotes", eventNotes);
		
		PendingIntent alarmIntent = PendingIntent.getActivity(
				getBaseContext(), eventId, j, 0);


		// ---deletes the alarm to trigger---
		
		alarmManager.cancel(alarmIntent);
		
		alarmManager.cancel(notificationIntent);

    	
    	DialogFragment newFragment = new PostponeReminderPickerFragment();
		newFragment.show(getFragmentManager(), "postponePicker");
    }
    
    public void clickReminderChange(View v) {
    	DialogFragment newFragment = new ChangeReminderPickerFragment();
		newFragment.show(getFragmentManager(), "changePicker");
    }
    
    /**
	 * Class for reminder picker fragment.
	 * 
	 * @author Greig Hamilton
	 *
	 */
	@SuppressLint("ValidFragment")
	public class PostponeReminderPickerFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("When would you like reminded again?");
			
			// get items for dialog
			final String[] delayTimes;
			
			
			delayTimes = getResources().getStringArray(R.array.reminder_delay_times);
			
			builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                dismiss();
                }
            });
			
			builder.setItems(delayTimes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							// The 'which' argument contains the index position
							// of the selected item
							String selectedDelay = delayTimes[item];
							
							Cursor e = db.getEvent(eventId);
							e.moveToFirst();
							
							eventOptions = e.getInt(DatabaseHelper.EVENT_OPTIONS);
							
							// ---get current date and time---
							Calendar calendar = Calendar.getInstance();

							// ---sets the time for the alarm to trigger---
							calendar.set(Calendar.YEAR, year);
							calendar.set(Calendar.MONTH, month - 1); // seems to be 1 off
							calendar.set(Calendar.DAY_OF_MONTH, day);
							calendar.set(Calendar.HOUR_OF_DAY, hour);
							calendar.set(Calendar.MINUTE, minute);
							calendar.set(Calendar.SECOND, 0);
							
							if (delayTimes[item].equals("10 minutes")) {
								calendar.add(Calendar.MINUTE, 10);
								Toast.makeText(getApplicationContext(), "You will be reminded again in 10 minutes.", Toast.LENGTH_LONG).show();
							}
							if (delayTimes[item].equals("30 minutes")) {
								calendar.add(Calendar.MINUTE, 30);
								Toast.makeText(getApplicationContext(), "You will be reminded again in 30 minutes.", Toast.LENGTH_LONG).show();
							}
							if (delayTimes[item].equals("1 hour")) {
								calendar.add(Calendar.HOUR, 1);
								Toast.makeText(getApplicationContext(), "You will be reminded again in 1 hour.", Toast.LENGTH_LONG).show();
							}
							if (delayTimes[item].equals("Tomorrow")) {
								calendar.add(Calendar.DAY_OF_MONTH, 1);
								Toast.makeText(getApplicationContext(), "You will be reminded again tomorrow.", Toast.LENGTH_LONG).show();
								
								// increment the date and update the event
								String tomorrowDate = Util.getTomorrowsDate(eventDate);
								String tomorrowTime = eventDate.substring(11);
								String timeTomorrow = tomorrowDate + " " + tomorrowTime;
								db.updateEvent(eventId, eventName, timeTomorrow, circled, underlined, starred, eventNotes, eventOptions);
							}
							if (delayTimes[item].equals("Next Week")) {
								calendar.add(Calendar.DAY_OF_MONTH, 7);
								Toast.makeText(getApplicationContext(), "You will be reminded again next week.", Toast.LENGTH_LONG).show();
								
								// increment the date and update the event
								String nextWeekDate = Util.getNextWeeksDate(eventDate);
								String nextWeekTime = eventDate.substring(11);
								String timeNextWeek = nextWeekDate + " " + nextWeekTime;
								db.updateEvent(eventId, eventName, timeNextWeek, circled, underlined, starred, eventNotes, eventOptions);
							}
							
							// ------------------------------------
							// add alarm for event

							// ---use the AlarmManager to trigger an alarm---
							alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

							

							// ---PendingIntent to launch activity when the alarm
							// triggers---
							Intent i = new Intent(
									"com.greighamilton.rememo.reminders.DisplayNotification");

							// ---assign an ID ---
							i.putExtra("EventId", eventId);
							i.putExtra("EventName", eventName);
							i.putExtra("EventDate", eventDate);
							i.putExtra("EventCircled", circled);
							i.putExtra("EventUnderlined", underlined);
							i.putExtra("EventStarred", starred);
							i.putExtra("EventNotes", eventNotes);

							PendingIntent notificationIntent = PendingIntent.getActivity(
									getBaseContext(), eventId, i, 0);

							// ---sets the alarm to trigger---
							alarmManager.set(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), notificationIntent);

							// ---PendingIntent to launch activity when the alarm triggers-
							Intent j = new Intent(
									"com.greighamilton.rememo.ReminderActivity");
							j.putExtra("EventName", eventName);
							j.putExtra("EventId", eventId);
							j.putExtra("EventDate", eventDate);
							j.putExtra("EventCircled", circled);
							j.putExtra("EventUnderlined", underlined);
							j.putExtra("EventStarred", starred);
							j.putExtra("EventNotes", eventNotes);
							
							PendingIntent alarmIntent = PendingIntent.getActivity(
									getBaseContext(), eventId, j, 0);

							// ---sets the alarm to trigger---
							alarmManager.set(AlarmManager.RTC_WAKEUP,
									calendar.getTimeInMillis(), alarmIntent);

							// ----------------------------------
							
							
							Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					    	startActivity(intent);
						}
					});
			return builder.create();
		}
	}
	
	/**
	 * Class for reminder picker fragment.
	 * 
	 * @author Greig Hamilton
	 *
	 */
	@SuppressLint("ValidFragment")
	public class ChangeReminderPickerFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("What colour would you like to change it to?");
			
			// get items for dialog
			final String[] colours;
			
			colours = getResources().getStringArray(R.array.reminder_colours);
			
			builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                dismiss();
                }
            });
			
			builder.setItems(colours,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							// The 'which' argument contains the index position
							// of the selected item
							
							LinearLayout reminderBackground = (LinearLayout) findViewById(R.id.reminder_colour);
							
							if (colours[item].equals("Red")) {
								
								sp.edit().putString("COLOUR", "#F21818").commit();
								
								try {
						        	reminderBackground.setBackgroundColor(Color.parseColor("#F21818"));
						        } catch (Exception e) {
						        	e.printStackTrace();
						        	reminderBackground.setBackgroundColor(Color.GRAY);
						        }
							}
							
							if (colours[item].equals("Pink")) {
								
								sp.edit().putString("COLOUR", "#EB1CAD").commit();
								
								try {
						        	reminderBackground.setBackgroundColor(Color.parseColor("#EB1CAD"));
						        } catch (Exception e) {
						        	e.printStackTrace();
						        	reminderBackground.setBackgroundColor(Color.GRAY);
						        }
							}

							if (colours[item].equals("Green")) {
								
								sp.edit().putString("COLOUR", "#3ECF4F").commit();
								
								try {
						        	reminderBackground.setBackgroundColor(Color.parseColor("#3ECF4F"));
						        } catch (Exception e) {
						        	e.printStackTrace();
						        	reminderBackground.setBackgroundColor(Color.GRAY);
						        }
							}
							if (colours[item].equals("Blue")) {
								
								sp.edit().putString("COLOUR", "#1885F2").commit();
								
								try {
						        	reminderBackground.setBackgroundColor(Color.parseColor("#1885F2"));
						        } catch (Exception e) {
						        	e.printStackTrace();
						        	reminderBackground.setBackgroundColor(Color.GRAY);
						        }
							}
							if (colours[item].equals("Yellow")) {
								
								sp.edit().putString("COLOUR", "#F5F50C").commit();
								
								try {
						        	reminderBackground.setBackgroundColor(Color.parseColor("#F5F50C"));
						        } catch (Exception e) {
						        	e.printStackTrace();
						        	reminderBackground.setBackgroundColor(Color.GRAY);
						        }
							}
							if (colours[item].equals("Orange")) {
								
								sp.edit().putString("COLOUR", "#FFAB19").commit();
								
								try {
						        	reminderBackground.setBackgroundColor(Color.parseColor("#FFAB19"));
						        } catch (Exception e) {
						        	e.printStackTrace();
						        	reminderBackground.setBackgroundColor(Color.GRAY);
						        }
							}
						}
					});
			return builder.create();
		}
	}
}