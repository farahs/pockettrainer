package com.pockettrainer.helper;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCache extends LruCache<Integer, Bitmap> {

	public BitmapCache(int maxSize) {
		super(maxSize);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int sizeOf(Integer key, Bitmap value) {
		return value.getByteCount();

	}

}
