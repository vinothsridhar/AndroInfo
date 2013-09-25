package com.example.phoneinfo;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.IBinder;
import android.text.format.Formatter;
import android.view.View;
import android.widget.RemoteViews;

public class networkInfoService extends Service
{
	long lastUSize,lastDSize=0;
	Timer timer;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		update(getApplicationContext(),intent);
		if(PhoneInfoProvider.timer!=null)
		{
		PhoneInfoProvider.timer.cancel();
		PhoneInfoProvider.timer=null;
		}
		if(PhoneInfoProvider.timer==null)
		{
			PhoneInfoProvider.timer=new Timer();
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.SECOND, 1);
			cal.add(Calendar.MILLISECOND, 0);
			PhoneInfoProvider.timer.schedule(new MyTime(getApplicationContext(),intent),cal.getTime(), 1000);
		}
		return START_STICKY;
	}
	
	public void update(Context context,Intent arg0)
	{
		ComponentName me=new ComponentName(this, PhoneInfoProvider.class);
		RemoteViews rv=new RemoteViews(context.getPackageName(), R.layout.widget);
		AppWidgetManager mgr=AppWidgetManager.getInstance(this);
		ConnectivityManager cmanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ninfo=cmanager.getActiveNetworkInfo();
		
		NumberFormat format=NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(2);
		float receivedBytes=(float)TrafficStats.getTotalRxBytes()/(1024*1024);
		float transferedBytes=(float)TrafficStats.getTotalTxBytes()/(1024*1024);
		rv.setTextViewText(R.id.textView17, "Total Download Size: "+format.format(receivedBytes)+" MB");
		rv.setTextViewText(R.id.textView18, "Total Upload Size: "+format.format(transferedBytes)+"MB");
		try
		{
		rv.setTextViewText(R.id.textView19, "Active Network: "+getCNT(ninfo.getType()));
		}
		catch(Exception e)
		{
			rv.setTextViewText(R.id.textView19, "Active Network: NONE");
		}
		rv.setTextViewText(R.id.textView20, "Download Speed: "+getDS());
		rv.setTextViewText(R.id.textView21, "Upload Speed: "+getUS());
		
		rv.setInt(R.id.simInfo, "setBackgroundResource", R.drawable.button);
		rv.setInt(R.id.phoneInfo, "setBackgroundResource", R.drawable.button);
		rv.setInt(R.id.nwInfo, "setBackgroundResource", R.drawable.button_select);
		
		rv.setInt(R.id.phoneInfoDetails, "setVisibility", View.GONE);
		rv.setInt(R.id.simInfoDetails, "setVisibility",View.GONE);
		rv.setInt(R.id.nwInfoDetails, "setVisibility", View.VISIBLE);
		
		Intent intent1=new Intent(getApplicationContext(),phoneInfoService.class);
		PendingIntent pi1=PendingIntent.getService(getApplicationContext(), 0, intent1, 0);
		
		Intent intent2=new Intent(getApplicationContext(),simInfoService.class);
		PendingIntent pi2=PendingIntent.getService(getApplicationContext(), 0, intent2, 0);
		
		Intent intent3=new Intent(getApplicationContext(),networkInfoService.class);
		PendingIntent pi3=PendingIntent.getService(getApplicationContext(), 0, intent3, 0);

		rv.setOnClickPendingIntent(R.id.simInfo, pi2);
		rv.setOnClickPendingIntent(R.id.phoneInfo, pi1);
		
		getApplicationContext().stopService(intent1);
		getApplicationContext().stopService(intent2);
		
		mgr.updateAppWidget(me, rv);
	}
	
	public String getDS()
	{
		String speed="";
		if(lastDSize==0)
		{
			lastDSize=TrafficStats.getTotalRxBytes();
			return speed;
		}
		long currentSize=TrafficStats.getTotalRxBytes();
		long diff=currentSize-lastDSize;
		if(diff<1024)
			speed=(int)diff+" Bytes/s";
		else
			speed=(int)(diff/1024)+" KB/s";
		lastDSize=currentSize;
		return speed;
	}
	
	public String getUS()
	{
		String speed="";
		if(lastUSize==0)
		{
			lastUSize=TrafficStats.getTotalTxBytes();
			return speed;
		}
		long currentSize=TrafficStats.getTotalTxBytes();
		long diff=currentSize-lastUSize;
		if(diff<1024)
			speed=(int)diff+" Bytes/s";
		else
			speed=(int)(diff/1024)+" KB/s";
		lastUSize=currentSize;
		
		return speed;
	}
	
	public String getCNT(int i)
	{
		String network_type="";
		switch(i)
		{
		case ConnectivityManager.TYPE_BLUETOOTH:
			network_type="BLUETOOTH";
			break;
		case ConnectivityManager.TYPE_MOBILE:
			network_type="MOBILE INTERNET";
			break;
		case ConnectivityManager.TYPE_WIFI:
			network_type="WIFI";
			break;
		case ConnectivityManager.TYPE_WIMAX:
			network_type="WIMAX";
			break;
		default:
			network_type="NONE";
		}
		
		return network_type;
	}
	
	private class MyTime extends TimerTask
	{
		Context context;
		Intent intent;
		public MyTime(Context c,Intent i)
		{
			this.context=c;
			this.intent=i;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try
			{
				System.out.println("Receiving Network..");
				update(context,intent);
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
		}
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}