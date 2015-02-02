package com.example.followmehome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.Toast;

public class MainScreenActivity extends Activity implements OnMapReadyCallback, LocationListener{
	
	private static String cellPhoneNumber = null;
	private static String homePhoneNumer = null; 
	public static String sout = "sout";
	Handler uiThreadHandler;
	
	private static LocationManager locationManager;
	private static Location lastKnownLocation;
	private static String locationProvider = LocationManager.GPS_PROVIDER;
	private static LatLng globalLatLng = null;
	private static LatLng userPrefrencesLatLng = null;
	
	private static MapFragment mapFragment;
	
	private static boolean cellNumberStatus = false;
	private static boolean homeNumberStatus = false;
	
	
	private final String userPrefrencesFileName = "userPrefrences";
	private static boolean callBeenMadeApprochingHome = false;
	private static boolean callBeenMadeLeavingHome = false;
	
	private enum NumberValidationType {
		CELL,
		HOME
	}
	
	private enum Provider {
		_012,
		ORANGE,
		PELEPHONE,
		CELLCOM,
		GOLAN,
		HOTMOBILE
	}
	
	private Provider provider;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapHolder);
        
        try{
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            setGeolocationListener();
            setProviderListener();
            addEditTextListeners();
        } catch (Exception e){
        	Log.d(sout, "got here");
        }
      

    }
	
	
	private void setProviderListener(){
		Spinner providerSpinner = (Spinner)findViewById(R.id.providerArraySpinner);
		providerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
				// Get the item selected from the Spinner (represented as the parent variable) at "position"
				String val = parent.getItemAtPosition(position).toString();
				
				switch (val){
					case "012":
						provider = Provider._012;
						break;					
					case "Pelephone":
						provider = Provider.PELEPHONE;
						break;
					case "Cellcom":
						provider = Provider.CELLCOM;
						break;	
					case "Orange":
						provider = Provider.ORANGE;
						break;
					case "Golan Telecom":
						provider = Provider.GOLAN;
						break;
					case "HOT Mobile":
						provider = Provider.HOTMOBILE;
						break;
				}
				Log.d(sout, "The item selected is: "+val);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast errorMessege = Toast.makeText(getApplicationContext(), "No provider selected. \nPlease choose a provider", Toast.LENGTH_SHORT);
				errorMessege.show();
			}
		});
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
		
		if (number.length() != 9){
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
        		userPrefrencesLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());        		
        	}
        }

		mapFragment.getMapAsync(this);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if (globalLatLng != null){
			mapFragment.getMapAsync(this);
		}
		Log.d(sout,"started");
	}
		
		
	

    @Override
    public void onMapReady(GoogleMap map) {
    	// LatLng currLatLng = globalLatLng;
    	LatLng currLatLng = userPrefrencesLatLng;

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 14));

        map.addMarker(new MarkerOptions()
                .title("Your location")
                .snippet("We'll use this location as home")
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
		    	
		    	// Consider if this is the best place for this piece of code (onCreate/onResume)
		    	Location userHomeLocation = new Location("");
		    	userHomeLocation.setLatitude(userPrefrencesLatLng.latitude);
		    	userHomeLocation.setLongitude(userPrefrencesLatLng.longitude);
		    	
		    	if ( (userHomeLocation.distanceTo(location) < 40) && (!callBeenMadeApprochingHome) ){
		    		// Means the distance between our current-location and house-location is less then 40meters away
		    		// that means we need to forward the calls from our cell -> home
		    		callBeenMadeLeavingHome = false;
		    		callBeenMadeApprochingHome = true;
		    		
		    		// Create a new ACTION_DIAL intent, initialize the number according to the relevant provider and fire away the intent
		    		Intent forwardCellToHome = new Intent(Intent.ACTION_CALL);
		    		forwardCellToHome.setData(Uri.parse("tel:"+buildForwardingNumberCellToHome(homePhoneNumer)));
		    		startActivity(forwardCellToHome);
		    	} else if ( (userHomeLocation.distanceTo(location) > 39) && (!callBeenMadeLeavingHome) ){
		    		callBeenMadeLeavingHome = true;
		    		callBeenMadeApprochingHome = false;
		    		Intent forwardCellToHome = new Intent(Intent.ACTION_CALL);
		    		forwardCellToHome.setData(Uri.parse("tel:"+buildCancelForwardingNumber()));
		    		startActivity(forwardCellToHome);
		    	}
		    	
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		  };
	    
		// Request updates with parameters of min time: 30 seconds and min distance 25 meters  
	    locationManager.requestLocationUpdates(locationProvider, 30000, 25, locationListener);	
	}
	
	
	private String buildCancelForwardingNumber(){
		
		String retval = null;
		switch(provider){
			case _012:
				retval = "#21#";
				break;
			case GOLAN:
				// Not support at this point
				break;
			case ORANGE:
				retval = "#21#";
				break;
			case PELEPHONE:
				// requires extra work - listening on the line and hanging up by the app is needed 
				retval = "*730";
				break;
			case CELLCOM:
				retval = "#21#";
				break;
			case HOTMOBILE:
				// requires extra work - listening on the line and hanging up by the app is needed
				retval = "#72";
		}
		return retval;
	}
	
	private String buildForwardingNumberCellToHome(String baseNumber){
		
		String retval = null;
		switch(provider){
			case _012:
				retval = "*21*"+baseNumber+"#";
				break;
			case GOLAN:
				// Not support at this point
				break;
			case ORANGE:
				retval = "*21*"+baseNumber+"#";
				break;
			case PELEPHONE:
				// Needs extra work, need to wait 2-3 seconds and hang up
				retval = "*73"+baseNumber;
				break;
			case CELLCOM:
				retval = "*21*"+baseNumber+"#";
				break;
			case HOTMOBILE:
				retval = "*21*"+baseNumber+"#";
		}
		return retval;
	}
	
	public void updatePrefrences(View view){
		
		EditText cellNumEditText = (EditText)findViewById(R.id.cellPhoneNumberEditArea);
		String cellNumString = cellNumEditText.getText().toString();
		cellNumString = cellNumString + "\n";

		EditText homeNumEditText = (EditText)findViewById(R.id.homeNumberEditArea);
		String homeNumString = homeNumEditText.getText().toString();
		homeNumString = homeNumString+"\n";

		String lat = String.valueOf(userPrefrencesLatLng.latitude);
		lat = lat+"\n";
		String longtitude = String.valueOf(userPrefrencesLatLng.longitude);
		longtitude = longtitude+"\n";

		try{
			FileOutputStream fos = openFileOutput(userPrefrencesFileName, Context.MODE_PRIVATE);
			
			fos.write(lat.getBytes());
			fos.write(longtitude.getBytes());
			fos.write(cellNumString.getBytes());
			fos.write(homeNumString.getBytes());
				
			switch (provider){
				case _012:
					fos.write("012".getBytes());
					break;					
				case PELEPHONE:
					fos.write("Pelephone".getBytes());
					break;
				case CELLCOM:
					fos.write("Cellcom".getBytes());
					break;	
				case ORANGE:
					fos.write("Orange".getBytes());
					break;
				case GOLAN:
					fos.write("Golan".getBytes());
					break;
				case HOTMOBILE:
					fos.write("HOT Mobile".getBytes());
					break;
			}
			fos.close();
		} catch (Exception e){
				
			}
	}
		
	@Override
	public void onLocationChanged(Location location) {
        String str = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();	
        Log.d(sout, "loc changed");
	}


	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getBaseContext(), "GPS turned off\nRemeber! FollowMeHome needs The GPS enabled ", Toast.LENGTH_LONG).show();
		Log.d(sout, "disabled");
	}


	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getBaseContext(), "GPS turned on ", Toast.LENGTH_LONG).show();
		Log.d(sout, "enabled");
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(sout, "changed");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showData();
		

	}
	
	public void showData(){
		
		/* Remember: the text file structure is this:
		 * Latitute
		 * Longtitude (alt)
		 * Cell num
		 * Home num
		 * provider
		 */
		File file = getBaseContext().getFileStreamPath(userPrefrencesFileName);
		if (file != null){ // Verify we have a file
			Log.d(sout,"userPrefrences file is fine");
			try {
			    BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput("userPrefrences")));		      		    
			    handleGeoLocationRead(inputReader);
			    handleCellPhoneRead(inputReader);
			    handleHomePhoneRead(inputReader);    
			    handleSpinnerLoad(inputReader);    
			} catch (IOException e) {
			    e.printStackTrace();
			}
		} else{
			Log.d(sout,"userPrefrences file does not exist");
		}
	}
	
	private void handleHomePhoneRead(BufferedReader inputReader){
		String inputString;
		try{
		    inputString = inputReader.readLine();
		    EditText homeNum = (EditText)findViewById(R.id.homeNumberEditArea);
		    homeNum.setText(inputString.toString());
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private void handleCellPhoneRead(BufferedReader inputReader){
		String inputString;
		try{		
			inputString = inputReader.readLine();
			EditText cellPhoneNum = (EditText)findViewById(R.id.cellPhoneNumberEditArea);
			cellPhoneNum.setText(inputString.toString());
		} catch (IOException e){
			//TO-DO
		}
	}
	
	private void handleGeoLocationRead(BufferedReader inputReader){
		
		String inputString;
		try{
			inputString = inputReader.readLine();
		    double lat = Double.parseDouble(inputString.toString());
		    inputString = inputReader.readLine();
		    double longtitue = Double.parseDouble(inputString.toString());
		    
		    userPrefrencesLatLng = new LatLng(lat,longtitue);
		    mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapHolder);
		    mapFragment.getMapAsync(this);
		} catch (IOException e){
			e.printStackTrace();
		}
	    
	}
	private void handleSpinnerLoad(BufferedReader inputReader){
		String value = null;
		try{
			value = inputReader.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		Spinner spinnerProvider = (Spinner)findViewById(R.id.providerArraySpinner);
	    switch (value) {
		case "Orange":
			spinnerProvider.setSelection(0);
			provider = Provider.ORANGE;
			break;

		case "012":
			spinnerProvider.setSelection(1);
			provider = Provider._012;
			break;
			
		case "Cellcom":
			spinnerProvider.setSelection(2);
			provider = Provider.CELLCOM;
			break;
			
		case "Pelephone":
			spinnerProvider.setSelection(3);
			provider = Provider.PELEPHONE;
			break;
			
		case "Golan":
			spinnerProvider.setSelection(4);
			provider = Provider.GOLAN;
			break;
			
		case "HOT Mobile":
			spinnerProvider.setSelection(5);
			provider = Provider.HOTMOBILE;
			break;
		}
	}

}
