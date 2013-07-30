package com.greighamilton.rememo.dialogs;

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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

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
	
	private int currentSound;

    public MusicDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDialogLayoutResource(R.layout.dialog_set_music);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        currentSound = sp.getInt("MUSIC", 0);
        
        con = context;
    }
    
    @Override
    protected void onBindDialogView(View view) {
    	
    	RadioButton musicZero = (RadioButton) view.findViewById(R.id.music_zero);
    	RadioButton musicOne = (RadioButton) view.findViewById(R.id.music_one);
    	RadioButton musicTwo = (RadioButton) view.findViewById(R.id.music_two);
    	RadioButton musicThree = (RadioButton) view.findViewById(R.id.music_three);
    	RadioButton musicFour = (RadioButton) view.findViewById(R.id.music_four);
    	RadioButton musicFive = (RadioButton) view.findViewById(R.id.music_five);
    	RadioButton alertOne = (RadioButton) view.findViewById(R.id.alert_one);
    	RadioButton alertTwo = (RadioButton) view.findViewById(R.id.alert_two);
    	RadioButton alertThree = (RadioButton) view.findViewById(R.id.alert_three);
    	RadioButton alertFour = (RadioButton) view.findViewById(R.id.alert_four);
    	RadioButton alertFive = (RadioButton) view.findViewById(R.id.alert_five);
     
    	RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.musicSelect);  
    	
    	if (currentSound == R.raw.canon)
    		radioGroup.check(musicOne.getId());  	
    	else if (currentSound == R.raw.friends)
    		radioGroup.check(musicTwo.getId());
    	else if (currentSound == R.raw.ghostbusters)
    		radioGroup.check(musicThree.getId());
    	else if (currentSound == R.raw.jamesbond)
    		radioGroup.check(musicFour.getId());
    	else if (currentSound == R.raw.jurassicpark)
    		radioGroup.check(musicFive.getId());
    	
    	else if (currentSound == R.raw.earcon1)
    		radioGroup.check(alertOne.getId());  	
    	else if (currentSound == R.raw.earcon2)
    		radioGroup.check(alertTwo.getId());
    	else if (currentSound == R.raw.earcon3)
    		radioGroup.check(alertThree.getId());
    	else if (currentSound == R.raw.earcon4)
    		radioGroup.check(alertFour.getId());
    	else if (currentSound == R.raw.earcon5)
    		radioGroup.check(alertFive.getId());
    	
    	else
    		radioGroup.check(musicZero.getId());
    	
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            	if (checkedId == R.id.music_zero) {
            		try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
            		
            	if (checkedId == R.id.music_one) {
            		
            		try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            		
            		// check if music is to be played
                	mp = MediaPlayer.create(getContext(), R.raw.canon);
                    	
                   	try {
                		mp.prepare();
                	} catch (IllegalStateException e) {
                		// TODO Auto-generated catch block
                		e.printStackTrace();
                	} catch (IOException e) {
                		// TODO Auto-generated catch block
                		e.printStackTrace();
                	}
                    	
                    // start the audio stream
                    mp.start();
            	}
            	
            	if (checkedId == R.id.music_two) {
            		
            		try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            		
            		// check if music is to be played
                	mp = MediaPlayer.create(getContext(), R.raw.friends);
                    	
                   	try {
                		mp.prepare();
                	} catch (IllegalStateException e) {
                		// TODO Auto-generated catch block
                		e.printStackTrace();
                	} catch (IOException e) {
                		// TODO Auto-generated catch block
                		e.printStackTrace();
                	}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.music_three) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.ghostbusters);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.music_four) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.jamesbond);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.music_five) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.jurassicpark);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.alert_one) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.earcon1);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.alert_two) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.earcon2);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.alert_three) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.earcon3);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.alert_four) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.earcon4);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}

				if (checkedId == R.id.alert_five) {

					try {
            			mp.stop();
            		} catch (Exception e) {
            			e.printStackTrace();
            		}

					// check if music is to be played
					mp = MediaPlayer.create(getContext(), R.raw.earcon5);

					try {
						mp.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// start the audio stream
					mp.start();
				}
            }
        });
     
        super.onBindDialogView(view);
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
        	RadioButton alertOne = (RadioButton)((AlertDialog) dialog).findViewById(R.id.alert_one);
        	if (alertOne.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon1).commit();
        	}
        	
        	RadioButton alertTwo = (RadioButton)((AlertDialog) dialog).findViewById(R.id.alert_two);
        	if (alertTwo.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon2).commit();
        	}
        	
        	RadioButton alertThree = (RadioButton)((AlertDialog) dialog).findViewById(R.id.alert_three);
        	if (alertThree.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon2).commit();
        	}
        	
        	RadioButton alertFour = (RadioButton)((AlertDialog) dialog).findViewById(R.id.alert_four);
        	if (alertFour.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon3).commit();
        	}
        	
        	RadioButton alertFive = (RadioButton)((AlertDialog) dialog).findViewById(R.id.alert_five);
        	if (alertFive.isChecked()) {
        		sp.edit().putInt("MUSIC", R.raw.earcon4).commit();	
        	}
        }
    }
}
