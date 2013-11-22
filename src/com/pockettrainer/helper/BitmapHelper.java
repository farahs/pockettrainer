package com.pockettrainer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class BitmapHelper {
	private WindowManager wm;
	Display display;
	Point size;
	Context context;
	View view;

	public BitmapHelper(View v, Context c) {
		context = c;
		view = v;
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		size = new Point();
		display.getSize(size);
	}

	public Bitmap resizeBitmap(int resource, int w, int h) {

		int width = w; // Width of the actual device
		int height = h; // height of the actual device
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = calculateInSampleSize(options, width, height);
		Bitmap originalBitmap = BitmapFactory.decodeResource(
				view.getResources(), resource, options);
		
		Bitmap Objbitmap = originalBitmap;

		if (Objbitmap != null) {
			int heightofBitMap = Objbitmap.getHeight();
			int widthofBitMap = Objbitmap.getWidth();
			heightofBitMap = width * heightofBitMap / widthofBitMap;
			widthofBitMap = width;
			
			Objbitmap = Bitmap.createScaledBitmap(originalBitmap,
					widthofBitMap, heightofBitMap, true);
		}
		
		return Objbitmap;
	}

	public Display getDisplay() {
		return display;
	}

	public Point getSize() {
		return size;
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
}
