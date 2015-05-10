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

public class SaveActivity extends Activity {
	WebView saveView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        this.setTitle(R.string.save_main_text);

		saveView = (WebView) findViewById(R.id.saveWebView);
		saveView.clearCache(true);
		saveView.setBackgroundColor(getResources().getColor(R.color.white_trans));

		WebSettings webSettings = saveView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		if (Build.VERSION.SDK_INT >= 11){
			saveView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Save Sharks");
            t.send(new HitBuilders.AppViewBuilder().build());
        }

		saveView.loadUrl("http://www.sharkbytes.co/mobilesavesharks.php");
	}
}
