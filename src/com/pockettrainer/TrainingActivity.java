package com.pockettrainer;

import java.util.ArrayList;
import java.util.List;

import com.example.pockettrainer.R;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.LineOverlay;
import com.mapquest.android.maps.MapActivity;
import com.mapquest.android.maps.MapController;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.MyLocationOverlay;

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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TrainingActivity extends Activity implements OnClickListener, LocationListener {

	TextView timerTV;
	TextView timerMsTV;
	Button startBtn;
	Button pauseBtn;
	Button resumeBtn;
	Button stopBtn;
	List<LatLng> trackedPoint;
	protected GoogleMap myMap;
	Location lastLocation;
	Location initialLocation;
	Location finalLocation;
	float speed;
	int point;
	float totalDistance;
	LocationManager locationManager;
	boolean running;
	NotificationDialog dialog;
	Button setting;
	float sumDistance;
	
	private long secs,mins,hrs;
	private String hours,minutes,seconds,milliseconds;
	private long startTime = 0l;
	private Handler customHandler = new Handler();
	private long updatedTime = 0L;
	private final int REFRESH_RATE = 50;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		trackedPoint = new ArrayList<LatLng>();
		
		setupView();
		setupEvent();

		try {
			initializeMap();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		setting.setText("Setting");
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
				getApplicationContext().startActivity(in);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.training_start:
			if(readyToRun()) {
				running = true;
				startBtn.setVisibility(View.GONE);
				pauseBtn.setVisibility(View.VISIBLE);
				resumeBtn.setVisibility(View.GONE);
				stopBtn.setVisibility(View.GONE);
				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			} else {
//				dialog.show();
			
				Intent i = new Intent(getApplicationContext(),
						TrainingResultActivity.class);

				startActivity(i);
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
			startActivity(i);
			this.finish();
			break;
		default:
			break;
		}
	}

	private Runnable updateTimerThread = new Runnable() {

		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeSwapBuff + timeInMilliseconds;

			secs = (long) (updatedTime/1000);
			mins = (long) ((updatedTime/1000)/60);
			hrs = (long) (((updatedTime/1000)/60)/60);
			
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
			
			milliseconds = String.valueOf((long)updatedTime);
			
			if(milliseconds.length() == 2) {
				milliseconds = "0" + milliseconds;
			}
			if (milliseconds.length() <= 1) {
				milliseconds = "00";
			}
			
			milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);
			
			timerTV.setText(hours + ":" + minutes + ":" + seconds);
			timerMsTV.setText("." + milliseconds);
			
			customHandler.postDelayed(this, REFRESH_RATE);
			
//			if (secs % 5 == 0) {
//				LatLng currentLocation = new LatLng(myMap.getMyLocation().getLatitude(), myMap.getMyLocation().getLongitude());
//				trackedPoint.add(currentLocation);
//				
//				Polyline route = myMap.addPolyline(new PolylineOptions().geodesic(true));
//				route.setPoints(trackedPoint);
//				
//				
//			}
		}

	};	
	
	private void initializeMap() {

		if (myMap == null) {

			myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			myMap.setMyLocationEnabled(true);
			
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			Location loc = locationManager.getLastKnownLocation(locationManager.getBestProvider(crit, false));
			
			CameraPosition camPos = new CameraPosition.Builder().target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(15.8f).build();
			CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
			
			myMap.moveCamera(camUpdate);
			
			initialLocation = loc;
			lastLocation = loc;
			finalLocation = loc;
		}	

	}

	private boolean readyToRun() {

		boolean isNetworkOK = isNetworkEnabled();
		boolean isGPSOK = isGPSEnabled();
		speed = 0f;
		point = 0;
		sumDistance = 0f;
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if (networkInfo != null && networkInfo.isConnected()) {
		
			if(isNetworkOK && isGPSOK) {
				
				if(isNetworkOK) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
				}
				if(isGPSOK) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
				}
				running = true;
				makeMarker(initialLocation, "START");
				return true;
			} else {
				return false;
			}
		}
		else {
			return false;
		}
		
	}
	
	private void stopTrain() {
		Criteria crit = new Criteria();
		finalLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(crit, false));
		totalDistance = calculateDistance(initialLocation, finalLocation);
		makeMarker(finalLocation, "FINISH");
	}

	@Override
	public void onLocationChanged(Location location) {
		
		Log.i("POCKETTRAINER", "locationchanged luar");
		if(running) {
			Log.i("POCKETTRAINER", "locationchanged dalam");
			Log.i("POCKETTRAINER", "" + calculateDistance(lastLocation, location));

				LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());	
				trackedPoint.add(currentLocation);
				Polyline route = myMap.addPolyline(new PolylineOptions().geodesic(true));
				route.setPoints(trackedPoint);
				point++;
				
				calculateSpeed(location);
		}
	}

	private float calculateDistance(Location last, Location now) {
//		float dist =  now.distanceTo(last);
		float [] c = new float[1];
		Location.distanceBetween(last.getLatitude(), last.getLongitude(), now.getLatitude(), now.getLongitude(), c);
		sumDistance += c[0];
		return c[0];
	}

	private float calculateSpeed(Location loc){
		
		float s = loc.getSpeed();
		
		float avg = ((speed * (point-1)) + s)/point;
		return avg;
	}
	

	private void trainingSummary() {
		Toast.makeText(getApplicationContext(),"speed: " + speed + " distance: " + totalDistance + " sumdistance: " + sumDistance, Toast.LENGTH_SHORT)
				.show();
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
		Notification.Builder builder = new Notification.Builder(
				getApplicationContext());
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
		Notification notification = builder.build();
		NotificationManager notificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManger.notify(01, notification);
	}
	
	private boolean isGPSEnabled(){
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	private boolean isNetworkEnabled(){
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	private void makeMarker(Location l, String title){
		MarkerOptions marker = new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongitude())).title(title);
		myMap.addMarker(marker);
	}
	
}
