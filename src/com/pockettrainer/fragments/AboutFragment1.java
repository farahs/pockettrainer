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

public class AboutFragment1 extends Fragment implements AnimationListener {

	AboutActivity activity;
	View rootView;
	ImageView obj1;
	TextView txt1, txt2;
	
	Animation slideTop, slideBottom, slideTop2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.rootView = inflater.inflate(R.layout.fragment_about_1, container, false);
		this.activity = (AboutActivity) this.getActivity();
		
		obj1 = (ImageView) this.rootView.findViewById(R.id.obj1);
		txt1 = (TextView) this.rootView.findViewById(R.id.txt1);
		txt2 = (TextView) this.rootView.findViewById(R.id.txt2);
		
		animate();
		
		return this.rootView;
	}
	
	public void animate() {
		slideTop = AnimationUtils.loadAnimation(this.rootView.getContext(),
				R.anim.fadein_top);
		slideTop.setAnimationListener(this);
		
		slideTop2 = AnimationUtils.loadAnimation(this.rootView.getContext(),
				R.anim.fadein_top);
		slideTop2.setAnimationListener(this);
		
		slideBottom = AnimationUtils.loadAnimation(this.rootView.getContext(),
				R.anim.fadein_bottom);
		slideBottom.setAnimationListener(this);
		
		obj1.startAnimation(slideBottom);
		txt1.startAnimation(slideTop);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(animation==slideTop) {
			txt2.setVisibility(View.VISIBLE);
			txt2.startAnimation(slideTop2);
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
