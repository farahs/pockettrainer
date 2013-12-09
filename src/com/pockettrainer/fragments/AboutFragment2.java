package com.pockettrainer.fragments;

import com.example.pockettrainer.R;
import com.pockettrainer.AboutActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment2 extends Fragment {

	AboutActivity activity;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.rootView = inflater.inflate(R.layout.fragment_about_2, container, false);
		this.activity = (AboutActivity) this.getActivity();
		
		return this.rootView;
	}

}
