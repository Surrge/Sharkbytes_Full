package com.vie.sharkbytes;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class HowToActivity extends Activity {
	LinearLayout howtoLayout;
	ProgressDialog spinner;
	ArrayList<GetInfoTask> jsonTasks = new ArrayList<GetInfoTask>();
	String activityCategoryStr;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto);
        this.setTitle("Avoiding Shark Attacks");
        
        Bundle extras = getIntent().getExtras();
        activityCategoryStr = extras.getString("attack");
        
        howtoLayout = (LinearLayout) findViewById(R.id.howtoLayout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Show Spinner
        spinner = new ProgressDialog(this);
		spinner.setMessage("Loading Data...");
		spinner.show();
		
		//Get json data from server
		howtoLayout.removeAllViews();
        jsonTasks.add((GetInfoTask) new GetInfoTask(this).execute("getHowTo", ""));
	}
	
	@Override
	protected void onPause() {
		//Cancel threads while reference is valid
		for(GetInfoTask t: jsonTasks) {t.cancel(true);}
		jsonTasks.clear();
		
		super.onPause();
	}
	
	public void onTaskFinish(GetInfoTask task, String data) {
		jsonTasks.remove(task);
		
		try {
			JSONArray json = new JSONArray(data);
			for(int i = 0; i < json.length(); i++) {
				if(json.getJSONObject(i).getString("attack").contentEquals(activityCategoryStr)) {
					//Add tile
					LayoutInflater inflater = LayoutInflater.from(this);
					View view = inflater.inflate(R.layout.howto_tile, null);
					TextView titleText = (TextView) view.findViewById(R.id.textTitle);
					titleText.setText(json.getJSONObject(i).getString("attack"));
					WebView bodyWeb = (WebView) view.findViewById(R.id.webBody);
					bodyWeb.loadData(Html.fromHtml(json.getJSONObject(i).getString("detail")).toString(), "text/html", "UTF-8");
					//fix flicker bug
					bodyWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
					
					howtoLayout.addView(view);
					howtoLayout.refreshDrawableState();

                    // GoogleAnalytics, log screen and view
                    if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
                        Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
                        t.setScreenName("Avoiding Attacks - " + activityCategoryStr);
                        t.send(new HitBuilders.AppViewBuilder().build());
                    }
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Problem retrieving data", Toast.LENGTH_LONG).show();
		}
		
		//Dismiss Spinner if done
		if(jsonTasks.isEmpty() && spinner != null && spinner.isShowing()) {
			spinner.dismiss();
		}
	}
}
