package com.example.followmehome;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends Activity implements OnMapReadyCallback, LocationListener{
	
	private static String cellPhoneNumber = null;
	private static String homePhoneNumer = null; 
	public static String sout = "sout";
	Handler uiThreadHandler;
	
	private static LocationManager locationManager;
	private static LocationListener locationListener;
	private static Location lastKnownLocation;
	private static String locationProvider = LocationManager.GPS_PROVIDER;
	private static LatLng globalLatLng = null;

	private static MapFragment mapFragment;
	
	private static boolean cellNumberStatus = false;
	private static boolean homeNumberStatus = false;
	
	private enum NumberValidationType {
		CELL,
		HOME
	}
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapHolder);
             
       
        try{
        	uiThreadHandler = new Handler();
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception e){
        	Log.d(sout, "got here");
        }
      
        setGeolocationListener();
        addEditTextListeners();
    }
	
	
	private void addEditTextListeners() {
		
		/* This function will take care of adding change listeners for the following:
		 * 1. Cell phone number edit text area
		 * 2. Home phone number edit text area
		 */
		
		EditText cellPhoneEditText = (EditText)findViewById(R.id.cellPhoneNumberEditArea);
		setEditTextListener(cellPhoneEditText, "Invalid cell number entered, please check the number again", NumberValidationType.CELL);
		
		EditText homePhoneEditText = (EditText)findViewById(R.id.homeNumberEditArea);
		setEditTextListener(homePhoneEditText, "Invalid home number entered, please check  the number again", NumberValidationType.HOME);
	}
	
	private void setEditTextListener(View editTextview, final String toastMessege, final NumberValidationType type){
		
		editTextview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				Button goBtn = (Button)findViewById(R.id.goButton);
				boolean res = false;
				
				if (!hasFocus){
					switch(type){
						case CELL:
							res = validateCellNumber();
							cellNumberStatus = res;
							break;
						case HOME:
							res = validateHomeNumber();
							homeNumberStatus = res;
							break;
					}
					
					// the user has zoomed out, we can check the data inserted
					if (res == false){
						// illegal number was inserted
						// Set the toast with the custom message toastMessege and display it
						Toast invalidInfo = Toast.makeText(getApplicationContext(), toastMessege , Toast.LENGTH_LONG);
						invalidInfo.show();
					
						// Disable goBtn click and view
						goBtn.setClickable(false);
						goBtn.setEnabled(false);
						Log.d(sout,"invalid");
					} else if (homeNumberStatus && cellNumberStatus){
						// legal number was inserted and also "partner" cell/home number is legal, enable the goBtn
						goBtn.setClickable(true);
						goBtn.setEnabled(true);
						Log.d(sout,"valid");
					}
				}
				
			}
		});
	}
	
	private boolean validateCellNumber(){
		EditText numberEditText = (EditText)findViewById(R.id.cellPhoneNumberEditArea);
		String number = numberEditText.getText().toString();
		
		if (number.length() != 10){
			return false;
		} else if (number.charAt(0) != '0'){
			return false;
		} else{
			// consider adding more test-cases here
			return true;
		}
		
	}
	
	private boolean validateHomeNumber(){
		EditText numberEditText = (EditText)findViewById(R.id.homeNumberEditArea);
		String number = numberEditText.getText().toString();
		
		if (number.length() != 10){
			return false;
		} else if (number.charAt(0) != '0'){
			return false;
		} else{
			// consider adding more test-cases here
			return true;
		}
	}

	public void onSyncBtnClicked(View v){
		
		Log.d(sout, "got here");
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if (lastKnownLocation != null){
        	// We found GPS signal
        	globalLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } else{
        	// Try to find network signal
        	lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	if (lastKnownLocation != null){
        		globalLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());        		
        	}
        }

		mapFragment.getMapAsync(this);
		
		
	}

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng currLatLng = globalLatLng;

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 14));

        map.addMarker(new MarkerOptions()
                .title("Your location")
                .snippet("This is the location that will be used to calculate your radius")
                .position(currLatLng));
    }
	

	public void setGeolocationListener(){
		// This function 
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Log.d(sout,"got here");
		
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	try{
					double latitude = location.getLatitude();
					double longtitude = location.getLongitude();
					Log.d(sout, "lat is: "+String.valueOf(latitude)+"  long is: "+String.valueOf(longtitude));
					globalLatLng = new LatLng(latitude, longtitude);
		    	} catch (Exception e){
		    		// to-handle
		    	}
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		  };
	    
		// Request updates with parameters of min time: 30 seconds and min distance 25 meters  
	    locationManager.requestLocationUpdates(locationProvider, 30000, 25, locationListener);
	    locationManager.requestLocationUpdates(locationProvider, 30000, 25, locationListener);
	
	}
	
	public void updatePrefrences(View view){
		
		final ProgressDialog progressDialog = ProgressDialog.show(
				this,
				"Updating your settings!",
				"This will only take a moment");
		
		
		// open up a new thread to make the work
		Thread processThread = new Thread(){
			// this thread will handle all computation that needs to be done outside the UI realm
			public void run(){
				updateValues();
				
				
				// somewhere inside here we need to pass control to the Handler so that the UI thread releases the ProgressDialog
				uiThreadHandler.post(new Runnable() {
					
					@Override
					public void run() {
						progressDialog.dismiss();
					}
				});
			}
		};
		processThread.start();
		
		/* do stuff here
		 
		  
		*/
		
		
		progressDialog.dismiss();
	}
	
	private void updateValues(){
		// to be implemented 
	}


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
        String str = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();	
        Log.d(sout, "loc changed");
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "GPS turned off\nRemeber! FollowMeHome needs The GPS enabled ", Toast.LENGTH_LONG).show();
		Log.d(sout, "disabled");
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "GPS turned on ", Toast.LENGTH_LONG).show();
		Log.d(sout, "enabled");
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d(sout, "changed");
	}

}

