package com.pockettrainer;

import com.example.pockettrainer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;

public class EvolutionActivity extends Activity implements AnimationListener,
		OnClickListener {

	Button cont;
	ImageView egg;
	ImageView star;
	Animation animBounce;
	Animation animFadeOut;
	Animation animFadeIn;
	Animation animRotate;
	int frame = 0;
	private Display mDisplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.activity_evolution);

		this.mDisplay = this.getWindowManager().getDefaultDisplay();

		cont = (Button) findViewById(R.id.contBtn);
		cont.setOnClickListener(this);

		egg = (ImageView) findViewById(R.id.egg);
		star = (ImageView) findViewById(R.id.star);

		animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.idle);
		animBounce.setAnimationListener(this);
		animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_out);
		animFadeOut.setAnimationListener(this);
		animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fade_in);
		animFadeIn.setAnimationListener(this);
		animRotate = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.rotating);
		animRotate.setAnimationListener(this);
		egg.startAnimation(animBounce);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contBtn:
			frame = 1;
			cont.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onAnimationEnd(Animation anim) {
		// TODO Auto-generated method stub
		if (frame == 0) {
			egg.startAnimation(animBounce);
		} else if (frame == 1) {
			egg.startAnimation(animFadeOut);
			star.setVisibility(View.VISIBLE);
			star.startAnimation(animFadeIn);
			frame = 2;
		} else if (frame == 2) {
			if (anim == animFadeOut)
				egg.setVisibility(View.GONE);
			if (anim == animFadeIn)
				star.startAnimation(animRotate);
			// frame = 3;
		} else if (frame == 3) {

		} else if (frame == 4) {

		} else if (frame == 5) {

		}

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
