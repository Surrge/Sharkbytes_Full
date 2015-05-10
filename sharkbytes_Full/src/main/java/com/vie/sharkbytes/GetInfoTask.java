package com.vie.sharkbytes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class GetInfoTask extends AsyncTask<String, Void, String>{
	public enum SourceType {
		getTypes,
		getTypeDetail,
		getFacts,
		getAttacks,
		getHowTo,
		getWallpaper
	}
	SourceType type;
	Activity parent;
	
	GetInfoTask(Activity parent) {
		this.parent = parent;
	}
	
	@Override
	protected String doInBackground(String... params) {
		type = SourceType.valueOf(params[0]);
		String extra = params[1];
		URI serviceLoc = getServiceLoc(type, extra);
		
		if(this.isCancelled())
			return "";
		else {
			try {
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
				HttpConnectionParams.setSoTimeout(httpParams, 5000);
				HttpClient httpClient = new DefaultHttpClient(httpParams);
				HttpGet httpget = new HttpGet(serviceLoc);
				HttpResponse response = httpClient.execute(httpget);
				HttpEntity entity = response.getEntity();			
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				
				return sb.toString();
			}
			catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}
	}
	
	@Override
	protected void onPostExecute(String data) {		
		if(!(this.isCancelled())){
			//Tell parent
			switch(type){
			case getTypes:
				((TypeActivity) parent).onTaskFinish(this, data);
				break;
			case getTypeDetail:
				((TypeDetailActivity) parent).onTaskFinish(this, data);
				break;
			case getFacts:
				((FactActivity) parent).onTaskFinish(this, data);
				break;
			case getAttacks:
				((AttackActivity) parent).onTaskFinish(this, data);
				break;
			case getHowTo:
				((HowToActivity) parent).onTaskFinish(this, data);
				break;
			case getWallpaper:
				((WallpaperActivity) parent).onTaskFinish(this, data);
				break;
			default:
				break;
			}
		}
	}
	
	private URI getServiceLoc(SourceType type, String extra) {
		String str = "";
		
		switch(type) {
		case getTypes:
			str = "http://sharkbytes.co/types.php";
			break;
		case getTypeDetail:
			str = "http://sharkbytes.co/types.php?type=" + extra;
			break;
		case getFacts:
			str = "http://sharkbytes.co/facts.php";
			break;
		case getAttacks:
			str = "http://sharkbytes.co/recent.php";
			break;
		case getHowTo:
			str = "http://sharkbytes.co/attacks.php";
			break;
		case getWallpaper:
			str = "http://sharkbytes.co/gallery_imagelist.php";
			break;
		default:
			str = "";
			break;
		}
		
		try {
			URL url = new URL(str);
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
