package com.greighamilton.rememo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.greighamilton.rememo.R;

/**
 * Class for a ColourDialogPreference object.
 * 
 * @author Greig Hamilton
 *
 */
public class ColourDialogPreference extends DialogPreference {
	
	private SharedPreferences sp;

    public ColourDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDialogLayoutResource(R.layout.dialog_set_colour);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    }

    /**
     * Method called when clicked to create a dialog.
     *
     */
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
        	
        	// find which colour was selected and add to shared preferences
        	RadioButton red = (RadioButton)((AlertDialog) dialog).findViewById(R.id.colour_red);
        	if (red.isChecked()) sp.edit().putString("COLOUR", "#F21818").commit();
        	
        	RadioButton orange = (RadioButton)((AlertDialog) dialog).findViewById(R.id.colour_orange);
        	if (orange.isChecked()) sp.edit().putString("COLOUR", "#FFAB19").commit();
        	
        	RadioButton green = (RadioButton)((AlertDialog) dialog).findViewById(R.id.colour_green);
        	if (green.isChecked()) sp.edit().putString("COLOUR", "#3ECF4F").commit();
        	
        	RadioButton blue = (RadioButton)((AlertDialog) dialog).findViewById(R.id.colour_blue);
        	if (blue.isChecked()) sp.edit().putString("COLOUR", "#1885F2").commit();
        	
        	RadioButton yellow = (RadioButton)((AlertDialog) dialog).findViewById(R.id.colour_yellow);
        	if (yellow.isChecked()) sp.edit().putString("COLOUR", "#F5F50C").commit();

        	
        }
    }
}
