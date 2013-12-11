package com.pockettrainer;

import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Pattern;

import com.example.pockettrainer.R;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.dal.USER_DAL;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.database.model.USER;
import com.pockettrainer.helper.FormatHelper;
import com.pockettrainer.helper.UserSession;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Monster 2013
 *
 */
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
	
	boolean loginStatus, havePet;
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
		getMenuInflater().inflate(R.menu.not_main, menu);
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
				this.finish();
			} else {
				Toast.makeText(getApplicationContext(), "Please insert your pet name",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	private boolean processData() {
		String a = petNameET.getText().toString();
		
		if (TextUtils.isEmpty(a) || Pattern.matches("( )+", a)) {
			return false;
		} else {
			this.myPet.setNAME(a);
		}

		if (petBirthDateTV.getText().toString() != null
				&& petBirthDateTV.getText().toString() != "") {
			this.myPet.setBIRTH_DATE(nowDate);
			Log.i("POCKETTRAINER", "" + myPet.getBIRTH_DATE());
		} else {
			return false;
		}

		this.myPet.setTYPE("1");
		this.myPet.setLEVEL("1");
		this.myPet.setCURRENT_EXPERIENCE(0);
		this.myPet.setTOTAL_EXPERIENCE(0);
		this.myPet.setMOOD("4");
		this.myPet.setHUNGER_INDICATOR(100);
		this.myPet.setSLEEP_INDICATOR(100);
		this.myPet.setHYGIENE_INDICATOR(100);
		this.myPet.setRELATIONSHIP_INDICATOR(100);
		
		return true;
	}

	private boolean processUser() {

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
			
			UserSession.setPetSession(getApplicationContext(), "" + myPet.getID());
			
			return true;
		}

		return false;
	}

	public void onBackPressed() {
		Intent i = new Intent(getApplicationContext(), SelectEnvironmentActivity.class);
		startActivity(i);
		this.finish();
	};
	
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
