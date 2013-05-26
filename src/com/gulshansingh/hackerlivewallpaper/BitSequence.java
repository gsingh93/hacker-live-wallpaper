package com.gulshansingh.hackerlivewallpaper;

import static com.gulshansingh.hackerlivewallpaper.SettingsFragment.KEY_BIT_COLOR;
import static com.gulshansingh.hackerlivewallpaper.SettingsFragment.KEY_CHANGE_BIT_SPEED;
import static com.gulshansingh.hackerlivewallpaper.SettingsFragment.KEY_ENABLE_DEPTH;
import static com.gulshansingh.hackerlivewallpaper.SettingsFragment.KEY_ENABLE_SMOOTH_FALLING;
import static com.gulshansingh.hackerlivewallpaper.SettingsFragment.KEY_FALLING_SPEED;
import static com.gulshansingh.hackerlivewallpaper.SettingsFragment.KEY_NUM_BITS;
import static com.gulshansingh.hackerlivewallpaper.SettingsFragment.KEY_TEXT_SIZE;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
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

	/** The Mask to use for blurred text */
	private static final BlurMaskFilter blurFilter = new BlurMaskFilter(3,
			Blur.NORMAL);

	/** The Mask to use for slightly blurred text */
	private static final BlurMaskFilter slightBlurFilter = new BlurMaskFilter(
			2, Blur.NORMAL);

	/** The Mask to use for regular text */
	private static final BlurMaskFilter regularFilter = null;

	/** The height of the screen */
	private static int HEIGHT;

	/** The bits this sequence stores */
	private ArrayDeque<String> bits = new ArrayDeque<String>();

	/** A variable used for all operations needing random numbers */
	private Random r = new Random();

	/** The scheduled operation for changing a bit */
	private ScheduledFuture<?> changeBitfuture;

	/** The scheduled operation for shifting downwards */
	private ScheduledFuture<?> fallingFuture;

	/** The position to draw the sequence at on the screen */
	float x, y;

	/** True when the BitSequence should be paused */
	private boolean pause = false;

	private static final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor();

	/** The characters to use in the sequence */
	private static final String[] symbols = { "0", "1" };

	/** Describes the style of the sequence */
	private final Style style = new Style();

	public static class Style {
		/** The default speed at which bits should be changed */
		private static final int DEFAULT_CHANGE_BIT_SPEED = 100;

		/** The maximum alpha a bit can have */
		private static final int MAX_ALPHA = 240;

		private static int changeBitSpeed;
		private static int numBits;
		private static int color;
		private static int defaultTextSize;
		private static int defaultFallingSpeed;
		private static int smoothFallingDivisor;
		private static boolean depthEnabled;

		private static int alphaIncrement;
		private static int initialY;

		private int textSize;
		private int fallingSpeed;
		private BlurMaskFilter maskFilter;

		private Paint paint = new Paint();

		public static void initParameters(Context context) {
			PreferenceUtility preferences = new PreferenceUtility(context);

			numBits = preferences.getInt(KEY_NUM_BITS,
					R.integer.default_num_bits);
			color = preferences
					.getInt(KEY_BIT_COLOR, R.color.default_bit_color);
			defaultTextSize = preferences.getInt(KEY_TEXT_SIZE,
					R.integer.default_text_size);

			double changeBitSpeedMultiplier = 100 / preferences.getDouble(
					KEY_CHANGE_BIT_SPEED, R.integer.default_change_bit_speed);
			double fallingSpeedMultiplier = preferences.getDouble(
					KEY_FALLING_SPEED, R.integer.default_falling_speed) / 100;

			changeBitSpeed = (int) (DEFAULT_CHANGE_BIT_SPEED * changeBitSpeedMultiplier);
			defaultFallingSpeed = (int) (defaultTextSize * fallingSpeedMultiplier);

			boolean smoothFalling = preferences.getBoolean(
					KEY_ENABLE_SMOOTH_FALLING, false);
			smoothFallingDivisor = smoothFalling ? 10 : 1;

			depthEnabled = preferences.getBoolean(KEY_ENABLE_DEPTH, true);

			alphaIncrement = MAX_ALPHA / numBits;
			initialY = -1 * defaultTextSize * numBits;
		}

		public Style() {
			paint.setColor(color);
		}

		public void createPaint() {
			paint.setTextSize(textSize);
			paint.setMaskFilter(maskFilter);
		}

		private static class PreferenceUtility {
			private SharedPreferences preferences;
			private Resources res;

			public PreferenceUtility(Context context) {
				preferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				res = context.getResources();
			}

			public int getInt(String key, int defaultId) {
				return preferences.getInt(key, res.getInteger(defaultId));
			}

			public double getDouble(String key, int defaultId) {
				return (double) preferences.getInt(key,
						res.getInteger(defaultId));
			}

			public boolean getBoolean(String key, boolean defaultVal) {
				return preferences.getBoolean(key, defaultVal);
			}
		}
	}

	/**
	 * Resets the sequence by repositioning it, reseting its visual
	 * characteristics, and rescheduling the thread
	 */
	private void reset() {
		y = Style.initialY;
		setDepth();
		style.createPaint();
		scheduleThread();
	}

	/** A runnable that changes the bit */
	private final Runnable changeBitRunnable = new Runnable() {
		public void run() {
			changeBit();
		}
	};

	/**
	 * A runnable that moves the sequences down and resets it after going passed
	 * the edge of the screen
	 */
	private final Runnable fallingRunnable = new Runnable() {
		public void run() {
			y += style.fallingSpeed / Style.smoothFallingDivisor;
			if (y > HEIGHT) {
				reset();
			}
		}
	};

	private void setDepth() {
		if (!Style.depthEnabled) {
			style.textSize = Style.defaultTextSize;
			style.fallingSpeed = Style.defaultFallingSpeed;
		} else {
			double factor = r.nextDouble() * (1 - .8) + .8;
			style.textSize = (int) (Style.defaultTextSize * factor);
			style.fallingSpeed = (int) (Style.defaultFallingSpeed * Math.pow(
					factor, 4));

			if (factor > .93) {
				style.maskFilter = regularFilter;
			} else if (factor <= .93 && factor >= .87) {
				style.maskFilter = slightBlurFilter;
			} else {
				style.maskFilter = blurFilter;
			}
		}
	}

	/**
	 * Configures any BitSequences parameters requiring the application context
	 * 
	 * @param context
	 *            the application context
	 */
	public static void configure(Context context) {
		Style.initParameters(context);
	}

	/**
	 * Configures the BitSequence based on the display
	 * 
	 * @param width
	 *            the width of the screen
	 * @param height
	 *            the height of the screen
	 */
	public static void setScreenDim(int width, int height) {
		HEIGHT = height;
	}

	public BitSequence(int x) {
		for (int i = 0; i < Style.numBits; i++) {
			bits.add(getRandomBit(r));
		}

		this.x = x;
		reset();
	}

	/**
	 * Pauses the BitSequence by cancelling the ScheduledFuture
	 */
	public void pause() {
		if (!pause) {
			if (changeBitfuture != null) {
				changeBitfuture.cancel(true);
			}
			if (fallingFuture != null) {
				fallingFuture.cancel(true);
			}
			pause = true;
		}
	}

	public void stop() {
		pause();
	}

	/**
	 * Unpauses the BitSequence by scheduling BitSequences on the screen to
	 * immediately start, and scheduling BitSequences off the screen to start
	 * after some delay
	 */
	public void unpause() {
		if (pause) {
			if (y <= Style.initialY + style.textSize || y > HEIGHT) {
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
		if (changeBitfuture != null)
			changeBitfuture.cancel(true);
		if (fallingFuture != null)
			fallingFuture.cancel(true);
		changeBitfuture = scheduler.scheduleAtFixedRate(changeBitRunnable,
				delay, Style.changeBitSpeed, TimeUnit.MILLISECONDS);
		fallingFuture = scheduler.scheduleAtFixedRate(fallingRunnable, delay,
				Style.changeBitSpeed / Style.smoothFallingDivisor,
				TimeUnit.MILLISECONDS);
	}

	/** Shifts the bits back by one and adds a new bit to the end */
	synchronized private void changeBit() {
		bits.removeFirst();
		bits.addLast(getRandomBit(r));
	}

	/**
	 * Gets a new random bit
	 * 
	 * @param r
	 *            the {@link Random} object to use
	 * @return A new random bit as a {@link String}
	 */
	private String getRandomBit(Random r) {
		return symbols[r.nextInt(symbols.length)];
	}

	/**
	 * Gets the width the BitSequence would be on the screen
	 * 
	 * @return the width of the BitSequence
	 */
	public static float getWidth() {
		Paint paint = new Paint();
		paint.setTextSize(Style.defaultTextSize);
		return paint.measureText("0");
	}

	/**
	 * Draws this BitSequence on the screen
	 * 
	 * @param canvas
	 *            the {@link Canvas} on which to draw the BitSequence
	 */
	synchronized public void draw(Canvas canvas) {
		Paint paint = style.paint;
		float bitY = y;
		paint.setAlpha(Style.alphaIncrement);
		for (int i = 0; i < bits.size(); i++) {
			canvas.drawText(bits.get(i), x, bitY, paint);
			bitY += style.textSize;
			paint.setAlpha(paint.getAlpha() + Style.alphaIncrement);
		}
	}
}
