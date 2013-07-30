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
import android.widget.SeekBar;

import com.greighamilton.rememo.R;

/**
 * Class for a ColourDialogPreference object.
 * 
 * @author Greig Hamilton
 *
 */
public class MusicLengthDialogPreference extends DialogPreference {
	
	private SharedPreferences sp;
	
	private int currentMusicTime;

    public MusicLengthDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDialogLayoutResource(R.layout.dialog_set_music_length);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());   
        
        currentMusicTime = sp.getInt("MUSIC_TIME", 0);
    }
    
    @Override
    protected void onBindDialogView(View view) {
    	
    	SeekBar bar = (SeekBar) view.findViewById(R.id.music_length_seekbar);
    	bar.setProgress(currentMusicTime*10);
    	
     
        super.onBindDialogView(view);
    }

    /**
     * Method called when clicked to create a dialog.
     *
     */
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
        	
        	// buildthe seek bar for selecting the length of the earcon / musicon
        	SeekBar bar = (SeekBar)((AlertDialog) dialog).findViewById(R.id.music_length_seekbar);
        	bar.setMax(100);
        	
        	int time = (int) (bar.getProgress()/10);
        	
        	// add the time to shared preferences
        	sp.edit().putInt("MUSIC_TIME", time).commit();
        }
    }
}
