package com.pockettrainer.helper;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class UserSession {

	private static String idUser = "0";
	private static String idPet = "0";
	public static String IS_LOGGED_IN = "ISLOGGEDIN";
	public static String LOGIN_ID = "LOGIN_ID";
	public static String HAVE_PET = "HAVE_PET";
	public static String PET_ID = "PET_ID";
//	public static String PET_HUNGER = "PET_HUNGER";
//	public static String PET_SLEEP = "PET_SLEEP";
//	public static String PET_HYGIENE = "PET_HYGIENE";
//	public static String PET_RELATIONSHIP = "PET_RELATIONSHIP";
	public static String SLEEP_FLAG = "SLEEP_FLAG";
	public static int HUNGER = 0;
	public static int SLEEP = 0;
	public static int HYGIENE = 0;
	public static int RELATIONSHIP = 0;
	public static boolean isSleep = false;
	
	public static SharedPreferences getSharedPreference(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static String getIdUser() {
		return idUser;
	}

	public static void setIdUser(String idUser) {
		UserSession.idUser = idUser;
	}

	public static String getIdPet() {
		return idPet;
	}

	public static void setIdPet(String idPet) {
		UserSession.idPet = idPet;
	}

	public static boolean isSleep() {
		return isSleep;
	}

	public static void setSleep(boolean isSleep) {
		UserSession.isSleep = isSleep;
	}

	public static HashMap<String, String> getUserSession(Context context)
	{
		SharedPreferences sp = getSharedPreference(context);
		
		HashMap<String, String> user = new HashMap<String, String>();
		user.put(LOGIN_ID, sp.getString(LOGIN_ID, null));
		
		return user;
	}
	
	public static void setUserSession(Context context, String userId)
	{
		SharedPreferences sp = getSharedPreference(context);

		setIdUser(idUser);
		
		Editor editor = sp.edit();
		editor.putBoolean(IS_LOGGED_IN, true);
		editor.putString(LOGIN_ID, userId);
		
		editor.commit();
	}

	public static void removeUserSession(Context context) {
		SharedPreferences sp = getSharedPreference(context);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	public static boolean isLoggedIn(Context context) {
		SharedPreferences sp = getSharedPreference(context);
		return sp.getBoolean(IS_LOGGED_IN, false);
	}
	
	public static HashMap<String, String> getPetSession(Context context)
	{
		SharedPreferences sp = getSharedPreference(context);
		
		HashMap<String, String> pet = new HashMap<String, String>();
		pet.put(PET_ID, sp.getString(PET_ID, idPet));
		
		return pet;
	}
	
	public static void setPetSession(Context context, String petId)
	{
		SharedPreferences sp = getSharedPreference(context);

		setIdPet(petId);
		
		Editor editor = sp.edit();
		editor.putBoolean(HAVE_PET, true);
		editor.putString(PET_ID, petId);
		
		editor.commit();
	}

	public static HashMap<String, Boolean> getPetSleepSession(Context context)
	{
		SharedPreferences sp = getSharedPreference(context);
		
		HashMap<String, Boolean> petSleep = new HashMap<String, Boolean>();
		petSleep.put(SLEEP_FLAG, sp.getBoolean(SLEEP_FLAG, false));
		
		return petSleep;
	}
	
	public static void setPetSleepSession(Context context, boolean isS)
	{
		SharedPreferences sp = getSharedPreference(context);

		setSleep(isS);
		
		Editor editor = sp.edit();
		editor.putBoolean(SLEEP_FLAG, true);
		
		editor.commit();
	}
	
	
}
