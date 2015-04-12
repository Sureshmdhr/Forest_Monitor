package com.spaceapp.forest_monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class FenceService
{
	private Context mContext;
	private SessionManager session;
	private Integer opened_no;
	private int no;
	private boolean[] receive;
	private Boolean[][] my_fences_checked;
	private Handler handler;
	private Getdata mydata;
	private String username;
	private Runnable runnable;
	
	public FenceService(Context context)
	{
		mContext=context;
		session = new SessionManager(mContext);
		HashMap<String, Integer>times=session.getTimesDetail();
		opened_no = times.get(SessionManager.KEY_OPEN);
		
		HashMap<String, String> user = session.getUserDetails();
	    username = user.get(SessionManager.KEY_EMAIL);
		mydata=new Getdata(username, mContext);
		
	}
	
	public void startService(final int time) 
	{
		if(opened_no==1)
		{
			Log.i("start", "fence");
 			no=1;
 			receive=new boolean[10000];
 			my_fences_checked=new Boolean[1000][1000];// no of max fences
 			for(int i=0;i<my_fences_checked.length;i++)
 			{
 				Arrays.fill(my_fences_checked[i], true);
 			}
 			Arrays.fill(receive, true);
 			session.createTimesSession(opened_no+1);
 			handler = new Handler();
 			runnable=new Runnable() 
 			{
				public void run() 
				{
			        SessionManager session = new SessionManager(mContext);
			    	if(session.checkLogin())
			    	{
						if(new GPSTracker(mContext).haveNetworkConnection())
						{
			    			checkForUserPosition();
			    			handler.postDelayed(this, time);
						}
						else
						{
			    			handler.postDelayed(this, time);
						}
			    	}
			    }
 			};
 			
 			handler.postDelayed(runnable,time);
 		}
		
	}

	
	public void stopService()
	{
		handler.removeCallbacks(runnable);
	}
	
	protected void checkForUserPosition()
	{
		ArrayList<String> admin_fencename = mydata.getAdminData("name");
		ArrayList<String> admin_catagory = mydata.getAdminData("catagory");
		for(int i=0;i<admin_fencename.size();i++)
		{
				String result=postData(admin_fencename.get(i));
    			ArrayList<String> condition= mydata.convertStringtoArrayList(result,"condition");
				ArrayList<String> message= mydata.convertStringtoArrayList(result,"message");
				ArrayList<String> id_string= mydata.convertStringtoArrayList(result,"id");
				ArrayList<String> lgt= mydata.convertStringtoArrayList(result,"lgt");
				ArrayList<String> lat= mydata.convertStringtoArrayList(result,"lat");
				for(int i1=0;i1<condition.size();i1++)
				{
					int j=Integer.valueOf(id_string.get(i1));
					//Enters a geofence
					if(condition.get(i1).equals("t")&&receive[j])
					{
						sendnotification(admin_fencename.get(i), message.get(i1),lgt.get(i1),lat.get(i1),j,"inside");
						receive[j]=false;//
					}
					//Already inside geofencce and has received notification
					else if(condition.get(i1).equals("t")&&(!receive[j]))
					{

					}
					//Leaves geofence for first time
					else if (condition.get(i1).equals("f")&&(!receive[j]))
					{
						sendnotification(admin_fencename.get(i), message.get(i1),lgt.get(i1),lat.get(i1),j,"outside");
						receive[j]=true;
					}
					//Leaves geofence and already got notification
					else
					{
						
					}
				}
			}
		}
	
	
	@SuppressWarnings("deprecation")
	public void sendnotification(String title,String message,String lgt,String lat,int j, String state)
	{
		no=no+1;
		NotificationManager mgr = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
 		int requestID = (int) System.currentTimeMillis(); 
		Notification note=new Notification(R.drawable.ic_launcher,"Disaster Geofence",System.currentTimeMillis());
		Intent intent=new Intent(mContext, message.class);
		intent.putExtra("lat", lat);
		intent.putExtra("lgt", lgt);
		intent.putExtra("title", title);
		intent.putExtra("message", message);
		intent.putExtra("no", no);
		PendingIntent i = PendingIntent.getActivity(mContext, requestID, intent, 0);
		if (state.equals("inside"))
			note.setLatestEventInfo(mContext, "You are entering the Geofence: "+title,message, i);
		else
			note.setLatestEventInfo(mContext, "You are exiting the Geofence: ",title, i);
		mgr.notify(j, note);
	}
	
	private String postData(String current_fence)
	{
		GPSTracker location = new GPSTracker(mContext);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		
        String lt = String.valueOf(latitude);
        String lg = String.valueOf(longitude);
        Log.i("position", "latitude="+lt+"longitude"+lg);
		Receiver connect=new Receiver(mContext);
		connect.setPath("polygon1/check_inside_polygon.php");
		connect.addNameValuePairs("value1", lg);
		connect.addNameValuePairs("value2",lt);
		connect.addNameValuePairs("value3", current_fence);
		AsyncTask<Void, Void, String> output = connect.execute(new Void[0]);
		String result=null;
		try 
		{
			result=output.get();
			return result;
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		return result;
	}


}
