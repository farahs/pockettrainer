package com.example.pockettrainer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TrainingActivity extends Activity implements OnClickListener {

	TextView timerTV;
	Button startBtn;
	Button pauseBtn;
	Button resumeBtn;
	Button stopBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);

		setupView();
		setupEvent();
	}

	private void setupView() {
		timerTV = (TextView) this.findViewById(R.id.train_timer);
		startBtn = (Button) this.findViewById(R.id.training_start);
		pauseBtn = (Button) this.findViewById(R.id.training_pause);
		resumeBtn = (Button) this.findViewById(R.id.training_resume);
		stopBtn = (Button) this.findViewById(R.id.training_stop);
		
		startBtn.setVisibility(View.VISIBLE);
		pauseBtn.setVisibility(View.GONE);
		resumeBtn.setVisibility(View.GONE);
		stopBtn.setVisibility(View.GONE);
	}

	private void setupEvent() {
		startBtn.setOnClickListener(this);
		pauseBtn.setOnClickListener(this);
		resumeBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.training_start:
			startBtn.setVisibility(View.GONE);
			pauseBtn.setVisibility(View.VISIBLE);
			resumeBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.GONE);
			break;
		case R.id.training_pause:
			startBtn.setVisibility(View.GONE);
			pauseBtn.setVisibility(View.GONE);
			resumeBtn.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.VISIBLE);
			break;
		case R.id.training_resume:
			startBtn.setVisibility(View.GONE);
			pauseBtn.setVisibility(View.VISIBLE);
			resumeBtn.setVisibility(View.GONE);
			stopBtn.setVisibility(View.GONE);
			break;
		case R.id.training_stop:
			Intent i = new Intent(getApplicationContext(), TrainingResultActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}

}
