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

public class HowToMenuActivity extends Activity {
	Button buttonType, buttonReasons, buttonPrevent, buttonDefense, buttonProtect, buttonImpact;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto_menu);
        this.setTitle(R.string.tips_main_text);
        
        buttonType = (Button) findViewById(R.id.buttonType);
        buttonReasons = (Button) findViewById(R.id.buttonReasons);
        buttonPrevent = (Button) findViewById(R.id.buttonPrevent);
        buttonDefense = (Button) findViewById(R.id.buttonDefense);
        buttonProtect = (Button) findViewById(R.id.buttonProtect);
        buttonImpact = (Button) findViewById(R.id.buttonImpact);
        
        buttonType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HowToMenuActivity.this, HowToActivity.class);
				intent.putExtra("attack", "Types of attacks");
				startActivity(intent);
			}
		});
        
        buttonReasons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HowToMenuActivity.this, HowToActivity.class);
				intent.putExtra("attack", "Reasons for attacks");
				startActivity(intent);
			}
		});
        
        buttonPrevent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HowToMenuActivity.this, HowToActivity.class);
				intent.putExtra("attack", "Prevention");
				startActivity(intent);
			}
		});
        
        buttonDefense.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HowToMenuActivity.this, HowToActivity.class);
				intent.putExtra("attack", "Self-defense");
				startActivity(intent);
			}
		});
        
        buttonProtect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HowToMenuActivity.this, HowToActivity.class);
				intent.putExtra("attack", "Dolphins Protection Myth");
				startActivity(intent);
			}
		});
        
        buttonImpact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HowToMenuActivity.this, HowToActivity.class);
				intent.putExtra("attack", "Media Myths and Impacts");
				startActivity(intent);
			}
		});

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Tips");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
	}
}
