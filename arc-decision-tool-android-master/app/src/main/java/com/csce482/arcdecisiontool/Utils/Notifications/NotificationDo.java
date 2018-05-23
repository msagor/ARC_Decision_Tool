package com.csce482.arcdecisiontool.Utils.Notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.csce482.arcdecisiontool.R;


 //
 //This class is used to actually send out a notification.
 //when alarm gets triggered, it calls out this class to send out a notification.
 //
public class NotificationDo extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {

		//default string ..indicator=0
		String text = "You may have tasks remaining!";
		String title = "Timeline Is Expiring Soon!";

		int code;
		int indicator = -1; //indicates when regional and nhq conference calls

		code = Integer.parseInt(intent.getStringExtra("code"));
		System.out.println("Received code is: "+ code);
		indicator = Integer.parseInt(intent.getStringExtra("indicator"));


		//unused
		if(indicator==1){
			text = "Now is the scheduled time for making regional conferenece call";	//reg call
			title = "Regional Conference Call Time Is Now!";
		}
		if(indicator==2){
			text = "Now is the scheduled time for making nhq conferenece call";			//nhq call
			title = "NHQ Conference Call Time Is Now!";
		}
		if(indicator==3){
			text = "Gale force wind is arriving in one hour";							//Gfw
			title = "Gale Force Wind Arrival In One Hour!";
		}
		if(indicator==4){
			text = "Airport is closing in one hour";									//Act
			title = "Airport Closing In One Hour!";
		}
		if(indicator==5){
			text = "Shelter opening time expires in one hour";							//Sheop
			title = "Time For Opening Shelters Expires In One Hour!";
		}
		if(indicator==6){
			text = "Hunker down time is an one hour";									//Hnkrdn
			title = "Hunker Down Time Is Scheduled In One Hour!";
		}
		if(indicator==7){
			text = "Hurricane is expected to reenter in one hour";						//Reentry
			title = "Expected Hurricane Reentry Is Scheduled Within One Hour!";
		}





		//notification
		Intent notificationIntent = new Intent(context, NotificationActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(NotificationActivity.class);
		stackBuilder.addNextIntent(notificationIntent);

		PendingIntent pendingIntent = stackBuilder.getPendingIntent(code, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		Notification notification = builder
				.setContentText(text)
				.setContentTitle(title)
				.setTicker("New Message Alert!")
				.setAutoCancel(true)
				.setSmallIcon(R.drawable.logo)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent).build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		notificationManager.notify(code, notification);



	}
}





























