/*
 * Disini halaman utama buat mainan tamagotchi-nya
 */

package com.pockettrainer;

import com.example.pockettrainer.R;
import com.pockettrainer.animation.MainThread;
import com.pockettrainer.animation.SpriteAnimation;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author impaler This is the main surface that handles the ontouch events and
 *         draws the image to the screen.
 */
public class MainDashboard extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainDashboard.class.getSimpleName();

	private BackgroundTask bgTask;
	private MainThread thread;
	private SpriteAnimation sprite;
	
	private boolean isTouched;

	// the fps to be displayed
	private String avgFps;

	private boolean ThreadIsRunning = false;

	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}

	public MainDashboard(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create Elaine and load bitmap
		sprite = new SpriteAnimation(BitmapFactory.decodeResource(
				getResources(), R.drawable.sprite_egg)
				, 10, 50   // initial position
				, 300, 300 // width and height of sprite
				, 30, 20   // FPS and number of frames in the animation
				, true); 
		sprite.setMove(BitmapFactory.decodeResource(getResources(), R.drawable.sprite_egg_move), 10);
		sprite.setEnd(BitmapFactory.decodeResource(getResources(), R.drawable.sprite_egg_remove), 10);

		// create the game loop thread
		// thread = new MainThread(getHolder(), this);

		bgTask = new BackgroundTask(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		Log.i("start", "resuming " + ThreadIsRunning);
		if (!ThreadIsRunning) {
			bgTask.execute();
			ThreadIsRunning = true;
		} else {
			bgTask.resumeTask();
		}

		// start();
	}

	public void start() {
		if (!ThreadIsRunning) {
			thread.setRunning(true);
			thread.start();
			ThreadIsRunning = true;
		} else {
			thread.setRunning(true);
			thread.onResume();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		bgTask.cancelTask();
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		// boolean retry = true;
		// while (retry) {
		// try {
		// thread.join();
		// retry = false;
		// } catch (InterruptedException e) {
		//
		// }
		// }
		// Log.d(TAG, "Thread was shut down cleanly");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if(!isTouched) {
				isTouched = true;
				sprite.goMove();
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			isTouched = false;
			sprite.goEnd();
		}
		return true;
	}

	public void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		sprite.draw(canvas);
		// display fps
		// displayFps(canvas, avgFps);
	}

	/**
	 * This is the game update method. It iterates through all the objects and
	 * calls their update method if they have one or calls specific engine's
	 * update method.
	 */
	public void update() {
		sprite.update(System.currentTimeMillis());
	}

	public void pauseThread() {
		// bgTask.setRunning(false);
		bgTask.pauseTask();
		// thread.setRunning(false);
		// thread.onPause();
	}

	public void resumeThread() {
		// bgTask.setRunning(true);
		bgTask.resumeTask();
		// thread.setRunning(true);
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

}