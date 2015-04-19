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
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SaveActivity extends Activity {
	LinearLayout saveLayout;
	ProgressDialog spinner;
	ArrayList<GetInfoTask> jsonTasks = new ArrayList<GetInfoTask>();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        this.setTitle("Save The Shark");
        
        saveLayout = (LinearLayout) findViewById(R.id.saveLayout);

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Save Shark Screen");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Show Spinner
        spinner = new ProgressDialog(this);
		spinner.setMessage("Loading Data...");
		spinner.show();
		
		//Get json data from server
		saveLayout.removeAllViews();
        jsonTasks.add((GetInfoTask) new GetInfoTask(this).execute("getSave", ""));
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
				//Add tile
				LayoutInflater inflater = LayoutInflater.from(this);
				View view = inflater.inflate(R.layout.save_tile, null);
				WebView saveWeb = (WebView) view.findViewById(R.id.webSave);
				saveWeb.loadData(Html.fromHtml(json.getJSONObject(i).getString("save")).toString(), "text/html", "UTF-8");
				//fix flicker bug
				saveWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				
				saveLayout.addView(view);
				saveLayout.refreshDrawableState();
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
