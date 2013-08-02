package com.greighamilton.rememo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Class for About activity.
 * 
 * @author Greig Hamilton
 *
 */
public class AboutActivity extends Activity {
	
	private static final String webGlasgow = "http://www.gla.ac.uk/schools/computing/?CFID=20180696&CFTOKEN=24912487";
	private static final String webMMH = "http://www.multimemohome.org/";
  
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
    
    public void clickGlasgow(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(webGlasgow));
        startActivity(Intent.createChooser(i, "Rememo"));
    }

    public void clickMMH(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(webMMH));
        startActivity(Intent.createChooser(i, "Rememo"));
    }

}
