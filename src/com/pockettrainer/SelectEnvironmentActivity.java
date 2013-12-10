package com.pockettrainer;

import com.example.pockettrainer.R;
import com.pockettrainer.database.model.PET;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author Monster 2013
 *
 */
public class SelectEnvironmentActivity extends Activity implements
		OnClickListener {

	Button nextBtn;
	Spinner selectEnv;
	Animation pop;
	ImageView env_logo;
	int pos;
	
	PET myPET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_environment);
		myPET = new PET();
		pos = 0;
		setView();
		setEvent();
	}

	private void setView() {
		nextBtn = (Button) findViewById(R.id.select_env_next);
		selectEnv = (Spinner) findViewById(R.id.select_environment);
		env_logo = (ImageView) findViewById(R.id.env_logo);
		pop = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.bounce);
	}

	private void setEvent() {
		nextBtn.setOnClickListener(this);
		selectEnv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				pos = position;
				if(position != 0){
					myPET.setENVIRONMENT("" + position);
					if(position==1) {
						env_logo.setImageResource(R.drawable.env_logo_fire);
					}						
					else if(position==2) {
						env_logo.setImageResource(R.drawable.env_logo_grass);
					}
					else if(position==3) {
						env_logo.setImageResource(R.drawable.env_logo_water);
					}
					env_logo.startAnimation(pop);
				} else {
					env_logo.setImageResource(R.drawable.env_logo_none);
					Toast.makeText(getApplicationContext(), "Anda belum memilih environment", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.not_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_env_next:
			if(pos != 0) {
				Intent i = new Intent(getApplicationContext(),
						PetIdentityActivity.class);
				
				Bundle bund = new Bundle();
				bund.putParcelable("PET", myPET);
				i.putExtras(bund);
				
				startActivity(i);
				this.finish();
			}
			else {
				Toast.makeText(getApplicationContext(), "Anda belum memilih environment", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
}
