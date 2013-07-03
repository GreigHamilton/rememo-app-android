package com.greighamilton.rememo.data;

import java.io.IOException;

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

import com.greighamilton.rememo.R;

/**
 * Class for a ColourDialogPreference object.
 * 
 * @author Greig Hamilton
 *
 */
public class MusicDialogPreference extends DialogPreference {
	
	private SharedPreferences sp;
	
	private MediaPlayer mp;
	
	private Context con;

    public MusicDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDialogLayoutResource(R.layout.dialog_set_music);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        
        con = context;
    }

    /**
     * Method called when clicked to create a dialog.
     *
     */
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
        	
        	// musicons
        	RadioButton musicZero = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_zero);
        	if (musicZero.isChecked()) {
        		sp.edit().putInt("MUSIC", 0).commit();
        	}
        	
        	RadioButton musicOne = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_one);
        	if (musicOne.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.canon).commit();
        	}
        	
        	RadioButton musicTwo = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_two);
        	if (musicTwo.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.friends).commit();
        	}
        	
        	RadioButton musicThree = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_three);
        	if (musicThree.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.ghostbusters).commit();
        	}
        	
        	RadioButton musicFour = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_four);
        	if (musicFour.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.jamesbond).commit();
        	}
        	
        	RadioButton musicFive = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_five);
        	if (musicFive.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.jurassicpark).commit();	
        	}
        	
        	// earcons
        	RadioButton alertOne = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_one);
        	if (alertOne.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon1).commit();
        	}
        	
        	RadioButton alertTwo = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_two);
        	if (alertTwo.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon2).commit();
        	}
        	
        	RadioButton alertThree = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_three);
        	if (alertThree.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon2).commit();
        	}
        	
        	RadioButton alertFour = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_four);
        	if (alertFour.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon3).commit();
        	}
        	
        	RadioButton alertFive = (RadioButton)((AlertDialog) dialog).findViewById(R.id.music_five);
        	if (alertFive.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon4).commit();	
        	}
        }
    }
}
