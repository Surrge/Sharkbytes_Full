package com.vie.sharkbytes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        this.setTitle(R.string.puzzle_main_text);
        
        puzzleView = (WebView) findViewById(R.id.puzzleWebView);
        puzzleView.clearCache(true);
        puzzleView.setBackgroundColor(getResources().getColor(R.color.white_trans));
        puzzleView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        
        WebSettings webSettings = puzzleView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= 11){
            puzzleView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Puzzle");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
        
        puzzleView.loadUrl("http://www.sharkbytes.co/mobilechoosepuzzle.php");
	}
}
