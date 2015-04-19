package com.vie.sharkbytes;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class TypeDetailActivity extends Activity {
	LinearLayout typeLayout;
	ProgressDialog spinner;
	ArrayList<GetInfoTask> jsonTasks = new ArrayList<GetInfoTask>();
	ArrayList<GetBitmapTask> downloadTasks = new ArrayList<GetBitmapTask>();
	String type;
	ArrayList<String> details = new ArrayList<String>();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_detail);
        
        Bundle extras = getIntent().getExtras();
        type = extras.getString("type");
        this.setTitle(type + " Sharks");
        
        typeLayout = (LinearLayout) findViewById(R.id.typeDetailLayout);

        // GoogleAnalytics, log screen and view
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext()) == ConnectionResult.SUCCESS) {
            Tracker t = ((Global) getApplication()).getTracker(Global.TrackerName.APP_TRACKER);
            t.setScreenName("Shark Types - " + type);
            t.send(new HitBuilders.AppViewBuilder().build());
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Show Spinner
        spinner = new ProgressDialog(this);
		spinner.setMessage("Loading " + type + " Sharks...");
		spinner.show();
		
		//Get json data from server
		typeLayout.removeAllViews();
        jsonTasks.add((GetInfoTask) new GetInfoTask(this).execute("getTypeDetail", type));
	}
	
	@Override
	protected void onPause() {
		//Cancel threads while reference is valid
		for(GetInfoTask t: jsonTasks) {t.cancel(true);}
		for(GetBitmapTask t: downloadTasks) {t.cancel(true);}
		jsonTasks.clear();
		downloadTasks.clear();
		
		super.onPause();
	}	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void onTaskFinish(GetInfoTask task, String data) {
		jsonTasks.remove(task);
		
		//Log.d("test", "onTaskFinish data=" + data);

		try {
			JSONArray json = new JSONArray(data);
			for(int i = 0; i < json.length(); i++) {
				final int index = i;
				String detail = json.getJSONObject(i).getString("detail");
				details.add(index, Html.fromHtml(detail).toString());
				
				//Add tile
				LayoutInflater inflater = LayoutInflater.from(this);
				View view = inflater.inflate(R.layout.type_detail_tile, null);
				
				ImageView image = (ImageView) view.findViewById(R.id.image);
				image.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showDetail(index);
					}
				});
				String image_url = json.getJSONObject(i).getString("image");
				if(image_url != null && !image_url.isEmpty() && image_url != "") {
					int width = typeLayout.getWidth() - 50;
					downloadTasks.add((GetBitmapTask) new GetBitmapTask(this, image, 0, width).execute(json.getJSONObject(i).getString("image")));
				}
				else {
					image.setImageResource(R.drawable.no_image_available);
				}
				
				TextView nameText = (TextView) view.findViewById(R.id.textName);
				nameText.setText("Name: " + json.getJSONObject(i).getString("name"));
				TextView textType = (TextView) view.findViewById(R.id.textType);
				textType.setText("Type: " + json.getJSONObject(i).getString("type"));
				TextView textOrder = (TextView) view.findViewById(R.id.textOrder);
				textOrder.setText("Order: " + json.getJSONObject(i).getString("order"));
				TextView textFamily = (TextView) view.findViewById(R.id.textFamily);
				textFamily.setText("Family: " + json.getJSONObject(i).getString("family"));
				TextView textGenus = (TextView) view.findViewById(R.id.textGenus);
				textGenus.setText("Genus: " + json.getJSONObject(i).getString("genus"));
				
				typeLayout.addView(view);
				typeLayout.refreshDrawableState();
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

	protected void showDetail(int i) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(details.get(i));
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
		dialog.show();
	}
}
