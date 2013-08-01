package com.greighamilton.rememo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.greighamilton.rememo.R;

/**
 * Class for a ColourDialogPreference object.
 * 
 * @author Greig Hamilton
 *
 */
public class TextColourDialogPreference extends DialogPreference {
	
	private SharedPreferences sp;
	
	private String currentTextColour;

    public TextColourDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDialogLayoutResource(R.layout.dialog_set_text_colour);
        
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        
        currentTextColour = sp.getString("TEXTCOLOUR", "#FFFFFF");
    }
    
    @Override
    protected void onBindDialogView(View view) {
    	
    	RadioButton black = (RadioButton) view.findViewById(R.id.colour_black);
    	RadioButton white = (RadioButton) view.findViewById(R.id.colour_white);
     
    	RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.textColourSelect);  
    	
    	if (currentTextColour == "#000000")
    		radioGroup.check(black.getId());
    	
    	else if (currentTextColour == "#FFFFFF")
    		radioGroup.check(white.getId());
     
        super.onBindDialogView(view);
    }

    /**
     * Method called when clicked to create a dialog.
     *
     */
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
        	
        	// find which colour was selected and add to shared preferences
        	RadioButton black = (RadioButton)((AlertDialog) dialog).findViewById(R.id.colour_black);
        	if (black.isChecked()) sp.edit().putString("TEXTCOLOUR", "#000000").commit();
        	
        	RadioButton white = (RadioButton)((AlertDialog) dialog).findViewById(R.id.colour_white);
        	if (white.isChecked()) sp.edit().putString("TEXTCOLOUR", "#FFFFFF").commit();
        }
    }
}
