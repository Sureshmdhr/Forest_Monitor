package com.spaceapp.forest_monitor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends MenuActivity  implements AdapterView.OnItemSelectedListener
{

	private Spinner spinner1;
	private EditText desc;
	private ImageButton photo;
	private Spinner spinner2;
	private Button upload;
	
	private String report_type="";

	private Uri fileUri;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 0,MEDIA_TYPE_IMAGE = 1;
	private File mediaFile;
	private File cacheDir;
	private String photoname,report_reason;
	protected File my_photo;
	private GPSTracker gps;
	private String host="http://192.168.19.1";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	      StrictMode.setThreadPolicy(policy);
	      
		spinner1=(Spinner)findViewById(R.id.spinner1);
		desc=(EditText)findViewById(R.id.description);
		spinner2=(Spinner)findViewById(R.id.spinner2);
		photo=(ImageButton)findViewById(R.id.photo);
		upload=(Button)findViewById(R.id.button1);
		
		spinner1.setEnabled(true);
		spinner1.setOnItemSelectedListener((OnItemSelectedListener) this);

		String[] regionsArray = getResources().getStringArray(R.array.type);
		ArrayList<String> type = new ArrayList<String>(Arrays.asList(regionsArray));
		
        ArrayAdapter <String> c = new ArrayAdapter <String> (this,android.R.layout.simple_spinner_item,type);
        c.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(c);
		spinner2.setOnItemSelectedListener((OnItemSelectedListener) this);
		spinner2.setEnabled(false);
		photo.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				if(isDeviceSupportCamera())
					my_photo=takephoto();
			}
		});
		
		gps=new GPSTracker(getApplicationContext());

		upload.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				if(!spinner2.isEnabled())
				{
					Receiver connect=new Receiver();
					connect.setHost(host);
					connect.setPath("/forestmonitor/upload.php");
					connect.addNameValuePairs("type", report_type);
					connect.addNameValuePairs("description", desc.getText().toString());
					connect.addNameValuePairs("reason", report_reason);
					connect.addNameValuePairs("latitude",String.valueOf(gps.getLatitude()));
					connect.addNameValuePairs("longitude",String.valueOf(gps.getLongitude()));
					if(mediaFile!=null)
					{
						connect.addNameValuePairs("image_url",mediaFile.getName());
						Log.i("photo",mediaFile.getName());
					}
					else
					{
						connect.addNameValuePairs("image_url","");
					}
					AsyncTask<Void, Void, String> result = connect.execute(new Void[0]);
					String output = null;
					try 
					{
						output = result.get();
						Log.i("report", output);
						if(!output.equals(String.valueOf("false")))
						{
							try 
							{
								if(mediaFile!=null)
								{
									String outcome=uploadphotousingmultipart(my_photo);
									if(outcome.equals(String.valueOf("success")))
									{
										Log.i("photo", "deleted");
										deleteFile(my_photo);
									}
								}
								spinner2.setEnabled(false);
								desc.setText("");
							}
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
						else
						{
							toaster("Report Not Sent");
						}
						}
					catch (InterruptedException e1) 
					{
						e1.printStackTrace();
					}
					catch (ExecutionException e1) 
					{
						e1.printStackTrace();
					}
					
					toaster("Report Successful");
				}
				else
				{
					toaster("Select Reason");
				}
			}

		});
	}

	private void deleteFile(File my_photo)
	{
		my_photo.delete();
	}

	public String uploadphotousingmultipart(File file) throws Exception 
	{
        String url = host+"/forestmonitor/upload_photo.php";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
       // post.addHeader("Content-Type", "application/json");
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file,"jpg");         
        //Add the data to the multipart entity
        mpEntity.addPart("file", cbFile);
		//ExifInterface exif = new ExifInterface(file.getAbsolutePath());
        post.setEntity(mpEntity);
        HttpResponse response1 = client.execute(post);
        //Get the response from the server
        HttpEntity resEntity = response1.getEntity();
        String Response=EntityUtils.toString(resEntity);
        Log.d("Response:", Response);
        Log.d("Path:", file.getAbsolutePath());
        client.getConnectionManager().shutdown();
        return Response;
	}

	protected File takephoto()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		getOutputMediaFile(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
		return mediaFile;
	}
	
	private boolean isDeviceSupportCamera() 
	{
		if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
		{
			return true;
		}
		else 
		{
			toaster("Camera Not Supported for This Device");
			return false;
		}
	}
			
	private Uri getOutputMediaFileUri(int type) 
		{
			return Uri.fromFile(getOutputMediaFile(type));
		}

	private File getOutputMediaFile(int type)
		{
		
	       if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		          cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
		          "SpaceApp/.Cache");
		       else
		           cacheDir=MainActivity.this.getCacheDir();
		       if(!cacheDir.exists())
		           cacheDir.mkdirs();
		    
		       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		       if (type == MEDIA_TYPE_IMAGE)
		       {
		           mediaFile = new File(cacheDir.getPath() + File.separator +
		           "IMG_"+ timeStamp + ".jpg");
		       }
		       else
		       {
		           return null;
		       }
		       return mediaFile;
		}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Log.i("came", "start");
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) 
		{
			if (resultCode == RESULT_OK) 
			{
				resizeCapturedImage();
				GPSTracker gps=new GPSTracker(MainActivity.this);
				double longitude = gps.getLongitude();
				double latitude = gps.getLatitude();
				geoTag(mediaFile.getAbsolutePath(),latitude,longitude);
			}  		    
		}
		else if (resultCode == RESULT_CANCELED) 
		{
			toaster("User cancelled image capture");
		}
		else 
		{
			toaster("Sorry! Failed to capture image");
		}
	}

	protected void toaster(String string)
	{
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}

	private void resizeCapturedImage() 
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),options);
		bitmap=getResizedBitmap(bitmap);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
		File f = new File(fileUri.getPath());
		try 
		{
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
			
	public Bitmap getResizedBitmap(Bitmap bm)
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		int newWidth=1000;
		int newHeight=1000*height/width;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}
		
	private void geoTag(String absolutePath, double latitude, double longitude) 
	{
		ExifInterface exif;
		try 
		{
			exif = new ExifInterface(absolutePath);
			int num1Lat = (int)Math.floor(latitude);
			int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
			double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;
			int num1Lon = (int)Math.floor(longitude);
			int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
			double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");
			if (latitude > 0) 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
			}
			else	 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
			}
			
			if (longitude > 0) 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
			}
			else 
			{
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
			}
			exif.saveAttributes();
		} 
		catch (IOException e) 
		{
			Log.e("PictureActivity", e.getLocalizedMessage());
		} 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) 
	{
		String[] regionsArray1 = getResources().getStringArray(R.array.deforestation_reason);
		ArrayList<String> deforestation_reason = new ArrayList<String>(Arrays.asList(regionsArray1));
		String[] regionsArray = getResources().getStringArray(R.array.fire_reason);
		ArrayList<String> fire_reason = new ArrayList<String>(Arrays.asList(regionsArray));

		if(arg0.equals(spinner1))
		{
			report_type=(String) spinner1.getSelectedItem();

			spinner2.setEnabled(true);
			if(spinner1.getSelectedItemPosition()==0)
			{
                ArrayAdapter <String> s1 = new ArrayAdapter <String> (this,android.R.layout.simple_spinner_item,deforestation_reason);
                s1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(s1);
			}
			else if(spinner1.getSelectedItemPosition()==1)
			{
                ArrayAdapter <String> s2 = new ArrayAdapter <String> (this,android.R.layout.simple_spinner_item,fire_reason);
                s2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(s2);

			}
			
		}
		
		if(arg0.equals(spinner2))
		{
			report_reason=(String) spinner2.getSelectedItem();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		if(arg0.equals(spinner1))
		{
			report_type="";
		}
		if(arg0.equals(spinner2))
		{
			report_reason="";
		}
		
	}
}
