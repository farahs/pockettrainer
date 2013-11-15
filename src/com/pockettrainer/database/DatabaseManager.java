package com.pockettrainer.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DatabaseManager {
	
	private static DatabaseHelper	databaseHelper;

	public static void releaseHelper()
	{

		if (databaseHelper != null)
		{
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}

	}

	/**
	 * You'll need this in your class to get the helper from the manager once
	 * per class.
	 */
	public static DatabaseHelper getHelper(Context context)
	{
		if (databaseHelper == null)
		{
			databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		}
		return databaseHelper;
	}
}
