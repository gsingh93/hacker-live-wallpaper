package com.gulshansingh.hackerlivewallpaper;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
	private List<String> bits = new LinkedList<String>();

	/** A variable used for all operations needing random numbers */
	private Random r = new Random();

	/** The scheduled operation for changing a bit and shifting downwards */
	private ScheduledFuture<?> future;

	/** The position to draw the sequence at on the screen */
	float x, y;

	/** The paint style for the bits */
	private static Paint paint = new Paint();

	/** The height of the screen */
	private static int HEIGHT;

	/** The number of bits this bit sequence should hold */
	private static final int NUM_BITS = 10;

	/** The speed at which bits should be changed */
	private static final int SPEED = 100;

	/** The increment at which the alpha of the bit sequence should increase */
	private static final int INCREMENT = 255 / NUM_BITS;


	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

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
		y = -1 * paint.getTextSize() * NUM_BITS;
		initPaint();
		final Runnable runnable = new Runnable() {
			public void run() {
				changeBit();
				y += paint.getTextSize();
				if (y > HEIGHT) {
					y = -1 * paint.getTextSize() * NUM_BITS;
					ScheduledFuture<?> futurePrev = future;
					future = scheduler.scheduleAtFixedRate(this,
							r.nextInt(6000), SPEED, TimeUnit.MILLISECONDS);
					futurePrev.cancel(true);
				}
			}
		};
		future = scheduler.scheduleAtFixedRate(runnable, r.nextInt(6000),
				SPEED, TimeUnit.MILLISECONDS);
	}

	/** Shifts the bits back by one and adds a new bit to the end */
	synchronized public void changeBit() {
		bits.remove(0);
		bits.add(getRandomBit(r));
	}

	/** Initializes the {@link Paint} object */
	private void initPaint() {
		paint.setTextSize(36);
		paint.setColor(Color.GREEN);
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
		return String.valueOf(bit);
	}
	
	/**
	 * Gets the width the BitSequence would be on the screen
	 * 
	 * @return the width of the BitSequence
	 */
	public static float getWidth() {
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
		float prevX = x;
		float prevY = y;
		for (String bit : bits) {
			canvas.drawText(bit, x, y, paint);
			y += paint.getTextSize();
			paint.setAlpha(paint.getAlpha() + INCREMENT);
		}
		x = prevX;
		y = prevY;
	}
}
