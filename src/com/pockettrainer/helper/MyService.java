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
	Handler customHandler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		idPet = Integer.parseInt(UserSession.getIdPet());
		myPet = PET_DAL.getPET_Single(getApplicationContext(), idPet);
		HUNGER = myPet.getHUNGER_INDICATOR();
		SLEEP = myPet.getSLEEP_INDICATOR();
		HYGIENE = myPet.getHYGIENE_INDICATOR();
		RELATIONSHIP = myPet.getRELATIONSHIP_INDICATOR();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

//		Thread t = new Thread(timer);
//		if(! t.isAlive()){
//			t.start();
//		}
		customHandler.postDelayed(timer, 0);
		Log.i("POCKETTRAINER", "run");
		return START_STICKY;

	}

	private Runnable timer = new Runnable() {

		public void run() {

			int count = 1;
			while (true){
				count++;
				try{
					Thread.sleep(1000);
					
					if(HUNGER < 0){
						HUNGER = 0;
						SLEEP = 0;
						HYGIENE = 0;
						RELATIONSHIP = 0;
					} else {
						HUNGER -= 1;
						SLEEP -= 1;
						HYGIENE -= 1;
						RELATIONSHIP -= 1;
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
					
				} catch(Exception ex) {
					
				}
			}
		}

	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		customHandler.removeCallbacks(timer);
	}
}
