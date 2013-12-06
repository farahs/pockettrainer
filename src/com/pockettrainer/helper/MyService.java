package com.pockettrainer.helper;

import java.sql.SQLException;

import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.model.PET;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

	PET myPet;
	int idPet;
	int HUNGER;
	int SLEEP;
	int HYGIENE;
	int RELATIONSHIP;
	int MINUS_SLEEP = 3;
	int MINUS_RELATIONSHIP = 5;
	int MINUS_HYGIENE = 4;
	int MINUS_HUNGER = 6;
	boolean b;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		idPet = Integer.parseInt(UserSession.getUserSession(
				getApplicationContext()).get(UserSession.LOGIN_ID));
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

		b = UserSession.getPetSleepSession(getApplicationContext())
				.get(UserSession.SLEEP_FLAG);
		
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
//				Thread.sleep(1000);
				Thread.sleep(60 * 60 * 1000);
					
				// HUNGER
				if (HUNGER == 0) {
					HUNGER = 0;
				} else if (HUNGER - MINUS_HUNGER <= 0) {
					HUNGER = 0;
				} else {
					HUNGER -= MINUS_HUNGER;
				}

				// SLEEP
				if (SLEEP == 0) {
					SLEEP = 0;
				} else if (SLEEP - MINUS_SLEEP <= 0) {
					SLEEP = 0;
				} else {
					SLEEP -= MINUS_SLEEP;
				}

				// HYGIENE
				if (HYGIENE == 0) {
					HYGIENE = 0;
				} else if (HYGIENE - MINUS_HYGIENE <= 0) {
					HYGIENE = 0;
				} else {
					HYGIENE -= MINUS_HYGIENE;
				}

				// RELATIONSHIP
				if (RELATIONSHIP == 0) {
					RELATIONSHIP = 0;
				} else if (RELATIONSHIP - MINUS_RELATIONSHIP <= 0) {
					RELATIONSHIP = 0;
				} else {
					RELATIONSHIP -= MINUS_RELATIONSHIP;
				}
				
				Log.i("POCKETTRAINER", " " + HUNGER + " " + SLEEP + " "
						+ HYGIENE + " " + RELATIONSHIP);

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
	}
}
