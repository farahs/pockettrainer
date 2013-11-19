package com.pockettrainer;

import com.example.pockettrainer.R;
import com.example.pockettrainer.R.layout;
import com.example.pockettrainer.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomeActivity extends Activity implements OnClickListener, AnimationListener {

	Button nextBtn;
	ImageView egg;
	
	Animation animBounce;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);

		setView();
		setEvent();
	}

	private void setView() {
		egg = (ImageView) findViewById(R.id.egg);
		nextBtn = (Button) findViewById(R.id.welcome_next);
	}

	private void setEvent() {
		nextBtn.setOnClickListener(this);
		animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.idle);
		animBounce.setAnimationListener(this);
		egg.startAnimation(animBounce);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.welcome_next:
			Intent i = new Intent(getApplicationContext(), SelectEnvironmentActivity.class);
			startActivity(i);
			break;

		default:
			break;
		}
	}

	@Override
	public void onAnimationEnd(Animation anim) {
		// TODO Auto-generated method stub
		egg.startAnimation(animBounce);
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
