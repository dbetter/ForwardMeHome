package com.example.followmehome;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainScreenActivity extends Activity implements OnMapReadyCallback{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapHolder);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

//	private static String cellPhoneNumber = null;
//	private static String homePhoneNumer = null; 
//	public static String sout = "sout";
//	Handler uiThreadHandler;
//	
//	private static LocationManager locationManager;
//	private static LocationListener locationListener;
//	private static Location lastKnownLocation;
//	private static String locationProvider = LocationManager.GPS_PROVIDER;
//	
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.activity_main_screen);
//		Spinner providerSpinner = (Spinner) findViewById(R.id.providerArraySpinner);
//	
//		// create a new UI thread handler
//		uiThreadHandler = new Handler(); 
//		
//		// Occupy the Spinner with service supplier's string array
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.serviceProviderArray, R.layout.support_simple_spinner_dropdown_item);
//		adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//		providerSpinner.setAdapter(adapter);
//	
////		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapHolder);
////        mapFragment.getMapAsync(this);		
//	}
//
//	public void setGeolocation (View view){
//		// This function 
//		// Acquire a reference to the system Location Manager
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		Log.d(sout,"got here");
//		
//		LocationListener locationListener = new LocationListener() {
//		    public void onLocationChanged(Location location) {
//		      // Called when a new location is found by the network location provider.
//		      //makeUseOfNewLocation(location);
//		    	try{
//					double stam1 = location.getLatitude();
//					double stam2 = location.getLongitude();
//					Log.d(sout, "lat is: "+String.valueOf(stam1)+"  long is: "+String.valueOf(stam2));
//		    	} catch (Exception e){
//		    		
//		    	}
//		    }
//
//		    public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//		    public void onProviderEnabled(String provider) {}
//
//		    public void onProviderDisabled(String provider) {}
//		  };
//	    
//	    locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
//		
////		Log.d(sout, "The altitude is:" + String.valueOf(lastKnownLocation.getAltitude()) + " \nThe longtitude is: "+String.valueOf(lastKnownLocation.getLongitude()));
//		
//	}
//	
//	public void updatePrefrences(View view){
//		
//		final ProgressDialog progressDialog = ProgressDialog.show(
//				this,
//				"Updating your settings!",
//				"This will only take a moment");
//		
//		
//		// open up a new thread to make the work
//		Thread processThread = new Thread(){
//			// this thread will handle all computation that needs to be done outside the UI realm
//			public void run(){
//				updateValues();
//				
//				
//				// somewhere inside here we need to pass control to the Handler so that the UI thread releases the ProgressDialog
//				uiThreadHandler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						progressDialog.dismiss();
//					}
//				});
//			}
//		};
//		processThread.start();
//		
//		/* do stuff here
//		 
//		  
//		*/
//		
//		
//		progressDialog.dismiss();
//	}
//	
//	private void updateValues(){
//		// to be implemented 
//	}

}

