package com.spaceapp.forest_monitor;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuActivity extends Activity
{
	    public boolean onCreateOptionsMenu(Menu menu)
	    {
	        MenuInflater menuInflater = getMenuInflater();
	        menuInflater.inflate(R.menu.main, menu);
	        return true;
	    }
	    
	    public boolean onOptionsItemSelected(MenuItem item)
	    {
	        
	        switch (item.getItemId())
	        {
	        case R.id.log_out:
	        	SessionManager session = new SessionManager(getApplicationContext());
				session.logoutUser();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	    
}
