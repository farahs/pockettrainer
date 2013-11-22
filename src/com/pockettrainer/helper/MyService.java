package com.pockettrainer.helper;

import java.sql.SQLException;
import java.util.Timer;

import com.mapquest.android.maps.GeoPoint;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.model.PET;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class MyService extends Service {

	PET myPet;
	int idPet;
	int HUNGER;
	int SLEEP;
	int HYGIENE;
	int RELATIONSHIP;
	long time = 0L;

	// Handler customHandler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		idPet = Integer.parseInt(UserSession.getUserSession(getApplicationContext()).get(UserSession.LOGIN_ID));
		myPet = PET_DAL.getPET_Single(getApplicationContext(), idPet);
		HUNGER = myPet.getHUNGER_INDICATOR();
		SLEEP = myPet.getSLEEP_INDICATOR();
		HYGIENE = myPet.getHYGIENE_INDICATOR();
		RELATIONSHIP = myPet.getRELATIONSHIP_INDICATOR();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// customHandler.postDelayed(timer, 0);
		ThreadDemo td = new ThreadDemo();
		td.start();

		Log.i("POCKETTRAINER", "run");

		return super.onStartCommand(intent, flags, startId);

	}

	private class ThreadDemo extends Thread {
		@Override
		public void run() {
			super.run();
			
			try {
				Thread.sleep(10000);

				if (HUNGER < 0) {
					HUNGER = 0;
					SLEEP = 0;
					HYGIENE = 0;
					RELATIONSHIP = 0;
				} else {
					HUNGER -= 5;
					SLEEP -= 5;
					HYGIENE -= 5;
					RELATIONSHIP -= 5;
					Log.i("POCKETTRAINER", " " + HUNGER + " " + SLEEP + " " + HYGIENE + " " + RELATIONSHIP);
				}

				myPet.setHUNGER_INDICATOR(HUNGER);
				myPet.setSLEEP_INDICATOR(SLEEP);
				myPet.setHYGIENE_INDICATOR(HYGIENE);
				myPet.setRELATIONSHIP_INDICATOR(RELATIONSHIP);

				try {
					PET_DAL.updatePET(getApplicationContext(), myPet);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} catch (Exception ex) {

			}
		}
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		customHandler.removeCallbacks(timer);
	}
}
