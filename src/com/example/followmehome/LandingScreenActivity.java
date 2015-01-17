package com.example.followmehome;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;


public class LandingScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_screen);
		addOnStartBtnClickListener();
		addOnInfoBtnClickedListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landing_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addOnStartBtnClickListener(){
		ImageView letsGoImageView = (ImageView)findViewById(R.id.letsGoBtn);
		letsGoImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent mainApp = new Intent(LandingScreenActivity.this, MainScreenActivity.class);
				startActivity(mainApp);				
			}
		});
	}
	
	private void addOnInfoBtnClickedListener(){
		ImageView infoImageView = (ImageView)findViewById(R.id.infoPhoto);
		infoImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = layoutInflater.inflate(R.layout.infopopup, null);
				final PopupWindow popupWin = new PopupWindow(popupView, 600, 700, true);
			
				popupWin.showAtLocation(popupView, Gravity.CENTER, 0, 0);
			}
		});
	}
}


