package com.csce482.arcdecisiontool.Controllers.Input;

/**IMPORTS*/
import java.io.*;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Date;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.csce482.arcdecisiontool.Models.Hurricane;
import com.csce482.arcdecisiontool.Models.Task;
import com.csce482.arcdecisiontool.R;
import com.csce482.arcdecisiontool.Controllers.Timeline.TimelineActivity;
import com.csce482.arcdecisiontool.Utils.Utils;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * README:
 * Before starting to read through this class, it is
 * expected that the programmer has used and observed the application,
 * and read through the Android version of user's manual and programmer's
 * manual first.
 * this class is a very exhaustive piece of work.
 * There are a lots of pieces of codes which could be minimized,
 * but if the programmer reads through carefully, he/she will find
 * that the code is very symmetrical and once understood, its should be
 * easy to bring changes.
 * The purpose of this class is to create a hurricane object(Hurricane.java),
 * populate it, and push into the
 * realm object(the database system we used in this project), so that
 * it could be used in TimelineActivity class.
 * The code in this class works in two ways -
 * First_Time_Input mode, when user creates a hurricane event
 * for the first time, Or,
 * Editing_Input mode, when user comes back to input screen to edit an
 * existing hurricane event.
 * If it is,First_Time_Input mode, the code creates a hurricane object,
 * populates it, and pushes it to the realm object.
 * If it is Editing_Input mode, the code fetched the existing hurricane
 * object, copies over all the data into a new hurricane object, deletes the
 * old hurricane object and pushes the new one in realm object.
 * The function "inputTakenClick()" is divided into two if-else
 * blocks, each of which works for one of the modes mentioned above.
 * More explanation has been given below, please read through.
 */



public class InputActivity extends Activity{
	/**
	 * GLOBAL VARIABLES
	 *These are the global variables
	 * which stores all the input data upon
	 * user passes them.
	 * Reason for making these global:
	 * These variables can be called
	 * from anywhere of the code, and the input values
	 * can be retrieved.
	 * the global variable namings are self explanatory,
	 * as an example, a variable names "dateLandfall"
	 * means it contains the date of the Landfall.
	 * There are some variables that start with "sem_" ,
	 * those variables are used as semaphores.
	 * As an example, sem_landfall starts as 0, but as soon
	 * as user has passed landfall values, sem_landfall becomes
	 * 1, so that programmer can check the variable later to see if
	 * user had passed landfall values or not.
	 * The global variables uses short names for each of the input
	 * field. Below is the names of the input fields and their short
	 * names are mentioned.
	 * galeForceWindArrive => Gfw
	 * airportsCloseTime   => Act
	 * shelterOpeningTime  => Sheop
	 * hunkerDownTime      => Hnkrdn
	 * expectedReEntryTime => Reentry
	 * conferenceCallTimeForRegion1 => Cfcreg1
	 * conferenceCallTimeForRegion2 => Cfcreg2
	 * conferenceCallTimeForNHQ1    => Cfcnhq1
	 * conferenceCallTimeForNHQ2    => Cfcnhq2
	 */

	/**
	 * Notification variables
	 */
	int code = 100;
	int req_code = 0; //these fields incremented everytime after use


	/**
	 * Realm and Hurricane obj
	 */
	Realm realm;
    Hurricane hurricane = new Hurricane();

	/**
	 * Calendar, Date and Time
	 */
	Calendar cal;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    int sem_edit_inputs = 0; //this semaphore keeps track if we are in First_Time_Input or Editing_Input mode


	/**
	 * name variable
	 */
	String name;
    EditText edit_name;
    int sem_name=0; //a semaphore to check if the user has passed the input or not

	/**
	 * location variable
	 */
	String location;
    EditText edit_location;
    int sem_locaion =0;

	/**
	 * location variable
	 */
	String evacOrder;
	EditText edit_evacOrder;
	int sem_evacOrder =0;

	/**
	 * id variables
	 */
	int sem_id = 0;

	/**
	 * landfall/galeForceWindArrive variable
	 */
	int  yearLandfall;
    int monthLandfall;
    int domLandfall;
    int hourLandfall;
    int minLandfall;
	Date dateLandfall;
	Date dateGaleforcewind;
    Button btn_landfall_date_n_time;
    int sem_landfall = 0;
    int sem_Gfw = 0;
    String Gfw_col = "";	//indicates which column in timeline screen Gfw task goes into

	/**
	 * airportsCloseTime variable
	 */
	int  yearAct;
    int monthAct;
    int domAct;
    int hourAct;
    int minAct;
    Date dateAirportclose;
    Button btn_act_date_n_time;
    int sem_Act = 0;
	String Act_col = ""; 	//indicates which column in timeline Act task goes into

	/**
	 * shelterOpeningTime Variable
	 */
	int  yearSheop;
    int monthSheop;
    int domSheop;
    int hourSheop;
    int minSheop;
    Date dateShelteropen;
    Button btn_sheop_date_n_time;
    int sem_Sheop = 0;
	String Sheop_col = ""; //indicates which column in timeline Sheop task goes into

	/**
	 * hunkerDownTime Variable
	 */
	int  yearHnkrdn;
    int monthHnkrdn;
    int domHnkrdn;
    int hourHnkrdn;
    int minHnkrdn;
    Date dateHunkerdown;
    Button btn_hnkrdn_date_n_time;
    int sem_Hnkrdn = 0;
	String Hnkrdn_col = ""; //indicates which column in timeline Hnrkdw task goes into

	/**
	 * expectedReEntryTime Variable
	 */
	int  yearReentry;
    int monthReentry;
    int domReentry;
    int hourReentry;
    int minReentry;
    Date dateReentry;
    Button btn_reentry_date_n_time;
    int sem_Reentry = 0;
	String Reentry_col = ""; //indicates which column in timeline Reentry task goes into

	/**
	 * conferenceCallTimeForRegion1 Variable
	 */
	int yearCfcreg1 = 0;
    int hourCfcreg1;
    int minCfcreg1;
    Date dateCfcreg1;
    Button btn_cfcreg1_date_n_time;
	int sem_Cfcreg1 = 0;

