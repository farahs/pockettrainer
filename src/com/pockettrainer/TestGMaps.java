package com.pockettrainer;

import com.example.pockettrainer.R;
import com.example.pockettrainer.R.layout;
import com.example.pockettrainer.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TestGMaps extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_gmaps);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_gmaps, menu);
		return true;
	}

}
