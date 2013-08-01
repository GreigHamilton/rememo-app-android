package com.greighamilton.rememo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
public class MusicRepetitionDialogPreference extends DialogPreference {
	
	private SharedPreferences sp;
	
	private MediaPlayer mp;
	
	private Context con;
	
	private int currentRep;

    public MusicRepetitionDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDialogLayoutResource(R.layout.dialog_set_music_repetition);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        currentRep = sp.getInt("MUSIC_REP", 0);
        
        con = context;
    }
    
    @Override
    protected void onBindDialogView(View view) {
    	
    	RadioButton repNone = (RadioButton) view.findViewById(R.id.sound_rep_none);
    	RadioButton repThirty = (RadioButton) view.findViewById(R.id.sound_rep_thirtyseconds);
    	RadioButton repOne = (RadioButton) view.findViewById(R.id.sound_rep_oneminute);
    	RadioButton repFive = (RadioButton) view.findViewById(R.id.sound_rep_fiveminutes);
    	RadioButton repTen = (RadioButton) view.findViewById(R.id.sound_rep_tenminutes);
     
    	RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.musicRepetitionLength);  
    	
    	if (currentRep == 0)
    		radioGroup.check(repNone.getId());  	
    	else if (currentRep == 1)
    		radioGroup.check(repThirty.getId());
    	else if (currentRep == 2)
    		radioGroup.check(repOne.getId());
    	else if (currentRep == 3)
    		radioGroup.check(repFive.getId());
    	else if (currentRep == 4)
    		radioGroup.check(repTen.getId());
    }
    
    
    /**
     * Method called when clicked to create a dialog.
     *
     */
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {    	
        	
        	// musicons
        	RadioButton repNone = (RadioButton)((AlertDialog) dialog).findViewById(R.id.sound_rep_none);
        	if (repNone.isChecked()) {
        		sp.edit().putInt("MUSIC_REP", 0).commit();
        	}
        	
        	RadioButton repThirty = (RadioButton)((AlertDialog) dialog).findViewById(R.id.sound_rep_thirtyseconds);
        	if (repThirty.isChecked()) {
        		sp.edit().putInt("MUSIC_REP", 1).commit();
        	}
        	
        	RadioButton repOne = (RadioButton)((AlertDialog) dialog).findViewById(R.id.sound_rep_oneminute);
        	if (repOne.isChecked()) {
        		sp.edit().putInt("MUSIC_REP", 2).commit();
        	}
        	
        	RadioButton repFive = (RadioButton)((AlertDialog) dialog).findViewById(R.id.sound_rep_fiveminutes);
        	if (repFive.isChecked()) {
        		sp.edit().putInt("MUSIC_REP", 3).commit();
        	}
        	
        	RadioButton repTen = (RadioButton)((AlertDialog) dialog).findViewById(R.id.sound_rep_tenminutes);
        	if (repTen.isChecked()) {
        		sp.edit().putInt("MUSIC_REP", 4).commit();
        	}
        }
    }
}
