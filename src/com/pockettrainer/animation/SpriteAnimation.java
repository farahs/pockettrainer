package com.pockettrainer.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class SpriteAnimation {

	private static final String TAG = SpriteAnimation.class.getSimpleName();

	private Bitmap bitmap; // the animation sequence

	private Bitmap an_idle;
	private Bitmap an_move;
	private Bitmap an_end;

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

	private int x; // the X coordinate of the object (top left of the image)
	private int y; // the Y coordinate of the object (top left of the image)

	private boolean finish;

	public SpriteAnimation(Bitmap bitmap, int x, int y, int width, int height,
			int fps, int frameCount, boolean looped) {
		this.bitmap = Bitmap.createScaledBitmap(bitmap, width * frameCount,
				height, true);
		an_idle = bitmap;
		this.x = x;
		this.y = y;
		currentFrame = 0;
		frameNr = frameCount;
		frameIdle = frameNr;
		this.looped = looped;
		// spriteWidth = bitmap.getWidth() / frameCount;
		// spriteHeight = bitmap.getHeight();
		spriteWidth = width;
		spriteHeight = height;
		sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		framePeriod = 1000 / fps;
		frameTicker = 0l;
	}

	public void setIdle(Bitmap b, int frameCount) {
		an_idle = Bitmap.createScaledBitmap(b, spriteWidth * frameCount,
				spriteHeight, true);
		frameIdle = frameCount;
	}

	public void setMove(Bitmap b, int frameCount) {
		an_move = Bitmap.createScaledBitmap(b, spriteWidth * frameCount,
				spriteHeight, true);
		frameMove = frameCount;
	}

	public void setEnd(Bitmap b, int frameCount) {
		an_end = Bitmap.createScaledBitmap(b, spriteWidth * frameCount,
				spriteHeight, true);
		frameEnd = frameCount;
	}

	public void goIdle() {
		currentFrame = 0;
		frameNr = frameIdle;
		bitmap = an_idle;
	}

	public void goMove() {
		currentFrame = 0;
		frameNr = frameMove;
		bitmap = an_move;
	}

	public void goEnd() {
		currentFrame = 0;
		looped = false;
		bitmap = an_end;
		frameNr = frameEnd;
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
					bitmap = an_idle;
					frameNr = frameIdle;
					currentFrame = 0;
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
