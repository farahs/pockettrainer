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
import com.pockettrainer.database.dal.PET_DAL;
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

	static MainActivity mainActivity;
	Button myBtn;
	static RelativeLayout hungerBtn, energyBtn, hygieneBtn, loveBtn;
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
	static boolean hungBtn = false;
	static boolean enBtn = false;
	static boolean hyBtn = false;
	PET myPet;
	USER myUser;
	Date nowDate;
	int nowMaxExp;
	Intent service;
	int hunger, energy, hygiene, love;
	int sum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dashboard = new MainDashboard(this);
		// setContentView(dashboard);
		setContentView(R.layout.activity_main);

		mainActivity = this;
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

		boolean b = UserSession.getPetSleepSession(getApplicationContext())
				.get(UserSession.SLEEP_FLAG);

		setupView();
		setupEvent();
		
		if (b) {
			dashboard.goSleep();
			MainActivity.setActEnergy(true);
		}

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
		hungerBtn = (RelativeLayout) this.findViewById(R.id.eat_button);
		energyBtn = (RelativeLayout) this.findViewById(R.id.sleep_button);
		hygieneBtn = (RelativeLayout) this.findViewById(R.id.bath_button);
		loveBtn = (RelativeLayout) this.findViewById(R.id.pet_button);

		levelTV = (TextView) this.findViewById(R.id.level);
		petNameTV = (TextView) this.findViewById(R.id.pet_name_text);
		petMood = (ImageView) this.findViewById(R.id.pet_mood);
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

		// setup bar indikator
		hunger = myPet.getHUNGER_INDICATOR();
		energy = myPet.getSLEEP_INDICATOR();
		hygiene = myPet.getHYGIENE_INDICATOR();
		love = myPet.getRELATIONSHIP_INDICATOR();

		setupBarIndikator(hunger, energy, hygiene, love);

		// setup mood
		setupMood(hunger, energy, hygiene, love);

	}

	private void setupMood(int h, int e, int hy, int l) {
		this.sum = h + e + hy + l;
		if (this.sum <= 100) {
			petMood.setImageResource(R.drawable.mood_verybad);
			myPet.setMOOD("1");
		} else if (this.sum <= 200) {
			petMood.setImageResource(R.drawable.mood_bad);
			myPet.setMOOD("2");
		} else if (this.sum <= 300) {
			petMood.setImageResource(R.drawable.mood_good);
			myPet.setMOOD("3");
		} else if (this.sum <= 400) {
			petMood.setImageResource(R.drawable.mood_verygood);
			myPet.setMOOD("4");
		}

	}

	public void setupExperience(String level) {
		double intLev = Double.parseDouble(level);
		this.nowMaxExp = (int) ((int) 100 * Math.pow(2, intLev));
	}

	public void setupBarExperience(int a, int max_exp) {
		int experienceWidthMax = experienceMax.getWidth();
		int experienceWidthNow = (experienceWidthMax * a) / max_exp;
		experiences.setLayoutParams(new LinearLayout.LayoutParams(
				experienceWidthNow, LayoutParams.MATCH_PARENT));
	}

	public void setupBarIndikator(int h, int e, int hy, int l) {

		// setup hunger bar
		int hungerWidthMax = this.hungerIndMax.getWidth();
		int hungerWidthNow = (hungerWidthMax * h) / 100;
		hungerInd.setLayoutParams(new LinearLayout.LayoutParams(hungerWidthNow,
				LayoutParams.MATCH_PARENT));

		// setup energy bar
		int energyWidthMax = this.energyIndMax.getWidth();
		int energyWidthNow = (energyWidthMax * e) / 100;
		energyInd.setLayoutParams(new LinearLayout.LayoutParams(energyWidthNow,
				LayoutParams.MATCH_PARENT));

		// setup hygiene bar
		int hygieneWidthMax = this.hygieneIndMax.getWidth();
		int hygieneWidthNow = (hygieneWidthMax * hy) / 100;
		hygieneInd.setLayoutParams(new LinearLayout.LayoutParams(
				hygieneWidthNow, LayoutParams.MATCH_PARENT));

		// setup love bar
		int loveWidthMax = this.loveIndMax.getWidth();
		int loveWidthNow = (loveWidthMax * l) / 100;
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

						Toast.makeText(
								getApplicationContext(),
								UserSession.getUserSession(
										getApplicationContext()).get(
										UserSession.LOGIN_ID),
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
		service = new Intent(MainActivity.this, MyService.class);
		stopService(service);
	}

	@Override
	public void onPause() {
		super.onPause();

		dashboard.pauseThread();
		service = new Intent(MainActivity.this, MyService.class);
		startService(service);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_button:
			Intent i = new Intent(getApplicationContext(),
					TrainingActivity.class);
			startActivity(i);
			break;
		case R.id.eat_button:
			if (!dashboard.getSleep()) {
				dashboard.goEat();
				setActHunger(hungBtn);
				// hunger = hunger + 5;
				// myPet.setHUNGER_INDICATOR(hunger);
				// try {
				// PET_DAL.updatePET(getApplicationContext(), myPet);
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
				// setupBarIndikator(hunger, energy, hygiene, love);
				// setupMood(hunger, energy, hygiene, love);
			}

			break;
		case R.id.sleep_button:
			dashboard.goSleep();
			setActEnergy(enBtn);
			// energy = energy + 5;
			// myPet.setSLEEP_INDICATOR(energy);
			// try {
			// PET_DAL.updatePET(getApplicationContext(), myPet);
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			// setupBarIndikator(hunger, energy, hygiene, love);
			// setupMood(hunger, energy, hygiene, love);
			break;
		case R.id.bath_button:
			if (!dashboard.getSleep()) {
				dashboard.setIsBath();
				setActHygiene(hyBtn);
				// hygiene = hygiene + 5;
				// myPet.setHYGIENE_INDICATOR(hygiene);
				// try {
				// PET_DAL.updatePET(getApplicationContext(), myPet);
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
				// setupBarIndikator(hunger, energy, hygiene, love);
				// setupMood(hunger, energy, hygiene, love);
			}

			break;
		case R.id.pet_button:
			// love = love + 5;
			// myPet.setRELATIONSHIP_INDICATOR(love);
			// try {
			// PET_DAL.updatePET(getApplicationContext(), myPet);
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			// setupBarIndikator(hunger, energy, hygiene, love);
			// setupMood(hunger, energy, hygiene, love);
			break;
		default:
			break;
		}
	}
	
	public static void setActHunger(boolean a) {
		if(a == true) {
			hungBtn = !a;
			hungerBtn.setBackgroundResource(R.drawable.top_hunger_color);
		} else {
			hungBtn = !a;
			hungerBtn.setBackgroundResource(R.drawable.top_default_color);
		}
	}
	
	public static void setActHygiene(boolean a) {
		if(a == true) {
			hyBtn = !a;
			hygieneBtn.setBackgroundResource(R.drawable.top_hygiene_color);
		} else {
			hyBtn = !a;
			hygieneBtn.setBackgroundResource(R.drawable.top_default_color);
		}
	}
	
	public static void setActEnergy(boolean a) {
		if(a == true) {
			enBtn = !a;
			energyBtn.setBackgroundResource(R.drawable.top_sleep_color);
		} else {
			enBtn = !a;
			energyBtn.setBackgroundResource(R.drawable.top_default_color);
		}
	}

	public static MainActivity getInstance() {
		return mainActivity;
	}

	public int getHunger() {
		return hunger;
	}

	public void setHunger(int tambahHunger) {
		if (hunger == 100) {
			hunger = 100;
		} else if (hunger + tambahHunger >= 100) {
			hunger = 100;
		} else {
			this.hunger += tambahHunger;
		}

		myPet.setHUNGER_INDICATOR(hunger);
		try {
			PET_DAL.updatePET(getApplicationContext(), myPet);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setupBarIndikator(hunger, energy, hygiene, love);
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int tambahEnergy) {
		if (energy == 100) {
			energy = 100;
		} else if (energy + tambahEnergy >= 100) {
			energy = 100;
		} else {
			this.energy += tambahEnergy;
		}

		myPet.setSLEEP_INDICATOR(energy);
		try {
			PET_DAL.updatePET(getApplicationContext(), myPet);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setupBarIndikator(hunger, energy, hygiene, love);
	}

	public int getHygiene() {
		return hygiene;
	}

	public void setHygiene(int tambahHygiene) {
		if (hygiene == 100) {
			hygiene = 100;
		} else if (hygiene + tambahHygiene >= 100) {
			hygiene = 100;
		} else {
			this.hygiene += tambahHygiene;
		}

		myPet.setHYGIENE_INDICATOR(hygiene);
		try {
			PET_DAL.updatePET(getApplicationContext(), myPet);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setupBarIndikator(hunger, energy, hygiene, love);
	}

	public int getLove() {
		return love;
	}

	public void setLove(int tambahLove) {
		if (love == 100) {
			love = 100;
		} else if (love + tambahLove >= 100) {
			love = 100;
		} else {
			this.love += tambahLove;
		}

		myPet.setRELATIONSHIP_INDICATOR(love);
		try {
			PET_DAL.updatePET(getApplicationContext(), myPet);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setupBarIndikator(hunger, energy, hygiene, love);
	}

}
