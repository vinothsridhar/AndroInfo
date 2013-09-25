package com.example.phoneinfo;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.view.View;
import android.widget.RemoteViews;

public class phoneInfoService extends Service
{
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
		
		Intent batterintent=context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level=batterintent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		int scale=batterintent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
		
		ActivityManager amanager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		
		MemoryInfo mi=new ActivityManager.MemoryInfo();
		amanager.getMemoryInfo(mi);
		List<RunningAppProcessInfo> runninAppProcesses=amanager.getRunningAppProcesses();
		
		StatFs stat=new StatFs(Environment.getExternalStorageDirectory().getPath());
		
		long amem=stat.getAvailableBlocks();
		long tmem=stat.getBlockCount();
		
		rv.setTextViewText(R.id.textView10, "OS Version: "+android.os.Build.VERSION.RELEASE);
		rv.setTextViewText(R.id.textView11, "Phone Model: "+android.os.Build.MANUFACTURER+"-"+android.os.Build.MODEL);
		rv.setTextViewText(R.id.textView12, "Avail. RAM MEM: "+(mi.availMem)/(1024*1024)+"MB");
		rv.setTextViewText(R.id.textView13, "Avail. SDCARD MEM: "+(amem*stat.getBlockSize())/(1024*1024)+"MB");
		rv.setTextViewText(R.id.textView14, "Total SDCARD MEM: "+(tmem*stat.getBlockSize())/(1024*1024)+"MB");
		rv.setTextViewText(R.id.textView15, "Total Running Process: "+runninAppProcesses.size());
		rv.setTextViewText(R.id.textView16, "Battery Level: "+(level*100/scale)+"%");
		rv.setTextViewText(R.id.textView22, "Last Boot: "+setUT(SystemClock.uptimeMillis()));
		
		rv.setInt(R.id.simInfo, "setBackgroundResource", R.drawable.button);
		rv.setInt(R.id.phoneInfo, "setBackgroundResource", R.drawable.button_select);
		rv.setInt(R.id.nwInfo, "setBackgroundResource", R.drawable.button);
		
		rv.setInt(R.id.phoneInfoDetails, "setVisibility", View.VISIBLE);
		rv.setInt(R.id.simInfoDetails, "setVisibility",View.GONE);
		rv.setInt(R.id.nwInfoDetails, "setVisibility", View.GONE);
		
		Intent intent1=new Intent(getApplicationContext(),phoneInfoService.class);
		PendingIntent pi1=PendingIntent.getService(getApplicationContext(), 0, intent1, 0);
		
		Intent intent2=new Intent(getApplicationContext(),simInfoService.class);
		PendingIntent pi2=PendingIntent.getService(getApplicationContext(), 0, intent2, 0);
		
		Intent intent3=new Intent(getApplicationContext(),networkInfoService.class);
		PendingIntent pi3=PendingIntent.getService(getApplicationContext(), 0, intent3, 0);

		rv.setOnClickPendingIntent(R.id.simInfo, pi2);
		rv.setOnClickPendingIntent(R.id.nwInfo, pi3);
		
		getApplicationContext().stopService(intent2);
		getApplicationContext().stopService(intent3);
		
		mgr.updateAppWidget(me, rv);
	}
	
	public String setUT(long milli)
	{
		String str="";
		if((milli/1000)<60)
			str=""+(milli/1000)+" secs ago";
		else if((milli/(1000*60)<60))
			str=""+milli/(1000*60)+" mins ago";
		else if((milli/(1000*60*60))<24)
			str=""+milli/(1000*60*60)+" hrs ago";
		else if((milli/(1000*60*60*24)<30))
			str=""+milli/(1000*60*60*24)+" days ago";
				
		return str;
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
				System.out.println("Receiving Phone..");
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