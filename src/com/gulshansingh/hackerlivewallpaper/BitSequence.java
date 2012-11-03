package com.gulshansingh.hackerlivewallpaper;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class BitSequence {

	private ArrayList<String> bits = new ArrayList<String>();

	private Random r = new Random();

	private ScheduledFuture<?> future;

	float x, y;

	private static Paint paint = new Paint();
	private static int HEIGHT;
	private static final int NUM_BITS = 10;
	private static final int SPEED = 100;
	private static final int INCREMENT = 255 / NUM_BITS;

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

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

	synchronized public void changeBit() {
		bits.remove(0);
		bits.add(getRandomBit(r));
	}

	private void initPaint() {
		paint.setTextSize(36);
		paint.setColor(Color.GREEN);
	}

	private String getRandomBit(Random r) {
		int bit = r.nextInt(2);
		return String.valueOf(bit);
	}
	
	public static float getWidth() {
		return paint.measureText("0");
	}

	synchronized public void draw(Canvas canvas) {
		paint.setAlpha(0);
		float prevX = x;
		float prevY = y;
		for (String bit : bits) {
			Log.d("TAG", String.valueOf(y));
			canvas.drawText(bit, x, y, paint);
			y += paint.getTextSize();
			paint.setAlpha(paint.getAlpha() + INCREMENT);
		}
		x = prevX;
		y = prevY;
	}
}
