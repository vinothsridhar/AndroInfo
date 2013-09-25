package com.example.phoneinfo;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;

public class simInfoService extends Service
{
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
		TelephonyManager tmanager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		rv.setTextViewText(R.id.textView2, "Operator: "+tmanager.getNetworkOperatorName());
		rv.setTextViewText(R.id.textView3, "Network Type: "+getNT(tmanager.getNetworkType()));
		rv.setTextViewText(R.id.textView4, "Data Activity: "+getDA(tmanager.getDataActivity()));
		rv.setTextViewText(R.id.textView5, "Data State: "+getDS(tmanager.getDataState()));
		rv.setTextViewText(R.id.textView6, "Number: "+tmanager.getLine1Number());
		rv.setTextViewText(R.id.textView7, "IMEI No.: "+tmanager.getDeviceId());
		rv.setTextViewText(R.id.textView8, "Location: "+tmanager.getCellLocation());
		rv.setTextViewText(R.id.textView9, "Country: "+getLoc(tmanager.getSimCountryIso()));
		
		rv.setInt(R.id.simInfo, "setBackgroundResource", R.drawable.button_select);
		rv.setInt(R.id.phoneInfo, "setBackgroundResource", R.drawable.button);
		rv.setInt(R.id.nwInfo, "setBackgroundResource", R.drawable.button);
		
		rv.setInt(R.id.nwInfoDetails, "setVisibility", View.GONE);
		rv.setInt(R.id.phoneInfoDetails, "setVisibility", View.GONE);
		rv.setInt(R.id.simInfoDetails, "setVisibility",View.VISIBLE);
		
		Intent intent1=new Intent(getApplicationContext(),phoneInfoService.class);
		PendingIntent pi1=PendingIntent.getService(getApplicationContext(), 0, intent1, 0);
		
		Intent intent2=new Intent(getApplicationContext(),simInfoService.class);
		PendingIntent pi2=PendingIntent.getService(getApplicationContext(), 0, intent2, 0);
		
		Intent intent3=new Intent(getApplicationContext(),networkInfoService.class);
		PendingIntent pi3=PendingIntent.getService(getApplicationContext(), 0, intent3, 0);

		rv.setOnClickPendingIntent(R.id.phoneInfo, pi1);
		rv.setOnClickPendingIntent(R.id.nwInfo, pi3);
		
		getApplicationContext().stopService(intent1);
		getApplicationContext().stopService(intent3);
		
		mgr.updateAppWidget(me, rv);
	}
	
	public String getLoc(String i)
	{
		String country="";
		Locale l=new Locale("",i);
		country=l.getDisplayCountry();
		return country;
	}
	public String getDS(int i)
	{
		String dataState="";
		switch(i)
		{
		case TelephonyManager.DATA_CONNECTED:
			dataState="CONNECTED";
			break;
		case TelephonyManager.DATA_CONNECTING:
			dataState="CONNECTING";
			break;
		case TelephonyManager.DATA_DISCONNECTED:
			dataState="DISCONNECTED";
			break;
		case TelephonyManager.DATA_SUSPENDED:
			dataState="SUSPENDED";
			break;
		}
		
		return dataState;
	}
	
	public String getDA(int i)
	{
		String dataActivity="";
		switch(i)
		{
		case TelephonyManager.DATA_ACTIVITY_IN:
			dataActivity="IN";
			break;
		case TelephonyManager.DATA_ACTIVITY_INOUT:
			dataActivity="INOUT";
			break;
		case TelephonyManager.DATA_ACTIVITY_OUT:
			dataActivity="OUT";
			break;
		case TelephonyManager.DATA_ACTIVITY_NONE:
			dataActivity="NONE";
			break;
		}
		return dataActivity;
	}
	
	View.OnClickListener phoneInfolistener=new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public String getNT(int i)
	{
		String network="";
		switch(i)
		{
		case TelephonyManager.NETWORK_TYPE_CDMA:
			network="CDMA/3G";
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			network="EDGE";
			break;
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			network="1XRTT";
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			network="EHRPD";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			network="EVDO_0";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			network="EVDO_A";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			network="EVDO_B";
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			network="GPRS";
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			network="HSDPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			network="HSPA";
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			network="HSPAP";
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			network="IDEN";
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:
			network="LTE";
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			network="UMTS";
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			network="UNKNOWN";
			break;
		}
		
		return network;
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
				System.out.println("Receiving Sim..");
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