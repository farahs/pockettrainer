package com.pockettrainer.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class DragAnimation {
	Bitmap bitmap;
	Context context;
	int brushX;
	int brushY;

	public DragAnimation(Bitmap b, Context c) {
		bitmap = b;
		context = c;
	}

	public void update(int x, int y) {
		brushX=x;
		brushY=y;
	}

	public void Draw(Canvas c) {
		c.drawBitmap(bitmap, brushX-bitmap.getWidth(), brushY-bitmap.getHeight(), null);
	}
}
