package com.pockettrainer;

import java.text.DecimalFormat;
import java.util.List;

import com.pockettrainer.animation.MainThread;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BackgroundTask extends AsyncTask<Object, Void, Void> {

	private Context context;
	private static final String TAG = MainThread.class.getSimpleName();

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
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private MainDashboard gamePanel;

	ThreadControl tControl = new ThreadControl();

	// flag to hold game state
	private boolean running;

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	public BackgroundTask(SurfaceHolder surfaceHolder, MainDashboard gamePanel) {
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	@Override
	protected void onPostExecute(Void result) {

	}

	@Override
	protected Void doInBackground(Object... params) {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");
		// initialise timing elements for stat gathering
		initTimingElements();

		long beginTime; // the time when the cycle begun
		long timeDiff; // the time it took for the cycle to execute
		int sleepTime; // ms to sleep (<0 if we're behind)
		int framesSkipped; // number of frames being skipped

		sleepTime = 0;
		

		while (true) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				tControl.waitIfPaused();
				if (tControl.isCancelled()) {
					break;
				}
				canvas = this.surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0; // resetting the frames skipped
					// update game state
					this.gamePanel.update();
					// render state to the screen
					// draws the canvas on the panel
					this.gamePanel.render(canvas);
					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int) (FRAME_PERIOD - timeDiff);

					if (sleepTime > 0) {
						// if sleepTime > 0 we're OK
						try {
							// send the thread to sleep for a short period
							// very useful for battery saving
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
					}

					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						// we need to catch up
						this.gamePanel.update(); // update without rendering
						sleepTime += FRAME_PERIOD; // add frame period to check
													// if in next frame
						framesSkipped++;
					}
					
					// for statistics
					framesSkippedPerStatCycle += framesSkipped;
					// calling the routine to store the gathered statistics
					storeStats();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} // end finally
		}
		return null;
	}

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
			gamePanel.setAvgFps("FPS: " + df.format(averageFps));
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
	
	public void pauseTask() {
		tControl.pause();
	}
	
	public void resumeTask() {
		tControl.resume();
	}
	
	public void cancelTask() {
		tControl.cancel();
	}
}