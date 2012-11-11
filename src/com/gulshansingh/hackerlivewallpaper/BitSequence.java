package com.gulshansingh.hackerlivewallpaper;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.gulshansingh.hackerlivewallpaper.thirdparty.ArrayDeque;

/**
 * A class that stores a list of bits. The first bit is removed and a new bit is
 * appended at a fixed interval. Calling the draw method of displays the bit
 * sequence vertically on the screen. Every time a bit is changed, the position
 * of the sequence on the screen will be shifted downward. Moving past the
 * bottom of the screen will cause the sequence to be placed above the screen
 * 
 * @author Gulshan Singh
 */
public class BitSequence {

	/** The bits this sequence stores */
	private ArrayDeque<String> bits = new ArrayDeque<String>();

	/** A variable used for all operations needing random numbers */
	private Random r = new Random();

	/** The scheduled operation for changing a bit and shifting downwards */
	private ScheduledFuture<?> future;

	/** The position to draw the sequence at on the screen */
	float x, y;

	/** The paint style for the bits */
	private Paint paint = new Paint();

	/** True when the BitSequence should be paused */
	private boolean pause = false;

	/** The height of the screen */
	private static int HEIGHT;

	/** The number of bits this bit sequence should hold */
	private static final int NUM_BITS = 13;

	/** The speed at which bits should be changed */
	private static final int SPEED = 100;

	/** The increment at which the alpha of the bit sequence should increase */
	private static final int INCREMENT = 255 / NUM_BITS;

	/** The font size for the bits */
	private static final int TEXT_SIZE = 36;

	/** The initial starting point for all BitSequences */
	private static final int INITIAL_Y = -1 * TEXT_SIZE * NUM_BITS;

	private final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor();
	
	/** This string is stored so it does not need to be created multiple times */
	private static final String zero = "0";

	/** This string is stored so it does not need to be created multiple times */
	private static final String one = "1";
	
	/** The Mask to use for blurred text */
	private static final BlurMaskFilter blurFilter = new BlurMaskFilter(3,
			Blur.NORMAL);

	/** The Mask to use for slightly blurred text */
	private static final BlurMaskFilter slightBlurFilter = new BlurMaskFilter(
			2, Blur.NORMAL);

	/** The Mask to use for regular text */
	private static final BlurMaskFilter regularFilter = null;// new
																// BlurMaskFilter(1,
																// Blur.NORMAL);

	/**
	 * A runnable that changes the bit, moves the sequence down, and reschedules
	 * its execution
	 */
	private final Runnable changeBitRunnable = new Runnable() {
		public void run() {
			// Log.d("TAG", "Running");
			changeBit();
			y += TEXT_SIZE;
			if (y > HEIGHT) {
				y = INITIAL_Y;
				scheduleThread();
				setMaskFilter();
			}
		}
	};

	/**
	 * Configures the BitSequence based on the display
	 * 
	 * @param width
	 *            the width of the screen
	 * @param height
	 *            the height of the screen
	 */
	public static void configure(int width, int height) {
		HEIGHT = height;
	}

	public BitSequence(int x) {
		for (int i = 0; i < NUM_BITS; i++) {
			bits.add(getRandomBit(r));
		}

		this.x = x;
		this.y = INITIAL_Y;
		initPaint();

		scheduleThread();
	}

	/**
	 * Pauses the BitSequence by cancelling the ScheduledFuture
	 */
	public void pause() {
		if (!pause) {
			if (future != null) {
				future.cancel(true);
			}
			pause = true;
		}
	}

	/**
	 * Unpauses the BitSequence by scheduling BitSequences on the screen to
	 * immediately start, and scheduling BitSequences off the screen to start
	 * after some delay
	 */
	public void unpause() {
		if (pause) {
			if (y <= INITIAL_Y + TEXT_SIZE || y > HEIGHT) {
				scheduleThread();
			} else {
				scheduleThread(0);
			}
			pause = false;
		}
	}

	/**
	 * Schedules the changeBitRunnable with a random delay less than 6000
	 * milliseconds, cancelling the previous scheduled future
	 */
	private void scheduleThread() {
		scheduleThread(r.nextInt(6000));
	}

	/**
	 * Schedules the changeBitRunnable with the specified delay, cancelling the
	 * previous scheduled future
	 * 
	 * @param delay
	 *            the delay in milliseconds
	 */
	private void scheduleThread(int delay) {
		ScheduledFuture<?> futurePrev = future;
		future = scheduler.scheduleAtFixedRate(changeBitRunnable, delay, SPEED,
				TimeUnit.MILLISECONDS);
		if (futurePrev != null) {
			futurePrev.cancel(true);
		}
	}

	/** Shifts the bits back by one and adds a new bit to the end */
	synchronized private void changeBit() {
		bits.removeFirst();
		bits.addLast(getRandomBit(r));
	}

	/** Initializes the {@link Paint} object */
	private void initPaint() {
		paint.setTextSize(TEXT_SIZE);
		paint.setColor(Color.GREEN);
		setMaskFilter();
	}

	private void setMaskFilter() {
		int blur = r.nextInt(4);
		if (blur == 0) {
			paint.setMaskFilter(blurFilter);
		} else if (blur == 1) {
			paint.setMaskFilter(slightBlurFilter);
		} else {
			paint.setMaskFilter(regularFilter);
		}
	}

	/**
	 * Gets a new random bit
	 * 
	 * @param r
	 *            the {@link Random} object to use
	 * @return A new random bit as a {@link String}
	 */
	private String getRandomBit(Random r) {
		int bit = r.nextInt(2);
		if (bit == 0) {
			return zero;
		} else {
			return one;
		}
	}
	
	/**
	 * Gets the width the BitSequence would be on the screen
	 * 
	 * @return the width of the BitSequence
	 */
	public static float getWidth() {
		Paint paint = new Paint();
		paint.setTextSize(TEXT_SIZE);
		return paint.measureText("0");
	}

	/**
	 * Draws this BitSequence on the screen
	 * 
	 * @param canvas
	 *            the {@link Canvas} on which to draw the BitSequence
	 */
	synchronized public void draw(Canvas canvas) {
		paint.setAlpha(0);
		float prevY = y;
		for (int i = 0; i < bits.size(); i++) {
			canvas.drawText(bits.get(i), x, y, paint);
			y += TEXT_SIZE;
			paint.setAlpha(paint.getAlpha() + INCREMENT);
		}
		y = prevY;
	}
}
