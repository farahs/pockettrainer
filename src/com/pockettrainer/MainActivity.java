package com.pockettrainer;

import com.example.pockettrainer.R;
import com.example.pockettrainer.R.id;
import com.example.pockettrainer.R.layout;
import com.example.pockettrainer.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Button myBtn;
	ImageButton hungerBtn, energyBtn, hygieneBtn, loveBtn;
	MainDashboard dashboard;
	RelativeLayout gameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dashboard = new MainDashboard(this);
		// setContentView(dashboard);
		setContentView(R.layout.activity_main);

		setupView();
	}

	private void setupView() {
		gameView = (RelativeLayout) this.findViewById(R.id.gameView);
		gameView.addView(dashboard);
		myBtn = (Button) this.findViewById(R.id.main_button);
		myBtn.setOnClickListener(this);
		hungerBtn = (ImageButton) this.findViewById(R.id.eat_button);
		hungerBtn.setOnClickListener(this);
		energyBtn = (ImageButton) this.findViewById(R.id.sleep_button);
		energyBtn.setOnClickListener(this);
		hygieneBtn = (ImageButton) this.findViewById(R.id.bath_button);
		hygieneBtn.setOnClickListener(this);
		loveBtn = (ImageButton) this.findViewById(R.id.pet_button);
		loveBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.findItem(R.id.action_settings).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(), "Setting",
								Toast.LENGTH_SHORT).show();
						return true;
						// Intent intent = new
						// Intent(MainPageActivity.this.getApplicationContext(),
						// SettingActivity.class);
						// startActivity(intent);
						// return true;
					}
				});

		return super.onCreateOptionsMenu(menu);
	}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.main_button:
			Intent i = new Intent(getApplicationContext(),
					TrainingActivity.class);
			startActivity(i);
			break;
		case R.id.eat_button:
			Toast.makeText(MainActivity.this, "Eat", Toast.LENGTH_SHORT).show();
			break;
		case R.id.sleep_button:
			Toast.makeText(MainActivity.this, "Sleep", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bath_button:
			Toast.makeText(MainActivity.this, "Bath", Toast.LENGTH_SHORT).show();
			break;
		case R.id.pet_button:
			Toast.makeText(MainActivity.this, "Pet", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

}