	/**
	 * conferenceCallTimeForNHQ1 Variable
	 */
	int yearCfcnhq1 = 0;
	int hourCfcnhq1;
    int minCfcnhq1;
    Date dateCfcnhq1;
    Button btn_cfcnhq1_date_n_time;
	int sem_Cfcnhq1 = 0;

	/**
	 * conferenceCallTimeForRegion2 Variable
	 */
	int yearCfcreg2 = 0;
	int hourCfcreg2;
	int minCfcreg2;
	Date dateCfcreg2;
	Button btn_cfcreg2_date_n_time;
	int sem_Cfcreg2 = 0;

	/**
	 * conferenceCallTimeForNHQ2 Variable
	 */
	int yearCfcnhq2= 0;
	int hourCfcnhq2;
	int minCfcnhq2;
	Date dateCfcnhq2;
	Button btn_cfcnhq2_date_n_time;
	int sem_Cfcnhq2 = 0;
	//Notice: none of the conference call variable blocks have field named "_col" because conference
	//calls appears in all the timeline columns(the ones that are not expired, aka not red).

//----------------------------------------------End of global variables ----------------------------------------

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //bring the default instance of realm
		Realm.init(this);
		realm = Realm.getDefaultInstance();

		//calling this function sets the view for the input screen
        setUpViews();


		Intent intent = getIntent();

		//this block will only execute when user did Reset during timeline so gotta delete all the scheduled alarms
		if(intent.hasExtra("hello")) {
			String key =  intent.getStringExtra("hello");
			if(key.equals("from the other side")) {
				delete_alarms_when_reset();
			}
		}

