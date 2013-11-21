package com.pockettrainer.animation;

import com.pockettrainer.helper.BitmapCache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class SpriteAnimation {

	private static final String TAG = SpriteAnimation.class.getSimpleName();

	private int idle = 1;
	private int move = 2;
	private int end = 3;
	
	private Bitmap bitmap; // the animation sequence
	private Bitmap cachedResult;

	private Rect sourceRect; // the rectangle to be drawn from the animation
								// bitmap
	private int frameNr; // number of frames in animation
	private int frameIdle;
	private int frameMove;
	private int frameEnd;
	private int currentFrame; // the current frame
	private long frameTicker; // the time of the last frame update
	private int framePeriod; // milliseconds between each frame (1000/fps)
	private boolean looped;

	private int spriteWidth; // the width of the sprite to calculate the cut out
								// rectangle
	private int spriteHeight; // the height of the sprite

	private final int MAX_HEIGHT = 400;
	private final int MAX_WIDTH = 400;
	
	private BitmapCache bitCache;

	private int x; // the X coordinate of the object (top left of the image)
	private int y; // the Y coordinate of the object (top left of the image)

	private boolean finish;

	public SpriteAnimation(Bitmap bitmap, int x, int y, int width, int height,
			int fps, int frameCount, boolean looped, Context context) {

		spriteWidth = width;
		spriteHeight = height;
		setCache(context);
		this.bitmap = Bitmap.createScaledBitmap(bitmap, spriteWidth
				* frameCount, spriteHeight, true);
		setIdle(this.bitmap, frameCount);
		this.x = x - width / 2;
		this.y = y - height / 2;
		currentFrame = 0;
		frameNr = frameCount;
		frameIdle = frameNr;
		this.looped = looped;

		sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		framePeriod = 1000 / fps;
		frameTicker = 0l;
	}
	
	public void setCache(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClassBytes = am.getMemoryClass() * 1024 * 1024;
		bitCache = new BitmapCache(memoryClassBytes / 4);
	}

	public void setIdle(Bitmap b, int frameCount) {
		frameIdle = frameCount;
		bitCache.put(idle, Bitmap.createScaledBitmap(b, spriteWidth * frameCount,
				spriteHeight, true));
	}

	public void setMove(Bitmap b, int frameCount) {
		frameMove = frameCount;
		bitCache.put(move, Bitmap.createScaledBitmap(b, spriteWidth * frameCount,
				spriteHeight, true));
	}

	public void setEnd(Bitmap b, int frameCount) {
		frameEnd = frameCount;
		bitCache.put(end, Bitmap.createScaledBitmap(b, spriteWidth * frameCount,
				spriteHeight, true));
	}

	public void goIdle() {
		currentFrame = 0;
		looped = true;
		frameNr = frameIdle;
		cachedResult = bitCache.get(idle);
		if(cachedResult!=null)
			bitmap = cachedResult;
	}

	public void goMove() {
		currentFrame = 0;
		looped = true;
		frameNr = frameMove;
		cachedResult = bitCache.get(move);
		if(cachedResult!=null)
			bitmap = cachedResult;
	}

	public void goEnd() {
		currentFrame = 0;
		looped = false;
		frameNr = frameEnd;
		cachedResult = bitCache.get(end);
		if(cachedResult!=null)
			bitmap = cachedResult;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Rect getSourceRect() {
		return sourceRect;
	}

	public void setSourceRect(Rect sourceRect) {
		this.sourceRect = sourceRect;
	}

	public int getFrameNr() {
		return frameNr;
	}

	public void setFrameNr(int frameNr) {
		this.frameNr = frameNr;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getFramePeriod() {
		return framePeriod;
	}

	public void setFramePeriod(int framePeriod) {
		this.framePeriod = framePeriod;
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidth = spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setLoop(boolean l) {
		looped = l;
	}

	public void update(long gameTime) {
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			// increment the frame
			currentFrame++;
			if (looped) {
				if (currentFrame >= frameNr) {
					currentFrame = 0;
				}
			} else {
				if (currentFrame >= frameNr) {
					goIdle();
					looped = true;
				}
			} 

		}
		// define the rectangle to cut out sprite
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
		Log.i("RECT", this.sourceRect.left + " x " + this.sourceRect.right);
	}

	public void draw(Canvas canvas) {
		// where to draw the sprite
		Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY()
				+ spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
	}

}
