package com.pockettrainer;

import com.example.pockettrainer.R;

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

public class SplashScreenActivity extends Activity implements AnimationListener
{

	private static long			SLEEP_TIME	= 5;

	private ImageView			bigWhite;

	private Display				mDisplay;
	
	Animation animBounce;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

	private class IntentLauncher extends Thread
	{

		@Override
		public void run()
		{
			try
			{
				Thread.sleep(SLEEP_TIME * 300);
			}
			catch (Exception e)
			{
				Log.v("splashscreen", "splashscreen error");
			}

			Intent intent = null;
			intent = new Intent(SplashScreenActivity.this, MainActivity.class);
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