		//check if a hurricane obj already exists (Editing_Input mode)
        if (Utils.hurricaneEventExists()) {

			//turning the semaphore on so that we can check it later
			sem_edit_inputs = 1;

			//deleting all the previously set alarms(aka notifications)
			delete_alarms_when_reset();

			//retrieving a copy of the old hurricane obj from realm
            String id = intent.getStringExtra("hurricane_id");
            hurricane = Utils.getHurricane();

			//copy over all the dates from the existing hurricane and initialize to global variables
			dateLandfall = hurricane.getLandfall();
			dateGaleforcewind = hurricane.getLandfall();
			dateAirportclose = hurricane.getAirportsCloseTime();
			dateShelteropen = hurricane.getLeadTimeToOpenShelters();
			dateHunkerdown = hurricane.getHunkerDownTime();
			dateReentry = hurricane.getExpectedReEntryTime();
			dateCfcreg1 = hurricane.getConferenceCallTimesForRegion().first();
			dateCfcnhq1 = hurricane.getConferenceCallTimesForNHQ().first();
			dateCfcreg2 = hurricane.getConferenceCallTimesForRegion().last();
			dateCfcnhq2 = hurricane.getConferenceCallTimesForNHQ().last();

			//set text on all buttons.
			//before setting the button text we need to check if the field contains value or not.
			//meaning, if a particular field in hurricane object contains value, that means
			//user had passed value for that field previously, but if the value for a field is
			//empty, aka year value is negative, then user has not yet passed any value for
			//that field yet.
            edit_name.setText(hurricane.getName());
            edit_location.setText(hurricane.getLocation());
            edit_evacOrder.setText(hurricane.getEvac());
            btn_landfall_date_n_time.setText("Landfall: "+(Utils.formattedDate(hurricane.getLandfall())));
			if(hurricane.getAirportsCloseTime().getYear()>0) {
				btn_act_date_n_time.setText("AIRPORT CLOSING: " + (Utils.formattedDate(hurricane.getAirportsCloseTime())));
			}else {btn_act_date_n_time.setText("Airport Closing Date and Time");}
			if(hurricane.getLeadTimeToOpenShelters().getYear()>0) {
				btn_sheop_date_n_time.setText("SHELTER OPENING: " + (Utils.formattedDate(hurricane.getLeadTimeToOpenShelters())));
			}else {btn_sheop_date_n_time.setText("Shelter Opening Date and Time");}
			if(hurricane.getHunkerDownTime().getYear()>0) {
				btn_hnkrdn_date_n_time.setText("HUNKER DOWN: " + (Utils.formattedDate(hurricane.getHunkerDownTime())));
			}else {btn_hnkrdn_date_n_time.setText("Hunker Down Date and Time");}
			if(hurricane.getExpectedReEntryTime().getYear()>0) {
				btn_reentry_date_n_time.setText("EXPECTED REENTRY: " + (Utils.formattedDate(hurricane.getExpectedReEntryTime())));
			}else {btn_reentry_date_n_time.setText("Expected Reentry Date and Time");}
			if(hurricane.getConferenceCallTimesForRegion().first().getYear()>0) {
				btn_cfcreg1_date_n_time.setText("REG CALL1: \n" + (Utils.getTimeFormatted(hurricane.getConferenceCallTimesForRegion().first())));
			}else {btn_cfcreg1_date_n_time.setText("Regional call1");}
			if(hurricane.getConferenceCallTimesForRegion().last().getYear()>0) {
				btn_cfcreg2_date_n_time.setText("REG CALL2: \n" + (Utils.getTimeFormatted(hurricane.getConferenceCallTimesForRegion().last())));
			}else {btn_cfcreg2_date_n_time.setText("Regional call2");}
			if(hurricane.getConferenceCallTimesForNHQ().first().getYear()>0) {
				btn_cfcnhq1_date_n_time.setText("NHQ CALL1: \n" + (Utils.getTimeFormatted(hurricane.getConferenceCallTimesForNHQ().first())));
			}else {btn_cfcnhq1_date_n_time.setText("NHQ call1");}
			if(hurricane.getConferenceCallTimesForNHQ().last().getYear()>0) {
				btn_cfcnhq2_date_n_time.setText("NHQ CALL2: \n" + (Utils.getTimeFormatted(hurricane.getConferenceCallTimesForNHQ().last())));
			}else {btn_cfcnhq2_date_n_time.setText("NHQ call2");}

        }

    }//onCreate



	/**
	 * This function sets up the view for inputs. this function must be called in onCreate.
	 * Inside this function, each of the blocks are separate for taking inputs for their
	 * respective fields and setting it to their respective global variables.
	 * This function is the link between this class and activity_input.xml file (under "res" directory)
	 *
	 *
	 */
    private void setUpViews(){

        //name input
        edit_name = (EditText)findViewById(R.id.textview_name);

        //location input
        edit_location = (EditText)findViewById(R.id.textview_location);

        //evacOrder input
		edit_evacOrder = (EditText) findViewById(R.id.textview_evac);

        //landfall inputs
        btn_landfall_date_n_time = (Button) findViewById(R.id.button_landfall_date_n_time);
        btn_landfall_date_n_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                domLandfall = cal.get(Calendar.DAY_OF_MONTH);
                yearLandfall = cal.get(Calendar.YEAR);
                monthLandfall = cal.get(Calendar.MONTH);

                tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        hourLandfall = h;
                        minLandfall = m;

                        //settext
						Calendar calx = Calendar.getInstance();
						calx.set(yearLandfall, monthLandfall, domLandfall, hourLandfall, minLandfall, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.formattedDate(datex);
						calx.clear();
                        btn_landfall_date_n_time.setText("Landfall: "+ date_str_x);
                        //input value for landfall has been taken and pushed into the global variable
                        //now, set the semaphore into 1.
                        sem_landfall = 1;
                        sem_Gfw = 1;
                    }
                }, hourLandfall, minLandfall, false);

                dpd = new DatePickerDialog(InputActivity.this, new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        domLandfall = d;
                        yearLandfall = y;
                        monthLandfall = m;
                        tpd.show();
                    }
                }, yearLandfall, monthLandfall, domLandfall);
				//btn_landfall_date_n_time.setEnabled(true);
                dpd.show();
            }
        });



        //airportsCloseTime inputs
        btn_act_date_n_time = (Button) findViewById(R.id.button_act_date_n_time);
        btn_act_date_n_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                domAct = cal.get(Calendar.DAY_OF_MONTH);
                yearAct = cal.get(Calendar.YEAR);
                monthAct = cal.get(Calendar.MONTH);

                tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        hourAct = h;
                        minAct = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(yearAct, monthAct, domAct, hourAct, minAct, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.formattedDate(datex);
						calx.clear();
						btn_act_date_n_time.setText("AIRPORT CLOSING: "+ date_str_x);

                        sem_Act =1;
                    }
                }, hourAct, minAct, false);

                dpd = new DatePickerDialog(InputActivity.this, new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        domAct = d;
                        yearAct = y;
                        monthAct = m;
                        tpd.show();
                    }
                }, yearAct, monthAct, domAct);
                dpd.show();
            }
        });



        //ShelterOpeningTime inputs
        btn_sheop_date_n_time = (Button) findViewById(R.id.button_sheop_date_n_time);
        btn_sheop_date_n_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                domSheop = cal.get(Calendar.DAY_OF_MONTH);
                yearSheop = cal.get(Calendar.YEAR);
                monthSheop = cal.get(Calendar.MONTH);

                tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        hourSheop = h;
                        minSheop = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(yearSheop, monthSheop, domSheop, hourSheop, minSheop, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.formattedDate(datex);
						calx.clear();
						btn_sheop_date_n_time.setText("SHELTER OPENING: "+ date_str_x);

                        sem_Sheop =1;
                    }
                }, hourSheop, minSheop, false);

                dpd = new DatePickerDialog(InputActivity.this, new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        domSheop = d;
                        yearSheop = y;
                        monthSheop = m;
                        tpd.show();
                    }
                }, yearSheop, monthSheop, domSheop);
                dpd.show();
            }
        });



        //hunkerDownTime inputs
        btn_hnkrdn_date_n_time = (Button) findViewById(R.id.button_hnkrdn_date_n_time);
        btn_hnkrdn_date_n_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                domHnkrdn = cal.get(Calendar.DAY_OF_MONTH);
                yearHnkrdn = cal.get(Calendar.YEAR);
                monthHnkrdn = cal.get(Calendar.MONTH);

                tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        hourHnkrdn = h;
                        minHnkrdn = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(yearHnkrdn, monthHnkrdn, domHnkrdn, hourHnkrdn, minHnkrdn, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.formattedDate(datex);
						calx.clear();
						btn_hnkrdn_date_n_time.setText("HUNKER DOWN: "+ date_str_x);

                        sem_Hnkrdn = 1;
                    }
                }, hourHnkrdn, minHnkrdn, false);

                dpd = new DatePickerDialog(InputActivity.this, new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        domHnkrdn = d;
                        yearHnkrdn = y;
                        monthHnkrdn = m;
                        tpd.show();
                    }
                }, yearHnkrdn, monthHnkrdn, domHnkrdn);
                dpd.show();
            }
        });



        //expectedRentryTime inputs
        btn_reentry_date_n_time = (Button) findViewById(R.id.button_reentry_date_n_time);
        btn_reentry_date_n_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                domReentry = cal.get(Calendar.DAY_OF_MONTH);
                yearReentry = cal.get(Calendar.YEAR);
                monthReentry = cal.get(Calendar.MONTH);

                tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        hourReentry = h;
                        minReentry = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(yearReentry, monthReentry, domReentry, hourReentry, minReentry, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.formattedDate(datex);
						calx.clear();
						btn_reentry_date_n_time.setText("EXPECTED REENTRY: "+ date_str_x);

                        sem_Reentry = 1;
                    }
                }, hourReentry, minReentry, false);

                dpd = new DatePickerDialog(InputActivity.this, new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int y, int m, int d) {
                        domReentry = d;
                        yearReentry = y;
                        monthReentry = m;
                        tpd.show();
                    }
                }, yearReentry, monthReentry, domReentry);
                dpd.show();
            }
        });



        //conferenceCallTimeForRegion1 inputs
        btn_cfcreg1_date_n_time = (Button) findViewById(R.id.button_cfcreg1_date_n_time);
        btn_cfcreg1_date_n_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                hourCfcreg1 = cal.get(Calendar.HOUR);
                minCfcreg1 = cal.get(Calendar.MINUTE);
                tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        hourCfcreg1 = h;
                        minCfcreg1 = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(2018, 0, 0, hourCfcreg1, minCfcreg1, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.getTimeFormatted(datex);
						calx.clear();
						btn_cfcreg1_date_n_time.setText("REG CALL1: \n"+ date_str_x);

                        sem_Cfcreg1 = 1;
                        yearCfcreg1 = 2018;
                    }
                }, hourCfcreg1, minCfcreg1, false);
                tpd.show();

            }
        });



        //conferenceCallTimeForNHQ1 inputs
        btn_cfcnhq1_date_n_time = (Button) findViewById(R.id.button_cfcnhq1_date_n_time);
        btn_cfcnhq1_date_n_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cal = Calendar.getInstance();
                hourCfcnhq1 = cal.get(Calendar.HOUR);
                minCfcnhq1 = cal.get(Calendar.MINUTE);
                tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int h, int m) {
                        hourCfcnhq1 = h;
                        minCfcnhq1 = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(2018, 0, 0, hourCfcnhq1, minCfcnhq1, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.getTimeFormatted(datex);
						calx.clear();
						btn_cfcnhq1_date_n_time.setText("NHQ CALL1: \n"+ date_str_x);

                        sem_Cfcnhq1 = 1;
                        yearCfcnhq1 = 2018;
                    }
                }, hourCfcnhq1, minCfcnhq1, false);
                tpd.show();

            }
        });



		//conferenceCallTimeForRegion2 inputs
		btn_cfcreg2_date_n_time = (Button) findViewById(R.id.button_cfcreg2_date_n_time);
		btn_cfcreg2_date_n_time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cal = Calendar.getInstance();
				hourCfcreg2 = cal.get(Calendar.HOUR);
				minCfcreg2 = cal.get(Calendar.MINUTE);
				tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker arg0, int h, int m) {
						hourCfcreg2 = h;
						minCfcreg2 = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(2018, 0, 0, hourCfcreg2, minCfcreg2, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.getTimeFormatted(datex);
						calx.clear();
						btn_cfcreg2_date_n_time.setText("REG CALL2: \n"+ date_str_x);

						sem_Cfcreg2 = 1;
						yearCfcreg2 = 2018;
					}
				}, hourCfcreg2, minCfcreg2, false);
				tpd.show();

			}
		});



		//conferenceCallTimeForNHQ2 inputs
		btn_cfcnhq2_date_n_time = (Button) findViewById(R.id.button_cfcnhq2_date_n_time);
		btn_cfcnhq2_date_n_time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cal = Calendar.getInstance();
				hourCfcnhq2 = cal.get(Calendar.HOUR);
				minCfcnhq2 = cal.get(Calendar.MINUTE);
				tpd = new TimePickerDialog(InputActivity.this, new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker arg0, int h, int m) {
						hourCfcnhq2 = h;
						minCfcnhq2 = m;

						//settext
						Calendar calx = Calendar.getInstance();
						calx.set(2018, 0, 0, hourCfcnhq2, minCfcnhq2, 0);
						Date datex = calx.getTime();
						String date_str_x = Utils.getTimeFormatted(datex);
						calx.clear();
						btn_cfcnhq2_date_n_time.setText("NHQ CALL2: \n"+ date_str_x);

						sem_Cfcnhq2 = 1;
						yearCfcnhq2 = 2018;
					}
				}, hourCfcnhq2, minCfcnhq2, false);
				tpd.show();

			}
		});
    } //end of setUpViews()





	/**
	 * This function gets triggered from activity_input.xml file, when
	 * user has filled all/some of the input fields and clicked
	 * "FINISH INPUT" button.
	 * The functions main purpose is to populate a hurricane object with
	 * user inputs and push the hurricane object into realm object.
	 * This function is mainly divided into two if-else block.
	 * The if block works for First_Time_Input mode, and
	 * the else block worsk for Editing_Input mode.
	 * @param view a View object
	 * */
    public void inputTakenClick(View view) throws FileNotFoundException {

		if(sem_edit_inputs==0) { //First_Time_Input mode

			//create a new hurricane object which will be populated and pushed into realm object
			Hurricane hurricane1 = new Hurricane();

			name = edit_name.getText().toString();
			if (name != "") {sem_name = 1;}

			location = edit_location.getText().toString();
			if (location != "") {sem_locaion = 1;}

			evacOrder = edit_evacOrder.getText().toString();
			if (evacOrder!="") {sem_evacOrder = 1;}

			//pushing id in hurricane obj
			UUID idOne = UUID.randomUUID();
			hurricane1.setId(idOne.toString());
			sem_id = 1;

			//pushing name in hurricane obj
			hurricane1.setName(name);

			//pushing location in hurricane obj
			hurricane1.setLocation(location);
			
			//pushing evacOrder in hurricane obj
			hurricane1.setEvac(evacOrder);

			//pushing evacOrder in hurricane obj
			hurricane1.setEvac(evacOrder);

			//setting dateLandfall and pushing in hurricane obj
			Calendar cal = Calendar.getInstance();
			cal.set(yearLandfall, monthLandfall, domLandfall, hourLandfall, minLandfall, 0);
			dateLandfall = cal.getTime();
			hurricane1.setLandfall(dateLandfall);
			cal.clear();

			//setting dateGaleforcewind and pushing in hurricane obj
			cal.set(yearLandfall, monthLandfall, domLandfall, hourLandfall, minLandfall, 0);
			dateGaleforcewind = cal.getTime();
			hurricane1.setGaleForceWindArrive(dateGaleforcewind);
			cal.clear();

			//setting dateAirportclose and pushing in hurricane obj
			cal.set(yearAct, monthAct, domAct, hourAct, minAct, 0);
			dateAirportclose = cal.getTime();
			hurricane1.setAirportsCloseTime(dateAirportclose);
			cal.clear();

			//setting dateShelteropen and pushing in hurricane obj
			cal.set(yearSheop, monthSheop, domSheop, hourSheop, minSheop, 0);
			dateShelteropen = cal.getTime();
			hurricane1.setLeadTimeToOpenShelters(dateShelteropen);
			cal.clear();

			//setting dateHunkerdown and pushing in hurricane obj
			cal.set(yearHnkrdn, monthHnkrdn, domHnkrdn, hourHnkrdn, minHnkrdn, 0);
			dateHunkerdown = cal.getTime();
			hurricane1.setHunkerDownTime(dateHunkerdown);
			cal.clear();

			//setting dateReentry and pushing in hurricane obj
			cal.set(yearReentry, monthReentry, domReentry, hourReentry, minReentry, 0);
			dateReentry = cal.getTime();
			hurricane1.setExpectedReEntryTime(dateReentry);
			cal.clear();

			//setting dateCfcreg1 and pushing in hurricane obj
			cal.set(yearCfcreg1, 0, 0, hourCfcreg1, minCfcreg1, 0);
			dateCfcreg1 = cal.getTime();
			hurricane1.setConferenceCallTimesForRegion(dateCfcreg1);
			cal.clear();

			//setting dateCfcnhq1 and pushing in hurricane obj
			cal.set(yearCfcnhq1, 0, 0, hourCfcnhq1, minCfcnhq1, 0);
			dateCfcnhq1 = cal.getTime();
			hurricane1.setConferenceCallTimesForNHQ(dateCfcnhq1);
			cal.clear();

			//setting dateCfcreg2 and pushing in hurricane obj
			cal.set(yearCfcreg2, 0, 0, hourCfcreg2, minCfcreg2, 0);
			dateCfcreg2 = cal.getTime();
			hurricane1.setConferenceCallTimesForRegion(dateCfcreg2);
			cal.clear();

			//setting dateCfcnhq2 and pushing in hurricane obj
			cal.set(yearCfcnhq2, 0, 0, hourCfcnhq2, minCfcnhq2, 0);
			dateCfcnhq2 = cal.getTime();
			hurricane1.setConferenceCallTimesForNHQ(dateCfcnhq2);
			cal.clear();

			//pushing hurricane1 obj in realm
			realm.beginTransaction();
			realm.copyToRealmOrUpdate(hurricane1);
			realm.commitTransaction();

			//set alarms
			init_alarm();
			//calculate the column number for each of the input values so that
			//they can be used in timeline screen
			special_tasks_timeline_column_calculation_and_set();
			//populating an array in util class with the input dates.
			populate_task_date_array();

			//at this point, we have taken inputs,
			// populated hurricane objects and pushed it in realm,
			// did some bookkeeping works(set alarms, column calc etc).
			//Now we are ready to jump into timeline screen.
			//before jumping, check two things->
			//check, if landfall value has been passed. if not, dont proceed.
			// we do it by checking the semaphore sem_landfall == 1
			//check, if any other date values are after landfall, if so, dont proceed.
			//we do it by comparing their date values to the landfall date value.
			if(sem_landfall==1) {

				int ready_to_jump = 1;
				if(sem_Act==1 && (dateAirportclose.after(dateLandfall))){
					Toast.makeText(InputActivity.this, "Airport closing time must be set before landfall", Toast.LENGTH_LONG).show();
					ready_to_jump = 0;
				}else if(sem_Sheop==1 && (dateShelteropen.after(dateLandfall))){
					Toast.makeText(InputActivity.this, "Shelter opening time must be set before landfall", Toast.LENGTH_LONG).show();
					ready_to_jump = 0;
				}else if(sem_Hnkrdn==1 && (dateHunkerdown.after(dateLandfall))){
					Toast.makeText(InputActivity.this, "Hunker down time must be set before landfall", Toast.LENGTH_LONG).show();
					ready_to_jump = 0;
				}
				if(ready_to_jump == 1){
					Toast.makeText(InputActivity.this, "Initiating timeline...", Toast.LENGTH_LONG).show();
					Intent intentToTimeline = new Intent(this, TimelineActivity.class);
					intentToTimeline.putExtra("hurricane_obj_id", hurricane1.getId());
					startActivity(intentToTimeline);
				}

			}else{
				Toast.makeText(InputActivity.this, "Must pass Landfall Values.", Toast.LENGTH_LONG).show();
			}



		}else{ 		//Editing_Input mode

			//a brand new hurricane object that will be pushed in realm
			Hurricane hurricane2 = new Hurricane();


			//first populating pre-existing data from hurricane to hurricane2 object
			hurricane2.setName(hurricane.getName());
			hurricane2.setId(hurricane.getId());
			hurricane2.setLocation(hurricane.getLocation());
			hurricane2.setEvac(hurricane.getEvac());
			hurricane2.setLandfall(hurricane.getLandfall());
			hurricane2.setGaleForceWindArrive(hurricane.getGaleForceWindArrive());
			hurricane2.setAirportsCloseTime(hurricane.getAirportsCloseTime());
			hurricane2.setLeadTimeToOpenShelters(hurricane.getLeadTimeToOpenShelters());
			hurricane2.setHunkerDownTime(hurricane.getHunkerDownTime());
			hurricane2.setExpectedReEntryTime(hurricane.getExpectedReEntryTime());
			//populating for reg calls and nhq calls are done below

			//pushing edited name in new hurricane obj
			name = edit_name.getText().toString();
			if (name != "") {sem_name = 1;}
			if(sem_name==1){hurricane2.setName(name);}

			//pushing edited location in new hurricane obj
			location = edit_location.getText().toString();
			if (location != "") {sem_locaion = 1;}
			if(sem_locaion==1){hurricane2.setLocation(location);}

			//pushing edited evacOrder in new hurricane obj
			evacOrder = edit_evacOrder.getText().toString();
			if(evacOrder!=""){sem_evacOrder = 1;}
			if(sem_evacOrder == 1){hurricane2.setEvac(evacOrder);}

			//pushing edited landfall time in new hurricane obj
			//if landfall time has been edited, sem_landfall will be 1
			//and by checking it, we can know if user edited this field or not.
			if(sem_landfall==1){
				Calendar cal = Calendar.getInstance();
				cal.set(yearLandfall, monthLandfall, domLandfall, hourLandfall, minLandfall, 0);
				Date dateLandfall_ = cal.getTime();
				dateLandfall = dateLandfall_;
				hurricane2.setLandfall(dateLandfall_);
				cal.clear();
			}

			//pushing edited galeforcewind arrival time in new hurricane obj
			if(sem_Gfw==1){
				cal.set(yearLandfall, monthLandfall, domLandfall, hourLandfall, minLandfall, 0);
				Date dateGaleforcewind_ = cal.getTime();
				dateGaleforcewind = dateGaleforcewind_;
				hurricane2.setGaleForceWindArrive(dateGaleforcewind_);
				cal.clear();
			}

			//pushing edited airport close time in new hurricane obj
			if(sem_Act==1){
				cal.set(yearAct, monthAct, domAct, hourAct, minAct, 0);
				Date dateAirportclose_ = cal.getTime();
				dateAirportclose = dateAirportclose_;
				hurricane2.setAirportsCloseTime(dateAirportclose_);
				cal.clear();
			}

			//pushing edited shelter Opening time in new hurricane obj
			if(sem_Sheop==1){
				cal.set(yearSheop, monthSheop, domSheop, hourSheop, minSheop, 0);
				Date dateShelteropen_ = cal.getTime();
				dateShelteropen = dateShelteropen_;
				hurricane2.setLeadTimeToOpenShelters(dateShelteropen_);
				cal.clear();
			}

			//pushing edited hunkerDown time in new hurricane obj
			if(sem_Hnkrdn==1){
				cal.set(yearHnkrdn, monthHnkrdn, domHnkrdn, hourHnkrdn, minHnkrdn, 0);
				Date dateHunkerdown_ = cal.getTime();
				dateHunkerdown = dateHunkerdown_;
				hurricane2.setHunkerDownTime(dateHunkerdown_);
				cal.clear();
			}

			//pushing edited Reentry time in new hurricane obj
			if(sem_Reentry==1){
				cal.set(yearReentry, monthReentry, domReentry, hourReentry, minReentry, 0);
				Date dateReentry_ = cal.getTime();
				dateReentry = dateReentry_;
				hurricane2.setExpectedReEntryTime(dateReentry_);
				cal.clear();
			}

			//pushing edited regional conference call time1 in new hurricane obj
			if(sem_Cfcreg1==1){
				Calendar cal1 = Calendar.getInstance();;
				cal1.set(yearCfcreg1, 0, 0, hourCfcreg1, minCfcreg1, 0);
				Date dateCfcreg1_ = cal1.getTime();
				dateCfcreg1 = dateCfcreg1_;
				hurricane2.setConferenceCallTimesForRegion(dateCfcreg1_);
				cal1.clear();
			}else{ hurricane2.setConferenceCallTimesForRegion(hurricane.getConferenceCallTimesForRegion().first());}

			//pushing edited nhq conference call time1 in new hurricane obj
			if(sem_Cfcnhq1==1){
				Calendar cal2 = Calendar.getInstance();
				cal2.set(yearCfcnhq1, 0, 0, hourCfcnhq1, minCfcnhq1, 0);
				Date dateCfcnhq1_ = cal2.getTime();
				dateCfcnhq1 = dateCfcnhq1_;
				hurricane2.setConferenceCallTimesForNHQ(dateCfcnhq1_);
				cal2.clear();
			}else{hurricane2.setConferenceCallTimesForNHQ(hurricane.getConferenceCallTimesForNHQ().first());}

			//pushing edited regional conference call time2 in new hurricane obj
			if(sem_Cfcreg2==1){
				Calendar cal3 = Calendar.getInstance();
				cal3.set(yearCfcreg2, 0, 0, hourCfcreg2, minCfcreg2, 0);
				Date dateCfcreg2_ = cal3.getTime();
				dateCfcreg2 = dateCfcreg2_;
				hurricane2.setConferenceCallTimesForRegion(dateCfcreg2_);
				cal3.clear();
			}else{hurricane2.setConferenceCallTimesForRegion(hurricane.getConferenceCallTimesForRegion().last());}

			//pushing edited nhq conference call time2 in new hurricane obj
			if(sem_Cfcnhq2==1){
				Calendar cal4 = Calendar.getInstance();
				cal4.set(yearCfcnhq2, 0, 0, hourCfcnhq2, minCfcnhq2, 0);
				Date dateCfcnhq2_ = cal4.getTime();
				dateCfcnhq2 = dateCfcnhq2_;
				hurricane2.setConferenceCallTimesForNHQ(dateCfcnhq2_);
				cal4.clear();
			}else{hurricane2.setConferenceCallTimesForNHQ(hurricane.getConferenceCallTimesForNHQ().last());}


			//set alarms
			init_alarm();
			//calculate the column number for each of the input values so that
			//they can be used in timeline screen
			special_tasks_timeline_column_calculation_and_set();
			//populating an array in util class with the input dates.
			populate_task_date_array();


			//similarly, at this point,
			//we have copied over all the previous input
			//values from hurricane to hurricane2 object,
			//changed only those fields which user edited,
			//and did some bookkeeping works, now its time to
			//jump into timeline screen for second time.
			if(dateAirportclose.getYear()<0){sem_Act = 0;}
			if(dateShelteropen.getYear()<0) {sem_Sheop = 0;}
			if(dateHunkerdown.getYear()<0){sem_Hnkrdn = 0;}

			int ready_to_jump = 1;
			if((sem_landfall==1 || sem_Act==1) && (dateAirportclose.after(dateLandfall))){
				Toast.makeText(InputActivity.this, "Airport closing time must be set before landfall", Toast.LENGTH_LONG).show();
				ready_to_jump = 0;
			}else if((sem_landfall==1 || sem_Sheop==1) && (dateShelteropen.after(dateLandfall))){
				Toast.makeText(InputActivity.this, "Shelter opening time must be set before landfall", Toast.LENGTH_LONG).show();
				ready_to_jump = 0;
			}else if((sem_landfall==1 || sem_Hnkrdn==1) && (dateHunkerdown.after(dateLandfall))){
				Toast.makeText(InputActivity.this, "Hunker down time must be set before landfall", Toast.LENGTH_LONG).show();
				ready_to_jump = 0;
			}
			if(ready_to_jump == 1){

				//deleting old hurricane obj and pushing new hurricane2 obj in realm
				RealmResults<Hurricane> results = realm.where(Hurricane.class).findAll();
				realm.beginTransaction();
				results.deleteAllFromRealm();
				realm.copyToRealmOrUpdate(hurricane2);
				realm.commitTransaction();

				Toast.makeText(InputActivity.this, "Initiating timeline...", Toast.LENGTH_LONG).show();
				Intent intentToTimeline = new Intent(this, TimelineActivity.class); //test
				intentToTimeline.putExtra("hurricane_obj_id", hurricane2.getId());
				startActivity(intentToTimeline);
			}
		}
    }


    /**
    * This function calls set_alarm() function for setting alarms.
    * The purpose of this function is to send out notifications to the devices
    * when timeline/tasks is expiring.
    * in android, alarms are events that can be set for a future time to
    * trigger. In our case, we set alarms for future, which sends out notifications.
    * We set alarms(aka send notification) for two reasons, when landfall expiring,
    * and when a timeline column is expiring.
    * Note that, alarms are deleted at the top of this class everytime, this class is visited (and then set again).
    * */
	public void init_alarm(){

		//current time since 1970(see official java currenttimeinmillis() doc for more details)
		long till_now = System.currentTimeMillis();

		//first, set alarms for one hour before landfall
		long till_landfall = dateLandfall.getTime();
		long time_from_now_till_landfall_alarm = (till_landfall - till_now - 3600000);
		if(time_from_now_till_landfall_alarm>0){set_alarm(time_from_now_till_landfall_alarm, 0);}

		//second, set alarms for the other timeline columns:
		//calculate how many hours left before landfall
		//then convert them into number of days (aka num of non expired timeline columns that are not red)
		//then set (num_days-1) many alarms for each timeline columns
		long time_from_now_till_landfall = time_from_now_till_landfall_alarm + 3600000;
		long hours_till_landfall = TimeUnit.MILLISECONDS.toHours(time_from_now_till_landfall);
		long mins_till_landfall = TimeUnit.MILLISECONDS.toMinutes(time_from_now_till_landfall);
		long days_till_landfall= -1;

		if(hours_till_landfall>=0 && hours_till_landfall<=24){days_till_landfall=1;}
		if(hours_till_landfall>=25&& hours_till_landfall<=48){days_till_landfall=2;}
		if(hours_till_landfall>=49 && hours_till_landfall<=72){days_till_landfall=3;}
		if(hours_till_landfall>=73 && hours_till_landfall<=96){days_till_landfall=4;}
		if(hours_till_landfall>=97 && hours_till_landfall<=120){days_till_landfall=5;}

		//at this point days_till_landfall must be at least 1 because there must be at least one timeline column active
		// this if statement just checking whether everything is alright
		long time_from_now_till_one_hour_before_landfall = time_from_now_till_landfall_alarm - (3600000* 24);
		if(days_till_landfall!=-1){
			for(int i =0;i<days_till_landfall-1;i++){
				set_alarm(time_from_now_till_one_hour_before_landfall,0);
				time_from_now_till_one_hour_before_landfall = time_from_now_till_one_hour_before_landfall - (3600000* 24);
			}
		}



	}

	/**
	 * This function sets one alarm(for notification) each time begin called.
	 * upon calling this function, a system level alarm gets set.
	 * This function uses action from manifest file to use NotificationDo class to actually send out a notification
	 * @param time how long from now the alarm will be triggered
	 * @param indicator indicator variable indicates what message to show in notification
	 */
    public void  set_alarm(long time, int indicator){

		Calendar clndr = Calendar.getInstance();
		clndr.add(Calendar.SECOND, 1);  //redundant line

		Intent intentA = new Intent("sagor.mohammad.action.DISPLAY_NOTIFICATION");
		intentA.putExtra("code",Integer.toString(code));
		intentA.putExtra("indicator",Integer.toString(indicator));
		PendingIntent pendingIntentX = PendingIntent.getBroadcast(getApplicationContext(), req_code, intentA, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManagerX = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManagerX.cancel(pendingIntentX);
		alarmManagerX.set(AlarmManager.RTC,clndr.getTimeInMillis()+time, pendingIntentX);
		code++;
		req_code++;

		//set the dummy intent
		intentA.setAction(Long.toString(System.currentTimeMillis()));

	}

	/**
	 * calling this function populates the inputtedTasksDates array with the dates of special inputted tasks
	 * so that we can use them in TimelineActivity.java class(this array contains same values as hurricane object)
	 */
	public void populate_task_date_array(){

    	//setting array for
		Utils.inputtedTasksDates[0] = dateAirportclose;
		Utils.inputtedTasksDates[1] = dateLandfall;
		Utils.inputtedTasksDates[2] = dateShelteropen;
		Utils.inputtedTasksDates[3] = dateHunkerdown;
		Utils.inputtedTasksDates[4] = dateReentry;
		Utils.inputtedTasksDates[5] = dateCfcreg1;
		Utils.inputtedTasksDates[6] = dateCfcreg2;
		Utils.inputtedTasksDates[7] = dateCfcnhq1;
		Utils.inputtedTasksDates[8] = dateCfcnhq2;
		//done
	}


	/**
	 * calling this function populates Task object and set the special inputted tasks to their respective timeline columns.
	 */
	public void special_tasks_timeline_column_calculation_and_set(){

		long till_now = System.currentTimeMillis();

		//this block calculates timeline column number only in Editing_Input mode
		if(sem_edit_inputs==1) {

			//get landfall/gfw date from hurricane and calculate the timeline column
			if (hurricane.getLandfall().getYear()>0){
				Date dateLandfallxyz = hurricane.getLandfall();
				long time_from_now_till_Gfw = dateLandfallxyz.getTime() - till_now;
				long hours_gfw = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Gfw);
				Gfw_col = timeline_col_no_for_special_tasks(hours_gfw);
			}else{Gfw_col = "";}

			//get Act date from hurricane and calculate the timeline column
			if(hurricane.getAirportsCloseTime().getYear()>0){
				Date dateActxyz = hurricane.getAirportsCloseTime();
				long time_from_now_till_Act = dateActxyz.getTime() - till_now;
				long hours_act = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Act);
				Act_col = timeline_col_no_for_special_tasks(hours_act);
			}else {Act_col = "";}

			//get Sheop date from hurricane and calculate the timeline column
			if(hurricane.getLeadTimeToOpenShelters().getYear()>0) {
				Date dateSheopxyz = hurricane.getLeadTimeToOpenShelters();
				long time_from_now_till_Sheop = dateSheopxyz.getTime() - till_now;
				long hours_sheop = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Sheop);
				Sheop_col = timeline_col_no_for_special_tasks(hours_sheop);
			}else{Sheop_col = "";}

			//get Hnkrdn date from hurricane and calculate the timeline column
			if(hurricane.getHunkerDownTime().getYear()>0){
				Date dateHnkrdnxyz = hurricane.getHunkerDownTime();
				long time_from_now_till_Hnkrdn = dateHnkrdnxyz.getTime() - till_now;
				long hours_hnkrdn = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Hnkrdn);
				Hnkrdn_col = timeline_col_no_for_special_tasks(hours_hnkrdn);
			}else {Hnkrdn_col = "";}

			//get Reentry date from hurricane and calculate the timeline column
			if(hurricane.getExpectedReEntryTime().getYear()>0) {
				Date dateReentryxyz = hurricane.getExpectedReEntryTime();
				long time_from_now_till_Reentry = dateReentryxyz.getTime() - till_now;
				long hours_reentry = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Reentry);
				Reentry_col = timeline_col_no_for_special_tasks(hours_reentry);
			}else {Reentry_col = "";}


		}


		if(sem_Gfw==1) {
			//calculate how many hours until Gfw and set the Gfw_col number
			long time_from_now_till_Gfw = dateLandfall.getTime() - till_now;
			long hours_gfw = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Gfw);
			Gfw_col = timeline_col_no_for_special_tasks(hours_gfw);
		}

		if(sem_Act == 1) {
			//calculate how many hours until Act and set the Act_col number
			long time_from_now_till_Act = dateAirportclose.getTime() - till_now;
			long hours_act = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Act);
			Act_col = timeline_col_no_for_special_tasks(hours_act);
		}

		if(sem_Sheop==1) {
			//calculate how many hours until Sheop and set the Sheop_col number
			long time_from_now_till_Sheop = dateShelteropen.getTime() - till_now;
			long hours_sheop = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Sheop);
			Sheop_col = timeline_col_no_for_special_tasks(hours_sheop);
		}

		if(sem_Hnkrdn==1) {
			//calculate how many hours until Hnkrdn and set the Hnkrdn_col number
			long time_from_now_till_Hnkrdn = dateHunkerdown.getTime() - till_now;
			long hours_hnkrdn = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Hnkrdn);
			Hnkrdn_col = timeline_col_no_for_special_tasks(hours_hnkrdn);
		}

		if(sem_Reentry==1) {
			//calculate how many hours until Reentry and set the Reentry_col number
			long time_from_now_till_Reentry = dateReentry.getTime() - till_now;
			long hours_reentry = TimeUnit.MILLISECONDS.toHours(time_from_now_till_Reentry);
			Reentry_col = timeline_col_no_for_special_tasks(hours_reentry);
		}

		//timeline column calculation for special inputted tasks is set to their respective global values
		//now pushing each values in Task object

		//setting timeline value for Act
		Task task_act = realm.where(Task.class).equalTo("name", Utils.inputtedTasks[0]).findFirst();
		realm.beginTransaction();
		task_act.setTimeline(Act_col);
		realm.commitTransaction();

		//setting timeline value for Gfw
		Task task_gfw = realm.where(Task.class).equalTo("name", Utils.inputtedTasks[1]).findFirst();
		realm.beginTransaction();
		task_gfw.setTimeline(Gfw_col);
		realm.commitTransaction();

		//setting timeline value for Sheop
		Task task_sheop = realm.where(Task.class).equalTo("name", Utils.inputtedTasks[2]).findFirst();
		realm.beginTransaction();
		task_sheop.setTimeline(Sheop_col);
		realm.commitTransaction();

		//setting timeline value for Hnkrdn
		Task task_hnkrdn = realm.where(Task.class).equalTo("name", Utils.inputtedTasks[3]).findFirst();
		realm.beginTransaction();
		task_hnkrdn.setTimeline(Hnkrdn_col);
		realm.commitTransaction();

		//setting timeline value for Reentry
		Task task_reentry = realm.where(Task.class).equalTo("name", Utils.inputtedTasks[4]).findFirst();
		realm.beginTransaction();
		task_reentry.setTimeline(Reentry_col);
		realm.commitTransaction();
		//done
	}


	/**
	 * @param hours
	 * after calculating for each special inputs their hours, we pass them here to get their column number
	 */
	public String timeline_col_no_for_special_tasks(long hours){
        long msTilLandfall = Utils.getHurricane().getLandfall().getTime() - (new Date()).getTime();
        long hoursTilLandfall = TimeUnit.MILLISECONDS.toHours(msTilLandfall);
        hours = hoursTilLandfall - hours;
        if (hours > 96) return "120";
        if (hours > 72) return "96";
        if (hours > 24) return "7248";
        if (hours > 0) return "24";
        if (hours == 0) return "0";
        return "-1";
	}

	/**
	* calling this function deletes the first 10 previously set alarms
	* */
	public void delete_alarms_when_reset(){
		int code = 100;  //code for notification
		int req_code = 0; //req_code for alarm
		for(int i =0; i<10; i++){
			delete_alarm(code, req_code,0);
			code++;
			req_code++;
		}

	}

	/**
	 * this function is called by delete_alarms_when_reset() function to delete alarms one by one, upto first 10.
	 * @param codee
	 * @param req_codee
	 * @param indicator
	 */
	public void delete_alarm(int codee, int req_codee, int indicator){
		Calendar clndr = Calendar.getInstance();
		clndr.add(Calendar.SECOND, 1);  //redundant line

		Intent intentA = new Intent("sagor.mohammad.action.DISPLAY_NOTIFICATION");
		intentA.putExtra("code",Integer.toString(codee));
		intentA.putExtra("indicator",Integer.toString(indicator));
		PendingIntent pendingIntentX = PendingIntent.getBroadcast(getApplicationContext(), req_codee, intentA, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManagerX = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManagerX.cancel(pendingIntentX);

		//set the dummy intent
		intentA.setAction(Long.toString(System.currentTimeMillis()));

	}

}
