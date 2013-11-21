package com.pockettrainer.helper;

import java.sql.SQLException;
import java.util.Timer;

import com.mapquest.android.maps.GeoPoint;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.model.PET;

import android.app.Service;
import android.content.Intent;
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

		

		return super.onStartCommand(intent, flags, startId);

	}

	private Runnable timer = new Runnable() {

		public void run() {

			time = SystemClock.uptimeMillis();

			if (time == 3600000L){
				HUNGER -= 10;
				SLEEP -= 10;
				HYGIENE -= 10;
				RELATIONSHIP -= 10;
				
				myPet.setHUNGER_INDICATOR(HUNGER);
				myPet.setSLEEP_INDICATOR(SLEEP);
				myPet.setHYGIENE_INDICATOR(HYGIENE);
				myPet.setRELATIONSHIP_INDICATOR(RELATIONSHIP);
				
				time = 0L;
				try {
					PET_DAL.updatePET(getApplicationContext(), myPet);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
