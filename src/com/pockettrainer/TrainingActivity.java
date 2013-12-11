package com.pockettrainer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.pockettrainer.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pockettrainer.database.dal.TRAINING_DAL;
import com.pockettrainer.database.model.TRAINING;
import com.pockettrainer.helper.UserSession;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Monster 2013
 *
 */
public class TrainingActivity extends Activity implements OnClickListener,
		LocationListener, SensorEventListener {

	final int START = 0;
	final int RESUME = 1;
	
	TextView timerTV;
	TextView timerMsTV;
	Button startBtn;
	Button pauseBtn;
	Button resumeBtn;
	Button stopBtn;

	Button setting;
	Button cancel;
	NotificationDialog dialog;

	protected GoogleMap myMap;
	LocationManager locationManager;
	List<LatLng> trackedPoint;
	Location lastLocation;
	Location initialLocation;
	Location finalLocation;
	Location onResumeLocation;
	float speed;
	float totalDistance;
	boolean running;

	private long secs, mins, hrs;
	private String hours, minutes, seconds, milliseconds;
	private long startTime = 0l;
	private Handler customHandler = new Handler();
	private long updatedTime = 0L;
	private final int REFRESH_RATE = 50;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;

	TRAINING myTraining;

	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mGyroscope;
	private final float NOISE = (float) 4.0;
	int stepsCount;
	boolean hasSteps;
	boolean hasMovements;
	boolean hasAccel;
	boolean hasGyro;
	boolean isNetworkOK;
	boolean isGPSOK;
	int counter;
	int minXGyro, maxXGyro, minYGyro, maxYGyro;

	Notification.Builder builder;
	NotificationManager notificationManager;
	Notification notification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		trackedPoint = new ArrayList<LatLng>();
		myTraining = new TRAINING();

		setupView();
		setupEvent();

		try {
			initializeMap();
			isNetworkOK = isNetworkEnabled();
			isGPSOK = isGPSEnabled();
			running = false;
			hasSteps = false;
			hasMovements = false;
			if (isGPSOK && isNetworkOK) {
				setLocationAndCamera();
			} else {
				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setupSensor();
		setupNotification();
	}

	private void setupView() {
		timerTV = (TextView) this.findViewById(R.id.train_timer);
		timerMsTV = (TextView) this.findViewById(R.id.train_timer_ms);
		startBtn = (Button) this.findViewById(R.id.training_start);
		pauseBtn = (Button) this.findViewById(R.id.training_pause);
		resumeBtn = (Button) this.findViewById(R.id.training_resume);
		stopBtn = (Button) this.findViewById(R.id.training_stop);

		startBtn.setVisibility(View.VISIBLE);
		pauseBtn.setVisibility(View.GONE);
		resumeBtn.setVisibility(View.GONE);
		stopBtn.setVisibility(View.GONE);

		dialog = new NotificationDialog(this);
		dialog.setTitle("Oops!");
		dialog.setMessage("GPS is not enabled");
		setting = (Button) dialog.findViewById(R.id.notifDialog_ok);
		setting.setText("Settings");
		cancel = (Button) dialog.findViewById(R.id.notifDialog_cancel);
		cancel.setText("Cancel");
	}

	private void setupEvent() {
		startBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		resumeBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(in);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
//				if (isGPSOK || isNetworkOK) {
//					setLocationAndCamera();
//				}
			}
		});
	}

	private void setupSensor() {
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		if (mAccelerometer != null) {
			hasAccel = true;
		} else {
			hasAccel = false;
		}
		Log.i("POCKETTRAINER", "" + hasAccel);

		if (mGyroscope != null) {
			hasGyro = true;
		} else {
			hasGyro = false;
		}

		minXGyro = 100000;
		maxXGyro = 0;
		minYGyro = 10000;
		maxYGyro = 0;
		Log.i("POCKETTRAINER", "" + hasGyro);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.not_main, menu);
	 * menu.findItem(R.id.action_settings).setOnMenuItemClickListener( new
	 * OnMenuItemClickListener() {
	 * 
	 * @Override public boolean onMenuItemClick(MenuItem item) { // Intent x =
	 * new Intent(getApplicationContext(), // TestGyroActivity.class); //
	 * startActivity(x); return true; } });
	 * 
	 * return super.onCreateOptionsMenu(menu); }
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.training_start:
			if (readyToRun(this.START)) {
				trackedPoint = new ArrayList<LatLng>();
				running = true;
				totalDistance = 0f;
				startSensor();
				startBtn.setVisibility(View.GONE);
				pauseBtn.setVisibility(View.VISIBLE);
				resumeBtn.setVisibility(View.GONE);
				stopBtn.setVisibility(View.GONE);
				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			}
			break;
		case R.id.training_pause:
			startBtn.setVisibility(View.GONE);
			pauseBtn.setVisibility(View.GONE);
			resumeBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.VISIBLE);
			timeSwapBuff += timeInMilliseconds;
			running = false;
			customHandler.removeCallbacks(updateTimerThread);
			stopTrain();
			break;
		case R.id.training_resume:
			if (readyToRun(this.RESUME)) {
				startBtn.setVisibility(View.GONE);
				pauseBtn.setVisibility(View.VISIBLE);
				resumeBtn.setVisibility(View.GONE);
				stopBtn.setVisibility(View.GONE);
				startTime = SystemClock.uptimeMillis();
				running = true;
				customHandler.postDelayed(updateTimerThread, 0);
			}
			break;
		case R.id.training_stop:
			running = false;
			stopTrain();
			turnGPSOff();
			stopSensor();
			trainingSummary();
			Intent i = new Intent(getApplicationContext(),
					TrainingResultActivity.class);
			Bundle bund = new Bundle();
			bund.putParcelable("TRAINING", this.myTraining);
			i.putExtras(bund);
			startActivity(i);
			destroyNotification();
			this.finish();
			break;
		default:
			break;
		}
	}

	private void startSensor() {
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGyroscope,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void stopSensor() {
		mSensorManager.unregisterListener(this, mAccelerometer);
		mSensorManager.unregisterListener(this, mGyroscope);
	}
	
	private Runnable updateTimerThread = new Runnable() {

		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeSwapBuff + timeInMilliseconds;

			secs = (long) (updatedTime / 1000);
			mins = (long) ((updatedTime / 1000) / 60);
			hrs = (long) (((updatedTime / 1000) / 60) / 60);

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

			milliseconds = String.valueOf((long) updatedTime);

			if (milliseconds.length() == 2) {
				milliseconds = "0" + milliseconds;
			}
			if (milliseconds.length() <= 1) {
				milliseconds = "00";
			}

			if (milliseconds.length() >= 3) {
				milliseconds = milliseconds.substring(
						milliseconds.length() - 3, milliseconds.length() - 2);
			}
			timerTV.setText(hours + ":" + minutes + ":" + seconds);
			timerMsTV.setText("." + milliseconds);

			customHandler.postDelayed(this, REFRESH_RATE);

		}

	};

	private void initializeMap() {

		if (myMap == null) {

			myMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			myMap.setMyLocationEnabled(true);

			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}

	}

	private void setLocationAndCamera() {
		Location loc;
		
		try {
			Criteria crit = new Criteria();
			loc = locationManager.getLastKnownLocation(locationManager
					.getBestProvider(crit, false));
			Log.i("POCKETTRAINER", "location by gps");
		} catch (Exception e) {
			loc = myMap.getMyLocation();
			Log.i("POCKETTRAINER", "location by map");
		}

		if(loc != null) {
			CameraPosition camPos = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(), loc.getLongitude()))
					.zoom(15.8f).build();
			CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
	
			myMap.moveCamera(camUpdate);
		} else {
			Toast.makeText(getApplicationContext(), "Please wait... We are trying to get your location", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean readyToRun(int CODE) {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {

			if (isNetworkOK && isGPSOK) {

				if (isNetworkOK) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 1000, 1, this);
				}
				if (isGPSOK) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 1000, 1, this);
				}

				Criteria crit = new Criteria();
				
				if(CODE == this.START) {
					
					initialLocation = locationManager
							.getLastKnownLocation(locationManager.getBestProvider(
									crit, false));
					lastLocation = locationManager
							.getLastKnownLocation(locationManager.getBestProvider(
									crit, false));
					finalLocation = locationManager
							.getLastKnownLocation(locationManager.getBestProvider(
									crit, false));
					
					if (initialLocation != null) {
						speed = 0f;
						totalDistance = 0f;
						running = true;
						stepsCount = 0;
						counter = 0;
						makeMarker(initialLocation, "START");
						return true;
					} else {
						Toast.makeText(getApplicationContext(),
								"You aren't ready to Train", Toast.LENGTH_SHORT)
								.show();
						return false;
					}
					
				}
				
				if(CODE == this.RESUME) {
					onResumeLocation = locationManager
							.getLastKnownLocation(locationManager.getBestProvider(
									crit, false));
					
					if(onResumeLocation != null) {
						return true;
					} else {
						Toast.makeText(getApplicationContext(),
								"You are not ready to resume your training", Toast.LENGTH_SHORT)
								.show();
						return false;
					}
				}
				
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"You don't have internet connection", Toast.LENGTH_SHORT).show();
			return false;
		}

		return false;

	}

	private void stopTrain() {
		Criteria crit = new Criteria();
		finalLocation = locationManager.getLastKnownLocation(locationManager
				.getBestProvider(crit, false));
		if (initialLocation != null && finalLocation != null) {
			makeMarker(finalLocation, "FINISH");
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.i("POCKETTRAINER", "locationchanged luar");

		if ((hasAccel == true) && (hasGyro == false)) {
			if (running && counter == 4) {

				Log.i("POCKETTRAINER", "accel true, gyro false");
				LatLng currentLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				trackedPoint.add(currentLocation);
				Polyline route = myMap.addPolyline(new PolylineOptions()
						.geodesic(true));
				route.setPoints(trackedPoint);

				totalDistance += calculateDistance(lastLocation, location);
				lastLocation = location;
				counter = 0;
			}
		}

		if ((hasAccel == true) && (hasGyro == true)) {

			int XGyro = Math.abs(maxXGyro - minXGyro);
			int YGyro = Math.abs(maxYGyro - minYGyro);
			
			if (running && counter >= 4 && ((XGyro > 1) || (YGyro > 1))) {

				Log.i("POCKETTRAINER", "accel true, gyro true, hasMovements "
						+ hasMovements);
				LatLng currentLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				trackedPoint.add(currentLocation);
				Polyline route = myMap.addPolyline(new PolylineOptions()
						.geodesic(true));
				route.setPoints(trackedPoint);

				totalDistance += calculateDistance(lastLocation, location);
				lastLocation = location;
				counter = 0;
				minXGyro = 100000;
				maxXGyro = 0;
				minYGyro = 10000;
				maxYGyro = 0;
			}
		}

	}

	private float calculateDistance(Location last, Location now) {
		float[] c = new float[1];
		Location.distanceBetween(last.getLatitude(), last.getLongitude(),
				now.getLatitude(), now.getLongitude(), c);
		return c[0];
	}

	private void processSpeed() {

		float seconds = this.timeInMilliseconds / 1000;
		float distances = this.totalDistance;

		float s = distances / seconds;

		this.speed = s;
	}

	private void trainingSummary() {

		String userId = UserSession.getUserSession(getApplicationContext())
				.get(UserSession.LOGIN_ID);
		myTraining.setUSER_ID(Integer.parseInt(userId));
		myTraining.setDURATION(timeInMilliseconds);
		myTraining.setDISTANCE(totalDistance);
		processSpeed();
		myTraining.setSPEED(speed);
		myTraining.setBURNED_CALORIES(0f);
		myTraining.setSTEPS(stepsCount);
		myTraining.setMONSTER_DEFEATED("");

		try {
			TRAINING_DAL.insertTRAINING(getApplicationContext(), myTraining);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		myTraining = TRAINING_DAL.getTRAINING_Single(getApplicationContext(),
				myTraining.getID());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	private void setupNotification() {
		Intent intent = new Intent(this, TrainingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder = new Notification.Builder(getApplicationContext());
		builder.setContentTitle("Pocket Trainer");
		builder.setContentText("You have an ongoing training");
		builder.setContentIntent(pendingIntent);
		builder.setTicker("You have an ongoing training");
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher));
		builder.setOngoing(true);
		builder.setAutoCancel(true);
		builder.setPriority(0);
		notification = builder.build();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(01, notification);
	}

	private void destroyNotification() {
		notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(01);
	}

	private boolean isGPSEnabled() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private boolean isNetworkEnabled() {
		return locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	private void makeMarker(Location l, String title) {
		MarkerOptions marker = new MarkerOptions().position(
				new LatLng(l.getLatitude(), l.getLongitude())).title(title);
		myMap.addMarker(marker);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (hasAccel || hasGyro) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				double x = 0d, y = 0d, z = 0d;
				double mLastX = 0d, mLastY = 0d, mLastZ = 0d;
				double deltaX = 0d, deltaY = 0d, deltaZ = 0d;
				final double alpha = 0.8;

				x = event.values[0];
				y = event.values[1];
				z = event.values[2];

				double[] gravity = { 0, 0, 0 };

				gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
				gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
				gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

				x = event.values[0] - gravity[0];
				y = event.values[1] - gravity[1];
				z = event.values[2] - gravity[2];

				if (!mInitialized) {
					mLastX = x;
					mLastY = y;
					mLastZ = z;
					mInitialized = true;
				} else {
					deltaX = Math.abs(mLastX - x);
					deltaY = Math.abs(mLastY - y);
					deltaZ = Math.abs(mLastZ - z);

					if (deltaX < NOISE) {
						deltaX = 0f;
					}

					if (deltaY < NOISE) {
						deltaY = 0f;
					}

					if (deltaZ < NOISE) {
						deltaZ = 0f;
					}

					mLastX = x;
					mLastY = y;
					mLastZ = z;

					if ((deltaZ > deltaX) && (deltaZ > deltaY)) {
						// Z shake
						counter++;
						if(counter == 3){
							stepsCount+=1;
							Log.i("SENSOR", "hasSteps: " + hasSteps);
						}
						
					} else {
						// hasSteps = false;
//						Log.i("SENSOR", "hasSteps: " + hasSteps);
					}
				}
			} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				float x = event.values[0];
				float y = event.values[1];
				// float z = event.values[2];

				if ((int) x <= minXGyro) {
					minXGyro = (int) x;
				}
				if ((int) x >= maxXGyro) {
					maxXGyro = (int) x;
				}
				if ((int) y <= minYGyro) {
					minYGyro = (int) y;
				}
				if ((int) y >= maxYGyro) {
					maxYGyro = (int) y;
				}

			}
		}

	}

	public void turnGPSOn() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		this.sendBroadcast(intent);

		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.sendBroadcast(poke);

		}
	}

	// automatic turn off the gps
	public void turnGPSOff() {
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.sendBroadcast(poke);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		destroyNotification();
	}

	String onResState = "awal";
	int state = 0; // 0 saat baru pertama kali di akses 1 sudah pernah di akses
					// tapi keluar dan masuk lagi

	@Override
	protected void onResume() {
		super.onResume();
		if (state == 1) {
			isNetworkOK = isNetworkEnabled();
			isGPSOK = isGPSEnabled();
			if (isGPSOK && isNetworkOK) {
				dialog.dismiss();
				setLocationAndCamera();
			}
		}
		state = 1;
	}

}
