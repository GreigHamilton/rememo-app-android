package com.greighamilton.rememo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.greighamilton.rememo.data.DatabaseHelper;

public class SearchResultsActivity extends Activity {
	
	private DatabaseHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchresults);

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			
			db = DatabaseHelper.getInstance(this);
			
			doMySearch(query);
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

        	case android.R.id.home:
        		finish();
            return true;
                
        }
        return super.onOptionsItemSelected(item);
    }

    private void doMySearch(String query) {
    	
    	ArrayList<String[]> returnedData;
    	
    	String[] results;
    	final String[] ids;
    	
    	// query the database for names like <<query>>
    	
    	returnedData = db.searchQuery(query);
    	
    	results = returnedData.get(0);
    	ids = returnedData.get(1);
    	
    	if (results.length != 0) {
    		
    		setTitle("Search Results For: " + query);
    		
    		// load results on ListView
        	ListView resultsList = (ListView) findViewById(R.id.results_list);
        	resultsList.setAdapter(new ArrayAdapter<String>(this, R.layout.result_list_item, results));
        	 
        	resultsList.setTextFilterEnabled(true);
     
        	resultsList.setOnItemClickListener(new OnItemClickListener() {
    			public void onItemClick(AdapterView<?> parent, View view,
    					int position, long id) {
    			    // When clicked, show a toast with the TextView text
    				int currentId = Integer.parseInt(ids[position]);
    				
    				int complete;
    				if (db.isEventComplete(currentId))
    					complete = 1;
    				else
    					complete = 0;
    			    
    			    Intent i = new Intent(SearchResultsActivity.this, EventActivity.class);
					i.putExtra("eventId", currentId);
					i.putExtra("eventComplete", complete);
					startActivity(i);
					
					SearchResultsActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    			}
    		});
    	}
    	
    	// there are no results
    	else {
    		setTitle("No Results For: " + query);
    	}
	}
}