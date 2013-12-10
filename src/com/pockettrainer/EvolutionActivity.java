package com.pockettrainer;

import java.sql.SQLException;

import com.example.pockettrainer.R;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.helper.UserSession;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Monster 2013
 *
 */
public class EvolutionActivity extends Activity implements AnimationListener,
		OnClickListener, SensorEventListener {

	private float mLastX, mLastY, mLastZ;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean mInitialized;
	private final float NOISE = (float) 7.0;
	private boolean enableShake = false;
	private int shaked = 0;

	Button cont;
	ImageView egg;
	ImageView evl;
	ImageView star;
	TextView txt;
	Animation animBounce;
	Animation animFadeOut;
	Animation animFadeIn;
	Animation animRotate;
	int frame = 0;
	private Display mDisplay;

	PET myPet;

	Vibrator v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myPet = new PET();
		initializePet();

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.activity_evolution);

		setView();
		setSensor();
		setEvent();

		this.mDisplay = this.getWindowManager().getDefaultDisplay();

		setEnvironment();
		setAnimation();
	}

	private void setView() {
		cont = (Button) findViewById(R.id.contBtn);

		txt = (TextView) findViewById(R.id.text_evolve);
		egg = (ImageView) findViewById(R.id.egg);
		evl = (ImageView) findViewById(R.id.evolution);
		star = (ImageView) findViewById(R.id.star);

	}

	private void setSensor() {
		mInitialized = false;

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	private void setEvent() {
		cont.setOnClickListener(this);
	}

	private void setEnvironment() {
		if (myPet.getENVIRONMENT().equals("1")) {
			evl.setImageResource(R.drawable.fire_single);
		} else if (myPet.getENVIRONMENT().equals("2")) {
			evl.setImageResource(R.drawable.grass_single);
		} else if (myPet.getENVIRONMENT().equals("3")) {
			evl.setImageResource(R.drawable.water_single);
		}
	}

	private void setAnimation() {
		animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.idle);
		animBounce.setAnimationListener(this);
		animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_out);
		animFadeOut.setAnimationListener(this);
		animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_in);
		animFadeIn.setAnimationListener(this);
		animRotate = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.rotating);
		animRotate.setAnimationListener(this);
		egg.startAnimation(animBounce);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contBtn:
			if (frame == 0) {
				frame = 1;
				cont.setVisibility(View.GONE);
			} else if (frame == 4) {
				frame = 5;
				cont.setVisibility(View.GONE);
			} else if (frame == 6) {
				petEvolve();
				Intent x = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(x);
				this.finish();
			}
			break;

		default:
			break;
		}
	}

	public void petEvolve() {
		String a = "" + (Integer.parseInt(myPet.getTYPE()) + 1);
		myPet.setTYPE(a);
		myPet.setLEVEL("1");
		myPet.setCURRENT_EXPERIENCE(0);
		myPet.setTOTAL_EXPERIENCE(0);
		myPet.setMOOD("4");
		myPet.setHUNGER_INDICATOR(100);
		myPet.setSLEEP_INDICATOR(100);
		myPet.setHYGIENE_INDICATOR(100);
		myPet.setRELATIONSHIP_INDICATOR(100);

		try {
			PET_DAL.updatePET(getApplicationContext(), myPet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PET uhuy = PET_DAL.getPET_Single(
				getApplicationContext(),
				Integer.parseInt(UserSession.getPetSession(
						getApplicationContext()).get(UserSession.PET_ID)));
		Log.i("POCKETTRAINER", uhuy.getTYPE() + " " + uhuy.getID());

	}

	@Override
	public void onAnimationEnd(Animation anim) {
		// TODO Auto-generated method stub
		if (frame == 0) {
			egg.startAnimation(animBounce);
		} else if (frame == 1) {
			egg.startAnimation(animFadeOut);
			star.setVisibility(View.VISIBLE);
			star.startAnimation(animFadeIn);
			frame = 2;
		} else if (frame == 2) {
			egg.setVisibility(View.GONE);
			if (anim == animRotate)
				frame = 3;
			txt.setText("Hold On! It's going to be amazing!");
			star.startAnimation(animRotate);
		} else if (frame == 3) {
			star.startAnimation(animRotate);
			txt.setText("Shake your device to make your pet evolve!");
			enableShake = true;
		} else if (frame == 4) {
			txt.setText("It's done! Tap the button to continue!");
			cont.setVisibility(View.VISIBLE);
			star.startAnimation(animRotate);
		} else if (frame == 5) {
			txt.setText("Congratulation! Your pet has evolved!");
			evl.setVisibility(View.VISIBLE);
			evl.startAnimation(animFadeIn);
			star.startAnimation(animFadeOut);
			frame = 6;
		} else if (frame == 6) {
			if (anim == animFadeOut)
				star.setVisibility(View.GONE);
			cont.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float x = event.values[0];

		float y = event.values[1];

		float z = event.values[2];

		if (enableShake) {

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
					shaked += 1;
				}

				mLastX = x;

				mLastY = y;

				mLastZ = z;

				if (shaked > 20 && shaked < 40) {
					txt.setText("Not enough! Shake more!");
				} else if (shaked > 40) {
					frame = 4;
					enableShake = false;
					v.vibrate(500);
				}
			}

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

	protected void initializePet() {
		String myPetID = UserSession.getPetSession(getApplicationContext())
				.get(UserSession.PET_ID);
		this.myPet = PET_DAL.getPET_Single(getApplicationContext(),
				Integer.parseInt(myPetID));
	}
}
