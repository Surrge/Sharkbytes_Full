package com.vie.sharkbytes;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class WallpaperAdapter extends BaseAdapter {
	Context context;
	int count;
	ArrayList<WallpaperButton> buttons;
	
	public WallpaperAdapter(Context context, int count, ArrayList<WallpaperButton> buttons) {
		this.context = context;
		this.count = count;
		this.buttons = buttons;
	}
	
	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int position) {
		return buttons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return buttons.get(position);
	}
}
