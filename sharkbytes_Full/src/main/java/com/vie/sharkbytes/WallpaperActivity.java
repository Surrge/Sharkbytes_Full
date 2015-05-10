package com.vie.sharkbytes;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class WallpaperActivity extends Activity {
    Tracker googleAnalyticsTracker;
	GridView gridView;
	ProgressDialog spinner;
	ArrayList<GetInfoTask> jsonTasks = new ArrayList<GetInfoTask>();
	ArrayList<GetBitmapTask> downloadTasks = new ArrayList<GetBitmapTask>();
	ArrayList<WallpaperButton> buttonList = new ArrayList<WallpaperButton>();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        this.setTitle(R.string.wallpaper_main_text);
        
        gridView = (GridView) findViewById(R.id.gridview);

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            googleAnalyticsTracker = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            googleAnalyticsTracker.setScreenName("Wallpapers");
            googleAnalyticsTracker.send(new HitBuilders.AppViewBuilder().build());
        }
        else {
            googleAnalyticsTracker = null;
        }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//Show Spinner
        spinner = new ProgressDialog(this);
		spinner.setMessage("Loading Wallpapers...");
		spinner.show();
		
		//Get json data from server
        jsonTasks.add((GetInfoTask) new GetInfoTask(this).execute("getWallpaper", ""));
	}
	
	@Override
	protected void onStop() {
		//Cancel threads while reference is valid
		for(GetInfoTask t: jsonTasks) {t.cancel(true);}
		for(GetBitmapTask t: downloadTasks) {t.cancel(true);}
		jsonTasks.clear();		
		downloadTasks.clear();
		
		super.onStop();
	}
	
	public void onTaskFinish(GetInfoTask task, String data) {
		jsonTasks.remove(task);
		
		try {
			JSONArray json = new JSONArray(data);
			for(int i = 0; i < json.length(); i++) {
				WallpaperButton current = new WallpaperButton(this);
				current.setBackgroundColor(this.getResources().getColor(R.color.black_trans));
				current.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(final View v) {
						new AlertDialog.Builder(WallpaperActivity.this)
			    		.setTitle("Save?")
			    		.setMessage("Save Image To Gallery?")
			    		.setPositiveButton("Save", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
                                // GoogleAnalytics, log user selection
                                if(googleAnalyticsTracker != null) {
                                    googleAnalyticsTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Wallpaper Screen - Download")
                                            .setAction("Target: " + v.getTag())
                                            .build());
                                }

                                Toast.makeText(WallpaperActivity.this, "Downloading Hi-Res Image", Toast.LENGTH_SHORT).show();
                                new GetBitmapTask(WallpaperActivity.this, null).execute((String) v.getTag());
							}
						})
						.setNegativeButton("Cancle", null)
						.show();
					}
				});
				//Keep square
				current.setAdjustViewBounds(true);
				current.setScaleType(ScaleType.FIT_XY);
				current.setTag(json.getJSONObject(i).getString("image"));
				
				buttonList.add(current);				
				downloadTasks.add((GetBitmapTask) new GetBitmapTask(this, current, 110, 110).execute(json.getJSONObject(i).getString("image")));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Problem retrieving data", Toast.LENGTH_LONG).show();
		}
		
		//Dismiss Spinner if done
		if(jsonTasks.isEmpty() && spinner != null && spinner.isShowing()) {
			spinner.dismiss();
			
			//Display button list
			gridView.setAdapter(new WallpaperAdapter(this, buttonList.size(), buttonList));
		}
	}
}
