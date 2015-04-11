package com.spaceapp.forest_monitor;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class message extends MenuActivity
{
	TextView msgtitle,msg;
	Button viewmap;
	//MapView mv=geofences.mv;
	String ms_lat,ms_lgt,title,message;
	double lat,lgt;
	int no;
	TextView[] value;
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.message);
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {    
        	ms_lat = extras.getString("lat");
            ms_lgt = extras.getString("lgt");
            title = extras.getString("title");
            message = extras.getString("message");
            no=extras.getInt("no");
        }
        if(no==1)
        {
        	View linearLayout =  findViewById(R.id.info);
        	value[no].setText(message);
        	value[no].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        	((LinearLayout) linearLayout).addView(value[no]);
        }
        lat=Double.parseDouble(ms_lat);
        lgt=Double.parseDouble(ms_lgt);
	    msgtitle=(TextView)findViewById(R.id.title);
	    msg=(TextView)findViewById(R.id.msg);
	    msgtitle.setText(title);
	    msg.setText(message);
	}
}