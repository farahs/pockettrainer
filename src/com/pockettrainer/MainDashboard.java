/*
 * Disini halaman utama buat mainan tamagotchi-nya
 */

package com.pockettrainer;

import java.text.DecimalFormat;

import com.example.pockettrainer.R;
import com.pockettrainer.animation.SpriteAnimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * @author impaler This is the main surface that handles the ontouch events and
 *         draws the image to the screen.
 */
public class MainDashboard extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainDashboard.class.getSimpleName();

	// desired fps
	private final static int MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;

	/* Stuff for stats */
	private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
	// we'll be reading the stats every second
	private final static int STAT_INTERVAL = 1000; // ms
	// the average will be calculated by storing
	// the last n FPSs
	private final static int FPS_HISTORY_NR = 10;
	// the status time counter
	private long statusIntervalTimer = 0l;
	// number of frames skipped since the game started
	private long totalFramesSkipped = 0l;
	// number of frames skipped in a store cycle (1 sec)
	private long framesSkippedPerStatCycle = 0l;

	// number of rendered frames in an interval
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	// the last FPS values
	private double fpsStore[];
	// the number of times the stat has been read
	private long statsCount = 0;
	// the average FPS since the game started
	private double averageFps = 0.0;
	// Surface holder that can access the physical surface

	private Handler customHandler = new Handler();
	private SpriteAnimation sprite;

	private boolean isTouched;
	boolean mSurfaceExists = true;

	private BitmapDrawable tile;

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

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		// create Elaine and load bitmap
		sprite = new SpriteAnimation(BitmapFactory.decodeResource(
				getResources(), R.drawable.sprite_egg), size.x / 2, 200 // initial
																		// position
				, 300, 300 // width and height of sprite
				, 30, 20 // FPS and number of frames in the animation
				, true);
		sprite.setMove(BitmapFactory.decodeResource(getResources(),
				R.drawable.sprite_egg_move), 10);
		sprite.setEnd(BitmapFactory.decodeResource(getResources(),
				R.drawable.sprite_egg_remove), 10);

		// create the game loop thread
		// thread = new MainThread(getHolder(), this);

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
		customHandler.postDelayed(taskRunnable, 0);
		ThreadIsRunning = true;
		// if (bgTask == null || bgTask.getStatus() ==
		// AsyncTask.Status.FINISHED)
		// {
		// bgTask = new BackgroundTask(getHolder(), this);
		// }
		// mSurfaceExists = true;
		// bgTask.execute(AndEngineTexturePackerExtension
		// AndEngineTexturePackerExtension );
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// mSurfaceExists = false;
		customHandler.removeCallbacks(taskRunnable);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int X = (int) event.getX();
		int Y = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			int centerX = sprite.getX() + sprite.getSpriteWidth() / 2;
			int centerY = sprite.getY() + sprite.getSpriteHeight() / 2;

			double radCircle = Math
					.sqrt((((centerX - X) * (centerX - X)) + (centerY - Y)
							* (centerY - Y)));
			if (radCircle < 150 && !isTouched) {
				isTouched = true;
				sprite.goMove();
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (isTouched) {
				isTouched = false;
				sprite.goEnd();
			}
		}
		return true;
	}

	public void render(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
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
		customHandler.removeCallbacks(taskRunnable);
	}

	public void resumeThread() {
//		customHandler.postDelayed(taskRunnable, 0);
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, this.getWidth() - 50, 20, paint);
		}
	}

	private Runnable taskRunnable = new Runnable() {

		@Override
		public void run() {
			Canvas canvas;
			Log.d(TAG, "Starting game loop");
			// initialise timing elements for stat gathering
			initTimingElements();

			long beginTime; // the time when the cycle begun
			long timeDiff; // the time it took for the cycle to execute
			int sleepTime; // ms to sleep (<0 if we're behind)
			int framesSkipped; // number of frames being skipped

			sleepTime = 0;

			canvas = getHolder().lockCanvas(null);
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				beginTime = System.currentTimeMillis();
				framesSkipped = 0; // resetting the frames skipped
				update();
				render(canvas);
				timeDiff = System.currentTimeMillis() - beginTime;
				sleepTime = (int) (FRAME_PERIOD - timeDiff);

				if (sleepTime > 0) {
					// if sleepTime > 0 we're OK
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
				}

				while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
					update(); // update without rendering
					sleepTime += FRAME_PERIOD; // add frame period to check
												// if in next frame
					framesSkipped++;
				}
				customHandler.postDelayed(this, 0);
				framesSkippedPerStatCycle += framesSkipped;
				storeStats();
			} finally {
				if (canvas != null) {
					getHolder().unlockCanvasAndPost(canvas);
				}
			} // end finally
		}

	};

	private void storeStats() {
		frameCountPerStatCycle++;
		totalFrameCount++;
		// assuming that the sleep works each call to storeStats
		// happens at 1000/FPS so we just add it up
		statusIntervalTimer += FRAME_PERIOD;

		if (statusIntervalTimer >= STAT_INTERVAL) {
			// calculate the actual frames pers status check interval
			double actualFps = (double) (frameCountPerStatCycle / (STAT_INTERVAL / 1000));

			// stores the latest fps in the array
			fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;

			// increase the number of times statistics was calculated
			statsCount++;

			double totalFps = 0.0;
			// sum up the stored fps values
			for (int i = 0; i < FPS_HISTORY_NR; i++) {
				totalFps += fpsStore[i];
			}

			// obtain the average
			if (statsCount < FPS_HISTORY_NR) {
				// in case of the first 10 triggers
				averageFps = totalFps / statsCount;
			} else {
				averageFps = totalFps / FPS_HISTORY_NR;
			}
			// saving the number of total frames skipped
			totalFramesSkipped += framesSkippedPerStatCycle;
			// resetting the counters after a status record (1 sec)
			framesSkippedPerStatCycle = 0;
			statusIntervalTimer = 0;
			frameCountPerStatCycle = 0;
			// Log.d(TAG, "Average FPS:" + df.format(averageFps));
			this.setAvgFps("FPS: " + df.format(averageFps));
		}
	}

	private void initTimingElements() {
		// initialise timing elements
		fpsStore = new double[FPS_HISTORY_NR];
		for (int i = 0; i < FPS_HISTORY_NR; i++) {
			fpsStore[i] = 0.0;
		}
		Log.d(TAG + ".initTimingElements()",
				"Timing elements for stats initialised");
	}

}