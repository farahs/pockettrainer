package com.pockettrainer;

import java.sql.SQLException;

import com.example.pockettrainer.R;
import com.pockettrainer.database.dal.PET_DAL;
import com.pockettrainer.database.dal.USER_DAL;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.database.model.USER;
import com.pockettrainer.helper.UserSession;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * 
 * @author Monster 2013
 *
 */
public class SplashScreenActivity extends Activity implements AnimationListener {

	private static long SLEEP_TIME = 5;

	private ImageView bigWhite;

	private Display mDisplay;

	private PET myPet;

	private USER myUser;

	Animation animBounce;

	private boolean loginStatus = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.activity_splash_screen);

		this.mDisplay = this.getWindowManager().getDefaultDisplay();

		setView();

		IntentLauncher launcher = new IntentLauncher();
		launcher.start();

	}

	private void setView() {
		bigWhite = (ImageView) findViewById(R.id.logo_big);
		animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.bounce);
		animBounce.setAnimationListener(this);
		animBounce.setRepeatCount(5);
		bigWhite.startAnimation(animBounce);
	}

	private class IntentLauncher extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(SLEEP_TIME * 300);
			} catch (Exception e) {
				Log.v("splashscreen", "splashscreen error");
			}

			loginStatus = UserSession.isLoggedIn(getApplicationContext());
			Intent intent = null;

			if (loginStatus) {
				myUser = new USER();
				myPet = new PET();

				try {
					myUser = USER_DAL.getUSER_All(getApplicationContext()).get(0);
					UserSession.setUserSession(getApplicationContext(), ""
							+ myUser.getID());
					if (myUser != null) {
						myPet = PET_DAL.getPET_SingleByUserId(
								getApplicationContext(), myUser.getID());
						UserSession.setPetSession(getApplicationContext(), ""
								+ myPet.getID());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				intent = new Intent(SplashScreenActivity.this,
						MainActivity.class);

				Bundle bund = new Bundle();
				bund.putParcelable("USER", myUser);
				bund.putParcelable("PET", myPet);
				intent.putExtras(bund);
			} else {
				intent = new Intent(SplashScreenActivity.this,
						WelcomeActivity.class);
			}

			SplashScreenActivity.this.startActivity(intent);
			SplashScreenActivity.this.finish();
		}

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}