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
	private int eat = 3;
	private int sleep = 4;

	private Bitmap bitmap; // the animation sequence
	private Bitmap cachedResult;
	private Bitmap an_end;
	private Bitmap an_eat;
	private Bitmap an_sleep;

	private Rect sourceRect; // the rectangle to be drawn from the animation
								// bitmap
	private int frameNr; // number of frames in animation
	private int frameIdle;
	private int frameMove;
	private int frameEnd;
	private int frameEat;
	private int frameSleep;
	private int repeat=0;
	private boolean isFinish = true;
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
		this.bitmap = bitmap;
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
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClassBytes = am.getMemoryClass() * 1024 * 1024;
		bitCache = new BitmapCache(memoryClassBytes / 6);
	}

	public void setIdle(Bitmap b, int frameCount) {
		frameIdle = frameCount;
		bitCache.put(idle, b);
	}

	public void setMove(Bitmap b, int frameCount) {
		frameMove = frameCount;
		bitCache.put(move, b);
	}

	public void setEnd(Bitmap b, int frameCount) {
		frameEnd = frameCount;
		// bitCache.put(end, b);
		an_end = b;
	}

	public void setEat(Bitmap b, int frameCount) {
		frameEat = frameCount;
		// bitCache.put(eat, b);
		an_eat = b;
	}

	public void setSleep(Bitmap b, int frameCount) {
		frameSleep = frameCount;
		// bitCache.put(sleep, b);
		an_sleep = b;
	}

	public void goIdle() {
		currentFrame = 0;
		looped = true;
		frameNr = frameIdle;
		cachedResult = bitCache.get(idle);
		if (cachedResult != null)
			bitmap = cachedResult;
	}

	public void goMove() {
		if(isFinish) {
			currentFrame = 0;
			looped = true;
			frameNr = frameMove;
			cachedResult = bitCache.get(move);
			if (cachedResult != null)
				bitmap = cachedResult;
		}
	}

	public void goEnd() {
		if(isFinish) {
			currentFrame = 0;
			looped = false;
			frameNr = frameEnd;
			// cachedResult = bitCache.get(end);
			// if (cachedResult != null)
			// bitmap = cachedResult;
			bitmap = an_end;
		}
	}

	public void goEat() {
		currentFrame = 0;
		repeat=4;
		looped = false;
		frameNr = frameEat;
		isFinish = false;
		// cachedResult = bitCache.get(end);
		// if (cachedResult != null)
		// bitmap = cachedResult;
		bitmap = an_eat;
	}

	public void goSleep() {
		currentFrame = 0;
		looped = true;
		frameNr = frameSleep;
		// cachedResult = bitCache.get(end);
		// if (cachedResult != null)
		// bitmap = cachedResult;
		bitmap = an_sleep;
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
			} else if(repeat>0) {
				if (currentFrame >= frameNr) {
					repeat-=1;
					currentFrame = 0;
				}
			} else {
				if (currentFrame >= frameNr) {
					isFinish=true;
					goIdle();
					looped = true;
				}
			}

		}
		// define the rectangle to cut out sprite
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
//		Log.i("RECT", this.sourceRect.left + " x " + this.sourceRect.right);
	}

	public void draw(Canvas canvas) {
		// where to draw the sprite
		Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY()
				+ spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
	}

}
