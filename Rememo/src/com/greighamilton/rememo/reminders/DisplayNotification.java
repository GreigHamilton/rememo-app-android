package com.greighamilton.rememo.reminders;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.greighamilton.rememo.MainActivity;
import com.greighamilton.rememo.R;
import com.greighamilton.rememo.data.DatabaseHelper;

public class DisplayNotification extends Activity {
	
	private DatabaseHelper db;
	
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = DatabaseHelper.getInstance(this);

		// ---get the notification ID for the notification;
		// passed in by the MainActivity---
		int notifID = getIntent().getExtras().getInt("NotifID");

		// ---PendingIntent to launch activity if the user selects
		// the notification---
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra("NotifID", notifID);

		PendingIntent detailsIntent = PendingIntent.getActivity(this, notifID, i, 0);

		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.ic_launcher,
				db.getEventName(notifID), System.currentTimeMillis()); // TODO actual message
		
		// Cancel the notification after its selected
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notif.defaults |= Notification.DEFAULT_SOUND;
		notif.defaults |= Notification.DEFAULT_VIBRATE;

		notif.ledARGB = 0xff00ff00;
		notif.ledOnMS = 300;
		notif.ledOffMS = 1000;
		notif.flags |= Notification.FLAG_SHOW_LIGHTS;

		notif.flags |= Notification.FLAG_AUTO_CANCEL;

		CharSequence from = "Rememo";
		CharSequence message = "Remember " + db.getEventName(notifID);
		notif.setLatestEventInfo(this, from, message, detailsIntent);

		// ---100ms delay, vibrate for 250ms, pause for 100 ms and
		// then vibrate for 500ms---
		notif.vibrate = new long[] { 100, 250, 100, 500 };
		nm.notify(notifID, notif);
		// ---destroy the activity---
		finish();
	}
}