package com.pockettrainer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.pockettrainer.R;
import com.example.pockettrainer.R.id;
import com.example.pockettrainer.R.layout;
import com.example.pockettrainer.R.menu;
import com.pockettrainer.database.dal.MONSTER_DAL;
import com.pockettrainer.database.model.MONSTER;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.database.model.USER;
import com.pockettrainer.helper.MyService;
import com.pockettrainer.helper.UserSession;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Button myBtn;
	ImageButton hungerBtn, energyBtn, hygieneBtn, loveBtn;
	LinearLayout hungerIndMax, energyIndMax, hygieneIndMax, loveIndMax;
	LinearLayout hungerInd, energyInd, hygieneInd, loveInd;
	TextView levelTV;
	TextView petNameTV;
	LinearLayout experienceMax;
	LinearLayout experiences;
	ImageView petMood;
	MainDashboard dashboard;
	RelativeLayout gameView;
	int environment;
	PET myPet;
	USER myUser;
	Date nowDate;
	int nowMaxExp;
	Intent service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dashboard = new MainDashboard(this);
		// setContentView(dashboard);
		setContentView(R.layout.activity_main);

		myPet = new PET();
		myUser = new USER();
		
		Intent i = getIntent();

		if (i != null) {
			Bundle bund = i.getExtras();

			if (bund != null) {
				myPet = bund.getParcelable("PET");
				myUser = bund.getParcelable("USER");
				Log.i("POCKETTRAINER", "MainActivity" + myPet.getNAME());
			}
		} else {
			Log.i("POCKETTRAINER", "Gak ada bundle di intent");
		}

		setupView();
		setupEvent();
		
		service = new Intent(MainActivity.this, MyService.class);
		
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		setupData();
	}

	private void setupView() {
		gameView = (RelativeLayout) this.findViewById(R.id.gameView);
		gameView.addView(dashboard);

		myBtn = (Button) this.findViewById(R.id.main_button);
		hungerBtn = (ImageButton) this.findViewById(R.id.eat_button);
		energyBtn = (ImageButton) this.findViewById(R.id.sleep_button);
		hygieneBtn = (ImageButton) this.findViewById(R.id.bath_button);
		loveBtn = (ImageButton) this.findViewById(R.id.pet_button);

		levelTV = (TextView) this.findViewById(R.id.level);
		petNameTV = (TextView) this.findViewById(R.id.pet_name_text);
		experienceMax = (LinearLayout) this.findViewById(R.id.exp_max);
		experiences = (LinearLayout) this.findViewById(R.id.exp_bar);
		hungerIndMax = (LinearLayout) this.findViewById(R.id.eat_indicator_max);
		energyIndMax = (LinearLayout) this
				.findViewById(R.id.sleep_indicator_max);
		hygieneIndMax = (LinearLayout) this
				.findViewById(R.id.bath_indicator_max);
		loveIndMax = (LinearLayout) this.findViewById(R.id.pet_indicator_max);
		hungerInd = (LinearLayout) this.findViewById(R.id.eat_indicator);
		energyInd = (LinearLayout) this.findViewById(R.id.sleep_indicator);
		hygieneInd = (LinearLayout) this.findViewById(R.id.bath_indicator);
		loveInd = (LinearLayout) this.findViewById(R.id.pet_indicator);

	}

	private void setupEvent() {
		myBtn.setOnClickListener(this);
		hungerBtn.setOnClickListener(this);
		energyBtn.setOnClickListener(this);
		hygieneBtn.setOnClickListener(this);
		loveBtn.setOnClickListener(this);
	}

	private void setupData() {

		nowDate = new Date();

		// setup nama pet
		petNameTV.setText(this.myPet.getNAME());

		// setup level
		levelTV.setText(this.myPet.getLEVEL());

		setupExperience(this.myPet.getLEVEL());
		
		environment = Integer.parseInt(this.myPet.getENVIRONMENT());
		
		dashboard.setEnvironment(environment);
		// setup current experience
		setupBarExperience(myPet.getCURRENT_EXPERIENCE(), this.nowMaxExp);

		// setup mood

		// setup bar indikator
		setupBarIndikator(myPet.getHUNGER_INDICATOR(),
				myPet.getSLEEP_INDICATOR(), myPet.getHYGIENE_INDICATOR(),
				myPet.getRELATIONSHIP_INDICATOR());
	}

	private void setupExperience(String level) {
		double intLev = Double.parseDouble(level);
		this.nowMaxExp = (int) ((int) 100 * Math.pow(2, intLev));
	}

	private void setupBarExperience(int a, int max_exp) {
		int experienceWidthMax = experienceMax.getWidth();
		int experienceWidthNow = (experienceWidthMax * a) / max_exp;
		experiences.setLayoutParams(new LinearLayout.LayoutParams(
				experienceWidthNow, LayoutParams.MATCH_PARENT));
	}

	private void setupBarIndikator(int a, int b, int c, int d) {

		// setup hunger bar
		int hungerWidthMax = this.hungerIndMax.getWidth();
		int hungerWidthNow = (hungerWidthMax * a) / 100;
		hungerInd.setLayoutParams(new LinearLayout.LayoutParams(hungerWidthNow,
				LayoutParams.MATCH_PARENT));

		// setup energy bar
		int energyWidthMax = this.energyIndMax.getWidth();
		int energyWidthNow = (energyWidthMax * b) / 100;
		energyInd.setLayoutParams(new LinearLayout.LayoutParams(energyWidthNow,
				LayoutParams.MATCH_PARENT));

		// setup hygiene bar
		int hygieneWidthMax = this.hygieneIndMax.getWidth();
		int hygieneWidthNow = (hygieneWidthMax * c) / 100;
		hygieneInd.setLayoutParams(new LinearLayout.LayoutParams(
				hygieneWidthNow, LayoutParams.MATCH_PARENT));

		// setup love bar
		int loveWidthMax = this.loveIndMax.getWidth();
		int loveWidthNow = (loveWidthMax * d) / 100;
		loveInd.setLayoutParams(new LinearLayout.LayoutParams(loveWidthNow,
				LayoutParams.MATCH_PARENT));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.findItem(R.id.action_settings).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								UserSession.getIdUser(), Toast.LENGTH_SHORT)
								.show();
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
//		stopService(service);
		dashboard.resumeThread();
	}

	@Override
	public void onPause() {
		super.onPause();
		
//		startService(service);
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
			Toast.makeText(MainActivity.this, "Eat", 20).show();
			break;
		case R.id.sleep_button:
			Toast.makeText(MainActivity.this, "Sleep", 20).show();
			break;
		case R.id.bath_button:
			Toast.makeText(MainActivity.this, "Bath", 20).show();
			break;
		case R.id.pet_button:
			Toast.makeText(MainActivity.this, "Pet", 20).show();
			break;
		default:
			break;
		}
	}

}
