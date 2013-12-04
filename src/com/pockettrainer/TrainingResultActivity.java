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
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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

	Bitmap bm;
	TextView runningTimeTV;
	TextView distanceTV;
	TextView numOfStepsTV;
	TextView speedTV;
	TextView monsterDefeatedTV;
	TextView monstersTV;
	TextView totalExpTV;
	TextView petNameTV;

	LinearLayout slime, frogger, gillian, papabear, observer, cyclops, ogre,
			darkness, owl, golem, toShare;
	TextView slimeQty, froggerQty, gillianQty, papabearQty, observerQty,
			cyclopsQty, ogreQty, darknessQty, owlQty, golemQty;

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
		this.myTraining = TRAINING_DAL.getTRAINING_Single(
				getApplicationContext(), myTraining.getID());
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
		speedTV = (TextView) findViewById(R.id.training_result_speed);
		monsterDefeatedTV = (TextView) findViewById(R.id.training_result_monster_defeated);
		totalExpTV = (TextView) findViewById(R.id.training_result_total_exp);

		petNameTV = (TextView) findViewById(R.id.training_result_pet_name_title);
		
		slime = (LinearLayout) findViewById(R.id.monster_slime);
		frogger = (LinearLayout) findViewById(R.id.monster_frogger);
		gillian = (LinearLayout) findViewById(R.id.monster_gillian);
		papabear = (LinearLayout) findViewById(R.id.monster_papabear);
		observer = (LinearLayout) findViewById(R.id.monster_observer);
		cyclops = (LinearLayout) findViewById(R.id.monster_cyclops);
		ogre = (LinearLayout) findViewById(R.id.monster_ogre);
		darkness = (LinearLayout) findViewById(R.id.monster_darkness);
		owl = (LinearLayout) findViewById(R.id.monster_owl);
		golem = (LinearLayout) findViewById(R.id.monster_golem);

		slimeQty = (TextView) findViewById(R.id.monster_slime_qty);
		froggerQty = (TextView) findViewById(R.id.monster_frogger_qty);
		gillianQty = (TextView) findViewById(R.id.monster_gillian_qty);
		papabearQty = (TextView) findViewById(R.id.monster_papabear_qty);
		observerQty = (TextView) findViewById(R.id.monster_observer_qty);
		cyclopsQty = (TextView) findViewById(R.id.monster_cyclops_qty);
		ogreQty = (TextView) findViewById(R.id.monster_ogre_qty);
		darknessQty = (TextView) findViewById(R.id.monster_darkness_qty);
		owlQty = (TextView) findViewById(R.id.monster_owl_qty);
		golemQty = (TextView) findViewById(R.id.monster_golem_qty);

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

			setSpeed(myTraining.getSPEED());

			int monsterDefeated = randomMonster(myTraining.getDISTANCE());
			monsterDefeatedTV.setText("" + monsterDefeated);

			if (myPet != null) {
				String name = myPet.getNAME();
				String upName = name.toUpperCase();
				petNameTV.setText(upName);
				int level = Integer.parseInt(myPet.getLEVEL());
				exp = calculateExperience(myTraining.getDISTANCE(),
						myTraining.getSTEPS(), level);
				totalExpTV.setText("" + exp);
			}

			listOfMonster();
			myTraining.setEXPERIENCE(exp);
			myTraining.setMONSTER_DEFEATED("" + monsterDefeated);

			updatePet(exp);

			try {
				TRAINING_DAL
						.updateTRAINING(getApplicationContext(), myTraining);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void updatePet(int exp) {
		int tempExp = 0;
		int tempLevel = 0;

		int monsterExp = myPet.getTOTAL_EXPERIENCE();
		int type = Integer.parseInt(myPet.getTYPE());

		monsterExp += exp;

		for (int i = 10; i > 0; i--) {
			if (monsterExp > eachLvExp[i]) {
				tempLevel = i + 1;
				tempExp = monsterExp - eachLvExp[i];
				break;
			}

		}

		if ((type == 2) && (monsterExp > eachLvExp[10])) {
			monsterExp = eachLvExp[10] - 1;
			tempExp = setupExperience("10") - 1;
			tempLevel = 10;
		}

		myPet.setTOTAL_EXPERIENCE(monsterExp);
		myPet.setLEVEL("" + tempLevel);
		myPet.setCURRENT_EXPERIENCE(tempExp);
		Log.i("POCKETTRAINER", "currexp: " + myPet.getCURRENT_EXPERIENCE());

		try {
			PET_DAL.updatePET(getApplicationContext(), myPet);
			PET uy = PET_DAL.getPET_Single(getApplicationContext(),
					myPet.getID());
			Log.i("POCKETTRAINER",
					"id: " + uy.getID() + " currexp: "
							+ myPet.getCURRENT_EXPERIENCE());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createArrayOfExp() {
		for (int i = 1; i < 11; i++) {
			if (i == 1) {
				eachLvExp[i] = setupExperience("" + i);
			} else {
				eachLvExp[i] = eachLvExp[i] + setupExperience("" + i);
			}

		}

	}

	public int setupExperience(String level) {
		double intLev = Double.parseDouble(level);
		return (int) ((int) 1000 * Math.pow(1.7, intLev));
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.not_main, menu);

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
	*/
	
	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			Log.d("ShareButton", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values
					.getString(SocialAuthAdapter.PROVIDER);
			Log.d("ShareButton", "Provider Name = " + providerName);

			String message = "I've been running with my pet " + myPet.getNAME()
					+ " for " + getDistance()
					+ "! Train yours today at @pocket_trainer";


			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.
			// adapter.updateStatus(message, new MessageListener(), false);
			try {
				toShare = (LinearLayout) findViewById(R.id.toShare);
				toShare.setDrawingCacheEnabled(true);
				toShare.buildDrawingCache();
				bm = toShare.getDrawingCache();
				if (bm != null)
					adapter.uploadImageAsync(message, "share.png", bm, 0,
							new UploadImageListener());
				else
					Toast.makeText(TrainingResultActivity.this,
							"Image not Uploaded", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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

	public boolean isTimeToEvolve() {
		int tempExp = myPet.getTOTAL_EXPERIENCE();
		int tempMaxExp = eachLvExp[10];
		int type = Integer.parseInt(myPet.getTYPE());

		if (tempExp >= tempMaxExp && type != 2) {
			return true;

		}

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continue_button:
			if (isTimeToEvolve()) {
				Intent i = new Intent(getApplicationContext(),
						EvolutionActivity.class);

				startActivity(i);
				this.finish();

			} else {

				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);

				startActivity(i);
				this.finish();
			}
			break;

		case R.id.notifDialog_ok:
			adapter.authorize(TrainingResultActivity.this, Provider.TWITTER);
			break;

		case R.id.notifDialog_cancel:
			notifDialog.dismiss();
			shared = false;
			break;
		default:
			break;
		}
	}

	private final class UploadImageListener implements
			SocialAuthListener<Integer> {

		@Override
		public void onExecute(String provider, Integer t) {
			notifDialog.dismiss();
			Integer status = t;
			Log.d("Custom-UI", String.valueOf(status));
			if (status.intValue() == 200 || status.intValue() == 201
					|| status.intValue() == 204)
				Toast.makeText(TrainingResultActivity.this, "Image Uploaded",
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(TrainingResultActivity.this,
						"Image not Uploaded", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SocialAuthError e) {

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
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 1);
					monster.add(myMonster);
				} else if (random <= 20 && random > 10) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 2);
					monster.add(myMonster);
				} else if (random <= 20 && random > 10) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 3);
					monster.add(myMonster);
				} else if (random <= 30 && random > 20) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 4);
					monster.add(myMonster);
				} else if (random <= 45 && random > 30) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 5);
					monster.add(myMonster);
				} else if (random <= 60 && random > 45) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 6);
					monster.add(myMonster);
				} else if (random <= 65 && random > 60) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 7);
					monster.add(myMonster);
				} else if (random <= 85 && random > 65) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 8);
					monster.add(myMonster);
				} else if (random <= 99 && random > 85) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 9);
					monster.add(myMonster);
				} else if (random == 100) {
					myMonster = MONSTER_DAL.getMONSTER_Single(
							getApplicationContext(), 10);
					monster.add(myMonster);
				}
			}
		}

		return monsterDefeated;
	}

	protected void listOfMonster() {
		MONSTER myMonster = new MONSTER();

		for (MONSTER momon : monster) {
			noMonster[momon.getID()] += 1;
		}

		for (int i = 1; i < 11; i++) {
			if (noMonster[i] != 0) {
				myMonster = MONSTER_DAL.getMONSTER_Single(
						getApplicationContext(), i);
				if (myMonster.getNAME().equals("Slime")) {
					slime.setVisibility(View.VISIBLE);
					slimeQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Frogger")) {
					frogger.setVisibility(View.VISIBLE);
					froggerQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Gillian")) {
					gillian.setVisibility(View.VISIBLE);
					gillianQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Papabear")) {
					papabear.setVisibility(View.VISIBLE);
					papabearQty.setText("x" + "x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Observer")) {
					observer.setVisibility(View.VISIBLE);
					observerQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Cyclops")) {
					cyclops.setVisibility(View.VISIBLE);
					cyclopsQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Ogre")) {
					ogre.setVisibility(View.VISIBLE);
					ogreQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Darkness")) {
					darkness.setVisibility(View.VISIBLE);
					darknessQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Owl")) {
					owl.setVisibility(View.VISIBLE);
					owlQty.setText("x" + noMonster[i]);
				}
				if (myMonster.getNAME().equals("Golem")) {
					golem.setVisibility(View.VISIBLE);
					golemQty.setText("x" + noMonster[i]);
				}
				// listMonster = listMonster.concat(myMonster.getNAME() + " x"
				// + noMonster[i] + " ");

			}
		}

	}

	protected int calculateExperience(float jarak, float langkah, int level) {

		int exp = 0;
		for (MONSTER momon : monster) {
			exp += ((momon.getBASE_EXPERIENCE() * level) / 2)
					+ ((jarak + langkah) / 20);
		}

		return exp;

	}

	protected void setDistance(float dist) {

		float ms, kms;
		ms = dist;

		if(ms >= 1000) {
			kms = ms / 1000f;
			distanceTV.setText(String.format("%.2f km", kms));
		} else {
			distanceTV.setText(String.format("%.2f m", ms));
		}

	}

	protected void setSpeed(float s) {

		float speed = s;
		speedTV.setText(String.format("%.2f m/s", speed));
	}

	protected String getDistance() {

		float ms, kms;
		ms = myTraining.getDISTANCE();
		String a = "";
		
		if(ms >= 1000) {
			kms = ms / 1000f;
			a = String.format("%.2f km", kms);
		} else {
			a = String.format("%.2f m", ms);
		}

		return a;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent t = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(t);
		this.finish();
	}

}
