package com.spaceapp.forest_monitor;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor,editor2;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "Disaster_Geofencing";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	private static final String IS_FIRST = "First";
	public static final String IS_CHECKED = "IsCheckedIn";
	public static final String earthquake = "earthquake";
	public static final String landslide = "landslide";
	public static final String flood = "flood";
	public static final String accident = "accident";
	public static final String other = "other";
	static final String KEY_Time = "time";
	
	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";
	public static final String KEY_LGT = "longitude";
	public static final String KEY_LAT = "latitude";
	public static final String KEY_Note = "notification";
	
	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";
	public static final String KEY_OPEN = "opened";
	public static final String KEY_ID = "id";

	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	
	// Constructor
	@SuppressLint("CommitPrefEdits")
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
		editor2 =pref.edit();;
	}
	
	/**
	 * Create login session
	 * */

	public void createTimesSession(int opened){
		editor.putInt(KEY_OPEN,opened);
		editor.commit();
	}
	public HashMap<String,Integer> getTimesDetail(){
		
		HashMap<String,Integer> times=new HashMap<String,Integer>();
		
		times.put(KEY_OPEN,pref.getInt(KEY_OPEN, 0));
		
		return times;
	}

	public void createLoginSession(String name, String email){
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		
		// Storing name in pref
		editor.putString(KEY_NAME, name);
		
		// Storing email in pref
		editor.putString(KEY_EMAIL, email);
		// commit changes
		editor.commit();
	}	
		
	public boolean checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			return false;
		}
		else{
			return true;
			}
		
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));
		
		// user email id
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		
		// return user
		return user;
	}

	public void logoutUser(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		editor.putString(KEY_Time, "Stop");
		editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, Form.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
	
	public boolean isSettingCalled(){
		return pref.getBoolean(IS_CHECKED, false);
	}
}
