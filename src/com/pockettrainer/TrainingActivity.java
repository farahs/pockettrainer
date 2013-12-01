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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TrainingActivity extends Activity implements OnClickListener,
		LocationListener, SensorEventListener {

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
	float speed;
	int point;
	float totalDistance;
	float initFinalDistance;
	float distanceTo;
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
	private final float NOISE = (float) 2.0;
	int stepsCount;

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
			running = false;
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
			}
		});
	}

	private void setupSensor() {
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_settings).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						return true;
					}
				});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.training_start:
			if (readyToRun()) {
				running = true;
				totalDistance = 0f;
				startSensor();
				startBtn.setVisibility(View.GONE);
				pauseBtn.setVisibility(View.VISIBLE);
				resumeBtn.setVisibility(View.GONE);
				stopBtn.setVisibility(View.GONE);
				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			} else {
				dialog.show();

				// Intent i = new Intent(getApplicationContext(),
				// TrainingResultActivity.class);
				//
				// startActivity(i);
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
			startBtn.setVisibility(View.GONE);
			pauseBtn.setVisibility(View.VISIBLE);
			resumeBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.GONE);
			startTime = SystemClock.uptimeMillis();
			running = true;
			customHandler.postDelayed(updateTimerThread, 0);
			break;
		case R.id.training_stop:
			running = false;
			stopTrain();
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

			if (milliseconds.length() >= 2) {
				milliseconds = milliseconds.substring(
						milliseconds.length() - 3, milliseconds.length() - 2);
			}
			timerTV.setText(hours + ":" + minutes + ":" + seconds);
			timerMsTV.setText("." + milliseconds);

			customHandler.postDelayed(this, REFRESH_RATE);

			// if (secs % 5 == 0) {
			// LatLng currentLocation = new
			// LatLng(myMap.getMyLocation().getLatitude(),
			// myMap.getMyLocation().getLongitude());
			// trackedPoint.add(currentLocation);
			//
			// Polyline route = myMap.addPolyline(new
			// PolylineOptions().geodesic(true));
			// route.setPoints(trackedPoint);
			//
			//
			// }
		}

	};

	private void initializeMap() {

		if (myMap == null) {

			myMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			myMap.setMyLocationEnabled(true);

			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			Location loc = locationManager.getLastKnownLocation(locationManager
					.getBestProvider(crit, false));

			CameraPosition camPos = new CameraPosition.Builder()
					.target(new LatLng(loc.getLatitude(), loc.getLongitude()))
					.zoom(15.8f).build();
			CameraUpdate camUpdate = CameraUpdateFactory
					.newCameraPosition(camPos);

			myMap.moveCamera(camUpdate);
		}

	}

	private boolean readyToRun() {

		boolean isNetworkOK = isNetworkEnabled();
		boolean isGPSOK = isGPSEnabled();
		speed = 0f;
		point = 0;

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
					totalDistance = 0f;
					running = true;
					stepsCount = 0;
					makeMarker(initialLocation, "START");
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	private void stopTrain() {
		Criteria crit = new Criteria();
		finalLocation = locationManager.getLastKnownLocation(locationManager
				.getBestProvider(crit, false));
		if (initialLocation != null && finalLocation != null) {
			initFinalDistance = calculateDistance(initialLocation,
					finalLocation);
			makeMarker(finalLocation, "FINISH");
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.i("POCKETTRAINER", "locationchanged luar");
		if (running) {
			Log.i("POCKETTRAINER", "locationchanged dalam");
			Log.i("POCKETTRAINER",
					"" + calculateDistance(lastLocation, location));
			LatLng currentLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			trackedPoint.add(currentLocation);
			Polyline route = myMap.addPolyline(new PolylineOptions()
					.geodesic(true));
			route.setPoints(trackedPoint);
			point++;

			totalDistance += calculateDistance(lastLocation, location);
			lastLocation = location;

			if (location.hasSpeed()) {
				calculateSpeed(location);
			}
		}

	}

	private float calculateDistance(Location last, Location now) {
		distanceTo += now.distanceTo(last);
		float[] c = new float[1];
		Location.distanceBetween(last.getLatitude(), last.getLongitude(),
				now.getLatitude(), now.getLongitude(), c);
		return c[0];
	}

	private float calculateSpeed(Location loc) {

		float s = loc.getSpeed();

		float avg = ((speed * (point - 1)) + s) / point;
		return avg;
	}

	private void trainingSummary() {
		Toast.makeText(
				getApplicationContext(),
				"speed: " + speed + " distance: " + totalDistance
						+ " distanceto: " + distanceTo + " initFinalDistance: "
						+ initFinalDistance, Toast.LENGTH_SHORT).show();

		String userId = UserSession.getUserSession(getApplicationContext())
				.get(UserSession.LOGIN_ID);
		myTraining.setUSER_ID(Integer.parseInt(userId));
		myTraining.setDURATION(timeInMilliseconds);
		myTraining.setDISTANCE(5000);
		// myTraining.setDISTANCE(totalDistance);
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
				stepsCount = stepsCount + 1;
			}
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		destroyNotification();
		Intent t = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(t);
		this.finish();
	}

}
