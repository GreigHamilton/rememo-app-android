package com.greighamilton.rememo.reminders;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.greighamilton.rememo.MainActivity;
import com.greighamilton.rememo.R;
import com.greighamilton.rememo.data.DatabaseHelper;

public class DisplayNotification extends Activity {
	
	private DatabaseHelper db;
	
	private SharedPreferences sp;
	private String colourString;
	
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = DatabaseHelper.getInstance(this);
		
		sp = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());	
        colourString = sp.getString("COLOUR", "");

		// ---get the notification ID for the notification;
		// passed in by the MainActivity---
		int eventId = getIntent().getExtras().getInt("EventId");
		String eventDate = getIntent().getExtras().getString("EventDate");
		String eventName = getIntent().getExtras().getString("EventName");
		int circled = getIntent().getExtras().getInt("EventCircled");
		int underlined = getIntent().getExtras().getInt("EventUnderlined");
		int starred = getIntent().getExtras().getInt("EventStarred");
		String eventNotes = getIntent().getExtras().getString("EventNotes");

		// ---PendingIntent to launch activity if the user selects
		// the notification---
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra("EventId", eventId);
		i.putExtra("EventDate", eventDate);
		i.putExtra("EventName", eventName);
		i.putExtra("EventCircled", circled);
		i.putExtra("EventUnderlined", underlined);
		i.putExtra("EventStarred", starred);
		i.putExtra("EventNotes", eventNotes);

		PendingIntent detailsIntent = PendingIntent.getActivity(this, eventId, i, 0);

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.notification_icon,
				db.getEventName(eventId), System.currentTimeMillis()); // TODO actual message
		
		// Cancel the notification after its selected
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notif.defaults |= Notification.DEFAULT_SOUND;
		notif.defaults |= Notification.DEFAULT_VIBRATE;

		// change colour of LED
		if 		(colourString.equals("#F21818")) notif.ledARGB = 0xFFF21818;
		else if (colourString.equals("#3ECF4F")) notif.ledARGB = 0xFF3ECF4F;
		else if (colourString.equals("#1885F2")) notif.ledARGB = 0xFF1885F2;
		else if (colourString.equals("#F5F50C")) notif.ledARGB = 0xFFF5F50C;
		else 									 notif.ledARGB = 0xFFFFAB19;
		
		notif.ledOnMS = 300;
		notif.ledOffMS = 1000;
		notif.flags |= Notification.FLAG_SHOW_LIGHTS;

		notif.flags |= Notification.FLAG_AUTO_CANCEL;

		CharSequence from = "Rememo";
		CharSequence message = "Remember " + eventName;
		notif.setLatestEventInfo(this, from, message, detailsIntent);

		// ---100ms delay, vibrate for 250ms, pause for 100 ms and
		// then vibrate for 500ms---
		notif.vibrate = new long[] { 100, 250, 100, 500 };
		nm.notify(eventId, notif);
		// ---destroy the activity---
		finish();
	}
}