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
public class DiaryDialogPreference extends DialogPreference {
	
	private SharedPreferences sp;
	
	private int currentDiaryLayout;

    public DiaryDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDialogLayoutResource(R.layout.dialog_set_diary);
        
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        
        currentDiaryLayout = sp.getInt("DIARY", 0);
    }
    
    @Override
    protected void onBindDialogView(View view) {
    	
    	RadioButton monsun = (RadioButton) view.findViewById(R.id.diary_layout_mon_sun);
    	RadioButton plussix = (RadioButton) view.findViewById(R.id.diary_layout_plus_six);
    	RadioButton plusthree = (RadioButton) view.findViewById(R.id.diary_layout_plus_three);
     
    	RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.diarySelect);  
    	
    	if (currentDiaryLayout == 0)
    		radioGroup.check(monsun.getId());
    	
    	else if (currentDiaryLayout == 1)
    		radioGroup.check(plussix.getId());
    	
    	else if (currentDiaryLayout == 2)
    		radioGroup.check(plusthree.getId());
     
        super.onBindDialogView(view);
    }

    /**
     * Method called when clicked to create a dialog.
     *
     */
    public void onClick (DialogInterface dialog, int which) {
        if (DialogInterface.BUTTON_POSITIVE == which) {
        	
        	// find which colour was selected and add to shared preferences
        	RadioButton monsun = (RadioButton)((AlertDialog) dialog).findViewById(R.id.diary_layout_mon_sun);
        	if (monsun.isChecked()) sp.edit().putInt("DIARY", 0).commit();
        	
        	RadioButton plussix = (RadioButton)((AlertDialog) dialog).findViewById(R.id.diary_layout_plus_six);
        	if (plussix.isChecked()) sp.edit().putInt("DIARY", 1).commit();
        	
        	RadioButton plusthree = (RadioButton)((AlertDialog) dialog).findViewById(R.id.diary_layout_plus_three);
        	if (plusthree.isChecked()) sp.edit().putInt("DIARY", 2).commit();

        	
        }
    }
}
