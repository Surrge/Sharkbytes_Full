package com.vie.sharkbytes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class TypeActivity extends Activity {
	LinearLayout typeLayout;
	ProgressDialog spinner;
	ArrayList<GetInfoTask> jsonTasks = new ArrayList<GetInfoTask>();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        this.setTitle(R.string.identification_main_text);
        
        typeLayout = (LinearLayout) findViewById(R.id.typeLayout);

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Shark Identification");
            t.send(new HitBuilders.AppViewBuilder().build());
        }
	}

	public void onStart() {
		super.onStart();



		if(((Global) getApplication()).EmptySharkTypesCache()) {
			//Show Spinner
			spinner = new ProgressDialog(this);
			spinner.setMessage("Loading Sharks...");
			spinner.setCancelable(false);
			spinner.show();

			//Get json data from server
			jsonTasks.add((GetInfoTask) new GetInfoTask(this).execute("getTypes", ""));
		} else if (typeLayout.getChildCount() == 0){
			//Load data from cache, should never happen
			List<String> SharkTypes = ((Global) getApplication()).GetCachedSharkTypes();

			for(int i = 0; i < SharkTypes.size(); i++) {
				final String currentType = SharkTypes.get(i);

				LayoutInflater inflater = LayoutInflater.from(this);
				View view = inflater.inflate(R.layout.type_tile, null);

				TextView textType = (TextView) view.findViewById(R.id.textType);
				textType.setText(currentType);
				//OnClick, get details
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(TypeActivity.this, TypeDetailActivity.class);
						intent.putExtra("type", currentType);
						startActivity(intent);
					}
				});

				typeLayout.addView(view);
				typeLayout.refreshDrawableState();
			}

			//Dismiss Spinner
			if(spinner != null && spinner.isShowing()) {
				spinner.dismiss();
			}
		}
	}
	
	@Override
	public void onStop() {
		//Cancel threads while reference is valid
		for(GetInfoTask t: jsonTasks) {t.cancel(true);}
		jsonTasks.clear();
				
		super.onStop();
	}
	
	public void onTaskFinish(GetInfoTask task, String data) {
		jsonTasks.remove(task);
		
		//Log.d("test", "onTaskFinish data=" + data);
		
		try {
			//Format JSON
			ArrayList<String> formatted_data = new ArrayList<String>();
			JSONArray json = new JSONArray(data);
			for(int i = 0; i < json.length(); i++) {
				formatted_data.add(json.getJSONObject(i).getString("types"));
			}
			
			Collections.sort(formatted_data);
			
			//Add tile
			for(int i = 0; i < formatted_data.size(); i++) {
				final String currentType = formatted_data.get(i);
				
				LayoutInflater inflater = LayoutInflater.from(this);
				View view = inflater.inflate(R.layout.type_tile, null);
				
				TextView textType = (TextView) view.findViewById(R.id.textType);
				textType.setText(currentType);
				//OnClick, get details
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(TypeActivity.this, TypeDetailActivity.class);
						intent.putExtra("type", currentType);
						startActivity(intent);
					}
				});
				
				typeLayout.addView(view);
				typeLayout.refreshDrawableState();
			}

			//Save to cache
			((Global)getApplication()).SetCachedSharkTypes(formatted_data);
		}
		catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Problem retrieving data", Toast.LENGTH_LONG).show();
		}
		
		//Dismiss Spinner
		if(jsonTasks.isEmpty() && spinner != null && spinner.isShowing()) {
			spinner.dismiss();
		}
	}
}
