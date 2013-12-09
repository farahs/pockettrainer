package com.pockettrainer.fragments;

import com.example.pockettrainer.R;
import com.pockettrainer.AboutActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment5 extends Fragment {

	AboutActivity activity;
	View rootView;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = (AboutActivity) this.getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.rootView = inflater.inflate(R.layout.fragment_about_5, container, false);
		
		return this.rootView;
	}

}
