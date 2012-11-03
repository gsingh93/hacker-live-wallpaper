package com.gulshansingh.hackerlivewallpaper;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class HackerWallpaperService extends WallpaperService {
    
	@Override
	public Engine onCreateEngine() {
		return new HackerWallpaperEngine();
	}
	
	public class HackerWallpaperEngine extends Engine {

		private Handler handler = new Handler();
		private boolean visible = true;
		
		/** The sequences to draw on the screen */
		private List<BitSequence> sequences = new LinkedList<BitSequence>();

		/**
		 * The main runnable that is given to the Handler to draw the animation
		 */
		private Runnable drawRunnable = new Runnable() {
			public void run() {
				draw();
			}
		};
		
		/** Draws all of the bit sequences on the screen */
		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = holder.lockCanvas();
			try {
				if (c != null) {
					c.drawARGB(255, 0, 0, 0);

					for (BitSequence sequence : sequences) {
						sequence.draw(c);
					}
				}
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}

			// Remove the runnable, and only schedule the next run if visible
			handler.removeCallbacks(drawRunnable);
			if (visible) {
				handler.post(drawRunnable);
			} else {
				stop();
			}
		}

		private void stop() {
			handler.removeCallbacks(drawRunnable);
			for (BitSequence sequence : sequences) {
				sequence.pause();
			}
		}

		private void start() {
			handler.post(drawRunnable);
			for (BitSequence sequence : sequences) {
				sequence.unpause();
			}
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			stop();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);

			BitSequence.configure(width, height);

			int numSequences = (int) (width / BitSequence.getWidth());

			// Initialize BitSequences
			sequences.clear();
			for (int i = 0; i < numSequences; i++) {
				sequences.add(new BitSequence(
						(int) (i * BitSequence.getWidth())));
			}
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible) {
				start();
			} else {
				stop();
			}
			this.visible = visible;
		}
	}
}