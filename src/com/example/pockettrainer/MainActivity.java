package com.example.pockettrainer;

import com.pockettrainer.MainDashboard;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

<<<<<<< HEAD
public class MainActivity extends Activity {
	
	MainDashboard dashboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dashboard = new MainDashboard(this);
		setContentView(dashboard);
=======
public class MainActivity extends Activity implements OnClickListener {
	
	Button myBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(new MainDashboard(this));
		setContentView(R.layout.activity_main);
		
		setupView();
	}
	
	private void setupView() {
		myBtn = (Button) this.findViewById(R.id.main_button);
		myBtn.setOnClickListener(this);
>>>>>>> 16ff77eac5fe676ab5f8181e945115c5d616b518
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				Toast.makeText(getApplicationContext(), "Setting", Toast.LENGTH_SHORT).show();
				return true;
//				Intent intent = new Intent(MainPageActivity.this.getApplicationContext(), SettingActivity.class);
//				startActivity(intent);
//				return true;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
<<<<<<< HEAD
	
	@Override
	public void onResume() {
		super.onResume();
		dashboard.resumeThread();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		dashboard.pauseThread();
	}
=======

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.main_button:
				Intent i = new Intent(getApplicationContext(), TrainingResultActivity.class);
				startActivity(i);
				break;
	
			default:
				break;
		}
	}

	

>>>>>>> 16ff77eac5fe676ab5f8181e945115c5d616b518
}
