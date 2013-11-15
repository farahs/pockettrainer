package com.pockettrainer;

import java.util.ArrayList;
import java.util.List;

import com.example.pockettrainer.R;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.LineOverlay;
import com.mapquest.android.maps.MapActivity;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.MyLocationOverlay;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TrainingActivity extends MapActivity implements OnClickListener {

	TextView timerTV;
	Button startBtn;
	Button pauseBtn;
	Button resumeBtn;
	Button stopBtn;
	List<GeoPoint> trackedPoint;
	protected MapView myMap;
	protected MyLocationOverlay myLocationOverlay;

	private long startTime = 0L;

	private Handler customHandler = new Handler();

	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);
		trackedPoint = new ArrayList<GeoPoint>();

		setupView();
		setupEvent();
		setupMapView();
		setupMyLocation();
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
			if(networkInfo != null && networkInfo.isConnected()){
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
			customHandler.removeCallbacks(updateTimerThread);
			break;
		case R.id.training_resume:
			startBtn.setVisibility(View.GONE);
			pauseBtn.setVisibility(View.VISIBLE);
			resumeBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.GONE);
			startTime = SystemClock.uptimeMillis();
			customHandler.postDelayed(updateTimerThread, 0);
			break;
		case R.id.training_stop:
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

			if (secs % 10 == 0) {
				GeoPoint currentLocation = myLocationOverlay.getMyLocation();
				trackedPoint.add(currentLocation);
				// Toast.makeText(TrainingActivity.this,
				// currentLocation.toString(), Toast.LENGTH_SHORT).show();
				drawTrackLine();
			}
		}

	};

	/**
	 * This will set up a MapQuest map with a MyLocation Overlay
	 */
	private void setupMapView() {
		this.myMap = (MapView) findViewById(R.id.map);

		// enable the zoom controls
		myMap.setBuiltInZoomControls(true);
	}

	protected void setupMyLocation() {
		this.myLocationOverlay = new MyLocationOverlay(this, myMap);

		myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				GeoPoint currentLocation = myLocationOverlay.getMyLocation();
				myMap.getController().animateTo(currentLocation);
				myMap.getController().setZoom(18);
				myMap.getOverlays().add(myLocationOverlay);
				myLocationOverlay.setFollowing(true);
			}
		});
	}

	private void drawTrackLine() {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
		paint.setAlpha(100);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(5);
		LineOverlay line = new LineOverlay(paint);
		line.setData(trackedPoint);

		this.myMap.getOverlays().add(line);
		// this.map.getController().z
		this.myMap.invalidate();
	}

	@Override
	protected void onResume() {
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
	}

	@Override
	public boolean isRouteDisplayed() {
		return false;
	}

}
