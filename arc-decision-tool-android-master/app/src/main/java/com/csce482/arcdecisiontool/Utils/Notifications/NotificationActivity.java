package com.csce482.arcdecisiontool.Utils.Notifications;

import android.app.Activity;
import android.os.Bundle;

import com.csce482.arcdecisiontool.R;

//this class is only used to define the action when user clicks on the notification when it pops up

public class NotificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//if clicking the notification wants to jump into any screen add here
		setContentView(R.layout.activity_timeline);

	}
}
