package com.greighamilton.rememo;

import java.util.ArrayList;

import com.greighamilton.rememo.data.DatabaseHelper;
import com.greighamilton.rememo.data.SettingsActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

/**
 * Class for Incomplete Events activity.
 * 
 * @author Greig Hamilton
 *
 */
public class IncompleteActivity extends Activity {
	
	private DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incomplete);
		// Show the Up button in the action bar.
		setupActionBar();
		
		db = DatabaseHelper.getInstance(this);
		
		doMySearch();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_incomplete, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent i;

		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.menu_about:
            i = new Intent(IncompleteActivity.this, AboutActivity.class);
            startActivity(i);
            break;

		case R.id.action_settings:
			i = new Intent(IncompleteActivity.this, SettingsActivity.class);
			IncompleteActivity.this.startActivity(i);
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void doMySearch() {
    	
    	ArrayList<String[]> returnedData;
    	
    	String[] results;
    	final String[] ids;
    	
    	// query the database for incomplete tasks
    	
    	returnedData = db.allIncompleteEvents();
    	
    	results = returnedData.get(0);
    	ids = returnedData.get(1);
    	
    	if (results.length != 0) {
    		
    		setTitle("Incomplete Events: ");
    		
    		// load results on ListView
        	ListView resultsList = (ListView) findViewById(R.id.incomplete_list);
        	resultsList.setAdapter(new ArrayAdapter<String>(this, R.layout.incomplete_list_item, results));
        	 
        	resultsList.setTextFilterEnabled(true);
     
        	resultsList.setOnItemClickListener(new OnItemClickListener() {
    			public void onItemClick(AdapterView<?> parent, View view,
    					int position, long id) {
    			    // When clicked, show a toast with the TextView text
    				int currentId = Integer.parseInt(ids[position]);
    			    
    				// call the event activity
    			    Intent i = new Intent(IncompleteActivity.this, EventActivity.class);
					i.putExtra("eventId", currentId);
					i.putExtra("eventComplete", 0);
					startActivity(i);
					
					IncompleteActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    			}
    		});
    	}
    	
    	// there are no results
    	else {
    		// TODO no incomplete tasks
    		setTitle("No Incomplete Events. ");
    	}
	}

}
