package com.vie.sharkbytes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class PuzzleActivity extends Activity {
	WebView puzzleView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        this.setTitle("Shark Puzzle");
        
        puzzleView = (WebView) findViewById(R.id.puzzleWebView);
        puzzleView.clearCache(true);
        puzzleView.setBackgroundColor(getResources().getColor(R.color.white_trans));
        
        WebSettings webSettings = puzzleView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Puzzle Screen");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
        
        puzzleView.loadUrl("http://www.sharkbytes.co/puzzle.php");
	}
}
