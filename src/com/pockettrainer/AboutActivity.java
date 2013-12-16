package com.pockettrainer;


import com.monster.pockettrainer.R;
import com.pockettrainer.fragments.AboutFragment1;
import com.pockettrainer.fragments.AboutFragment2;
import com.pockettrainer.fragments.AboutFragment3;
import com.pockettrainer.fragments.AboutFragment4;
import com.pockettrainer.fragments.AboutFragment5;
import com.pockettrainer.fragments.AboutFragment6;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author Monster 2013
 *
 */
public class AboutActivity extends FragmentActivity {

	ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_about);
		
		this.viewPager = (ViewPager) findViewById(R.id.pager);
		this.viewPager.setAdapter(new SectionPagerAdapter());
		this.viewPager.setOffscreenPageLimit(0);
		
	}
	
	public class SectionPagerAdapter extends FragmentPagerAdapter {

		final int PAGE_COUNT = 6;
		public AboutFragment1 fragment1;
		public AboutFragment2 fragment2;
		public AboutFragment3 fragment3;
		public AboutFragment4 fragment4;
		public AboutFragment5 fragment5;
		public AboutFragment6 fragment6;
		
		
		public SectionPagerAdapter() {
			super(AboutActivity.this.getSupportFragmentManager());
			this.fragment1 = null;
			this.fragment2 = null;
			this.fragment3 = null;
			this.fragment4 = null;
			this.fragment5 = null;
			this.fragment6 = null;
		}


		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return this.fragment1 = new AboutFragment1();
				
			case 1:
				return this.fragment2 = new AboutFragment2();
				
			case 2:
				return this.fragment3 = new AboutFragment3();
				
			case 3:
				return this.fragment4 = new AboutFragment4();

			case 4:
				return this.fragment5  = new AboutFragment5();
				
			case 5:
				return this.fragment6 = new AboutFragment6();
			default:
				return null;

			}
		}


		@Override
		public int getCount() {
			return this.PAGE_COUNT;
		}
		
	}

	
}
