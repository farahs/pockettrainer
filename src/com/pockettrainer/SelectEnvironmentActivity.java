package com.pockettrainer;

import com.example.pockettrainer.PetIdentityActivity;
import com.example.pockettrainer.R;
import com.pockettrainer.database.model.PET;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectEnvironmentActivity extends Activity implements
		OnClickListener {

	Button nextBtn;
	Spinner selectEnv;
	
	PET myPET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_environment);
		
		myPET = new PET();
		
		setView();
		setEvent();
	}

	private void setView() {
		nextBtn = (Button) findViewById(R.id.select_env_next);
		selectEnv = (Spinner) findViewById(R.id.select_environment);
	}

	private void setEvent() {
		nextBtn.setOnClickListener(this);
		selectEnv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				if(position != 0){
					myPET.setENVIRONMENT("" + position);
				} else {
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
		getMenuInflater().inflate(R.menu.select_environment, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_env_next:
			Intent i = new Intent(getApplicationContext(),
					PetIdentityActivity.class);
			
			Bundle bund = new Bundle();
			bund.putParcelable("PET", myPET);
			i.putExtras(bund);
			
			startActivity(i);
			break;
		default:
			break;
		}
	}

}
