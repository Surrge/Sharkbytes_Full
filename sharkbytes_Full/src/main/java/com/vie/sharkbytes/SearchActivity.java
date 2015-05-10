package com.vie.sharkbytes;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SearchActivity extends Activity {
	WebView searchBeachView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.AppBaseTheme);   //ignore app style, bad text color
        setContentView(R.layout.activity_search);
        this.setTitle(R.string.search_beach_main_text);

        searchBeachView = (WebView) findViewById(R.id.searchBeachView);
        searchBeachView.clearCache(true);
        searchBeachView.setBackgroundColor(getResources().getColor(R.color.white_trans));
        searchBeachView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings webSettings = searchBeachView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= 11){
            searchBeachView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Search Your Beach");
            t.send(new HitBuilders.AppViewBuilder().build());
        }

        searchBeachView.loadUrl("http://www.sharkbytes.co/searchyourbeach.php?lat=" + lastKnownLocation.getLatitude() + "&lon=" + lastKnownLocation.getLongitude());
	}
}
