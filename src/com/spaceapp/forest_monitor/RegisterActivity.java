package com.spaceapp.forest_monitor;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@SuppressLint("NewApi")
public class RegisterActivity extends Activity {
	EditText firstname,username,email,password,repassword; 
	Button register;
	String data;
	String fname,uname,eml,pass,repass;
	StringBuilder sb;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);
		
		firstname=(EditText)findViewById(R.id.reg_firstname);
		username=(EditText)findViewById(R.id.reg_username);
		email=(EditText)findViewById(R.id.reg_email);
		password=(EditText)findViewById(R.id.reg_password);
		repassword=(EditText)findViewById(R.id.reg_repassword);
		register=(Button)findViewById(R.id.btnRegister);
		
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        loginScreen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), Form.class);
                startActivity(i);
                finish();

			}
		});

		register.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				fname=firstname.getText().toString();
				uname=username.getText().toString();
				eml=email.getText().toString();
				pass=password.getText().toString();
				repass=repassword.getText().toString();
				if(eml.equals("")||fname.equals("")||uname.equals("")||pass.equals("")||repass.equals(""))
		        		{
		        			toaster("Some or all Field are EMPTY");
		        		}
		         //Password Matching Check
		        else if (!pass.equals(repass)){
        			passclear();	 //Clear Only Password EditText Fields
        			toaster("Password mismatch");
		        }
		        
		        //Email Check
		        else if(!isValidEmailAddress(eml)){
	        		email.setText("");
		        	passclear();
		        	toaster("Invalid Email");
	        	}
				
		        else if ((pass.length()<8)||(pass.length()>25))
		        {
			        	passclear();
			        	toaster("Passwod Length should be between 8 and 25");					
				}
				else
				{
					postData();				
				}
			}
		});
		

	}
	protected void postData() 
	{
		Receiver connect=new Receiver(this);
		connect.setPath("/forestmonitor/register.php");
		connect.addNameValuePairs("fullname",firstname.getText().toString());
		connect.addNameValuePairs("username",username.getText().toString());
		connect.addNameValuePairs("password",password.getText().toString());
		connect.addNameValuePairs("email",email.getText().toString());
		AsyncTask<Void, Void, String> output = connect.execute(new Void[0]);
        String result = null;
		try 
		{
			result = output.get();
			Log.i("result", result);
			if(Integer.valueOf(result)==1)
			{
				showcreateddialog("Account created.");
			}
			else
			{
				showerrordialog(result);
			}
		}
		catch (InterruptedException e2) 
		{
			e2.printStackTrace();
		} 
		catch (ExecutionException e2) 
		{
			e2.printStackTrace();
		}
	}
    
	public boolean isValidEmailAddress(String emailAddress) 
	{
	    String emailRegEx;
	    Pattern pattern;
	    // Regex for a valid email address
	    emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
	    // Compare the regex with the email address
	    pattern = Pattern.compile(emailRegEx);
	    Matcher matcher = pattern.matcher(emailAddress);
	    if (!matcher.find()) 
	    {
	    	return false;
	    }
	    return true;
	  }
	

	protected void passclear() 
	{
		password.setText("");
		repassword.setText("");
	}

	protected void toaster(String string) 
	{
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("deprecation")
	public void showerrordialog(String message)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
		// Setting Dialog Title
		alertDialog.setTitle("Error");
		// Setting Dialog Message
		alertDialog.setMessage(message);
		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(final DialogInterface dialog, final int which) 
			{
				// Write your code here to execute after dialog closed
				// Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
			}
		});
		// Showing Alert Message
		alertDialog.show();	                	
	}
	
	@SuppressWarnings("deprecation")
	public void showcreateddialog(String message)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
		// Setting Dialog Title
		alertDialog.setTitle("Success");
		// Setting Dialog Message
		alertDialog.setMessage(message);
		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(final DialogInterface dialog, final int which) 
			{
		        Intent intent=new Intent(getApplicationContext(),Form.class);
		        startActivity(intent);
		        finish();
			}
		});
		// Showing Alert Message
		alertDialog.show();	                	
	}
}