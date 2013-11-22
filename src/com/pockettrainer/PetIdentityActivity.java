package com.pockettrainer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.pockettrainer.R;
import com.example.pockettrainer.R.id;
import com.example.pockettrainer.R.layout;
import com.example.pockettrainer.R.menu;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PetIdentityActivity extends Activity implements OnClickListener, AnimationListener {

	USER myUser;
	Button startBtn;
	PET myPet;
	Animation animBounce;
	ImageView pet_sprt;
	ImageView pet_env;
	TextView petEnvironmentTV;
	EditText petNameET;
	TextView petBirthDateTV;
	
	String clause = "You decided to grow your pet on ";
	
	boolean loginStatus;
	Date nowDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		pet_sprt = (ImageView) findViewById(R.id.egg);
		pet_env = (ImageView) findViewById(R.id.pet_env_iv);
		animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.idle);
		startBtn = (Button) findViewById(R.id.start);
		petEnvironmentTV = (TextView) findViewById(R.id.pet_environment);
		petNameET = (EditText) findViewById(R.id.name);
		petBirthDateTV = (TextView) findViewById(R.id.pet_birth);
	}

	private void setEvent() {
		startBtn.setOnClickListener(this);
		animBounce.setAnimationListener(this);
		pet_sprt.startAnimation(animBounce);
	}

	private void setData() {
		if (myPet.getENVIRONMENT().equals("1")) {
			petEnvironmentTV.setText(clause + "Fire environment");
			pet_env.setImageResource(R.drawable.env_fire);
		} else if (myPet.getENVIRONMENT().equals("2")) {
			petEnvironmentTV.setText(clause + "Grass environment");
			pet_env.setImageResource(R.drawable.env_grass);
		} else if (myPet.getENVIRONMENT().equals("3")) {
			petEnvironmentTV.setText(clause + "Water environment");
			pet_env.setImageResource(R.drawable.env_water);
		}

		nowDate = new Date();
		petBirthDateTV.setText(FormatHelper.getFormattedStringDate(nowDate,
				FormatHelper.SHOW_DATE_FORMAT));

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
				bund.putParcelable("USER", this.myUser);
				bund.putParcelable("PET", this.myPet);
				i.putExtras(bund);
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(), "ELSE",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.pet_birth:
			// DialogFragment newFragment = new
			// DatePickerFragment(petBirthDateTV);
			// newFragment.show(getFragmentManager(), "datePicker");
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
			this.myPet.setBIRTH_DATE(nowDate);
			Log.i("POCKETTRAINER", "" + myPet.getBIRTH_DATE());
		} else {
			return false;
		}

		this.myPet.setLEVEL("1");
		this.myPet.setCURRENT_EXPERIENCE(0);
		this.myPet.setMOOD("1");
		this.myPet.setHUNGER_INDICATOR(100);
		this.myPet.setSLEEP_INDICATOR(100);
		this.myPet.setHYGIENE_INDICATOR(100);
		this.myPet.setRELATIONSHIP_INDICATOR(100);
		this.myPet.setLAST_UPDATE_HUNGER_INDICATOR(nowDate);
		this.myPet.setLAST_UPDATE_SLEEP_INDICATOR(nowDate);
		this.myPet.setLAST_UPDATE_HYGIENE_INDICATOR(nowDate);
		this.myPet.setLAST_UPDATE_RELATIONSHIP_INDICATOR(nowDate);
		
		return true;
	}

	private boolean processUser() {

		Toast.makeText(getApplicationContext(), "PROCESS USER MULAI",
				Toast.LENGTH_SHORT).show();
		try {
			this.myUser.setTRAINING_HISTORY("");
			USER_DAL.insertUSER(getApplicationContext(), this.myUser);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		try {
			this.myUser = USER_DAL.getUSER_All(getApplicationContext()).get(0);
			this.myPet.setUSER_ID(this.myUser.getID());
			PET_DAL.insertPET(getApplicationContext(), this.myPet);

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		try {
			this.myPet = PET_DAL.getPET_All(getApplicationContext()).get(0);
			Log.i("POCKETTRAINER", "InsertPet" + myPet.getNAME());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean processSession() {
		UserSession
				.setUserSession(getApplicationContext(), "" + myUser.getID());

		loginStatus = UserSession.isLoggedIn(getApplicationContext());

		if (loginStatus) {
			UserSession.setUserSession(getApplicationContext(),
					"" + myUser.getID());
			UserSession.setIdUser("" + myUser.getID());
			return true;
		}

		return false;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		pet_sprt.startAnimation(animBounce);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}