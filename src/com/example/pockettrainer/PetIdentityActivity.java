package com.example.pockettrainer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.pockettrainer.MainActivity;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.dal.USER_DAL;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.database.model.USER;
import com.pockettrainer.helper.FormatHelper;
import com.pockettrainer.helper.UserSession;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PetIdentityActivity extends Activity implements OnClickListener {

	USER myUser;
	Button startBtn;
	PET myPet;
	TextView petEnvironmentTV;
	EditText petNameET;
	TextView petBirthDateTV;
	boolean loginStatus;
	Date nowDate; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pet_identity);

		myPet = new PET();
		myUser = new USER();
		
		Intent i = getIntent();

		if (i != null) {
			Bundle bund = i.getExtras();

			if (bund != null) {
				myPet = bund.getParcelable("PET");
			}
		} else {
			Log.i("POCKETTRAINER", "Gak ada bundle di intent");
		}

		setView();
		setEvent();
		setData();

	}

	private void setView() {
		startBtn = (Button) findViewById(R.id.start);
		petEnvironmentTV = (TextView) findViewById(R.id.pet_environment);
		petNameET = (EditText) findViewById(R.id.pet_name);
		petBirthDateTV = (TextView) findViewById(R.id.pet_birth);
	}

	private void setEvent() {
		startBtn.setOnClickListener(this);
	}

	private void setData() {
		if (myPet.getENVIRONMENT().equals("1")) {
			petEnvironmentTV.setText("Fire");
		} else if (myPet.getENVIRONMENT().equals("2")) {
			petEnvironmentTV.setText("Grass");
		} else if (myPet.getENVIRONMENT().equals("3")) {
			petEnvironmentTV.setText("Water");
		}
		
		nowDate = new Date();
		petBirthDateTV.setText(FormatHelper.getFormattedStringDate(nowDate, FormatHelper.SHOW_DATE_FORMAT));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pet_identity, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			if (processData() && processUser() && processSession()) {
				
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				Bundle bund = new Bundle();
				bund.putParcelable("USER", myUser);
				bund.putParcelable("PET", myPet);
				i.putExtras(bund);
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(), "ELSE",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.pet_birth:
//			DialogFragment newFragment = new DatePickerFragment(petBirthDateTV);
//			newFragment.show(getFragmentManager(), "datePicker");
			break;

		default:
			break;
		}
	}

	private boolean processData() {
		if (petNameET.getText().toString() != null
				&& petNameET.getText().toString() != "") {
			this.myPet.setNAME(petNameET.getText().toString());
		} else {
			return false;
		}

		if (petBirthDateTV.getText().toString() != null
				&& petBirthDateTV.getText().toString() != "") {
			myPet.setBIRTH_DATE(FormatHelper.getFormattedDate(petBirthDateTV.getText().toString(), FormatHelper.SYSTEM_DATE_FORMAT));
			Log.i("POCKETTRAINER", "" + myPet.getBIRTH_DATE());
		} else {
			return false;
		}
		
		return true;
	}

	private boolean processUser() {
		
		Toast.makeText(getApplicationContext(), "PROCESS USER MULAI",
				Toast.LENGTH_SHORT).show();
		try {
			myUser.setTRAINING_HISTORY("");
			USER_DAL.insertUSER(getApplicationContext(), myUser);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			myUser = USER_DAL.getUSER_All(getApplicationContext()).get(0);
			myPet.setUSER_ID(myUser.getID());
			PET_DAL.insertPET(getApplicationContext(), myPet);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			myPet = PET_DAL.getPET_All(getApplicationContext()).get(0);
			Log.i("POCKETTRAINER", "InsertPet" + myPet.getNAME());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	private boolean processSession() {
		UserSession.setUserSession(getApplicationContext(), "" + myUser.getID());
		
		loginStatus = UserSession.isLoggedIn(getApplicationContext());
		
		if(loginStatus){
			UserSession.setUserSession(getApplicationContext(), "" + myUser.getID());
			UserSession.setIdUser("" + myUser.getID());
			return true;
		}
		
		return false;
	}
}

class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{

	TextView	et;

	public DatePickerFragment(TextView et)
	{
		this.et = et;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

//		Date date = c.getTime();

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(this.getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day)
	{
		// Do something with the date chosen by the user

		final Calendar c = Calendar.getInstance();
		c.set(year, month, day);

		Date date = c.getTime();

		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
//		Date tgl = null;

		this.et.setText(dateFormat.format(date));
	}
}

