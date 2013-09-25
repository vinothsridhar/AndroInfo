package com.example.phoneinfo;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		System.out.println("ReStarting");
		PhoneInfoProvider provider=new PhoneInfoProvider();
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(arg0);
		provider.onUpdate(arg0, appWidgetManager, null);
	}
	
}