package com.vie.sharkbytes;

import android.content.Context;
import android.widget.ImageView;

public class WallpaperButton extends ImageView {

	public WallpaperButton(Context context) {
		super(context);
	}
	
	@Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
    {
        super.onSizeChanged(w, w, oldw, oldh);
    }
}