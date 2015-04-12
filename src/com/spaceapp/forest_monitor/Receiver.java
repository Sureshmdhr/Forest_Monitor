package com.spaceapp.forest_monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class Receiver extends AsyncTask<Void, Void, String>
{
	private String host = "http://192.168.19.1/";
	private List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	private String path;
	private Context mContext=null;

	public Receiver()
	{
		
	}
	
	public Receiver(Context context)
	{
		this.mContext=context;
	}
	public void addNameValuePairs(String paramString1, String paramString2)
	{
		this.nameValuePairs.add(new BasicNameValuePair(paramString1, paramString2));
	}

	protected String doInBackground(Void[] paramArrayOfVoid)
	{
		try
		{
			String str1 = this.host + this.path;
			DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
			HttpPost localHttpPost = new HttpPost(str1);
			localHttpPost.setEntity(new UrlEncodedFormEntity(this.nameValuePairs));
			String str2 = EntityUtils.toString(localDefaultHttpClient.execute(localHttpPost).getEntity());
			return str2;
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return null;
	}

	public String getHost()
	{
		return this.host;
	}

	public List<NameValuePair> getNameValuePairs()
	{
		return this.nameValuePairs;
	}

	public String getPath()
	{
		return this.path;
	}	

	protected void onPostExecute(String paramString)
	{
		super.onPostExecute(paramString);
	}	

	protected void onProgressUpdate(Void... values)
	{
		super.onProgressUpdate(values);
	}

	protected void onPreExecute()
	{
		super.onPreExecute();
	}

	public void setHost(String paramString)
	{
		this.host = paramString;
	}

	public void setPath(String paramString)
	{
		this.path = paramString;
	}
	
	public boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;
	    ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}

}