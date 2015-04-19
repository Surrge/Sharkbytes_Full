package com.vie.sharkbytes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.kskkbys.rate.RateThisApp;

public class MenuActivity extends Activity {
	ImageView buttonSearch;
	Button buttonType, buttonFacts, buttonAttacks, buttonHowTo, buttonSave, buttonWallpaper, buttonPuzzle;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        buttonType = (Button) findViewById(R.id.buttonType);
        buttonFacts = (Button) findViewById(R.id.buttonFacts);
        buttonAttacks = (Button) findViewById(R.id.buttonAttacks);
        buttonSearch = (ImageView) findViewById(R.id.buttonSearch);
        buttonHowTo = (Button) findViewById(R.id.buttonHowTo);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonWallpaper = (Button) findViewById(R.id.buttonWallpaper);
        buttonPuzzle = (Button) findViewById(R.id.buttonPuzzle);
        
        buttonType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, TypeActivity.class));
			}
		});
        
        buttonFacts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, FactActivity.class));
			}
		});
        
        buttonAttacks.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, AttackActivity.class));
			}
		});
        
        buttonSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, SearchActivity.class));
			}
		});
        
        buttonHowTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, HowToMenuActivity.class));
			}
		});
        
        buttonSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, SaveActivity.class));
			}
		});
        
        buttonWallpaper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, WallpaperActivity.class));
			}
		});
        
        buttonPuzzle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MenuActivity.this, PuzzleActivity.class));
			}
		});

        // GoogleAnalytics, log screen and view
        int playServicesFlag = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext());
        if(playServicesFlag == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Main Menu");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(playServicesFlag, this, 0);
        }

        // Init RatingDialog logic, possibly display
        RateThisApp.onStart(this);
        RateThisApp.showRateDialogIfNeeded(this);
	}
}
