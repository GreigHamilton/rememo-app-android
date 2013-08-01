package com.greighamilton.rememo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.greighamilton.rememo.R;

/**
 * Class for About activity.
 * 
 * @author Greig Hamilton
 *
 */
public class AboutActivity extends Activity {
     
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
    }
     
    /**
     * Method called when the user clicks on the close button
     * 
     * @param v		View
     */
    public void onClickClose(View v) {
        finish();   
    }
     
}
