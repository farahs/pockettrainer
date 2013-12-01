package com.pockettrainer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import com.example.pockettrainer.R;
import com.pockettrainer.database.dal.MONSTER_DAL;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.dal.TRAINING_DAL;
import com.pockettrainer.database.model.MONSTER;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.database.model.TRAINING;
import com.pockettrainer.helper.UserSession;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TrainingResultActivity extends Activity implements
		OnClickListener, SensorEventListener {

	private float mLastX, mLastY, mLastZ;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean mInitialized;
	private final float NOISE = (float) 11.0;
	private boolean shared = false;
	NotificationDialog notifDialog;
	SocialAuthAdapter adapter;
	Button share;
	Button cont;
	Button tweet;
	Button cancel;
	TRAINING myTraining;
	PET myPet;
	List<MONSTER> monster;
	int[] noMonster = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	int[] eachLvExp = new int[11];

	TextView runningTimeTV;
	TextView distanceTV;
	TextView numOfStepsTV;
	TextView monsterDefeatedTV;
	TextView monstersTV;
	TextView totalExpTV;

	Vibrator v;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training_result);

		myTraining = new TRAINING();
		myPet = new PET();
		monster = new ArrayList<MONSTER>();

		Intent i = getIntent();

		if (i != null) {
			Bundle bund = i.getExtras();

			if (bund != null) {
				myTraining = bund.getParcelable("TRAINING");
				processTraining();
			}
		} else {
			Log.i("POCKETTRAINER", "Gak ada bundle di intent");
		}

		processPet();
		setView();
		setEvent();
		setSensor();
		setAdapter();
		setData();
	}

	private void processTraining() {
		this.myTraining = TRAINING_DAL.getTRAINING_Single(getApplicationContext(), myTraining.getID());
	}

	private void processPet() {
		String myPetID = UserSession.getPetSession(getApplicationContext())
				.get(UserSession.PET_ID);
		this.myPet = PET_DAL.getPET_Single(getApplicationContext(),
				Integer.parseInt(myPetID));
	}

	private void setView() {
		notifDialog = new NotificationDialog(this);
		notifDialog.setCanceledOnTouchOutside(false);
		notifDialog.setTitle("Confirm Share");
		notifDialog.setMessage("Do you want to share your activities?");

		tweet = (Button) notifDialog.findViewById(R.id.notifDialog_ok);
		cancel = (Button) notifDialog.findViewById(R.id.notifDialog_cancel);
		share = (Button) findViewById(R.id.share_button);
		cont = (Button) findViewById(R.id.continue_button);

		runningTimeTV = (TextView) findViewById(R.id.training_result_running_time);
		distanceTV = (TextView) findViewById(R.id.training_result_distance);
		numOfStepsTV = (TextView) findViewById(R.id.training_result_steps);
		monsterDefeatedTV = (TextView) findViewById(R.id.training_result_monster_defeated);
		monstersTV = (TextView) findViewById(R.id.training_result_monsters);
		totalExpTV = (TextView) findViewById(R.id.training_result_total_exp);

	}

	private void setEvent() {
		cont.setOnClickListener(this);
		tweet.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	private void setSensor() {
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	private void setAdapter() {
		adapter = new SocialAuthAdapter(new ResponseListener());
		// adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addCallBack(Provider.TWITTER,
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
		// adapter.enable(share);
	}

	private void setData() {
		if (myTraining != null) {
			int exp = 0;
			createArrayOfExp();
			setTime(myTraining.getDURATION());
			setDistance(myTraining.getDISTANCE());
			numOfStepsTV.setText("" + myTraining.getSTEPS());
			int monsterDefeated = randomMonster(myTraining.getDISTANCE());
			monsterDefeatedTV.setText("" + monsterDefeated);

			if (myPet != null) {
				int level = Integer.parseInt(myPet.getLEVEL());
				exp = calculateExperience(myTraining.getDISTANCE(),
						myTraining.getSTEPS(), level);
				totalExpTV.setText("" + exp);

				String monsterList = listOfMonster();
				monstersTV.setText(monsterList);
			}
			
			myTraining.setEXPERIENCE(exp);
			myTraining.setMONSTER_DEFEATED("" + monsterDefeated);
			
			updatePet(exp);
			
			try {
				TRAINING_DAL.updateTRAINING(getApplicationContext(), myTraining);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void updatePet(int exp) {
		int tempExp = 0;
		int tempLevel = 0;
		
		int monsterExp  = myPet.getTOTAL_EXPERIENCE();
		monsterExp += exp;		
		
		for(int i = 10 ; i > 0 ; i--){
			if(monsterExp > eachLvExp[i]) {
				tempLevel = i+1;
				tempExp = monsterExp - eachLvExp[i];
				break;
			}
			
		}
		
		myPet.setTOTAL_EXPERIENCE(monsterExp);
		myPet.setLEVEL("" + tempLevel);
		myPet.setCURRENT_EXPERIENCE(tempExp);
		
		try {
			PET_DAL.updatePET(getApplicationContext(), myPet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createArrayOfExp() {
		for(int i = 1 ; i < 11 ; i++){
			if( i == 1 ){
				eachLvExp[i] = setupExperience("" + i);
			} else {
				eachLvExp[i] = eachLvExp[i] + setupExperience("" + i);
			}
			
		}
		
	}
	public int setupExperience(String level) {
		double intLev = Double.parseDouble(level);
		return (int) ((int) 1000 * Math.pow(2, intLev));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.findItem(R.id.action_settings).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						 Toast.makeText(getApplicationContext(), "Setting",
						 Toast.LENGTH_SHORT).show();
						 return true;
					}
				});

		return true;
	}

	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			Log.d("ShareButton", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values
					.getString(SocialAuthAdapter.PROVIDER);
			Log.d("ShareButton", "Provider Name = " + providerName);

			String message = "Pocket Trainer share button test. Stay tuned! will be out soon!";

			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.
			adapter.updateStatus(message, new MessageListener(), false);

		}

		@Override
		public void onError(SocialAuthError error) {
			Log.d("ShareButton", "Authentication Error: " + error.getMessage());
			// adapter.signOut(getApplicationContext(),
			// Provider.TWITTER.toString());
		}

		@Override
		public void onCancel() {
			Log.d("ShareButton", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Button", "Dialog Closed by pressing Back Key");
			shared = false;
		}

	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201
					|| status.intValue() == 204)
				Toast.makeText(TrainingResultActivity.this,
						"Message posted on " + provider, Toast.LENGTH_LONG)
						.show();
			else
				Toast.makeText(TrainingResultActivity.this,
						"Message not posted on " + provider, Toast.LENGTH_LONG)
						.show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continue_button:
			Intent i = new Intent(getApplicationContext(), MainActivity.class);

			startActivity(i);
			break;

		case R.id.notifDialog_ok:
			adapter.authorize(TrainingResultActivity.this, Provider.TWITTER);
			notifDialog.dismiss();
			break;

		case R.id.notifDialog_cancel:
			notifDialog.dismiss();
			shared = false;
			break;
		default:
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float x = event.values[0];

		float y = event.values[1];

		float z = event.values[2];

		if (!mInitialized) {

			mLastX = x;

			mLastY = y;

			mLastZ = z;

			mInitialized = true;

		} else {

			float deltaX = Math.abs(mLastX - x);

			float deltaY = Math.abs(mLastY - y);

			float deltaZ = Math.abs(mLastZ - z);

			if (deltaX > NOISE || deltaY > NOISE || deltaZ > NOISE) {
				if (!shared) {
					shared = true;
					v.vibrate(500);
					notifDialog.show();
				}
			}

			mLastX = x;

			mLastY = y;

			mLastZ = z;

		}

	}

	@Override
	protected void onResume() {

		super.onResume();

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	protected void onPause() {

		super.onPause();

		mSensorManager.unregisterListener(this);

	}

	protected void setTime(float time) {

		long secs, mins, hrs;
		String seconds, minutes, hours;

		secs = (long) (time / 1000);
		mins = (long) ((time / 1000) / 60);
		hrs = (long) (((time / 1000) / 60) / 60);

		secs = secs % 60;
		seconds = String.valueOf(secs);

		if (secs == 0) {
			seconds = "00";
		}
		if (secs < 10 && secs > 0) {
			seconds = "0" + seconds;
		}

		mins = mins % 60;
		minutes = String.valueOf(mins);

		if (mins == 0) {
			minutes = "00";
		}
		if (mins < 10 && mins > 0) {
			minutes = "0" + minutes;
		}

		hours = String.valueOf(hrs);
		if (hrs == 0) {
			hours = "00";
		}
		if (hrs < 10 && hrs > 0) {
			hours = "0" + hours;
		}

		runningTimeTV.setText(hours + ":" + minutes + ":" + seconds);
	}

	protected int randomMonster(float jarak) {
		int numberOfMonster = (int) jarak / 100;
		int monsterDefeated = 0;
		MONSTER myMonster = new MONSTER();

		for (int i = 0; i < numberOfMonster; i++) {

			
			int monsterAppearance = (int) (Math.random() * 10 + 1);

			if (monsterAppearance <= 6) {
				monsterDefeated++;
				int random = (int) (Math.random() * 100 + 1);

				if (random <= 10) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 1);
					monster.add(myMonster);
				} else if (random <= 20 && random > 10) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 2);
					monster.add(myMonster);
				} else if (random <= 20 && random > 10) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 3);
					monster.add(myMonster);
				} else if (random <= 30 && random > 20) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 4);
					monster.add(myMonster);
				} else if (random <= 45 && random > 30) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 5);
					monster.add(myMonster);
				} else if (random <= 60 && random > 45) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 6);
					monster.add(myMonster);
				} else if (random <= 65 && random > 60) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 7);
					monster.add(myMonster);
				} else if (random <= 85 && random > 65) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 8);
					monster.add(myMonster);
				} else if (random <= 99 && random > 85) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 9);
					monster.add(myMonster);
				} else if (random == 100) {
					myMonster = MONSTER_DAL.getTRAINING_Single(
							getApplicationContext(), 10);
					monster.add(myMonster);
				}
			}
		}

		return monsterDefeated;
	}

	protected String listOfMonster() {
		String listMonster = "";
		MONSTER myMonster = new MONSTER();

		for (MONSTER momon : monster) {
			noMonster[momon.getID()] += 1;
		}

		for (int i = 1; i < 11; i++) {
			if(noMonster[i] != 0) {
			myMonster = MONSTER_DAL.getTRAINING_Single(getApplicationContext(),
					i);
			listMonster = listMonster.concat(myMonster.getNAME() + " x"
					+ noMonster[i] + " ");

			}
		}

		return listMonster;
	}

	protected int calculateExperience(float jarak, float langkah, int level) {

		int exp = 0;
		for (MONSTER momon : monster) {
			exp += ((momon.getBASE_EXPERIENCE() * level) / 2)
					+ ((jarak + langkah) / 5);
		}

		return exp;

	}

	protected void setDistance(float dist) {
		//
		float ms, kms;
		// String meters, kilos;
		//
		ms = dist;
		kms = ms / 1000f;

		distanceTV.setText(String.format("%.2f Km", kms));
		//
		// ms = ms % 1000;
		// meters = String.valueOf(ms);
		// if(ms == 0) {
		// meters = "000";
		// }
		//
		//
		// kilos = String.valueOf(kms);

	}

}
