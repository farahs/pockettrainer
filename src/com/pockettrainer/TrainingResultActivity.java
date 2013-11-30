package com.pockettrainer;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import com.example.pockettrainer.R;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.database.model.TRAINING;
import com.pockettrainer.helper.UserSession;

import android.os.Bundle;
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
	private final float NOISE = (float) 7.0;
	private boolean shared = false;
	NotificationDialog notifDialog;
	SocialAuthAdapter adapter;
	Button share;
	Button cont;
	Button tweet;
	Button cancel;
	TRAINING myTraining;
	PET myPet;

	TextView runningTimeTV;
	TextView distanceTV;
	TextView burnedCaloriesTV;
	TextView numOfStepsTV;
	TextView monsterDefeatedTV;
	TextView trainingExpTV;
	TextView totalExpTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training_result);
		
		myTraining = new TRAINING();
		myPet = new PET();
		Intent i = getIntent();

		if (i != null) {
			Bundle bund = i.getExtras();

			if (bund != null) {
				myTraining = bund.getParcelable("TRAINING");
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

	private void processPet() {
		String petId = UserSession.getPetSession(getApplicationContext()).get(UserSession.PET_ID);
		if(!petId.equals("0")){
			myPet = PET_DAL.getPET_Single(getApplicationContext(),Integer.parseInt(petId));
		}
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
		burnedCaloriesTV = (TextView) findViewById(R.id.training_result_cal_burned);
		numOfStepsTV = (TextView) findViewById(R.id.training_result_steps);
		monsterDefeatedTV = (TextView) findViewById(R.id.training_result_monster_defeated);
		trainingExpTV = (TextView) findViewById(R.id.training_result_exp);
		totalExpTV = (TextView) findViewById(R.id.training_result_total_exp);

	}
	
	private void setEvent() {
		cont.setOnClickListener(this);
		tweet.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	
	private void setSensor(){
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void setAdapter() {
		adapter = new SocialAuthAdapter(new ResponseListener());
		// adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addCallBack(Provider.TWITTER,
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
		// adapter.enable(share);
	}
	
	private void setData() {
		if(myTraining != null) {
			setTime(myTraining.getDURATION());
			
		}
		
		if(myPet != null) {
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.findItem(R.id.action_settings).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// Toast.makeText(getApplicationContext(), "Setting",
						// Toast.LENGTH_SHORT).show();
						// return true;
						Intent intent = new Intent(TrainingResultActivity.this
								.getApplicationContext(),
								TestAccelerometerActivity.class);
						startActivity(intent);
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
			Intent i = new Intent(getApplicationContext(), TestGMaps.class);

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
		
		secs = (long) (time/1000);
		mins = (long) ((time/1000)/60);
		hrs = (long) (((time/1000)/60)/60);
		
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
	
	protected void setDistance(float dist) {
//		
		float ms, kms;
//		String meters, kilos;
//		
		ms = dist;
		kms = ms/1000f;
		
		distanceTV.setText(String.format("%.2f Km", kms));
//		
//		ms = ms % 1000;
//		meters = String.valueOf(ms);
//		if(ms == 0) {
//			meters = "000";
//		}
//		
//		
//		kilos = String.valueOf(kms);
		
	}
	
}
