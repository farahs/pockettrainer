package com.pockettrainer.fragments;

import com.example.pockettrainer.R;
import com.pockettrainer.AboutActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutFragment3 extends Fragment implements AnimationListener{

	AboutActivity activity;
	View rootView;
	
	ImageView obj1;
	TextView txt1;
	
	Animation slideTop, slideBottom;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.rootView = inflater.inflate(R.layout.fragment_about_3, container, false);
		this.activity = (AboutActivity) this.getActivity();
		
		obj1 = (ImageView) this.rootView.findViewById(R.id.obj1);
		txt1 = (TextView) this.rootView.findViewById(R.id.txt1);
		
		animate();
		
		return this.rootView;
	}
	
	public void animate() {
		slideTop = AnimationUtils.loadAnimation(this.rootView.getContext(),
				R.anim.fadein_top);
		slideTop.setAnimationListener(this);
		
		slideBottom = AnimationUtils.loadAnimation(this.rootView.getContext(),
				R.anim.fadein_bottom);
		slideBottom.setAnimationListener(this);
		
		obj1.startAnimation(slideBottom);
		txt1.startAnimation(slideTop);
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
