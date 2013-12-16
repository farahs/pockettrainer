package com.pockettrainer;

import com.monster.pockettrainer.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Monster 2013
 *
 */
public class NotificationDialog extends Dialog implements
		android.view.View.OnClickListener {

	String nMessage = "";

	TextView nMessageTv;

	Button okBtn;

	Button cancelBtn;

	public NotificationDialog(Context context) {
		super(context);
		this.setNotificationTitle("Oops");
		this.setupView();
		this.setupEvent();
	}

	public NotificationDialog(Context context, String message) {
		super(context);
		this.setNotificationTitle("Oops");

		this.setupView();
		this.setupEvent();
		this.setMessage(message);
	}

	public NotificationDialog(Context context, String title, String message) {
		super(context);

		this.setupView();
		this.setupEvent();
		this.setNotificationTitle(title);
		this.setMessage(message);
	}

	public void setOkText(String s) {
		okBtn.setText(s);
	}
	public void setNotificationTitle(String title) {
		this.setTitle(title);
	}

	public void setMessage(String message) {
		this.nMessage = message;
		this.nMessageTv.setText(this.nMessage);
	}

	private void setupView() {
		this.setContentView(R.layout.notification_dialog);
		this.nMessageTv = (TextView) this.findViewById(R.id.notifDialog_text);
		this.cancelBtn = (Button) this.findViewById(R.id.notifDialog_cancel);
		this.okBtn = (Button) this.findViewById(R.id.notifDialog_ok);
	}

	private void setupEvent() {
		// this.cancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		default:
			break;
		}
	}

}
