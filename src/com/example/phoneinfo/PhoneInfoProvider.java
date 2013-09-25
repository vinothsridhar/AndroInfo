package com.example.phoneinfo;

import java.util.Timer;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class PhoneInfoProvider extends AppWidgetProvider
{

	AppWidgetManager awm;
	public static Timer timer;
	RemoteViews rv;
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		if(timer!=null)
			timer.cancel();
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		awm=appWidgetManager;
		rv=new RemoteViews("com.example.phoneinfo", R.layout.widget);
		
		Intent intent2=new Intent(context,simInfoService.class);
		context.startService(intent2);
		update(context);
	}
	
	public void update(Context context)
	{
		ComponentName me=new ComponentName(context,PhoneInfoProvider.class);
		
		awm.updateAppWidget(me, rv);
		
	}
}