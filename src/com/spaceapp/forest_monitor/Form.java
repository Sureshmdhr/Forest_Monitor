package com.spaceapp.forest_monitor;

import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Form extends Activity {

	public static EditText login_username;
	static EditText login_password;
	TextView v;
	StringBuilder sb;
	String data;
	static int user_id;
	String uname,upass;
	String username;
	SessionManager session;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		session = new SessionManager(getApplicationContext());

        Log.i("check",String.valueOf(session.checkLogin()));
        if(!session.checkLogin())
        {
        	if(!new GPSTracker(getApplicationContext()).haveNetworkConnection())
        	{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.internet_connectivity_message)
				.setTitle(R.string.internet_connectivity_header);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finish();
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();	

        	}
        	else
        	{
	        	Button login=(Button)findViewById(R.id.btnReport);
				login_username=(EditText)findViewById(R.id.log_username);
				login_password=(EditText)findViewById(R.id.rep_place);
		        login.setOnClickListener(new OnClickListener()
		        {
		        	public void onClick(View arg0) 
		        	{
		        		Log.i("login", "check");
		        		uname=login_username.getText().toString();
						upass=login_password.getText().toString();
						if((!uname.equals(""))&&(!upass.equals("")))
						{
							postData(uname,upass);
						}
						else 
						{
							showerrordialog("Empty field!");
						}
		        	}
		        });
        	}
        	
        	TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        	
	        registerScreen.setOnClickListener(new View.OnClickListener() 
	        {
	        	public void onClick(View v) 
	        	{
	        		Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
	                startActivity(i);
	                finish();
	        	}
	        });
        }
        else
        {
        	startActivity(new Intent(getApplicationContext(),MainActivity.class));
        	finish();
        }
	}
	
	public void postData(String username,String password)
	{
		Receiver connect=new Receiver(Form.this);
		connect.setPath("/login.php");
		connect.addNameValuePairs("username",username);
		connect.addNameValuePairs("password",password);
		AsyncTask<Void, Void, String> output = connect.execute(new Void[0]);
		try 
		{
			String result=output.get();
			Log.i("result",result);
			if(Integer.valueOf(result)==1)
			{	
				showcreateddialog("Login Successful");
			}
			else
			{
				showerrordialog("Login Failed");
			}

		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
	}

	public void toaster(String s)
	{
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}
	
	public void passclear()
	{
		login_username.setText("");
    	login_password.setText("");
	}
	@SuppressWarnings("deprecation")
	public void showerrordialog(String message){
	    AlertDialog alertDialog = new AlertDialog.Builder(Form.this).create();
	// Setting Dialog Title
	alertDialog.setTitle("Error");
	// Setting Dialog Message
	alertDialog.setMessage(message);
	// Setting OK Button
	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(final DialogInterface dialog, final int which) {
	        // Write your code here to execute after dialog closed
	       // Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
	        }
	});

	// Showing Alert Message
	alertDialog.show();	                	

	}
	@SuppressWarnings("deprecation")
	public void showcreateddialog(String message){
	    AlertDialog alertDialog = new AlertDialog.Builder(Form.this).create();
		// Setting Dialog Title
		alertDialog.setTitle("Success");
		// Setting Dialog Message
		alertDialog.setMessage(message);
		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(final DialogInterface dialog, final int which) 
			{
				session.createLoginSession("user",uname);
				Intent intent=new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		alertDialog.show();	                	
	}
}
