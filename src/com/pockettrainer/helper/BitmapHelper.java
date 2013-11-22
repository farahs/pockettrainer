package com.pockettrainer.helper;

import android.content.Context;
import android.content.res.Resources;
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

		int width = w;
		int height = h;
		final BitmapFactory.Options options = new BitmapFactory.Options();

		Bitmap originalBitmap = decodeBitmapFromResource(view.getResources(),
				resource, w, h);

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

	public Bitmap decodeBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
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
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}
}
