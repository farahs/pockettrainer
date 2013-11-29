/*
 * Disini halaman utama buat mainan tamagotchi-nya
 */

package com.pockettrainer;

import java.text.DecimalFormat;

import com.example.pockettrainer.R;
import com.pockettrainer.animation.DragAnimation;
import com.pockettrainer.animation.SpriteAnimation;
import com.pockettrainer.helper.BitmapCache;
import com.pockettrainer.helper.BitmapHelper;
import com.pockettrainer.helper.UserSession;

import android.app.ActivityManager;
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
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.RelativeLayout;

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

	private int spriteOptimalSize;

	private Bitmap currEnv;
	private Bitmap brush;
	private boolean bathing = false;
	private boolean isTouchBath = false;
	private int bLastTouchX;
	private int bLastTouchY;
	private int brushX, brushY;
	private int selEnv = 0;
	private int incHygiene = 0;
	private int incRel = 0;

	private boolean isTouched;
	private boolean isSleep = false;
	private boolean isEat = false;
	private boolean isTouchEat = false;
	boolean mSurfaceExists = true;

	private BitmapHelper bHelper;
	private DragAnimation brushAnim;
	private DragAnimation foodAnim;

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
		getHolder().setFormat(0x00000004);

		bHelper = new BitmapHelper(this, context);

		brushAnim = new DragAnimation(bHelper.resizeBitmap(R.drawable.sikat,
				100, 100), context);
		foodAnim = new DragAnimation(bHelper.resizeBitmap(R.drawable.burger,
				100, 100), context);
		setSprite(context, R.drawable.sprite_egg, R.drawable.sprite_egg_move,
				R.drawable.sprite_egg_remove, 20, 10, 10, 10, 10);

		setFocusable(true);
	}

	public void setSprite(Context context, int idle, int move, int end,
			int frameIdle, int frameMove, int frameEnd, int frameEat,
			int frameSleep) {
		spriteOptimalSize = bHelper.getSize().x / 2;

		sprite = new SpriteAnimation(bHelper.resizeBitmap(idle,
				spriteOptimalSize * frameIdle, spriteOptimalSize),
				bHelper.getSize().x / 2, 200 // initial position
				, spriteOptimalSize, spriteOptimalSize // width and height of
														// sprite
				, 30, frameIdle // FPS and number of frames in the animation
				, true, context);
		sprite.setMove(bHelper.resizeBitmap(R.drawable.sprite_egg_move,
				spriteOptimalSize * frameMove, spriteOptimalSize), frameMove);
		sprite.setEnd(bHelper.resizeBitmap(R.drawable.sprite_egg_remove,
				spriteOptimalSize * frameEnd, spriteOptimalSize), frameEnd);
		sprite.setEat(bHelper.resizeBitmap(R.drawable.sprite_egg_eat,
				spriteOptimalSize * frameEnd, spriteOptimalSize), frameEnd);
		sprite.setSleep(bHelper.resizeBitmap(R.drawable.sprite_egg_sleep,
				spriteOptimalSize * frameEnd, spriteOptimalSize), frameEnd);
	}

	public void setEnvironment(int env) {
		if (env == 1)
			currEnv = bHelper.resizeBitmap(R.drawable.env_fire,
					bHelper.getSize().x, bHelper.getSize().y);
		if (env == 2)
			currEnv = bHelper.resizeBitmap(R.drawable.env_grass,
					bHelper.getSize().x, bHelper.getSize().y);
		if (env == 3)
			currEnv = bHelper.resizeBitmap(R.drawable.env_water,
					bHelper.getSize().x, bHelper.getSize().y);
	}

	public void goEat() {
		if (isEat) {
			isEat = false;
			int hunger = MainActivity.getInstance().getHunger();
			int energy = MainActivity.getInstance().getEnergy();
			int hygiene = MainActivity.getInstance().getHygiene();
			int love = MainActivity.getInstance().getLove();

			MainActivity.getInstance().setHunger(10);
			
			MainActivity.setActHunger();
		} else {
			isEat = true;
			if(bathing) {
				bathing = false;
				MainActivity.setActHygiene();
			}
		}
	}

	private void setBrushX(int a) {
		brushX = a;
	}

	private void setBrushY(int a) {
		brushY = a;
	}

	private int getBrushX() {
		return brushX;
	}

	private int getBrushY() {
		return brushY;
	}

	public void goSleep() {
		if (!isSleep) {
			sprite.goSleep();
			isSleep = true;
			EnergyRecharger er = new EnergyRecharger();
			er.start();
			UserSession.setPetSleepSession(getContext(), isSleep);
			
			if(bathing) {
				bathing = false;
				MainActivity.setActHygiene();
			}
			if(isEat) {
				isEat = false;
				MainActivity.setActHunger();
			}
			
		} else {
			isSleep = false;
			sprite.goIdle();
			UserSession.setPetSleepSession(getContext(), isSleep);
		}
	}

	private class EnergyRecharger extends Thread {

		@Override
		public void run() {
			super.run();

			try {

				int hunger = MainActivity.getInstance().getHunger();
				int energy = MainActivity.getInstance().getEnergy();
				int hygiene = MainActivity.getInstance().getHygiene();
				int love = MainActivity.getInstance().getLove();

				Thread.sleep(1000);
				MainActivity.getInstance().setEnergy(10);

				Log.i("POCKETTRAINER", " " + hunger + " " + energy + " "
						+ hygiene + " " + love);
			} catch (Exception ex) {

			}
		}
	}

	public boolean getSleep() {
		return isSleep;
	}

	public void setIsBath() {
		if (bathing)
			bathing = false;
		else
			bathing = true;

		if(isEat) {
			isEat = false;
			MainActivity.setActHunger();
		}

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
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// mSurfaceExists = false;
		customHandler.removeCallbacks(taskRunnable);
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);

		sprite.setX(xNew / 2 - spriteOptimalSize / 2);
		sprite.setY(yNew / 2 - spriteOptimalSize / 2 + 25);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			int centerX = sprite.getX() + sprite.getSpriteWidth() / 2;
			int centerY = sprite.getY() + sprite.getSpriteHeight() / 2;
			int X = (int) event.getX();
			int Y = (int) event.getY();
			Log.d("brush", X + " & " + Y);
			setBrushX(X);
			setBrushY(Y);

			double radCircle = Math
					.sqrt((((centerX - X) * (centerX - X)) + (centerY - Y)
							* (centerY - Y)));
			isTouchBath = true;
			isTouchEat = true;
			if (radCircle < 150 && !isEat) {
				if (!isTouched) {
					if (isSleep) {
						goSleep();
						MainActivity.setActEnergy();
					}
					isTouched = true;
					sprite.goMove();
				}
			} else {
				if (isTouched)
					sprite.goEnd();
				isTouched = false;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			int centerX = sprite.getX() + sprite.getSpriteWidth() / 2;
			int centerY = sprite.getY() + sprite.getSpriteHeight() / 2;
			int X = (int) event.getX();
			int Y = (int) event.getY();
			double radCircle = Math
					.sqrt((((centerX - X) * (centerX - X)) + (centerY - Y)
							* (centerY - Y)));

			if (radCircle < 100 && isEat) {
				sprite.goEat();
				goEat();
			}

			if (isTouched) {
				isTouched = false;
				int hunger = MainActivity.getInstance().getHunger();
				int energy = MainActivity.getInstance().getEnergy();
				int hygiene = MainActivity.getInstance().getHygiene();
				int love = MainActivity.getInstance().getLove();

				MainActivity.getInstance().setLove(incRel);
				incRel = 0;
				sprite.goEnd();
			}
			if (isTouchBath) {
				isTouchBath = false;
				int hunger = MainActivity.getInstance().getHunger();
				int energy = MainActivity.getInstance().getEnergy();
				int hygiene = MainActivity.getInstance().getHygiene();
				int love = MainActivity.getInstance().getLove();

				MainActivity.getInstance().setHygiene(incHygiene);
				incHygiene = 0;
			}
			if (isTouchEat) {
				isTouchEat = false;
			}
		}
		return true;
	}

	public void render(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Paint alpha = new Paint();
		alpha.setColor(Color.BLACK);
		alpha.setAlpha(95);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(currEnv, canvas.getWidth() - currEnv.getWidth(),
				canvas.getHeight() - currEnv.getHeight(), paint);
		sprite.draw(canvas);
		if (isSleep)
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), alpha);
		if (bathing && isTouchBath) {
			brushAnim.update(brushX, brushY);
			brushAnim.Draw(canvas);
		}
		if (isEat && isTouchEat) {
			foodAnim.update(brushX, brushY);
			foodAnim.Draw(canvas);
		}
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
		// customHandler.postDelayed(taskRunnable, 0);
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
				if (isTouchBath && bathing) {
					// nambah hygiene
					incHygiene += 1;
				} else if (isTouchBath && !bathing) {
					// nambah relationship
					incRel += 1;
				}
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