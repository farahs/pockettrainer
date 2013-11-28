package com.pockettrainer;

import java.util.ArrayList;
import java.util.List;

import com.example.pockettrainer.R;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.LineOverlay;
import com.mapquest.android.maps.MapActivity;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.MyLocationOverlay;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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

public class TrainingActivity extends MapActivity implements OnClickListener, LocationListener {

	TextView timerTV;
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
//	LocationListener myLocationListener;
//	protected MyLocationOverlay myLocation;
	
	
	private long startTime = 0L;

	private Handler customHandler = new Handler();

	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		trackedPoint = new ArrayList<LatLng>();

		setupView();
		setupEvent();

		try {
			initializeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupView() {
		timerTV = (TextView) this.findViewById(R.id.train_timer);
		startBtn = (Button) this.findViewById(R.id.training_start);
		pauseBtn = (Button) this.findViewById(R.id.training_pause);
		resumeBtn = (Button) this.findViewById(R.id.training_resume);
		stopBtn = (Button) this.findViewById(R.id.training_stop);

		startBtn.setVisibility(View.VISIBLE);
		pauseBtn.setVisibility(View.GONE);
		resumeBtn.setVisibility(View.GONE);
		stopBtn.setVisibility(View.GONE);
	}

	private void setupEvent() {
		startBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		resumeBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
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
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				running = true;
				startBtn.setVisibility(View.GONE);
				pauseBtn.setVisibility(View.VISIBLE);
				resumeBtn.setVisibility(View.GONE);
				stopBtn.setVisibility(View.GONE);
				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			} else {
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
			break;
		default:
			break;
		}
	}

	private Runnable updateTimerThread = new Runnable() {

		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeSwapBuff + timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			int milliseconds = (int) (updatedTime % 1000);
			timerTV.setText("" + mins + ":" + String.format("%02d", secs) + ":"
					+ String.format("%02d", milliseconds));
			customHandler.postDelayed(this, 0);
			
			
//			onLocationChanged(myMap.getMyLocation());
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
	
	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	private void initializeMap() {

		if (myMap == null) {

			myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
			
			myMap.setMyLocationEnabled(true);
			
			Location now = locationManager.getLastKnownLocation("gps");
			
			double latitude = now.getLatitude();
			double longitude = now.getLongitude();
			
			initialLocation = now;
			lastLocation = now;
			finalLocation = now;
			Log.i("POCKETTRAINER", "" + latitude + " " + longitude);
			speed = 0f;
			point = 0;
			running = false;
//			
//			MarkerOptions marker = new MarkerOptions().position(
//					new LatLng(latitude, longitude)).title("My Position").snippet("Rumah");
//			myMap.addMarker(marker);

			if (myMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	private void stopTrain() {
		finalLocation = myMap.getMyLocation();
		totalDistance = calculateDistance(initialLocation, finalLocation);
	}


	@Override
	public void onLocationChanged(Location location) {
		
		Log.i("POCKETTRAINER", "locationchanged luar");
		if(running) {
			Log.i("POCKETTRAINER", "locationchanged dalam");
	//		kalo jaraknya lebih dari 1 meter, baru gambar
			Log.i("POCKETTRAINER", "" + calculateDistance(lastLocation, location));
			if(calculateDistance(lastLocation, location) > 0.05) {

				LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());	
				trackedPoint.add(currentLocation);
				Polyline route = myMap.addPolyline(new PolylineOptions().geodesic(true));
				route.setPoints(trackedPoint);
//				new LatLng(myMap.getMyLocation().getLatitude(), myMap.getMyLocation().getLongitude());
				point++;
				
				calculateSpeed(location);
				lastLocation = location;
//				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, TrainingActivity.this);
			}	
		}
	}

	private float calculateDistance(Location last, Location now) {
		float dist =  now.distanceTo(last);
		float [] c = new float[1];
		Location.distanceBetween(last.getLatitude(), last.getLongitude(), now.getLatitude(), now.getLongitude(), c);
//		Location.dis
		return c[0];
	}

	private float calculateSpeed(Location loc){
		
		float s = loc.getSpeed();
		
		float avg = ((speed * (point-1)) + s)/point;
		return avg;
	}
	

	private void trainingSummary() {
		Toast.makeText(getApplicationContext(),"speed: " + speed + " distance: " + totalDistance, Toast.LENGTH_SHORT)
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
	
}
